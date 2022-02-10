package com.whatswater.curd.project.sys.serial;


import cn.hutool.core.collection.CollectionUtil;
import com.whatswater.curd.project.common.CrudUtils;
import com.whatswater.curd.project.common.Page;
import com.whatswater.curd.project.common.PageResult;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.assist.SQLExecute;
import io.vertx.ext.sql.assist.SqlAssist;
import io.vertx.mysqlclient.MySQLClient;
import io.vertx.mysqlclient.MySQLPool;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

// 可能的改进
// bufferMap的写和buffer的更新操作单独放在一个线程中
public class SerialService {
    private Map<String, TopicBuffer> bufferMap = new TreeMap<>();
    private final SerialSQL serialSQL;

    public SerialService(MySQLPool pool) {
        this.serialSQL = new SerialSQL(SQLExecute.createMySQL(pool));
    }

    public Future<PageResult<Serial>> search(Page page, SerialQuery query) {
        SqlAssist sqlAssist = query.toSqlAssist();
        sqlAssist.setStartRow(page.getOffset());
        sqlAssist.setRowSize(page.getLimit());

        return serialSQL.getCount(sqlAssist).compose(total -> {
            if (CrudUtils.notZero(total)) {
                return serialSQL.selectAll(sqlAssist).map(list -> PageResult.of(list.stream().map(Serial::new).collect(Collectors.toList()), page, total));
            } else {
                return Future.succeededFuture(PageResult.empty());
            }
        });
    }

    public Future<Serial> getById(Long serialId) {
        Future<JsonObject> result = serialSQL.selectById(serialId);
        return result.map(json -> {
            if (json == null) {
                return null;
            }
            return new Serial(json);
        });
    }

    public Future<Serial> getByTopic(String topic) {
        SqlAssist sqlAssist = Serial.topicSqlAssist(topic);
        return serialSQL.selectAll(sqlAssist).map(SerialService::mapOne);
    }

    private static Serial mapOne(List<JsonObject> jsonList) {
        if (CollectionUtil.isEmpty(jsonList)) {
            return null;
        }
        return new Serial(jsonList.get(0));
    }

    public Future<Integer> allocNewSerial(String topic, Integer oldValue, Integer newValue) {
        Serial serial = new Serial();
        serial.setCurrentValue(newValue);
        return serialSQL.updateNonEmptyByAssist(serial, Serial.topicAndCurrentValueSqlAssist(topic, oldValue));
    }

    public Future<Long> insert(Serial serial) {
        return serialSQL.insertNonEmptyGeneratedKeys(serial, MySQLClient.LAST_INSERTED_ID);
    }

    public Future<Integer> update(Serial serial) {
        return serialSQL.updateNonEmptyById(serial);
    }

    public Future<Integer> nextSerial(String topic) {
        TopicBuffer buffer = bufferMap.get(topic);
        if (buffer != null && (!buffer.isExhaust())) {
            int nextValue = buffer.nextValue();
            if (nextValue > 0) {
                return Future.succeededFuture(nextValue);
            } else {
                bufferMap.remove(topic);
            }
        }

        return createOrAllocNewTopicBuffer(topic).compose(topicBuffer -> {
            int nextValue = topicBuffer.nextValue();
            if (nextValue > 0) {
                bufferMap.put(topic, topicBuffer);
                return Future.succeededFuture(nextValue);
            }
            return nextSerial(topic);
        });
    }

    public Future<TopicBuffer> createOrAllocNewTopicBuffer(String topic) {
        return getByTopic(topic).compose(serial -> {
            if (serial != null) {
                return Future.succeededFuture(serial);
            }

            Serial newSerial = new Serial();
            newSerial.setTopic(topic);
            newSerial.setExpireDate(LocalDate.now().plusDays(7));
            newSerial.setFilterValue(30);
            newSerial.setCurrentValue(0);
            newSerial.setStep(100);
            return insert(newSerial).map(id -> {
                newSerial.setId(id);
                return newSerial;
            });
        }).compose(serial -> {
            Integer step = serial.getStep();
            Integer newValue = serial.currentValue + step;

            return allocNewSerial(topic, serial.currentValue, newValue).compose(cnt -> {
                if (cnt == 0) {
                    return createOrAllocNewTopicBuffer(topic);
                }
                TopicBuffer topicBuffer = new TopicBuffer();
                topicBuffer.serial = serial;
                topicBuffer.start = serial.currentValue;
                topicBuffer.currentValue = new AtomicInteger(topicBuffer.start);
                topicBuffer.max = newValue;
                return Future.succeededFuture(topicBuffer);
            });
        });
    }

    // start开，max闭
    private static class TopicBuffer {
        private Serial serial;
        private int start;
        private AtomicInteger currentValue;
        private int max;
        private TopicBuffer next;

        public boolean isExhaust() {
            return currentValue.get() > max;
        }

        public int nextValue() {
            int value = currentValue.get();
            while (value < max && currentValue.compareAndSet(value, ++value)) {
                return value + 1;
            }
            return -1;
        }
    }
}

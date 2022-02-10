package com.whatswater.curd.project.sys.serial;


import cn.hutool.core.util.StrUtil;
import com.whatswater.curd.project.common.CrudUtils;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.assist.SqlAssist;
import io.vertx.ext.sql.assist.Table;
import io.vertx.ext.sql.assist.TableColumn;
import io.vertx.ext.sql.assist.TableId;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Table("sys_serial")
public class Serial {
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TOPIC = "topic";
    public static final String COLUMN_CURRENT_VALUE = "current_value";
    public static final String COLUMN_STEP = "step";
    public static final String COLUMN_FILTER_VALUE = "filter_value";
    public static final String COLUMN_EXPIRE_DATE = "expire_date";

    @TableId(COLUMN_ID)
    Long id;
    @TableColumn(COLUMN_TOPIC)
    String topic;
    @TableColumn(COLUMN_CURRENT_VALUE)
    Integer currentValue;
    @TableColumn(COLUMN_STEP)
    Integer step;
    @TableColumn(COLUMN_FILTER_VALUE)
    Integer filterValue;
    @TableColumn(COLUMN_EXPIRE_DATE)
    LocalDate expireDate;


    public Serial() {

    }

    public Serial(JsonObject json) {
        this.id = json.getLong(COLUMN_ID);
        this.topic = json.getString(COLUMN_TOPIC);
        this.currentValue = json.getInteger(COLUMN_CURRENT_VALUE);
        this.step = json.getInteger(COLUMN_STEP);
        this.filterValue = json.getInteger(COLUMN_FILTER_VALUE);
        String expireDate = json.getString(COLUMN_EXPIRE_DATE);
        this.expireDate = StrUtil.isEmpty(expireDate) ? null : CrudUtils.parseDate(expireDate);
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTopic() {
        return this.topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Integer getCurrentValue() {
        return this.currentValue;
    }

    public void setCurrentValue(Integer currentValue) {
        this.currentValue = currentValue;
    }

    public Integer getStep() {
        return this.step;
    }

    public void setStep(Integer step) {
        this.step = step;
    }

    public Integer getFilterValue() {
        return this.filterValue;
    }

    public void setFilterValue(Integer filterValue) {
        this.filterValue = filterValue;
    }

    public LocalDate getExpireDate() {
        return this.expireDate;
    }

    public void setExpireDate(LocalDate expireDate) {
        this.expireDate = expireDate;
    }

    public static SqlAssist topicSqlAssist(String topic) {
        SqlAssist sqlAssist = new SqlAssist();
        sqlAssist.andEq(COLUMN_TOPIC, topic);
        sqlAssist.setRowSize(1);

        return sqlAssist;
    }

    public static SqlAssist topicAndCurrentValueSqlAssist(String topic, Integer currentValue) {
        SqlAssist sqlAssist = new SqlAssist();
        sqlAssist.andEq(COLUMN_TOPIC, topic);
        sqlAssist.andEq(COLUMN_CURRENT_VALUE, currentValue);
        sqlAssist.setRowSize(1);

        return sqlAssist;
    }
}

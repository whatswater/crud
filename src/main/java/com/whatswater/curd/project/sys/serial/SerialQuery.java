package com.whatswater.curd.project.sys.serial;


import cn.hutool.core.util.StrUtil;
import io.vertx.ext.sql.assist.SqlAssist;

public class SerialQuery {
    
    String topic;
    
    
    public String getTopic() {
        return this.topic;
    }
    
    public void setTopic(String topic) {
        this.topic = topic;
    }
    
    public SqlAssist toSqlAssist() {
        SqlAssist sqlAssist = new SqlAssist();
        if (StrUtil.isNotEmpty(topic)) {
            sqlAssist.andEq(Serial.COLUMN_TOPIC, topic);
        }
        return sqlAssist;
    }
}

package com.whatswater.sql.dialect;


import com.whatswater.sql.table.annotation.IdType;
import com.whatswater.sql.table.annotation.TableId;
import com.whatswater.sql.table.annotation.TableName;
import io.vertx.ext.sql.assist.TableColumn;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("payments")
public class Payments {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableColumn("user_id")
    private Long userId;

    @TableColumn("money")
    private BigDecimal money;

    @TableColumn("create_time")
    private LocalDateTime createTime;
}

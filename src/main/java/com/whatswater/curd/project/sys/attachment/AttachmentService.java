package com.whatswater.curd.project.sys.attachment;


import cn.hutool.core.collection.CollectionUtil;
import com.whatswater.curd.project.common.CrudUtils;
import com.whatswater.curd.project.common.CrudUtils.SameFutureBuilder;
import com.whatswater.curd.project.common.Page;
import com.whatswater.curd.project.common.PageResult;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.assist.SQLExecute;
import io.vertx.ext.sql.assist.SqlAssist;
import io.vertx.mysqlclient.MySQLClient;
import io.vertx.mysqlclient.MySQLPool;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AttachmentService {
    private AttachmentSQL attachmentSQL;
    private String uploadFileFolder;

    public AttachmentService() {
    }

    public Future<PageResult<Attachment>> search(Page page, AttachmentQuery query) {
        SqlAssist sqlAssist = query.toSqlAssist();
        sqlAssist.setStartRow(page.getOffset());
        sqlAssist.setRowSize(page.getLimit());

        return attachmentSQL.getCount(sqlAssist).compose(total -> {
            if (CrudUtils.notZero(total)) {
                return attachmentSQL.selectAll(sqlAssist).map(list -> PageResult.of(list.stream().map(Attachment::new).collect(Collectors.toList()), page, total));
            } else {
                return Future.succeededFuture(PageResult.empty());
            }
        });
    }

    public Future<Attachment> getById(Long attachmentId) {
        Future<JsonObject> result = attachmentSQL.selectById(attachmentId);
        return result.map(json -> {
            if (json == null) {
                return null;
            }
            return new Attachment(json);
        });
    }

    public Future<List<Attachment>> listByIds(List<Long> attachmentIds) {
        SqlAssist sqlAssist = Attachment.idListSqlAssist(attachmentIds);

        return attachmentSQL.selectAll(sqlAssist).map(jsonList -> {
            if (CollectionUtil.isEmpty(jsonList)) {
                return Collections.emptyList();
            }
            return jsonList.stream().map(Attachment::new).collect(Collectors.toList());
        });
    }

    public Future<Integer> updateBusinessTypeAndId(List<Long> attachmentIds, String businessType, String businessId) {
        SqlAssist sqlAssist = Attachment.idListSqlAssist(attachmentIds);
        Attachment attachment = new Attachment();
        attachment.setBusinessType(businessType);
        attachment.setBusinessId(businessId);

        return attachmentSQL.updateNonEmptyByAssist(attachment, sqlAssist);
    }

    public Future<List<Long>> batchInsert(List<Attachment> attachments) {
        LocalDateTime now = LocalDateTime.now();
        List<SameFutureBuilder<Long>> taskBuilderList = attachments.stream().map(attachment -> {
            return (SameFutureBuilder<Long>)r -> {
                attachment.setCreateTime(now);
                return insert(attachment);
            };
        }).collect(Collectors.toList());
        return CrudUtils.serialTask(taskBuilderList);
    }

    public Future<Integer> deleteBy(String businessType, String businessId) {
        SqlAssist sqlAssist = Attachment.businessTypeAndIdSqlAssist(businessType, businessId);
        return attachmentSQL.deleteByAssist(sqlAssist);
    }

    public Future<Long> insert(Attachment attachment) {
        return attachmentSQL.insertNonEmptyGeneratedKeys(attachment, MySQLClient.LAST_INSERTED_ID);
    }

    public Future<Integer> update(Attachment attachment) {
        return attachmentSQL.updateNonEmptyById(attachment);
    }

    public void setAttachmentSQL(AttachmentSQL attachmentSQL) {
        this.attachmentSQL = attachmentSQL;
    }

    public String getUploadFileFolder() {
        return uploadFileFolder;
    }

    public void setUploadFileFolder(String uploadFileFolder) {
        this.uploadFileFolder = uploadFileFolder;
    }
}

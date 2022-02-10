package com.whatswater.curd.project.sys.attachment;

import com.whatswater.curd.CrudConst;
import com.whatswater.curd.project.common.*;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.RoutingContext;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.time.LocalDate;
import java.util.*;

@Path("/sys/attachment")
public class AttachmentRest {
    private final AttachmentService attachmentService;

    public AttachmentRest(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @POST
    @Path("/search")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<PageResult<Attachment>>> search(@BeanParam Page page, AttachmentQuery query) {
        if (query == null) {
            query = new AttachmentQuery();
        }
        return attachmentService.search(page, query).map(RestResult::success);
    }

    @POST
    @Path("/get")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    public Future<Attachment> get(@QueryParam("id") Long attachmentId) {
        return attachmentService.getById(attachmentId);
    }

    // rest.vertx存在一个问题，标注了@Context注解的RoutingContext参数，默认方法不会启用BodyHandler
    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    public Future<RestResult<List<Long>>> upload(
        @HeaderParam(CrudConst.HEADER_TOKEN) String token,
        @Context RoutingContext rc,
        @FormParam("businessType") String businessType,
        @FormParam("businessId") String businessId
    ) {
        Set<FileUpload> fileUploadSet = rc.fileUploads();
        if (fileUploadSet == null || fileUploadSet.isEmpty()) {
            throw ErrorCodeEnum.PARAM_NO_VALID.toException("上传文件为空");
        }

        List<Future> taskList = new ArrayList<>(fileUploadSet.size());
        List<Attachment> attachments = new ArrayList<>(fileUploadSet.size());
        for (FileUpload fileUpload: fileUploadSet) {
            String oldName = fileUpload.fileName();
            LocalDate localDate = LocalDate.now();
            String relativePath = localDate.getYear()
                + File.separator
                + localDate.getMonthValue()
                + File.separator
                + localDate.getDayOfMonth()
                + File.separator;
            String newFileName = UUID.randomUUID().toString();
            Attachment attachment = new Attachment();
            attachment.setBusinessId(businessId);
            attachment.setBusinessType(businessType);
            attachment.setContentSize(fileUpload.size());
            attachment.setFileOldName(oldName);
            attachment.setRelativeFilePath(relativePath + newFileName);
            attachments.add(attachment);

            String absolutePath = this.attachmentService.getUploadFileFolder() + File.separator + relativePath;
            File dir = new File(absolutePath);
            if (!dir.exists()) {
                boolean result = dir.mkdirs();
                if (!result) {
                    return Future.failedFuture("上传文件时创建文件夹失败");
                }
            }

            taskList.add(rc.vertx().fileSystem().move(fileUpload.uploadedFileName(), absolutePath + newFileName));
        }
        return CompositeFuture.all(taskList).compose(r -> attachmentService.batchInsert(attachments)).map(RestResult::success);
    }

    @POST
    @Path("/insert")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<Long>> insert(Attachment attachment) {
        return attachmentService.insert(attachment).map(RestResult::success);
    }

    @PUT
    @Path("/update")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<Integer>> update(Attachment attachment) {
        Assert.assertNotNull(attachment.getId(), "Id不能为空");
        return attachmentService.update(attachment).map(RestResult::success);
    }
}

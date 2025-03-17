package com.ch.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ch.demo.entity.EntityClass;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

public interface EntityClassService extends IService<EntityClass> {

    @Transactional(timeout = 120)
    void sseBatchExport(String filePath, SseEmitter emitter) throws IOException;

    void cleanupExportProcess();

    void handleExportError(Exception ex);

    @Transactional(timeout = 120)
    void batchExport(String filePath);
}
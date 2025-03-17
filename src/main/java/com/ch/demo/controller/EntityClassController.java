package com.ch.demo.controller;

import com.ch.demo.service.EntityClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/api")
public class EntityClassController {
    @Autowired
    private EntityClassService entityClassService;

    @GetMapping(value = "/sseBatchExport", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter sseBatchExport() throws IOException {
        long startTime = System.currentTimeMillis();
        System.out.println("开始执行批量导出，当前时间：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        String filePath = System.getProperty("java.io.tmpdir") + "batchExport.xlsx";
        SseEmitter emitter = new SseEmitter(60_000L);
        System.out.println("数据导出完成，path ：" + filePath);
        System.out.println("总耗时：" + (System.currentTimeMillis() - startTime)/1000 + "s");
        emitter.onCompletion(() -> entityClassService.cleanupExportProcess());
        emitter.onError((ex) -> entityClassService.handleExportError((Exception) ex));
        
        entityClassService.sseBatchExport(filePath,emitter);
        return emitter;
    }

    @GetMapping("/batchExport")
    public ResponseEntity<String> batchExportData() {
        long startTime = System.currentTimeMillis();
        try {
            System.out.println("开始执行批量导出，当前时间：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            String filePath = System.getProperty("java.io.tmpdir") + "batchExport.xlsx";
            entityClassService.batchExport(filePath);
            System.out.println("数据导出完成，path ：" + filePath);
            System.out.println("总耗时：" + (System.currentTimeMillis() - startTime)/1000 + "s");
            return ResponseEntity.ok().body(filePath);
        } catch (Exception e) {
            System.err.println("导出异常：" + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("导出失败：" + e.getMessage());
        }
    }
}
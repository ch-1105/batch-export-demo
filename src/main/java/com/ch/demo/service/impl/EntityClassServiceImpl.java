package com.ch.demo.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ch.demo.entity.EntityClass;
import com.ch.demo.mapper.EntityClassMapper;
import com.ch.demo.service.EntityClassService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

@Service

public class EntityClassServiceImpl extends ServiceImpl<EntityClassMapper, EntityClass> implements EntityClassService {
    @Transactional(timeout = 120)
    @Override
    public void sseBatchExport(String filePath, SseEmitter emitter) throws IOException {
        long total = baseMapper.selectCount(new QueryWrapper<>());
        final int pageSize = 500;
        int totalPages = (int) Math.ceil((double)total / pageSize);
        
        try (ExcelWriter excelWriter = EasyExcel.write(filePath, EntityClass.class).build()) {
            emitter.send(SseEmitter.event().data("开始导出，总数据量：" + total));
            WriteSheet writeSheet = EasyExcel.writerSheet("Sheet1").build();
            int currentSheetRowCount = 0;
            int sheetIndex = 1;
            
            for (int current = 1; current <= totalPages; current++) {
                Page<EntityClass> page = new Page<>(current, pageSize);
                List<EntityClass> records = baseMapper.selectPage(page, null).getRecords();
                
                // 超过104万行创建新sheet
                if(currentSheetRowCount + records.size() > 1048500) {
                    writeSheet = EasyExcel.writerSheet("Sheet" + (++sheetIndex)).build();
                    currentSheetRowCount = 0;
                }
                
                System.out.println("已处理第" + current + "页，本页数据量:" + records.size() + " 当前sheet:" + writeSheet.getSheetName());
                excelWriter.write(records, writeSheet);
                currentSheetRowCount += records.size();
                records.clear(); // 及时清除内存
                System.gc(); // 建议主动触发GC
                System.out.println("当前sheet已写入行数：" + currentSheetRowCount + " 累计处理：" + (current * pageSize) + "条");
                
                // 发送进度事件
                emitter.send(SseEmitter.event().data("{\"percentage\":" + (current / totalPages) * 100 + "}"));
            }
            emitter.send(SseEmitter.event().data("{\"percentage\":100,\"filePath\":\"/api/batchExport/download\"}").name("complete"));
        } catch (Exception e) {
            emitter.completeWithError(new RuntimeException("导出过程中发生异常", e));
            throw e;
        }
    }

    @Override
    public void cleanupExportProcess() {
        // 清理导出临时文件
        // 重置导出状态标志
        // 记录清理日志
    }

    @Override
    public void handleExportError(Exception ex) {
        // 清理错误产生的临时文件
        // 发送警报通知
        // 更新导出状态为失败
    }

    @Transactional(timeout = 120)
    @Override
    public void batchExport(String filePath) {
        long total = baseMapper.selectCount(new QueryWrapper<EntityClass>());
        int pageSize = 500;
        int totalPages = (int) Math.ceil((double)total / pageSize);
        System.out.println("总数据量：" + total + " 预计分" + totalPages + "页导出");

        try (ExcelWriter excelWriter = EasyExcel.write(filePath, EntityClass.class).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet("Sheet1").build();
            int currentSheetRowCount = 0;
            int sheetIndex = 1;

            for (int current = 1; current <= totalPages; current++) {
                Page<EntityClass> page = new Page<>(current, pageSize);
                List<EntityClass> records = baseMapper.selectPage(page, null).getRecords();

                // 超过104万行创建新sheet
                if(currentSheetRowCount + records.size() > 1048500) {
                    writeSheet = EasyExcel.writerSheet("Sheet" + (++sheetIndex)).build();
                    currentSheetRowCount = 0;
                }

                System.out.println("已处理第" + current + "页，本页数据量:" + records.size() + " 当前sheet:" + writeSheet.getSheetName());
                excelWriter.write(records, writeSheet);
                currentSheetRowCount += records.size();
                records.clear(); // 及时清除内存
                System.gc(); // 建议主动触发GC
                System.out.println("当前sheet已写入行数：" + currentSheetRowCount + " 累计处理：" + (current * pageSize) + "条");
            }
        } catch (Exception e) {
            throw new RuntimeException("批量导出失败", e);
        }
    }
}
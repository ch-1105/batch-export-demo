package com.ch.demo.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class ExcelExportUtil {
    public static void exportDataToExcel(String filePath, List<?> data, Class<?> head) {
        try (OutputStream out = new FileOutputStream(filePath)) {
            ExcelWriterSheetBuilder writerSheetBuilder = EasyExcel.write(out, head).sheet("Sheet1");
            WriteSheet writeSheet = writerSheetBuilder.build();
            writerSheetBuilder.doWrite(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
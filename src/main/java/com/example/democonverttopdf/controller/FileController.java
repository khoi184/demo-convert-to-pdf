package com.example.democonverttopdf.controller;

import com.itextpdf.text.Document;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

@RestController
public class FileController {

    @PostMapping("/convert")
    public ResponseEntity<Resource> convertExcelToPdf(@RequestParam(value = "file", required = false) MultipartFile file) throws Exception {
//        // Create a temporary file to save the uploaded Excel file
//        File tempFile = File.createTempFile("input", ".xlsx");
//        file.transferTo(tempFile);

        // Generate the PDF file from the Excel file
        File outputFile = File.createTempFile("output", ".pdf");
        convertExcelToPdf(outputFile);

        // Return the generated PDF file as a resource
        Resource resource = new FileSystemResource(outputFile);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(resource.getFile().length())
                .body(resource);
    }

    private void convertExcelToPdf(File outputFile) throws Exception {
        // Load the Excel file
        FileInputStream inputStream = new FileInputStream("src/main/resources/static/input.xlsx");
        Workbook workbook = new XSSFWorkbook(inputStream);

        // Create a PDF document
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(outputFile));
        document.open();

        // Loop through each sheet in the workbook
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(i);

            // Create a table for the sheet data
            PdfPTable table = new PdfPTable(sheet.getRow(0).getLastCellNum());
            table.setWidthPercentage(100);

            // Add the headers
            for (Cell cell : sheet.getRow(0)) {
                table.addCell(new Phrase(cell.getStringCellValue()));
            }

            // Add the data
            for (int j = 1; j < sheet.getPhysicalNumberOfRows(); j++) {
                Row row = sheet.getRow(j);
                for (Cell cell : row) {
                    table.addCell(new Phrase(cell.getStringCellValue()));
                }
            }

            // Add the table to the document
            document.add(table);
        }

        // Close the document and the input stream
        document.close();
        inputStream.close();
    }
}

package ui;

import model.Invoice;
import service.InvoiceService;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class ReportHelper {

    public static List<Invoice> filterInvoicesByDateRange(List<Invoice> invoices,
            java.time.LocalDate fromDate,
            java.time.LocalDate toDate) {
        if (fromDate == null || toDate == null) {
            return invoices;
        }

        return invoices.stream()
                .filter(inv -> {
                    if (inv.getCreatedAt() == null)
                        return false;
                    try {
                        java.time.LocalDate invDate = java.time.LocalDate.parse(inv.getCreatedAt().substring(0, 10));
                        return !invDate.isBefore(fromDate) && !invDate.isAfter(toDate);
                    } catch (Exception e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());
    }

    public static void exportReportToPDF(List<Invoice> filteredInvoices,
            java.time.LocalDate fromDate,
            java.time.LocalDate toDate,
            javafx.stage.Window owner) {
        try {
            // Calculate stats
            double totalRevenue = filteredInvoices.stream()
                    .filter(inv -> inv.getStatus().equals("paid"))
                    .mapToDouble(Invoice::getTotalAmount)
                    .sum();

            int totalInvoices = filteredInvoices.size();
            long uniqueCustomers = filteredInvoices.stream()
                    .map(Invoice::getPhone)
                    .filter(phone -> phone != null && !phone.isEmpty())
                    .distinct()
                    .count();
            long paidInvoices = filteredInvoices.stream()
                    .filter(inv -> inv.getStatus().equals("paid"))
                    .count();

            // Create file chooser
            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Lưu Báo Cáo PDF");
            fileChooser.setInitialFileName("BaoCao_" + java.time.LocalDate.now() + ".pdf");
            fileChooser.getExtensionFilters().add(
                    new javafx.stage.FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

            java.io.File file = fileChooser.showSaveDialog(owner);
            if (file == null)
                return;

            // Create PDF
            com.itextpdf.kernel.pdf.PdfWriter writer = new com.itextpdf.kernel.pdf.PdfWriter(file);
            com.itextpdf.kernel.pdf.PdfDocument pdf = new com.itextpdf.kernel.pdf.PdfDocument(writer);
            com.itextpdf.layout.Document document = new com.itextpdf.layout.Document(pdf);

            // Sử dụng font hỗ trợ tiếng Việt từ PDFFontHelper
            com.itextpdf.kernel.font.PdfFont font = util.PDFFontHelper.createVietnameseFont();
            com.itextpdf.kernel.font.PdfFont boldFont = util.PDFFontHelper.createVietnameseFont(true);

            // In thông tin font để debug
            System.out.println("=== XUẤT BÁO CÁO PDF ===");
            if (util.PDFFontHelper.testVietnameseSupport(font)) {
                System.out.println("✓ Font hỗ trợ tiếng Việt");
            } else {
                System.out.println("⚠ Font có thể không hỗ trợ đầy đủ tiếng Việt");
            }

            // Title với font đậm
            document.add(new com.itextpdf.layout.element.Paragraph("CÔNG TY TNHH TM DV PHỤ TÙNG Ô TÔ MINH TÂM")
                    .setFont(boldFont)
                    .setFontSize(20)
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));

            document.add(new com.itextpdf.layout.element.Paragraph(
                    "Ngã tư Trương Định và An Dương Vương, P. Nghĩa Lộ, tỉnh Quảng Ngãi")
                    .setFont(boldFont)
                    .setFontSize(12)
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));

            document.add(new com.itextpdf.layout.element.Paragraph("BÁO CÁO DANH THU")
                    .setFont(boldFont)
                    .setFontSize(18)
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));

            document.add(new com.itextpdf.layout.element.Paragraph("\n"));

            // Date range
            String dateRangeText = "Thời gian: ";
            if (fromDate != null && toDate != null) {
                dateRangeText += fromDate + " đến " + toDate;
            } else {
                dateRangeText += "Tất cả";
            }
            document.add(new com.itextpdf.layout.element.Paragraph(dateRangeText)
                    .setFont(font)
                    .setFontSize(12));

            document.add(new com.itextpdf.layout.element.Paragraph("Ngày xuất: " + java.time.LocalDate.now())
                    .setFont(font)
                    .setFontSize(12));

            document.add(new com.itextpdf.layout.element.Paragraph("\n"));

            // Stats table với font đậm cho tiêu đề
            document.add(new com.itextpdf.layout.element.Paragraph("TỔNG QUAN")
                    .setFont(boldFont)
                    .setFontSize(14));

            com.itextpdf.layout.element.Table statsTable = new com.itextpdf.layout.element.Table(2);
            statsTable.setWidth(com.itextpdf.layout.properties.UnitValue.createPercentValue(100));

            statsTable.addCell(new com.itextpdf.layout.element.Cell().add(
                    new com.itextpdf.layout.element.Paragraph("Tổng doanh thu:").setFont(font)));
            statsTable.addCell(new com.itextpdf.layout.element.Cell().add(
                    new com.itextpdf.layout.element.Paragraph(String.format("%,.0f VNĐ", totalRevenue)).setFont(font)));

            statsTable.addCell(new com.itextpdf.layout.element.Cell().add(
                    new com.itextpdf.layout.element.Paragraph("Tổng hóa đơn:").setFont(font)));
            statsTable.addCell(new com.itextpdf.layout.element.Cell().add(
                    new com.itextpdf.layout.element.Paragraph(String.valueOf(totalInvoices)).setFont(font)));

            statsTable.addCell(new com.itextpdf.layout.element.Cell().add(
                    new com.itextpdf.layout.element.Paragraph("Khách hàng:").setFont(font)));
            statsTable.addCell(new com.itextpdf.layout.element.Cell().add(
                    new com.itextpdf.layout.element.Paragraph(String.valueOf(uniqueCustomers)).setFont(font)));

            statsTable.addCell(new com.itextpdf.layout.element.Cell().add(
                    new com.itextpdf.layout.element.Paragraph("Đã thanh toán:").setFont(font)));
            statsTable.addCell(new com.itextpdf.layout.element.Cell().add(
                    new com.itextpdf.layout.element.Paragraph(String.valueOf(paidInvoices)).setFont(font)));

            document.add(statsTable);

            document.add(new com.itextpdf.layout.element.Paragraph("\n"));

            // Invoice list với font đậm cho tiêu đề
            document.add(new com.itextpdf.layout.element.Paragraph("CHI TIẾT HÓA ĐƠN")
                    .setFont(boldFont)
                    .setFontSize(14));

            com.itextpdf.layout.element.Table invoiceTable = new com.itextpdf.layout.element.Table(4);
            invoiceTable.setWidth(com.itextpdf.layout.properties.UnitValue.createPercentValue(100));

            // Header với font đậm
            invoiceTable.addHeaderCell(new com.itextpdf.layout.element.Cell().add(
                    new com.itextpdf.layout.element.Paragraph("Mã HĐ").setFont(boldFont)));
            invoiceTable.addHeaderCell(new com.itextpdf.layout.element.Cell().add(
                    new com.itextpdf.layout.element.Paragraph("Khách hàng").setFont(boldFont)));
            invoiceTable.addHeaderCell(new com.itextpdf.layout.element.Cell().add(
                    new com.itextpdf.layout.element.Paragraph("Tổng tiền").setFont(boldFont)));
            invoiceTable.addHeaderCell(new com.itextpdf.layout.element.Cell().add(
                    new com.itextpdf.layout.element.Paragraph("Trạng thái").setFont(boldFont)));

            // Data rows
            for (Invoice inv : filteredInvoices) {
                invoiceTable.addCell(new com.itextpdf.layout.element.Cell().add(
                        new com.itextpdf.layout.element.Paragraph("#" + String.format("%05d", inv.getId()))
                                .setFont(font)));
                invoiceTable.addCell(new com.itextpdf.layout.element.Cell().add(
                        new com.itextpdf.layout.element.Paragraph(inv.getCustomerName()).setFont(font)));
                invoiceTable.addCell(new com.itextpdf.layout.element.Cell().add(
                        new com.itextpdf.layout.element.Paragraph(String.format("%,.0f", inv.getTotalAmount()))
                                .setFont(font)));
                invoiceTable.addCell(new com.itextpdf.layout.element.Cell().add(
                        new com.itextpdf.layout.element.Paragraph(
                                inv.getStatus().equals("paid") ? "Đã thanh toán" : "Chưa thanh toán").setFont(font)));
            }

            document.add(invoiceTable);

            document.close();

            javafx.scene.control.Alert alert = util.AlertHelper.createAlert(
                    javafx.scene.control.Alert.AlertType.INFORMATION,
                    "Thành công",
                    "Xuất báo cáo PDF thành công!\nĐã lưu tại: " + file.getAbsolutePath());
            alert.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            javafx.scene.control.Alert alert = util.AlertHelper.createAlert(
                    javafx.scene.control.Alert.AlertType.ERROR,
                    "Lỗi",
                    "Không thể xuất báo cáo: " + e.getMessage());
            alert.showAndWait();
        }
    }

    public static List<model.DailyReportRow> generateDailyReportRows(List<Invoice> invoices) {
        List<model.DailyReportRow> reportRows = new ArrayList<>();
        int stt = 1;
        dao.InvoiceItemDAO itemDAO = new dao.InvoiceItemDAO();
        
        for (Invoice inv : invoices) {
            List<model.InvoiceItem> items = itemDAO.getItemsByInvoiceId(inv.getId());
            String servicesDesc = items.stream()
                .map(model.InvoiceItem::getItemName)
                .collect(Collectors.joining(", "));
            
            double revWash = 0;
            double revCare = 0;
            double revAccessory = 0;
            double revPaint = 0;
            double costCare = 0;
            double costAccessory = 0;
            double costPaint = 0;
            
            for (model.InvoiceItem item : items) {
                String cat = item.getCategory() != null ? item.getCategory().toLowerCase().trim() : "";
                double price = item.getTotalPrice();
                double cost = item.getCostPrice() * item.getQuantity();
                
                if (cat.contains("rửa") || cat.contains("rua")) {
                    revWash += price;
                } else if (cat.contains("chăm sóc") || cat.contains("cham soc")) {
                    revCare += price;
                    costCare += cost;
                } else if (cat.contains("phụ kiện") || cat.contains("phu kien")) {
                    revAccessory += price;
                    costAccessory += cost;
                } else if (cat.contains("sơn") || cat.contains("son")) {
                    revPaint += price;
                    costPaint += cost;
                } else {
                    if ("product".equals(item.getItemType())) {
                        revAccessory += price;
                        costAccessory += cost;
                    } else if ("package".equals(item.getItemType())) {
                        revCare += price;
                        costCare += cost;
                    } else {
                        revWash += price;
                    }
                }
            }
            
            double profitCare = revCare - costCare;
            double profitAccessory = revAccessory - costAccessory;
            double profitPaint = revPaint - costPaint;
            String pMethod = inv.getPaymentMethod();
            if (pMethod == null || pMethod.trim().isEmpty()) {
                pMethod = "TM";
            }
            
            String dateFormatted = "";
            if (inv.getCreatedAt() != null && inv.getCreatedAt().length() >= 10) {
                try {
                    java.time.LocalDate d = java.time.LocalDate.parse(inv.getCreatedAt().substring(0, 10));
                    dateFormatted = d.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                } catch (Exception e) {
                    dateFormatted = inv.getCreatedAt().substring(0, 10);
                }
            }
            
            model.DailyReportRow row = new model.DailyReportRow(
                stt++,
                dateFormatted,
                inv.getLicensePlate() != null ? inv.getLicensePlate() : "",
                servicesDesc,
                revWash,
                revCare,
                revAccessory,
                revPaint,
                inv.getTotalAmount(),
                pMethod,
                costCare,
                costAccessory,
                costPaint,
                profitCare,
                profitAccessory,
                profitPaint,
                inv.getNotes() != null ? inv.getNotes() : ""
            );
            reportRows.add(row);
        }
        return reportRows;
    }

    public static void exportToExcel(List<model.DailyReportRow> rows,
                                     java.time.LocalDate fromDate,
                                     java.time.LocalDate toDate,
                                     javafx.stage.Window owner) {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Lưu Báo Cáo Excel");
        fileChooser.setInitialFileName("BaoSoHangNgay_" + java.time.LocalDate.now() + ".xlsx");
        fileChooser.getExtensionFilters().add(
                new javafx.stage.FileChooser.ExtensionFilter("Excel Workbook", "*.xlsx"));
        
        java.io.File file = fileChooser.showSaveDialog(owner);
        if (file == null) return;
        
        try (org.apache.poi.xssf.usermodel.XSSFWorkbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {
            org.apache.poi.xssf.usermodel.XSSFSheet sheet = workbook.createSheet("Báo số hằng ngày");
            sheet.setDisplayGridlines(true);
            
            org.apache.poi.xssf.usermodel.XSSFCellStyle titleStyle = workbook.createCellStyle();
            org.apache.poi.xssf.usermodel.XSSFFont titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 16);
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
            
            org.apache.poi.xssf.usermodel.XSSFCellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.xssf.usermodel.XSSFFont headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 10);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
            headerStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            headerStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            headerStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            headerStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            
            org.apache.poi.xssf.usermodel.XSSFCellStyle yellowHeaderStyle = workbook.createCellStyle();
            yellowHeaderStyle.cloneStyleFrom(headerStyle);
            yellowHeaderStyle.setFillForegroundColor(new org.apache.poi.xssf.usermodel.XSSFColor(new byte[]{(byte)255, (byte)235, (byte)59}, null));
            yellowHeaderStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
            
            org.apache.poi.xssf.usermodel.XSSFCellStyle greenHeaderStyle = workbook.createCellStyle();
            greenHeaderStyle.cloneStyleFrom(headerStyle);
            greenHeaderStyle.setFillForegroundColor(new org.apache.poi.xssf.usermodel.XSSFColor(new byte[]{(byte)200, (byte)230, (byte)201}, null));
            greenHeaderStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
            
            org.apache.poi.xssf.usermodel.XSSFCellStyle normalHeaderStyle = workbook.createCellStyle();
            normalHeaderStyle.cloneStyleFrom(headerStyle);
            normalHeaderStyle.setFillForegroundColor(new org.apache.poi.xssf.usermodel.XSSFColor(new byte[]{(byte)245, (byte)245, (byte)245}, null));
            normalHeaderStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
            
            org.apache.poi.xssf.usermodel.XSSFCellStyle borderStyle = workbook.createCellStyle();
            borderStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            borderStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            borderStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            borderStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            
            org.apache.poi.xssf.usermodel.XSSFCellStyle numberStyle = workbook.createCellStyle();
            numberStyle.cloneStyleFrom(borderStyle);
            numberStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0"));
            
            org.apache.poi.xssf.usermodel.XSSFRow row0 = sheet.createRow(0);
            org.apache.poi.xssf.usermodel.XSSFRow row1 = sheet.createRow(1);
            
            String[] r0Headers = {
                "STT", "Ngày/Tháng", "Tên xe", "Tên dịch vụ",
                "DOANH THU", "", "", "",
                "TỔNG DT THỰC NHẬN", "Phương thức thanh toán",
                "CHI PHÍ", "", "",
                "LỢI NHUẬN", "", "",
                "GHI CHÚ"
            };
            
            String[] r1Headers = {
                "", "", "", "",
                "Rửa xe", "Chăm sóc", "Phụ kiện", "Sơn",
                "", "",
                "Chi phí chăm sóc", "Chi phí phụ kiện", "Chi phí sơn",
                "Lợi nhuận chăm sóc", "Lợi nhuận phụ kiện", "Lợi nhuận sơn",
                ""
            };
            
            for (int i = 0; i < r0Headers.length; i++) {
                org.apache.poi.xssf.usermodel.XSSFCell cell0 = row0.createCell(i);
                cell0.setCellValue(r0Headers[i]);
                
                org.apache.poi.xssf.usermodel.XSSFCell cell1 = row1.createCell(i);
                cell1.setCellValue(r1Headers[i]);
                
                if (i >= 4 && i <= 7) {
                    cell0.setCellStyle(yellowHeaderStyle);
                    cell1.setCellStyle(yellowHeaderStyle);
                } else if ((i >= 10 && i <= 12) || (i >= 13 && i <= 15)) {
                    cell0.setCellStyle(greenHeaderStyle);
                    cell1.setCellStyle(greenHeaderStyle);
                } else {
                    cell0.setCellStyle(normalHeaderStyle);
                    cell1.setCellStyle(normalHeaderStyle);
                }
            }
            
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 1, 0, 0));
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 1, 1, 1));
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 1, 2, 2));
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 1, 3, 3));
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 4, 7));
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 1, 8, 8));
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 1, 9, 9));
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 10, 12));
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 13, 15));
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 1, 16, 16));
            
            int rowIndex = 2;
            for (model.DailyReportRow data : rows) {
                org.apache.poi.xssf.usermodel.XSSFRow r = sheet.createRow(rowIndex++);
                r.createCell(0).setCellValue(data.getStt());
                r.createCell(1).setCellValue(data.getDate());
                r.createCell(2).setCellValue(data.getLicensePlate());
                r.createCell(3).setCellValue(data.getServices());
                
                r.createCell(4).setCellValue(data.getRevenueWash());
                r.createCell(5).setCellValue(data.getRevenueCare());
                r.createCell(6).setCellValue(data.getRevenueAccessory());
                r.createCell(7).setCellValue(data.getRevenuePaint());
                r.createCell(8).setCellValue(data.getTotalRevenue());
                r.createCell(9).setCellValue(data.getPaymentMethod());
                r.createCell(10).setCellValue(data.getCostCare());
                r.createCell(11).setCellValue(data.getCostAccessory());
                r.createCell(12).setCellValue(data.getCostPaint());
                r.createCell(13).setCellValue(data.getProfitCare());
                r.createCell(14).setCellValue(data.getProfitAccessory());
                r.createCell(15).setCellValue(data.getProfitPaint());
                r.createCell(16).setCellValue(data.getNotes());
                
                for (int col = 0; col <= 16; col++) {
                    org.apache.poi.xssf.usermodel.XSSFCell c = r.getCell(col);
                    if (c == null) c = r.createCell(col);
                    if (col == 9 || col == 1 || col == 2 || col == 3 || col == 16) {
                        c.setCellStyle(borderStyle);
                    } else if (col == 0) {
                        c.setCellStyle(borderStyle);
                    } else {
                        c.setCellStyle(numberStyle);
                    }
                }
            }
            
            org.apache.poi.xssf.usermodel.XSSFRow totalRow = sheet.createRow(rowIndex);
            totalRow.createCell(0).setCellValue("TỔNG CỘNG");
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(rowIndex, rowIndex, 0, 3));
            
            org.apache.poi.xssf.usermodel.XSSFCellStyle totalStyle = workbook.createCellStyle();
            org.apache.poi.xssf.usermodel.XSSFFont totalFont = workbook.createFont();
            totalFont.setBold(true);
            totalStyle.setFont(totalFont);
            totalStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.DOUBLE);
            totalStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.DOUBLE);
            totalStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            totalStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            
            org.apache.poi.xssf.usermodel.XSSFCellStyle totalNumStyle = workbook.createCellStyle();
            totalNumStyle.cloneStyleFrom(totalStyle);
            totalNumStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0"));
            
            for (int col = 0; col <= 16; col++) {
                org.apache.poi.xssf.usermodel.XSSFCell c = totalRow.getCell(col);
                if (c == null) c = totalRow.createCell(col);
                c.setCellStyle(totalStyle);
                
                if (col == 4 || col == 5 || col == 6 || col == 7 || col == 8 || col == 10 || col == 11 || col == 12 || col == 13 || col == 14 || col == 15) {
                    char colLetter = (char) ('A' + col);
                    c.setCellFormula("SUM(" + colLetter + "3:" + colLetter + (rowIndex) + ")");
                    c.setCellStyle(totalNumStyle);
                }
            }
            
            for (int i = 0; i <= 16; i++) {
                sheet.autoSizeColumn(i);
            }
            
            try (java.io.FileOutputStream fileOut = new java.io.FileOutputStream(file)) {
                workbook.write(fileOut);
            }
            
            javafx.scene.control.Alert succ = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            succ.setTitle("Thành công");
            succ.setHeaderText(null);
            succ.setContentText("Xuất file Excel báo cáo thành công!\nĐã lưu tại: " + file.getAbsolutePath());
            succ.showAndWait();
            
        } catch (Exception ex) {
            ex.printStackTrace();
            javafx.scene.control.Alert err = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            err.setTitle("Lỗi");
            err.setHeaderText(null);
            err.setContentText("Đã xảy ra lỗi khi xuất file Excel: " + ex.getMessage());
            err.showAndWait();
        }
    }

    public static void exportDailyReportToPDF(List<model.DailyReportRow> rows,
            java.time.LocalDate fromDate,
            java.time.LocalDate toDate,
            javafx.stage.Window owner) {
        try {
            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Lưu Báo Cáo PDF");
            fileChooser.setInitialFileName("BaoSoHangNgay_" + java.time.LocalDate.now() + ".pdf");
            fileChooser.getExtensionFilters().add(
                    new javafx.stage.FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

            java.io.File file = fileChooser.showSaveDialog(owner);
            if (file == null)
                return;

            com.itextpdf.kernel.pdf.PdfWriter writer = new com.itextpdf.kernel.pdf.PdfWriter(file);
            com.itextpdf.kernel.pdf.PdfDocument pdf = new com.itextpdf.kernel.pdf.PdfDocument(writer);
            pdf.setDefaultPageSize(com.itextpdf.kernel.geom.PageSize.A4.rotate());
            com.itextpdf.layout.Document document = new com.itextpdf.layout.Document(pdf);
            document.setMargins(15, 15, 15, 15);

            com.itextpdf.kernel.font.PdfFont font = util.PDFFontHelper.createVietnameseFont();
            com.itextpdf.kernel.font.PdfFont boldFont = util.PDFFontHelper.createVietnameseFont(true);

            // Left-aligned Company Info (matching the design in the second image)
            document.add(new com.itextpdf.layout.element.Paragraph("CÔNG TY TNHH TM DV PHỤ TÙNG Ô TÔ MINH TÂM")
                    .setFont(boldFont)
                    .setFontSize(10)
                    .setMarginBottom(1));
            document.add(new com.itextpdf.layout.element.Paragraph("Ngã tư An Dương Vương và Trương Định, P. Trần Phú, TP. Quảng Ngãi, T. Quảng Ngãi.")
                    .setFont(font)
                    .setFontSize(8.5f)
                    .setMarginBottom(1));
            document.add(new com.itextpdf.layout.element.Paragraph("MST: 4300899201")
                    .setFont(font)
                    .setFontSize(8.5f)
                    .setMarginBottom(15));

            // Centered Title
            document.add(new com.itextpdf.layout.element.Paragraph("BÁO SỐ HẰNG NGÀY")
                    .setFont(boldFont)
                    .setFontSize(16)
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                    .setMarginBottom(2));

            String dateRangeText = "";
            if (fromDate != null && toDate != null) {
                if (fromDate.getMonthValue() == toDate.getMonthValue() && fromDate.getYear() == toDate.getYear()) {
                    dateRangeText = "Tháng " + fromDate.getMonthValue() + " Năm " + fromDate.getYear();
                } else {
                    dateRangeText = "Thời gian: " + fromDate.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " đến " + toDate.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                }
            } else {
                dateRangeText = "Tất cả";
            }
            document.add(new com.itextpdf.layout.element.Paragraph(dateRangeText)
                    .setFont(boldFont)
                    .setFontSize(11)
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                    .setMarginBottom(10));

            document.add(new com.itextpdf.layout.element.Paragraph("Ngày xuất: " + java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                    .setFont(font)
                    .setFontSize(9)
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT)
                    .setMarginBottom(5));

            float[] columnWidths = {15f, 45f, 45f, 90f, 42f, 46f, 46f, 42f, 45f, 20f, 46f, 46f, 42f, 46f, 46f, 42f, 70f};
            com.itextpdf.layout.element.Table table = new com.itextpdf.layout.element.Table(columnWidths);
            table.setWidth(com.itextpdf.layout.properties.UnitValue.createPercentValue(100));

            com.itextpdf.kernel.colors.DeviceRgb headerBg = new com.itextpdf.kernel.colors.DeviceRgb(245, 245, 245);

            // Row 1 Header with proper spans and alignment
            table.addHeaderCell(new com.itextpdf.layout.element.Cell(2, 1)
                .add(new com.itextpdf.layout.element.Paragraph("STT").setFont(boldFont).setFontSize(8))
                .setBackgroundColor(headerBg)
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE)
                .setPadding(3));
            
            table.addHeaderCell(new com.itextpdf.layout.element.Cell(2, 1)
                .add(new com.itextpdf.layout.element.Paragraph("Ngày").setFont(boldFont).setFontSize(8))
                .setBackgroundColor(headerBg)
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE)
                .setPadding(3));

            table.addHeaderCell(new com.itextpdf.layout.element.Cell(2, 1)
                .add(new com.itextpdf.layout.element.Paragraph("Tên xe").setFont(boldFont).setFontSize(8))
                .setBackgroundColor(headerBg)
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE)
                .setPadding(3));

            table.addHeaderCell(new com.itextpdf.layout.element.Cell(2, 1)
                .add(new com.itextpdf.layout.element.Paragraph("Dịch vụ").setFont(boldFont).setFontSize(8))
                .setBackgroundColor(headerBg)
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE)
                .setPadding(3));

            table.addHeaderCell(new com.itextpdf.layout.element.Cell(1, 4)
                .add(new com.itextpdf.layout.element.Paragraph("DOANH THU").setFont(boldFont).setFontSize(8))
                .setBackgroundColor(headerBg)
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE)
                .setPadding(3));

            table.addHeaderCell(new com.itextpdf.layout.element.Cell(2, 1)
                .add(new com.itextpdf.layout.element.Paragraph("TỔNG DT").setFont(boldFont).setFontSize(8))
                .setBackgroundColor(headerBg)
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE)
                .setPadding(3));

            table.addHeaderCell(new com.itextpdf.layout.element.Cell(2, 1)
                .add(new com.itextpdf.layout.element.Paragraph("PT").setFont(boldFont).setFontSize(8))
                .setBackgroundColor(headerBg)
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE)
                .setPadding(3));

            table.addHeaderCell(new com.itextpdf.layout.element.Cell(1, 3)
                .add(new com.itextpdf.layout.element.Paragraph("CHI PHÍ").setFont(boldFont).setFontSize(8))
                .setBackgroundColor(headerBg)
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE)
                .setPadding(3));

            table.addHeaderCell(new com.itextpdf.layout.element.Cell(1, 3)
                .add(new com.itextpdf.layout.element.Paragraph("LỢI NHUẬN").setFont(boldFont).setFontSize(8))
                .setBackgroundColor(headerBg)
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE)
                .setPadding(3));

            table.addHeaderCell(new com.itextpdf.layout.element.Cell(2, 1)
                .add(new com.itextpdf.layout.element.Paragraph("GHI CHÚ").setFont(boldFont).setFontSize(8))
                .setBackgroundColor(headerBg)
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE)
                .setPadding(3));

            // Row 2 Header
            String[] subHeaders = {
                "Rửa xe", "Chăm sóc", "Phụ kiện", "Sơn xe",
                "Chăm sóc", "Phụ kiện", "Sơn xe",
                "Chăm sóc", "Phụ kiện", "Sơn xe"
            };
            for (String sh : subHeaders) {
                table.addHeaderCell(new com.itextpdf.layout.element.Cell(1, 1)
                    .add(new com.itextpdf.layout.element.Paragraph(sh).setFont(boldFont).setFontSize(7f))
                    .setBackgroundColor(headerBg)
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                    .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE)
                    .setPadding(3));
            }

            double sumWash = 0, sumCare = 0, sumAccessory = 0, sumPaint = 0, sumTotal = 0;
            double sumCostCare = 0, sumCostAccessory = 0, sumCostPaint = 0;
            double sumProfitCare = 0, sumProfitAccessory = 0, sumProfitPaint = 0;

            java.util.function.BiConsumer<String, com.itextpdf.layout.properties.TextAlignment> addBodyCell = (text, align) -> {
                com.itextpdf.layout.element.Cell cell = new com.itextpdf.layout.element.Cell()
                    .add(new com.itextpdf.layout.element.Paragraph(text != null ? text : "").setFont(font).setFontSize(8))
                    .setPadding(3)
                    .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
                if (align != null) {
                    cell.setTextAlignment(align);
                }
                table.addCell(cell);
            };

            for (model.DailyReportRow row : rows) {
                addBodyCell.accept(String.valueOf(row.getStt()), com.itextpdf.layout.properties.TextAlignment.CENTER);
                addBodyCell.accept(row.getDate(), com.itextpdf.layout.properties.TextAlignment.CENTER);
                addBodyCell.accept(row.getLicensePlate(), com.itextpdf.layout.properties.TextAlignment.LEFT);
                addBodyCell.accept(row.getServices(), com.itextpdf.layout.properties.TextAlignment.LEFT);

                addBodyCell.accept(String.format("%,.0f", row.getRevenueWash()), com.itextpdf.layout.properties.TextAlignment.RIGHT);
                addBodyCell.accept(String.format("%,.0f", row.getRevenueCare()), com.itextpdf.layout.properties.TextAlignment.RIGHT);
                addBodyCell.accept(String.format("%,.0f", row.getRevenueAccessory()), com.itextpdf.layout.properties.TextAlignment.RIGHT);
                addBodyCell.accept(String.format("%,.0f", row.getRevenuePaint()), com.itextpdf.layout.properties.TextAlignment.RIGHT);
                addBodyCell.accept(String.format("%,.0f", row.getTotalRevenue()), com.itextpdf.layout.properties.TextAlignment.RIGHT);
                
                addBodyCell.accept(row.getPaymentMethod(), com.itextpdf.layout.properties.TextAlignment.CENTER);

                addBodyCell.accept(String.format("%,.0f", row.getCostCare()), com.itextpdf.layout.properties.TextAlignment.RIGHT);
                addBodyCell.accept(String.format("%,.0f", row.getCostAccessory()), com.itextpdf.layout.properties.TextAlignment.RIGHT);
                addBodyCell.accept(String.format("%,.0f", row.getCostPaint()), com.itextpdf.layout.properties.TextAlignment.RIGHT);

                addBodyCell.accept(String.format("%,.0f", row.getProfitCare()), com.itextpdf.layout.properties.TextAlignment.RIGHT);
                addBodyCell.accept(String.format("%,.0f", row.getProfitAccessory()), com.itextpdf.layout.properties.TextAlignment.RIGHT);
                addBodyCell.accept(String.format("%,.0f", row.getProfitPaint()), com.itextpdf.layout.properties.TextAlignment.RIGHT);

                addBodyCell.accept(row.getNotes(), com.itextpdf.layout.properties.TextAlignment.LEFT);

                sumWash += row.getRevenueWash();
                sumCare += row.getRevenueCare();
                sumAccessory += row.getRevenueAccessory();
                sumPaint += row.getRevenuePaint();
                sumTotal += row.getTotalRevenue();
                sumCostCare += row.getCostCare();
                sumCostAccessory += row.getCostAccessory();
                sumCostPaint += row.getCostPaint();
                sumProfitCare += row.getProfitCare();
                sumProfitAccessory += row.getProfitAccessory();
                sumProfitPaint += row.getProfitPaint();
            }

            table.addCell(new com.itextpdf.layout.element.Cell(1, 4)
                .add(new com.itextpdf.layout.element.Paragraph("TỔNG CỘNG").setFont(boldFont).setFontSize(8))
                .setPadding(3)
                .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE));
            
            java.util.function.Consumer<Double> addTotalCell = val -> {
                table.addCell(new com.itextpdf.layout.element.Cell()
                    .add(new com.itextpdf.layout.element.Paragraph(String.format("%,.0f", val)).setFont(boldFont).setFontSize(8))
                    .setPadding(3)
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT)
                    .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE));
            };

            addTotalCell.accept(sumWash);
            addTotalCell.accept(sumCare);
            addTotalCell.accept(sumAccessory);
            addTotalCell.accept(sumPaint);
            addTotalCell.accept(sumTotal);

            table.addCell(new com.itextpdf.layout.element.Cell()
                .add(new com.itextpdf.layout.element.Paragraph("").setFont(boldFont).setFontSize(8))
                .setPadding(3));

            addTotalCell.accept(sumCostCare);
            addTotalCell.accept(sumCostAccessory);
            addTotalCell.accept(sumCostPaint);
            addTotalCell.accept(sumProfitCare);
            addTotalCell.accept(sumProfitAccessory);
            addTotalCell.accept(sumProfitPaint);

            table.addCell(new com.itextpdf.layout.element.Cell()
                .add(new com.itextpdf.layout.element.Paragraph("").setFont(boldFont).setFontSize(8))
                .setPadding(3));

            document.add(table);
            document.close();

            javafx.scene.control.Alert alert = util.AlertHelper.createAlert(
                    javafx.scene.control.Alert.AlertType.INFORMATION,
                    "Thành công",
                    "Xuất báo cáo PDF thành công!\nĐã lưu tại: " + file.getAbsolutePath());
            alert.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            javafx.scene.control.Alert alert = util.AlertHelper.createAlert(
                    javafx.scene.control.Alert.AlertType.ERROR,
                    "Lỗi",
                    "Không thể xuất báo cáo PDF: " + e.getMessage());
            alert.showAndWait();
        }
    }

    public static void exportYearlyToExcel(List<model.YearlyReportRow> rows,
                                           int year,
                                           javafx.stage.Window owner) {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Lưu Báo Cáo Excel Năm");
        fileChooser.setInitialFileName("BangThongKeNam_" + year + "_" + java.time.LocalDate.now() + ".xlsx");
        fileChooser.getExtensionFilters().add(
                new javafx.stage.FileChooser.ExtensionFilter("Excel Workbook", "*.xlsx"));
        
        java.io.File file = fileChooser.showSaveDialog(owner);
        if (file == null) return;
        
        try (org.apache.poi.xssf.usermodel.XSSFWorkbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {
            org.apache.poi.xssf.usermodel.XSSFSheet sheet = workbook.createSheet("Thống kê năm");
            sheet.setDisplayGridlines(true);
            
            org.apache.poi.xssf.usermodel.XSSFCellStyle titleStyle = workbook.createCellStyle();
            org.apache.poi.xssf.usermodel.XSSFFont titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 16);
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
            
            org.apache.poi.xssf.usermodel.XSSFRow titleRow = sheet.createRow(0);
            org.apache.poi.xssf.usermodel.XSSFCell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("BẢNG THỐNG KÊ NĂM " + year);
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 11));
            
            org.apache.poi.xssf.usermodel.XSSFCellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.xssf.usermodel.XSSFFont headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 10);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
            headerStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            headerStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            headerStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            headerStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            
            org.apache.poi.xssf.usermodel.XSSFCellStyle yellowHeaderStyle = workbook.createCellStyle();
            yellowHeaderStyle.cloneStyleFrom(headerStyle);
            yellowHeaderStyle.setFillForegroundColor(new org.apache.poi.xssf.usermodel.XSSFColor(new byte[]{(byte)255, (byte)235, (byte)59}, null));
            yellowHeaderStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
            
            org.apache.poi.xssf.usermodel.XSSFCellStyle greenHeaderStyle = workbook.createCellStyle();
            greenHeaderStyle.cloneStyleFrom(headerStyle);
            greenHeaderStyle.setFillForegroundColor(new org.apache.poi.xssf.usermodel.XSSFColor(new byte[]{(byte)200, (byte)230, (byte)201}, null));
            greenHeaderStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
            
            org.apache.poi.xssf.usermodel.XSSFCellStyle normalHeaderStyle = workbook.createCellStyle();
            normalHeaderStyle.cloneStyleFrom(headerStyle);
            normalHeaderStyle.setFillForegroundColor(new org.apache.poi.xssf.usermodel.XSSFColor(new byte[]{(byte)245, (byte)245, (byte)245}, null));
            normalHeaderStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
            
            org.apache.poi.xssf.usermodel.XSSFCellStyle borderStyle = workbook.createCellStyle();
            borderStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            borderStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            borderStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            borderStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            
            org.apache.poi.xssf.usermodel.XSSFCellStyle numberStyle = workbook.createCellStyle();
            numberStyle.cloneStyleFrom(borderStyle);
            numberStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0"));
            
            org.apache.poi.xssf.usermodel.XSSFRow row2 = sheet.createRow(2);
            org.apache.poi.xssf.usermodel.XSSFRow row3 = sheet.createRow(3);
            
            String[] r2Headers = {
                "STT", "Doanh thu rửa xe", "Doanh thu chăm sóc", "Doanh thu phụ kiện", "Doanh thu sơn xe", "Tổng doanh thu",
                "Lợi nhuận chăm sóc", "Lợi nhuận phụ kiện", "Lợi nhuận sơn", "Chi phí biến thiên", "Chi Phí Cố định", "TỔNG LỢI NHUẬN"
            };
            
            for (int i = 0; i < r2Headers.length; i++) {
                org.apache.poi.xssf.usermodel.XSSFCell cell2 = row2.createCell(i);
                cell2.setCellValue(r2Headers[i]);
                cell2.setCellStyle(normalHeaderStyle);
                
                org.apache.poi.xssf.usermodel.XSSFCell cell3 = row3.createCell(i);
                cell3.setCellStyle(normalHeaderStyle);
                
                sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(2, 3, i, i));
            }
            
            int rowIndex = 4;
            // Write first 12 months. Row index 4 to 15.
            for (int m = 0; m < 12; m++) {
                model.YearlyReportRow data = rows.get(m);
                org.apache.poi.xssf.usermodel.XSSFRow r = sheet.createRow(rowIndex++);
                r.createCell(0).setCellValue(data.getMonth());
                r.createCell(1).setCellValue(data.getRevenueWash());
                r.createCell(2).setCellValue(data.getRevenueCare());
                r.createCell(3).setCellValue(data.getRevenueAccessory());
                r.createCell(4).setCellValue(data.getRevenuePaint());
                r.createCell(5).setCellValue(data.getTotalRevenue());
                r.createCell(6).setCellValue(data.getProfitCare());
                r.createCell(7).setCellValue(data.getProfitAccessory());
                r.createCell(8).setCellValue(data.getProfitPaint());
                r.createCell(9).setCellValue(data.getVariableCost());
                r.createCell(10).setCellValue(data.getFixedCost());
                r.createCell(11).setCellValue(data.getTotalNetProfit());
                
                for (int col = 0; col <= 11; col++) {
                    org.apache.poi.xssf.usermodel.XSSFCell c = r.getCell(col);
                    if (c == null) c = r.createCell(col);
                    if (col == 0) {
                        c.setCellStyle(borderStyle);
                    } else {
                        c.setCellStyle(numberStyle);
                    }
                }
            }
            
            // Row 16 (index 16) is TOTAL row.
            org.apache.poi.xssf.usermodel.XSSFRow totalRow = sheet.createRow(rowIndex);
            totalRow.createCell(0).setCellValue("TỔNG CỘNG");
            
            org.apache.poi.xssf.usermodel.XSSFCellStyle totalStyle = workbook.createCellStyle();
            org.apache.poi.xssf.usermodel.XSSFFont totalFont = workbook.createFont();
            totalFont.setBold(true);
            totalStyle.setFont(totalFont);
            totalStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.DOUBLE);
            totalStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.DOUBLE);
            totalStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            totalStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            
            org.apache.poi.xssf.usermodel.XSSFCellStyle totalNumStyle = workbook.createCellStyle();
            totalNumStyle.cloneStyleFrom(totalStyle);
            totalNumStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0"));
            
            for (int col = 0; col <= 11; col++) {
                org.apache.poi.xssf.usermodel.XSSFCell c = totalRow.getCell(col);
                if (c == null) c = totalRow.createCell(col);
                c.setCellStyle(totalStyle);
                
                if (col >= 1 && col <= 11) {
                    char colLetter = (char) ('A' + col);
                    c.setCellFormula("SUM(" + colLetter + "5:" + colLetter + (rowIndex) + ")");
                    c.setCellStyle(totalNumStyle);
                }
            }
            
            for (int i = 0; i <= 11; i++) {
                sheet.autoSizeColumn(i);
            }
            
            try (java.io.FileOutputStream fileOut = new java.io.FileOutputStream(file)) {
                workbook.write(fileOut);
            }
            
            javafx.scene.control.Alert succ = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            succ.setTitle("Thành công");
            succ.setHeaderText(null);
            succ.setContentText("Xuất file Excel thống kê năm thành công!\nĐã lưu tại: " + file.getAbsolutePath());
            succ.showAndWait();
            
        } catch (Exception ex) {
            ex.printStackTrace();
            javafx.scene.control.Alert err = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            err.setTitle("Lỗi");
            err.setHeaderText(null);
            err.setContentText("Đã xảy ra lỗi khi xuất file Excel: " + ex.getMessage());
            err.showAndWait();
        }
    }

    public static void exportYearlyToPDF(List<model.YearlyReportRow> rows,
                                         int year,
                                         javafx.stage.Window owner) {
        try {
            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Lưu Báo Cáo PDF Năm");
            fileChooser.setInitialFileName("BangThongKeNam_" + year + "_" + java.time.LocalDate.now() + ".pdf");
            fileChooser.getExtensionFilters().add(
                    new javafx.stage.FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

            java.io.File file = fileChooser.showSaveDialog(owner);
            if (file == null) return;

            com.itextpdf.kernel.pdf.PdfWriter writer = new com.itextpdf.kernel.pdf.PdfWriter(file);
            com.itextpdf.kernel.geom.PageSize pageSize = com.itextpdf.kernel.geom.PageSize.A4.rotate();
            com.itextpdf.kernel.pdf.PdfDocument pdf = new com.itextpdf.kernel.pdf.PdfDocument(writer);
            pdf.setDefaultPageSize(pageSize);
            
            com.itextpdf.layout.Document document = new com.itextpdf.layout.Document(pdf);
            document.setMargins(15, 15, 15, 15);

            com.itextpdf.kernel.font.PdfFont font = util.PDFFontHelper.createVietnameseFont();
            com.itextpdf.kernel.font.PdfFont boldFont = util.PDFFontHelper.createVietnameseFont(true);

            // Left-aligned Company Info (matching the design in the second image)
            document.add(new com.itextpdf.layout.element.Paragraph("CÔNG TY TNHH TM DV PHỤ TÙNG Ô TÔ MINH TÂM")
                    .setFont(boldFont)
                    .setFontSize(10)
                    .setMarginBottom(1));
            document.add(new com.itextpdf.layout.element.Paragraph("Ngã tư An Dương Vương và Trương Định, P. Trần Phú, TP. Quảng Ngãi, T. Quảng Ngãi.")
                    .setFont(font)
                    .setFontSize(8.5f)
                    .setMarginBottom(1));
            document.add(new com.itextpdf.layout.element.Paragraph("MST: 4300899201")
                    .setFont(font)
                    .setFontSize(8.5f)
                    .setMarginBottom(15));

            // Centered Title
            document.add(new com.itextpdf.layout.element.Paragraph("BẢNG THỐNG KÊ DOANH THU & CHI PHÍ NĂM")
                    .setFont(boldFont)
                    .setFontSize(16)
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                    .setMarginBottom(2));
            document.add(new com.itextpdf.layout.element.Paragraph("Năm " + year)
                    .setFont(boldFont)
                    .setFontSize(12)
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                    .setMarginBottom(10));

            document.add(new com.itextpdf.layout.element.Paragraph("Ngày xuất: " + java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                    .setFont(font)
                    .setFontSize(9)
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT)
                    .setMarginBottom(5));

            float[] columnWidths = {50f, 70f, 70f, 70f, 70f, 80f, 70f, 70f, 70f, 70f, 70f, 80f};
            com.itextpdf.layout.element.Table table = new com.itextpdf.layout.element.Table(columnWidths);
            table.setWidth(com.itextpdf.layout.properties.UnitValue.createPercentValue(100));

            com.itextpdf.kernel.colors.DeviceRgb headerBg = new com.itextpdf.kernel.colors.DeviceRgb(245, 245, 245);

            java.util.function.Function<String, com.itextpdf.layout.element.Cell> makeHeaderCell = text -> 
                new com.itextpdf.layout.element.Cell()
                    .add(new com.itextpdf.layout.element.Paragraph(text).setFont(boldFont).setFontSize(8))
                    .setBackgroundColor(headerBg)
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                    .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE)
                    .setPadding(3);

            table.addHeaderCell(makeHeaderCell.apply("Tháng"));
            table.addHeaderCell(makeHeaderCell.apply("DT Rửa Xe"));
            table.addHeaderCell(makeHeaderCell.apply("DT Chăm Sóc"));
            table.addHeaderCell(makeHeaderCell.apply("DT Phụ Kiện"));
            table.addHeaderCell(makeHeaderCell.apply("DT Sơn Xe"));
            table.addHeaderCell(makeHeaderCell.apply("Tổng DT"));
            table.addHeaderCell(makeHeaderCell.apply("LN Chăm Sóc"));
            table.addHeaderCell(makeHeaderCell.apply("LN Phụ Kiện"));
            table.addHeaderCell(makeHeaderCell.apply("LN Sơn"));
            table.addHeaderCell(makeHeaderCell.apply("CP Biến Thiên"));
            table.addHeaderCell(makeHeaderCell.apply("CP Cố Định"));
            table.addHeaderCell(makeHeaderCell.apply("LN RÒNG"));

            for (model.YearlyReportRow row : rows) {
                boolean isTotal = "TỔNG CỘNG".equals(row.getMonth());
                com.itextpdf.kernel.font.PdfFont activeFont = isTotal ? boldFont : font;

                java.util.function.BiConsumer<String, com.itextpdf.layout.properties.TextAlignment> addRowCell = (text, align) -> {
                    com.itextpdf.layout.element.Cell cell = new com.itextpdf.layout.element.Cell()
                        .add(new com.itextpdf.layout.element.Paragraph(text != null ? text : "").setFont(activeFont).setFontSize(8))
                        .setPadding(3)
                        .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
                    if (align != null) {
                        cell.setTextAlignment(align);
                    }
                    table.addCell(cell);
                };

                addRowCell.accept(row.getMonth(), com.itextpdf.layout.properties.TextAlignment.CENTER);
                addRowCell.accept(String.format("%,.0f", row.getRevenueWash()), com.itextpdf.layout.properties.TextAlignment.RIGHT);
                addRowCell.accept(String.format("%,.0f", row.getRevenueCare()), com.itextpdf.layout.properties.TextAlignment.RIGHT);
                addRowCell.accept(String.format("%,.0f", row.getRevenueAccessory()), com.itextpdf.layout.properties.TextAlignment.RIGHT);
                addRowCell.accept(String.format("%,.0f", row.getRevenuePaint()), com.itextpdf.layout.properties.TextAlignment.RIGHT);
                addRowCell.accept(String.format("%,.0f", row.getTotalRevenue()), com.itextpdf.layout.properties.TextAlignment.RIGHT);
                addRowCell.accept(String.format("%,.0f", row.getProfitCare()), com.itextpdf.layout.properties.TextAlignment.RIGHT);
                addRowCell.accept(String.format("%,.0f", row.getProfitAccessory()), com.itextpdf.layout.properties.TextAlignment.RIGHT);
                addRowCell.accept(String.format("%,.0f", row.getProfitPaint()), com.itextpdf.layout.properties.TextAlignment.RIGHT);
                addRowCell.accept(String.format("%,.0f", row.getVariableCost()), com.itextpdf.layout.properties.TextAlignment.RIGHT);
                addRowCell.accept(String.format("%,.0f", row.getFixedCost()), com.itextpdf.layout.properties.TextAlignment.RIGHT);
                addRowCell.accept(String.format("%,.0f", row.getTotalNetProfit()), com.itextpdf.layout.properties.TextAlignment.RIGHT);
            }

            document.add(table);
            document.close();

            javafx.scene.control.Alert alert = util.AlertHelper.createAlert(
                    javafx.scene.control.Alert.AlertType.INFORMATION,
                    "Thành công",
                    "Xuất báo cáo PDF thống kê năm thành công!\nĐã lưu tại: " + file.getAbsolutePath());
            alert.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            javafx.scene.control.Alert alert = util.AlertHelper.createAlert(
                    javafx.scene.control.Alert.AlertType.ERROR,
                    "Lỗi",
                    "Không thể xuất báo cáo PDF: " + e.getMessage());
            alert.showAndWait();
        }
    }
}

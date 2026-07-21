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
            double costWash = 0;
            double costCare = 0;
            double costAccessory = 0;
            double costPaint = 0;

            for (model.InvoiceItem item : items) {
                String cat = item.getCategory() != null ? item.getCategory().toLowerCase().trim() : "";
                double price = item.getTotalPrice();
                double cost = item.getCostPrice() * item.getQuantity();

                if (cat.contains("rửa") || cat.contains("rua")) {
                    revWash += price;
                    costWash += cost;
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
                        costWash += cost;
                    }
                }
            }

            double profitWash = revWash - costWash;
            double profitCare = revCare - costCare;
            double profitAccessory = revAccessory - costAccessory;
            double profitPaint = revPaint - costPaint;
            String pMethod = inv.getPaymentMethod();
            if (pMethod == null || pMethod.trim().isEmpty()) {
                pMethod = "TM";
            }

            double vat = 0;
            if ("CK".equalsIgnoreCase(pMethod)) {
                vat = (revWash + revCare + revAccessory + revPaint) * 0.08;
            }

            double displayTotal = (revWash + revCare + revAccessory + revPaint);
            if ("CK".equalsIgnoreCase(pMethod)) {
                displayTotal += vat;
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
                    displayTotal,
                    vat,
                    pMethod,
                    costWash,
                    costCare,
                    costAccessory,
                    costPaint,
                    profitWash,
                    profitCare,
                    profitAccessory,
                    profitPaint,
                    inv.getNotes() != null ? inv.getNotes() : "");
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
        if (file == null)
            return;

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
            yellowHeaderStyle.setFillForegroundColor(new org.apache.poi.xssf.usermodel.XSSFColor(
                    new byte[] { (byte) 255, (byte) 235, (byte) 59 }, null));
            yellowHeaderStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);

            org.apache.poi.xssf.usermodel.XSSFCellStyle greenHeaderStyle = workbook.createCellStyle();
            greenHeaderStyle.cloneStyleFrom(headerStyle);
            greenHeaderStyle.setFillForegroundColor(new org.apache.poi.xssf.usermodel.XSSFColor(
                    new byte[] { (byte) 200, (byte) 230, (byte) 201 }, null));
            greenHeaderStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);

            org.apache.poi.xssf.usermodel.XSSFCellStyle normalHeaderStyle = workbook.createCellStyle();
            normalHeaderStyle.cloneStyleFrom(headerStyle);
            normalHeaderStyle.setFillForegroundColor(new org.apache.poi.xssf.usermodel.XSSFColor(
                    new byte[] { (byte) 245, (byte) 245, (byte) 245 }, null));
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
                    "TỔNG DT THỰC NHẬN", "VAT", "Phương thức thanh toán",
                    "CHI PHÍ", "", "", "",
                    "LỢI NHUẬN", "", "", "",
                    "GHI CHÚ"
            };

            String[] r1Headers = {
                    "", "", "", "",
                    "Rửa xe", "Chăm sóc", "Phụ kiện", "Sơn",
                    "", "", "",
                    "Chi phí rửa xe", "Chi phí chăm sóc", "Chi phí phụ kiện", "Chi phí sơn",
                    "Lợi nhuận rửa xe", "Lợi nhuận chăm sóc", "Lợi nhuận phụ kiện", "Lợi nhuận sơn",
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
                } else if ((i >= 11 && i <= 14) || (i >= 15 && i <= 18)) {
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
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 1, 10, 10));
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 11, 14));
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 15, 18));
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 1, 19, 19));

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
                r.createCell(9).setCellValue(data.getVat());
                r.createCell(10).setCellValue(data.getPaymentMethod());
                r.createCell(11).setCellValue(data.getCostWash());
                r.createCell(12).setCellValue(data.getCostCare());
                r.createCell(13).setCellValue(data.getCostAccessory());
                r.createCell(14).setCellValue(data.getCostPaint());
                r.createCell(15).setCellValue(data.getProfitWash());
                r.createCell(16).setCellValue(data.getProfitCare());
                r.createCell(17).setCellValue(data.getProfitAccessory());
                r.createCell(18).setCellValue(data.getProfitPaint());
                r.createCell(19).setCellValue(data.getNotes());

                for (int col = 0; col <= 19; col++) {
                    org.apache.poi.xssf.usermodel.XSSFCell c = r.getCell(col);
                    if (c == null)
                        c = r.createCell(col);
                    if (col == 10 || col == 1 || col == 2 || col == 3 || col == 19) {
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

            for (int col = 0; col <= 19; col++) {
                org.apache.poi.xssf.usermodel.XSSFCell c = totalRow.getCell(col);
                if (c == null)
                    c = totalRow.createCell(col);
                c.setCellStyle(totalStyle);

                if (col == 4 || col == 5 || col == 6 || col == 7 || col == 8 || col == 9 || (col >= 11 && col <= 18)) {
                    char colLetter = (char) ('A' + col);
                    c.setCellFormula("SUM(" + colLetter + "3:" + colLetter + (rowIndex) + ")");
                    c.setCellStyle(totalNumStyle);
                }
            }

            for (int i = 0; i <= 19; i++) {
                sheet.autoSizeColumn(i);
            }

            try (java.io.FileOutputStream fileOut = new java.io.FileOutputStream(file)) {
                workbook.write(fileOut);
            }

            javafx.scene.control.Alert succ = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.INFORMATION);
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
            document.add(new com.itextpdf.layout.element.Paragraph(
                    "Ngã tư An Dương Vương và Trương Định, P. Trần Phú, TP. Quảng Ngãi, T. Quảng Ngãi.")
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
                    dateRangeText = "Thời gian: "
                            + fromDate.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " đến "
                            + toDate.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                }
            } else {
                dateRangeText = "Tất cả";
            }
            document.add(new com.itextpdf.layout.element.Paragraph(dateRangeText)
                    .setFont(boldFont)
                    .setFontSize(11)
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                    .setMarginBottom(10));

            document.add(new com.itextpdf.layout.element.Paragraph("Ngày xuất: "
                    + java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                    .setFont(font)
                    .setFontSize(9)
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT)
                    .setMarginBottom(5));

            float[] columnWidths = { 15f, 35f, 35f, 70f, 35f, 38f, 38f, 35f, 40f, 35f, 20f, 35f, 38f, 38f, 35f, 35f,
                    38f, 38f, 35f, 50f };
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
                    .add(new com.itextpdf.layout.element.Paragraph("VAT").setFont(boldFont).setFontSize(8))
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

            table.addHeaderCell(new com.itextpdf.layout.element.Cell(1, 4)
                    .add(new com.itextpdf.layout.element.Paragraph("CHI PHÍ").setFont(boldFont).setFontSize(8))
                    .setBackgroundColor(headerBg)
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                    .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE)
                    .setPadding(3));

            table.addHeaderCell(new com.itextpdf.layout.element.Cell(1, 4)
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
                    "Rửa xe", "Chăm sóc", "Phụ kiện", "Sơn xe",
                    "Rửa xe", "Chăm sóc", "Phụ kiện", "Sơn xe"
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
            double sumVat = 0;
            double sumCostWash = 0, sumCostCare = 0, sumCostAccessory = 0, sumCostPaint = 0;
            double sumProfitWash = 0, sumProfitCare = 0, sumProfitAccessory = 0, sumProfitPaint = 0;

            java.util.function.BiConsumer<String, com.itextpdf.layout.properties.TextAlignment> addBodyCell = (text,
                    align) -> {
                com.itextpdf.layout.element.Cell cell = new com.itextpdf.layout.element.Cell()
                        .add(new com.itextpdf.layout.element.Paragraph(text != null ? text : "").setFont(font)
                                .setFontSize(8))
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

                addBodyCell.accept(String.format("%,.0f", row.getRevenueWash()),
                        com.itextpdf.layout.properties.TextAlignment.RIGHT);
                addBodyCell.accept(String.format("%,.0f", row.getRevenueCare()),
                        com.itextpdf.layout.properties.TextAlignment.RIGHT);
                addBodyCell.accept(String.format("%,.0f", row.getRevenueAccessory()),
                        com.itextpdf.layout.properties.TextAlignment.RIGHT);
                addBodyCell.accept(String.format("%,.0f", row.getRevenuePaint()),
                        com.itextpdf.layout.properties.TextAlignment.RIGHT);
                addBodyCell.accept(String.format("%,.0f", row.getTotalRevenue()),
                        com.itextpdf.layout.properties.TextAlignment.RIGHT);
                addBodyCell.accept(String.format("%,.0f", row.getVat()),
                        com.itextpdf.layout.properties.TextAlignment.RIGHT);

                addBodyCell.accept(row.getPaymentMethod(), com.itextpdf.layout.properties.TextAlignment.CENTER);

                addBodyCell.accept(String.format("%,.0f", row.getCostWash()),
                        com.itextpdf.layout.properties.TextAlignment.RIGHT);
                addBodyCell.accept(String.format("%,.0f", row.getCostCare()),
                        com.itextpdf.layout.properties.TextAlignment.RIGHT);
                addBodyCell.accept(String.format("%,.0f", row.getCostAccessory()),
                        com.itextpdf.layout.properties.TextAlignment.RIGHT);
                addBodyCell.accept(String.format("%,.0f", row.getCostPaint()),
                        com.itextpdf.layout.properties.TextAlignment.RIGHT);

                addBodyCell.accept(String.format("%,.0f", row.getProfitWash()),
                        com.itextpdf.layout.properties.TextAlignment.RIGHT);
                addBodyCell.accept(String.format("%,.0f", row.getProfitCare()),
                        com.itextpdf.layout.properties.TextAlignment.RIGHT);
                addBodyCell.accept(String.format("%,.0f", row.getProfitAccessory()),
                        com.itextpdf.layout.properties.TextAlignment.RIGHT);
                addBodyCell.accept(String.format("%,.0f", row.getProfitPaint()),
                        com.itextpdf.layout.properties.TextAlignment.RIGHT);

                addBodyCell.accept(row.getNotes(), com.itextpdf.layout.properties.TextAlignment.LEFT);

                sumWash += row.getRevenueWash();
                sumCare += row.getRevenueCare();
                sumAccessory += row.getRevenueAccessory();
                sumPaint += row.getRevenuePaint();
                sumTotal += row.getTotalRevenue();
                sumVat += row.getVat();
                sumCostWash += row.getCostWash();
                sumCostCare += row.getCostCare();
                sumCostAccessory += row.getCostAccessory();
                sumCostPaint += row.getCostPaint();
                sumProfitWash += row.getProfitWash();
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
                        .add(new com.itextpdf.layout.element.Paragraph(String.format("%,.0f", val)).setFont(boldFont)
                                .setFontSize(8))
                        .setPadding(3)
                        .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT)
                        .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE));
            };

            addTotalCell.accept(sumWash);
            addTotalCell.accept(sumCare);
            addTotalCell.accept(sumAccessory);
            addTotalCell.accept(sumPaint);
            addTotalCell.accept(sumTotal);
            addTotalCell.accept(sumVat);

            table.addCell(new com.itextpdf.layout.element.Cell()
                    .add(new com.itextpdf.layout.element.Paragraph("").setFont(boldFont).setFontSize(8))
                    .setPadding(3));

            addTotalCell.accept(sumCostWash);
            addTotalCell.accept(sumCostCare);
            addTotalCell.accept(sumCostAccessory);
            addTotalCell.accept(sumCostPaint);
            addTotalCell.accept(sumProfitWash);
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
        if (file == null)
            return;

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
            yellowHeaderStyle.setFillForegroundColor(new org.apache.poi.xssf.usermodel.XSSFColor(
                    new byte[] { (byte) 255, (byte) 235, (byte) 59 }, null));
            yellowHeaderStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);

            org.apache.poi.xssf.usermodel.XSSFCellStyle greenHeaderStyle = workbook.createCellStyle();
            greenHeaderStyle.cloneStyleFrom(headerStyle);
            greenHeaderStyle.setFillForegroundColor(new org.apache.poi.xssf.usermodel.XSSFColor(
                    new byte[] { (byte) 200, (byte) 230, (byte) 201 }, null));
            greenHeaderStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);

            org.apache.poi.xssf.usermodel.XSSFCellStyle normalHeaderStyle = workbook.createCellStyle();
            normalHeaderStyle.cloneStyleFrom(headerStyle);
            normalHeaderStyle.setFillForegroundColor(new org.apache.poi.xssf.usermodel.XSSFColor(
                    new byte[] { (byte) 245, (byte) 245, (byte) 245 }, null));
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
                    "STT", "Doanh thu rửa xe", "Doanh thu chăm sóc", "Doanh thu phụ kiện", "Doanh thu sơn xe",
                    "Tổng doanh thu",
                    "Tổng VAT", "Lợi nhuận rửa xe", "Lợi nhuận chăm sóc", "Lợi nhuận phụ kiện", "Lợi nhuận sơn",
                    "Chi phí biến thiên", "Chi Phí Cố định", "TỔNG LỢI NHUẬN"
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
                r.createCell(6).setCellValue(data.getVat());
                r.createCell(7).setCellValue(data.getProfitWash());
                r.createCell(8).setCellValue(data.getProfitCare());
                r.createCell(9).setCellValue(data.getProfitAccessory());
                r.createCell(10).setCellValue(data.getProfitPaint());
                r.createCell(11).setCellValue(data.getVariableCost());
                r.createCell(12).setCellValue(data.getFixedCost());
                r.createCell(13).setCellValue(data.getTotalNetProfit());

                for (int col = 0; col <= 13; col++) {
                    org.apache.poi.xssf.usermodel.XSSFCell c = r.getCell(col);
                    if (c == null)
                        c = r.createCell(col);
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

            for (int col = 0; col <= 13; col++) {
                org.apache.poi.xssf.usermodel.XSSFCell c = totalRow.getCell(col);
                if (c == null)
                    c = totalRow.createCell(col);
                c.setCellStyle(totalStyle);

                if (col >= 1 && col <= 13) {
                    char colLetter = (char) ('A' + col);
                    c.setCellFormula("SUM(" + colLetter + "5:" + colLetter + (rowIndex) + ")");
                    c.setCellStyle(totalNumStyle);
                }
            }

            for (int i = 0; i <= 13; i++) {
                sheet.autoSizeColumn(i);
            }

            try (java.io.FileOutputStream fileOut = new java.io.FileOutputStream(file)) {
                workbook.write(fileOut);
            }

            javafx.scene.control.Alert succ = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.INFORMATION);
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
            if (file == null)
                return;

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
            document.add(new com.itextpdf.layout.element.Paragraph(
                    "Ngã tư An Dương Vương và Trương Định, P. Trần Phú, TP. Quảng Ngãi, T. Quảng Ngãi.")
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

            document.add(new com.itextpdf.layout.element.Paragraph("Ngày xuất: "
                    + java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                    .setFont(font)
                    .setFontSize(9)
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT)
                    .setMarginBottom(5));

            float[] columnWidths = { 45f, 65f, 65f, 65f, 65f, 75f, 65f, 65f, 65f, 65f, 65f, 65f, 75f };
            com.itextpdf.layout.element.Table table = new com.itextpdf.layout.element.Table(columnWidths);
            table.setWidth(com.itextpdf.layout.properties.UnitValue.createPercentValue(100));

            com.itextpdf.kernel.colors.DeviceRgb headerBg = new com.itextpdf.kernel.colors.DeviceRgb(245, 245, 245);

            java.util.function.Function<String, com.itextpdf.layout.element.Cell> makeHeaderCell = text -> new com.itextpdf.layout.element.Cell()
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
            table.addHeaderCell(makeHeaderCell.apply("LN Rửa Xe"));
            table.addHeaderCell(makeHeaderCell.apply("LN Chăm Sóc"));
            table.addHeaderCell(makeHeaderCell.apply("LN Phụ Kiện"));
            table.addHeaderCell(makeHeaderCell.apply("LN Sơn"));
            table.addHeaderCell(makeHeaderCell.apply("CP Biến Thiên"));
            table.addHeaderCell(makeHeaderCell.apply("CP Cố Định"));
            table.addHeaderCell(makeHeaderCell.apply("LN RÒNG"));

            for (model.YearlyReportRow row : rows) {
                boolean isTotal = "TỔNG CỘNG".equals(row.getMonth());
                com.itextpdf.kernel.font.PdfFont activeFont = isTotal ? boldFont : font;

                java.util.function.BiConsumer<String, com.itextpdf.layout.properties.TextAlignment> addRowCell = (text,
                        align) -> {
                    com.itextpdf.layout.element.Cell cell = new com.itextpdf.layout.element.Cell()
                            .add(new com.itextpdf.layout.element.Paragraph(text != null ? text : "").setFont(activeFont)
                                    .setFontSize(8))
                            .setPadding(3)
                            .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
                    if (align != null) {
                        cell.setTextAlignment(align);
                    }
                    table.addCell(cell);
                };

                addRowCell.accept(row.getMonth(), com.itextpdf.layout.properties.TextAlignment.CENTER);
                addRowCell.accept(String.format("%,.0f", row.getRevenueWash()),
                        com.itextpdf.layout.properties.TextAlignment.RIGHT);
                addRowCell.accept(String.format("%,.0f", row.getRevenueCare()),
                        com.itextpdf.layout.properties.TextAlignment.RIGHT);
                addRowCell.accept(String.format("%,.0f", row.getRevenueAccessory()),
                        com.itextpdf.layout.properties.TextAlignment.RIGHT);
                addRowCell.accept(String.format("%,.0f", row.getRevenuePaint()),
                        com.itextpdf.layout.properties.TextAlignment.RIGHT);
                addRowCell.accept(String.format("%,.0f", row.getTotalRevenue()),
                        com.itextpdf.layout.properties.TextAlignment.RIGHT);
                addRowCell.accept(String.format("%,.0f", row.getProfitWash()),
                        com.itextpdf.layout.properties.TextAlignment.RIGHT);
                addRowCell.accept(String.format("%,.0f", row.getProfitCare()),
                        com.itextpdf.layout.properties.TextAlignment.RIGHT);
                addRowCell.accept(String.format("%,.0f", row.getProfitAccessory()),
                        com.itextpdf.layout.properties.TextAlignment.RIGHT);
                addRowCell.accept(String.format("%,.0f", row.getProfitPaint()),
                        com.itextpdf.layout.properties.TextAlignment.RIGHT);
                addRowCell.accept(String.format("%,.0f", row.getVariableCost()),
                        com.itextpdf.layout.properties.TextAlignment.RIGHT);
                addRowCell.accept(String.format("%,.0f", row.getFixedCost()),
                        com.itextpdf.layout.properties.TextAlignment.RIGHT);
                addRowCell.accept(String.format("%,.0f", row.getTotalNetProfit()),
                        com.itextpdf.layout.properties.TextAlignment.RIGHT);
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

    public static void exportDebtToExcel(List<Invoice> rows,
            int targetMonth,
            int targetYear,
            javafx.stage.Window owner) {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Lưu Báo Cáo Công Nợ Excel");
        String monthStr = targetMonth > 0 ? "Thang" + targetMonth : "CaNam";
        fileChooser.setInitialFileName("BaoCaoCongNo_" + monthStr + "_" + targetYear + ".xlsx");
        fileChooser.getExtensionFilters().add(
                new javafx.stage.FileChooser.ExtensionFilter("Excel Workbook", "*.xlsx"));

        java.io.File file = fileChooser.showSaveDialog(owner);
        if (file == null)
            return;

        try (org.apache.poi.xssf.usermodel.XSSFWorkbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {
            org.apache.poi.xssf.usermodel.XSSFSheet sheet = workbook.createSheet("Danh sách công nợ");
            sheet.setDisplayGridlines(true);

            // Header Styles
            org.apache.poi.xssf.usermodel.XSSFCellStyle titleStyle = workbook.createCellStyle();
            org.apache.poi.xssf.usermodel.XSSFFont titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 14);
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
            headerStyle.setFillForegroundColor(new org.apache.poi.xssf.usermodel.XSSFColor(
                    new byte[] { (byte) 255, (byte) 205, (byte) 210 }, null)); // Light red background
            headerStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);

            org.apache.poi.xssf.usermodel.XSSFCellStyle borderStyle = workbook.createCellStyle();
            borderStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            borderStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            borderStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            borderStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);

            org.apache.poi.xssf.usermodel.XSSFCellStyle centerStyle = workbook.createCellStyle();
            centerStyle.cloneStyleFrom(borderStyle);
            centerStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);

            org.apache.poi.xssf.usermodel.XSSFCellStyle rightStyle = workbook.createCellStyle();
            rightStyle.cloneStyleFrom(borderStyle);
            rightStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.RIGHT);

            // Title
            org.apache.poi.ss.usermodel.Row titleRow = sheet.createRow(0);
            org.apache.poi.ss.usermodel.Cell titleCell = titleRow.createCell(0);
            String titleText = targetMonth > 0 ? "BÁO CÁO CÔNG NỢ THÁNG " + targetMonth + "/" + targetYear
                    : "BÁO CÁO CÔNG NỢ NĂM " + targetYear;
            titleCell.setCellValue(titleText);
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 8));

            // Header Row
            org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(2);
            String[] headers = { "STT", "Mã HĐ", "Ngày tạo", "Khách hàng", "Số điện thoại", "Biển số",
                    "Tổng tiền nợ (VNĐ)", "PTTT", "Ghi chú" };
            for (int i = 0; i < headers.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Data Rows
            int rowIdx = 3;
            double totalDebt = 0.0;
            int stt = 1;
            for (model.Invoice inv : rows) {
                org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowIdx++);

                org.apache.poi.ss.usermodel.Cell c0 = row.createCell(0);
                c0.setCellValue(stt++);
                c0.setCellStyle(centerStyle);
                org.apache.poi.ss.usermodel.Cell c1 = row.createCell(1);
                c1.setCellValue(String.format("%05d", inv.getId()));
                c1.setCellStyle(centerStyle);
                org.apache.poi.ss.usermodel.Cell c2 = row.createCell(2);
                c2.setCellValue(inv.getCreatedAt() != null ? inv.getCreatedAt() : "");
                c2.setCellStyle(centerStyle);
                org.apache.poi.ss.usermodel.Cell c3 = row.createCell(3);
                c3.setCellValue(inv.getCustomerName());
                c3.setCellStyle(borderStyle);
                org.apache.poi.ss.usermodel.Cell c4 = row.createCell(4);
                c4.setCellValue(inv.getPhone() != null ? inv.getPhone() : "");
                c4.setCellStyle(centerStyle);
                org.apache.poi.ss.usermodel.Cell c5 = row.createCell(5);
                c5.setCellValue(inv.getLicensePlate() != null ? inv.getLicensePlate() : "");
                c5.setCellStyle(centerStyle);
                org.apache.poi.ss.usermodel.Cell c6 = row.createCell(6);
                c6.setCellValue(inv.getTotalAmount());
                c6.setCellStyle(rightStyle);
                org.apache.poi.ss.usermodel.Cell c7 = row.createCell(7);
                c7.setCellValue(inv.getPaymentMethod() != null ? inv.getPaymentMethod() : "");
                c7.setCellStyle(centerStyle);
                org.apache.poi.ss.usermodel.Cell c8 = row.createCell(8);
                c8.setCellValue(inv.getNotes() != null ? inv.getNotes() : "");
                c8.setCellStyle(borderStyle);

                totalDebt += inv.getTotalAmount();
            }

            // Total Row
            org.apache.poi.ss.usermodel.Row totalRow = sheet.createRow(rowIdx);
            org.apache.poi.xssf.usermodel.XSSFCellStyle totalStyle = workbook.createCellStyle();
            org.apache.poi.xssf.usermodel.XSSFFont totalFont = workbook.createFont();
            totalFont.setBold(true);
            totalStyle.setFont(totalFont);
            totalStyle.cloneStyleFrom(rightStyle);

            org.apache.poi.ss.usermodel.Cell tLabel = totalRow.createCell(5);
            tLabel.setCellValue("TỔNG NỢ:");
            org.apache.poi.xssf.usermodel.XSSFCellStyle tLabelStyle = workbook.createCellStyle();
            tLabelStyle.setFont(totalFont);
            tLabelStyle.cloneStyleFrom(borderStyle);
            tLabelStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.RIGHT);
            tLabel.setCellStyle(tLabelStyle);

            org.apache.poi.ss.usermodel.Cell tVal = totalRow.createCell(6);
            tVal.setCellValue(totalDebt);
            tVal.setCellStyle(totalStyle);

            // Borders for empty cells in total row
            for (int i = 0; i < headers.length; i++) {
                if (i != 5 && i != 6) {
                    totalRow.createCell(i).setCellStyle(borderStyle);
                }
            }
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(rowIdx, rowIdx, 0, 4));

            // Auto size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            try (java.io.FileOutputStream fos = new java.io.FileOutputStream(file)) {
                workbook.write(fos);
            }

            javafx.scene.control.Alert alert = util.AlertHelper.createAlert(
                    javafx.scene.control.Alert.AlertType.INFORMATION,
                    "Thành công",
                    "Xuất báo cáo công nợ Excel thành công!\nĐã lưu tại: " + file.getAbsolutePath());
            alert.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            javafx.scene.control.Alert alert = util.AlertHelper.createAlert(
                    javafx.scene.control.Alert.AlertType.ERROR,
                    "Lỗi",
                    "Không thể xuất báo cáo Excel: " + e.getMessage());
            alert.showAndWait();
        }
    }

    public static void exportDebtToPDF(List<Invoice> rows,
            int targetMonth,
            int targetYear,
            javafx.stage.Window owner) {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Lưu Báo Cáo Công Nợ PDF");
        String monthStr = targetMonth > 0 ? "Thang" + targetMonth : "CaNam";
        fileChooser.setInitialFileName("BaoCaoCongNo_" + monthStr + "_" + targetYear + ".pdf");
        fileChooser.getExtensionFilters().add(
                new javafx.stage.FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

        java.io.File file = fileChooser.showSaveDialog(owner);
        if (file == null)
            return;

        try {
            com.itextpdf.kernel.pdf.PdfWriter writer = new com.itextpdf.kernel.pdf.PdfWriter(file);
            com.itextpdf.kernel.pdf.PdfDocument pdf = new com.itextpdf.kernel.pdf.PdfDocument(writer);
            com.itextpdf.layout.Document document = new com.itextpdf.layout.Document(pdf);

            com.itextpdf.kernel.font.PdfFont font = util.PDFFontHelper.createVietnameseFont();
            com.itextpdf.kernel.font.PdfFont boldFont = util.PDFFontHelper.createVietnameseFont(true);

            // Header company info
            document.add(new com.itextpdf.layout.element.Paragraph("CÔNG TY TNHH TM DV PHỤ TÙNG Ô TÔ MINH TÂM")
                    .setFont(boldFont).setFontSize(13)
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));
            document.add(new com.itextpdf.layout.element.Paragraph(
                    "Ngã tư Trương Định và An Dương Vương, P. Nghĩa Lộ, tỉnh Quảng Ngãi")
                    .setFont(font).setFontSize(9)
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));

            String titleText = targetMonth > 0 ? "BÁO CÁO CHI TIẾT CÔNG NỢ THÁNG " + targetMonth + "/" + targetYear
                    : "BÁO CÁO CHI TIẾT CÔNG NỢ NĂM " + targetYear;
            document.add(new com.itextpdf.layout.element.Paragraph(titleText)
                    .setFont(boldFont).setFontSize(15)
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                    .setMarginTop(12));
            document.add(new com.itextpdf.layout.element.Paragraph("\n"));

            // Table definition: STT, Mã HĐ, Ngày tạo, Khách hàng, SĐT, Biển số, Tiền nợ,
            // PTTT, Ghi chú
            float[] colWidths = { 30, 45, 65, 95, 70, 60, 75, 40, 80 };
            com.itextpdf.layout.element.Table table = new com.itextpdf.layout.element.Table(colWidths);
            table.setWidth(com.itextpdf.layout.properties.UnitValue.createPercentValue(100));

            java.util.function.Function<String, com.itextpdf.layout.element.Cell> makeHeaderCell = text -> {
                return new com.itextpdf.layout.element.Cell()
                        .add(new com.itextpdf.layout.element.Paragraph(text).setFont(boldFont).setFontSize(9))
                        .setBackgroundColor(new com.itextpdf.kernel.colors.DeviceRgb(255, 205, 210)) // light red
                        .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                        .setPadding(4)
                        .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
            };

            table.addHeaderCell(makeHeaderCell.apply("STT"));
            table.addHeaderCell(makeHeaderCell.apply("Mã HĐ"));
            table.addHeaderCell(makeHeaderCell.apply("Ngày tạo"));
            table.addHeaderCell(makeHeaderCell.apply("Khách hàng"));
            table.addHeaderCell(makeHeaderCell.apply("SĐT"));
            table.addHeaderCell(makeHeaderCell.apply("Biển số"));
            table.addHeaderCell(makeHeaderCell.apply("Tiền nợ (đ)"));
            table.addHeaderCell(makeHeaderCell.apply("PTTT"));
            table.addHeaderCell(makeHeaderCell.apply("Ghi chú"));

            int stt = 1;
            double totalDebt = 0.0;
            for (model.Invoice inv : rows) {
                java.util.function.BiConsumer<String, com.itextpdf.layout.properties.TextAlignment> addRowCell = (text,
                        align) -> {
                    com.itextpdf.layout.element.Cell cell = new com.itextpdf.layout.element.Cell()
                            .add(new com.itextpdf.layout.element.Paragraph(text != null ? text : "").setFont(font)
                                    .setFontSize(8))
                            .setPadding(4)
                            .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
                    if (align != null) {
                        cell.setTextAlignment(align);
                    }
                    table.addCell(cell);
                };

                addRowCell.accept(String.valueOf(stt++), com.itextpdf.layout.properties.TextAlignment.CENTER);
                addRowCell.accept(String.format("%05d", inv.getId()),
                        com.itextpdf.layout.properties.TextAlignment.CENTER);
                addRowCell.accept(inv.getCreatedAt() != null ? inv.getCreatedAt().substring(0, 16) : "",
                        com.itextpdf.layout.properties.TextAlignment.CENTER);
                addRowCell.accept(inv.getCustomerName(), com.itextpdf.layout.properties.TextAlignment.LEFT);
                addRowCell.accept(inv.getPhone() != null ? inv.getPhone() : "",
                        com.itextpdf.layout.properties.TextAlignment.CENTER);
                addRowCell.accept(inv.getLicensePlate() != null ? inv.getLicensePlate() : "",
                        com.itextpdf.layout.properties.TextAlignment.CENTER);
                addRowCell.accept(String.format("%,.0f", inv.getTotalAmount()),
                        com.itextpdf.layout.properties.TextAlignment.RIGHT);
                addRowCell.accept(inv.getPaymentMethod() != null ? inv.getPaymentMethod() : "",
                        com.itextpdf.layout.properties.TextAlignment.CENTER);
                addRowCell.accept(inv.getNotes() != null ? inv.getNotes() : "",
                        com.itextpdf.layout.properties.TextAlignment.LEFT);

                totalDebt += inv.getTotalAmount();
            }

            // Add total cell row
            com.itextpdf.layout.element.Cell totalLabelCell = new com.itextpdf.layout.element.Cell(1, 6)
                    .add(new com.itextpdf.layout.element.Paragraph("TỔNG CỘNG TIỀN NỢ:").setFont(boldFont)
                            .setFontSize(9))
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT)
                    .setPadding(4);
            table.addCell(totalLabelCell);

            com.itextpdf.layout.element.Cell totalValueCell = new com.itextpdf.layout.element.Cell(1, 3)
                    .add(new com.itextpdf.layout.element.Paragraph(String.format("%,.0f đ", totalDebt))
                            .setFont(boldFont).setFontSize(9))
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.LEFT)
                    .setPadding(4);
            table.addCell(totalValueCell);

            document.add(table);
            document.close();

            javafx.scene.control.Alert alert = util.AlertHelper.createAlert(
                    javafx.scene.control.Alert.AlertType.INFORMATION,
                    "Thành công",
                    "Xuất báo cáo công nợ PDF thành công!\nĐã lưu tại: " + file.getAbsolutePath());
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

    public static void exportInventoryToPDF(List<model.InventoryReceipt> receipts,
            String monthYear,
            javafx.stage.Window owner) {
        try {
            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Lưu Báo Cáo Nhập Kho PDF");
            fileChooser.setInitialFileName("BaoCaoNhapKho_" + monthYear.replace("-", "_") + "_" + java.time.LocalDate.now() + ".pdf");
            fileChooser.getExtensionFilters().add(
                    new javafx.stage.FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

            java.io.File file = fileChooser.showSaveDialog(owner);
            if (file == null) return;

            com.itextpdf.kernel.pdf.PdfWriter writer = new com.itextpdf.kernel.pdf.PdfWriter(file);
            com.itextpdf.kernel.pdf.PdfDocument pdf = new com.itextpdf.kernel.pdf.PdfDocument(writer);
            com.itextpdf.layout.Document document = new com.itextpdf.layout.Document(pdf);
            document.setMargins(20, 20, 20, 20);

            com.itextpdf.kernel.font.PdfFont font = util.PDFFontHelper.createVietnameseFont();
            com.itextpdf.kernel.font.PdfFont boldFont = util.PDFFontHelper.createVietnameseFont(true);

            // Left-aligned Company Info (matching the design in other PDFs)
            document.add(new com.itextpdf.layout.element.Paragraph("CÔNG TY TNHH TM DV PHỤ TÙNG Ô TÔ MINH TÂM")
                    .setFont(boldFont)
                    .setFontSize(10)
                    .setMarginBottom(1));
            document.add(new com.itextpdf.layout.element.Paragraph(
                    "Ngã tư An Dương Vương và Trương Định, P. Trần Phú, TP. Quảng Ngãi, T. Quảng Ngãi.")
                    .setFont(font)
                    .setFontSize(8.5f)
                    .setMarginBottom(1));
            document.add(new com.itextpdf.layout.element.Paragraph("MST: 4300899201")
                    .setFont(font)
                    .setFontSize(8.5f)
                    .setMarginBottom(15));

            // Title
            document.add(new com.itextpdf.layout.element.Paragraph("BÁO CÁO NHẬP KHO THEO THÁNG")
                    .setFont(boldFont).setFontSize(16)
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                    .setMarginBottom(2));
            
            String monthVal = monthYear.substring(5, 7);
            String yearVal = monthYear.substring(0, 4);
            document.add(new com.itextpdf.layout.element.Paragraph("Tháng " + monthVal + " Năm " + yearVal)
                    .setFont(boldFont).setFontSize(12)
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                    .setMarginBottom(15));

            // Metadata
            String reportTime = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
                    .format(java.time.LocalDateTime.now());
            document.add(new com.itextpdf.layout.element.Paragraph("Thời gian lập báo cáo: " + reportTime)
                    .setFont(font).setFontSize(9).setMarginBottom(2));
            document.add(new com.itextpdf.layout.element.Paragraph("Người lập báo cáo: Ban quản trị hệ thống (Admin)")
                    .setFont(font).setFontSize(9).setMarginBottom(15));

            // Table setup: 8 columns
            float[] colWidths = {30f, 60f, 70f, 130f, 50f, 70f, 80f, 80f};
            com.itextpdf.layout.element.Table table = new com.itextpdf.layout.element.Table(colWidths);
            table.setWidth(com.itextpdf.layout.properties.UnitValue.createPercentValue(100));

            // Header cells helper
            java.util.function.BiConsumer<String, com.itextpdf.layout.properties.TextAlignment> addHeaderCell = (text, align) -> {
                com.itextpdf.layout.element.Cell cell = new com.itextpdf.layout.element.Cell()
                        .add(new com.itextpdf.layout.element.Paragraph(text).setFont(boldFont).setFontSize(9))
                        .setTextAlignment(align)
                        .setBackgroundColor(new com.itextpdf.kernel.colors.DeviceRgb(200, 230, 201))
                        .setPadding(5);
                table.addHeaderCell(cell);
            };

            addHeaderCell.accept("STT", com.itextpdf.layout.properties.TextAlignment.CENTER);
            addHeaderCell.accept("Mã Phiếu", com.itextpdf.layout.properties.TextAlignment.CENTER);
            addHeaderCell.accept("Ngày Nhập", com.itextpdf.layout.properties.TextAlignment.CENTER);
            addHeaderCell.accept("Tên Sản Phẩm", com.itextpdf.layout.properties.TextAlignment.LEFT);
            addHeaderCell.accept("SL Nhập", com.itextpdf.layout.properties.TextAlignment.RIGHT);
            addHeaderCell.accept("Giá Nhập", com.itextpdf.layout.properties.TextAlignment.RIGHT);
            addHeaderCell.accept("Thành Tiền", com.itextpdf.layout.properties.TextAlignment.RIGHT);
            addHeaderCell.accept("Người Thực Hiện", com.itextpdf.layout.properties.TextAlignment.LEFT);

            double totalAmount = 0;
            int stt = 1;

            java.util.function.BiConsumer<String, com.itextpdf.layout.properties.TextAlignment> addRowCell = (text, align) -> {
                com.itextpdf.layout.element.Cell cell = new com.itextpdf.layout.element.Cell()
                        .add(new com.itextpdf.layout.element.Paragraph(text).setFont(font).setFontSize(8.5f))
                        .setTextAlignment(align)
                        .setPadding(4);
                table.addCell(cell);
            };

            for (model.InventoryReceipt r : receipts) {
                addRowCell.accept(String.valueOf(stt++), com.itextpdf.layout.properties.TextAlignment.CENTER);
                addRowCell.accept("NK-" + String.format("%04d", r.getId()), com.itextpdf.layout.properties.TextAlignment.CENTER);
                addRowCell.accept(r.getReceiptDate(), com.itextpdf.layout.properties.TextAlignment.CENTER);
                addRowCell.accept(r.getProductName(), com.itextpdf.layout.properties.TextAlignment.LEFT);
                addRowCell.accept(new java.text.DecimalFormat("#.##").format(r.getQuantity()), com.itextpdf.layout.properties.TextAlignment.RIGHT);
                addRowCell.accept(String.format("%,.0f đ", r.getCostPrice()), com.itextpdf.layout.properties.TextAlignment.RIGHT);
                addRowCell.accept(String.format("%,.0f đ", r.getTotalPrice()), com.itextpdf.layout.properties.TextAlignment.RIGHT);
                addRowCell.accept(r.getOperator(), com.itextpdf.layout.properties.TextAlignment.LEFT);

                totalAmount += r.getTotalPrice();
            }

            // Total row
            com.itextpdf.layout.element.Cell totalLabelCell = new com.itextpdf.layout.element.Cell(1, 6)
                    .add(new com.itextpdf.layout.element.Paragraph("TỔNG CỘNG GIÁ TRỊ NHẬP KHO THÁNG:").setFont(boldFont).setFontSize(9))
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT)
                    .setPadding(5);
            table.addCell(totalLabelCell);

            com.itextpdf.layout.element.Cell totalValueCell = new com.itextpdf.layout.element.Cell(1, 2)
                    .add(new com.itextpdf.layout.element.Paragraph(String.format("%,.0f đ", totalAmount)).setFont(boldFont).setFontSize(9))
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT)
                    .setPadding(5);
            table.addCell(totalValueCell);

            document.add(table);
            
            // Signature section
            document.add(new com.itextpdf.layout.element.Paragraph("\n\n"));
            com.itextpdf.layout.element.Table sigTable = new com.itextpdf.layout.element.Table(2);
            sigTable.setWidth(com.itextpdf.layout.properties.UnitValue.createPercentValue(100));
            
            com.itextpdf.layout.element.Cell sigCell1 = new com.itextpdf.layout.element.Cell()
                    .add(new com.itextpdf.layout.element.Paragraph("Người lập báo cáo\n(Ký và ghi rõ họ tên)").setFont(boldFont).setFontSize(9))
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                    .setBorder(null);
            com.itextpdf.layout.element.Cell sigCell2 = new com.itextpdf.layout.element.Cell()
                    .add(new com.itextpdf.layout.element.Paragraph("Chủ doanh nghiệp\n(Ký và đóng dấu)").setFont(boldFont).setFontSize(9))
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                    .setBorder(null);
            
            sigTable.addCell(sigCell1);
            sigTable.addCell(sigCell2);
            document.add(sigTable);

            document.close();

            javafx.scene.control.Alert alert = util.AlertHelper.createAlert(
                    javafx.scene.control.Alert.AlertType.INFORMATION,
                    "Thành công",
                    "Xuất báo cáo nhập kho PDF thành công!\nĐã lưu tại: " + file.getAbsolutePath());
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

    public static void exportInventoryToExcel(List<model.InventoryReceipt> receipts,
            String monthYear,
            javafx.stage.Window owner) {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Lưu Báo Cáo Nhập Kho Excel");
        fileChooser.setInitialFileName("BaoCaoNhapKho_" + monthYear.replace("-", "_") + "_" + java.time.LocalDate.now() + ".xlsx");
        fileChooser.getExtensionFilters().add(
                new javafx.stage.FileChooser.ExtensionFilter("Excel Workbook", "*.xlsx"));

        java.io.File file = fileChooser.showSaveDialog(owner);
        if (file == null) return;

        try (org.apache.poi.xssf.usermodel.XSSFWorkbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {
            org.apache.poi.xssf.usermodel.XSSFSheet sheet = workbook.createSheet("Báo cáo nhập kho");
            sheet.setDisplayGridlines(true);

            // Title Style
            org.apache.poi.xssf.usermodel.XSSFCellStyle titleStyle = workbook.createCellStyle();
            org.apache.poi.xssf.usermodel.XSSFFont titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 16);
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);

            org.apache.poi.xssf.usermodel.XSSFRow titleRow = sheet.createRow(0);
            org.apache.poi.xssf.usermodel.XSSFCell titleCell = titleRow.createCell(0);
            String monthVal = monthYear.substring(5, 7);
            String yearVal = monthYear.substring(0, 4);
            titleCell.setCellValue("BÁO CÁO NHẬP KHO THÁNG " + monthVal + " NĂM " + yearVal);
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 7));

            // Header Style
            org.apache.poi.xssf.usermodel.XSSFCellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.xssf.usermodel.XSSFFont headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
            headerStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            headerStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            headerStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            headerStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            headerStyle.setFillForegroundColor(new org.apache.poi.xssf.usermodel.XSSFColor(new byte[]{(byte)200, (byte)230, (byte)201}, null));
            headerStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);

            // Row style
            org.apache.poi.xssf.usermodel.XSSFCellStyle borderStyle = workbook.createCellStyle();
            borderStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            borderStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            borderStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            borderStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);

            org.apache.poi.xssf.usermodel.XSSFCellStyle numberStyle = workbook.createCellStyle();
            numberStyle.cloneStyleFrom(borderStyle);
            numberStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0"));
            numberStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.RIGHT);

            String[] headers = {
                "STT", "Mã Phiếu", "Ngày Nhập", "Tên Sản Phẩm", "Số Lượng", "Đơn Giá", "Thành Tiền", "Người Thực Hiện"
            };
            
            org.apache.poi.xssf.usermodel.XSSFRow headerRow = sheet.createRow(2);
            for (int i = 0; i < headers.length; i++) {
                org.apache.poi.xssf.usermodel.XSSFCell c = headerRow.createCell(i);
                c.setCellValue(headers[i]);
                c.setCellStyle(headerStyle);
            }

            int rowIndex = 3;
            int stt = 1;
            double totalAmount = 0;

            for (model.InventoryReceipt r : receipts) {
                org.apache.poi.xssf.usermodel.XSSFRow row = sheet.createRow(rowIndex++);
                
                org.apache.poi.xssf.usermodel.XSSFCell cell0 = row.createCell(0);
                cell0.setCellValue(stt++);
                cell0.setCellStyle(borderStyle);
                
                org.apache.poi.xssf.usermodel.XSSFCell cell1 = row.createCell(1);
                cell1.setCellValue("NK-" + String.format("%04d", r.getId()));
                cell1.setCellStyle(borderStyle);
                
                org.apache.poi.xssf.usermodel.XSSFCell cell2 = row.createCell(2);
                cell2.setCellValue(r.getReceiptDate());
                cell2.setCellStyle(borderStyle);
                
                org.apache.poi.xssf.usermodel.XSSFCell cell3 = row.createCell(3);
                cell3.setCellValue(r.getProductName());
                cell3.setCellStyle(borderStyle);
                
                org.apache.poi.xssf.usermodel.XSSFCell cell4 = row.createCell(4);
                cell4.setCellValue(r.getQuantity());
                cell4.setCellStyle(borderStyle);
                
                org.apache.poi.xssf.usermodel.XSSFCell cell5 = row.createCell(5);
                cell5.setCellValue(r.getCostPrice());
                cell5.setCellStyle(numberStyle);
                
                org.apache.poi.xssf.usermodel.XSSFCell cell6 = row.createCell(6);
                cell6.setCellValue(r.getTotalPrice());
                cell6.setCellStyle(numberStyle);
                
                org.apache.poi.xssf.usermodel.XSSFCell cell7 = row.createCell(7);
                cell7.setCellValue(r.getOperator());
                cell7.setCellStyle(borderStyle);
                
                totalAmount += r.getTotalPrice();
            }

            // Total row
            org.apache.poi.xssf.usermodel.XSSFRow totalRow = sheet.createRow(rowIndex);
            org.apache.poi.xssf.usermodel.XSSFCell totalLabelCell = totalRow.createCell(0);
            totalLabelCell.setCellValue("TỔNG CỘNG GIÁ TRỊ NHẬP KHO");
            
            org.apache.poi.xssf.usermodel.XSSFCellStyle totalLabelStyle = workbook.createCellStyle();
            org.apache.poi.xssf.usermodel.XSSFFont boldFont = workbook.createFont();
            boldFont.setBold(true);
            totalLabelStyle.setFont(boldFont);
            totalLabelStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.RIGHT);
            totalLabelStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            totalLabelStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            totalLabelCell.setCellStyle(totalLabelStyle);
            
            // Fill borders for empty cells in total row
            for(int i = 1; i <= 5; i++) {
                org.apache.poi.xssf.usermodel.XSSFCell cell = totalRow.createCell(i);
                cell.setCellStyle(totalLabelStyle);
            }
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(rowIndex, rowIndex, 0, 5));

            org.apache.poi.xssf.usermodel.XSSFCell totalValCell = totalRow.createCell(6);
            totalValCell.setCellValue(totalAmount);
            org.apache.poi.xssf.usermodel.XSSFCellStyle totalValStyle = workbook.createCellStyle();
            totalValStyle.cloneStyleFrom(numberStyle);
            totalValStyle.setFont(boldFont);
            totalValCell.setCellStyle(totalValStyle);
            
            org.apache.poi.xssf.usermodel.XSSFCell cell7Empty = totalRow.createCell(7);
            org.apache.poi.xssf.usermodel.XSSFCellStyle emptyStyle = workbook.createCellStyle();
            emptyStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            emptyStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            cell7Empty.setCellStyle(emptyStyle);

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Save workbook
            try (java.io.FileOutputStream out = new java.io.FileOutputStream(file)) {
                workbook.write(out);
            }

            javafx.scene.control.Alert alert = util.AlertHelper.createAlert(
                    javafx.scene.control.Alert.AlertType.INFORMATION,
                    "Thành công",
                    "Xuất báo cáo nhập kho Excel thành công!\nĐã lưu tại: " + file.getAbsolutePath());
            alert.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            javafx.scene.control.Alert alert = util.AlertHelper.createAlert(
                    javafx.scene.control.Alert.AlertType.ERROR,
                    "Lỗi",
                    "Không thể xuất báo cáo Excel: " + e.getMessage());
            alert.showAndWait();
        }
    }
}

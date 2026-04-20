package ui;

import model.Invoice;
import service.InvoiceService;
import java.util.List;
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
                if (inv.getCreatedAt() == null) return false;
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
                new javafx.stage.FileChooser.ExtensionFilter("PDF Files", "*.pdf")
            );
            
            java.io.File file = fileChooser.showSaveDialog(owner);
            if (file == null) return;
            
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
            document.add(new com.itextpdf.layout.element.Paragraph("BÁO CÁO DOANH THU")
                .setFont(boldFont)
                .setFontSize(24)
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));
            
            document.add(new com.itextpdf.layout.element.Paragraph("MTProAuto - Hệ Thống Quản Lý Gara")
                .setFont(boldFont)
                .setFontSize(12)
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
                    new com.itextpdf.layout.element.Paragraph("#" + inv.getId()).setFont(font)));
                invoiceTable.addCell(new com.itextpdf.layout.element.Cell().add(
                    new com.itextpdf.layout.element.Paragraph(inv.getCustomerName()).setFont(font)));
                invoiceTable.addCell(new com.itextpdf.layout.element.Cell().add(
                    new com.itextpdf.layout.element.Paragraph(String.format("%,.0f", inv.getTotalAmount())).setFont(font)));
                invoiceTable.addCell(new com.itextpdf.layout.element.Cell().add(
                    new com.itextpdf.layout.element.Paragraph(inv.getStatus().equals("paid") ? "Đã TT" : "Chưa TT").setFont(font)));
            }
            
            document.add(invoiceTable);
            
            document.close();
            
            javafx.scene.control.Alert alert = util.AlertHelper.createAlert(
                javafx.scene.control.Alert.AlertType.INFORMATION, 
                "Thành công", 
                "Xuất báo cáo PDF thành công!\nĐã lưu tại: " + file.getAbsolutePath()
            );
            alert.showAndWait();
            
        } catch (Exception e) {
            e.printStackTrace();
            javafx.scene.control.Alert alert = util.AlertHelper.createAlert(
                javafx.scene.control.Alert.AlertType.ERROR, 
                "Lỗi", 
                "Không thể xuất báo cáo: " + e.getMessage()
            );
            alert.showAndWait();
        }
    }
}

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
            
            com.itextpdf.kernel.font.PdfFont font = com.itextpdf.kernel.font.PdfFontFactory.createFont(
                com.itextpdf.io.font.constants.StandardFonts.HELVETICA);
            
            // Title
            document.add(new com.itextpdf.layout.element.Paragraph("BAO CAO DOANH THU")
                .setFont(font)
                .setFontSize(24)
                .setBold()
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));
            
            document.add(new com.itextpdf.layout.element.Paragraph("MTProAuto - He Thong Quan Ly Gara")
                .setFont(font)
                .setFontSize(12)
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));
            
            document.add(new com.itextpdf.layout.element.Paragraph("\n"));
            
            // Date range
            String dateRangeText = "Thoi gian: ";
            if (fromDate != null && toDate != null) {
                dateRangeText += fromDate + " den " + toDate;
            } else {
                dateRangeText += "Tat ca";
            }
            document.add(new com.itextpdf.layout.element.Paragraph(dateRangeText)
                .setFont(font)
                .setFontSize(12));
            
            document.add(new com.itextpdf.layout.element.Paragraph("Ngay xuat: " + java.time.LocalDate.now())
                .setFont(font)
                .setFontSize(12));
            
            document.add(new com.itextpdf.layout.element.Paragraph("\n"));
            
            // Stats table
            document.add(new com.itextpdf.layout.element.Paragraph("TONG QUAN")
                .setFont(font)
                .setFontSize(14)
                .setBold());
            
            com.itextpdf.layout.element.Table statsTable = new com.itextpdf.layout.element.Table(2);
            statsTable.setWidth(com.itextpdf.layout.properties.UnitValue.createPercentValue(100));
            
            statsTable.addCell(new com.itextpdf.layout.element.Cell().add(
                new com.itextpdf.layout.element.Paragraph("Tong doanh thu:").setFont(font)));
            statsTable.addCell(new com.itextpdf.layout.element.Cell().add(
                new com.itextpdf.layout.element.Paragraph(String.format("%,.0f VND", totalRevenue)).setFont(font)));
            
            statsTable.addCell(new com.itextpdf.layout.element.Cell().add(
                new com.itextpdf.layout.element.Paragraph("Tong hoa don:").setFont(font)));
            statsTable.addCell(new com.itextpdf.layout.element.Cell().add(
                new com.itextpdf.layout.element.Paragraph(String.valueOf(totalInvoices)).setFont(font)));
            
            statsTable.addCell(new com.itextpdf.layout.element.Cell().add(
                new com.itextpdf.layout.element.Paragraph("Khach hang:").setFont(font)));
            statsTable.addCell(new com.itextpdf.layout.element.Cell().add(
                new com.itextpdf.layout.element.Paragraph(String.valueOf(uniqueCustomers)).setFont(font)));
            
            statsTable.addCell(new com.itextpdf.layout.element.Cell().add(
                new com.itextpdf.layout.element.Paragraph("Da thanh toan:").setFont(font)));
            statsTable.addCell(new com.itextpdf.layout.element.Cell().add(
                new com.itextpdf.layout.element.Paragraph(String.valueOf(paidInvoices)).setFont(font)));
            
            document.add(statsTable);
            
            document.add(new com.itextpdf.layout.element.Paragraph("\n"));
            
            // Invoice list
            document.add(new com.itextpdf.layout.element.Paragraph("CHI TIET HOA DON")
                .setFont(font)
                .setFontSize(14)
                .setBold());
            
            com.itextpdf.layout.element.Table invoiceTable = new com.itextpdf.layout.element.Table(4);
            invoiceTable.setWidth(com.itextpdf.layout.properties.UnitValue.createPercentValue(100));
            
            // Header
            invoiceTable.addHeaderCell(new com.itextpdf.layout.element.Cell().add(
                new com.itextpdf.layout.element.Paragraph("Ma HD").setFont(font).setBold()));
            invoiceTable.addHeaderCell(new com.itextpdf.layout.element.Cell().add(
                new com.itextpdf.layout.element.Paragraph("Khach hang").setFont(font).setBold()));
            invoiceTable.addHeaderCell(new com.itextpdf.layout.element.Cell().add(
                new com.itextpdf.layout.element.Paragraph("Tong tien").setFont(font).setBold()));
            invoiceTable.addHeaderCell(new com.itextpdf.layout.element.Cell().add(
                new com.itextpdf.layout.element.Paragraph("Trang thai").setFont(font).setBold()));
            
            // Data rows
            for (Invoice inv : filteredInvoices) {
                invoiceTable.addCell(new com.itextpdf.layout.element.Cell().add(
                    new com.itextpdf.layout.element.Paragraph("#" + inv.getId()).setFont(font)));
                invoiceTable.addCell(new com.itextpdf.layout.element.Cell().add(
                    new com.itextpdf.layout.element.Paragraph(inv.getCustomerName()).setFont(font)));
                invoiceTable.addCell(new com.itextpdf.layout.element.Cell().add(
                    new com.itextpdf.layout.element.Paragraph(String.format("%,.0f", inv.getTotalAmount())).setFont(font)));
                invoiceTable.addCell(new com.itextpdf.layout.element.Cell().add(
                    new com.itextpdf.layout.element.Paragraph(inv.getStatus().equals("paid") ? "Da TT" : "Chua TT").setFont(font)));
            }
            
            document.add(invoiceTable);
            
            document.close();
            
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("Thành công");
            alert.setHeaderText(null);
            alert.setContentText("Xuất báo cáo PDF thành công!\nĐã lưu tại: " + file.getAbsolutePath());
            alert.showAndWait();
            
        } catch (Exception e) {
            e.printStackTrace();
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText(null);
            alert.setContentText("Không thể xuất báo cáo: " + e.getMessage());
            alert.showAndWait();
        }
    }
}

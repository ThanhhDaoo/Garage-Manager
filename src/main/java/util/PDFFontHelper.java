package util;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class để tạo font hỗ trợ tiếng Việt cho PDF
 * Tự động tìm và sử dụng font system có sẵn
 */
public class PDFFontHelper {
    
    // Danh sách font ưu tiên (hỗ trợ tiếng Việt tốt)
    private static final List<String> PREFERRED_FONTS = Arrays.asList(
        "Arial Unicode MS", "Arial", "Tahoma", "Segoe UI", "Calibri", 
        "Times New Roman", "DejaVu Sans", "Liberation Sans", "Noto Sans",
        "Microsoft Sans Serif", "Verdana", "Trebuchet MS"
    );
    
    // Đường dẫn font system phổ biến (ưu tiên Windows)
    private static final List<String> FONT_PATHS = Arrays.asList(
        "C:/Windows/Fonts/",                    // Windows (ưu tiên cao nhất)
        "C:/WINDOWS/Fonts/",                    // Windows alternative
        "/System/Library/Fonts/Supplemental/",  // macOS Supplemental (backup)
        "/System/Library/Fonts/",               // macOS System (backup)
        "/Library/Fonts/",                      // macOS user (backup)
        "/usr/share/fonts/",                    // Linux (backup)
        "/usr/local/share/fonts/",              // Linux local (backup)
        "/usr/share/fonts/truetype/",           // Linux truetype (backup)
        "/usr/share/fonts/TTF/"                 // Linux TTF (backup)
    );
    
    /**
     * Tạo font chính hỗ trợ tiếng Việt
     */
    public static PdfFont createVietnameseFont() {
        return createVietnameseFont(false);
    }
    
    /**
     * Tạo font chính hỗ trợ tiếng Việt
     * @param bold true nếu muốn font đậm
     */
    public static PdfFont createVietnameseFont(boolean bold) {
        // Thử tìm font file trực tiếp trước (tốt nhất)
        PdfFont fontFromFile = tryFontFiles(bold);
        if (fontFromFile != null) {
            return fontFromFile;
        }
        
        // Thử font system
        PdfFont systemFont = trySystemFonts(bold);
        if (systemFont != null) {
            return systemFont;
        }
        
        // Fallback cuối cùng - sử dụng Helvetica với Unicode
        System.out.println("⚠ Sử dụng font fallback - có thể không hiển thị đầy đủ tiếng Việt");
        try {
            return PdfFontFactory.createFont(
                StandardFonts.HELVETICA, 
                PdfEncodings.IDENTITY_H,
                PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED
            );
        } catch (Exception e) {
            // Final fallback
            try {
                return PdfFontFactory.createFont(StandardFonts.HELVETICA);
            } catch (Exception ex) {
                throw new RuntimeException("Không thể tạo font cho PDF", ex);
            }
        }
    }
    
    /**
     * Thử tìm file font trực tiếp (ưu tiên cao nhất)
     */
    private static PdfFont tryFontFiles(boolean bold) {
        for (String fontPath : FONT_PATHS) {
            File fontDir = new File(fontPath);
            if (!fontDir.exists() || !fontDir.isDirectory()) {
                continue;
            }
            
            // Danh sách file font ưu tiên cho Windows
            String[] fontFiles;
            if (bold) {
                fontFiles = new String[]{
                    "arialuni.ttf",           // Arial Unicode MS (tốt nhất cho Windows)
                    "arialbd.ttf",            // Arial Bold (Windows)
                    "tahomabd.ttf",           // Tahoma Bold (Windows)
                    "calibrib.ttf",           // Calibri Bold (Windows)
                    "timesbd.ttf",            // Times Bold (Windows)
                    "arial.ttf",              // Arial regular (fallback cho bold)
                    "Arial Unicode.ttf",      // macOS backup
                    "Arial Bold.ttf",         // macOS backup
                    "Tahoma Bold.ttf",        // macOS backup
                    "Times New Roman Bold.ttf", // macOS backup
                    "DejaVuSans-Bold.ttf",    // Linux backup
                    "LiberationSans-Bold.ttf" // Linux backup
                };
            } else {
                fontFiles = new String[]{
                    "arialuni.ttf",           // Arial Unicode MS (tốt nhất cho Windows)
                    "arial.ttf",              // Arial (Windows)
                    "tahoma.ttf",             // Tahoma (Windows)
                    "calibri.ttf",            // Calibri (Windows)
                    "times.ttf",              // Times (Windows)
                    "verdana.ttf",            // Verdana (Windows)
                    "Arial Unicode.ttf",      // macOS backup
                    "Arial.ttf",              // macOS backup
                    "Tahoma.ttf",             // macOS backup
                    "Times New Roman.ttf",    // macOS backup
                    "DejaVuSans.ttf",         // Linux backup
                    "LiberationSans-Regular.ttf", // Linux backup
                    "NotoSans-Regular.ttf"    // Linux backup
                };
            }
            
            for (String fontFile : fontFiles) {
                File file = new File(fontDir, fontFile);
                if (file.exists()) {
                    try {
                        PdfFont font = PdfFontFactory.createFont(
                            file.getAbsolutePath(),
                            PdfEncodings.IDENTITY_H,
                            PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED
                        );
                        
                        // Test font với ký tự tiếng Việt
                        if (testFontWithVietnamese(font)) {
                            System.out.println("✓ Sử dụng font file: " + file.getName());
                            return font;
                        }
                    } catch (Exception e) {
                        // Thử file tiếp theo
                        System.out.println("✗ Không thể dùng font file: " + file.getName() + " - " + e.getMessage());
                    }
                }
            }
        }
        return null;
    }
    
    /**
     * Thử tìm và sử dụng font system
     */
    private static PdfFont trySystemFonts(boolean bold) {
        // Lấy danh sách font có sẵn trên hệ thống
        String[] availableFonts = GraphicsEnvironment.getLocalGraphicsEnvironment()
            .getAvailableFontFamilyNames();
        
        // Thử từng font ưu tiên
        for (String preferredFont : PREFERRED_FONTS) {
            if (Arrays.asList(availableFonts).contains(preferredFont)) {
                PdfFont font = tryCreateFontFromSystem(preferredFont, bold);
                if (font != null) {
                    System.out.println("✓ Sử dụng font system: " + preferredFont + (bold ? " Bold" : ""));
                    return font;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Thử tạo font từ tên font system
     */
    private static PdfFont tryCreateFontFromSystem(String fontName, boolean bold) {
        try {
            // Thử tạo font với tên system
            Font systemFont = new Font(fontName, bold ? Font.BOLD : Font.PLAIN, 12);
            
            // Kiểm tra font có hỗ trợ tiếng Việt không
            if (!systemFont.canDisplay('ă') || !systemFont.canDisplay('ơ') || 
                !systemFont.canDisplay('ư') || !systemFont.canDisplay('đ')) {
                System.out.println("✗ Font " + fontName + " không hỗ trợ đầy đủ tiếng Việt");
                return null; // Font không hỗ trợ tiếng Việt
            }
            
            // Thử các cách tạo font khác nhau
            String[] fontVariants = {
                fontName,
                fontName + (bold ? " Bold" : ""),
                fontName + (bold ? "-Bold" : "-Regular"),
                fontName + (bold ? " Đậm" : " Thường")
            };
            
            for (String variant : fontVariants) {
                try {
                    PdfFont pdfFont = PdfFontFactory.createFont(
                        variant,
                        PdfEncodings.IDENTITY_H,
                        PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED
                    );
                    
                    // Test font thực tế
                    if (testFontWithVietnamese(pdfFont)) {
                        System.out.println("✓ Tạo thành công font system: " + variant);
                        return pdfFont;
                    } else {
                        System.out.println("✗ Font " + variant + " không pass test tiếng Việt");
                    }
                } catch (Exception e) {
                    // Thử variant tiếp theo
                    System.out.println("✗ Không thể tạo font: " + variant + " - " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("✗ Lỗi kiểm tra font system: " + fontName + " - " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Test font với ký tự tiếng Việt thực tế
     */
    private static boolean testFontWithVietnamese(PdfFont font) {
        try {
            // Test các ký tự tiếng Việt quan trọng
            String[] testChars = {"ă", "â", "ê", "ô", "ơ", "ư", "đ", "á", "à", "ả", "ã", "ạ"};
            
            // Nếu font không support sẽ throw exception hoặc return false
            for (String testChar : testChars) {
                // Thử encode ký tự
                font.getWidth(testChar, 12);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Kiểm tra font có hỗ trợ tiếng Việt không
     */
    public static boolean testVietnameseSupport(PdfFont font) {
        return testFontWithVietnamese(font);
    }
    
    /**
     * In thông tin font đang sử dụng (để debug)
     */
    public static void printFontInfo() {
        System.out.println("=== THÔNG TIN FONT HỆ THỐNG ===");
        String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment()
            .getAvailableFontFamilyNames();
        
        System.out.println("Font ưu tiên có sẵn:");
        for (String preferredFont : PREFERRED_FONTS) {
            boolean available = Arrays.asList(fonts).contains(preferredFont);
            System.out.println("  " + preferredFont + ": " + (available ? "✓" : "✗"));
            
            // Test font có hỗ trợ tiếng Việt không
            if (available) {
                try {
                    Font testFont = new Font(preferredFont, Font.PLAIN, 12);
                    boolean supportsVietnamese = testFont.canDisplay('ă') && 
                                               testFont.canDisplay('ơ') && 
                                               testFont.canDisplay('ư') && 
                                               testFont.canDisplay('đ');
                    System.out.println("    Hỗ trợ tiếng Việt: " + (supportsVietnamese ? "✓" : "✗"));
                } catch (Exception e) {
                    System.out.println("    Không thể test: " + e.getMessage());
                }
            }
        }
        
        System.out.println("\nThư mục font:");
        for (String path : FONT_PATHS) {
            File dir = new File(path);
            System.out.println("  " + path + ": " + (dir.exists() ? "✓" : "✗"));
            
            // List một số font quan trọng
            if (dir.exists()) {
                String[] importantFonts = {"arialuni.ttf", "arial.ttf", "tahoma.ttf"};
                for (String fontFile : importantFonts) {
                    File file = new File(dir, fontFile);
                    if (file.exists()) {
                        System.out.println("    " + fontFile + ": ✓");
                    }
                }
            }
        }
    }
    
    /**
     * Tạo font từ resource (nếu có font file trong project)
     */
    public static PdfFont createFontFromResource(String resourcePath, boolean bold) {
        try {
            // Thử load font từ resources
            java.io.InputStream fontStream = PDFFontHelper.class.getResourceAsStream(resourcePath);
            if (fontStream != null) {
                byte[] fontBytes = fontStream.readAllBytes();
                fontStream.close();
                
                return PdfFontFactory.createFont(
                    fontBytes,
                    PdfEncodings.IDENTITY_H,
                    PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED
                );
            }
        } catch (Exception e) {
            System.out.println("✗ Không thể load font từ resource: " + resourcePath);
        }
        return null;
    }
}
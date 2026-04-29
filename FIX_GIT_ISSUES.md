# 🔧 HƯỚNG DẪN SỬA LỖI GIT

## ⚠️ Vấn đề hiện tại:
Dự án đang commit các file KHÔNG NÊN commit:
- ❌ Database files (identifier.sqlite, .sqlite-shm, .sqlite-wal)
- ❌ IDE config files (.idea/)

## ✅ Giải pháp:

### Bước 1: Xóa các file không cần thiết khỏi Git history
```bash
# Xóa database files khỏi git (nhưng giữ lại trên máy local)
git rm --cached identifier.sqlite
git rm --cached identifier.sqlite-shm
git rm --cached identifier.sqlite-wal

# Xóa toàn bộ thư mục .idea khỏi git
git rm -r --cached .idea/
```

### Bước 2: Commit thay đổi
```bash
git add .gitignore
git commit -m "fix: Remove database and IDE files from git tracking"
```

### Bước 3: Push lên remote (nếu có)
```bash
git push origin main
```

## 📋 .gitignore đã được cập nhật với:

### Database files:
```
*.sqlite
*.sqlite-shm
*.sqlite-wal
*.db
*.db-shm
*.db-wal
data/
```

### IDE files:
```
.idea/
*.iws
*.iml
*.ipr
```

## 🎯 Lợi ích sau khi sửa:

✅ Repo nhẹ hơn (không có file binary lớn)
✅ Bảo mật dữ liệu khách hàng
✅ Không conflict khi làm việc nhóm
✅ Professional Git practices
✅ Tốt cho CV và portfolio

## 📝 Lưu ý:

- Database sẽ được tạo tự động khi chạy app lần đầu
- Mỗi developer sẽ có database riêng trên máy local
- Nếu cần share database mẫu, tạo file `schema.sql` hoặc `seed.sql`

## 🔒 Best Practices cho tương lai:

1. **KHÔNG BAO GIỜ commit:**
   - Database files (*.db, *.sqlite)
   - Log files (*.log)
   - Compiled files (*.class, *.jar trong src/)
   - IDE configs (.idea/, .vscode/)
   - Environment files (.env)
   - Credentials (passwords, API keys)

2. **NÊN commit:**
   - Source code (*.java)
   - Configuration templates (application.properties.example)
   - Database schema (schema.sql)
   - Documentation (README.md)
   - Build files (pom.xml, build.gradle)

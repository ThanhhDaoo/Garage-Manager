@echo off
echo ========================================
echo    GARAGE MANAGER - BUILD TO EXE
echo ========================================

echo.
echo [1/4] Cleaning previous builds...
call mvn clean

echo.
echo [2/4] Compiling and packaging...
call mvn package

echo.
echo [3/4] Copying database file...
if not exist "target\data" mkdir "target\data"
copy "identifier.sqlite" "target\data\"

echo.
echo [4/4] Creating executable with Launch4j...
echo.
echo HƯỚNG DẪN TIẾP THEO:
echo 1. Tải Launch4j từ: http://launch4j.sourceforge.net/
echo 2. Cài đặt Launch4j
echo 3. Mở Launch4j và cấu hình:
echo    - Output file: GarageManager.exe
echo    - Jar: target\GarageManager-1.0-SNAPSHOT-shaded.jar
echo    - Min JRE version: 17
echo    - Max heap size: 512
echo 4. Build để tạo file .exe

echo.
echo ✓ JAR file đã sẵn sàng tại: target\GarageManager-1.0-SNAPSHOT-shaded.jar
echo ✓ Database đã được copy tại: target\data\identifier.sqlite
echo.
pause
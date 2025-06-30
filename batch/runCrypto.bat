@echo off
chcp 65001 > nul

echo === File Encryption/Decryption Utility ===
echo 1. Encrypt
echo 2. Decrypt
set /p choice="Select an option (1 or 2): "
if "%choice%"=="1" (
    set mode=enc
) else if "%choice%"=="2" (
    set mode=dec
) else (
    echo Invalid selection. Exiting.
    exit /b
)

set /p src="Enter source directory path: "
set /p tgt="Enter target directory path: "

java FileCryptoProcessor %mode% "%src%" "%tgt%"

echo Operation completed successfully.

pause

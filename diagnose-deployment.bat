@echo off
echo ========================================
echo SYNEX DEPLOYMENT DIAGNOSTICS
echo ========================================
echo.

echo [1] Checking if Tomcat is running...
tasklist /FI "IMAGENAME eq java.exe" 2>NUL | find /I /N "java.exe">NUL
if "%ERRORLEVEL%"=="0" (
    echo ✓ Tomcat appears to be running
) else (
    echo ✗ Tomcat does not appear to be running
    echo   Please start Tomcat in IntelliJ IDEA
)
echo.

echo [2] Checking servlet class files...
set CLASSES_DIR=..\out\artifacts\synex_web_Web_exploded\WEB-INF\classes\presentation\web
if exist "%CLASSES_DIR%\CustomerLoginServlet.class" (
    echo ✓ CustomerLoginServlet.class found
) else (
    echo ✗ CustomerLoginServlet.class NOT found
)

if exist "%CLASSES_DIR%\CustomerRegisterServlet.class" (
    echo ✓ CustomerRegisterServlet.class found
) else (
    echo ✗ CustomerRegisterServlet.class NOT found
)

if exist "%CLASSES_DIR%\PosHomeServlet.class" (
    echo ✓ PosHomeServlet.class found
) else (
    echo ✗ PosHomeServlet.class NOT found
)
echo.

echo [3] Checking web.xml...
set WEB_XML=..\out\artifacts\synex_web_Web_exploded\WEB-INF\web.xml
if exist "%WEB_XML%" (
    echo ✓ web.xml found
    findstr /C:"CustomerLoginServlet" "%WEB_XML%" >NUL
    if %ERRORLEVEL%==0 (
        echo ✓ CustomerLoginServlet mapping found in web.xml
    ) else (
        echo ✗ CustomerLoginServlet mapping NOT found in web.xml
    )
) else (
    echo ✗ web.xml NOT found
)
echo.

echo [4] Checking database connection...
if exist "web\WEB-INF\classes\db.properties" (
    echo ✓ db.properties found
    type "web\WEB-INF\classes\db.properties"
) else (
    echo ✗ db.properties NOT found
)
echo.

echo ========================================
echo RECOMMENDED ACTIONS:
echo ========================================
echo 1. Stop Tomcat completely in IntelliJ
echo 2. Run: Build ^> Rebuild Project
echo 3. Start Tomcat again
echo 4. Test the URLs
echo.
echo If still not working:
echo - Check IntelliJ Run Configuration
echo - Verify Application context is set to: /synex
echo - Check Tomcat logs in IntelliJ console
echo ========================================
pause

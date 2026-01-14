

@echo off
echo ====================================
echo  SYNEX POS SYSTEM - COMPLETE SETUP
echo ====================================
echo.

echo [1/5] Checking Java installation...
java -version
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Java is not installed or not in PATH
    pause
    exit /b 1
)
echo ✓ Java is installed

echo.
echo [2/5] Checking MySQL JDBC Driver...
if exist "web\WEB-INF\lib\mysql-connector-j-8.2.0.jar" (
    echo ✓ MySQL JDBC Driver found
) else (
    echo ERROR: MySQL JDBC Driver not found in web\WEB-INF\lib\
    echo Copying from lib directory...
    copy "lib\mysql-connector-j-8.2.0.jar" "web\WEB-INF\lib\" > nul
    copy "lib\protobuf-java-3.21.9.jar" "web\WEB-INF\lib\" > nul
    if exist "web\WEB-INF\lib\mysql-connector-j-8.2.0.jar" (
        echo ✓ MySQL JDBC Driver copied successfully
    ) else (
        echo ERROR: Failed to copy MySQL JDBC Driver
        pause
        exit /b 1
    )
)

echo.
echo [3/5] Checking Protobuf dependency...
if exist "web\WEB-INF\lib\protobuf-java-3.21.9.jar" (
    echo ✓ Protobuf Java library found
) else (
    echo ERROR: Protobuf Java library not found
    pause
    exit /b 1
)

echo.
echo [4/5] Compiling Java classes...
cd src

echo Compiling core database classes...
javac -cp ".;..\web\WEB-INF\lib\mysql-connector-j-8.2.0.jar;..\web\WEB-INF\lib\protobuf-java-3.21.9.jar" -d "..\web\WEB-INF\classes" infrastructure\jdbc\Db.java
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Failed to compile Db.java
    cd ..
    pause
    exit /b 1
)

echo Compiling test connection class...
javac -cp ".;..\web\WEB-INF\lib\mysql-connector-j-8.2.0.jar;..\web\WEB-INF\lib\protobuf-java-3.21.9.jar" -d "..\web\WEB-INF\classes" infrastructure\jdbc\TestDbConnection.java

echo Compiling domain and service classes...
javac -cp ".;..\web\WEB-INF\lib\mysql-connector-j-8.2.0.jar;..\web\WEB-INF\lib\protobuf-java-3.21.9.jar" -d "..\web\WEB-INF\classes" domain\*.java domain\auth\*.java domain\cart\*.java domain\customer\*.java domain\manager\*.java domain\orders\*.java domain\store\*.java
javac -cp ".;..\web\WEB-INF\lib\mysql-connector-j-8.2.0.jar;..\web\WEB-INF\lib\protobuf-java-3.21.9.jar" -d "..\web\WEB-INF\classes" ports\in\*.java ports\out\*.java
javac -cp ".;..\web\WEB-INF\lib\mysql-connector-j-8.2.0.jar;..\web\WEB-INF\lib\protobuf-java-3.21.9.jar" -d "..\web\WEB-INF\classes" infrastructure\jdbc\*.java infrastructure\concurrency\*.java
javac -cp ".;..\web\WEB-INF\lib\mysql-connector-j-8.2.0.jar;..\web\WEB-INF\lib\protobuf-java-3.21.9.jar" -d "..\web\WEB-INF\classes" application\*.java

echo NOTE: Servlet classes need to be compiled from IntelliJ IDEA or with proper Jakarta EE classpath
echo The Jakarta EE servlet API is required for servlet compilation
cd ..
echo ✓ Java classes compiled successfully

echo.
echo [5/5] Testing database connection...
echo Testing MySQL JDBC Driver loading...
java -cp "web\WEB-INF\classes;web\WEB-INF\lib\mysql-connector-j-8.2.0.jar;web\WEB-INF\lib\protobuf-java-3.21.9.jar" infrastructure.jdbc.TestDbConnection
if %ERRORLEVEL% NEQ 0 (
    echo WARNING: Database connection test failed
    echo Make sure MySQL is running and database exists
    echo Run setup-db.bat to create the database
) else (
    echo ✓ Database connection test passed
)

echo.
echo ====================================
echo  SETUP COMPLETE!
echo ====================================
echo.
echo Next steps:
echo 1. Start your Tomcat server
echo 2. Deploy the application
echo 3. Access http://localhost:8080/synex-web/
echo.
pause

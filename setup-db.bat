@echo off
echo Setting up database...
mysql -u root -e "SOURCE 'D:/APIIT/THIRD YEAR/second semester/CCCP/SynexOutletStore-A2/synex-web/src/main/resources/db/schema.sql'"
if %ERRORLEVEL% EQU 0 (
    echo Database created successfully!
) else (
    echo Failed to create database. Make sure MySQL is running and accessible.
)
pause


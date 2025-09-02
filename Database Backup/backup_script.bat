rem path to mysql server bin folder
cd "C:\Program Files\MySQL\MySQL Server 8.0\bin"

rem credentials to connect to mysql server
set mysql_user=root
set mysql_password=*dbTesting#

rem backup file name generation
set backup_path=C:\Users\yereg\Documents\Coding\Projects\2024\5\Database Backup
set backup_name=acitya_canteen
set TIMESTAMP=[%time:~0,2%.%time:~3,2%.%time:~6,2%].[%DATE:~7,2%-%DATE:~4,2%-%DATE:~10,4%]


rem backup creation
mysqldump --skip-lock-tables --user=%mysql_user% --password=%mysql_password% --databases acitya_canteen --routines --events --result-file="%backup_path%\%backup_name%%TIMESTAMP%.sql"
if %ERRORLEVEL% neq 0 (
    (echo Backup failed: error during dump creation - %TIMESTAMP%) >> "%backup_path%\log.txt"
) else (echo Backup successful  - %TIMESTAMP%) >> "%backup_path%\log.txt"
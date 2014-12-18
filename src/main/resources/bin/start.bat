cd %~dp0..\
SET JERRY_HOME=%cd%
java -Dlog4j.configurationFile=conf/log4j2.xml -Dgaia.base=%JERRY_HOME% -Dgaia.home=%JERRY_HOME% -jar lib/Jerrymouse-1.5.jar
pause
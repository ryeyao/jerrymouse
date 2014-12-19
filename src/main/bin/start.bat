cd %~dp0..\
SET JERRY_HOME=%cd%
rem java -cp lib/* -Dlog4j.configurationFile=conf/log4j2.xml -Dgaia.base=%JERRY_HOME% -Dgaia.home=%JERRY_HOME% org.omg.gaia.startup.Bootstrap
java -Dlog4j.configurationFile=conf/log4j2.xml -Dgaia.base=%JERRY_HOME% -Dgaia.home=%JERRY_HOME% -jar lib/Jerrymouse-1.5.jar
pause
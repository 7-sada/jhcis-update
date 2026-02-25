@echo off
echo build....
java -cp "getdown-launcher-1.8.7.jar;getdown-core-1.8.7.jar" com.threerings.getdown.tools.Digester .
pause
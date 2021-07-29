@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem

@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  dam_dashboard startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Resolve any "." and ".." in APP_HOME to make it shorter.
for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

@rem Add default JVM options here. You can also use JAVA_OPTS and DAM_DASHBOARD_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto execute

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto execute

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\dam_dashboard.jar;%APP_HOME%\lib\vertx-mqtt-4.1.1.jar;%APP_HOME%\lib\vertx-web-4.1.1.jar;%APP_HOME%\lib\vertx-web-client-4.1.1.jar;%APP_HOME%\lib\vertx-web-common-4.1.1.jar;%APP_HOME%\lib\vertx-auth-common-4.1.1.jar;%APP_HOME%\lib\vertx-bridge-common-4.1.1.jar;%APP_HOME%\lib\vertx-core-4.1.1.jar;%APP_HOME%\lib\gson-2.8.7.jar;%APP_HOME%\lib\javafx-fxml-16-win.jar;%APP_HOME%\lib\javafx-controls-16-win.jar;%APP_HOME%\lib\javafx-controls-16.jar;%APP_HOME%\lib\javafx-graphics-16-win.jar;%APP_HOME%\lib\javafx-graphics-16.jar;%APP_HOME%\lib\javafx-base-16-win.jar;%APP_HOME%\lib\javafx-base-16.jar;%APP_HOME%\lib\netty-handler-proxy-4.1.65.Final.jar;%APP_HOME%\lib\netty-codec-http2-4.1.65.Final.jar;%APP_HOME%\lib\netty-codec-http-4.1.65.Final.jar;%APP_HOME%\lib\netty-resolver-dns-4.1.65.Final.jar;%APP_HOME%\lib\netty-handler-4.1.65.Final.jar;%APP_HOME%\lib\netty-codec-mqtt-4.1.65.Final.jar;%APP_HOME%\lib\netty-codec-socks-4.1.65.Final.jar;%APP_HOME%\lib\netty-codec-dns-4.1.65.Final.jar;%APP_HOME%\lib\netty-codec-4.1.65.Final.jar;%APP_HOME%\lib\netty-transport-4.1.65.Final.jar;%APP_HOME%\lib\netty-buffer-4.1.65.Final.jar;%APP_HOME%\lib\netty-resolver-4.1.65.Final.jar;%APP_HOME%\lib\netty-common-4.1.65.Final.jar;%APP_HOME%\lib\jackson-core-2.11.4.jar


@rem Execute dam_dashboard
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %DAM_DASHBOARD_OPTS%  -classpath "%CLASSPATH%" Main %*

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable DAM_DASHBOARD_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%DAM_DASHBOARD_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega

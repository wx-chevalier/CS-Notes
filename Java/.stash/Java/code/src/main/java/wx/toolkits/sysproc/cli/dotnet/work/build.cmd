@echo off
if not exist target mkdir target
if not exist target\classes mkdir target\classes


echo compile classes
javac -nowarn -d target\classes -sourcepath jvm -cp "c:\users\lotuc\desktop\jni4net-0.8.8.0-bin\lib\jni4net.j-0.8.8.0.jar"; "jvm\testlib\Test.java" 
IF %ERRORLEVEL% NEQ 0 goto end


echo testlib.j4n.jar 
jar cvf testlib.j4n.jar  -C target\classes "testlib\Test.class"  > nul 
IF %ERRORLEVEL% NEQ 0 goto end


echo testlib.j4n.dll 
csc /nologo /warn:0 /t:library /out:testlib.j4n.dll /recurse:clr\*.cs  /reference:"C:\Users\lotuc\Desktop\aaa\work\testlib.dll" /reference:"C:\Users\lotuc\Desktop\jni4net-0.8.8.0-bin\lib\jni4net.n-0.8.8.0.dll"
IF %ERRORLEVEL% NEQ 0 goto end


:end

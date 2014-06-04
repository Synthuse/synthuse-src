REM set path=C:\Program Files (x86)\Microsoft Visual Studio 11.0\VC\bin;%path%

%WinDir%\Microsoft.NET\Framework\v4.0.30319\MSBuild.exe /p:configuration=release /p:platform=win32 %*

pause
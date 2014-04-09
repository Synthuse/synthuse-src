Synthuse 

Version 1.0.6 released on 2-17-2014
By Edward Jakubowski  ejakubowski7@gmail.com

Description:
 Synthuse is a portable java base software testing framework for desktop windows 
applications. This framework has a built-in object spy and automation Test IDE.
What makes this different from other frameworks is that it shares similar command
syntax as Selenium.  It also utilizes Xpath statements to locate target windows/objects
The familar syntaxes makes it easier to train automation tester in this frameworks.

  
Configurations:
  All configurable settings are stored in the following file: synthuse.properties
Changes to this file will not take affect until you restart the application.
Example synthuse.properties file below:
urlList=
xpathList=//win[@class\='Notepad']\u00BA
xpathHightlight=.*process\="([^"]*)".*
    

Software Requirements:
  - Java 1.6 or greater
  

Release Notes:
  4-8-2014  version 1.0.9
  - Added Support for automating WPF and Silverlight applications, this requires .Net 4.0 framework
  - Added better mouse scripting support
  2-17-2014  version 1.0.6
  - fixed Test IDE window
  1-30-2014  version 1.0.5
   - Base version


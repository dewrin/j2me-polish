****************************************************
*** J2ME Polish ${ Version }
****************************************************

Find more information about J2ME Polish at www.j2mepolish.org

The documentation is located in the doc-folder.

To migrate J2ME Polish into existing projects, just copy the file 
"build.xml" and the "import" folder to the project root. 

****************************************************
*** Sample Application 
****************************************************

If you have installed the sample application, you can now
build your MIDlet right away. Just call "ant" in the installation
folder (this folder). J2ME Polish will create the MIDlet files in
the "dist" folder.
When you call "ant notest j2mepolish" the MIDlet files will be
obfuscated as well.
You can change the design of the sample application by changing
the file "resources/polish.css". You can check out an entirely
different design by changing the "resdir"-attribute in the 
"build.xml" file to "resources2" (instead of "resources").

Have fun!
_______________________________________________________________________
MATERIAL


These are the working examples from the following 2 part tutorial.
I've tried to organise them so you can run specific scenarios without
changing/commenting any code - for now just using a switched enum

https://www.youtube.com/watch?v=4CQalha3MyY
https://www.youtube.com/watch?v=wpCeIz0oEPY

______________________________________________________________________
PROJECT IMPORT: TESTED ON INTELLIJ, SHOULD BE IDENTICAL ON ECLIPSE

-Git project only contains maven skeleton
1. Intellij > new project from version control > import from github
2. Project will be picked up as maven project and libraries and project settings file created
    -no need to add these to git when prompted

-make sure Java 8 is installed, required for Ignite

____________________________________________________________________
PROJECT SETTINGS

A few different settings are required for 1.8 Java

-  Project settings > Project > Project SDK > 1.8
-  Project settings > modules > sources > language level 8
-  Platform settings > SDKS > 1.8

Get this Error -> javacTask: source release 8 requires target release 1.8
- IDEA > Preferences > Build/Execution/Deployment > Compiler > Java compiler > locate the module in the list and set the target to 1.8


______________________________________________________________________
GETTING STARTED

Try running IgniteNodeStartUp



_____________________________________________________________________
AIM

This code is already available in the Ignite samples folder but wanted to put it
in a ready to run way and also structured precisely around
the content of the two Webinar code walkthroughs below by the co-founder of Gridgain Dmitriy Setrakyan


https://www.youtube.com/watch?v=4CQalha3MyY
https://www.youtube.com/watch?v=wpCeIz0oEPY
____________________________________________________________________
SUGGESTED APPROACH

-watch the videos above in the back ground and follow instructions on each session
    which closely follow the videos

________________________________________________________________________
PREREQUISITES

Java 8 is installed, required for Ignite
______________________________________________________________________
INTELLIJ SETUP

https://github.com/mattua/IgniteExample101.git

-Self-contained Git project only contains maven skeleton -> src, resources, pom
-Ignite is just a set of libraries pulled in from maven via the pom

1. Intellij > new project from version control > import from github
2. Project will be picked up as maven project and libraries and project settings file created
    -no need to add these to git when prompted

A few different settings are required for 1.8 Java

-  Project settings > Project > Project SDK > 1.8
-  Project settings > modules > sources > language level 8
-  Platform settings > SDKS > 1.8

Get this Error -> javacTask: source release 8 requires target release 1.8
- IDEA > Preferences > Build/Execution/Deployment > Compiler > Java compiler > locate the module in the list and set the target to 1.8


____________________________________________________________________
ECLIPSE

https://github.com/mattua/IgniteExample101

1)	Download zip from the webpage https://github.com/mattua/IgniteExample101, extract in workspace.
2)	Import as maven project.
3)	In case project is not using java 8 and showing compilation error : Right click on project >> properties >> java compiler >> set compiler compliance level to 1.8

______________________________________________________________________
GETTING STARTED

Try starting some Ignite Nodes by running "IgniteNodeStartUp"

This will create basic server nodes (Ignite instances)
    -notice how they all register with each other in the console as they start up


_______________________________________________________________________
SESSION 1 EXAMPLES

Once you have started a few nodes, run the session1.IgniteWebinarExamples1 class, changing the
selected scenario per the switch statement.

________________________________________________________________________
SESSION 2

This is mostly about streaming and requires the following to be run sequentially

1) Start a few instances of IgniteNoodeStartup as per sesssion one - you can have as many nodes running on your
    cluster as you want.


2) Run "session2.StreamWords" class - this will populate an Ignite cache by reading in the lines of the "Alice In Wonderland"
    story and streaming the words one by one into the shared Ignite cache - a cache which is configured with a time to
    life of 1 second, so it will be a sliding window of every word streamed in the last 1 second

3) Run "session2.QueryWords" class to query the contents of this cache in real time in SQL like syntax- and do some analytics

    a) MAKE SURE session2.StreamWords is running in the background (2) streaming words into the cache in
    real time with a 1 second time to expiry

    b) After a while, shutdown the session2.StreamWords instance - you should see that the streaming
    stops and almost immediately, the QueryResults window shows no contents - almost immediately all
    the contents of the cache have expired and there are no new entries coming in

    You should see "Query result set is empty."

    We need a way to have a backup in case the actual streamer goes down - that's where the next
    exmaple comes into play

4) Run "session2.StreamerSingletonServiceExample"

    We have basically wrapped up the previous Streamer functionality into a service/impl and deployed that service
    object into the Ignite cluster as a singleton.






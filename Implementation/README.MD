# Project: Cretaceous Gardens Controller
## Team Members:  Siri Khalsa, Anas Gauba, Ezequiel Ramos, Matthew Stone,Santiago Cejas

## Introduction
This project similates possible behavior of the CGC. The CGC is a system
that would help control the entire software application for a park that has a T-Rex 
exhibit. it will handle sales and tracking of devices as well as communication.
It this where to be implemented in a real life scenario we would be using sockets 
communication between all different parts. in an effort to simulate this without having to run
multiple applications at once we isolated everything to run from one application.

## Contributions
This is a group project that is an extension of the hard design work performed
in the documents, TFS, RDD, SRS, SAD. All 5 team members have contributed

## Usage

It is very easy to use this application.
- Entry Point: Main Class
- JDK Version: 10
- must use JRE 10

#### Starting the App
1. please Make sure you are using the JRE 10 to run this application
2. Please Download the folder CGCExecutableJAR it contains everything needed to run the application
3. Open up a terminal or command prompt and cd into the downloaded folder
4. Type `java -jar Implementation.jar`

##### When you download the jar make sure you download the jar from the master branch on csgit


#### GUI
The GUI is really simple. The application is a true simulation in the sense that it pretty much runs on it's own.
there is one thing that can be controlled and this is the Emergency Mode state. this can be toggled
using the buttons on the left side of the GUI. The buttons on the right toggle a health overlay. and will take you to a page describing the 
tokens that have been purchased


## Versions 

right now there is currently only 1 version.

## Docs

the documents used to build this application can be found outside of the Implementation folder.

## Overview of Threading Communication
 
In an effort to help explain how we were able to make our threads communicate and function correctly. 
We will explain it briefly here as requested.

We did follow our design document so this will be a good place to see which classes can communicate with each other.

1. The magic that allows all the threads to communicate with each other is the Communicator Interface. 
This guarantees that these classes will be able to accept the Message Interface class.

2. this leads into the next main idea that we used. The Message system. Since we are using
java we get to use the generics. This is critical to the working of the communication system.
Every thread contains a PriorityBlockingQueue<Message> it can accept any type of Message. When a class needs to communicate
with a different class it can define its own Message.

3. This leads to the next idea that is critical. Threads should only communicate with other threads by putting messages in a blocking queue
It is possible  to perform your own blocking on resources.... but this is a nightmare! Someone already wrote a blocking queue for us.
we also used a Priority queue. and every message is generated with a timestamp. 
The messages are then responded to in order of their creation time. it also allowed us to make Emergency Messages get responded to immediately by overriding the timestamp.

4. the Last piece to this communication stratagy is a method called processMessage. this is where we
perform the logical response to the messages. the key idea is to ONLY access and change state from this method.
the thread waits on this queue when it receives a Message it passes it into processMessage to respond to.

5. Another key point here is that the synchronized keyword in java should not be over used. any function on the priority blocking queue already performs
blocking. We had a method called sendMessage that is obtained from the communicator interface. It places the message in the queue.
We fixed almost ALL of our thread communication issues by removing the synchronized keyword
what would happen is  threads would be be put to sleep  from the blocking queue and also from the synchronized method. the one blocked 
on the synchronized sendMessage method would never wake back up and be stuck.

6. we have timers all over the place that produced data. for example updating the location so we could animate
walking. following rule 4 the timer would just send the message to itself. 

7. The GUI was also unique. as it controlled 2 threads one is from the communicator interface and the other is from 
the AnimationTimer. The animation Timer should NOT block ever. if there is no state to rendor we just dont do anything.
the communicator thread will block on messages still :) the communicator thread is what writes to the local cache of state that is rendered.
the animation timer ONLY ever views that cache of data or it can change JAVAFX nodes.

## Status
### Known Issues
all known issue or potential future features can be found in the featuresAndBugs.txt
this is in the same location as this readme file. Most bugs have been identified and fixed. :)
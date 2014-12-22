Jerrymouse
==========

A simple Java multi-threaded app container extracted from projects I once worked on. 

## Features
* App lifecycle state machine (inspired by tomcat)
* One thread per app (inefficient and ugly thread model, to be improved)
* Isolated classloaders for each app (inspired by tomcat)
* ~~Hot deployment~~ (not implemented yet)
* I18n (still working on it)
* Simple session management
* Thread level message queue (in memmory)
* ~~Persistent message queue~~ (not implemented yet)
* Simple request processing framework, can be easily extended to either an asynchronzed or a synchronized server
* Simple udp server implementation

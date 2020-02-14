# Clojure intermediate workshop

## Setup instructions

### Java 11
You will need java to work with this workshop content.

First check if you have Java 11. You can do this by running the following command:
`java -version`

If Java is not installed please register at this link and download it for free from the following link.
https://www.oracle.com/technetwork/java/javase/downloads/jdk11-downloads-5066655.html

You can verify that java has been installed properly by running the following command
`java -version`

We have not tested this project with Java versions other than Java 11 hence it is highly recommended 
that you have this one installed 

### Leiningen
Follow Leiningen setup instructions mentioned here: https://leiningen.org/

Fire up a REPL by running the following command `lein repl`. This should give you a repl
prompt. Enter `(+ 1 2)` and press enter. You should get back `3` as the result.

Additionally run `lein --version` to ensure that the correct version of lein has been installed.

This project is tested with Leiningen 2.9.1. It is highly recommended that you use the same version.

**WARNING** This project will break with Leiningen 3.0.0 or higher

### Intellij

You can download Intellij by following the instructions here: https://www.jetbrains.com/idea/download/index.html

You are free to use the editor of your choice as long as you can do the following with it:

* Jump to definition for Clojure or Java sources
* Start or connect to a REPL
* Structural editing with paredit / parinfer


We will be using Intellij + Cursive for this workshop hence it is highly recommended that you have it installed. For Intellij, 
we recommend version 2019.3.2 or later.


### Cursive

You can get started with Cursive by following the instructions here: https://cursive-ide.com/userguide/


### Postman
You can download Postman by following the instructions here: https://www.postman.com/downloads/

The recommended version of Postman is 7.17 or later
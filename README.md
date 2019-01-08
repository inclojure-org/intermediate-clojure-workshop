# Clojure intermediate workshop

# Setup Instructions

Just do the following one by one, and you should be fine.

## Java 8

You will need Java to work with this Clojure workshop content.

First, make sure you have Java 8.

  - Run `java -version` in your terminal.
  - If Java is not installed, please [download and install Java 8 from here](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html).
  - Once you are done, `java -version` should show you a Java 1.8.x version.

Notes:

  - If you have Java 9+, that should be OK too.
  - The LightTable editor is known to break with Java 9. Use Java 8 if you are keen on using LightTable.
  - We have not tested this project with Java 7 and earlier.


## Leiningen

Follow [Leiningen setup instructions here](https://leiningen.org/).

### Fire up a REPL

  - Clone this project
  - Open your terminal, and do the following.
  - `cd` into this project's root directory
  - Use `lein repl` command to start a REPL with Leiningen.
  - Wait for it... the REPL will start and print out a message with some
    useful information
  - Locate the `port` and `host` information in the message. We will need this information soon.

Note:

  - [Boot](http://boot-clj.com/) should be fine too, but you may need to generate your own _boot_ file(s).


## Code Editor and Tooling

Set up an editor and figure out how to evaluate Clojure code with it.
We are fine with you choosing the editor as long as your editor can do,

  - Connect to a Clojure REPL from the editor
    - Evaluate snippets and/or entire namespaces in the connected REPL from the editor.
  - Code navigation
  - Paredit / Parinfer

Editors we can help out with
  - Emacs
  - Vim
  - Cursive


### Cursive (IntelliJ)

If you don't have an editor setup, we suggest you use Cursive with IntelliJ. Please follow instructions from [here](https://cursive-ide.com/userguide/).
Do note that you may need to use it in trial mode or get an appropriate license ahead of time. There's a cost-free license available for personal/non-commercial hacking.


## Copyright and License

Copyright � 2018-2019 [IN/Clojure](https://inclojure.org/).

Distributed under the [MIT license](https://github.com/inclojure-org/clojure-by-example/blob/master/LICENSE).

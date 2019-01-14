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


# Further reading

## Inspiring and insightful source code

### [Monger](https://github.com/michaelklishin/monger)

Standard way of writing database wrappers. Monger uses official Java client for Mongo.
Few interesting parts,
  - [monger.query/exec](https://github.com/michaelklishin/monger/blob/master/src/clojure/monger/query.clj#L87) uses doto to run a bunch of Java methods
  -  [monger.query/exec](https://github.com/michaelklishin/monger/blob/master/src/clojure/monger/query.clj#L87) also uses Protocols [monger.conversion/ConvertToDBObject](https://github.com/michaelklishin/monger/blob/6bf528ed5b8a21153e3df1aa0cd1d88e08f31e3a/src/clojure/monger/conversion.clj#L52) and [monger.conversion/ConvertFromDBObject](https://github.com/michaelklishin/monger/blob/6bf528ed5b8a21153e3df1aa0cd1d88e08f31e3a/src/clojure/monger/conversion.clj#L108) to convert objects at boundry. Monger tries to convert mutable structures List/DBList to Clojure vectors.

### [Carmine](https://github.com/ptaoussanis/carmine)
Carmine is Redis client and written almost from scratch.
  - API is built with [macro](https://github.com/ptaoussanis/carmine/blob/master/src/taoensso/carmine.clj#L21)
  - Atom is used to collect [test](https://github.com/ptaoussanis/carmine/blob/d00b61afb25426c8ec44f24bf544ae85dc93a4af/test/taoensso/carmine/tests/main.clj#L249) results
    - Exercise - Can we use core.async here to remove dependency on Thread/sleep?
  - It uses [IConnectionPool](https://github.com/ptaoussanis/carmine/blob/d00b61afb25426c8ec44f24bf544ae85dc93a4af/src/taoensso/carmine/connections.clj#L42) to create different implementations of connection pool
  - It uses a [edn file](https://github.com/ptaoussanis/carmine/blob/7d0e6f054a42473af4c513869491b752567f3cec/src/commands.edn) to map list of commands Redis supports to Carmine API functions using [defcommands macro](https://github.com/ptaoussanis/carmine/blob/d00b61afb25426c8ec44f24bf544ae85dc93a4af/src/taoensso/carmine/commands.clj#L275)

### [core.cache](https://github.com/clojure/core.cache)
core.cache is Clojure contrib library. It provides in-memory implementations of different caching strategies
  - [core.cache/defcache](https://github.com/clojure/core.cache/blob/master/src/main/clojure/clojure/core/cache.clj#L67) is a macro that reduces repetition of defining a type
  - [This](https://github.com/clojure/core.cache/blob/master/src/main/clojure/clojure/core/cache.clj#L224) shows that mutation to cache creates a new value

## Links
  - [Clojure ground up](https://aphyr.com/tags/Clojure-from-the-ground-up))
  - [Clojure toolbox - list of clojure projects/libraries](https://www.clojure-toolbox.com)

## Copyright and License

Copyright � 2018-2019 [IN/Clojure](https://inclojure.org/).

Distributed under the [MIT license](https://github.com/inclojure-org/clojure-by-example/blob/master/LICENSE).

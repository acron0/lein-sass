# lein-sass [![Build Status](https://travis-ci.org/101loops/lein-sass.svg)](https://travis-ci.org/101loops/lein-sass)

Leiningen plugin to compile SASS/SCSS files with [SassC](https://github.com/sass/sassc).

[![Clojars Project](http://clojars.org/lein-sass/latest-version.svg)](http://clojars.org/lein-sass)


## Installation

You can install the plugin by adding lein-sass to your `project.clj` file in the `plugins` section:

```clj
(defproject example "1.0.0"
  :plugins [[lein-sass "0.3.0-SNAPSHOT"]])
```

[SassC](https://github.com/sass/sassc) needs to be installed manually.
For example on OSX run `brew install sassc`.


## Configuration

Here is an example of `project.clj` with all the possible definitions.

```clj
(defproject example-project "1.2.3"

  :sass {:src "resources/sass"
         :output-directory "resources/public/css"

         ;; other options (provided are default values):
         ;; :delete-output-dir true
         ;; :source-maps true
         ;; :style :nested
         }
```


## Usage

To compile your files once:

```sh
$ lein sassc once
```

To keep the compiler running and watch for changes:

```sh
$ lein sassc auto
```

To delete all the files generated by lein-sass

```
$ lein sassc clean
```


## License

Copyright (C) 2013 Renaud Tircher, 2014 Stephan Behnke

Distributed under the Eclipse Public License, the same as Clojure.

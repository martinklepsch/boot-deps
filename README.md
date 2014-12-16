# boot-ancient

Wanna-be [lein-ancient](https://github.com/xsc/lein-ancient) for Boot.

Provides the `ancient` task, which lists outdated dependencies

[![Clojars Project](http://clojars.org/boot-ancient/latest-version.svg)](http://clojars.org/boot-ancient)

## Usage

This is mostly intended for Terminal usage as of now:

```clojure
$ boot -d boot-ancient ancient
Searching for outdated dependencies...
Currently using [om "0.7.3"] but 0.8.0-beta3 is available
Currently using [boot-garden "1.2.5"] but 1.2.5-1 is available
```

## Options

Currently there are no options but eventually boot-ancient will support similar options as lein-ancient.

## License

Copyright Martin Klepsch 2014.

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.

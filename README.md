# boot-deps

Wanna-be [lein-ancient](https://github.com/xsc/lein-ancient) and more for Boot.

Provides:
- `ancient` task, which lists outdated dependencies
- More to come, PRs welcome

[![Clojars Project](http://clojars.org/boot-deps/latest-version.svg)](http://clojars.org/boot-deps)

## Usage

This is mostly intended for Terminal usage as of now:

```clojure
$ boot -d boot-deps ancient
Searching for outdated dependencies...
Currently using [om "0.7.3"] but 0.8.0-beta3 is available
Currently using [boot-garden "1.2.5"] but 1.2.5-1 is available
```

If you want to have `boot-deps` available globally you can add it to your `~/.profile.boot` like so:

```clojure
(set-env! :dependencies '[[boot-deps "0.1.2"]])
(require '[boot-deps :refer [ancient]])
```


## Options

```clojure
[s snapshots  bool  "allow SNAPSHOT versions to be reported as new"
 q qualified  bool  "allow alpha, beta, etc... versions to be reported as new"
 a all        bool  "allow SNAPSHOT and qualified versions to be reported as new"]
```

## License

Copyright Martin Klepsch 2014.

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.

# boot-deps

Wanna-be [lein-ancient](https://github.com/xsc/lein-ancient) and more for Boot. Also consider `boot show --updates` which is a built-in task listing dependencies with newer releases.

Provides:
- `ancient` task, which lists outdated dependencies
- `latest` task, which shows latest version of a library
- More to come, PRs welcome

[](dependency)
```clojure
[org.martinklepsch/boot-deps "0.2.0-SNAPSHOT"] ;; latest release
```
[](/dependency)

## Usage

This is mostly intended for Terminal usage as of now:

```clojure
$ boot -d boot-deps ancient
Searching for outdated dependencies...
Currently using [om "0.7.3"] but 0.8.0-beta3 is available
Currently using [boot-garden "1.2.5"] but 1.2.5-1 is available

$ boot -d boot-deps latest -l org.clojure/clojure -q
Searching for latest version of [org.clojure/clojure]...: 1.7.0-RC1
```

If you want to have `boot-deps` available globally you can add it to your `$BOOT_HOME/profile.boot` (usually `$BOOT_HOME` is set to `~/.boot`, see `boot -h` for details) like so:

```clojure
(set-env! :dependencies '[[org.martinklepsch/boot-deps "RELEASE"]])
(require '[org.martinklepsch/boot-deps :refer [ancient]])
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

(ns boot-deps
  {:boot/export-tasks true}
  (:require [clojure.java.io   :as io]
            [boot.core         :as boot :refer [deftask]]
            [boot.pod          :as pod]
            [boot.file         :as file]
            [boot.util         :as util]))

(def initial
  (atom true))

(defn make-ancient-pod [] ;; need slingshot b/c ancient-clj has :exclusions on it and clj-http needs it
  (pod/make-pod (assoc (boot/get-env) :dependencies '[[slingshot "0.12.2"] [ancient-clj "0.3.14"]])))

(defn- skip-upgrade-check?
  [[_ _ & opts]]
  (let [{:keys [upgrade upgrade?]} (apply hash-map opts)]
    (or (= upgrade false)
        (= upgrade? false))))

(defn find-outdated [env opts]
  (let [ancient-pod (make-ancient-pod)
        {:keys [dependencies repositories]} env]
    (pod/with-eval-in ancient-pod
      (require '[ancient-clj.core :as ancient])
      (let [deps ~(into []
                    (comp
                      (remove skip-upgrade-check?)
                      (map #(list 'quote %)))
                    dependencies)
            artifacts (map ancient/read-artifact deps)
            outdated (map #(ancient/artifact-outdated? % ~opts) deps)]
        (->> (map vector artifacts outdated)
             (filter #(identity (second %))))))))

(defn find-latest [env library opts]
  (let [ancient-pod (make-ancient-pod)
        opts        (assoc opts :repositories (:repositories env))]
    (pod/with-eval-in ancient-pod
      (require '[ancient-clj.core :as ancient])
      (ancient/latest-version-string! (quote ~library) ~opts))))

(deftask ancient
  "Find outdated dependencies"
  [s snapshots  bool  "allow SNAPSHOT versions to be reported as new"
   q qualified  bool  "allow alpha, beta, etc... versions to be reported as new"
   a all        bool  "allow SNAPSHOT and qualified versions to be reported as new"]
  (boot/with-pre-wrap fileset
    (util/info "Searching for outdated dependencies...\n")
    (let [opts {:snapshots? (or snapshots all)
                :qualified? (or qualified all)}]
      (doseq [[artifact new] (find-outdated (boot/get-env) opts)]
        (util/info "Currently using %s but %s is available\n"
          (pr-str (:form artifact)) (:version-string new))))
    fileset))

(deftask latest
  "Find latest version of given library"
  [l library   VAL sym  "library name"
   s snapshots     bool "allow SNAPSHOT versions to be reported as new"
   q qualified     bool "allow alpha, beta, etc... versions to be reported as new"
   a all           bool "allow SNAPSHOT and qualified versions to be reported as new"]
  (if-not library
    (do
      (util/fail "The -l/--library option is required!\n")
      (*usage*))
    (boot/with-pre-wrap fileset
      (util/info (str "Searching for latest version of [" library "]...:"))
      (let [opts {:snapshots? (or snapshots all)
                  :qualified? (or qualified all)}]
        (util/info " %s\n" (find-latest (boot/get-env) library opts)))
      fileset)))

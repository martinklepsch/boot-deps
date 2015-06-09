(ns boot-deps
  {:boot/export-tasks true}
  (:require [clojure.java.io   :as io]
            [boot.core         :as boot :refer [deftask]]
            [boot.pod          :as pod]
            [boot.file         :as file]
            [boot.util         :as util]))

(def initial
  (atom true))

(defn make-ancient-pod []
  (pod/make-pod (assoc (boot/get-env) :dependencies '[[ancient-clj "0.2.1"]])))

(defn find-outdated [env opts]
  (let [ancient-pod (make-ancient-pod)
        {:keys [dependencies repositories]} env]
    (pod/with-eval-in ancient-pod
      (require '[ancient-clj.core :as ancient])
      (let [deps ~(mapv #(list 'quote %) dependencies)
            artifacts (map ancient/read-artifact deps)
            outdated (map #(ancient/artifact-outdated? % ~opts) deps)]
        (->> (map vector artifacts outdated)
          (filter #(identity (second %))))))))

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

(defn find-latest [env library opts]
  (let [ancient-pod (make-ancient-pod)
        {:keys [repositories]} env]
    (pod/with-eval-in ancient-pod
      (require '[ancient-clj.core :as ancient])
      (ancient/latest-version-string! (quote ~library)
                                      (assoc ~opts :repositories ~repositories)))))

(deftask latest
  "Find latest version of given library"
  [l library   VAL sym  "library identifier"
   s snapshots     bool "allow SNAPSHOT versions to be reported as new"
   q qualified     bool "allow alpha, beta, etc... versions to be reported as new"
   a all           bool "allow SNAPSHOT and qualified versions to be reported as new"]
  (boot/with-pre-wrap fileset
    (util/info (str "Searching for latest versions of [" library "]...:"))
    (let [opts {:snapshots? (or snapshots all)
                :qualified? (or qualified all)}]
      (util/info " %s\n" (find-latest (boot/get-env) library opts)))
    fileset))

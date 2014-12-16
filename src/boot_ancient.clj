(ns boot-ancient
  {:boot/export-tasks true}
  (:require [clojure.java.io   :as io]
            [boot.core         :as boot :refer [deftask]]
            [boot.pod          :as pod]
            [boot.file         :as file]
            [boot.util         :as util]))

(def initial
  (atom true))

(defn make-ancient-pod []
  (pod/make-pod (assoc-in (boot/get-env) [:dependencies] '[[ancient-clj "0.2.1"]])))

(deftask ancient
  "Find outdated dependencies"
  []
  (let [ancient-pod (make-ancient-pod)
        deps        (boot/get-env :dependencies)]
    (boot/with-pre-wrap fileset
      (util/info "Searching for outdated dependencies...\n")
      (pod/with-eval-in ancient-pod
        (require 'ancient-clj.core)
        (doseq [dep ~(mapv #(list 'quote %) deps)]
          (let [artifact (ancient-clj.core/read-artifact dep)
                outdated (ancient-clj.core/artifact-outdated? dep)]
            (if outdated
              (util/info "Currently using %s but %s is available\n" (pr-str (:form artifact)) (:version-string outdated))))))
      fileset)))

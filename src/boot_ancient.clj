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
  [;o output-to PATH      str   "The output css file path relative to docroot."
   ;s styles-var SYM      sym   "The var containing garden rules"
   ;p pretty-print        bool  "Pretty print compiled CSS"
   ;v vendors             [str] "Vendors to apply prefixed for"
   ;a auto-prefix         [str] "Properties to auto-prefix with vendor-prefixes"
   ]

  (let [ancient-pod (make-ancient-pod)
        deps        (boot/get-env :dependencies)]
    (boot/with-pre-wrap fileset
      (util/info "Searching for outdated dependencies...\n")
      ;(util/info (first deps) "\n")
      (pod/with-eval-in ancient-pod
        (require 'ancient-clj.core)
        ;(ancient-clj.core/artifact-outdated? ~(first deps))
        (doseq [dep ~(mapv #(list 'quote %) deps)]
          (let [artifact (ancient-clj.core/read-artifact dep)
                outdated (ancient-clj.core/artifact-outdated? dep)]
            (if outdated
              (println "Currently using" (pr-str (:form artifact)) "but" (:version-string outdated) "is available")))))
      fileset)))

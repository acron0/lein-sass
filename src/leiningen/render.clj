(ns leiningen.render
  (:use leiningen.utils)
  (:require [clojure-watch.core :refer [start-watch]]
            [clojure.java.shell :as shell]
            [clojure.java.io :as io])
  (:import java.lang.Thread))

(defn build-command-vec [src-file dest-file {:keys [command style source-maps]}]
  (let [src-path (.getPath src-file)
        dest-path (.getPath dest-file)
        sass-style (name style)]
    (case command
      :sassc (let [opts [ "-t" sass-style src-path dest-path]
                   add-opts (if source-maps ["-m"] [])]
                 (concat ["sassc"] add-opts opts))

      :sass (let [opts [ "--update" "--force" "-t" sass-style (str src-path ":" dest-path)]
                  add-opts (if source-maps ["--sourcemap=auto"] ["--sourcemap=none"])]
                 (concat ["sass"] add-opts opts)))))

(defn render
  [src-file dest-file options]
  (when (and (not (is-partial? src-file)) (name-matches? src-file options))
    (io/make-parents dest-file)
    (let [opts-vec (build-command-vec src-file dest-file options)]
      (println (str "  [sass] - " (.getName src-file)))
      ;;(println opts-vec)
      (println (:err (apply shell/sh opts-vec))))))

(defn render-once!
  [options]
  (let [descriptors (files-from options)]
    (doseq [[src-file dest-file] descriptors]
      (render src-file dest-file options))))

(defn render-loop!
  ([options]
    (render-once! options)
    (start-watch [{:path (:src options)
                   :event-types [:create :modify :delete]
                   :callback (fn [_ _] (render-once! options))
                   :options {:recursive true}}])
    (let [t (Thread/currentThread)]
      (locking t
        (.wait t)))))

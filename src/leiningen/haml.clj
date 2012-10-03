(ns leiningen.haml
  (:use leiningen.lein-haml-sass.render-engine
        leiningen.lein-haml-sass.options
        leiningen.lein-common.lein-utils)
  (:require [leiningen.help    :as lhelp]
            [leiningen.clean   :as lclean]
            [leiningen.compile :as lcompile]
            [robert.hooke      :as hooke]))

(defn- once
  "Compiles the haml files once"
  [options]
  (println (str "Compiling haml files located in " (:src options)))
  (render-all! options false true))

(defn- auto
  "Automatically recompiles when files are modified"
  [options]
  (println (str "Ready to compile haml files located in " (:src options)))
  (flush)
  (render-all! options true))

(defn- clean
  "Removes the autogenerated files"
  [options]
  (println "Deleting files generated by lein-haml.")
  (clean-all! options))

(defn- deps
  "Installs the required haml gem"
  [options]
  (install-gem! options))

;; Leiningen task
(defn haml
  "Compiles haml files."
  {:help-arglists '([once auto clean])
   :subtasks [#'once #'auto #'clean #'deps]}
  ([project]
     (exit-failure (lhelp/help-for "haml")))

  ([project subtask & args]
     (let [options (extract-options :haml project)]
       (case subtask
         "once"  (once  options)
         "auto"  (auto  options)
         "clean" (clean options)
         "deps"  (deps  options)
         (task-not-found subtask)))))


;; Hooks stuffs
(defmacro hook [task subtask args]
  `(let [options# (extract-options :haml (first ~args))]
     (apply ~task ~args)
     (when-not (~subtask (:ignore-hooks options#))
       (~(symbol (name subtask)) options#))))

(defn- compile-hook [task & args]
  (hook task :once args))

(defn- clean-hook [task & args]
  (hook task :clean args))

(hooke/add-hook #'lcompile/compile #'compile-hook)
(hooke/add-hook #'lclean/clean #'clean-hook)

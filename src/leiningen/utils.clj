(ns leiningen.utils
  (:require [me.raynes.fs :as fs]
            [clojure.java.io :as io]
            [clojure.string :as string]))

(def ^:private default-options {:src "resources"
                                :matches :all
                                :style :nested
                                :command :sassc
                                :delete-output-dir true
                                :output-directory "resources/public/css"
                                :source-maps true})

(defn normalize-options
  [options]
  (merge default-options options))

(defn- dest-file
  [src-file src-dir dest-dir]
  (let [src-dir (.getCanonicalPath (io/file src-dir))
        dest-dir (.getCanonicalPath (io/file dest-dir))
        src-path (.getCanonicalPath src-file)
        rel-src-path (string/replace src-path src-dir "")
        rel-dest-path (string/replace rel-src-path (fs/extension src-file) ".css")]
    (io/file (str dest-dir rel-dest-path))))

(defn files-from
  [{:keys [src output-directory]}]
  (let [file-filter (fn [file]
                      (case (fs/extension file)
                        ".scss" true
                        ".sass" true
                        false))
        source-files (fs/find-files* src file-filter)]
    (reduce #(assoc %1 %2 (io/file (dest-file %2 src output-directory))) {} source-files)))

(defn is-partial?
  [file]
  (.startsWith (.getName file) "_"))

(defn name-matches?
  [file options]
  (if (= (:matches options) :all)
    true
    (let [file-str (.getAbsolutePath file)
          match-arr (:matches options)]
          (boolean (some (fn [match]
                           (> (.indexOf file-str match) -1)) match-arr)))))

(defn exists
  [dir]
  (and dir (.exists (io/file dir))))

(defn dir-empty?
  [dir]
  (not (reduce (fn [memo path] (or memo (.isFile path))) false (file-seq (io/file dir)))))

(defn delete-file!
  [file]
  (when (.exists file)
    (println (str "Deleting: " file))
    (io/delete-file file)))

(defn delete-directory-recursively!
  [base-dir]
  (doseq [file (reverse (file-seq (io/file base-dir)))]
    (delete-file! file)))

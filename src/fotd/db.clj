(ns fotd.db
  (:require [fotd.io :as io]))

;; a bootleg file db implementation

(def index (atom {:loaded false}))

;; the schema of a database
;; db-name should be a folder
;; db-name/index.edn
;;      -contains index of tables
;;      -each table represents a file in db-name/tables

;; If they pass a file path, that's on them.
;; honestly first-date.jpg/index.edn would be kinda funny
(defn init-index [db-path]
  (->> (str db-path "/index.edn")
       io/slurp
       (reset! index))
  (swap! index assoc :loaded true))

(defn new-index [folder-path]
  {:version :1
   :table-path (str folder-path "/tables/")
   :tables {}})

(defn create-db [folder-path]
  (io/mkdir folder-path)
  (->> (new-index folder-path)
       (io/spit (str folder-path "/index.edn"))))

(defn init-db [folder-path]
  (when (not (io/dir? folder-path))
    (create-db folder-path))
  (init-index folder-path))

#_(clojure.java.io/make-parents "resources/testdb/index.edn")

(defn get-tables []
  (when (:loaded @index)
    (:tables @index)))

(defn create-table [name]
  (when (:loaded @index)
    (swap! index assoc-in [:tables name] (io/file-agent (str (:table-path @index) name)))))

(get-tables)
(init-db "resources/database")
@index
(create-table :users)

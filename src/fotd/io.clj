(ns fotd.io
  (:require [clojure.java.io :as cio]))

(def file-agents (agent {}))

(defn register-file-agent [file-path agent]
  (send file-agents assoc file-path agent))

(defn get-agent [file-path]
  (when (contains? @file-agents file-path)
    (deref (get @file-agents file-path))))

(defn path-exists? [file-path]
  (.exists (cio/file file-path)))

(defn file-agent [file-path]
  (let [new-agent
        (if (path-exists? file-path)
          (agent (clojure.core/slurp file-path))
          (agent nil))]
    (register-file-agent file-path new-agent)
    (add-watch new-agent :file-writer
               (fn [key agent old-state new-state]
                 (future
                   (cio/make-parents file-path)
                   (spit file-path new-state))))))

(defn agent-write [file-agent content]
  (send file-agent (constantly content)))

(defn slurp
  ([file-path] (slurp file-path identity))
  ([file-path xform]
   (if (contains? @file-agents file-path)
     (xform (deref (get @file-agents file-path)))
     (xform (deref (file-agent file-path))))))

(defn spit [file-path content]
  (if (contains? @file-agents file-path)
    (agent-write (get @file-agents file-path) content)
    (agent-write (file-agent file-path) content)))

;; some helpers
(defn dir? [path]
  (.isDirectory (cio/file path)))

(defn mkdir [path]
  (.mkdir (cio/file path)))


;; Notes:
;; Due to the cache this system doesn't know about outside file
;; system changes. It will not auto-update the cache if the file
;; changes. It will only update the file when the atom changes

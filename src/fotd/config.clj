(ns fotd.config)

(def config-file-name "config.edd")

(def config-path (str "resources/" config-file-name))

(defn generate-config []
  (let [default-config {:environment :dev}]
    (spit config-path default-config)
    default-config))

(defn load-config []
  (try
    (slurp config-path)
    (catch java.io.FileNotFoundException _ (generate-config))))

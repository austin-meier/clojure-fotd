(ns fotd.core
  (:require [fotd.server]
            [fotd.config :as config])
  (:gen-class))

(def app (atom {}))

(defn -main [& args]
  (swap! app assoc :config (config/load-config))
  (fotd.server/start-server))

;; important things
(comment
  (deref app)

  (-main)

  (fotd.server/stop-server))

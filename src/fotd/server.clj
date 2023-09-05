(ns fotd.server
  (:require [org.httpkit.server :as httpkit]
            [fotd.html :as html]))

(defn hello-handler [req]
  {:status 200
   :body (str req)})


(defn bye-handler [req]
  {:status 200
   :body "BYE HANDLER"})

(def not-found-handler
  {:status 404
   :headers {"Content-Type" "text/html"}
   :body (html/not-found)})

(defn is-route [[method route] req]
  (and (= method (:request-method req))
       (= (str "/" route) (:uri req))))

(defn route [req]
  (let [is-route #(is-route % req)]
    (cond
      (is-route [:get "hello"]) (hello-handler req)
      (is-route [:get "bye"]) (bye-handler req)
      :else not-found-handler)))

(defonce server (atom nil))

(defn stop-server []
  (when-not (nil? @server)
    ;; graceful shutdown: wait 100ms for existing requests to be finished
    ;; :timeout is optional, when no timeout, stop immediately
    (@server :timeout 100)
    (reset! server nil)))

(defn start-server []
  (reset! server (httpkit/run-server #'route {:port 8080}))
  nil)

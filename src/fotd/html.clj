(ns fotd.html
  (:require [hiccup2.core :as h]))


(defn not-found []
  (str (h/html [:h2 "Page not found :("])))

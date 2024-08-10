(ns battleship.engine
  (:require [cljs.reader :refer [read-string]]))

(def canvas (.querySelector js/document "#grid"))



(comment

  (read-string "(+ 1 1)")

  (js/console.log "ernie")

  (js/Promise. (+ 1 1))
  
  *)

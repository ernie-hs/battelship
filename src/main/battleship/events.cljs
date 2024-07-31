(ns battleship.events)

;; js/Event stuff convienience/wrapper

(defprotocol Events
  (listen [this event-name event-fn] "listen for an event on this object")
  (dispatch [this event-name] [this event-name event-detail] "dispatch a js/CustomEvent to this object"))

(extend-type js/EventTarget
  Events
  (listen [this event-name event-fn]
    (.addEventListener this event-name event-fn))
  (dispatch
    ([this event-name]
     (.dispatchEvent this (js/CustomEvent. event-name)))
    ([this event-name event-detail]
     (.dispatchEvent this (js/CustomEvent. event-name (js-obj "detail" event-detail))))))

(comment

  (def canvas (.querySelector js/document "#grid"))

  canvas

  (listen canvas "ernie" (fn [e] (js/console.log "ernie " (.-detail e))))

  (dispatch canvas "ernie" {:ernie 9})

  *)

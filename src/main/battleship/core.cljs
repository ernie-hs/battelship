(ns battleship.core
  (:require [three :as t]
            [battleship.three :as bst]
            [battleship.events :as e]
            ["three/addons/controls/ArcballControls.js" :refer [ArcballControls]]
            ["three/addons/geometries/TextGeometry.js" :refer [TextGeometry]]))

;; the game

(defonce *game
  (atom {:assets {:boat {:type :model
                         :src "models/ship.glb"}
                  :font1 {:type :font
                          :src "fonts/helvetiker_bold.typeface.json"}
                  :font2 {:type :font
                          :src "fonts/helvetiker_regular.typeface.json"}}}))

(defn window-resize []
  (let [renderer (:renderer @*game)
        camera (:camera @*game)
        w (.-innerWidth js/window)
        h (.-innerHeight js/window)
        a (/ w h)]
    (.setPixelRatio renderer (.-devicePixelRatio js/window))
    (.setSize renderer w h)
    (set! (.-aspect camera) a)
    (.updateProjectionMatrix camera)))

(defn init-game
  [e]
  (let [canvas (:canvas (.-detail e))]
    (js/console.log "init-game")
    (try
      (let [renderer (t/WebGLRenderer. (js-obj "canvas" canvas "antialias" true))
            camera (t/PerspectiveCamera. 45 1.3 0.1 1000)]
        (swap! *game assoc
               :canvas canvas
               :renderer renderer
               :camera camera)
        (e/dispatch js/window "resize")
        (e/dispatch canvas "intro-screen"))
      (catch js/Error e
        (e/dispatch canvas "error-screen" {:error e})))))
  
(defn intro-screen
  []
  (js/console.log "intro-screen"))

(defn player-select-screen
  []
  (js/console.log "player-select-screen"))

(defn game-screen
  []
  (js/console.log "game-screen"))

(defn winner-screen
  []
  (js/console.log "winner-screen"))

(defn looser-screen
  []
  (js/console.log "looser-screen"))

(defn error-screen
  [e]
  (js/console.log "error-screen")
  (js/console.log "error " e))

(defn register-listeners
  [canvas]
  (js/console.log "registering listeners")
  (e/listen js/window "resize" window-resize)
  (e/listen canvas "init-game" init-game)
  (e/listen canvas "intro-screen" intro-screen)
  (e/listen canvas "player-select-screen" player-select-screen)
  (e/listen canvas "game-screen" game-screen)
  (e/listen canvas "winner-screen" winner-screen)
  (e/listen canvas "looser-screen" looser-screen)
  (e/listen canvas "error-screen" error-screen))
 
(defn un-register-listeners
  [canvas]
  (js/console.log "unregistering listeners")
  (e/un-listen js/window "resize" window-resize)
  (e/un-listen canvas "init-game" init-game)
  (e/un-listen canvas "intro-screen" intro-screen)
  (e/un-listen canvas "player-select-screen" player-select-screen)
  (e/un-listen canvas "game-screen" game-screen)
  (e/un-listen canvas "winner-screen" winner-screen)
  (e/un-listen canvas "looser-screen" looser-screen)
  (e/un-listen canvas "error-screen" error-screen))  

;; shadow-cljs stuff

(defn init []
  (let [canvas (.querySelector js/document "#grid")]
    (js/console.log "init")
    (register-listeners canvas)
    (e/dispatch canvas "init-game" {:canvas canvas})))

(defn start []
  (let [canvas (.querySelector js/document "#grid")]
    (js/console.log "start")
    (register-listeners canvas)
    (e/dispatch canvas "init-game" {:canvas canvas})))

(defn stop []
  (let [canvas (.querySelector js/document "#grid")]
    (js/console.log "stop")
    (un-register-listeners canvas)))
 
(comment

  *)

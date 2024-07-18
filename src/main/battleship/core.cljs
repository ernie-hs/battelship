(ns battleship.core
  (:require [three :as t]
            ["three/addons/controls/ArcballControls.js" :refer [ArcballControls]]
            [battleship.utils :as u]))

(defn get-window-dims
 "get the dimensions of js/window"
  []
  (let [width (.-innerWidth js/window)
        height (.-innerHeight js/window)]
    {:width width :height height :aspect (/ width height)}))

(defn on-resize-window-builder [renderer camera control]
  (fn []
    (let [d (get-window-dims)]
      (.setPixelRatio renderer (.-devicePixelRatio js/window))
      (.setSize renderer (:width d) (:height d))
      (set! (.-aspect camera) (:aspect d))
      (.updateProjectionMatrix camera)
      (.update control))))

;; globals

(def canvas (.querySelector js/document "#grid"))
(def renderer (t/WebGLRenderer. (js-obj "canvas" canvas) "antialias" true))
(set! (.-enabled (.-shadowMap renderer)) true)
(def camera (t/PerspectiveCamera. 45 1.3 0.1 1000))
(def scene (t/Scene.))
(def control (ArcballControls. camera canvas scene))

(.set (.-position camera) 0 5 10)
(.lookAt camera 0 0 0)
(.update control)

;; handlers

(.addEventListener js/window "resize" (on-resize-window-builder renderer camera control))

;; do something

(def light (t/PointLight. 0xffffff 1000))
(set! (.-castShadow light) true)
(.set (.-position light) 10 10 5)
(.add scene light)

(def plane-geometry (t/PlaneGeometry. 10 10))
(.rotateX plane-geometry (/ js/Math.PI -2))
(def plane-material (t/MeshPhysicalMaterial. (js-obj "color" "cornflowerblue")))
(def plane (t/Mesh. plane-geometry plane-material))
(set! (.-receiveShadow plane) true)
(.add scene plane)

(def grid (t/GridHelper. 10 10))
(.set (.-position grid) 0 0.01 0)
(.add scene grid)

(def geometry (t/BoxGeometry. 1 1 1))
(def material (t/MeshPhysicalMaterial. (js-obj "color" "orange")))
(def box (t/Mesh. geometry material))
(set! (.-castShadow box) true)
(set! (.-receiveShadow box) true)
(.set (.-position box) 2 2 0)

(def group (t/Group.))
(.add group box)
(.add scene group)

(defn animation
  "animation loop used by renderer, do and draw stuff here"
  []
  (let [y (.-y (.-rotation box))
        z (.-z (.-rotation box))
        y-group (.-y (.-rotation group))]
    (set! (.-y (.-rotation box)) (+ 0.01 y))
    (set! (.-z (.-rotation box)) (+ 0.01 z))
    (set! (.-y (.-rotation group)) (+ 0.01 y-group))
    (.render renderer scene camera)))

;; shadow stuff

(defn init []
  (.setAnimationLoop renderer animation)
  (.dispatchEvent js/window (js/Event. "resize"))
  (js/console.log "init"))

(defn start []
  (js/console.log "start"))

(defn stop []
  (js/console.log "stop"))

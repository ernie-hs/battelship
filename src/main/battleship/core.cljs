(ns battleship.core
  (:require [three :as t]
            ["three/addons/controls/ArcballControls.js" :refer [ArcballControls]]
            ["three/addons/loaders/FontLoader.js" :refer [FontLoader]]
            ["three/addons/loaders/GLTFLoader.js" :refer [GLTFLoader]]
            ["three/addons/geometries/TextGeometry.js" :refer [TextGeometry]]
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

(defn create-text-mesh [font text color]
  (t/Mesh. (TextGeometry. text (js-obj "font" font "size" 10 "depth" 1))
           (t/MeshPhysicalMaterial. (js-obj "color" color))))

;; globals
(set! (.-enabled t/Cache) true)
(def canvas (.querySelector js/document "#grid"))
(def renderer (t/WebGLRenderer. (js-obj "canvas" canvas "antialias" true)))
(def camera (t/PerspectiveCamera. 45 1.3 0.1 2000))
(def scene (t/Scene.))
(set! (.-background scene) (t/Color. "lightblue"))
(def control (ArcballControls. camera canvas scene))

(def *game-state (atom {}))

(.set (.-position camera) 2 7 20)
(.lookAt camera 0 0 0)
(.update control)

;; handlers

(.addEventListener js/window "resize" (on-resize-window-builder renderer camera control))

;; do something

(def light (t/PointLight. 0xffffff 80000))
(.set (.-position light) 100 100 5)
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

(defn animation
  "animation loop used by renderer, do and draw stuff here"
  []
  (.render renderer scene camera))
  
;; shadow-cljs stuff

(defn init []
  (js/console.log "init")
  (.setAnimationLoop renderer animation)
  (.dispatchEvent js/window (js/Event. "resize")))

(defn start []
  (js/console.log "start"))

(defn stop []
  (js/console.log "stop"))


(comment

  (object? (js-obj "ernie" 1))
  (object? (FontLoader.))


  *)

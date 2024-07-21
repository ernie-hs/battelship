(ns battleship.core
  (:require [three :as t]
            ["three/addons/controls/ArcballControls.js" :refer [ArcballControls]]
            ["three/addons/loaders/FontLoader.js" :refer [FontLoader]]
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

(defn create-text-mesh [font text]
  (def geometry (TextGeometry. text (js-obj "font" font
                                            "size" 7
                                            "depth" 2
                                            "curveSegments" 4
                                            "bevelThickness" 0.1
                                            "bevelSize" 1.5
                                            "bevelEnabled" false)))
  (def materials [(t/MeshPhongMaterial. (js-obj "color" 0xff0000
                                                "flatShading" true))
                  (t/MeshPhongMaterial. (js-obj "color" 0x00ff00))])
  (def mesh (t/Mesh. geometry materials))
  (.computeBoundingBox geometry)
  (js/console.log (.-boundingBox geometry))
  (u/pos mesh 0 0 0)
  (u/rot mesh 0 0 0)
  mesh)


;; globals

(def canvas (.querySelector js/document "#grid"))
(def renderer (t/WebGLRenderer. (js-obj "canvas" canvas) "antialias" true))
(set! (.-enabled (.-shadowMap renderer)) true)
(def camera (t/PerspectiveCamera. 45 1.3 0.1 1000))
(def scene (t/Scene.))
(def control (ArcballControls. camera canvas scene))
(def *assets (atom {}))

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

;;(def dir-light (t/DirectionalLight. 0xffffff 1))
;;(.add scene dir-light)

(def plane-geometry (t/PlaneGeometry. 10 10))
(.rotateX plane-geometry (/ js/Math.PI -2))
(def plane-material (t/MeshPhysicalMaterial. (js-obj "color" "cornflowerblue")))
(def plane (t/Mesh. plane-geometry plane-material))
(set! (.-receiveShadow plane) true)
(.add scene plane)

(def font-loader (FontLoader.))
(.load font-loader "fonts/helvetiker_bold.typeface.json"
       (fn [font]
         (let [mesh (create-text-mesh font "ernie")]
           (.add scene mesh)))
       (fn [xhr] (js/console.log xhr " " (* (/ (.-loaded xhr) (.-total xhr)) 100) " % loaded"))
       (fn [err] (js/console.log "you suck!")))
         
(def grid (t/GridHelper. 10 10))
(.set (.-position grid) 0 0.01 0)
(.add scene grid)

(defn animation
  "animation loop used by renderer, do and draw stuff here"
  []
  (.render renderer scene camera))

;; shadow-cljs stuff

(defn init []
  (.setAnimationLoop renderer animation)
  (.dispatchEvent js/window (js/Event. "resize"))
  (js/console.log "init"))

(defn start []
  (js/console.log "start"))

(defn stop []
  (js/console.log "stop"))

(ns battleship.three
  (:require [clojure.spec.alpha :as s]
            [three :as t]
            ["three/addons/loaders/FontLoader.js" :refer [FontLoader]]
            ["three/addons/loaders/GLTFLoader.js" :refer [GLTFLoader]]))
             
;; three.js convienience/wrappers

(defprotocol Object3D
  (pos [this x y z] "set the position of the Object3D")
  (rot [this x y z] "set the rotation of the Object3D")
  (scale [this x y z] "set the scale of the Object3D"))

(extend-type t/Object3D
  Object3D
  (pos [this x y z]
    (.set (.-position this) x y z))
  (rot [this x y z]
    (.set (.-rotation this) x y z))
  (scale [this x y z]
    (.set (.-scale this) x y z)))


;; async. asset loading

(s/def :bs.asset/type #{:font :model})
(s/def :bs.asset/loader (fn [x] (instance? t/Loader x)))

;; default fn's for manager

(defn fn-on-start
  [url loaded total]
  (js/console.log "start loading file " url " loaded " loaded " total " total))

(defn fn-on-progress
  [url loaded total]
  (js/console.log "loading file " url " loaded " loaded " total " total))

(defn fn-on-error
  [url]
  (js/console.log "error loading file " url))

(defn get-manager
  [fn-on-load]
  (let [manager (t/LoadingManager.)]
    (set! (.-onLoad manager) fn-on-load)
    (set! (.-onStart manager) fn-on-start)
    (set! (.-onProgress manager) fn-on-progress)
    (set! (.-onError manager) fn-on-error)
    manager))

(defn get-loader
  [type manager]
  {:pre [(s/valid? :bs.asset/type type)]
   :post [(s/valid? :bs.asset/loader %)]}
  (type {:font (FontLoader. manager)
         :model (GLTFLoader. manager)}))

(def get-loader-memo (memoize get-loader))

(defprotocol Loader
  (load [this url fn-success] [this url fn-success fn-monitor fn-error]))

(extend-type t/Loader
  Loader
  (load ([this url fn-success]
         (.load this url fn-success))
        ([this url fn-success fn-monitor fn-error]
         (.load this url fn-success fn-monitor fn-error))))

(defn load-all [*game fn-on-load]
  (let [manager (get-manager fn-on-load)]
    (doseq [[name asset] (:assets @*game)]
      (let [loader (get-loader-memo (:type asset) manager)]
        (load loader
              (:src asset)
              (fn [obj]
                (swap! *game assoc-in [:assets name] (assoc asset :obj obj)))))))
  *game)

(comment

 (def *stuff (atom {:boat {:type :model
                           :src "models/ship.glb"}
                    :font1 {:type :font
                            :src "fonts/helvetiker_bold.typeface.json"}
                    :font2 {:type :font
                            :src "fonts/helvetiker_regular.typeface.jso"}}))

 (:font1 @*stuff)
 
 (load-all *stuff (fn [] (js/console.log "all done!")))
  
 (:font (:font "ernie"))
 
 (get-loader :model)
 
 (instance? t/Loader (FontLoader.))
  
 t/Loader
  
 (def s (t/Scene.))

 (pos s 1 2 3)

 (.-y (.-position s)
  
  *))

(ns battleship.assets
  (:require [clojure.spec.alpha :as s]
            ["three/addons/loaders/FontLoader.js" :refer [FontLoader]]
            ["three/addons/loaders/GLTFLoader.js" :refer [GLTFLoader]]))

;; asset spec

(s/def :bs.asset/type #{:font :model})
(s/def :bs.asset/name keyword?)
(s/def :bs.asset/src string?)
(s/def :bs/asset
  (s/keys :req-un [:bs.asset/name :bs.asset/type :bs.asset/src]))
(s/def :bs/assets (s/coll-of :bs/asset))

;; load an asset based upon it's type

(defn loader-from-type
  [type]
  {:pre [(s/valid? :bs.asset/type type)]}
  (type {:font (FontLoader.)
         :model (GLTFLoader.)}))

(defn load
  [asset fn-success fn-monitor fn-error]
  {:pre [(s/valid? :bs/asset asset)
         (s/valid? fn? fn-success)
         (s/valid? fn? fn-monitor)
         (s/valid? fn? fn-error)]}
  (js/console.log "loading asset " (str (:name asset)))
  (.load (loader-from-type (:type asset)) (:src asset) fn-success fn-monitor fn-error))

(defn- success [*assets asset]
  (fn [obj]
    (swap! *assets assoc-in [:items (:name asset)] (assoc asset :obj obj))))

(defn- error [*assets asset]
  (fn [err]
    (swap! *assets assoc :error true)))

(defn load-all
  "load a whole bunch of assets async and return an atom of assets"
  [assets fn-success fn-monitor fn-error]
  {:pre [(s/valid? :bs/assets assets)
         (s/valid? fn? fn-success)
         (s/valid? fn? fn-monitor)
         (s/valid? fn? fn-error)]}
  (let [*assets (atom {:ready false :error false :items {}})]
    (doseq [asset assets]
      (let [fn-s (success *assets asset)
            fn-e (error *assets asset)]
        (load asset fn-s fn-monitor fn-e)))
    *assets))

(comment

  (defn fn-success [f] (js/console.log "success"))
  (defn fn-monitor [x] (js/console.log "monitor" x))
  (defn fn-error [e] (js/console.log "error"))

  (fn? fn-success)
  
  (def my-font-1 {:name :my-font-1 :type :font :src "fonts/gentilis_regular.typeface.json"})
  (def my-font-2 {:name :my-font-2 :type :font :src "fonts/gentilis_bold.typeface.json"})
  (def my-font-3 {:name :my-font-3 :type :font :src "fonts/optimer_bold.typeface.json"})
  (def my-model-1 {:name :my-model-1 :type :model :src "models/ship.glb"})

  my-font-1

  (:name my-font-1)
  
  (loader-from-type (:type my-font-1))

  (loader-from-type (:type my-model-1))
  
  (load my-font1 fn-success fn-monitor fn-error)
 
  (def my-rubbish {:name :y-rubbish :type :font :src "rubbish"})

  my-rubbish
  
  (def my-assets [my-font-1 my-font-2 my-font-3 my-model-1 my-rubbish])

  my-assets

  (def *assholes (load-all my-assets fn-success fn-monitor fn-error))

  *assholes
  
  (def *asses (atom {:ready false :error false :items {}}))

  *asses

  (swap! *asses assoc-in [:items (:name my-font-1)] (assoc my-font-1 :obj (js/Object.)))
  
  (def s (success *asses my-font-1))

  *asses
  
  (s "ernie")
  
  
  *)

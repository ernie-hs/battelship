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
  [asset fn-success fn-error]
  {:pre [(s/valid? :bs/asset asset)
         (s/valid? fn? fn-success)
         (s/valid? fn? fn-error)]}
  (.load (loader-from-type (:type asset)) (:src asset) fn-success nil fn-error))

(defn- success
  [*assets asset]
  (fn [obj]
    (swap! *assets assoc-in [:items (:name asset)] (assoc asset :obj obj))
    (when (= (:count @*assets) (count (filter :obj (vals (:items @*assets)))))
      (swap! *assets assoc :ready true))))

(defn- error
  [*assets asset]
  (fn [err]
    (swap! *assets assoc :error true)))

(defn load-all
  "load a whole bunch of assets async and return an atom of assets"
  [assets]
  {:pre [(s/valid? :bs/assets assets)]}
  (let [*assets (atom {:ready false :error false :count (count assets) :items {}})]
    (doseq [asset assets]
      (load asset (success *assets asset) (error *assets asset)))
    *assets))

(comment

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
  
  (def my-assets [my-font-1 my-font-2 my-font-3 my-model-1])

  my-assets

  (:name (first my-assets))
  
  (def my-rubbish-assets (conj my-assets my-rubbish))

  my-rubbish-assets

  (def *a (load-all my-assets))

  *a

  (reset! *a update :ready true)

  (assoc @*a :ready true)
  
  (count (filter :obj (vals (:items @*a))))
  
  (def *b (load-all my-rubbish-assets))

  *b
  
  *)

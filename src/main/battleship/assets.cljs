(ns battleship.assets
  (:require [clojure.spec.alpha :as s]))

;; asset spec

(s/def :bs.asset/type #{:font :model :image})
(s/def :bs.asset/name keyword?)
(s/def :bs.asset/src string?)
(s/def :bs.asset/obj some?)
(s/def :bs/asset
  (s/keys :req-un [:bs.asset/type :bs.asset/src]
          :opt-un [:bs.asset/obj]))
(s/def :bs.assets/ready boolean?)
(s/def :bs.assets/items (s/and not-empty (s/map-of :bs.asset/name :bs/asset)))
(s/def :bs/assets
  (s/keys :req-un [:bs.assets/ready :bs.assets/items]))
(s/def :bs.assets/loaders (s/and not-empty (s/map-of :bs.asset/type some?)))

;; loader TODO sort this out

(defn load-assets
  [items loaders]
  {:pre [(s/valid? :bs.assets/items items)
         (s/valid? :bs.assets/loaders loaders)]
   :post [(s/valid? :bs/assets @%)]}
  (let [*assets (atom {:ready false :items items})]
    (doseq [[name asset] (:items @*assets)]
      (.load ((:type asset) loaders) (:src asset)
             (fn [loaded]
               (swap! *assets assoc-in [:items name :obj] loaded)
               (when (= (count (:items @*assets)) (count (filter :obj (vals (:items @*assets)))))
                 (swap! *assets assoc :ready true)))))
    *assets))

(comment

  (nil? 1)
  
  (def asset {:type :cheese :src "cheddar.js"})

  (let [[type src] (vals asset)]
    (js/console.log type " " src))
  
  (def items {:my-font1 {:type :font :src "fonts/gentilis_bold.typeface.json"}
              :my-font2 {:type :font :src "fonts/gentilis_regular.typeface.json"}
              :my-model {:type :model :src "models/ship.glb"}})


  (s/valid? :bs.assets/loaders {:font (fn [x y])})
  
  @*r

  (swap! *r assoc :ready true)
  
  (= (count (:items @*r))
     (count (filter :obj (vals (:items @*r)))))
  
  (filter :obj (vals (:items @*r)))
  
  *)

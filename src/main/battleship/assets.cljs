(ns battleship.assets
  (:require [clojure.spec.alpha :as s]
            ["three/addons/loaders/FontLoader.js" :refer [FontLoader]]))

;; asset spec

(s/def :bs.asset/type #{:font :model :image})
(s/def :bs.asset/name keyword?)
(s/def :bs.asset/src string?)
(s/def :bs.asset/obj object?)
(s/def :bs/asset
  (s/keys :req-un [:bs.asset/type :bs.asset/src]
          :opt-un [:bs.asset/obj]))
(s/def :bs.assets/ready boolean?)
(s/def :bs.assets/items (s/map-of :bs.asset/name :bs/asset))
(s/def :bs/assets
  (s/keys :req-un [:bs.assets/ready :bs.assets/items]))

;; assets loader

(defn load-assets
  [items]
  {:pre [(s/valid? :bs.assets/items items)]
   :post [(s/valid? :bs/assets @%)]}
  (let [*assets (atom {:ready false :items items})
        font-loader (FontLoader.)]
    (doseq [k (keys (:items @*assets))]
      (.load font-loader (-> @*assets :items k :src)
             (fn [font]
               (js/console.log "loaded " k)
               (swap! *assets assoc-in [:items k :obj] font))))
    *assets))

(comment

  (def items {:my-font1 {:type :font :src "fonts/gentilis_bold.typeface.json"}
              :my-font2 {:type :font :src "fonts/gentilis_regular.typeface.json"}})


  (def *r (load-assets items))

  @*r

  (def *e (atom {:ernie :cheese :stuff {:eggs {:name "ernie"}
                                        :roger {:name "babs"}}}))

  @*e

  (map :name (vals (:stuff @*e)))
  
  (doseq [k (keys (:stuff @*e))]
    (swap! *e assoc-in [:stuff k :fart] 2))

  @*e
  
  *)

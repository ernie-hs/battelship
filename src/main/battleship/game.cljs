(ns battleship.game
  (:require [clojure.spec.alpha :as s]))

(defn int-gt-zero? [x] (and int? (> x 0)))
(defn int-ge-zero? [x] (and int? (>= x 0)))

;; position spec

(s/def :bs.pos/x int-ge-zero?)
(s/def :bs.pos/y int-ge-zero?)
(s/def :bs/pos
  (s/keys :req-un [:bs.pos/x :bs.pos/y]))

;; rect spec

(s/def :bs.rect/w int-gt-zero?)
(s/def :bs.rect/h int-gt-zero?)
(s/def :bs/rect
  (s/keys :req-un [:bs.rect/w :bs.rect/h]))

;; grid spec

(defn correct-data-size-for-grid? [grid]
  (= (* (-> grid :rect :w) (-> grid :rect :h)) (count (:data grid))))

(s/def :bs.grid/data vector?)
(s/def :bs/grid
  (s/and correct-data-size-for-grid?
         (s/keys :req-un [:bs/rect :bs.grid/data])))

;; player spec

(s/def :bs.player/name string?)
(s/def :bs.player/type #{:human :computer :gabor})
(s/def :bs.player/hits int-ge-zero?)
(s/def :bs.player/misses int-ge-zero?)
(s/def :bs.player/remaining-ships int-ge-zero?)
(s/def :bs/player
  (s/keys :req-un [:bs.player/name :bs.player/type :bs.player/hits :bs.player/misses :bs.player/remaining-ships :bs/grid]))

;; ship spec

(s/def :bs.ship/v int?)
(s/def :bs/ship
  (s/keys :req-un [:bs/rect :bs/v]))

;; game spec

(defn non-empty-vector? [v] (and (vector? v) (> (count v) 1)))
                                  
(s/def :bs.game/ready boolean?)
(s/def :bs.game/turn int-ge-zero?)
(s/def :bs.game/ships int-gt-zero?)
(s/def :bs.game/players (s/and non-empty-vector? (s/coll-of :bs/player)))
(s/def :bs/game
  (s/keys :req-un [:bs.game/ready :bs.game/turn :bs.game/players]))

;; helper fn's

(defn- create-grid
  "create an empty grid, w x h and initialised with v"
  [w h v]
  {:pre [(s/valid? :bs.rect/w w)
         (s/valid? :bs.rect/h h)]
   :post [(s/valid? :bs/grid %)]}
  {:rect {:w w :h h} :data (vec (replicate (* w h) v))})

(defn- calc-idx-from-pos [grid pos]
  {:pre [(s/valid? :bs/grid grid)
         (s/valid? :bs/pos pos)]
   :post [(s/valid? int-ge-zero? %)]}
  (+ (:x pos) (* (-> grid :rect :w) (:y pos))))

(defn- set-grid
  "set the grid values to v, for a given position and area"
  [grid pos area v]
  {:pre [(s/valid? :bs/grid grid)
         (s/valid? :bs/pos pos)
         (s/valid? :bs/rect area)
         (s/valid? int? v)]
   :post [(s/valid? :bs/grid %)]}
  (assoc grid :data (assoc (:data grid) (calc-idx-from-pos grid pos) v)))


;; game stuff

(defn create-game
  "create a new game"
  [players starting-ship-count grid-dimensions]
  {:pre [(s/valid? int-gt-zero? starting-ship-count)
         (s/valid? :bs/rect grid-dimensions)]
   :post [(s/valid? :bs/game %)]}
  (let [w (:w grid-dimensions)
        h (:h grid-dimensions)]
    {:ready false
     :turn 0
     :ships starting-ship-count
     :players (mapv (fn [p]
                      (assoc p
                             :grid (create-grid w h 0)
                             :hits 0
                             :misses 0
                             :remaining-ships 0)) players)}))

(defn place-ship
  "place a ship on the player grid"
  [game player-idx pos ship]
  {:pre [(s/valid? :bs/game game)
         (s/valid? int-ge-zero? player-idx)
         (s/valid? :bs/ship ship)
         (s/valid? #(false? (:ready %)) game)]
   :post [(s/valid? :bs/game %)]}
  (let [player (nth (:players game) player-idx)
        ships (:ships game)
        updated-grid (set-grid (:grid player) pos (:rect ship) (:v ship))
        updated-game (assoc game :players
                            (assoc (:players game) player-idx
                                   (assoc player
                                          :grid updated-grid
                                          :remaining-ships (inc (:remaining-ships player)))))]
    (assoc updated-game
           :ready (apply = ships (map :remaining-ships (:players updated-game))))))


(comment

  (s/valid? false? false)
  
  (s/valid? :bs/ship {:rect {:w 1 :h 1} :v 2})
  
  (create-grid 1 1 1)

  (s/valid? :bs/rect
            {:x 1
             :y :d})
  
  (s/explain :bs/grid
             {:rect {:w 2
                     :h 2}
              :data [1 1 1 0]})

  (s/explain :bs/player
             {:name -1
              :type :gabor
              :hits 0
              :misses 0
              :remaining-ships 0
              :grid {:w 1
                     :h 1
                     :data []}})
  

  (create-game [] 2 {:w 1 :h 1})
  
  (apply = 2 [2 2 2 2])
  
  (def game (atom (create-game [{:name "ernie" :type :human} {:name "computer" :type :computer}] 3 {:w 3 :h 3})))

  @game

  (reset! game (place-ship @game 0 {:x 1 :y 2} {:rect {:w 1 :h 1} :v 2}))
  
  (reset! game (place-ship @game 1 {:x 0 :y 0} {:rect {:w 1 :h 1} :v 2}))
  
  *)

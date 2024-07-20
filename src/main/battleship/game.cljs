(ns battleship.game
  (:require [clojure.spec.alpha :as s]))

(defn int-gt-zero? [x] (and int? (> x 0)))
(defn int-ge-zero? [x] (and int? (>= x 0)))

;; rect spec

(s/def :bs.rect/w int-gt-zero?)
(s/def :bs.rect/h int-gt-zero?)

;; grid spec

(s/def :bs.grid/data vector?)
(s/def :bs/grid
  (s/keys :req-un [:bs/rect :bs.grid/data]))

;; player spec

(s/def :bs.player/name string?)
(s/def :bs.player/type #{:human :computer :gabor})
(s/def :bs.grid/hits int-ge-zero?)
(s/def :bs.grid/misses int-ge-zero?)
(s/def :bs.grid/remaining-ships int-ge-zero?)
(s/def :bs/player
  (s/keys :req-un [:bs/name :bs.player/type :bs/turns :bs/hits :bs/misses :bs/remaining-ships :bs/grid]))

;; ship spec

(s/def :bs.ship/v int?)
(s/def :bs/ship
  (s/keys :req-un [:bs/rect :bs/v]))

;; game spec
(s/def :bs.game/ship-count (s/and int? #(> % 0)))
(s/def :bs.game/ready boolean?)
(s/def :bs.game/turn int-ge-zero?)
(s/def :bs.game/players (s/and vector? #(> (count %) 1)))
(s/def :bs/game
  (s/keys :req-un [:bs.game/ship-count :bs.game/ready :bs.game/turn :bs.game/players]))

;; helper fn's

(defn- create-grid
  "create an empty grid, w x h and initialised with v"
  [w h v]
  {:pre [(s/valid? :bs.rect/w w)
         (s/valid? :bs.rect/h h)]
   :post [(s/valid? :bs/grid %)]}
  {:rect {:w w :h h} :data (vec (replicate (* w h) v))})

(defn- calc-idx-from-xy [grid x y]
  {:pre [(s/valid? :bs/grid grid)
         (s/valid? int-ge-zero? x)
         (s/valid? int-ge-zero? y)]
   :post [(s/valid? int-ge-zero? %)]}
  (+ x (* (:w grid) y)))

(defn- set-grid
  "set the grid values to v, for a given position and area"
  [grid position area v]
  (assoc grid :data (assoc (:data grid) (calc-idx-from-xy grid (:x position) (:y position)) v)))

;; game stuff

(defn create-game
  "create a new game"
  [players ship-count grid-dimensions]
  {:pre [(s/valid? :bs.game/players players)]
   :post [(s/valid? :bs/game %)]}
  (let [w (:w grid-dimensions)
        h (:h grid-dimensions)]
    {:ship-count ship-count
     :ready false
     :turn 0
     :players (mapv (fn [p]
                      (assoc p
                             :turns 0
                             :grid (create-grid w h 0)
                             :hits 0
                             :misses 0
                             :remaining-ships 0)) players)}))

(defn place-ship
  "place a ship on the player grid"
  [game player-idx position ship]
  (let [p (nth (:players game) player-idx)
        ship-count (:ship-count game)
        updated-grid (set-grid (:grid p) position (:area ship) (:v ship))
        updated-game (assoc game :players
                            (assoc (:players game) player-idx
                                   (assoc p
                                          :grid updated-grid
                                          :remaining-ships (inc (:remaining-ships p)))))]
    (assoc updated-game
           :ready (apply = ship-count (map :remaining-ships (:players updated-game))))))


(comment

  (s/explain :bs/grid
             {:w 10
              :h -1
              :data []})

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
  
  (def game (create-game [{:name "ernie" :type :human} {:name "computer" :type :computer}] 3 {:w 3 :h 3}))

  (apply = 0 (map :remaining-ships (:players game)))
  
  game

  (:grid (nth (:players game) 1))
  
  (calc-idx-from-xy (:grid (nth (:players game) 1)) 1 1)

  (assoc [0 0 0 0 0] 2 99)
  
  (set-grid (:grid (nth (:players game) 1)) {:x 1 :y 1} {:w 2 :h 2} 5)
  
  (assoc game :players (:players game))

  (place-ship game 0 {:x 1 :y 1} {:area {:w 1 :h 2} :v 3})


  *)

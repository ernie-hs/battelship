(ns battleship.game)

(defn- create-grid
  "create an empty grid, w x h and initialised with v"
  [w h v]
  (vec (replicate (* w h) v)))

(defn- calc-idx-from-xy [grid x y]
  (+ x (* (:w grid) y)))

(defn- set-grid
  "set the grid values to v, for a given position and area"
  [grid position area v]
  (assoc grid :data (assoc (:data grid) (calc-idx-from-xy grid (:x position) (:y position)) v)))

;; game stuff

(defn create-game
  "create a new game"
  [players ship-count grid-dimensions]
  (let [w (:w grid-dimensions)
        h (:h grid-dimensions)]
    {:player-count (count players)
     :ship-count ship-count
     :ready false
     :turn 0
     :players (mapv (fn [p]
                      (assoc p
                             :turns 0
                             :grid {:w w :h h :data (create-grid w h 0)}
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

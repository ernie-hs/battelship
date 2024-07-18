(ns battleship.game)

(defn create-game
  "create a new game"
  [players ship-count grid-dimensions]
  (let [w (:w grid-dimensions)
        h (:h grid-dimensions)]
    {:player-count (count players)
     :grid-dimensions grid-dimensions
     :ship-count ship-count
     :ready false
     :turn 0
     :players (mapv (fn [p]
                      (assoc p
                             :turns 0
                             :grid (vec (replicate (* w h) 0))
                             :hits 0
                             :misses 0
                             :remaining-ships 0)) players)}))


(defn place-ship
  "place a ship on the player grid"
  [game player position ship]
  game)

(comment

  (def game (create-game [{:name "ernie" :type :human} {:name "computer" :type :computer}] 3 {:w 3 :h 3}))

  *)

(ns pixcell-clj.state)

(def SIZE (+ 2     ;; 2 chars for color & palette
             256)) ;; one char for each cell

;; === helpers ===

;; conversion byte <-> hex string
(defn- byte->hex [b] (let [h (java.lang.Integer/toHexString b)]
                       (if (= 1 (count h))
                         (str "0" h)
                         h)))
(defn- hex->byte [h] (java.lang.Integer/parseInt h 16))

;; convertion: pair of 4bit <-> byte
(defn- pair->byte [[a b]] (+ (* 16 a) b))
(defn- byte->pair [p] (let [b (mod p 16)
                            a (/ (- p b) 16)]
                        [a b]))

;; unpacks the hex-string into array of cells
(defn- hex->cells [h]
  (mapcat (comp byte->pair
                hex->byte
                (partial apply str))
          (partition 2 h)))

;; packs the array of cells into hex-string
(defn- cells->hex [cs]
  (clojure.string/join
   (mapcat (comp byte->hex pair->byte)
           (partition 2 cs))))

;; === API ===

;; initial state (all cells are black)
(def initial
  {:palette 0
   :color 0
   :cells (replicate 256 0)})

(defn encode
  "Encode editor state to string"
  [state]
  (let [{p :palette c :color cs :cells} state]
    (str (->> [p c]
              pair->byte
              byte->hex)
         (cells->hex cs))))

(defn decode
  "Decode editor state from string"
  [state]
  (when (and (= SIZE (count state))
             (every? (set "0123456789abcdef") state))
    (let [[pal col] (->> state
                         (take 2)
                         (apply str)
                         hex->byte
                         byte->pair)
          cells (->> state
                     (drop 2)
                     hex->cells)]
      {:palette pal
       :color col
       :cells cells})))

(defn set-color
  "Sets the current color"
  [state color]
  {:pre [(<= 0 color 15)]}
  (assoc state :color color))

(defn cycle-palette
  "Switches the palette to next"
  [state palette-count]
  {:pre [(pos? palette-count)]}
  (update-in state
             [:palette]
             #(mod (inc %) palette-count)))

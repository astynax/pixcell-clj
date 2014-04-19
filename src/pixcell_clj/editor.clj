(ns pixcell-clj.editor
  (:require [hiccup.core :refer [html]])
  (:require [pixcell-clj.state :refer :all]))

(def PALETTES [;; common
               ["#000000" "#7F007F" "#FF00FF" "#00007F" "#0000FF" "#007F7F" "#00FFFF" "#007F00"
                "#00FF00" "#7F7F00" "#FFFF00" "#7F0000" "#FF0000" "#7F7F7F" "#4F4F4F" "#FFFFFF"]
               ;; HUSL
               ["#000000" "#EA0064" "#C55600" "#9E6E00" "#847800" "#668100" "#008B12" "#008863"
                "#00867C" "#00848F" "#0081A9" "#0071F4" "#AC33FF" "#D300D1" "#E000A2" "#FFFFFF"]
               ;; reds
               ["#000000" "#100408" "#200810" "#300A0F" "#401020" "#501428" "#601830" "#701A38"
                "#802040" "#AA2448" "#BB2850" "#CC2A58" "#DD3060" "#EE3468" "#FF3870" "#FFFFFF"]
               ;; greens
               ["#000000" "#081000" "#102000" "#183000" "#204000" "#285000" "#306000" "#387000"
                "#408000" "#489000" "#50AA00" "#58BB00" "#60CC00" "#68DD00" "#70EE00" "#FFFFFF"]])

(assert (= (count PALETTES) PAL-COUNT))


(defn- seq->tag
  ([tag items]
     (vec (cons tag items)))
  ([tag attrs items]
     (vec (cons tag (cons attrs items)))))

(defn op
  ([op] (str "perform(\"" op "\")"))
  ([op arg] (str "perform(\"" op "\", \"" arg "\")")))

;; seq "item1","item2"... -> seq [0 "item1"],[1 "item2]...
(def enumerate (partial map vector (range)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn grid [palette cells]
  (let [rows (partition 16 (enumerate cells))]
    [:table#grid
     (seq->tag :tbody
               (for [row rows]
                 (seq->tag :tr
                           (for [[idx color] row]
                             [:td {:bgcolor (nth palette color)
                                   :onclick (op "set-cell" idx)}]))))]))

(def header [:h2 "✎ PixCell"])
(def footer [:i "by @alex_pir"])

(def toolbar
  (let [tools [["■ New"  ""]
               ["▧ Fill" "fill"]
               ["░ Rnd"  "rand-fill"]
               ["⇄"      "mirror"]
               ["⇅"      "flip"]
               ["▟"      "simple-clone"]
               ["◕"      "rot-clone"]
               ["▐"      "mirror-clone"]
               ["▄"      "flip-clone"]
               ["⊛"      "cycle-colors"]]]
    [:table.toolbar {:width (* 16 30)}
     [:tbody
      (seq->tag :tr
                (for [[icon op-name] tools]
                  [:td.button {:onclick (op op-name)}
                   (str "&nbsp;" icon "&nbsp;")]))]]))

(defn colorbar [pal col]
  [:table.toolbar {:width (* 16 30)
                   :height 20}
   [:tbody
    (seq->tag :tr
              (cons
               [:td.button
                {:onclick (op "cycle-palette")}
                "⇸"]
               (for [[idx c] (map vector (range) pal)]
                 [(if (= idx col)
                    :td.current-color
                    :td.color)
                  {:style (str "background-color:" c ";")
                   :onclick (op "set-color" idx)}
                  "&nbsp;"]))
              )]])

(def css [:style {:type "text/css"} "
    table#grid, table.toolbar {
       border: 0px;
       padding: 0;
       margin: 0;
       border-spacing: 0;
    }
    table#grid, tr {
       height: 30px;
       padding: 0;
    }
    table#grid td {
       width: 30px;
       padding: 0;
    }
    table.toolbar td.button {
       background-color: #404040;
       color: #FFFFFF;
       cursor: pointer;
       border: 4px outset gray;
       text-align: center;
    }
    table.toolbar td.color,
    table.toolbar td.current-color {
       cursor: pointer;
       border: 4px outset gray;
       padding: 0;
       margin: 0;
    }
    table.toolbar td.current-color {
       border: 4px inset gray;
    }
    "])

(def callback "
  function perform(what, arg) {
    if (!what) {
      var url = '/';
    } else {
      var url = '/?state=' + state + '&op=' + what;
    }
    if (arg) {
      url = url + '&arg=' + arg
    }
    window.location = url;
  };")

(defn ui
  "Editor UI page"
  [state-str op arg]
  (let [old-state (if (nil? state-str) ;; can be empty
                initial
                (or (decode state-str)
                    initial))      ;; can contain errors
        state (perform old-state op arg)
        pal (nth PALETTES (:palette state))
        col (:color state)]
    (html
     [:html
      [:head
       [:title "PixCell by @alex_pir"]
       css
       ;; current state as text
       [:script {:type "text/javascript"}
        (str "var state=\"" (encode state) "\";")
        callback]]
      [:body
       header
       toolbar
       (grid pal (:cells state))
       (colorbar pal col)
       [:br]
       footer]])))

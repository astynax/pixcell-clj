(ns pixcell-clj.editor
  (:require [hiccup.core :refer [html]])
  (:require [pixcell-clj.state :refer :all]))

(def PALETTES [;; common
               ["#000000" "#0000FF" "#00FF00" "#00FFFF"
                "#FF0000" "#FF00FF" "#FFFF00" "#FFFFFF"]
               ;; reds
               ["#000000" "#200810" "#401020" "#601830"
                "#802040" "#BB2850" "#DD3060" "#FF3870"]
               ;; greens
               ["#000000" "#102000" "#204000" "#306000"
                "#408000" "#50BB00" "#60DD00" "#70FF00"]])

(def PAL-COUNT 3)


(defn- seq->tag
  ([tag items]
     (vec (cons tag items)))
  ([tag attrs items]
     (vec (cons tag (cons attrs items)))))


(defn grid [palette cells]
  (let [rows (partition 8 cells)]
    [:table
     {:cellspacing "0"
      :cellpadding"none"}
     (seq->tag :tbody
               (for [row rows]
                 (seq->tag :tr
                           {:height 30}
                           (for [color row]
                             [:td
                              {:bgcolor (nth palette color)
                               :width 30}]))))]))

(def header [:h2 "✎ PixCell"])
(def footer [:i "by @alex_pir"])

(def toolbar
  (let [tools [["■ New"    ""]
               ["▧ Fill"   "fill"]
               ["░ Random" "rand_fill"]
               ["⇄ Mirror" "mirror"]
               ["⇅ Flip"   "flip"]
               ["◰"        "simple_clone"]
               ["◕"        "rot_clone"]
               ["◑"        "mirror_clone"]
               ["◒"        "flip_clone"]]]
    [:table [:tbody
             (seq->tag :tr
                       (for [[icon url] tools]
                         [:td
                          [:div {:class "btn"
                                 :onclick (str "alert(\"" url ")")}
                           (str "&nbsp;" icon "&nbsp;")]]))]]))

(defn colorbar [pal col]
  [:table [:tbody
           (seq->tag :tr
                     (for [c pal]
                       [:td
                        [:div {:class "color"
                               :style (str "background-color:" c ";")}
                         "&nbsp;"]]))]])

(def css [:style {:type "text/css"} "
    div.btn {
       background-color:#404040;
       color:#FFFFFF;
       cursor:pointer;
       border:4px outset gray;
       text-align:center;
    }
    div.color {
       width:30px;
       height:30px;
       cursor: pointer;
       border: 4px;
    }
    "])

(defn ui
  "Editor UI page"
  [state-str op]
  (let [state (if (nil? state-str) ;; can be empty
                initial
                (or (decode state-str)
                    initial))      ;; can contain errors
        pal (nth PALETTES (:palette state))
        col (:color state)]
    (html
     [:html
      [:head
       [:title "PixCell by @alex_pir"]
       css
       ;; current state as text
       [:script {:type "text/javascript"}
        (str "var state=\"" (encode state) "\";")]]
      [:body
       header
       toolbar
       (grid pal (:cells state))
       (colorbar pal col)
       [:br]
       footer]])))

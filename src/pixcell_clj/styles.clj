(ns pixcell-clj.styles
  (:require [garden.core :as g]
            [garden.units :as u]
            [garden.selectors :as s]))

(def styles
  (g/css
   [:.header {:margin-bottom (u/px 5)}]

   [:.footer
    {:margin-top (u/px 5)
     :width (u/px 480)
     :font-style :italic
     :text-align :right}

    [[:.nick {:font-weight :bold}]]]

   [:table.grid
    :table.toolbar
    {:border (u/px 0)
     :padding 0
     :margin 0
     :border-spacing 0}]

   [:table.grid
    [[:tr
      {:height (u/px 30)
       :padding 0}]

     [:td
      {:width (u/px 30)
       :padding 0}]]]

   [:table.toolbar
    [[:td.button
      {:background-color "#404040"
       :color "#FFFFFF"
       :cursor :pointer
       :border "4px outset gray"
       :text-align :center}]

     [:td.color
      :td.current-color
      {:cursor :pointer
       :border "4px outset gray"
       :padding 0
       :margin 0}]

     [:td.current-color
      {:border "4px inset gray"}]]]))

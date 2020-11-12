(ns mkical.core
  (:gen-class))

(use 'clj-ical.format)
(use 'joda-time)

(def month-indexes {"Juni" 6, "Mei" 5, "April" 4, "Februari" 2, "Maart" 3, "September" 9, "November" 11, "Oktober" 10, "Juli" 7, "Januari" 1, "Augustus" 8, "December" 12})

(defn md-to-map 
  [mdfile]
  (with-open [rdr (clojure.java.io/reader mdfile)]
    (dissoc (second (reduce (fn [heading_map_tuple line_tuple]
              (let [[keyheading, mdmap] heading_map_tuple 
                    [l1, l2] line_tuple
                    updated_lines (conj (get mdmap keyheading) l1)]
                (cond
                  (clojure.string/starts-with? l2 "===") [l1, mdmap] ; new key heading
                  (clojure.string/starts-with? l1 "===")[keyheading, mdmap] ; ignore
                  :else [keyheading, (assoc mdmap keyheading updated_lines)] ; add lines
                  )))
            [:noheader, {:noheader []}]
            (partition 2 1 (line-seq rdr))))
            :noheader)))

(defn moestuin
  [year]
  (let [mdmap (md-to-map "moestuin.md")]
  (with-open [fwriter (clojure.java.io/writer (str "moestuin" year ".ics"))]
    (write-ical fwriter
      (concat [:vcalendar] 
       (map (fn [month]
              [:vevent 
               [:uid (java.util.UUID/randomUUID)]
               [:summary (str "Moestuin voor " month)]
               ; allday
               [:dtstart {:value "date"} (date-time year (get month-indexes month) 1)]
               [:dtend {:value "date"} (date-time year (get month-indexes month) 2)]
               [:description (clojure.string/join "\\n" (reverse (get mdmap month)))]
               ])
            (keys mdmap))
       )))))

(defn moestuin
  [year]
  (let [mdmap (md-to-map "moestuin.md")]
  (with-open [fwriter (clojure.java.io/writer (str "moestuin" year ".ics"))]
    (write-ical fwriter
      (concat [:vcalendar] 
       (map (fn [month]
              [:vevent 
               [:uid (java.util.UUID/randomUUID)]
               [:summary (str "Moestuin voor " month)]
               ; allday
               [:dtstart {:value "date"} (date-time year (get month-indexes month) 1)]
               [:dtend {:value "date"} (date-time year (get month-indexes month) 2)]
               [:description (clojure.string/join "\\n" (reverse (get mdmap month)))]
               ])
            (keys mdmap))
       )))))


(defn -main 
  [& args]
  ; (moestuin 2021)
  )

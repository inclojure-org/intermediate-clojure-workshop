(ns icw.data.process
  (:require [clojure.string :as cs]
            [icw.java-interop.jdbc :as jdbc]
            [clojure.test.check.generators :as gen]))


;; Reading and processing data from resources/data/albumlist.csv

(defonce album-source (clojure.java.io/reader "resources/data/albumlist.csv"))

(comment
  (first (line-seq album-source)))

;; Parsing

(defn parse-line
  "Input
  line -> \"1,1967,Sgt. Pepper's Lonely Hearts Club Band,The Beatles,Rock,\"Rock & Roll, Psychedelic Rock\"

  Output
  [\"1\"
   \"1967\"
   \"Sgt. Pepper's Lonely Hearts Club BandThe Beatles\"
   \"Rock\"
   [\"Rock & Roll\" \"Psychedelic Rock\"]"
  [line])


(comment
  (= (parse-line "1,1967,Sgt. Pepper's Lonely Hearts Club Band,The Beatles,Rock,\"Rock & Roll, Psychedelic Rock\"")
     ["1"
      "1967"
      "Sgt. Pepper's Lonely Hearts Club BandThe Beatles"
      "Rock"
      ["Rock & Roll"
       "Psychedelic Rock"]]))

(comment
  (map parse-line (line-seq album-source)))


(defn line-vec->line-map
  "xs -> [\"1\"
          \"1967\"
          \"Sgt. Pepper's Lonely Hearts Club BandThe Beatles\"
          \"Rock\"
          [\"Rock & Roll\"
           \"Psychedelic Rock\"]]
  Output
  {:number \"1\"
   :year \"1967\"
   :album \"Sgt. Pepper's Lonely Hearts Club BandThe Beatles\"
   :genre \"Rock\"
   :subgenre-xs [\"Rock & Roll\"
                 \"Psychedelic Rock\"]}"
  [xs]
  (let [[number year album artist genre subgenre-xs] xs]
    {:number number
     :year year
     :album album
     :genre genre
     :artist artist
     :subgenre-xs subgenre-xs}))

(comment
  (= (line-vec->line-map ["1"
                         "1967"
                         "Sgt. Pepper's Lonely Hearts Club BandThe Beatles"
                         "Rock"
                         ["Rock & Roll"
                          "Psychedelic Rock"]])
     {:number "1"
      :year "1967"
      :album "Sgt. Pepper's Lonely Hearts Club BandThe Beatles"
      :genre "Rock"
      :subgenre ["Rock & Roll"
                 "Psychedelic Rock"]}))

(comment
  (take 10
        (map line-vec->line-map
             (map parse-line
                  (line-seq album-source)))))


(defn source->album-xs
  [source]
  ;; Use source with line-seq to get a lazy sequence
  ;; Use parse-line to convert list of strings to list of vectors
  ;; Use line-vec->line-map to convert list of vectors to list of map
  )


(defn populate-db
  []
  (jdbc/init-db)

  (with-open [rdr (clojure.java.io/reader "resources/data/albumlist.csv")]
    (let [lines (line-seq rdr)
          ;; FIXME!!!
          albums (source->album-xs album-source)]
      (doseq [album albums]
        (jdbc/insert! album)))))


;; Check http://localhost:6789/albums


;; Some more experiments with sequences

(defn find-popular-year
  ;; Find top years for album releases
  [album-xs]
  )

(comment (= (find-popular-year (source->album-xs album-source))
            '(["1970" 26]
              ["1972" 24]
              ["1973" 23]
              ["1969" 22]
              ["1971" 21])))

(defn find-popular-artists
  [album-xs]
  )


(comment (= (find-popular-artists (source->albums-xs album-source))
            '(["The Beatles" 10]
              ["Bob Dylan" 10]
              ["The Rolling Stones" 9]
              ["Bruce Springsteen" 7]
              ["The Who" 7])))

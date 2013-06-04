(ns excel-exporter.excel-reader
  (:use clojure.java.io)
  (:import [org.apache.poi.xssf.usermodel XSSFWorkbook])
  (:import [org.apache.poi.hssf.usermodel HSSFWorkbook])
  (:import [org.apache.poi.ss.usermodel Row Cell DateUtil WorkbookFactory CellStyle Font]))
(defn indexed
  "Returns a lazy sequence of [index, item] pairs, where items come
  from 's' and indexes count up from zero.

  (indexed '(a b c d))  =>  ([0 a] [1 b] [2 c] [3 d])"
  [s]
  (map vector (iterate inc 0) s))

(defn positions
  "Returns a lazy sequence containing the positions at which pred
   is true for items in coll."
  [pred coll]
  (for [[idx elt] (indexed coll) :when (pred elt)] idx))
(defn filter-digits [s]
  (re-find #"[0-9]*" s))
(defn parse-int [s]
  (if (number? s)
    (int s)
    (if (or (empty? s) (empty? (filter-digits s)))
      nil
      (Integer. (filter-digits s)))))
(defn cell-value
  "Return proper getter based on cell-value"
  ([cell] (cell-value cell (.getCellType cell)))
  ([cell cell-type]
    (condp = cell-type
      Cell/CELL_TYPE_BLANK nil
      Cell/CELL_TYPE_STRING (.getStringCellValue cell)
      Cell/CELL_TYPE_NUMERIC (if (DateUtil/isCellDateFormatted cell)
                               (.getDateCellValue cell)
                               (.getNumericCellValue cell))
      Cell/CELL_TYPE_BOOLEAN (.getBooleanCellValue cell)
      Cell/CELL_TYPE_FORMULA {:formula (.getCellFormula cell)}
      Cell/CELL_TYPE_ERROR {:error (.getErrorCellValue cell)}
      :unsupported )))
(defn workbook
  "Create or open new excel workbook. Defaults to xlsx format."
  ([] (new XSSFWorkbook))
  ([input] (WorkbookFactory/create (input-stream input))))
(defn sheets
  "Get seq of sheets."
  [wb] (map #(.getSheetAt wb %1) (range 0 (.getNumberOfSheets wb))))

(defn rows
  "Return rows from sheet as seq.  Simple seq cast via Iterable implementation."
  [sheet] (seq sheet))

(defn cells
  "Return seq of cells from row.  Simpel seq cast via Iterable implementation."
  [row] (seq row))

(defn values
  "Return cells from sheet as seq."
  [row] (map cell-value (cells row)))

(defn col-idx [sheet-description sheet col-symbol]
  (let [title-string (sheet-description col-symbol)
        title-cells (cells (nth (rows sheet) (sheet-description :heading-row-index )))
        title-strings (map cell-value title-cells)
        cell-indices (map #(.getColumnIndex %) title-cells)
        m (zipmap  title-strings cell-indices)
        ]
    (m title-string)))

(defn col [rows idx]
  (map #(nth (cells %) idx) rows)
  )

(defn till-empty [sq]
  (loop [idx -1 sq-rest sq]

    (if (empty? sq-rest)
      idx
      (let [act (first sq-rest)]

        (if (nil? act)
          idx
          (recur (inc idx) (rest sq-rest)))))))


(defn rows-with-number [number-col-fn sheet]
  (filter #(number? (number-col-fn %)) (rows sheet)))

(defn row-by-number-col [number-col-fn sheet number]
  (first (filter #(= (parse-int number) (number-col-fn %))
           (rows-with-number number-col-fn sheet))))

(defn cell-value-of-column [column-idx row]
  (if (nil? row)
    nil
    (let [ cs (cells row)
           cell-indices (map #(.getColumnIndex %) cs)
          m (zipmap cell-indices cs)
          act-cell-vals (map cell-value (cells row))
          act-val (cell-value(m column-idx))
          ]

      (if (number? act-val)
        (int act-val)
        act-val
        )
      ))
  )

(defn vereine [verein-fn startnummer-fn sheet]
  (map #(hash-map :startnummer (startnummer-fn %) :verein (verein-fn %)) (rows sheet)))


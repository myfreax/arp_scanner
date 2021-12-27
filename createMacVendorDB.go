package main

import (
	"database/sql"
	"encoding/csv"
	"fmt"
	"log"
	"net/http"
	"os"

	_ "github.com/mattn/go-sqlite3"
)

func fetchCsv(db *sql.DB) {
	fmt.Println("Loading.... Please wait a montent")
	resp, _ := http.Get("http://standards-oui.ieee.org/oui/oui.csv")
	defer resp.Body.Close()
	r := csv.NewReader(resp.Body)

	records, err := r.ReadAll()
	if err != nil {
		fmt.Println("read error")
		return
	}

	for i := 1; i < len(records); i++ {
		fmt.Println(records[i][1], records[i][2], i)
		write(db, records[i][1], records[i][2])
	}
}

func write(db *sql.DB, mac string, text string) {
	tx, err := db.Begin()
	if err != nil {
		log.Fatal(err)
	}
	stmt, err := tx.Prepare("insert into macvendor (name, mac) values(?, ?)")
	if err != nil {
		log.Fatal(err)
	}
	defer stmt.Close()
	_, err = stmt.Exec(text, mac)
	if err != nil {
		log.Fatal(err)
	}
	tx.Commit()
}

func main() {
	dbFile := "./android/src/main/assets/mac_devices.db"
	os.Remove(dbFile)

	db, err := sql.Open("sqlite3", dbFile)

	if err != nil {
		log.Fatal(err)
	}
	defer db.Close()

	sqlStmt := `
	create table macvendor (name TEXT not null, mac TEXT not null, PRIMARY KEY (name, mac));
	`
	_, err = db.Exec(sqlStmt)
	if err != nil {
		log.Printf("%q: %s\n", err, sqlStmt)
		return
	}
	fetchCsv(db)

}

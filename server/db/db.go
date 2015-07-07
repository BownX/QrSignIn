package db

import (
	"github.com/astaxie/beego/orm"
	_ "github.com/mattn/go-sqlite3"
)

const (
	DB_DRIVER  = "sqlite3"
	DB_NAME    = "qrsignin.db"
	DB_DEFAULT = "default"
)

var (
	Db orm.Ormer
)

func init() {
	orm.Debug = true
	orm.RegisterModel(
		new(Course),
		new(Student),
		new(Lesson),
		new(SignIn),
		new(CourseStudentRel),
	)
	orm.RegisterDataBase(DB_DEFAULT, DB_DRIVER, DB_NAME)
	orm.RunSyncdb(DB_DEFAULT, false, false)
	Db = orm.NewOrm()
}

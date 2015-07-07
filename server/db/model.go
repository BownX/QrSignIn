package db

import (
	"time"
)

type Course struct {
	Id       int        `orm:"pk;auto" json:"id"`
	Title    string     `json:"title"`
	Teacher  string     `json:"teacher"`
	Students []*Student `orm:"rel(m2m);rel_through(bown.xyz/QrSignIn/db.CourseStudentRel)" json:"students"` // rel_through多对一关系
	Lessons  []*Lesson  `orm:"reverse(many)" json:"lessons"`
}

type Student struct {
	Id      int       `orm:"pk;auto" json:"id"`
	Name    string    `json:"name"`
	StuId   string    `orm:"unique" json:"stuid"`
	Imei    string    `orm:"unique" json:"imei"`
	Major   string    `json:"major"`
	Courses []*Course `orm:"reverse(many)" json:"courses"`
	SignIns []*SignIn `orm:"reverse(many)" json:"signins"`
}

type Lesson struct {
	Id      int       `orm:"pk;auto" json:"id"`
	Course  *Course   `orm:"rel(fk)" json:"course"`
	Info    string    `json:"info"`
	SignIns []*SignIn `orm:"reverse(many)" json:"signins"`
}

type SignIn struct {
	Id       int       `orm:"pk;auto" json:"id"`
	SignTime time.Time `orm:"auto_now_add;type(datetime)" json:"signtime"`
	Student  *Student  `orm:"rel(fk)" json:"student"`
	Lesson   *Lesson   `orm:"rel(fk)" json:"lesson"`
}

type CourseStudentRel struct {
	Id      int      `orm:"pk;auto" json:"id"`
	Course  *Course  `orm:"rel(fk)" json:"course"`
	Student *Student `orm:"rel(fk)" json:"student"`
}

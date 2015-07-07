package web

import (
	"encoding/json"
	"github.com/BownX/QrSignIn/server/db"
	"log"
	"strconv"
)

type Response struct {
	Code int         `json:"code"`
	Data interface{} `json:"data"`
}

const (
	CODE_SUCCESS    = 0
	CODE_ERROR      = 1
	CODE_REGISTERED = 2

	MSG_SUCCESS                         = "成功"
	MSG_ERROR_DEFAULT                   = "服务器异常"
	MSG_MISSING_PARAMS                  = "请补全参数"
	MSG_REGISTER_STUDENT_ID_DUPLICATE   = "此学号已注册"
	MSG_REGISTER_STUDENT_IMEI_DUPLICATE = "此手机已注册"
	MSG_NO_COURSE                       = "找不到这门课"
	MSG_NO_STUDENT                      = "找不到这个学生"
	MSG_NO_LESSON                       = "找不到这堂课"
	MSG_STUDENT_ALREADY_ADDED           = "已添加这个学生"
	MSG_STUDENT_ALREADY_SIGN_IN         = "此学生已签到"
)

func welcome(ctx *Context) string {
	return "welcome to QrSignIn System"
}

// 学生注册，需要姓名、手机识别码、学号、专业
func registerStudent(ctx *Context) string {
	name, nameOk := ctx.Params["name"]
	imei, imeiOk := ctx.Params["imei"]
	stuId, stuIdOk := ctx.Params["stuid"]
	major, majorOk := ctx.Params["major"]
	if nameOk && stuIdOk && imeiOk && majorOk {
		var checkExist bool
		checkExist = db.Db.QueryTable("student").Filter("stuid", stuId).Exist()
		if checkExist {
			return buildResponse(CODE_REGISTERED, MSG_REGISTER_STUDENT_ID_DUPLICATE)
		}
		checkExist = db.Db.QueryTable("student").Filter("imei", imei).Exist()
		if checkExist {
			return buildResponse(CODE_ERROR, MSG_REGISTER_STUDENT_IMEI_DUPLICATE)
		}
		student := new(db.Student)
		student.Name = name
		student.StuId = stuId
		student.Imei = imei
		student.Major = major
		_, err := db.Db.Insert(student)
		if err != nil {
			log.Println("DB:", err)
			return buildResponse(CODE_ERROR, MSG_ERROR_DEFAULT)
		}
		return buildResponse(CODE_SUCCESS, MSG_SUCCESS)
	} else {
		return buildResponse(CODE_ERROR, MSG_MISSING_PARAMS)
	}
}

func addCourse(ctx *Context) string {
	title, titleOk := ctx.Params["title"]
	teacher, teacherOk := ctx.Params["teacher"]
	if titleOk && teacherOk {
		course := new(db.Course)
		course.Teacher = teacher
		course.Title = title
		_, err := db.Db.Insert(course)
		if err != nil {
			log.Println("DB:", err)
			return buildResponse(CODE_ERROR, MSG_ERROR_DEFAULT)
		}
		return buildResponse(CODE_SUCCESS, MSG_SUCCESS)
	} else {
		return buildResponse(CODE_ERROR, MSG_MISSING_PARAMS)
	}
}

func addLesson(ctx *Context) string {
	courseId, courseIdOk := ctx.Params["courseid"]
	info, infoOk := ctx.Params["info"]
	if courseIdOk && infoOk {
		cid := string2int(courseId)
		course := db.Course{Id: int(cid)}
		err := db.Db.Read(&course)
		if err != nil {
			return buildResponse(CODE_ERROR, MSG_NO_COURSE)
		}

		lesson := new(db.Lesson)
		lesson.Course = &course
		lesson.Info = info
		_, err = db.Db.Insert(lesson)
		if err != nil {
			log.Println("DB:", err)
			return buildResponse(CODE_ERROR, MSG_ERROR_DEFAULT)
		}
		return buildResponse(CODE_SUCCESS, MSG_SUCCESS)
	} else {
		return buildResponse(CODE_ERROR, MSG_MISSING_PARAMS)
	}
}

func addCourseStudent(ctx *Context) string {
	courseId, courseIdOk := ctx.Params["courseid"]
	studentId, studentIdOk := ctx.Params["studentid"]
	if courseIdOk && studentIdOk {
		cid := string2int(courseId)
		course := db.Course{Id: int(cid)}
		err := db.Db.Read(&course)
		// 如果没找着这门课
		if err != nil {
			return buildResponse(CODE_ERROR, MSG_NO_COURSE)
		}
		sid := string2int(studentId)
		student := db.Student{Id: sid}
		err = db.Db.Read(&student)
		if err != nil {
			return buildResponse(CODE_ERROR, MSG_NO_STUDENT)
		}
		exist := db.Db.QueryTable("course_student_rel").Filter("course__id", cid).Filter("student__id", sid).Exist()
		if exist {
			return buildResponse(CODE_ERROR, MSG_STUDENT_ALREADY_ADDED)
		}
		rel := db.CourseStudentRel{Course: &course, Student: &student}
		_, err = db.Db.Insert(&rel)
		if err != nil {
			log.Println("DB:", err)
			return buildResponse(CODE_ERROR, MSG_ERROR_DEFAULT)
		}
		return buildResponse(CODE_SUCCESS, MSG_SUCCESS)
	} else {
		return buildResponse(CODE_ERROR, MSG_MISSING_PARAMS)
	}
}

func listLessons(ctx *Context) string {
	courseId, courseIdOk := ctx.Params["courseid"]
	if courseIdOk {
		cid := string2int(courseId)
		course := db.Course{Id: int(cid)}
		err := db.Db.Read(&course)
		if err != nil {
			return buildResponse(CODE_ERROR, MSG_NO_COURSE)
		}
		var lessons []*db.Lesson
		_, err = db.Db.QueryTable("lesson").Filter("course__id", cid).All(&lessons)
		if err != nil {
			return buildResponse(CODE_ERROR, MSG_ERROR_DEFAULT)
		}
		return buildResponse(CODE_SUCCESS, lessons)
	} else {
		return buildResponse(CODE_ERROR, MSG_MISSING_PARAMS)
	}
}

func listStudents(ctx *Context) string {
	var students []*db.Student
	_, err := db.Db.QueryTable("student").All(&students)
	if err != nil {
		return buildResponse(CODE_ERROR, MSG_ERROR_DEFAULT)
	}
	return buildResponse(CODE_SUCCESS, students)
}

func listCourses(ctx *Context) string {
	var courses []*db.Course
	_, err := db.Db.QueryTable("course").All(&courses)
	if err != nil {
		return buildResponse(CODE_ERROR, MSG_ERROR_DEFAULT)
	}
	return buildResponse(CODE_SUCCESS, courses)
}

func listCourseStudents(ctx *Context) string {
	courseId, courseIdOk := ctx.Params["courseid"]
	if courseIdOk {
		cid := string2int(courseId)
		course := db.Course{Id: int(cid)}
		err := db.Db.Read(&course)
		if err != nil {
			return buildResponse(CODE_ERROR, MSG_NO_COURSE)
		}
		var courseStudents []*db.CourseStudentRel
		_, err = db.Db.QueryTable("course_student_rel").Filter("course__id", cid).All(&courseStudents)
		if err != nil {
			return buildResponse(CODE_ERROR, MSG_ERROR_DEFAULT)
		}
		if courseStudents == nil || len(courseStudents) == 0 {
			return buildResponse(CODE_SUCCESS, courseStudents)
		}
		studnetIds := make([]int, len(courseStudents), len(courseStudents))
		for index, value := range courseStudents {
			studnetIds[index] = value.Student.Id
		}
		var studnets []*db.Student
		_, err = db.Db.QueryTable("student").Filter("id__in", studnetIds).All(&studnets)
		if err != nil {
			return buildResponse(CODE_ERROR, MSG_ERROR_DEFAULT)
		}
		return buildResponse(CODE_SUCCESS, studnets)
	}
	return buildResponse(CODE_ERROR, MSG_MISSING_PARAMS)
}

func listLessonStudents(ctx *Context) string {
	lessonId, lessonIdOk := ctx.Params["lessonid"]
	if lessonIdOk {
		lid := string2int(lessonId)
		lesson := db.Lesson{Id: int(lid)}
		err := db.Db.Read(&lesson)
		if err != nil {
			return buildResponse(CODE_ERROR, MSG_NO_LESSON)
		}
		var signIns []*db.SignIn
		_, err = db.Db.QueryTable("sign_in").Filter("lesson__id", lid).All(&signIns)
		if err != nil {
			return buildResponse(CODE_ERROR, MSG_ERROR_DEFAULT)
		}
		return buildResponse(CODE_SUCCESS, signIns)
	}
	return buildResponse(CODE_ERROR, MSG_MISSING_PARAMS)
}

func signIn(ctx *Context) string {
	imei, imeiOk := ctx.Params["imei"]
	lessonId, lessonIdOk := ctx.Params["lessonid"]
	if imeiOk && lessonIdOk {
		lid := string2int(lessonId)
		lesson := db.Lesson{Id: lid}
		err := db.Db.Read(&lesson)
		if err != nil {
			return buildResponse(CODE_ERROR, MSG_NO_LESSON)
		}
		student := db.Student{Imei: imei}
		err = db.Db.Read(&student, "Imei")
		if err != nil {
			return buildResponse(CODE_ERROR, MSG_NO_STUDENT)
		}
		exist := db.Db.QueryTable("sign_in").Filter("lesson__id", lid).Filter("student__id", student.Id).Exist()
		if exist {
			return buildResponse(CODE_ERROR, MSG_STUDENT_ALREADY_SIGN_IN)
		}
		signIn := db.SignIn{Student: &student, Lesson: &lesson}
		_, err = db.Db.Insert(&signIn)
		if err != nil {
			return buildResponse(CODE_ERROR, MSG_ERROR_DEFAULT)
		}
		return buildResponse(CODE_ERROR, MSG_SUCCESS)
	}
	return buildResponse(CODE_ERROR, MSG_MISSING_PARAMS)
}

func buildResponse(code int, data interface{}) string {
	result, _ := json.Marshal(Response{
		code,
		data,
	})
	return string(result)
}

func string2int(str string) int {
	id, err := strconv.ParseInt(str, 10, 0)
	if err != nil {
		return -1
	}
	return int(id)
}

func GetUrlMappings() map[string]func(ctx *Context) string {
	return map[string]func(ctx *Context) string{
		"/":                    welcome,
		"/signin":              signIn,
		"/course/add":          addCourse,
		"/lesson/add":          addLesson,
		"/lesson/list":         listLessons,
		"/course/list":         listCourses,
		"/student/list":        listStudents,
		"/student/register":    registerStudent,
		"/course/student/add":  addCourseStudent,
		"/course/student/list": listCourseStudents,
		"/lesson/student/list": listLessonStudents,
	}
}

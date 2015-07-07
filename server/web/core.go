package web

import (
	"fmt"
	"log"
	"net"
	"net/http"
)

type Context struct {
	Ip      string
	Method  string
	Headers map[string]string
	Params  map[string]string
}

func register(url string, f func(ctx *Context) string) {
	http.HandleFunc(url, func(w http.ResponseWriter, r *http.Request) {
		r.ParseForm()
		ip, _, _ := net.SplitHostPort(r.RemoteAddr)
		context := Context{
			ip,
			r.Method,
			make(map[string]string),
			make(map[string]string),
		}
		for k, v := range r.Form {
			context.Params[k] = v[0]
		}
		for k, v := range r.PostForm {
			context.Params[k] = v[0]
		}
		for k, v := range r.Header {
			context.Headers[k] = v[0]
		}
		fmt.Fprint(w, f(&context))
		log.Println("Request: ", r.RequestURI, r.Method, ip)
	})
}

func Run(port int, urlMapping map[string]func(ctx *Context) string) {
	for k, v := range urlMapping {
		register(k, v)
	}

	var err error
	err = http.ListenAndServe(fmt.Sprintf(":%d", port), nil)
	if err != nil {
		log.Fatal("Server: ", err)
	}
}

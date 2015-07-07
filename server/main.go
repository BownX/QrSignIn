package main

import (
	"github.com/BownX/QrSignIn/web"
)

const (
	SERVER_PORT = 9090
)

func main() {
	// 启动QrSignin服务端
	web.Run(SERVER_PORT, web.GetUrlMappings())
}

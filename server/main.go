package main

import (
	"github.com/BownX/QrSignIn/server/web"
)

const (
	SERVER_PORT = 9090
)

func main() {
	web.Run(SERVER_PORT, web.GetUrlMappings())
}

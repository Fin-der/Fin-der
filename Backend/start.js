import { app, port} from './server.js'
import socketio from 'socket.io'
import http from 'http'
import WebSockets from "./utils/WebSockets.js"
import "./config/mongo.js"

const server = http.createServer(app)

global.io = socketio.listen(server)
global.io.on('connection', (socket) => WebSockets.connection(socket));
//global.io.on('join-room', (socket) => WebSockets.subscribeOtherUser(socket));

server.listen(port)

server.on("listening", () => {
    console.log(`Listening on port:: http://localhost:${port}/`)
});
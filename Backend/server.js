import express from 'express'
import bodyparser from 'body-parser'
import admin from './config/firebase-config.js'
import cors from 'cors'
import socketio from 'socket.io'
import http from 'http'
import "./config/mongo.js"
// socket configuration
import WebSockets from "./utils/WebSockets.js"
// routes
import indexRouter from "./routes/index.js"
import userRouter from "./routes/user.js"
import chatRoomRouter from "./routes/chatRoom.js"
import deleteRouter from "./routes/delete.js"
import matchRouter from "./routes/match.js"

import { decode } from './middlewares/jwt.js'

// run server with node --experimental-json-modules server.js
const app = express();
app.use(bodyparser.json());

const port = 3000
app.set("port", port)

app.use(express.json());

app.use("/", indexRouter);
app.use("/users", userRouter);
app.use("/room", chatRoomRouter); // add decryption here
app.use("/delete", deleteRouter);
app.use("/match", matchRouter);

app.use('*', (req, res) => {
    return res.status(404).json({
        success: false,
        message: 'API endpoint doesnt exist'
    })
});

const server = http.createServer(app)

global.io = socketio.listen(server)
global.io.on('connection', (socket) => WebSockets.connection(socket));
//global.io.on('join-room', (socket) => WebSockets.subscribeOtherUser(socket));

server.listen(port)

server.on("listening", () => {
    console.log(`Listening on port:: http://localhost:${port}/`)
});
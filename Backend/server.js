import { app, port, logger} from "./app.js";
import socketio from "socket.io";
import http from "http";
import WebSockets from "./utils/WebSockets.js";
import "./config/mongo.js";
import admin from "./utils/FirebaseMessaging.js";

// Create http server for chatting
const server = http.createServer(app);

// Initialize Firebase Cloud Messaging service for sending push notifications
admin.initFCM;

// Initialize Websockets for chatting
global.io = socketio.listen(server);
global.io.on("connection", (socket) => WebSockets.connection(socket));
global.io.on("join-room", (socket) => WebSockets.subscribeOtherUser(socket));

// Start http Express server
server.listen(port);

server.on("listening", () => {
    logger.info(`Listening on port:: http://localhost:${port}/`);
});

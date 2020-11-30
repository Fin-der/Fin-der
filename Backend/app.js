import express from "express";
import pino from "pino";
// routes
import userRouter from "./routes/user.js";
import chatRoomRouter from "./routes/chatRoom.js";
import matchRouter from "./routes/match.js";

// Initialize Pino logger
export const logger = pino({
    enabled: !(process.env.NODE_ENV === "test")
});
export const app = express();

export const port = 3000;
app.set("port", port);

// Create Express server
app.use(express.json());

// Primary app routes
// Routes for users
app.use("/users", userRouter);
// Routes fro chatting
app.use("/room", chatRoomRouter); 
// Routes for matching
app.use("/match", matchRouter);

app.use("*", (req, res) => {
    return res.status(404).json({
        success: false,
        message: "API endpoint doesnt exist"
    });
});

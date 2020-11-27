import express from "express";
// routes
import userRouter from "./routes/user.js";
import chatRoomRouter from "./routes/chatRoom.js";
import deleteRouter from "./routes/delete.js";
import matchRouter from "./routes/match.js";

// run server with node --experimental-json-modules server.js
export const app = express();

export const port = 3000;
app.set("port", port);

app.use(express.json());

app.use("/users", userRouter);
app.use("/room", chatRoomRouter); 
app.use("/delete", deleteRouter);
app.use("/match", matchRouter);

app.use("*", (req, res) => {
    return res.status(404).json({
        success: false,
        message: "API endpoint doesnt exist"
    });
});

import express from 'express'
//import bodyparser from 'body-parser'
import admin from './config/firebase-config.js'
//import cors from 'cors'
// routes
import indexRouter from "./routes/index_routes.js"
import userRouter from "./routes/user_routes.js"
import chatRoomRouter from "./routes/chatRoom_routes.js"
import deleteRouter from "./routes/delete_routes.js"
import matchRouter from "./routes/match_routes.js"

import { decode } from './middlewares/jwt.js'

// run server with node --experimental-json-modules server.js
export const app = express();

export const port = 3000
app.set("port", port)

app.use(express.json());

app.use("/", indexRouter);
app.use("/users", userRouter);
app.use("/room", chatRoomRouter); // add decryption here
app.use("/delete", deleteRouter);
app.use("/match", matchRouter);

app.use('/test', async (req, res) => {
    res.status(200).json({message: 'pass!'})
  })

app.use('*', (req, res) => {
    return res.status(404).json({
        success: false,
        message: 'API endpoint doesnt exist'
    })
});

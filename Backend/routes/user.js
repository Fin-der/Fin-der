import express from "express";
// controllers
import user from "../controllers/user.js";

const router = express.Router();
// route: ip:port/users
router
    // params: nothing
    // returns: list of user objects contain user id, timestamps
    .get("/", user.onGetAllUsers)
    // params: json all info defined in user schema (models/User.js)
    // returns: user info of newly generated user
    .post("/", user.onCreateUser)
    // params: :id the id of the user to get
    // returns: user info
    .get("/:id", user.onGetUserById)
    .delete("/:id", user.onDeleteUserById)
    // params: FCM token, user id
    // returns: message containing token and user id
    .put("/:id/:token", user.onRegisterFCMToken);

export default router;
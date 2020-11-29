import express from "express";
// controllers
import user from "../controllers/user.js";

const router = express.Router();
// route: ip:port/users
router
    /**
     * Route serving getting the info of all users
     * @module route/user
     * @name get/
     * @returns {Array} An array of user objects 
     */
    .get("/", user.onGetAllUsers)
    /**
     * Route serving creating a user
     * @module route/user
     * @name post/
     * @param {Object} body A Object representing necessary info for user creation
     * @returns {Object} A User object representing the data given
     */
    .post("/", user.onCreateUser)
    /**
     * Route serving getting user info
     * @module route/user
     * @name get/:id
     * @param {String} id - id of the user you would like the info of 
     * @returns {Object} A User object representing the user selected
     */
    .get("/:id", user.onGetUserById)
    /**
     * Route serving updating a user
     * @module route/user
     * @name put/:id
     * @param {Object} body - object representing field you would like to update
     * @param {String} id - id of the user you would like to update
     * @returns {Object} A User object representing the updated user
     */
    .put("/:id", user.onUpdateUserById)
    /**
     * Route serving deleting a user
     * @module route/user
     * @name delete/:id
     * @param {String} id - id of the user you would like to delete
     * @returns {String} A Message specifying the how many users were deleted
     */
    .delete("/:id", user.onDeleteUserById)
    /**
     * Route serving updating a users FCMToken
     * @module route/user
     * @name put/:id/:token
     * @param {String} id - id of the user you would like to update the FCMToken of
     * @param {String} token - the new FCMToken for the user of given id
     * @returns {String} A Message specifying that the token was successfully updated
     */
    .put("/:id/:token", user.onRegisterFCMToken);

export default router;
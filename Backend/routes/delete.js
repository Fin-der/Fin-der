import express from "express";
// controllers
import deleteController from "../controllers/delete.js";

const router = express.Router();
// route: ip:port/delete
router
    /**
     * Routes serving deletion of a chatroom
     * @module route/delete
     * @name delete/room/:roomId
     * @param {String} roomId - the id of the room to delete
     * @returns {Object} The number of rooms deleted
     */
    .delete("/room/:roomId", deleteController.deleteRoomById)
    /**
     * Routes serving the deletion of a chat message
     * @module route/delete
     * @name delete/message/:messageId
     * @param {String} messageId - the id of the message to delete
     * @returns {Object} The number of messages deleted
     */
    .delete("/message/:messageId", deleteController.deleteMessageById);

export default router;
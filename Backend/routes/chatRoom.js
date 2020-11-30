import express from "express";
// controllers
import chatRoom from "../controllers/chatRoom.js";

const router = express.Router();
// route: ip:port/room
router
    // params: user id
    /**
     * Routes serving getting a recent chatroom
     * @module route/chatRoom
     * @name get/
     * @param {String} userId - the user to find the recent conversation of
     * @param {Number} query.page - for pagination
     * @param {Number} query.limit - for pagination
     * @returns {Object} A Object representing a recent conversation
     */
    .get("/", chatRoom.getRecentConversation)
    /**
     * Routes serving getting conversations
     * @module route/chatRoom
     * @name get/:roomId/:skip
     * @param {String} roomId - the id of the room to get
     * @param {String} skip - the number of documents to skip for pagination
     * @returns {Object} The conversation with given roomId
     */
    .get("/:roomId/:skip", chatRoom.getConversationByRoomId)
    /**
     * Routes serving creating conversations
     * @module route/chatRoom
     * @name post/initiate
     * @param {Array} userIds an array of userids belonging to the chatRoom
     * @returns {Object} The roomId of the created conversation
     */
    .post("/initiate", chatRoom.initiate)
    // params: messageText, roomId, userId(sender)
    // returns: a post json: chatRoomId, Message, User, Read-by
    /**
     * Routes serving sending messages
     * @module route/chatRoom
     * @name post/:roomId/:id/message
     * @param {String} messageText - the message body
     * @param {String} roomId - the id of the room to send the messaging in
     * @param {String} id - the id of the user sending the message
     * @returns {Object} Object representing the sent message
     */
    .post("/:roomId/:id/message", chatRoom.postMessage)
    /**
     * Routes serving marking messages as read
     * @module route/chatRoom
     * @name post/:roomId/:id/mark-read
     * @param {String} roomId - the id of the room the user is reading
     * @param {String} id - the id of the user reading the conversation
     * @returns {Object} The number of messages read
     */
    .put("/:roomId/:id/mark-read", chatRoom.markConversationReadByRoomId);

export default router;
/**
 * @module controller/chatRoom
 */
import ChatRoomModel from "../models/ChatRoom.js";
import ChatMessageModel from "../models/ChatMessage.js";
import UserModel from "../models/User.js";
import FirebaseMessaging from "../utils/FirebaseMessaging.js";
import {logger} from "../app.js";
/**
 * Helper function for parsing query params
 */
let generateOptions = (req, skip) => {
    const options = {
        page: parseInt(req.query.page, 10),
        limit: parseInt(req.query.limit, 10) || 25,
        skip: parseInt(skip, 10),
    };
    return options;
};
export default {
    /**
     * Creates a chatRoom 
     * 
     * @function initiate
     * @param {Array} userIds an array of userids belonging to the chatRoom
     * @returns {Object} The status, message and roomId of the created conversation
     */
    initiate: async (req, res) => {
        try {
            const { userIds } = req.body;
            const { userId: chatInitiator } = req;
            const allUserIds = [...userIds, chatInitiator];
            const chatRoom = await ChatRoomModel.initiateChat(allUserIds, chatInitiator);
            return res.status(200).json({ success: true, chatRoom });
        } catch (error) {
            logger.error(error);
            return res.status(500).json({ success: false, error });
        }
    },
    /**
     * Sends a message in a chat
     * 
     * @function postMessage
     * @param {String} messageText - the message body
     * @param {String} roomId - the id of the room to send the messaging in
     * @param {String} id - the id of the user sending the message
     * @returns {Object} Object representing the sent message
     */
    postMessage: async (req, res) => {
        try {
            const messagePayload = {
                messageText: req.body.messageText,
            };
            // const currentLoggedUser = req.body.userId;
            const currentLoggedUser = req.params.id;
            const user = await UserModel.getUserById(currentLoggedUser);
            const roomId = req.params.roomId;
            const post = await ChatMessageModel.createPostInChatRoom(roomId, messagePayload, currentLoggedUser);
            if (global.io){
                global.io.sockets.in(roomId).emit("new message", { message: post });
            }
            // get token of other users
            var userIds = await ChatRoomModel.getUserIdsFromRoomId(roomId);
            userIds.splice(userIds.indexOf(user._id), 1);
            const FCMTokens = await UserModel.getTokensByIds(userIds);
            const msgBody = "You have a new message from " + user.firstName + " " + messagePayload.messageText;
            await FirebaseMessaging.sendMultiNotifMsg(FCMTokens, msgBody);
            return res.status(200).json({ success: true, post });
        } catch (error) {
            logger.error(error);
            return res.status(500).json({ success: false, error });
        }
    },
    /**
     * Retrieves the recent conversation for a user
     * 
     * @function getRecentConversation
     * @param {String} userId - the user to find the recent conversation of
     * @param {Number} query.page - for pagination
     * @param {Number} query.limit - for pagination
     * @returns {Object} A Object representing a recent conversation
     */
    getRecentConversation: async (req, res) => {
        try {
            const currentLoggedUser = req.body.userId;
            const options = {
                page: parseInt(req.query.page, 10) || 0,
                limit: parseInt(req.query.limit, 10) || 10,
            };
            const rooms = await ChatRoomModel.getChatRoomsByUserId(currentLoggedUser);
            const roomIds = rooms.map((room) => room._id);
            const recentConversation = await ChatMessageModel.getRecentConversation(
                roomIds, options, currentLoggedUser
            );
            return res.status(200).json({ success: true, conversation: recentConversation });
        } catch (error) {
            logger.error(error);
            return res.status(500).json({ success: false, error });
        }
    },
    /**
     * Retrieves the chat logs in a specific room
     * 
     * @function getConversationByRoomId
     * @param {String} roomId - the id of the room to get
     * @param {String} skip - the number of documents to skip for pagination
     * @returns {Object} The conversation with given roomId
     */
    getConversationByRoomId: async (req, res) => {
        try {
            const { roomId, skip } = req.params;
            const room = await ChatRoomModel.getChatRoomByRoomId(roomId);
            const users = await UserModel.getUsersByIds(room.userIds);
            const options = generateOptions(req, skip);   
            const conversation = await ChatMessageModel.getConversationByRoomId(roomId, options);
            return res.status(200).json({
                success: true,
                conversation,   
                users,
            });
        } catch (error) {
            logger.error(error);
            return res.status(500).json({ success: false, error });
        }
    },
    /**
     * Marks messages as read
     * 
     * @function markConversationReadByRoomId
     * @param {String} roomId - the id of the room the user is reading
     * @param {String} id - the id of the user reading the conversation
     * @returns {Object} The number of messages read
     */
    markConversationReadByRoomId: async (req, res) => {
        try {
            const { roomId, id } = req.params;
            const room = await ChatRoomModel.getChatRoomByRoomId(roomId);
            const currentLoggedUser = id;
            const result = await ChatMessageModel.markMessageRead(roomId, currentLoggedUser);
            return res.status(200).json({ success: true, data: result });
        } catch (error) {
            logger.error(error);
            return res.status(500).json({ success: false, error });
        }
    },
};

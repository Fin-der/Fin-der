import ChatRoomModel from "../models/ChatRoom.js";
import ChatMessageModel from "../models/ChatMessage.js";
import UserModel from "../models/User.js";
import admin from "../config/firebase-config.js";

export default {
    initiate: async (req, res) => {
        try {  // eslint-disable-line no-eval
            const { userIds, type } = req.body;
            const { userId: chatInitiator } = req;
            const allUserIds = [...userIds, chatInitiator];
            const chatRoom = await ChatRoomModel.initiateChat(allUserIds, type, chatInitiator);
            
            return res.status(200).json({ success: true, chatRoom });
        } catch (error) {
            return res.status(500).json({ success: false, error });
        }
    },
    postMessage: async (req, res) => {
        try {
            const messagePayload = {
                messageText: req.body.messageText,
            };
            const currentLoggedUser = req.body.userId;
            const roomId = req.body.roomId;
            const post = await ChatMessageModel.createPostInChatRoom(roomId, messagePayload, currentLoggedUser);
            global.io.sockets.in(roomId).emit("new message", { message: post });
            // get token of other users
            const userIds = await ChatRoomModel.getUserIdsFromRoomId(roomId);
            const registrationTokens = await UserModel.getTokensbyIds(userIds);
            var notifMessage = {
                "notification": {
                    "title": "Message From ",
                    "body": "message"
                },
                "tokens": registrationTokens
            };
            if (registrationTokens.length !== 0) {
                admin.messaging().sendMulticast(notifMessage);
            }
            
            return res.status(200).json({ success: true, post });
        } catch (error) {
            return res.status(500).json({ success: false, error });
        }
    },
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
            return res.status(500).json({ success: false, error });
        }
    },
    getConversationByRoomId: async (req, res) => {
        try {
            const { roomId, skip } = req.params;
            const room = await ChatRoomModel.getChatRoomByRoomId(roomId);
            const users = await UserModel.getUserByIds(room.userIds);
            const options = await this.generateOptions(req, skip);
            const conversation = await ChatMessageModel.getConversationByRoomId(roomId, options);
            return res.status(200).json({
                success: true,
                conversation,
                users,
            });
        } catch (error) {
            return res.status(500).json({ success: false, error });
        }
    },
    markConversationReadByRoomId: async (req, res) => {
        try {
            const { roomId } = req.params;
            const room = await ChatRoomModel.getChatRoomByRoomId(roomId);
            if (!room) {
                return res.status(400).json({
                    success: false,
                    message: "No room exists for this id",
                });
            }

            const currentLoggedUser = req.userId;
            const result = await ChatMessageModel.markMessageRead(roomId, currentLoggedUser);
            return res.status(200).json({ success: true, data: result });
        } catch (error) {
            return res.status(500).json({ success: false, error });
        }
    },
    generateOptions: async (req, skip) => {
        try {
            const options = {
                page: parseInt(req.query.page, 10),
                limit: parseInt(req.query.limit, 10) || 25,
                skip: parseInt(skip, 10),
            };
            return options;
        } catch (error) {
            throw error;
        }
    },
};


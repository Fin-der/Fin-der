import ChatRoomModel from '../models/ChatRoom.js';
import ChatMessageModel from '../models/ChatMessage.js';
import UserModel from '../models/User.js';

export default {
    initiate: async (req, res) => {
        try {
            const { userIds, type } = req.body;
            const { userId: chatInitiator } = req;
            const allUserIds = [...userIds, chatInitiator];
            const chatRoom = await ChatRoomModel.initiateChat(allUserIds, type, chatInitiator);
            
            return res.status(200).json({ success: true, chatRoom });
        } catch (error) {
            return res.status(500).json({ success: false, error: error })
        }
    },
    postMessage: async (req, res) => {
        try {
            const messagePayload = {
                messageText: req.body.messageText,
            };
            const currentLoggedUser = req.userId;
            const roomId = req.roomId;
            const post = await ChatMessageModel.createPostInChatRoom(roomId, messagePayload, currentLoggedUser);
            global.io.sockets.in(roomId).emit('new message', { message: post });
            // get token of other users
            const userIds = await ChatRoomModel.getUserIdsFromRoomId(roomId)
            const registrationTokens = await UserModel.getTokensbyIds(userIds)
            var notif_message = {
                "notification": {
                    "title": "Message From ",
                    "body": "message"
                },
                "tokens": registrationTokens
            }
            admin.messaging().sendMulticast(notif_message)
            const userIds = await ChatRoomModel.getUserByIds
            return res.status(200).json({ success: true, post });
        } catch (error) {
            return res.status(500).json({ success: false, error: error })
        }
    },
    getRecentConversation: async (req, res) => {
        try {
            const currentLoggedUser = req.userId;
            const options = {
                page: parseInt(req.query.page) || 0,
                limit: parseInt(req.query.limit) || 10,
            };
            const rooms = await ChatRoomModel.getChatRoomsByUserId(currentLoggedUser);
            const roomIds = rooms.map(room => room._id);
            const recentConversation = await ChatMessageModel.getRecentConversation(
                roomIds, options, currentLoggedUser
            );
            return res.status(200).json({ success: true, conversation: recentConversation });
        } catch (error) {
            return res.status(500).json({ success: false, error: error })
        }
    },
    getConversationByRoomId: async (req, res) => {
        try {
            const { roomId } = req.params;
            const room = await ChatRoomModel.getChatRoomByRoomId(roomId)
            if (!room) {
                return res.status(400).json({
                success: false,
                message: 'No room exists for this id',
                })
            }
            console.log(roomId)
            const users = await UserModel.getUserByIds(room.userIds);
            const options = {
                page: parseInt(req.query.page) || 0,
                limit: parseInt(req.query.limit) || 10,
            };
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
            const room = await ChatRoomModel.getChatRoomByRoomId(roomId)
            if (!room) {
                return res.status(400).json({
                success: false,
                message: 'No room exists for this id',
                })
            }

            const currentLoggedUser = req.userId;
            const result = await ChatMessageModel.markMessageRead(roomId, currentLoggedUser);
            return res.status(200).json({ success: true, data: result });
        } catch (error) {
            console.log(error);
            return res.status(500).json({ success: false, error });
        }
    },
}
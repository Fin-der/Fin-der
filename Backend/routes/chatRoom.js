import express from 'express';
// controllers
import chatRoom from '../controllers/chatRoom.js';

const router = express.Router();
// route: ip:port/room
router
    // params: user id
    // returns: chatroom info defined in schema(models/ChatRoom.js)
    .get('/', chatRoom.getRecentConversation)
    // params: room id
    // returns: chatroom info defined in schema(models/ChatRoom.js)
    .get('/:roomId', chatRoom.getConversationByRoomId)
    // params: list of userids, type(for now)
    // returns: room id of new room created
    .post('/initiate', chatRoom.initiate)
    // params: messageText, roomId, userId(sender)
    // returns: a post json: chatRoomId, Message, User, Read-by
    .post('/:roomId/:id/message', chatRoom.postMessage)
    // params: roomId, userId
    // returns: data json list of results
    .put('/:roomId/:id/mark-read', chatRoom.markConversationReadByRoomId)

export default router;
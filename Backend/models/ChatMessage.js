/**
 * @module model/ChatMessage
 */
import mongoose from "mongoose";
import { v4 as uuidv4 } from "uuid";
import encrypt from "mongoose-encryption";
import keys from "../config/mongoose-encrpytion.json";

const MESSAGE_TYPES = {
    TYPE_TEXT: "text",
};

/**
 * Representation of reading a message
 */
const readByRecipientSchema = new mongoose.Schema(
    {
        _id: false,
        readByUserId: String,
        readAt: {
            type: Date,
            default: Date.now(),
        },
    },
    {
        timestamps: false,
    }
);

/**
 * Representation of chatMessage
 * 
 * @class ChatMessageModel
 */
const chatMessageSchema = new mongoose.Schema(
    {
        _id: {
            type: String,
            default: () => uuidv4().replace(/\-/g, ""),
        },
        chatRoomId: {type: String},
        message: mongoose.Schema.Types.Mixed,
        type: {
            type: String,
            default: () => MESSAGE_TYPES.TYPE_TEXT,
        },
        postedByUser: String,
        readByRecipients: [readByRecipientSchema],
    },
    {
        timestamps: true,
        collection: "chatmessages",
    }
);

chatMessageSchema.plugin(encrypt, {encryptionKey: keys.encKey, signingKey: keys.sigKey, encryptedFields: ["message"]});

/**
 * This method will create a post in chat
 * 
 * @param {String} chatRoomId - id of chat room
 * @param {Object} message - message you want to post in the chat room
 * @param {String} postedByUserId - user id who is posting the message
 * @returns {String} the newly created post
 */
chatMessageSchema.statics.createPostInChatRoom = async function (chatRoomId, message, postedByUserId) {
    const post = await this.create({
        chatRoomId,
        message,
        postedByUser: postedByUserId,
        readByRecipients: { readByUserId: postedByUserId }
    });
    return post;
};

/**
 * Retrieves the messages/conversation of a chatRoom
 * sorts by newest first
 * 
 * @param {String} chatRoomId - chat room id
 * @returns the Conversation with RoomId
 */
chatMessageSchema.statics.getConversationByRoomId = async function (chatRoomId, options = {}) {
    const room = await this.find({chatRoomId})
                           .sort({createdAt: 1})
                           .skip(options.skip)
                           .limit(options.limit)
                           .sort({createdAt: -1});
    return room;
};

/**
 * Marks a message as read
 * 
 * @param {String} chatRoomId - chat room id
 * @param {String} currentUserOnlineId - user id
 * @returns {Object} info of what changed
 */
chatMessageSchema.statics.markMessageRead = async function (chatRoomId, currentUserOnlineId) {
    var messages = await this.find({
        chatRoomId,
        "readByRecipients.readByUserId": { $ne: currentUserOnlineId }
    });
    await Promise.all(messages.map(async (message) => {
        message.readByRecipients.push({
            readAt: new Date(),
            readByUserId: currentUserOnlineId
        });
        message.save();
    }));

    return await this.find({
        chatRoomId,
        "readByRecipients.readByUserId": { $ne: currentUserOnlineId }
    });
};

/**
 * Retrieves messages from given chatRoomIds
 * 
 * @param {Array} chatRoomIds - chat room ids
 * @param {{ page, limit }} options - pagination options
 */
chatMessageSchema.statics.getRecentConversation = async function (chatRoomIds, options) {
    
    var messagesInChatRooms = [];
    await Promise.all(chatRoomIds.map(async (chatRoomId) => {
        const mostRecentMessage = await this.findOne({chatRoomId}).sort({createdAt: -1});
        var conversation = Object.assign({chatRoomId}, mostRecentMessage.toObject());
        messagesInChatRooms.push(conversation);
    }));
    // sort by most recent
    messagesInChatRooms.sort((a, b) => a.createdAt - b.createdAt);
    // apply pagination
    return messagesInChatRooms.slice(options.page * options.limit, (options.page + 1) * options.limit);
};

export default mongoose.model("ChatMessage", chatMessageSchema);

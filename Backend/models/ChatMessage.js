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
        chatRoomId: {type: String, default: "why"},
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

chatMessageSchema.plugin(encrypt, {encryptionKey: keys.encKey, signingKey: keys.sigKey});

/**
 * This method will create a post in chat
 * 
 * @param {String} chatRoomId - id of chat room
 * @param {Object} message - message you want to post in the chat room
 * @param {String} postedByUser - user who is posting the message
 * @returns {String} the newly created post
 */
chatMessageSchema.statics.createPostInChatRoom = async function (chatRoomId, message, postedByUser) {
    const post = await this.create({
        chatRoomId,
        message,
        postedByUser,
        readByRecipients: { readByUserId: postedByUser }
    });
    const aggregate = await this.aggregate([
    // get post where _id = post._id
    {   $match: { _id: post._id } },
    // do a join on another table called users, and 
    // get me a user whose _id = postedByUser
    {
        $lookup: {
            from: "users",
            localField: "postedByUser",
            foreignField: "_id",
            as: "postedByUser",
        }
    },
    {   $unwind: "$postedByUser" },
    // do a join on another table called chatrooms, and 
    // get me a chatroom whose _id = chatRoomId
    {
        $lookup: {
            from: "chatrooms",
            localField: "chatRoomId",
            foreignField: "_id",
            as: "chatRoomInfo",
        }
    },
    {   $unwind: "$chatRoomInfo" },
    {   $unwind: "$chatRoomInfo.userIds" },
    // do a join on another table called users, and 
    // get me a user whose _id = userIds
    {
        $lookup: {
            from: "users",
            localField: "chatRoomInfo.userIds",
            foreignField: "_id",
            as: "chatRoomInfo.userProfile",
        }
    },
    {   $unwind: "$chatRoomInfo.userProfile" },
    // group data
    {
        $group: {
            _id: "$chatRoomInfo._id",
            postId: { $last: "$_id" },
            chatRoomId: { $last: "$chatRoomInfo._id" },
            message: { $last: "$message" },
            type: { $last: "$type" },
            postedByUser: { $last: "$postedByUser" },
            readByRecipients: { $last: "$readByRecipients" },
            chatRoomInfo: { $addToSet: "$chatRoomInfo.userProfile" },
            createdAt: { $last: "$createdAt" },
            updatedAt: { $last: "$updatedAt" },
        }
    }]);
    return aggregate[0];
};

/**
 * Retrieves the messages/conversation of a chatRoom
 * 
 * @param {String} chatRoomId - chat room id
 * @returns the Conversation with RoomId
 */
chatMessageSchema.statics.getConversationByRoomId = async function (chatRoomId, options = {}) {
    return this.aggregate([
        { $match: { chatRoomId } },
        { $sort: { createdAt: -1 } },
        // do a join on another table called users, and 
        // get me a user whose _id = postedByUser
        {
            $lookup: {
            from: "users",
            localField: "postedByUser",
            foreignField: "_id",
            as: "postedByUser",
            }
        },
        { $unwind: "$postedByUser" },
        // apply pagination
        { $skip: options.skip },
        { $limit: options.limit },
        { $sort: { createdAt: 1 } },
    ]);
};

/**
 * Marks a message as read
 * 
 * @param {String} chatRoomId - chat room id
 * @param {String} currentUserOnlineId - user id
 * @returns {Object} info of what changed
 */
chatMessageSchema.statics.markMessageRead = async function (chatRoomId, currentUserOnlineId) {
    return this.updateMany(
        {
            chatRoomId,
            "readByRecipients.readByUserId": { $ne: currentUserOnlineId }
        },
        {
            $addToSet: {
                readByRecipients: { 
                    readByUserId: currentUserOnlineId 
                }
            }
        },
        {
            multi: true
        }
    );
};

/**
 * Retrieves messages from given chatRoomIds
 * 
 * @param {Array} chatRoomIds - chat room ids
 * @param {{ page, limit }} options - pagination options
 */
chatMessageSchema.statics.getRecentConversation = async function (chatRoomIds, options) {
    return this.aggregate([
        { $match: { chatRoomId: { $in: chatRoomIds } } },
        {
            $group: {
                _id: "$chatRoomId",
                messageId: { $last: "$_id" },
                chatRoomId: { $last: "$chatRoomId" },
                message: { $last: "$message" },
                type: { $last: "$type" },
                postedByUser: { $last: "$postedByUser" },
                createdAt: { $last: "$createdAt" },
                readByRecipients: { $last: "$readByRecipients" },
            }
        },
        { $sort: { createdAt: -1 } },
        // do a join on another table called users, and 
        // get me a user whose _id = postedByUser
        {
            $lookup: {
            from: "users",
            localField: "postedByUser",
            foreignField: "_id",
            as: "postedByUser",
            }
        },
        { $unwind: "$postedByUser" },
        // do a join on another table called chatrooms, and 
        // get me room details
        {
            $lookup: {
            from: "chatrooms",
            localField: "_id",
            foreignField: "_id",
            as: "roomInfo",
            }
        },
        { $unwind: "$roomInfo" },
        { $unwind: "$roomInfo.userIds" },
        // do a join on another table called users 
        {
            $lookup: {
            from: "users",
            localField: "roomInfo.userIds",
            foreignField: "_id",
            as: "roomInfo.userProfile",
            }
        },
        { $unwind: "$readByRecipients" },
        // do a join on another table called users 
        {
            $lookup: {
            from: "users",
            localField: "readByRecipients.readByUserId",
            foreignField: "_id",
            as: "readByRecipients.readByUser",
            }
        },

        {
            $group: {
            _id: "$roomInfo._id",
            messageId: { $last: "$messageId" },
            chatRoomId: { $last: "$chatRoomId" },
            message: { $last: "$message" },
            type: { $last: "$type" },
            postedByUser: { $last: "$postedByUser" },
            readByRecipients: { $addToSet: "$readByRecipients" },
            roomInfo: { $addToSet: "$roomInfo.userProfile" },
            createdAt: { $last: "$createdAt" },
            },
        },
        // apply pagination
        { $skip: options.page * options.limit },
        { $limit: options.limit },
    ]);
};

export default mongoose.model("ChatMessage", chatMessageSchema);

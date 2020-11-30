/**
 * @module model/ChatRoom
 */
import mongoose from "mongoose";
import { v4 as uuidv4 } from "uuid";

export const CHAT_ROOM_TYPES = {
    CONSUMER_TO_CONSUMER: "consumer-to-consumer",
    CONSUMER_TO_MULTI_CONSUMERS: "consumer-to-multiple-consumers",
};

/**
 * Representation of a change room between users
 * 
 * @class ChatRoomModel
 */
const chatRoomSchema = new mongoose.Schema(
    {
        _id: {
            type: String,
            default: () => uuidv4().replace(/\-/g, ""),
        },
        userIds: Array,
        chatInitiator: String,
    },
    {
        timestamps: true,
        collection: "chatrooms",
    }
);

/**
 * Retrieves an Array of ChatRooms with given userId
 * 
 * @param {String} userId - id of user
 * @return {Array} array of all chatroom that the user belongs to
 */
chatRoomSchema.statics.getChatRoomsByUserId = async function (userId) {
    const rooms = await this.find({ userIds: { $all: [userId] } });
    return rooms;
};
  
/**
 * Retrieves a chatRoom with given roomId
 * 
 * @param {String} roomId - id of chatroom
 * @return {Object} chatroom Object
 * @throws will throw an error if there is no room with given roomId
 */
chatRoomSchema.statics.getChatRoomByRoomId = async function (roomId) {
    const room = await this.findOne({ _id: roomId });
    if (!room) { throw ({ error: "No room with this id found" }); }
    return room;
};

/**
 * Creates a new chatroom
 * 
 * @param {Array} userIds - array of strings of userIds
 * @param {String} chatInitiator - the id of the person who created the chat
 * @returns {Object} - a status, message and the roomId
 */
chatRoomSchema.statics.initiateChat = async function (userIds, chatInitiator) {
    const availableRoom = await this.findOne({
        userIds: {
            $size: userIds.length,
            $all: [...userIds],
        },
    });
    if (availableRoom) {
        return {
            isNew: false,
            message: "retrieving an old chat room",
            chatRoomId: availableRoom._doc._id,
        };
    }

    const newRoom = await this.create({ userIds, chatInitiator });
    return {
        isNew: true,
        message: "creating a new chatroom",
        chatRoomId: newRoom._doc._id,
    };
};

/**
 * Retrieves the users that belong to a certain room
 * 
 * @param {String} roomId - the room id 
 * @returns {Array} an array of user ids
 */
chatRoomSchema.statics.getUserIdsFromRoomId = async function (roomId) {
    const userIds = await this.findOne({ _id: roomId }, "userIds");
    return userIds;
};

/**
 * Deletes the user from all chatrooms it is in
 * 
 * @param {String} userId - the userId of the user belonging to the chatrooms to delete
 */
chatRoomSchema.statics.deleteUserFromChatRooms = async function (userId) {
    const rooms = await this.getChatRoomsByUserId(userId);
    await Promise.all(rooms.map(async (room) => {
        const updatedRoom = await this.findOneAndUpdate({_id: room._id},{ $pull: { userIds : UserId}},{multi: true, new: true});
        // delete userless chatrooms
        if (updatedRoom.userIds.length <= 1) {
            await this.deleteOne({_id: room._id});
        }
    }));
};

export default mongoose.model("ChatRoom", chatRoomSchema);
import mongoose from "mongoose";
import { v4 as uuidv4 } from "uuid";

export const CHAT_ROOM_TYPES = {
    CONSUMER_TO_CONSUMER: "consumer-to-consumer",
    CONSUMER_TO_MULTI_CONSUMERS: "consumer-to-multiple-consumers",
};

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
 * @param {String} userId - id of user
 * @return {Array} array of all chatroom that the user belongs to
 */
chatRoomSchema.statics.getChatRoomsByUserId = async function (userId) {
    const rooms = await this.find({ userIds: { $all: [userId] } });
    return rooms;
};
  
/**
 * @param {String} roomId - id of chatroom
 * @return {Object} chatroom
 */
chatRoomSchema.statics.getChatRoomByRoomId = async function (roomId) {
    const room = await this.findOne({ _id: roomId });
    if (!room) { throw ({ error: "No room with this id found" }); }
    return room;
};

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

chatRoomSchema.statics.getUserIdsFromRoomId = async function (roomId) {
    const userIds = await this.findOne({ _id: roomId }, "userIds");
    return userIds;
};


export default mongoose.model("ChatRoom", chatRoomSchema);
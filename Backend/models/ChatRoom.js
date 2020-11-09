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
        type: String,
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
    try {
        const rooms = await this.find({ userIds: { $all: [userId] } });
        return rooms;
    } catch (error) {
        throw error;
    }
};
  
/**
 * @param {String} roomId - id of chatroom
 * @return {Object} chatroom
 */
chatRoomSchema.statics.getChatRoomByRoomId = async function (roomId) {
    try {
        const room = await this.findOne({ _id: roomId });
        if (!room) { throw ({ error: "No room with this id found" }); }
        return room;
    } catch (error) {
        throw error;
    }
};

chatRoomSchema.statics.initiateChat = async function (
	userIds, type, chatInitiator
) {
    try {
        const availableRoom = await this.findOne({
            userIds: {
                $size: userIds.length,
                $all: [...userIds],
            },
            type,
        });
        if (availableRoom) {
            return {
                isNew: false,
                message: "retrieving an old chat room",
                chatRoomId: availableRoom._doc._id,
                type: availableRoom._doc.type,
            };
        }

        const newRoom = await this.create({ userIds, type, chatInitiator });
        return {
            isNew: true,
            message: "creating a new chatroom",
            chatRoomId: newRoom._doc._id,
            type: newRoom._doc.type,
        };
    } catch (error) {
        throw error;
    }
};

chatRoomSchema.statics.getUserIdsFromRoomId = async function (roomId) {
    try {
        const userIds = await this.findOne({ _id: roomId }, "userIds");
        return userIds;
    } catch (error) {
        throw error;
    }
};


export default mongoose.model("ChatRoom", chatRoomSchema);
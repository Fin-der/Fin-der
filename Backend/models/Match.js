import mongoose from "mongoose";
import UserModel from "./User.js";
import { v4 as uuidv4 } from "uuid";

// TODO: add error checking (cant find)
// Not actually necessary except for finding mutuals
const MatchVertexSchema = new mongoose.Schema(
    {
        _id: {
            type: String,
            default: () => uuidv4().replace(/\-/g, ""),
        },
        user: UserModel.schema,
        matches: [UserModel.schema],
    },
    {
        timestamps: false,
        collection: "matchVertices",
    }
);

const MatchEdgeSchema = new mongoose.Schema(
    {
        _id: {
            type: String,
            default: () => uuidv4().replace(/\-/g, ""),
        },
        status: {
            type: String,
            enum: ["declined", "potential", "approved"],
            default: "potential",
        },
        score: Number,
        from: UserModel.schema,
        fromStatus: {
            type: String,
            enum: ["declined", "potential", "approved"],
            default: "potential",
        },
        to: UserModel.schema,
        toStatus: {
            type: String,
            enum: ["declined", "potential", "approved"],
            default: "potential",
        },
    },
    {
        timestamps: false,
        collection: "matchEdges",
    }
);

MatchVertexSchema.statics.createMatchVertex = async function (newUser, potentialMatches) {
    const vertex = await this.create({user: newUser, matches: potentialMatches});
    return vertex;
};

MatchVertexSchema.statics.addPotentialMatch = async function (userId, otherUserId) {
    const user = await UserModel.getUserById(userId);
    const otherUser = await UserModel.getUserById(otherUserId);
    const userVertex = await this.update({user},{$push: {matches: otherUser}});
    return userVertex;
};

MatchEdgeSchema.statics.getPotentialMatches = async function (userId) {
    const edges = await this.find({"from._id": userId, status: "potential"});
    console.log(edges);
    return edges;
};

MatchEdgeSchema.statics.getFriendMatches = async function (userId) {
    const edges = await this.find({"from._id": userId, status: "approved"});
    return edges;
};

MatchEdgeSchema.statics.createBidirectionalEdge = async function (score, userId1, userId2) {
    const user1 = await UserModel.getUserById(userId1);
    const user2 = await UserModel.getUserById(userId2);
    const edge1 = await this.create({score, from: user1, to: user2});
    const edge2 = await this.create({score, from: user2, to: user1});
    return edge1, edge2;
};

MatchEdgeSchema.statics.changeMatchStatus = async function (matchId, userId, status) {
    const options = {
        multi: true
    };
    const match = await this.findOne({_id: matchId}).lean();
    const otherMatch = await this.findOne({from: match.to, to: match.from });
    await this.updateToFromMatchStatus(match, otherMatch, userId, status, options);
    await this.determineMatchStatus(matchId, options);
    return await this.findOne({_id: matchId});
};

MatchEdgeSchema.statics.updateToFromMatchStatus = async function (match, otherMatch, userId, status, options) {
    if (match.from._id === userId) {
        await this.updateOne({_id: match._id}, {$set: {fromStatus: status}}, options);
        await this.updateOne({_id: otherMatch._id}, {$set: {toStatus: status}}, options);
    } else if (match.to._id === userId) {
        await this.updateOne({_id: match._id}, {$set: {toStatus: status}}, options);
        await this.updateOne({_id: otherMatch._id}, {$set: {fromStatus: status}}, options);
    } else {
        throw ({ error: "User is not a part of this match"});
    }
    return;
};

MatchEdgeSchema.statics.determineMatchStatus = async function (matchId, options) {
    const match = await this.findOne({_id: matchId}).lean();
    const otherMatch = await this.findOne({from: match.to, to: match.from });
    await this.checkApprovedStatus(match, otherMatch, options);
    await this.checkDeclinedStatus(match, otherMatch, options);
    return;
};

MatchEdgeSchema.statics.checkApprovedStatus = async function (match, otherMatch, options) {
    if (match.toStatus === "approved" && match.fromStatus === "approved") {
        await this.updateOne({_id: match._id}, {$set: {status: "approved"}}, options);
        await this.updateOne({_id: otherMatch._id}, {$set: {status: "approved"}}, options);
    }
    return;
};

MatchEdgeSchema.statics.checkDeclinedStatus = async function (match, otherMatch, options) {
    if (match.toStatus === "declined" || match.fromStatus === "declined") {
        await this.updateOne({_id: match._id}, {$set: {status: "declined"}}, options);
        await this.updateOne({_id: otherMatch._id}, {$set: {status: "declined"}}, options);
    } 
    return;
};

const MatchVertexModel = mongoose.model("matchVertex", MatchVertexSchema);
const MatchEdgeModel = mongoose.model("matchEdge", MatchEdgeSchema);
export {
    MatchVertexModel,
    MatchEdgeModel,
};

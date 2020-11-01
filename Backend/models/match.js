import mongoose from "mongoose";
import UserModel from "./User.js"
import { v4 as uuidv4 } from "uuid";
import User from "./User.js";

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
)

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
        from_approve: {
            type: String,
            enum: ["declined", "potential", "approved"],
            default: "potential",
        },
        to: UserModel.schema,
        to_approve: {
            type: String,
            enum: ["declined", "potential", "approved"],
            default: "potential",
        },
    },
    {
        timestamps: false,
        collection: "matchEdges",
    }
)

MatchVertexSchema.createMatchVertex = async function (user, potentialMatches) {
    try {
        // THIS will all go in add user
        var potentialMatches;
        // go through each vertex and find if interests are same
        // on success create bidirectional edge
        const curInterests = new Set(curUser.interests);
        const users = await UserModel.getUsers(); 
        users.forEach((user) => {
            if (user._id != curUser._id) {
                sameInterests = 0;
                user.interests.forEach((interest) => {
                    if (curInterests.has(interest)) {
                        sameInterests++;
                    }
                })
                if (sameInterests > 0) {
                    potentialMatches.push(user);
                    MatchEdgeModel.createBidirectionalEdge(sameInterests, cur_user, user);
                }
            }
        });

        const vertex = await this.create({user: user, matches: matches});
        return vertex;
    } catch (error) {
        throw error;
    }
}

MatchVertexSchema.potentialMatches = async function (userId) {
    try {
        const user = await UserModel.getUserById(userId);
        const edges = await MatchEdgeModel.find({from: user, status: "potential"});
        return edges;
    } catch (error) {
        throw error;
    }
}

MatchVertexSchema.findFriends = async function (userId) {
    try {
        const user = await UserModel.getUserById(userId);
        const edges = await MatchEdgeModel.find({from: user, status: "approved"});
        return edges;
    } catch (error) {
        throw error;
    }
}

MatchVertexSchema.addPotentialMatch = async function (userId, otherUserId) {
    try {
        const user = await UserModel.getUserById(userId);
        const otherUser = await UserModel.getUserById(otherUserId);
        const userVertex = await this.find({user: user});
        userVertex.matches.push(otherUser);
        userVertex.save(done);
        return userVertex;
    } catch (error) {
        throw error;
    }
}

MatchEdgeSchema.createBidirectionalEdge = async function (score, userId1, userId2) {
    try {
        const user1 = await UserModel.getUserById(userId1);
        const user2 = await UserModel.getUserById(userId2);
        const edge1 = await this.create({score: score, from: user1, to: user2});
        const edge2 = await this.create({score: score, from: user2, to: user1});
        return edge1, edge2;
    } catch (error) {
        throw error;
    }
}

MatchEdgeSchema.changeMatchStatus = async function (matchId, userId, status) {
    try {
        const match = await this.find({_id: matchId});
        const otherMatch = await this.find({from: match.to});
        const user = await UserModel.getUserById(userId);
        if (match.from == user) {
            match.from_status = status;
            otherMatch.to_status = status;
        } else if (match.to == user) {
            match.to_status = status;
            otherMatch.from_status = status;
        } else {
            throw ({ error: 'User is not a part of this match'})
        }

        if (match.to_status == "approved" && match.from_status == "approved") {
            match.status == "approved";
            otherMatch.status == "approved";
        } else if (match.to_status == "declined" || match.from_status == "declined") {
            match.status == "declined";
            otherMatch.status == "declined";
        }
        return match;
    } catch (error) {
        throw error;
    }
}

const MatchVertexModel = mongoose.model("matchVertex", MatchVertexSchema);
const MatchEdgeModel = mongoose.model("matchEdge", MatchEdgeSchema);
export {
    MatchEdgeModel,
    MatchVertexModel,
}

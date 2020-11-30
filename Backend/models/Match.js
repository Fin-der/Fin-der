/**
 * @modules model/Match
 */
import mongoose from "mongoose";
import UserModel from "./User.js";
import { v4 as uuidv4 } from "uuid";
import calcQuery from "../utils/MatchHelper.js";

/**
 * Representation of connections between Users
 * 
 * @class MatchVertexModel
 */
const MatchVertexSchema = new mongoose.Schema(
    {
        _id: {
            type: String,
            default: () => uuidv4().replace(/\-/g, ""),
        },
        userId: String,
        matchesId: [String],
    },
    {
        timestamps: false,
        collection: "matchVertices",
    }
);

/**
 * Representation of connections between Users
 * 
 * @class MatchEdgeModel
 */
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
        fromId: String,
        fromStatus: {
            type: String,
            enum: ["declined", "potential", "approved"],
            default: "potential",
        },
        toId: String,
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

/**
 * Creates a Match Vertex Object
 * 
 * @function createMatchVertex
 * @param {String} userId - The userId of the match vertex object
 * @param {Array} potentialMatchesId - An array of Strings of potential matches userIds
 * @returns {Object} The newly created Match Vertex Object
 */
MatchVertexSchema.statics.createMatchVertex = async function (userId, potentialMatchesId) {
    const vertex = await this.create({userId, matchesId: potentialMatchesId});
    return vertex;
};

/**
 * Deletes a Match Vertex Object
 * 
 * @function deleteMatchVertex
 * @param {String} id - the id of the vertex to delete
 * @returns {Object} details of the action performed
 */
MatchVertexSchema.statics.deleteMatchVertex = async function (id) {
    const vertex = await this.findOne({"userId": id});
    await Promise.all(vertex.matchesId.map(async (userId) => {
        await this.findOneAndUpdate({userId},{ $pull: { matches: id}},{multi: true});
    }));
    const result = await this.deleteOne({"userId": id});
    return result;
};

/**
 * Finds Users that are likely to match with the user of userId given
 * will first find friends of friends who fit the users preferences
 * and will then find other registered users
 * 
 * @function getUsersForMatching
 * @param {String} userId - id of the user to find matches for
 * @param {Object} options - options for pagination
 * @param {Number} options.skip - the amount of results to skip
 * @param {Number} options.limit - the maximum amount of results to show
 * @returns {Array} An array of User Objects  
 */
MatchVertexSchema.statics.getUsersForMatching = async function (userId, options) {
    
    const user = await UserModel.getUserById(userId);
    // Generate query using user preferences
    // generates the query to restrict search results by
    const generateQuery = (user) => {
        let query = {};
        if (typeof user.preferences !== "undefined") {
            calcQuery(user, query);
            query.interests = {$in: user.interests};
        }
        return query;
    };
    const query = generateQuery(user);

    // user graphLookup to find friends of friends
    const aggregate = await this.aggregate( [
    { $match: {userId} },
    { $graphLookup: { 
        from: "MatchVertices",
        startWith: "$matchesId",
        connectFromField: "matchesId",
        connectToField: "userId",
        maxDepth: 2,
        as: "mutuals",
        restrictSearchWithMatch: query
      }
    }]);
    let mutualCount = 0;
    if (typeof aggregate.mutuals !== "undefined" && aggregate.mutuals.length >= 0) {
        // if they have mutual connection we show them first then random users after that
        const mutuals = await UserModel.find({_id: {$in: aggregate.mutuals}}).skip(options.page * options.limit).limit(options.limit);
        if (mutuals?.length) {
            mutualCount++;
            return mutuals;
        } else {
            return await UserModel.find({_id: {$nin: aggregate.mutuals}}).skip(options.page * options.limit - mutualCount).limit(options.limit);
        }
    } else {
        return await UserModel.find().skip(options.page * options.limit).limit(options.limit);
    }
};

/**
 * Adds matches to a specified Vertex
 * 
 * @function addPotentialMatches
 * @param {String} userId - id of the user to add matches to
 * @param {Array} userIds - the array of users to add to the vertex with given userId 
 * @returns {Object} The updated user Vertex Object
 */
MatchVertexSchema.statics.addPotentialMatches = async function (userId, userIds) {
    const userVertex = await this.updateOne({userId}, {$push: {matches: { $each: userIds }}}, {multi: true});
    return userVertex;
};

/**
 * Creates a bi-direction edge by creating 2 directed edges
 * 
 * @function createBidirectionalEdge
 * @param {Number} score - the score or weight of the newly created edge
 * @param {String} userId1 - the user id of the one user
 * @param {String} userId2 - the user id of the other user
 * @returns {Array} An array with 2 elements: the two edges created
 */
MatchEdgeSchema.statics.createBidirectionalEdge = async function (score, userId1, userId2) {
    const edge1 = await this.create({score, fromId: userId1, toId: userId2});
    const edge2 = await this.create({score, fromId: userId2, toId: userId1});
    return [ edge1, edge2 ];
};

/**
 * Gets matches for the given userId
 * matches are denoted by having status "potential"
 * 
 * @function getPotentialMatches
 * @param {String} userId - id of the user to get matches for
 * @returns {Array} An array of MatchEdge Objects representing potentialmatches
 */
MatchEdgeSchema.statics.getPotentialMatches = async function (userId) {
    // we use fromId and fromStatus so that we don't return duplicate edges
    const edges = await this.find({"fromId": userId, fromStatus: "potential"});
    await Promise.all(edges.map(async (edge) => {
        edge.toId = await UserModel.findOne({_id: edge.toId});
        edge.fromId = await UserModel.findOne({_id: edge.fromId});
    }));
    
    return edges;
};

/**
 * Gets friends matches for the given userId
 * friends matches are denoted by having status "approved"
 * 
 * @function getPotentialMatches
 * @param {String} userId - id of the user to get matches for
 * @returns {Array} An array of MatchEdge Objects representing potentialmatches
 */
MatchEdgeSchema.statics.getFriendMatches = async function (userId) {
    const edges = await this.find({"fromId": userId, status: "approved"});
    await Promise.all(edges.map(async (edge) => {
        edge.toId = await UserModel.findOne({_id: edge.toId});
        edge.fromId = await UserModel.findOne({_id: edge.fromId});
    }));

    return edges;
};

/**
 * Deletes Edges with given userId
 * 
 * @function deleteEdgesWithId
 * @param {String} id - if of a user belonging to the match
 * @returns {Object} details of the action performed
 */
MatchEdgeSchema.statics.deleteEdgesWithId = async function (id) {
    const result = await this.deleteMany({ $or: [{"fromId": id}, {"toId": id}]});
    return result;
};

/**
 * Helper function for determineMatchStatus
 * Checks if the overall status should be changed
 * 
 * @param {Object} match - Edge Object representing one direction of a match
 * @param {Object} otherMatch - Edge Object representing the other direction of a match
 */
MatchEdgeSchema.statics.checkApprovedStatus = async function (match, otherMatch) {
    if (match.toStatus === "approved" && match.fromStatus === "approved") {       
        await this.updateOne({_id: match._id}, {$set: {status: "approved"}});
        await this.updateOne({_id: otherMatch._id}, {$set: {status: "approved"}});
    }
    return;
};

/**
 * Helper function for determineMatchStatus
 * Checks if the overall status should be changed
 * 
 * @param {Object} match - Edge Object representing one direction of a match
 * @param {Object} otherMatch - Edge Object representing the other direction of a match
 */
MatchEdgeSchema.statics.checkDeclinedStatus = async function (match, otherMatch) {
    if (match.toStatus === "declined" || match.fromStatus === "declined") {
        await this.updateOne({_id: match._id}, {$set: {status: "declined"}});
        await this.updateOne({_id: otherMatch._id}, {$set: {status: "declined"}});
    } 
};

/**
 * Helper function for changeMatchStatus
 * Updates the status of per direction statuses
 * 
 * @param {Object} match - Edge Object representing one direction of a match
 * @param {Object} otherMatch - Edge Object representing the other direction of a match
 * @param {String} userId - the userId of the side to update
 * @param {String} status - the status to update the edges with
 * @throws will throw an error if the userId isnt a part of match
 */
MatchEdgeSchema.statics.updateToFromMatchStatus = async function (match, otherMatch, userId, status) {
    if (match.fromId === userId) {
        await this.updateOne({_id: match._id}, {$set: {fromStatus: status}});
        await this.updateOne({_id: otherMatch._id}, {$set: {toStatus: status}});
    } else if (match.toId === userId) {
        await this.updateOne({_id: match._id}, {$set: {toStatus: status}});
        await this.updateOne({_id: otherMatch._id}, {$set: {fromStatus: status}});
    } else {
        throw ({ error: "User is not a part of this match"});
    }
};

/**
 * Helper function for changeMatchStatus
 * Updates the status of per direction statuses
 * 
 * @param {String} matchId - the match to change the status of
 */
MatchEdgeSchema.statics.determineMatchStatus = async function (matchId) {
    const match = await this.findOne({_id: matchId}).lean();
    const otherMatch = await this.findOne({fromId: match.toId, toId: match.fromId });
    await this.checkApprovedStatus(match, otherMatch);
    await this.checkDeclinedStatus(match, otherMatch);
};

/**
 * 
 * @function changeMatchStatus
 * @param {String} matchId - the match to change the status of
 * @param {String} userId - the user who changed their per side status
 * @param {String} status - the status to update with
 * @returns {Object} The updated match edge object
 */
MatchEdgeSchema.statics.changeMatchStatus = async function (matchId, userId, status) {
    const match = await this.findOne({_id: matchId}).lean();
    const otherMatch = await this.findOne({fromId: match.toId, toId: match.fromId });
    await this.updateToFromMatchStatus(match, otherMatch, userId, status);
    await this.determineMatchStatus(matchId);
    return await this.findOne({_id: matchId});
};

const MatchVertexModel = mongoose.model("matchVertex", MatchVertexSchema);
const MatchEdgeModel = mongoose.model("matchEdge", MatchEdgeSchema);
export {
    MatchVertexModel,
    MatchEdgeModel,
};

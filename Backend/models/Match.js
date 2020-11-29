import mongoose from "mongoose";
import UserModel from "./User.js";
import { v4 as uuidv4 } from "uuid";

// TODO: add error checking (cant find)
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

MatchVertexSchema.statics.createMatchVertex = async function (userId, potentialMatchesId) {
    const vertex = await this.create({userId, matchesId: potentialMatchesId});
    return vertex;
};

MatchVertexSchema.statics.deleteMatchVertex = async function (id) {
    const result = await this.deleteOne({"userId": id});
    return result;
};

MatchVertexSchema.statics.getUsersForMatching = async function (userId, options) {
    
    const user = await UserModel.getUserById(userId);
    // Generate query using user preferences
    const wrapLng = (lng) => {
        var result = lng;
        if (lng > 180) {
            result = lng - 360;
        } else if (lng < -180) {
            result = lng + 360;
        }
    };
    const wrapLat = (lat) => {
        var result = lat;
        if (lat > 90) {
            result = 180 - lat;
        } else if (lat < -90) {
            result = lat + 180;
        }
        return result;
    };
    const calcProxQuery = (user, query) => {
        if (typeof user.preferences.proximity !== "undefined") {
            // Numbers and Formulae from 
            // https://stackoverflow.com/questions/1253499/simple-calculations-for-working-with-lat-lon-and-km-distance
            const latKmPerDeg = 110.574;
            const lngKmPerDeg = 111.32;
            const latProximityDeg = user.preferences.proximity / latKmPerDeg;
            const lngProximityDeg = user.preferences.proximity / (lngKmPerDeg * Math.cos(user.geoLocation.lat * Math.PI / 180));
            query.geoLocation = new Object();
            query.geoLocation.lng = { 
                $gt: wrapLng(user.geoLocation.lng - lngProximityDeg),
                $lt: wrapLng(user.geoLocation.lng + lngProximityDeg)
            };
            query.geoLocation.lat = {
                $gt: wrapLat(user.geoLocation.lat - latProximityDeg),
                $lt: wrapLat(user.geoLocation.lat + latProximityDeg)
            };
        }
    };
    const calcQuery = (user, query) => {
        if (typeof user.preferences.gender !== "undefined" && user.preferences.gender !== "All") {
            query.gender = user.preferences.gender;
        }
        if (typeof user.preferences.ageRange !== "undefined") {
            query.age = {$gt: user.preferences.ageRange.min, $lt: user.preferences.ageRange.max};
        }
        calcProxQuery;
    };
    const generateQuery = (user) => {
        let query = {};
        if (typeof user.preferences !== "undefined") {
            calcQuery(user, query);
            query.interests = {$in: user.interests};
        }
        return query;
    };
    const query = generateQuery(user);

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

MatchVertexSchema.statics.addPotentialMatches = async function (userId, userIds) {
    const userVertex = await this.updateOne({userId}, {$push: {matches: { $each: userIds }}}, {multi: true});
    return userVertex;
};

MatchEdgeSchema.statics.getPotentialMatches = async function (userId) {
    const edges = await this.find({"fromId": userId, fromStatus: "potential"});
    await Promise.all(edges.map(async (edge) => {
        edge.toId = await UserModel.findOne({_id: edge.toId});
        edge.fromId = await UserModel.findOne({_id: edge.fromId});
    }));
    
    return edges;
};

MatchEdgeSchema.statics.getFriendMatches = async function (userId) {
    const edges = await this.find({"fromId": userId, status: "approved"});
    await Promise.all(edges.map(async (edge) => {
        edge.toId = await UserModel.findOne({_id: edge.toId});
        edge.fromId = await UserModel.findOne({_id: edge.fromId});
    }));

    return edges;
};

MatchEdgeSchema.statics.createBidirectionalEdge = async function (score, userId1, userId2) {
    const edge1 = await this.create({score, fromId: userId1, toId: userId2});
    const edge2 = await this.create({score, fromId: userId2, toId: userId1});
    return [ edge1, edge2 ];
};

MatchEdgeSchema.statics.deleteEdgesWithId = async function (id) {
    const result = await this.deleteMany({ $or: [{"fromId": id}, {"toId": id}]});
    return result;
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

MatchEdgeSchema.statics.updateToFromMatchStatus = async function (match, otherMatch, userId, status, options) {
    if (match.fromId === userId) {
        await this.updateOne({_id: match._id}, {$set: {fromStatus: status}}, options);
        await this.updateOne({_id: otherMatch._id}, {$set: {toStatus: status}}, options);
    } else if (match.toId === userId) {
        await this.updateOne({_id: match._id}, {$set: {toStatus: status}}, options);
        await this.updateOne({_id: otherMatch._id}, {$set: {fromStatus: status}}, options);
    } else {
        throw ({ error: "User is not a part of this match"});
    }
    return;
};

MatchEdgeSchema.statics.determineMatchStatus = async function (matchId, options) {
    const match = await this.findOne({_id: matchId}).lean();
    const otherMatch = await this.findOne({fromId: match.toId, toId: match.fromId });
    await this.checkApprovedStatus(match, otherMatch, options);
    await this.checkDeclinedStatus(match, otherMatch, options);
    return;
};

MatchEdgeSchema.statics.changeMatchStatus = async function (matchId, userId, status) {
    const options = {
        multi: true
    };
    const match = await this.findOne({_id: matchId}).lean();
    const otherMatch = await this.findOne({fromId: match.toId, toId: match.fromId });
    await this.updateToFromMatchStatus(match, otherMatch, userId, status, options);
    await this.determineMatchStatus(matchId, options);
    return await this.findOne({_id: matchId});
};

const MatchVertexModel = mongoose.model("matchVertex", MatchVertexSchema);
const MatchEdgeModel = mongoose.model("matchEdge", MatchEdgeSchema);
export {
    MatchVertexModel,
    MatchEdgeModel,
};

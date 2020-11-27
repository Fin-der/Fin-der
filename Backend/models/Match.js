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

// Note this function removes the timestamps and _v fields
// this is fine because those fields aren't used
MatchVertexSchema.statics.updateMatchVertex = async function (id, updateInfo) {
    const updateInfoWithId = Object.assign({_id: id}, updateInfo);
    const vertex = await this.findOneAndUpdate({"user._id": id}, {user: updateInfoWithId}, {new: true});
    await Promise.all(vertex.matches.map(async (user) => {
        await this.updateOne({"user._id": user._id,},{"matches.$[element]" : updateInfoWithId},{multi: true, arrayFilters: [ { "element._id": id}]});
    })); 
    
    return vertex;
};

MatchVertexSchema.statics.deleteMatchVertex = async function (id) {
    const vertex = await this.findOne({"user._id": id});
    await Promise.all(vertex.matches.map(async (user) => {
        await this.updateOne({"user._id": user._id},{ $pull: { matches: { _id: id }}},{multi: true});
    }));
    const result = await this.deleteOne({"user._id": id});
    return result;
};

MatchVertexSchema.statics.getUsersForMatching = async function (userId, options) {
    const user = UserModel.getUserById(userId);
    let query = {};
    // Generate query using user preferences
    if (typeof user.preferences !== "undefined") {
        if (typeof user.preferences.gender !== "undefined") {
            query.gender = user.preferences.gender;
        }
        if (typeof user.preferences.ageRange !== "undefined") {
            query.age = {$gt: user.preferences.ageRange.min, $lt: user.preferences.ageRange.max};
        }
        if (typeof user.preferences.proximity !== "undefined") {
            // Numbers and Formulae from 
            // https://stackoverflow.com/questions/1253499/simple-calculations-for-working-with-lat-lon-and-km-distance
            const latKmPerDeg = 110.574;
            const lngKmPerDeg = 111.32;
            const latProximityDeg = user.preferences.proximity / latKmPerDeg;
            const lngProximityDeg = user.preferences.proximity / (lngKmPerDeg * Math.cos(user.location.lat * Math.PI / 180));
            query.location = new Object();
            query.location.lng = { 
                $gt: user.location.lng - lngProximityDeg,
                $lt: user.location.lng + lngProximityDeg
            };
            query.location.lat = {
                $gt: user.location.lat - latProximityDeg,
                $lt: user.location.lat + latProximityDeg
            };
        }
    }

    const aggregate = await this.aggregate( [
    { $match: { "user._id": userId }},
    { $graphLookup: { 
        from: "MatchVertices",
        startWith: "$matches",
        connectFromField: "matches",
        connectToField: "user",
        maxDepth: 2,
        as: "mutuals",
        restrictSearchWithMatch: { query }
      }
    }]);
    if (typeof aggregate.mutuals !== "undefined" && aggregate.mutuals.length >= options.limit / 2) {
        return aggregate.mutuals;
    } else {
        return UserModel.find().skip(options.page * options.limit).limit(options.limit);
    }
}

MatchVertexSchema.statics.addPotentialMatches = async function (userId, users) {
    const user = await UserModel.getUserById(userId);
    const userVertex = await this.updateOne({user}, {$push: {matches: { $each: users }}}, {multi: true});
    return userVertex;
};

MatchEdgeSchema.statics.getPotentialMatches = async function (userId) {
    const edges = await this.find({"from._id": userId, fromStatus: "potential"});
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
    return [ edge1, edge2 ];
};

MatchEdgeSchema.statics.updateEdgesWithId = async function (id, updateInfo) {
    const updateInfoWithId = Object.assign({_id: id}, updateInfo);
    const result = await this.updateMany(
        {"from._id": id}, 
        {from: updateInfoWithId}
    );
    await this.updateMany(
        {"to._id": id},
        {to: updateInfoWithId}
    );
    return result;
}

MatchEdgeSchema.statics.deleteEdgesWithId = async function (id) {
    const result = await this.deleteMany({ $or: [{"from._id": id}, {"to._id": id}]});
    return result;
}

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

const MatchVertexModel = mongoose.model("matchVertex", MatchVertexSchema);
const MatchEdgeModel = mongoose.model("matchEdge", MatchEdgeSchema);
export {
    MatchVertexModel,
    MatchEdgeModel,
};

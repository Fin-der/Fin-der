import mongoose from "mongoose";
import UserModel from "./User.js"
import { v4 as uuidv4 } from "uuid";

const MatchVertexSchema = new mongoose.Schema(
    {
        _id: {
            type: String,
            default: () => uuidv4().replace(/\-/g, ""),
        },
        user: {
            type: mongoose.Schema.ObjectId, ref: 'User'
        },
        matches: [{type: mongoose.Schema.ObjectId, ref: 'User'}],
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
        from: {
            type: mongoose.Schema.ObjectId, ref: 'User'
        },
        to: {
            type: mongoose.Schema.ObjectId, ref: 'User'
        },
    },
    {
        timestamps: false,
        collection: "matchEdges",
    }
)

export default mongoose.model("matchGraph", MatchVertexSchema);

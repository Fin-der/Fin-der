import { app, port } from "../../../app.js"; // Link to your server file
import http from "http";
import mongoose, {ValidationError} from "mongoose";
import RoomModel from "../../../models/ChatRoom.js";
import {MatchVertexModel, MatchEdgeModel} from "../../../models/Match.js";
import { hasUncaughtExceptionCaptureCallback } from "process";

describe("test user models", () => {

    var server;


    beforeEach(async () => {
        const { collections } = mongoose.connection;

        for (const key in collections) {
            const collection = collections[key];
            await collection.deleteMany();
        }
    });
    
    beforeAll(async () => {
        // Setup
        await mongoose.connect("mongodb://localhost:27017/test", { useNewUrlParser: true, useUnifiedTopology: true  });
        server = http.createServer(app);
        server.listen(port);
    });

    afterAll(async () => {
        // Cleanup
        await mongoose.connection.close();
        server.close();
    });



});
import { app, port } from "../../app.js"; // Link to your server file
import http from "http";
import supertest from 'supertest';
import mongoose from "mongoose";

const request = supertest(app);

describe("matching integration", () => {

    var server;
    beforeEach(async () => {
        const { collections } = mongoose.connection;

        for (const key in collections) {
            if (Object.prototype.hasOwnProperty.call(collections, key)) {
                await collections[key].deleteMany();
            }
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

    const user1 = {
        _id: "eb176894d456475e9f2be1868bab8fd6",
        firstName: "Foo",
        lastName: "Bar",
        interests: ["music", "coding"]
    };
    const user2 = {
        _id: "42",
        firstName: "Far",
        lastName: "Boo",
        interests: ["music", "skating"]
    };
    const user3 = {
        _id: "42222",
        firstName: "Jason",
        lastName: "Borne",
        interests: ["numbers", "masonry"]
    };
    const user4 = {
        _id: "4222444",
        firstName: "Jason",
        lastName: "Borne",
        interests: ["music", "masonry"]
    };

    it("IntegrationTest Matching", async (done) => {
        // populate users
        let response = await request.post("/users")
                        .send(user1);
        expect(response.status).toBe(200);
        response = await request.post("/users")
                       .send(user2);
        expect(response.status).toBe(200);
        response = await request.post("/users")
                       .send(user3);
        expect(response.status).toBe(200);
        response = await request.post("/users")
                       .send(user4);
        expect(response.status).toBe(200);
        // check for match between user1 and user2
        response = await request.get("/match/" + user1._id);
        expect(response.status).toBe(200);
        const matchId = response.body.matches[0]._id;
        response = await request.get("/match/" + user2._id);
        expect(response.status).toBe(200);
        expect(response.body.matches[0]._id === matchId);
        // approve match from user1 side
        response = await request.put("/match/approve/" + matchId + "/" + user1._id);
        expect(response.status).toBe(200);
        // approve match from user2 side
        response = await request.put("/match/approve/" + matchId + "/" + user2._id);
        expect(response.status).toBe(200);
        // user3 is lonely
        response = await request.put("/match/approve/" + matchId + "/" + user3._id);
        expect(response.status).toBe(500);
        // check for mutual friendship
        response = await request.get("/match/friend/" + user1._id);
        expect(response.status).toBe(200);
        expect(response.body.friends.status === "approved");
        response = await request.get("/match/friend/" + user2._id);
        expect(response.status).toBe(200);
        expect(response.body.friends.status === "approved");
        // one user thinks the friendship is bad and unfriends
        response = await request.put("/match/decline/" + matchId + "/" + user2._id);
        expect(response.status).toBe(200);
        // check successful unfriend
        response = await request.get("/match/friend/" + user1._id);
        expect(response.status).toBe(200);
        expect(response.body.friends === []);
        response = await request.get("/match/friend/" + user2._id);
        expect(response.status).toBe(200);
        expect(response.body.friends === []);



        done();
    });

});
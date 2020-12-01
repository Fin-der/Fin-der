import { app, port } from "../../app.js"; // Link to your server file
import http from "http";
import supertest from "supertest";
import mongoose from "mongoose";

const request = supertest(app);

describe("matching integration", () => {

    var server;
    beforeEach(async () => {
        await mongoose.connection.db.dropDatabase();
    });

    beforeAll(async () => {
        // Setup
        await mongoose.connect("mongodb://localhost:27017/test", { 
            useNewUrlParser: true,
            useUnifiedTopology: true,
            useCreateIndex: true,
            useFindAndModify: false  
        });
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
    const pickyUser = {
        _id: "1093",
        firstName: "Mike",
        lastName: "Hawk",
        interests: ["skating", "music"],
        geoLocation: {
            lat: 0,
            lng: 0
        },
        preferences: {
            gender: "Male",
            ageRange: {
                min: 0,
                max: 99
            },
            proximity: 1000
        }
    };

    it("IntegrationTest Matching ", async (done) => {
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
        response = await request.post("/users")
                       .send(pickyUser);
        expect(response.status).toBe(200);
        // check for match between user1 and user2
        response = await request.get("/match/" + user1._id);
        expect(response.status).toBe(200);
        var matchId;
        for (const match of response.body.matches) {
            if (match.fromId === user1._id &&
                match.toId   === user2._id) {
                matchId = match._id;
            }
        }
        response = await request.get("/match/" + user2._id);
        expect(response.status).toBe(200);
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
        expect(response.body.friends[0].status).toBe("approved");
        response = await request.get("/match/friend/" + user2._id);
        expect(response.status).toBe(200);
        expect(response.body.friends[0].status).toBe("approved");
        // one user thinks the friendship is bad and unfriends
        response = await request.put("/match/decline/" + matchId + "/" + user2._id);
        expect(response.status).toBe(200);
        // check successful unfriend
        response = await request.get("/match/friend/" + user1._id);
        expect(response.status).toBe(200);
        expect(response.body.friends).toMatchObject([]);
        response = await request.get("/match/friend/" + user2._id);
        expect(response.status).toBe(200);
        expect(response.body.friends).toMatchObject([]);
        // check that picky user and match with people
        response = await request.get("/match/" + pickyUser._id);
        expect(response.status).toBe(200);
        expect(response.body.matches.length).toBe(3);
        done();
    });

});
import { app, port } from "../../app.js"; // Link to your server file
import http from "http";
import supertest from "supertest";
import mongoose from "mongoose";

const request = supertest(app);

describe("user creation integration test", () => {

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
        server.listen(port + 1);
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
        interests: ["music", "skating"],
        preferences: {
            ageRange: {
                max: 2,
                min: 0
            }
        }
    };
    const user3 = {
        _id: "42222",
        firstName: "Jason",
        lastName: "Borne",
        interests: ["numbers", "masonry"],
        preferences: {
            ageRange: {
                max: 2
            }
        }
    };
    const user4 = {
        _id: "4222444",
        firstName: "Jason",
        lastName: "Borne",
        interests: ["music", "masonry"],
        preferences: {
            ageRange: {
                min: 0
            }
        }
    };
    const user5 = {
        _id: "42234644",
        firstName: "Ron",
        lastName: "Don",
        interests: ["music", "masonry"],
        preferences: {
            ageRange: {
                max: 23,
                min: 76
            }
        }
    };
    const FCMToken = "234878239487";
    it("IntegrationTest Matching", async (done) => {
        // populate users
        let response = await request.post("/users")
                        .send(user1);
        expect(response.status).toBe(200);
        expect(response.body.user).toMatchObject(user1);
        response = await request.post("/users")
                        .send(user1);
        expect(response.status).toBe(500);
        response = await request.post("/users")
                       .send(user2);
        expect(response.status).toBe(200);
        expect(response.body.user).toMatchObject(user2);
        response = await request.post("/users")
                       .send(user3);
        expect(response.status).toBe(200);
        expect(response.body.user).toMatchObject(user3);
        response = await request.post("/users")
                       .send(user4);
        expect(response.status).toBe(200);
        expect(response.body.user).toMatchObject(user4);
        response = await request.post("/users")
                       .send(user5);
        expect(response.status).toBe(500);
        // get all users
        response = await request.get("/users");
        expect(response.status).toBe(200);
        expect(response.body.users).toMatchObject([user1, user2, user3, user4]);
        // get users by id
        response = await request.get("/users/" + user1._id);
        expect(response.status).toBe(200);
        expect(response.body.user).toMatchObject(user1);
        response = await request.get("/users/" + user2._id);
        expect(response.status).toBe(200);
        expect(response.body.user).toMatchObject(user2);
        response = await request.get("/users/" + user3._id);
        expect(response.status).toBe(200);
        expect(response.body.user).toMatchObject(user3);
        response = await request.get("/users/" + "2394078");
        expect(response.status).toBe(500);
        // delete user
        response = await request.delete("/users/" + user4._id);
        expect(response.status).toBe(200);
        response = await request.get("/users/" + user4._id);
        expect(response.status).toBe(500);
        // update user
        // user doesnt exist
        response = await request.put("/users/" + user5._id);
        expect(response.status).toBe(500);
        response = await request.put("/users/" + user2._id)
                                .send(user1);
        expect(response.status).toBe(200);
        let user = JSON.parse(JSON.stringify(user1));
        user._id = user2._id; //user keeps their _id
        expect(response.body.user).toMatchObject(user);
        // register FCM tokens
        response = await request.put("/users/" + user3._id + "/" + FCMToken);
        expect(response.status).toBe(200);
        expect(response.body.message).toBe("Token: " + FCMToken + " successfully registed with User(ID): " + user3._id);
        done();
    });

});
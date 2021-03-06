import { app, port } from "../../app.js"; // Link to your server file
import http from "http";
import supertest from "supertest";
import mongoose from "mongoose";
import admin from "../../config/firebase-config.js";

const request = supertest(app);

describe("room creation integration test", () => {

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
        interests: ["music", "coding"],
        FCMToken:"eKCJ5OGkShKZHpBucPqurJ:APA91bFYBg3-0OVdJFSO1TLmZUqXLQzIIgVoekAdUiz8-SR4i3FNr9bjK0c1Sph2qfcfzUplHHn_EkrfV14scu2XdoLgC1wyVhxtH_sdndClOs6qTvfQxScvtfmUSiSV5clqHpqBa4V0"
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
        },
        FCMToken:"eKCJ5OGkShKZHpBucPqurJ:APA91bFYBg3-0OVdJFSO1TLmZUqXLQzIIgVoekAdUiz8-SR4i3FNr9bjK0c1Sph2qfcfzUplHHn_EkrfV14scu2XdoLgC1wyVhxtH_sdndClOs6qTvfQxScvtfmUSiSV5clqHpqBa4V0"
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
        },
        FCMToken:"eKCJ5OGkShKZHpBucPqurJ:APA91bFYBg3-0OVdJFSO1TLmZUqXLQzIIgVoekAdUiz8-SR4i3FNr9bjK0c1Sph2qfcfzUplHHn_EkrfV14scu2XdoLgC1wyVhxtH_sdndClOs6qTvfQxScvtfmUSiSV5clqHpqBa4V0"
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
        },
        FCMToken:"eKCJ5OGkShKZHpBucPqurJ:APA91bFYBg3-0OVdJFSO1TLmZUqXLQzIIgVoekAdUiz8-SR4i3FNr9bjK0c1Sph2qfcfzUplHHn_EkrfV14scu2XdoLgC1wyVhxtH_sdndClOs6qTvfQxScvtfmUSiSV5clqHpqBa4V0"
    };

    const badUser = {
        _id: "42234644",
        firstName: "Ron",
        lastName: "Don",
        interests: ["music", "masonry"],
        preferences: {
            ageRange: {
                max: 23,
                min: 76
            }
        },
        FCMToken:"eKCJ5OGkShKZHpBucPqurJ:APA91bFYBg3-0OVdJFSO1TLmZUqXLQzIIgVoekAdUiz8-SR4i3FNr9bjK0c1Sph2qfcfzUplHHn_EkrfV14scu2XdoLgC1wyVhxtH_sdndClOs6qTvfQxScvtfmUSiSV5clqHpqBa4V0"
    };

    const room1UserIds = [user4._id, user3._id, user2._id, user1._id];
    
    const user1Message = "Hello, Gamer";
    const user2Message = "Hello, Fellow Gamer";
    const user3Message = "Wassup My Homies";
    const user4Message = "Hello Hello Hello";

    const room1Messages = [user4Message, user3Message, user2Message, user1Message];
    
    it("IntegrationTest Chatting", async (done) => {
        admin.messaging().send = jest.fn().mockImplementation(() => ({
            then: jest.fn().mockImplementation(() => ({ 
                catch: jest.fn().mockResolvedValue()
            }))
        }));
        admin.messaging().sendMulticast = jest.fn().mockImplementation(() => ({
            then: jest.fn().mockResolvedValue()
        }));
        // populate users
        let response = await request.post("/users")
                        .send(user1);
        expect(response.status).toBe(200);
        expect(response.body.user).toMatchObject(user1);
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

        // initiate room
        response = await request.post("/room/initiate")
                            .send({"userIds": room1UserIds, "userId": user1._id});
        expect(response.status).toBe(200);
        expect(response.body.chatRoom.chatRoomId).toEqual(expect.anything());
        expect(response.body.chatRoom.isNew).toEqual(true);

        const roomId = response.body.chatRoom.chatRoomId;

        // initiate room
        response = await request.post("/room/initiate")
            .send({"userIds": room1UserIds, "userId": user1._id});
        expect(response.status).toBe(200);
        expect(response.body.chatRoom.chatRoomId).toEqual(expect.anything());
        expect(response.body.chatRoom.isNew).toEqual(false);


        

        // fail initiate room
        response = await request.post("/room/initiate");
        expect(response.status).toBe(500);

        // postMessage success
        response = await request.post("/room/" + roomId + "/" + user1._id + "/message")
            .send({"messageText": user1Message});
        expect(response.status).toBe(200);
        expect(response.body.post.message.messageText).toEqual(user1Message);
        expect(response.body.post.postedByUser).toEqual(user1._id);
        expect(response.body.post.chatRoomId).toEqual(roomId);
        response = await request.post("/room/" + roomId + "/" + user2._id + "/message")
            .send({"messageText": user2Message});
        expect(response.status).toBe(200);
        expect(response.body.post.message.messageText).toEqual(user2Message);
        expect(response.body.post.postedByUser).toEqual(user2._id);
        expect(response.body.post.chatRoomId).toEqual(roomId);
        response = await request.post("/room/" + roomId + "/" + user3._id + "/message")
            .send({"messageText": user3Message});
        expect(response.status).toBe(200);
        expect(response.body.post.message.messageText).toEqual(user3Message);
        expect(response.body.post.postedByUser).toEqual(user3._id);
        expect(response.body.post.chatRoomId).toEqual(roomId);
        response = await request.post("/room/" + roomId + "/" + user4._id + "/message")
            .send({"messageText": user4Message});
        expect(response.status).toBe(200);
        expect(response.body.post.message.messageText).toEqual(user4Message);
        expect(response.body.post.postedByUser).toEqual(user4._id);
        expect(response.body.post.chatRoomId).toEqual(roomId);

        // postMessage fail
        response = await request.post("/room/" + roomId + "/" + badUser._id + "/message");
        expect(response.status).toBe(500);

        // getRecentConversation success
        response = await request.get("/room").send({"userId": user1._id});
        expect(response.status).toBe(200);
        expect(response.body.conversation[0].chatRoomId).toEqual(roomId);
        expect(response.body.conversation[0].message.messageText).toEqual(user4Message);
        expect(response.body.conversation[0].postedByUser).toEqual(user4._id);

        // getConversationByRoomId success
        response = await request.get("/room/" + roomId + "/" + "0");
        expect(response.status).toBe(200);
        expect(response.body.conversation.length).toEqual(4);
        expect(response.body.conversation[0].chatRoomId).toEqual(roomId);
        // duplication cause codacy is dumb >:(
        expect(response.body.conversation[0].message.messageText).toEqual(room1Messages[0]);
        expect(response.body.conversation[0].postedByUser).toEqual(room1UserIds[0]);
        expect(response.body.conversation[1].message.messageText).toEqual(room1Messages[1]);
        expect(response.body.conversation[1].postedByUser).toEqual(room1UserIds[1]);
        expect(response.body.conversation[2].message.messageText).toEqual(room1Messages[2]);
        expect(response.body.conversation[2].postedByUser).toEqual(room1UserIds[2]);
        expect(response.body.conversation[3].message.messageText).toEqual(room1Messages[3]);
        expect(response.body.conversation[3].postedByUser).toEqual(room1UserIds[3]);

        // getConversationByRoomId fail
        response = await request.get("/room/" + "asdf" + "/" + "0");
        expect(response.status).toBe(500);

        // markConversation Read success
        response = await request.put("/room/" + roomId + "/" + user1._id + "/mark-read");
        expect(response.status).toBe(200);
        expect(response.body.data).toEqual(expect.anything());

        // markConversation room doesnt exist
        response = await request.put("/room/" + "asdf" + "/" + user1._id + "/mark-read");
        expect(response.status).toBe(500);

        done();
    });

});
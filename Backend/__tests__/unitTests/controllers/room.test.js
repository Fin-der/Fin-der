import { app, port } from "../../../app.js"; // Link to your server file
import supertest from "supertest";
import ChatRoomModel from "../../../models/ChatRoom.js";
import ChatMessageModel from "../../../models/ChatMessage.js";
import UserModel from "../../../models/User.js";


const request = supertest(app);

describe("test chat room controller", () => {

    const exampleRoom1 = {
        _id: "DEADBEEF",
        userIds: ["2","4","6"],
        chatInitiator: "4"
    };


    const examplePost = {
        _id: "asdfasdfasdf",
        chatRoomId: "DEADBEEF",
        message: "Hello, Gamer",
        type: "text",
        postedByUser: "2",
        readByRecipients: [],
    }

    const exampleUser = {
        _id: "2",
        firstName: "Foo",
        lastName: "Bar",
        interests: ["music", "coding"]
    };
    const exampleUser2 = {
        _id: "4",
        firstName: "Far",
        lastName: "Boo",
        interests: ["music", "skating"]
    };
    const exampleUser3 = {
        _id: "6",
        firstName: "Jason",
        lastName: "Borne",
        interests: ["numbers", "masonry"]
    };

    
    it("test initiate chatroom success", async (done) => {
        ChatRoomModel.initiateChat = jest.fn(() => {return exampleRoom1;});
        
        const resp = {success:true, exampleRoom1};
        const response = await request.post("/room/initiate").send({userIds: exampleRoom1.userIds});

        expect(response.status).toBe(200);
        expect(response.body).toMatchObject(resp);
        done();
    }); 

    
    it("test initiate chatroom fail", async (done) => {
        ChatRoomModel.initiateChat = jest.fn(() => {throw error;});
        
        const resp = {success:false};
        
        const response = await request.post("/room/initiate");

        expect(response.status).toBe(500);
        expect(response.body).toMatchObject(resp);
        done();
    }); 

        
    it("test postMessage success", async (done) => {
        ChatMessageModel.createPostInChatRoom = jest.fn((roomId, messagePayload, currentLoggedUser) => {return examplePost;});
        ChatRoomModel.getUsersIdsFromRoomId = jest.fn((roomId) => {return exampleRoom1.userIds})
        UserModel.getTokensByIds = jest.fn((userIds) => {return ["asdfasfd"];})
        
        const resp = {success:true, examplePost};
        
        const response = await request.post("/room/" + exampleRoom1._id + "/" + examplePost.postedByUser + "/message");

        expect(response.status).toBe(200);
        expect(response.body).toMatchObject(resp);
        done();
    }); 


    it("test postMessage fail", async (done) => {
        ChatMessageModel.createPostInChatRoom = jest.fn((roomId, messagePayload, currentLoggedUser) => {throw error;});
        
        const resp = {success:false};
        
        const response = await request.post("/room/" + exampleRoom1._id + "/" + examplePost.postedByUser + "/message");

        expect(response.status).toBe(500);
        expect(response.body).toMatchObject(resp);
        done();
    }); 


    it("test getRecentConversation success", async (done) => {
        ChatRoomModel.getChatRoomsByUserId = jest.fn((currentLoggedUser) => {return [exampleRoom1];});
        ChatMessageModel.getRecentConversation = jest.fn((roomIds, options, currentLoggedUser) => {
            return [exampleRoom1];
        })
        
        const resp = {success:true, conversation: [exampleRoom1]};
        
        const response = await request.get("/room");

        expect(response.status).toBe(200);
        expect(response.body).toMatchObject(resp);
        done();
    }); 

    
    it("test getRecentConversation fail", async (done) => {
        ChatRoomModel.getChatRoomsByUserId = jest.fn((currentLoggedUser) => {throw error});
        
        const resp = {success:false};
        
        const response = await request.get("/room");

        expect(response.status).toBe(500);
        expect(response.body).toMatchObject(resp);
        done();
    }); 


    it("test getConversation success", async (done) => {
        ChatRoomModel.getChatRoomByUserId = jest.fn((roomId) => {return exampleRoom1;});
        UserModel.getUserByIds = jest.fn((userIds) => {[exampleUser, exampleUser2, exampleUser3]});
        ChatMessageModel.getConversationByRoomId = jest.fn((roomId, options) => {return [exampleRoom1]});
        
        const resp = {success:true, conversation: [exampleRoom1], users: [exampleUser, exampleUser2, exampleUser3]};
        
        const response = await request.get("/room/" + exampleRoom1._id + "/");

        expect(response.status).toBe(200);
        expect(response.body).toMatchObject(resp);
        done();
    }); 


    it("test getConversation fail", async (done) => {
        ChatRoomModel.getChatRoomByUserId = jest.fn((roomId) => {throw error;});
        
        const resp = {success:false};
        
        const response = await request.get("/room/" + exampleRoom1._id + "/");

        expect(response.status).toBe(500);
        expect(response.body).toMatchObject(resp);
        done();
    }); 


    it("test markConversationRead success", async (done) => {
        ChatRoomModel.getChatRoomByRoomId = jest.fn((roomId) => {return exampleRoom1;});
        ChatMessageModel.markMessageRead = jest.fn((room, currentLoggedUser) => {return {}});
        
        const resp = {success:true, result: {}};
        
        const response = await request.put("/room/" + exampleRoom1._id + "/" + exampleUser._id + "/mark-read");

        expect(response.status).toBe(200);
        expect(response.body).toMatchObject(resp);
        done();
    }); 

    it("test markConversationRead room doesn't exist", async (done) => {
        ChatRoomModel.getChatRoomByRoomId = jest.fn((roomId) => {return null;});
        
        const resp = {success:false, message: "No room exists for this id"};
        
        const response = await request.put("/room/" + exampleRoom1._id + "/" + exampleUser._id + "/mark-read");

        expect(response.status).toBe(400);
        expect(response.body).toMatchObject(resp);
        done();
    }); 

    it("test markConversationRead fail", async (done) => {
        ChatRoomModel.getChatRoomByRoomId = jest.fn((roomId) => {throw error;});
        
        const resp = {success:false};
        
        const response = await request.put("/room/" + exampleRoom1._id + "/" + exampleUser._id + "/mark-read");

        expect(response.status).toBe(500);
        expect(response.body).toMatchObject(resp);
        done();
    }); 



});

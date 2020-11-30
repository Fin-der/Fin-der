process.env.NODE_ENV = "test";
import { app } from "../../../app.js"; // Link to your server file
import supertest from "supertest";
import UserModel from "../../../models/User.js";
import {MatchVertexModel, MatchEdgeModel} from "../../../models/Match.js";
import ChatRoomModel from "../../../models/ChatRoom.js";

const request = supertest(app);

describe("test user controller", () => {

    const exampleUser = {
        _id: "eb176894d456475e9f2be1868bab8fd6",
        firstName: "Foo",
        lastName: "Bar",
        interests: ["music", "coding"]
    };
    const exampleUser2 = {
        _id: "42",
        firstName: "Far",
        lastName: "Boo",
        interests: ["music", "skating"]
    };
    const exampleUser3 = {
        _id: "42222",
        firstName: "Jason",
        lastName: "Borne",
        interests: ["numbers", "masonry"]
    };
    const users = [exampleUser, exampleUser2, exampleUser3];
    const exampleFCMToken = "e9f2be1868bab8fd";

    it("test getAllUser success", async (done) => {
        UserModel.getUsers = jest.fn(() => {return users;});
        
        const resp = {success:true, users};
        
        const response = await request.get("/users");

        expect(response.status).toBe(200);
        expect(response.body).toMatchObject(resp);
        done();
    }); 

    it("test getAllUser fail", async (done) => {
        UserModel.getUsers = jest.fn(() => {throw error;});
        
        const resp = {success:false};
        
        const response = await request.get("/users");

        expect(response.status).toBe(500);
        expect(response.body).toMatchObject(resp);
        done();
    }); 

    it("test createUser pass many users", async (done) => {
        UserModel.createUser = jest.fn(() => {return exampleUser;});
        MatchVertexModel.createMatchVertex = jest.fn(() => {return; });
        
        const resp = {success:true, user: exampleUser};
        const response = await request.post("/users");

        expect(response.status).toBe(200);
        expect(response.body).toMatchObject(resp);
        done();
    }); 

    it("test createUser internal error", async (done) => {
        UserModel.createUser = jest.fn(() => {return exampleUser;});
        MatchVertexModel.createMatchVertex = jest.fn(() => {throw error; });
        
        const resp = {success:false};
        
        const response = await request.post("/users");

        expect(response.status).toBe(500);
        expect(response.body).toMatchObject(resp);
        done();
    }); 

    it("test getUserById pass", async (done) => {
        UserModel.getUserById = jest.fn(() => {return exampleUser;});
        
        const resp = {success:true, user: exampleUser};
        
        const response = await request.get("/users/" + exampleUser._id);

        expect(response.status).toBe(200);
        expect(response.body).toMatchObject(resp);
        done();
    }); 

    it("test getUserById fail", async (done) => {
        UserModel.getUserById = jest.fn(() => {throw error;});
        
        const resp = {success:false};
        
        const response = await request.get("/users/" + exampleUser._id);

        expect(response.status).toBe(500);
        expect(response.body).toMatchObject(resp);
        done();
    }); 

    it("test updateUser pass", async (done) => {
        UserModel.updateUser = jest.fn(() => {return exampleUser;});
        
        const resp = {success:true, user: exampleUser};
        
        const response = await request.put("/users/" + exampleUser._id);

        expect(response.status).toBe(200);
        expect(response.body).toMatchObject(resp);
        done();
    }); 

    it("test updateUser fail", async (done) => {
        UserModel.updateUser = jest.fn(() => {throw error;});
        
        const resp = {success:false};
        
        const response = await request.put("/users/" + exampleUser._id);

        expect(response.status).toBe(500);
        expect(response.body).toMatchObject(resp);
        done();
    }); 

    it("test deleteUserById pass", async (done) => {
        const user = {
            deletedCount: 1
        };
        UserModel.deleteUserById = jest.fn(() => {return user;});
        MatchVertexModel.deleteMatchVertex = jest.fn(() => {return;});
        MatchEdgeModel.deleteEdgesWithId = jest.fn(() => {return;});
        ChatRoomModel.deleteUserFromChatRooms = jest.fn(() => {return;});
        
        const resp = {success:true, message: "Deleted a count of 1 user."};
        
        const response = await request.delete("/users/" + exampleUser._id);

        expect(response.status).toBe(200);
        expect(response.body).toMatchObject(resp);
        done();
    }); 

    it("test deleteUserById fail", async (done) => {
        UserModel.deleteUserById = jest.fn(() => {throw error;});
        MatchVertexModel.deleteMatchVertex = jest.fn(() => {return;});
        MatchEdgeModel.deleteEdgesWithId = jest.fn(() => {return;});
        ChatRoomModel.deleteUserFromChatRooms = jest.fn(() => {return;});
        
        const resp = {success:false};
        
        const response = await request.delete("/users/" + exampleUser._id);

        expect(response.status).toBe(500);
        expect(response.body).toMatchObject(resp);
        done();
    }); 

    it("test registerFCMToken pass", async (done) => {
        const query = {
            id: exampleUser._id,
            token: exampleFCMToken
        };
        UserModel.registerFCMToken = jest.fn(() => {return;});
        
        const resp = {
            success:true, 
            message: "Token: " + exampleFCMToken + " successfully registed with User(ID): " + exampleUser._id 
        };
        
        const response = await request.put("/users/" + exampleUser._id + "/" + exampleFCMToken).query(query);

        expect(response.status).toBe(200);
        expect(response.body).toMatchObject(resp);
        done();
    }); 

    it("test registerFCMToken fail", async (done) => {
        UserModel.registerFCMToken = jest.fn(() => {throw error;});
        
        const resp = {success:false};
        
        const response = await request.put("/users/" + exampleUser._id + "/" + exampleFCMToken);

        expect(response.status).toBe(500);
        expect(response.body).toMatchObject(resp);
        done();
    }); 
});

import { app, port } from "../../../app.js"; // Link to your server file
import http from "http";
import mongoose, {ValidationError} from "mongoose";
import RoomModel from "../../../models/ChatRoom.js";
import {MatchVertexModel, MatchEdgeModel} from "../../../models/Match.js";
import supertest from "supertest";


const request = supertest(app);

describe("test chat room controller", () => {

    const exampleRoom1 = {
        _id: "DEADBEEF",
        userIds: ["2","4","6","8"],
        chatInitiator: "4"
    };
    const exampleRoom2 = {
        _id: "qwertyuiop",
        userIds: ["1","3","5","7", "9"],
        chatInitiator: "1"
    };

    
    it("test initiate chatroom success", async (done) => {
        RoomModel.initiateChat = jest.fn(() => {return exampleRoom1;});
        
        const resp = {success:true, exampleRoom1};
        
        const response = await request.get("/room/initiate");

        expect(response.status).toBe(200);
        expect(response.body).toMatchObject(resp);
        done();
    }); 

    
    it("test getAllUser fail", async (done) => {
        RoomModel.initiateChat = jest.fn(() => {throw error;});
        
        const resp = {success:false};
        
        const response = await request.get("/room/initiate");

        expect(response.status).toBe(500);
        expect(response.body).toMatchObject(resp);
        done();
    }); 


});

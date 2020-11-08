import { app, port } from "../app.js"; // Link to your server file
import supertest from "supertest";
import http from "http";
import mongoose from "mongoose";
import UserModel from "../models/User.js";

const request = supertest(app);

describe("test", () => {

    var server;
    
    beforeEach(async () => {
        // Setup
        await mongoose.connect("mongodb://localhost:27017/test", { useNewUrlParser: true, useUnifiedTopology: true  });
        server = http.createServer(app);
        server.listen(port);
    });

    afterEach(async () => {
        // Cleanup
        await mongoose.connection.close();
        server.close();
    });
      
    // it("gets the test endpoint", async done => {
    //     const response = await request.get("/test")
    
    //     expect(response.status).toBe(200)
    //     expect(response.body.message).toBe("pass!")
    //     done()
    // })

    it("mock test", async (done) => {
        const users = [{_id: "eb176894d456475e9f2be1868bab8fd6",
                        firstName: "Foo",
                        lastName: "Bar"}];
        const mockGetUser = jest.fn(() => {return users});
        UserModel.getUsers = mockGetUser;
        
        const resp = {success:true, users};
        
        const response = await request.get("/users");

        expect(response.status).toBe(200);
        expect(response.body).toMatchObject(resp);
        done();
    })
})


/* 
it ("tests onGetAllUsers", async done => {
    const response = await request.get("/users")
  
    expect(response.status).toBe(200)
    done()
}) */
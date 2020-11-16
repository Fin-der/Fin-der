import { app, port } from "../app.js"; // Link to your server file
import supertest from "supertest";
import http from "http";
import mongoose from "mongoose";

const request = supertest(app);

describe("test", () => {

    var server;
    
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
      
    it("missing endpoint", async done => {
        const response = await request.get("/test")
    
        expect(response.status).toBe(404)
        done()
    })
});

import { app } from "../app.js"; // Link to your server file
import supertest from "supertest";

const request = supertest(app);

describe("test", () => {
      
    it("missing endpoint", async done => {
        const response = await request.get("/test")
    
        expect(response.status).toBe(404)
        done()
    })
});

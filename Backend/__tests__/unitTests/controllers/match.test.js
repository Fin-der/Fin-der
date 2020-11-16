import { app } from "../../../app.js"; // Link to your server file
import supertest from "supertest";
import {MatchEdgeModel} from "../../../models/Match.js";

const request = supertest(app);

describe("test match controller", () => {

    const errorResp = {success:false};
    const userId = "42";
    const match1 = {
        _id : "27e817beabf241aa915ff84be81ac182",
        status : "approved",
        fromStatus : "approved",
        toStatus : "approved",
        score : 1,
        from : {
            interests : [ 
                "smoking", 
                "skating"
            ],
            _id : "420",
            firstName : "SNOOOP",
            lastName : "DOOOOOG",
        },
        to : {
            interests : [ 
                "board", 
                "skating"
            ],
            _id : "69",
            firstName : "Mike",
            lastName : "Hawk",
        }
    };
    const match2 = {
        _id : "27e817beabf241aa915ff84be81ac182",
        status : "declined",
        fromStatus : "declined" ,
        toStatus : "potential",
        score : 1,
        from : {
            interests : [ 
                "smoking", 
                "skating"
            ],
            _id : "420",
            firstName : "SNOOOP",
            lastName : "DOOOOOG",
        },
        to : {
            interests : [ 
                "board", 
            ],
            _id : "30",
            firstName : "marth",
            lastName : "stewart",
        }

    };
    const matches = [match1, match2];
    it("getPotentialMatches pass", async (done) => {
        MatchEdgeModel.getPotentialMatches = jest.fn(() => {return matches});
        const resp = {success:true, matches: matches};
        const response = await request.get("/match/" + userId);

        expect(response.status).toBe(200);
        expect(response.body).toMatchObject(resp);
        done();
    }); 

    it("getPotentialMatches fail", async (done) => {
        MatchEdgeModel.getPotentialMatches = jest.fn(() => {throw error;});
        const response = await request.get("/match/" + userId);

        expect(response.status).toBe(500);
        expect(response.body).toMatchObject(errorResp);
        done();
    }); 

    it("approveMatch pass", async (done) => {
        MatchEdgeModel.changeMatchStatus = jest.fn(() => {return match1});
        const resp = {success:true, match: match1};
        const response = await request.put("/match/approve/" + match1._id + "/"+ userId);

        expect(response.status).toBe(200);
        expect(response.body).toMatchObject(resp);
        done();
    }); 

    it("approveMatch fail", async (done) => {
        MatchEdgeModel.changeMatchStatus = jest.fn(() => {throw error;});
        const response = await request.put("/match/approve/" + match1._id + "/"+userId);

        expect(response.status).toBe(500);
        expect(response.body).toMatchObject(errorResp);
        done();
    }); 

    it("declineMatch pass", async (done) => {
        MatchEdgeModel.changeMatchStatus = jest.fn(() => {return match1});
        const resp = {success:true, match: match1};
        const response = await request.put("/match/decline/" + match1._id + "/"+ userId);

        expect(response.status).toBe(200);
        expect(response.body).toMatchObject(resp);
        done();
    }); 

    it("declineMatch fail", async (done) => {
        MatchEdgeModel.changeMatchStatus = jest.fn(() => {throw error;});
        const response = await request.put("/match/decline/" + match1._id + "/"+userId);

        expect(response.status).toBe(500);
        expect(response.body).toMatchObject(errorResp);
        done();
    }); 

    it("getFriendMatches pass", async (done) => {
        MatchEdgeModel.getFriendMatches = jest.fn(() => {return matches});
        const resp = {success:true, friends: matches};
        const response = await request.get("/match/friend/" + userId);

        expect(response.status).toBe(200);
        expect(response.body).toMatchObject(resp);
        done();
    }); 

    it("getFriendMatches fail", async (done) => {
        MatchEdgeModel.getFriendMatches = jest.fn(() => {throw error;});
        const response = await request.get("/match/friend/" + userId);

        expect(response.status).toBe(500);
        expect(response.body).toMatchObject(errorResp);
        done();
    }); 
});
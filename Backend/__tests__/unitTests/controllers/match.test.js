process.env.NODE_ENV = "test";
import { app } from "../../../app.js"; // Link to your server file
import supertest from "supertest";
import UserModel from "../../../models/User.js";
import {MatchEdgeModel, MatchVertexModel} from "../../../models/Match.js";
import FirebaseMessaging from "../../../utils/FirebaseMessaging.js";

const request = supertest(app);

describe("test match controller", () => {

    const errorResp = {success:false};
    const userId = "42";
    const user = {
        _id: "eb176894d456475e9f2be1868bab8fd6",
        firstName: "Foo",
        lastName: "Bar",
        interests: ["music", "coding"]
    };
    const noInterestUser = {
        _id: "420",
        firstName: "Foo",
        lastName: "Bar",
        interests: []
    };
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
    const vertex = {
        _id: "23409183674",
        userId : user._id,
        matchesId: [user._id, user._id]
    };
    const exampleFCMToken = "e9f2be1868bab8fd";
    const matches = [match1, match2];
    
    it("getPotentialMatches pass fcm token not registered", async (done) => {
        UserModel.getUserById = jest.fn(() => {return user;});
        MatchVertexModel.getMatchVertex = jest.fn(() => {return vertex;});
        MatchEdgeModel.getPotentialMatches = jest.fn(() => {return matches;});
        MatchVertexModel.getUsersForMatching = jest.fn(() => {return [user];});
        UserModel.getTokensByIds = jest.fn(() => {return [];});
        FirebaseMessaging.sendNotifMsg = jest.fn(() => {return; });
        MatchVertexModel.addPotentialMatches = jest.fn(() => {return; });
        MatchEdgeModel.createBidirectionalEdge = jest.fn(() => {return; });

        const resp = {success:true, matches};
        const response = await request.get("/match/" + userId);

        expect(response.status).toBe(200);
        expect(response.body).toMatchObject(resp);
        done();
    }); 

    it("getPotentialMatches pass fcm token registered", async (done) => {
        UserModel.getUserById = jest.fn(() => {return user;});
        MatchVertexModel.getMatchVertex = jest.fn(() => {return vertex;});
        MatchEdgeModel.getPotentialMatches = jest.fn(() => {return matches;});
        MatchVertexModel.getUsersForMatching = jest.fn(() => {return [user];});
        UserModel.getTokensByIds = jest.fn(() => {return [exampleFCMToken];});
        FirebaseMessaging.sendNotifMsg = jest.fn(() => {return; });
        MatchVertexModel.addPotentialMatches = jest.fn(() => {return; });
        MatchEdgeModel.createBidirectionalEdge = jest.fn(() => {return; });

        const resp = {success:true, matches};
        const response = await request.get("/match/" + userId);
        
        expect(response.status).toBe(200);
        expect(response.body).toMatchObject(resp);
        done();
    }); 

    it("getPotentialMatches matches already created", async (done) => {
        UserModel.getUserById = jest.fn(() => {return user;});
        MatchVertexModel.getMatchVertex = jest.fn(() => {return vertex;});
        MatchEdgeModel.getPotentialMatches = jest.fn(() => {return matches;});
        MatchVertexModel.getUsersForMatching = jest.fn(() => {return [user, noInterestUser];});
        UserModel.getTokensByIds = jest.fn(() => {return [exampleFCMToken];});
        FirebaseMessaging.sendNotifMsg = jest.fn(() => {return; });
        MatchVertexModel.addPotentialMatches = jest.fn(() => {return; });
        MatchEdgeModel.createBidirectionalEdge = jest.fn(() => {return; });

        const resp = {success:true, matches};
        const response = await request.get("/match/" + 420);

        expect(response.status).toBe(200);
        expect(response.body).toMatchObject(resp);
        done();
    }); 

    it("getPotentialMatches no shared interest", async (done) => {
        UserModel.getUserById = jest.fn(() => {return noInterestUser;});
        MatchVertexModel.getMatchVertex = jest.fn(() => {return vertex;});
        MatchEdgeModel.getPotentialMatches = jest.fn(() => {return [];});
        MatchVertexModel.getUsersForMatching = jest.fn(() => {return [user, noInterestUser];});
        UserModel.getTokensByIds = jest.fn(() => {return [exampleFCMToken];});
        FirebaseMessaging.sendNotifMsg = jest.fn(() => {return; });
        MatchVertexModel.addPotentialMatches = jest.fn(() => {return; });
        MatchEdgeModel.createBidirectionalEdge = jest.fn(() => {return; });

        const resp = {success:true, matches: []};
        const response = await request.get("/match/" + 420);

        expect(response.status).toBe(200);
        expect(response.body).toMatchObject(resp);
        done();
    }); 

    it("getPotentialMatches fail", async (done) => {
        UserModel.getUserById = jest.fn(() => {return user;});
        MatchVertexModel.getMatchVertex = jest.fn(() => {return vertex;});
        MatchEdgeModel.getPotentialMatches = jest.fn(() => {return matches;});
        MatchVertexModel.getUsersForMatching = jest.fn(() => {return [user];});
        UserModel.getTokensByIds = jest.fn(() => {return exampleFCMToken;});
        FirebaseMessaging.sendNotifMsg = jest.fn(() => {return; });
        MatchVertexModel.addPotentialMatches = jest.fn(() => {throw error; });
        MatchEdgeModel.createBidirectionalEdge = jest.fn(() => {return; });


        const response = await request.get("/match/" + userId);

        expect(response.status).toBe(500);
        expect(response.body).toMatchObject(errorResp);
        done();
    }); 

    it("approveMatch pass fcm token not registered", async (done) => {
        MatchEdgeModel.changeMatchStatus = jest.fn(() => {return match1;});
        UserModel.getTokensByIds = jest.fn(() => {return [];});
        const resp = {success:true, match: match1};
        const response = await request.put("/match/approve/" + match1._id + "/" + userId);

        expect(response.status).toBe(200);
        expect(response.body).toMatchObject(resp);
        done();
    }); 

    it("approveMatch pass status declined", async (done) => {
        MatchEdgeModel.changeMatchStatus = jest.fn(() => {return match2;});
        UserModel.getTokensByIds = jest.fn(() => {return [];});
        const resp = {success:true, match: match2};
        const response = await request.put("/match/approve/" + match2._id + "/" + userId);

        expect(response.status).toBe(200);
        expect(response.body).toMatchObject(resp);
        done();
    }); 

    it("approveMatch pass fcm token registered", async (done) => {
        MatchEdgeModel.changeMatchStatus = jest.fn(() => {return match1;});
        UserModel.getTokensByIds = jest.fn(() => {return [exampleFCMToken];});
        const resp = {success:true, match: match1};
        const response = await request.put("/match/approve/" + match1._id + "/" + userId);

        expect(response.status).toBe(200);
        expect(response.body).toMatchObject(resp);
        done();
    }); 

    it("approveMatch fail", async (done) => {
        MatchEdgeModel.changeMatchStatus = jest.fn(() => {throw error;});
        const response = await request.put("/match/approve/" + match1._id + "/" + userId);

        expect(response.status).toBe(500);
        expect(response.body).toMatchObject(errorResp);
        done();
    }); 

    it("declineMatch pass", async (done) => {
        MatchEdgeModel.changeMatchStatus = jest.fn(() => {return match1;});
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
        MatchEdgeModel.getFriendMatches = jest.fn(() => {return matches;});
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
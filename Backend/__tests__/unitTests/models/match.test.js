import UserModel from "../../../models/User.js";
import {MatchVertexModel, MatchEdgeModel} from "../../../models/Match.js";
import mongoose from "mongoose";
import http from "http";
import { app, port } from "../../../app.js";

describe("test matchs models", () => {

    var server;
    const user = {
        _id : "30",
        preferences : {
            ageRange : {
                max : 99
            },
            gender : "Male",
            proximity : 100
        },
        interests : [ 
            "sports", 
            "sleeping", 
            "food"
        ],
        firstName : "jacky",
        lastName : "aasd",
        age : 20,
        gender : "Female",
        email : "yo@gmail.com",
        location : {
            "lat" : {
                "$numberDecimal": "0",
            },
            "lng" : {
                "$numberDecimal": "1",
            },
        },
        description : "plz",
    };
    const noPreferenceUser = {
        _id : "30",
        preferences : {
        },
        interests : [ 
            "sports", 
            "sleeping", 
            "food"
        ],
        firstName : "jacky",
        lastName : "aasd",
        age : 20,
        gender : "Female",
        email : "yo@gmail.com",
        location : {
            "lat" : {
                "$numberDecimal": "0",
            },
            "lng" : {
                "$numberDecimal": "1",
            },
        },
        description : "plz",
    };

    const vertex = {
        _id: "23409183674",
        user,
        matches: [user, user]
    };
    const simpleUser = {
        interests : [ 
            "smoking", 
            "skating"
        ],
        _id : "420",
        firstName : "SNOOOP",
        lastName : "DOOOOOG",
    };
    const match1 = {
        _id : "27e817beabf241aa915ff84be81ac182",
        status : "potential",
        fromStatus : "potential",
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
    const match3 = {
        _id : "27e817beabf2",
        status : "potential",
        fromStatus : "potential",
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
                "smoking", 
                "skating"
            ],
            _id : "420",
            firstName : "SNOOOP",
            lastName : "DOOOOOG",
        }
    };
    const matches = [match1, match2];

    const error = {
        error : "User is not a part of this match"
    };
    const options = {
        multi: true
    };

    beforeEach(async () => {
        const { collections } = mongoose.connection;

        for (const key in collections) {
            if (Object.prototype.hasOwnProperty.call(collections, key)) {
                await collections[key].deleteMany();
            }
        }
    });
    
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

    it("createMatchVertex default", async (done) => {
        var vertex = {
            user: simpleUser,
            matches: []
        };
        const potentialMatches = [];
        const vert = await MatchVertexModel.createMatchVertex(simpleUser, potentialMatches);
        vertex._id = vert._id;
        delete vert._doc.createdAt;
        delete vert._doc.updatedAt;
        delete vert.user._doc.createdAt;
        delete vert.user._doc.updatedAt;
        delete vert._doc.__v;
        expect(vert.toJSON()).toMatchObject(vertex);
        done();
    });

    it("createMatchVertex", async (done) => {
        const potentialMatches = [user];
        MatchVertexModel.create = jest.fn(() => {return vertex;});
        const vert = await MatchVertexModel.createMatchVertex(user, potentialMatches);
        expect(vert).toBe(vertex);
        done();
    });

    it("createMatchVertex already in db", async (done) => {
        const potentialMatches = [user];
        MatchVertexModel.create = jest.fn(() => {throw error;});
        try {
            await MatchVertexModel.createMatchVertex(user, potentialMatches);
            done.fail(new Error("createMatch should have thrown an error"));
        } catch (err) {
            expect(err).toMatchObject(error);
        }
        done();
    });

    it("updateVertex", async (done) => {
        MatchVertexModel.findOneAndUpdate = jest.fn(() => {return vertex;});
        MatchVertexModel.updateOne = jest.fn(() => {return;});

        const vert = await MatchVertexModel.updateMatchVertex(user._id, {});
        expect(vert).toBe(vertex);
        done();
    });

    it("deleteVertex", async (done) => {
        const deleteMsg = {
            "deletedCount": 0, 
            "n": 0, 
            "ok": 1
        };

        MatchVertexModel.findOne = jest.fn(() => {return vertex;});
        MatchVertexModel.updateOne = jest.fn(() => {return;});
        MatchVertexModel.deleteOne = jest.fn(() => {return deleteMsg;});

        const msg = await MatchVertexModel.deleteMatchVertex(user._id, {});
        expect(msg).toBe(deleteMsg);
        done();
    });

    it("getUsersForMatching random users", async (done) => {
        const options = {
            skip: 0,
            limit: 10
        };
        UserModel.getUserById = jest.fn(() => {return user;});
        MatchVertexModel.aggregate = jest.fn(() => {return {};});
        UserModel.find = jest.fn().mockImplementationOnce(() => (
            { 
                skip: jest.fn().mockImplementationOnce(() => (
                    { 
                        limit: jest.fn().mockResolvedValueOnce([user, user, user])
                    }
                ))
            }
        ));

        const users = await MatchVertexModel.getUsersForMatching(user._id, options);
        expect(users).toEqual([user, user, user]);
        done();
    });

    it("getUsersForMatching using graphlookup", async (done) => {
        const options = {
            skip: 0,
            limit: 0
        }
        const aggregate = {
            mutuals: [user, user, user]
        }
        UserModel.getUserById = jest.fn(() => {return user;});
        MatchVertexModel.aggregate = jest.fn(() => {return aggregate;});
        UserModel.find = jest.fn().mockImplementationOnce(() => (
            { 
                skip: jest.fn().mockImplementationOnce(() => (
                    { 
                        limit: jest.fn().mockResolvedValueOnce([user, user, user])
                    }
                ))
            }
        ));

        const users = await MatchVertexModel.getUsersForMatching(user._id, options);
        expect(users).toEqual(aggregate.mutuals);
        done();
    });

    it("getUsersForMatching no preferences", async (done) => {
        const options = {
            skip: 0,
            limit: 10
        };
        UserModel.getUserById = jest.fn(() => {return simpleUser;});
        MatchVertexModel.aggregate = jest.fn(() => {return {};});
        UserModel.find = jest.fn().mockImplementationOnce(() => (
            { 
                skip: jest.fn().mockImplementationOnce(() => (
                    { 
                        limit: jest.fn().mockResolvedValueOnce([user, user, user])
                    }
                ))
            }
        ));

        const users = await MatchVertexModel.getUsersForMatching(user._id, options)
        expect(users).toEqual([user, user, user]);
        done();
    });

    it("getUsersForMatching empty preferences", async (done) => {
        const options = {
            skip: 0,
            limit: 10
        };
        UserModel.getUserById = jest.fn(() => {return noPreferenceUser;});
        MatchVertexModel.aggregate = jest.fn(() => {return {};});
        UserModel.find = jest.fn().mockImplementationOnce(() => (
            { 
                skip: jest.fn().mockImplementationOnce(() => (
                    { 
                        limit: jest.fn().mockResolvedValueOnce([user, user, user])
                    }
                ))
            }
        ));

        const users = await MatchVertexModel.getUsersForMatching(user._id, options)
        expect(users).toEqual([user, user, user]);
        done();
    });


    it("addPotentialMatch", async (done) => {
        UserModel.getUserById = jest.fn(() => {return user;});
        MatchVertexModel.updateOne = jest.fn(() => {return vertex;});
    
        const vert = await MatchVertexModel.addPotentialMatches(user._id, [user]);
        expect(vert).toBe(vertex);
        done();
    });

    it("addPotentialMatch error", async (done) => {
        UserModel.getUserById = jest.fn(() => {return user;});
        MatchVertexModel.updateOne = jest.fn(() => {throw error;});
    
        try {
            await MatchVertexModel.addPotentialMatches(user._id, [user]);
            done.fail(new Error("addpotential should have thrown an error"));
        }  
        catch (err) {
            expect(err).toMatchObject(error);
        }
        done();
    });

    it("getPotentialMatch", async (done) => {
        MatchEdgeModel.find = jest.fn(() => {return matches;});
    
        const matchs = await MatchEdgeModel.getPotentialMatches(user._id);
        expect(matchs).toBe(matches);
        done();
    });

    it("getPotentialMatch error", async (done) => {
        MatchEdgeModel.find = jest.fn(() => {throw error;});
    
        try {
            await MatchEdgeModel.getPotentialMatches(user._id);
            done.fail(new Error("getPotential should have thrown an error"));
        }  
        catch (err) {
            expect(err).toMatchObject(error);
        }
        done();
    });

    it("getFriendMatch", async (done) => {
        MatchEdgeModel.find = jest.fn(() => {return matches;});
    
        const matchs = await MatchEdgeModel.getFriendMatches(user._id);
        expect(matchs).toBe(matches);
        done();
    });

    it("getFriendMatch error", async (done) => {
        MatchEdgeModel.find = jest.fn(() => {throw error;});
    
        try {
            await MatchEdgeModel.getFriendMatches(user._id);
            done.fail(new Error("getFriends should have thrown an error"));
        }  
        catch (err) {
            expect(err).toMatchObject(error);
        }
        done();
    });

    it("createBidirectionalEdge test default", async (done) => {
        UserModel.getUserById = jest.fn(() => {return simpleUser;});

        let [ edge1, edge2 ] = await MatchEdgeModel.createBidirectionalEdge("1", simpleUser._id, simpleUser._id);
        match3._id = edge1._id;
        delete edge1._doc.createdAt;
        delete edge1._doc.updatedAt;
        delete edge1.from._doc.createdAt;
        delete edge1.from._doc.updatedAt;
        delete edge1.to._doc.createdAt;
        delete edge1.to._doc.updatedAt;
        delete edge1._doc.__v;
        expect(edge1.toJSON()).toMatchObject(match3);
        match3._id = edge2._id;
        expect(edge2.toJSON()).toMatchObject(match3);
        done();
    });

    it("createBidirectionalEdge", async (done) => {
        UserModel.getUserById = jest.fn(() => {return user;});
        MatchEdgeModel.create = jest.fn(() => {return match1;});

        let [ edge1, edge2 ] = await MatchEdgeModel.createBidirectionalEdge("0", user._id, user._id);
        expect(edge1).toBe(match1);
        expect(edge2).toBe(match1);
        done();
    });

    it("createBidirectionalEdge error", async (done) => {
        UserModel.getUserById = jest.fn(() => {throw error;});
        MatchEdgeModel.create = jest.fn(() => {return; });
    
        try {
            await MatchEdgeModel.createBidirectionalEdge("0", user._id, user._id);
            done.fail(new Error("create should have thrown an error"));
        }  
        catch (err) {
            expect(err).toMatchObject(error);
        }
        done();
    });

    it("updateEdgesWithId", async (done) => {
        const updateMsg = {
            "updatedCount": 1, 
            "n": 0, 
            "ok": 1
        };
        MatchEdgeModel.updateMany = jest.fn(() => {return updateMsg;});
    
        const result = await MatchEdgeModel.updateEdgesWithId(user._id, {});
        expect(result).toBe(updateMsg);
        done();
    });

    it("deleteEdgesWithId", async (done) => {
        const deleteMsg = {
            "deletedCount": 1, 
            "n": 0, 
            "ok": 1
        };
        MatchEdgeModel.deleteMany = jest.fn(() => {return deleteMsg;});
    
        const result = await MatchEdgeModel.deleteEdgesWithId(user._id, {});
        expect(result).toBe(deleteMsg);
        done();
    });

    it("checkApprovedStatus 2 approves", async (done) => {
        var match = JSON.parse( JSON.stringify(match1));
        // we use error here to determine that it hit the if statement
        MatchEdgeModel.updateOne = jest.fn(() => {throw error;});
        match.toStatus = "approved";
        match.fromStatus = "approved";

        try {
            await MatchEdgeModel.checkApprovedStatus(match, match1, options);
            done.fail(new Error("checkApproved should have thrown an error"));
        } catch (err) {
            expect(err).toMatchObject(error);
        }
        done();
    });

    it("checkApprovedStatus 2 approves hit second update", async (done) => {
        var match = JSON.parse( JSON.stringify(match1));
        MatchEdgeModel.updateOne = jest.fn(() => {return;});
        match.toStatus = "approved";
        match.fromStatus = "approved";

        try {
            await MatchEdgeModel.checkApprovedStatus(match, match1, options);
        } catch (err) {
            done.fail(new Error("checkApproved shouldnt have thrown an error"));
        }
        done();
    });

    it("checkApprovedStatus 1 approve 1 decline", async (done) => {
        var match = JSON.parse( JSON.stringify(match1));
        // we use error here to determine that it hit the if statement
        MatchEdgeModel.updateOne = jest.fn(() => {throw error;});
        match.toStatus = "approved";
        match.fromStatus = "declined";
        
        try {
            await MatchEdgeModel.checkApprovedStatus(match, match1, options);
        } catch (err) {
            done.fail(new Error("checkApproved shouldnt have thrown an error"));
        }
        done();
        
    });

    it("checkDeclinedStatus 2 approves", async (done) => {
        var match = JSON.parse( JSON.stringify(match1));
        // we use error here to determine that it hit the if statement
        MatchEdgeModel.updateOne = jest.fn(() => {throw error});
        match.toStatus = "approved";
        match.fromStatus = "approved";

        try {
            await MatchEdgeModel.checkDeclinedStatus(match, match1, options);
            
        } catch (err) {
            done.fail(new Error("checkDeclined shouldnt have thrown an error"));
        }
        done();
        
    });

    it("checkDeclinedStatus 1 approve 1 decline", async (done) => {
        var match = JSON.parse( JSON.stringify(match1));
        // we use error here to determine that it hit the if statement
        MatchEdgeModel.updateOne = jest.fn(() => {throw error});
        match.toStatus = "approved";
        match.fromStatus = "declined";
        try {
            await MatchEdgeModel.checkDeclinedStatus(match, match1, options);
            done.fail(new Error("checkDeclined should have thrown an error"));
        } catch (err) {
            expect(err).toMatchObject(error);
        }
        done();
    });

    it("checkDeclinedStatus 2 decline", async (done) => {
        var match = JSON.parse( JSON.stringify(match1));
        MatchEdgeModel.updateOne = jest.fn(() => {return; });
        match.toStatus = "declined";
        match.fromStatus = "declined";
        try {
            await MatchEdgeModel.checkDeclinedStatus(match, match1, options);
        } catch (err) {
            done.fail(new Error("checkDeclined shouldnt have thrown an error"));
        }
        done();
    });

    it("updateToFromMatchStatus user belongs to match (to field)", async (done) => {
        MatchEdgeModel.updateOne = jest.fn(() => {return;});

        try {
            await MatchEdgeModel.updateToFromMatchStatus(match2, match1, user._id, "approve", options);
        } catch (err) {
            done.fail(new Error("update shouldnt have thrown an error"));
        }
        done();
    });

    it("updateToFromMatchStatus user belongs to match (from field)", async (done) => {
        MatchEdgeModel.updateOne = jest.fn(() => {return;});

        try {
            await MatchEdgeModel.updateToFromMatchStatus(match1, match2, "420", "approve", options);
        } catch (err) {
            done.fail(new Error("update shouldnt have thrown an error"));
        }
        done();
    });

    it("updateToFromMatchStatus user doesnt belong to match", async (done) => {
        MatchEdgeModel.updateOne = jest.fn(() => {return;});
    
        try {
            await MatchEdgeModel.updateToFromMatchStatus(match1, match2, "452342340", "approve", options);
            done.fail(new Error("update should have thrown an error"));
        }  
        catch (err) {
            expect(err).toMatchObject(error);
        }
        done();
    });

    it("determineMatchStatus returns normally", async (done) => {
        MatchEdgeModel.findOne = jest.fn().mockImplementation(() => ({ 
            lean: jest.fn().mockResolvedValueOnce(match1)  
        }));
        MatchEdgeModel.checkApprovedStatus = jest.fn(() => {return;});
        MatchEdgeModel.checkDeclinedStatus = jest.fn(() => {return;});

        try {
            await MatchEdgeModel.determineMatchStatus(match1, options);
        } catch (err) {
            done.fail(new Error("determine shouldnt have thrown an error"));
        }
        done();
    });

    it("determineMatchStatus error", async (done) => {
        MatchEdgeModel.findOne = jest.fn().mockImplementation(() => ({ 
            lean: jest.fn().mockResolvedValueOnce(match1)  
        }));
        MatchEdgeModel.checkApprovedStatus = jest.fn(() => {throw error;});
        MatchEdgeModel.checkDeclinedStatus = jest.fn(() => {return;});
    
        try {
            await MatchEdgeModel.determineMatchStatus(match1, options);
            done.fail(new Error("update should have thrown an error"));
        }  
        catch (err) {
            expect(err).toMatchObject(error);
        }
        done();
    });

    it("changeMatchStatus", async (done) => {
        MatchEdgeModel.findOne = jest.fn(() => {return match2;});
        MatchEdgeModel.findOne().lean = jest.fn(() => {return match2;});
        
        MatchEdgeModel.updateToFromMatchStatus = jest.fn(() => {return;});
        MatchEdgeModel.determineMatchStatus = jest.fn(() => {return;});

        var match = await MatchEdgeModel.changeMatchStatus(match2._id, user._id, "approve");
        expect(match).toBe(match2);
        done();
    });

    it("changeMatchStatus error", async (done) => {
        MatchEdgeModel.findOne = jest.fn(() => {throw error;});
        MatchEdgeModel.updateToFromMatchStatus = jest.fn(() => {return;});
        MatchEdgeModel.determineMatchStatus = jest.fn(() => {return;});
    
        try {
            await MatchEdgeModel.changeMatchStatus(match1._id, user._id, "approve");
            done.fail(new Error("change should have thrown an error"));
        }  
        catch (err) {
            expect(err).toMatchObject(error);
        }
        done();
    });
});
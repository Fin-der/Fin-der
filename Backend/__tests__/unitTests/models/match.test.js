process.env.NODE_ENV = "test";
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
        geoLocation : {
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
        geoLocation : {
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
        userId : user._id,
        matchesId: [user._id, user._id]
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
        fromId : "420",
        toId : "69",
            
    };
    const match2 = {
        _id : "27e817beabf241aa915ff84be81ac182",
        status : "declined",
        fromStatus : "declined" ,
        toStatus : "potential",
        score : 1,
        fromId : "420",
        toId : "30",
    };
    const match3 = {
        _id : "27e817beabf2",
        status : "potential",
        fromStatus : "potential",
        toStatus : "potential",
        score : 1,
        fromId : "420",
        toId : "420",
            
    };
    const matches = [match1, match2];

    const error = {
        error : "User is not a part of this match"
    };
    const options = {
        multi: true
    };

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
        server.listen(port);
    });

    afterAll(async () => {
        // Cleanup
        await mongoose.connection.close();
        server.close();
    });

    it("createMatchVertex default", async (done) => {
        var vertex = {
            userId: simpleUser._id,
            matchesId: []
        };
        const potentialMatches = [];
        const vert = await MatchVertexModel.createMatchVertex(simpleUser._id, potentialMatches);
        vertex._id = vert._id;
        delete vert._doc.createdAt;
        delete vert._doc.updatedAt;
        delete vert._doc.__v;
        expect(vert.toJSON()).toMatchObject(vertex);
        done();
    });

    it("createMatchVertex", async (done) => {
        const potentialMatches = [user._id];
        MatchVertexModel.create = jest.fn(() => {return vertex;});
        const vert = await MatchVertexModel.createMatchVertex(user._id, potentialMatches);
        expect(vert).toBe(vertex);
        done();
    });

    it("createMatchVertex already in db", async (done) => {
        const potentialMatches = [user._id];
        MatchVertexModel.create = jest.fn(() => {throw error;});
        try {
            await MatchVertexModel.createMatchVertex(user._id, potentialMatches);
            done.fail(new Error("createMatch should have thrown an error"));
        } catch (err) {
            expect(err).toMatchObject(error);
        }
        done();
    });

    it("getMatchVertex success", async (done) => {
        MatchVertexModel.findOne = jest.fn().mockImplementation(() => ({ 
            lean: jest.fn().mockResolvedValue(vertex)
        }));
        const vert = await MatchVertexModel.getMatchVertex(user._id);
        expect(vert).toBe(vertex);
        done();
    });

    it("getMatchVertex no user", async (done) => {
        MatchVertexModel.findOne = jest.fn().mockImplementation(() => ({ 
            lean: jest.fn().mockResolvedValue()
        }));
        try {
            await MatchVertexModel.getMatchVertex(user._id);
            done.fail(new Error("getMatch should have thrown an error"));
        } catch (err) {
            expect(err).toMatchObject({error: "No user with this id found"});
        }
        done();
    });

    it("getMatchVertex fail", async (done) => {
        MatchVertexModel.findOne = jest.fn(() => {throw error;});
        try {
            await MatchVertexModel.getMatchVertex(user._id);
            done.fail(new Error("getMatch should have thrown an error"));
        } catch (err) {
            expect(err).toMatchObject(error);
        }
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
        UserModel.find = jest.fn().mockImplementation(() => (
            { 
                skip: jest.fn().mockImplementation(() => (
                    { 
                        limit: jest.fn().mockResolvedValue([user, user, user])
                    }
                ))
            }
        ));
        user.toObject = jest.fn(() => {return user;});

        const users = await MatchVertexModel.getUsersForMatching(user._id, options);
        expect(users).toEqual([user, user, user]);
        done();
    });

    it("getUsersForMatching using graphlookup", async (done) => {
        const options = {
            skip: 0,
            limit: 25
        };
        const aggregate = {
            mutuals: [user, user, user]
        };
        UserModel.getUserById = jest.fn(() => {return user;});
        MatchVertexModel.aggregate = jest.fn(() => {return aggregate;});
        UserModel.find = jest.fn().mockImplementation(() => ({
            where: jest.fn().mockImplementation(() => ({ 
                skip: jest.fn().mockImplementation(() => ({ 
                    limit: jest.fn().mockResolvedValue([user, user, user])
                }))
            }))
        }));
        user.toObject = jest.fn(() => {return user;});

        const users = await MatchVertexModel.getUsersForMatching(user._id, options);
        expect(users).toEqual(aggregate.mutuals);
        done();
    });

    it("getUsersForMatching using graphlookup run out of mutuals", async (done) => {
        const options = {
            skip: 0,
            limit: 25
        };
        const aggregate = {
            mutuals: [user, user, user]
        };
        UserModel.getUserById = jest.fn(() => {return user;});
        MatchVertexModel.aggregate = jest.fn(() => {return aggregate;});
        UserModel.find = jest.fn().mockImplementation(() => ({
            where: jest.fn().mockImplementation(() => ({ 
                skip: jest.fn().mockImplementation(() => ({ 
                    limit: jest.fn().mockResolvedValue([])
                }))
            }))
        }));
        user.toObject = jest.fn(() => {return user;});

        const users = await MatchVertexModel.getUsersForMatching(user._id, options);
        expect(users).toEqual([]);
        done();
    });

    it("getUsersForMatching using graphlookup no mutuals", async (done) => {
        const options = {
            page: 0,
            limit: 0
        };
        const aggregate = {
            mutuals: []
        };
        UserModel.getUserById = jest.fn(() => {return user;});
        MatchVertexModel.aggregate = jest.fn(() => {return aggregate;});
        UserModel.find = jest.fn().mockImplementation(() => ({
            skip: jest.fn().mockImplementation(() => ({ 
                limit: jest.fn().mockResolvedValue([])
            }))
        }));
        user.toObject = jest.fn(() => {return user;});

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
        UserModel.find = jest.fn().mockImplementation(() => ({
            skip: jest.fn().mockImplementation(() => ({ 
                limit: jest.fn().mockResolvedValue([user, user, user])
            }))
        }));
        simpleUser.toObject = jest.fn(() => {return simpleUser;});

        const users = await MatchVertexModel.getUsersForMatching(user._id, options);
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
        UserModel.find = jest.fn().mockImplementation(() => ({
            skip: jest.fn().mockImplementation(() => ({ 
                limit: jest.fn().mockResolvedValue([user, user, user])
            }))
        }));
        noPreferenceUser.toObject = jest.fn(() => {return noPreferenceUser;});

        const users = await MatchVertexModel.getUsersForMatching(user._id, options);
        expect(users).toEqual([user, user, user]);
        done();
    });

    it("getUsersForMatching wraplong/wraplat tests", async (done) => {
        const options = {
            skip: 0,
            limit: 0
        };
        const aggregate = {
            mutuals: []
        };
        let testUser = JSON.parse(JSON.stringify(user));
        testUser.geoLocation.lat = 90;
        testUser.geoLocation.lng = 180;  
        testUser.preferences.proximity = 100000; 
        UserModel.getUserById = jest.fn(() => {return testUser;});
        MatchVertexModel.aggregate = jest.fn(() => {return aggregate;});
        UserModel.find = jest.fn().mockImplementation(() => (
            { 
                skip: jest.fn().mockImplementation(() => (
                    { 
                        limit: jest.fn().mockResolvedValue([])
                    }
                ))
            }
        ));
        testUser.toObject = jest.fn(() => {return testUser;});

        let users = await MatchVertexModel.getUsersForMatching(user._id, options);
        expect(users).toEqual(aggregate.mutuals);

        testUser.geoLocation.lat = -90;
        testUser.geoLocation.lng = -180;
        testUser.preferences.proximity = 100000; 
        UserModel.getUserById = jest.fn(() => {return testUser;});
        MatchVertexModel.aggregate = jest.fn(() => {return aggregate;});
        UserModel.find = jest.fn().mockImplementation(() => ({ 
            skip: jest.fn().mockImplementation(() => ({ 
                limit: jest.fn().mockResolvedValue([])
            }))
        }));

        users = await MatchVertexModel.getUsersForMatching(user._id, options);
        expect(users).toEqual(aggregate.mutuals);
        done();
    });


    it("addPotentialMatch", async (done) => {
        UserModel.getUserById = jest.fn(() => {return user;});
        MatchVertexModel.findOneAndUpdate = jest.fn(() => {return vertex;});
    
        const vert = await MatchVertexModel.addPotentialMatches(user._id, [user]);
        expect(vert).toBe(vertex);
        done();
    });

    it("addPotentialMatch error", async (done) => {
        UserModel.getUserById = jest.fn(() => {return user;});
        MatchVertexModel.findOneAndUpdate = jest.fn(() => {throw error;});
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
        const matchesCopy = JSON.parse(JSON.stringify(matches));
        MatchEdgeModel.find = jest.fn().mockImplementation(() => ({ 
            skip: jest.fn().mockImplementation(() => ({ 
                limit: jest.fn().mockImplementation(() => ({ 
                    sort: jest.fn().mockImplementation(() => ({ 
                        lean: jest.fn().mockResolvedValue(matchesCopy)
                    }))
                }))
            }))
        }));

        const matchs = await MatchEdgeModel.getPotentialMatches(user._id);
        expect(matchs).toBe(matchesCopy);
        done();
    });

    it("getPotentialMatch error", async (done) => {
        MatchEdgeModel.find = jest.fn().mockImplementation(() => ({ 
            skip: jest.fn().mockImplementation(() => ({ 
                limit: jest.fn().mockImplementation(() => ({ 
                    sort: jest.fn().mockImplementation(() => ({ 
                        lean: jest.fn().mockImplementation(() => {
                            throw error;
                        })
                    }))
                }))
            }))
        }));
    
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
        const matchesCopy = JSON.parse(JSON.stringify(matches));
        MatchEdgeModel.find = jest.fn().mockImplementation(() => ({ 
            skip: jest.fn().mockImplementation(() => ({ 
                limit: jest.fn().mockImplementation(() => ({ 
                    sort: jest.fn().mockImplementation(() => ({ 
                        lean: jest.fn().mockResolvedValue(matchesCopy)
                    }))
                }))
            }))
        }));
        UserModel.findOne = jest.fn(() => {return;});

        const matchs = await MatchEdgeModel.getFriendMatches(user._id);
        expect(matchs).toBe(matchesCopy);
        done();
    });

    it("getFriendMatch error", async (done) => {
        MatchEdgeModel.find = jest.fn().mockImplementation(() => ({ 
            skip: jest.fn().mockImplementation(() => ({ 
                limit: jest.fn().mockImplementation(() => ({ 
                    sort: jest.fn().mockImplementation(() => ({ 
                        lean: jest.fn().mockImplementation(() => {
                            throw error;
                        })
                    }))
                }))
            }))
        }));
        UserModel.findOne = jest.fn(() => {return;});
        
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
        delete edge1._doc.__v;
        expect(edge1.toJSON()).toMatchObject(match3);
        match3._id = edge2._id;
        expect(edge2.toJSON()).toMatchObject(match3);
        done();
    });

    it("createBidirectionalEdge", async (done) => {
        MatchEdgeModel.create = jest.fn(() => {return match1;});

        let [ edge1, edge2 ] = await MatchEdgeModel.createBidirectionalEdge("0", user._id, user._id);
        expect(edge1).toBe(match1);
        expect(edge2).toBe(match1);
        done();
    });

    it("createBidirectionalEdge error", async (done) => {
        MatchEdgeModel.create = jest.fn(() => {throw error; });
    
        try {
            await MatchEdgeModel.createBidirectionalEdge("0", user._id, user._id);
            done.fail(new Error("create should have thrown an error"));
        }  
        catch (err) {
            expect(err).toMatchObject(error);
        }
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
        MatchEdgeModel.updateOne = jest.fn(() => {throw error;});
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
        MatchEdgeModel.updateOne = jest.fn(() => {throw error;});
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
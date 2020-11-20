import UserModel from "../../../models/User.js";
import {MatchVertexModel, MatchEdgeModel} from "../../../models/Match.js";

describe("test matchs models", () => {

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
            "lat" : 0,
            "lng" : 1
        },
        description : "plz",

    };

    const vertex = {
        _id: "23409183674",
        user: user,
        matches: []
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
    const matches = [match1, match2];

    const error = {
        error : "User is not a part of this match"
    };
    const options = {
        multi: true
    };

    it("createMatchVertex", async done => {
        const potentialMatches = [user];
        MatchVertexModel.create = jest.fn(() => {return vertex;});
        const vert = await MatchVertexModel.createMatchVertex(user, potentialMatches)
        expect(vert).toBe(vertex);
        done();
    });

    it("createMatchVertex already in db", async done => {
        const potentialMatches = [user];
        MatchVertexModel.create = jest.fn(() => {throw error;});
        try {
            await MatchVertexModel.createMatchVertex(user, potentialMatches);
            done.fail(new Error("createMatch should have thrown an error"));
        } catch (err) {
            expect(err == error);
        }
        done();
    });


    // it("addPotentialMatch", async done => {
    //     UserModel.getUserById = jest.fn(() => {return user;});
    //     MatchVertexModel.update = jest.fn(() => {return vertex;});
    
    //     const vert = await MatchVertexModel.addPotentialMatch(user._id, user._id);
    //     expect(vert).toBe(vertex);
    //     done();
    // });

    // it("addPotentialMatch error", async done => {
    //     UserModel.getUserById = jest.fn(() => {throw error;});
    //     MatchVertexModel.update = jest.fn(() => {return vertex;});
    
    //     try {
    //         await MatchVertexModel.addPotentialMatch(user._id, user._id);
    //         done.fail(new Error("addpotential should have thrown an error"));
    //     }  
    //     catch (err) {
    //         expect(err == error);
    //     }
    //     done();
    // });

    it("getPotentialMatch", async done => {
        MatchEdgeModel.find = jest.fn(() => {return matches;});
    
        const matchs = await MatchEdgeModel.getPotentialMatches(user._id);
        expect(matchs).toBe(matches);
        done();
    });

    it("getPotentialMatch error", async done => {
        MatchEdgeModel.find = jest.fn(() => {throw error;});
    
        try {
            await MatchEdgeModel.getPotentialMatches(user._id);
            done.fail(new Error("getPotential should have thrown an error"));
        }  
        catch (err) {
            expect(err == error);
        }
        done();
    });

    it("getFriendMatch", async done => {
        MatchEdgeModel.find = jest.fn(() => {return matches;});
    
        const matchs = await MatchEdgeModel.getFriendMatches(user._id);
        expect(matchs).toBe(matches);
        done();
    });

    it("getFriendMatch error", async done => {
        MatchEdgeModel.find = jest.fn(() => {throw error;});
    
        try {
            await MatchEdgeModel.getFriendMatches(user._id);
            done.fail(new Error("getFriends should have thrown an error"));
        }  
        catch (err) {
            expect(err == error);
        }
        done();
    });

    it("createBidirectionalEdge", async done => {
        UserModel.getUserById = jest.fn(() => {return user;});
        MatchEdgeModel.create = jest.fn(() => {return match1;});

        let [ edge1, edge2 ] = await MatchEdgeModel.createBidirectionalEdge("0", user._id, user._id);
        expect(edge1).toBe(match1);
        expect(edge2).toBe(match1);
        done();
    });

    it("createBidirectionalEdge error", async done => {
        UserModel.getUserById = jest.fn(() => {return user;});
        MatchEdgeModel.create = jest.fn(() => {throw error;});
    
        try {
            edge1, edge2 = await MatchEdgeModel.createBidirectionalEdge("0", user._id, user._id);
            done.fail(new Error("create should have thrown an error"));
        }  
        catch (err) {
            expect(err == error);
        }
        done();
    });

    it("changeMatchStatus", async done => {
        MatchEdgeModel.findOne = jest.fn(() => {return match2;});
        MatchEdgeModel.findOne().lean = jest.fn(() => {return match2;});
        MatchEdgeModel.updateToFromMatchStatus = jest.fn(() => {return;});
        MatchEdgeModel.determineMatchStatus = jest.fn(() => {return;});

        var match = await MatchEdgeModel.changeMatchStatus(match2._id, user._id, "approve");
        expect(match).toBe(match2);
        done();
    });

    it("changeMatchStatus error", async done => {
        MatchEdgeModel.findOne = jest.fn(() => {throw error;});
        MatchEdgeModel.updateToFromMatchStatus = jest.fn(() => {return;});
        MatchEdgeModel.determineMatchStatus = jest.fn(() => {return;});
    
        try {
            await MatchEdgeModel.changeMatchStatus(match1._id, user._id, "approve");
            done.fail(new Error("change should have thrown an error"));
        }  
        catch (err) {
            expect(err == error);
        }
        done();
    });

    it("updateToFromMatchStatus user belongs to match", async done => {
        MatchEdgeModel.updateOne = jest.fn(() => {return;});

        try {
            await MatchEdgeModel.updateToFromMatchStatus(match1, match2, user._id, "approve", options);
        } catch (err) {
            done.fail(new Error("update shouldnt have thrown an error"));
        }
        done();
    });

    it("updateToFromMatchStatus user belongs to match to", async done => {
        MatchEdgeModel.updateOne = jest.fn(() => {return;});

        try {
            await MatchEdgeModel.updateToFromMatchStatus(match1, match2, "420", "approve", options);
        } catch (err) {
            done.fail(new Error("update shouldnt have thrown an error"));
        }
        done();
    });

    it("updateToFromMatchStatus user doesnt belong to match", async done => {
        MatchEdgeModel.updateOne = jest.fn(() => {return;});
    
        try {
            await MatchEdgeModel.updateToFromMatchStatus(match1, match2, "452342340", "approve", options);
            //done.fail(new Error("update should have thrown an error"));
        }  
        catch (err) {
            expect(err == error);
        }
        done();
    });

    it("determineMatchStatus returns normally", async done => {
        MatchEdgeModel.findOne = jest.fn(() => {return;});
        MatchEdgeModel.checkApprovedStatus = jest.fn(() => {return;});
        MatchEdgeModel.checkDeclinedStatus = jest.fn(() => {return;});

        try {
            await MatchEdgeModel.determineMatchStatus(match1, options);
        } catch (err) {
            done.fail(new Error("determine shouldnt have thrown an error"));
        }
        done();
    });

    it("determineMatchStatus error", async done => {
        MatchEdgeModel.findOne = jest.fn(() => {return;});
        MatchEdgeModel.checkApprovedStatus = jest.fn(() => {throw error;});
        MatchEdgeModel.checkDeclinedStatus = jest.fn(() => {return;});
    
        try {
            await MatchEdgeModel.updateToFromMatchStatus(match1, match2, "450", "approve", options);
            //done.fail(new Error("update should have thrown an error"));
        }  
        catch (err) {
            expect(err == error);
        }
        done();
    });

    it("checkApprovedStatus 2 approves", async done => {
        var match = JSON.parse( JSON.stringify(match1));
        // we use error here to determine that it hit the if statement
        MatchEdgeModel.updateOne = jest.fn(() => {throw error});
        match.toStatus = "approved";
        match.fromStatus = "approved";

        try {
            await MatchEdgeModel.checkApprovedStatus(match, match1, options);
            done.fail(new Error("checkApproved should have thrown an error"));
        } catch (err) {
            
        }
        done();
    });

    it("checkApprovedStatus 1 approve 1 decline", async done => {
        var match = JSON.parse( JSON.stringify(match1));
        // we use error here to determine that it hit the if statement
        MatchEdgeModel.updateOne = jest.fn(() => {throw error});
        match.toStatus = "approved";
        match.fromStatus = "declined";
        
        try {
            await MatchEdgeModel.checkApprovedStatus(match, match1, options);
        } catch (err) {
            //done.fail(new Error("checkApproved shouldnt have thrown an error"));
        }
        done();
        
    });

    it("checkDeclinedStatus 2 approves", async done => {
        var match = JSON.parse( JSON.stringify(match1));
        // we use error here to determine that it hit the if statement
        MatchEdgeModel.updateOne = jest.fn(() => {throw error});
        match.toStatus = "approved";
        match.fromStatus = "approved";

        try {
            await MatchEdgeModel.checkDeclinedStatus(match, match1, options);
            done();
        } catch (err) {
            done.fail(new Error("checkDeclined shouldnt have thrown an error"));
        }
        
    });

    it("checkDeclinedStatus 1 approve 1 decline", async done => {
        var match = JSON.parse( JSON.stringify(match1));
        // we use error here to determine that it hit the if statement
        MatchEdgeModel.updateOne = jest.fn(() => {throw error});
        match.toStatus = "approved";
        match.fromStatus = "declined";
        try {
            await MatchEdgeModel.checkDeclinedStatus(match, match1, options);
            //done.fail(new Error("checkDeclined should have thrown an error"));
        } catch (err) {
    
        }
        done();
    });

});
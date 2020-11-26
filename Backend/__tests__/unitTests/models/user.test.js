import { app, port } from "../../../app.js"; // Link to your server file
import http from "http";
import mongoose, {ValidationError} from "mongoose";
import {MatchVertexModel, MatchEdgeModel} from "../../../models/Match.js";
import UserModel from "../../../models/User.js";

describe("test user models", () => {

    var server;
    const exampleFCMToken = "e9f2be1868bab8fd";
    const user = {
        _id : "3",
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
        description : "plz",
        FCMToken: exampleFCMToken,
        profileURL: ""

    };
    const badUser = {
        _id : "3",
        preferences : {
            ageRange : {
                min : 100,
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
            "lat": {
                "$numberDecimal": "0",
            },
            "lng": {
                "$numberDecimal": "1",
            },
        },
        description : "plz",
        FCMToken: exampleFCMToken,
        profileURL: ""
    };
    const badUser2 = {
        _id : "3",
        preferences : {
            ageRange : {
                min : 99
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
        description : "plz",
        FCMToken: exampleFCMToken,
        profileURL: ""

    };

    const users = [user, badUser];
    const noUsers = [];

    beforeEach(async () => {
        const { collections } = mongoose.connection;

        for (const key in collections) {
            const collection = collections[key];
            await collection.deleteMany();
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

    it("test min max validation functions", async (done) => {
        const func = async () => {
            return UserModel.createUser(badUser._id, badUser.firstName, badUser.lastName, badUser.age,
            badUser.gender, badUser.email, badUser.location, badUser.preferences, badUser.interests, 
            badUser.description, badUser.FCMToken, badUser.profileURL);
        }
        await expect(func).rejects.toThrow(ValidationError);

        done();
    })

    it("test createUser creates without max", async (done) => {
        var actualUser = await UserModel.createUser(user._id, user.firstName, user.lastName, user.age,
            user.gender, user.email, user.location, user.preferences, user.interests, 
            user.description, user.FCMToken, user.profileURL);
        delete actualUser._doc.createdAt;
        delete actualUser._doc.updatedAt;
        delete actualUser._doc.__v;
        // we just check that fields are the same rather than references 
        // because actualUser is a mongoDoc
        expect(actualUser.toJSON()).toMatchObject(user);
        done();
    });

    it("test createUser creates without min", async (done) => {
        var actualUser = await UserModel.createUser(badUser2._id, badUser2.firstName, badUser2.lastName, badUser2.age,
            badUser2.gender, badUser2.email, badUser2.location, badUser2.preferences, badUser2.interests, 
            badUser2.description, badUser2.FCMToken, badUser2.profileURL);
        delete actualUser._doc.createdAt;
        delete actualUser._doc.updatedAt;
        delete actualUser._doc.__v;
        // we just check that fields are the same rather than references 
        // because actualUser is a mongoDoc
        await expect(actualUser.toJSON()).toMatchObject(badUser2);
        done();
    });

    it("getUserById finds a user", async (done) => {
        UserModel.findOne = jest.fn(() => {return user;});
        var actualUser = await UserModel.getUserById(user._id);
    
        await expect(actualUser).toBe(user);
        done();
    });

    it("getUserById cant find a user", async (done) => {
        UserModel.findOne = jest.fn(() => {return null;});
        const error = {
            "error": "No user with this id found"
        };
        try {
            await UserModel.getUserById(user._id);
        } catch (err) {
            expect(err).toMatchObject(error);
        }
        done();
    });

    it("updateUser id exist", async (done) => {
        UserModel.findOneAndUpdate = jest.fn(() => {return user;});
        const actualUser = await UserModel.updateUser(user._id, {});
    
        await expect(actualUser).toBe(user);
        done();
    });

    it("updateUser id doesnt exist", async (done) => {
        UserModel.findOneAndUpdate = jest.fn(() => {return null;});
        const error = {
            "error": "No user with this id found"
        };
        try {
            await UserModel.updateUser(user._id, {});
            done.fail(new Error("updateUser should have thrown an error"));
        } catch (err) {
            expect(err).toMatchObject(error);
        }
        done();
    });

    it("getUsers finds users", async (done) => {
        UserModel.find = jest.fn(() => {return users;});
        const allUsers = await UserModel.getUsers();
        expect(allUsers).toBe(users);
        done();
    });

    it("getUsers finds no users", async (done) => {
        UserModel.find = jest.fn(() => {return noUsers;});
        const allUsers = await UserModel.getUsers();
        expect(allUsers).toBe(noUsers);
        done();
    });

    it("getUsersByIds finds users", async (done) => {
        UserModel.find = jest.fn(() => {return noUsers;});
        const allUsers = await UserModel.getUsersByIds();
        expect(allUsers).toBe(noUsers);
        done();
    });

    it("deleteUserById", async (done) => {
        const deleteMsg = {
            "deletedCount": 0, 
            "n": 0, 
            "ok": 1
        };
        //UserModel.prototype.import = jest.fn(() => {return; });
        MatchVertexModel.deleteMatchVertex = jest.fn(() => {return; });
        MatchEdgeModel.deleteEdgesWithId = jest.fn(() => {return; });
        UserModel.deleteOne = jest.fn(() => {return deleteMsg;});
        const msg = await UserModel.deleteUserById(user._id);
        expect(msg).toBe(deleteMsg);
        done();
    });

    it("registerFCMToken finds a user", async (done) => {
        UserModel.findOne = jest.fn(() => {return user;});
        var actualUser = await UserModel.registerFCMToken(user._id, exampleFCMToken);
    
        await expect(actualUser).toBe(user);
        done();
    });

    it("registerFCMToken cant find a user", async (done) => {
        UserModel.findOne = jest.fn(() => {return null;});
        const error = {
            "error": "No user with this id found"
        };
        try {
            await UserModel.registerFCMToken(user._id, exampleFCMToken);
        } catch (err) {
            expect(err).toMatchObject(error);
        }
        done();
    });

    it("getTokensByIds retrieve a token", async (done) => {
        UserModel.find = jest.fn(() => {return [exampleFCMToken];});
        const tokens = await UserModel.getTokensByIds(user._id);
        expect(tokens).toMatchObject([exampleFCMToken]);
        done();
    });

    it("getTokensByIds no token associated", async (done) => {
        UserModel.find = jest.fn(() => {return [{}, {}];});
        const tokens = await UserModel.getTokensByIds(user._id);
        expect(tokens).toMatchObject([]);
        done();
    });

});
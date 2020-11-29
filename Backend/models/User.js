/**
 * @module models/User
 */
import { Decimal128 } from "mongodb";
import mongoose from "mongoose";
import encrypt from "mongoose-encryption";
import keys from "../config/mongoose-encrpytion.json";

/**
 * Schema representing a User
 * Note: UserModel is encrypted
 * @class UserModel
 */
const userSchema = new mongoose.Schema(
    {
        _id: {
            // By default this is unique and required
            type: String,
        },
        firstName: {
            type: String,
            required: true
        },
        lastName: {
            type: String,
            required: true
        },
        age: Number,
        gender: String,
        email: String, 
        geoLocation: {
            lng: mongoose.Types.Decimal128, 
            lat: mongoose.Types.Decimal128,
        }, 
        preferences: {
            gender: String,
            ageRange: {
                min: {
                    type: Number, 
                    min: 0,
                    validate: {
                        validator(val){
                            const currMax = this.preferences.ageRange.max;
                            return (typeof currMax !== "undefined" ? val <= currMax : true);
                        },
                        message: "The MIN range with value {VALUE} must be <= than the max range!"
                    }
                },
                max: {
                    type: Number, 
                    min: 0,
                    validate: {
                        validator(val) {
                            const currMin = this.preferences.ageRange.min;
                            return (typeof currMin !== "undefined" ? val >= currMin : true);
                        },
                        message: "The MAX range with value {VALUE} must be >= than the min range!"
                    }
                }
            },
            proximity: Number // in km
        },
        interests: [String],
        description: String,
        FCMToken: String,
        profileURL: String
    },
    {
        timestamps: true,
        collection: "users",
    }
);

userSchema.plugin(encrypt, {encryptionKey: keys.encKey, signingKey: keys.sigKey});

/**
 * Creates a User
 * 
 * @function createUser
 * @param {String} _id - the _id of the newly created user
 * @param {String} firstName - the first name of the newly created user
 * @param {String} lastName - the last name of the newly created user
 * @param {Number} age - the age of the newly created user
 * @param {String} gender - the gender of the newly created user
 * @param {String} email - the email of the newly created user
 * @param {Object} geoLocation - the geoLocation of the newly created user
 * @param {Decimal128} geoLocation.lng - the longitude of the newly created user
 * @param {Decimal128} geoLocation.lat - the latitude of the newly created user
 * @param {Object} preferences - the preferences of the newly created user
 * @param {String} preferences.gender - the gender preferences of the newly created user
 * @param {Object} preferences.ageRange - the ageRange preferences of the newly created user
 * @param {Number} preferences.ageRange.min - the min ageRange preference of the newly created user
 * @param {Number} preferences.ageRange.max - the max ageRange preference of the newly created user
 * @param {Number} preferences.proximity - the proximity preferences of the newly created user in km
 * @param {Array}  interests - A String of interest of the newly created user
 * @param {String} description - the description of the newly created user
 * @param {String} FCMToken - the FCMToken of the newly created user
 * @param {String} profileURL - the profileURL of the newly created user
 * 
 * @returns {Object} The new user object created
 */
userSchema.statics.createUser = async function (_id, firstName, lastName, 
                                age, gender, email, geoLocation, preferences,
                                interests, description, FCMToken, profileURL) {
    const user = await this.create({ _id, firstName, lastName, 
        age, gender, email, geoLocation, preferences,
        interests, description, FCMToken, profileURL});
    return user;
};

/**
 * Retrieves a user by id
 * 
 * @function getUserById
 * @param {String} id - user id
 * @throws Will throw an error if the user doesn't exist
 * @returns {Object} User object with given user id
 */
userSchema.statics.getUserById = async function (id) {
    const user = await this.findOne({ _id: id });
    if (!user) { 
        throw ({ error: "No user with this id found" }); 
    }
    return user;
};

/**
 * Updates the info of a user
 * 
 * @function updateUser
 * @param {String} id - user id
 * @param {Object} updateInfo - same info needed in CreateUser
 * @throws Will throw an error if the user doesn't exist
 * @returns {Object} User object with updated info
 */
userSchema.statics.updateUser = async function (id, updateInfo) {
    var updatedUser = await this.findOne({_id: id});
    if (!updatedUser) { 
        throw ({ error: "No user with this id found" }); 
    }
    // Note: this.update cannot be used due to using mongoose-encryption
    updatedUser = Object.assign(updatedUser, updateInfo);
    await updatedUser.save();
    return updatedUser;
};

/**
 * Retrieves a list of all users
 * 
 * @function getUsers
 * @return {Array} List of User Objects of all users
 */
userSchema.statics.getUsers = async function () {
    const users = await this.find();
    return users;
};

/**
 * Retrieves a list of users of given ids
 * 
 * @function getUsersByIds
 * @param {Array} ids - string of user ids
 * @return {Array} List of User Objects of user of given id
 */
userSchema.statics.getUsersByIds = async function (ids) {
    const users = await this.find({ _id: { $in: ids } });
    return users;
};

/**
 * Deletes a User of given id
 * 
 * @function deleteUserById
 * @param {String} id - id of user to delete
 * @return {Object} details of action performed
 */
userSchema.statics.deleteUserById = async function (id) {
    const result = await this.deleteOne({ _id: id });
    return result;
};

/**
 * Updates the FCMToken of a user of given id
 * 
 * @function registerFCMToken
 * @param {String} id - id of the user to register a FCMToken for
 * @param {String} token - FCMToken to give the user with id
 * @throws Will throw an error if the user doesn't exist
 * @returns {Object} User object with updated info
 */
userSchema.statics.registerFCMToken = async function (id, token) {
    let user = await this.findOne({ _id: id });
    if (!user) { throw ({ error: "No user with this id found" }); }
    user.FCMToken = token;
    await user.save();
    return await this.findOne({ _id: id });
};

/**
 * Retrieves the FCMTokens of users with given ids
 * 
 * @function getTokensByIds
 * @param {Array} ids - array of strings representing the ids of users
 * @returns {Array} An array of tokens 
 */
userSchema.statics.getTokensByIds = async function (ids) {
    const tokens = await this.find({ _id: { $in: ids } },"FCMToken -_id", {lean: true});
    // removes empty objects from tokens
    return tokens.filter((value) => Object.keys(value).length !== 0);
};

export default mongoose.model("User", userSchema);

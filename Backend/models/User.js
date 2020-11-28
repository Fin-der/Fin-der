import mongoose from "mongoose";

const userSchema = new mongoose.Schema(
    {
        // TODO: Fuzzy matching
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
        location: {
            lng: mongoose.Types.Decimal128, 
            lat: mongoose.Types.Decimal128,
        }, // lng, lat TODO: VALIDATE THIS VALUE
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

/**
 * @param {String} firstName
 * @param {String} lastName
 * @param {String} age
 * @param {String} gender
 * @param {String} email
 * @param {String} type
 * @returns {Object} new user object created
 */
userSchema.statics.createUser = async function (_id, firstName, lastName, 
                                age, gender, email, location, preferences,
                                interests, description, FCMToken, profileURL) {
    const user = await this.create({ _id, firstName, lastName, 
        age, gender, email, location, preferences,
        interests, description, FCMToken, profileURL});
    return user;
};

/**
 * @param {String} id, user id
 * @return {Object} User profile object
 */
userSchema.statics.getUserById = async function (id) {
    const user = await this.findOne({ _id: id });
    if (!user) { 
        throw ({ error: "No user with this id found" }); 
    }
    return user;
};

userSchema.statics.updateUser = async function (id, updateInfo) {
    const updatedUser = await this.findOneAndUpdate({_id: id}, updateInfo, {new: true});
    if (!updatedUser) { 
        throw ({ error: "No user with this id found" }); 
    }
    return updatedUser;
};

/**
 * @return {Array} List of all users
 */
userSchema.statics.getUsers = async function () {
    const users = await this.find();
    return users;
};

/**
 * @param {Array} ids, string of user ids
 * @return {Array of Objects} users list
 */
userSchema.statics.getUsersByIds = async function (ids) {
    const users = await this.find({ _id: { $in: ids } });
    return users;
};

/**
 * @param {String} id - id of user
 * @return {Object} - details of action performed
 */
userSchema.statics.deleteUserById = async function (id) {
    const result = await this.deleteOne({ _id: id });
    return result;
};

userSchema.statics.registerFCMToken = async function (id, token) {
    let user = await this.findOne({ _id: id });
    if (!user) { throw ({ error: "No user with this id found" }); }
    await this.updateOne({_id: id}, {$set: {FCMToken: token}}, {multi: true});
    return await this.findOne({ _id: id });
};

userSchema.statics.getTokensByIds = async function (ids) {
    const tokens = await this.find({ _id: { $in: ids } },"FCMToken -_id", {lean: true});
    return tokens.filter((value) => Object.keys(value).length !== 0);
};

export default mongoose.model("User", userSchema);

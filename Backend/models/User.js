import mongoose from "mongoose";

const userSchema = new mongoose.Schema(
    {
        // TODO: Fuzzy matching
        _id: {
            // By default this is unique
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
        location: (Number, Number),
        preferences: {
            gender: String,
            age: (Number, Number), // min and max
            proximity: Number
        },
        interests: [String],
        description: String,
        FCMToken: String,
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
                                interests, description) {
    const user = await this.create({ _id, firstName, lastName, 
        age, gender, email, location, preferences,
        interests, description});
    return user;
};

/**
 * @param {String} id, user id
 * @return {Object} User profile object
 */
userSchema.statics.getUserById = async function (id) {
    const user = await this.findOne({ _id: id });
    if (!user) { throw ({ error: "No user with this id found" }); }
    return user;
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
userSchema.statics.getUserByIds = async function (ids) {
    const users = await this.find({ _id: { $in: ids } });
    return users;
};

/**
 * @param {String} id - id of user
 * @return {Object} - details of action performed
 */
userSchema.statics.deleteByUserById = async function (id) {
    const result = await this.remove({ _id: id });
    return result;
};

userSchema.statics.registerFCMToken = async function (id, token) {
    let user = await this.findOne({ _id: id });
    if (!user) { throw ({ error: "No user with this id found" }); }
    user.FCMToken = token;
    user.save();
};

userSchema.statics.getTokensbyIds = async function (ids) {
    const tokens = await this.find({ _id: { $in: ids } }, "FCMToken");
    return tokens;
};

export default mongoose.model("User", userSchema);

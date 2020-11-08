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
        FCM_token: String,
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
    try {
        const user = await this.create({ _id, firstName, lastName, 
            age, gender, email, location, preferences,
            interests, description});
        return user;
    } catch (error) {
        throw error;
    }
}

/**
 * @param {String} id, user id
 * @return {Object} User profile object
 */
userSchema.statics.getUserById = async function (id) {
    try {
        const user = await this.findOne({ _id: id });
        if (!user) {throw ({ error: 'No user with this id found' })};
        return user;
    } catch (error) {
        throw error;
    }
}

/**
 * @return {Array} List of all users
 */
userSchema.statics.getUsers = async function () {
    try {
        const users = await this.find();
        return users;
    } catch (error) {
        throw error;
    }
}

/**
 * @param {Array} ids, string of user ids
 * @return {Array of Objects} users list
 */
userSchema.statics.getUserByIds = async function (ids) {
    try {
        const users = await this.find({ _id: { $in: ids } });
        return users;
    } catch (error) {
        throw error;
    }
}

/**
 * @param {String} id - id of user
 * @return {Object} - details of action performed
 */
userSchema.statics.deleteByUserById = async function (id) {
    try {
        const result = await this.remove({ _id: id });
        return result;
    } catch (error) {
        throw error;
    }
}

userSchema.statics.registerFCMToken = async function (id, token) {
    try {
        let user = await this.findOne({ _id: id })
        if (!user) throw ({ error: 'No user with this id found' });
        user.FCM_token = token
        user.save();
    } catch (error) {
        throw error;
    }
}

userSchema.statics.getTokensbyIds = async function (ids) {
    try {
        const tokens = await this.find({ _id: { $in: ids } }, 'FCM_token');
        return tokens;
    } catch (error) {
        throw error;
    }
}

export default mongoose.model("User", userSchema);

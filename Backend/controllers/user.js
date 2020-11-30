/**
 * @module controller/user
 */
import UserModel from "../models/User.js";
import { MatchEdgeModel, MatchVertexModel } from "../models/Match.js";
import ChatRoomModel from "../models/ChatRoom.js";
import {logger} from "../app.js";

export default {
    /**
     * Retrieves the info of all users
     * For Developer use 
     * 
     * @function onGetAllUsers
     * @returns {Array} An array of user objects 
     */
    onGetAllUsers: async (req, res) => {
        try {
            const users = await UserModel.getUsers();
            return res.status(200).json({ success: true, users });
        } catch (error) {
            logger.error(error);
            return res.status(500).json({ success: false, error });
        }
    },
    /**
     * Retrieves user info of user with given id
     * 
     * @function onGetUserById
     * @param {String} id - id of the user you would like the info of 
     * @returns {Object} A User object representing the user selected
     */
    onGetUserById: async (req, res) => {
        try {
            const id = req.params.id;
            const user = await UserModel.getUserById(id);
            return res.status(200).json({ success: true, user });
        } catch (error) {
            logger.error(error);
            return res.status(500).json({ success: false, error });
        }
    },
    /**
     * Creates a user with given info
     * 
     * @function onCreateUser
     * @param {Object} body A Object representing necessary info for user creation
     * @returns {Object} A User object representing the data given
     */
    onCreateUser: async (req, res) => {
        try {
            const {_id, firstName, lastName, 
                age, gender, email, geoLocation, preferences,
                interests, description, FCMToken, profileURL} = req.body;
            const user = await UserModel.createUser(_id, firstName, lastName, 
                age, gender, email, geoLocation, preferences,
                interests, description, FCMToken, profileURL);
            await MatchVertexModel.createMatchVertex(_id, []);
            
            return res.status(200).json({ success: true, user });
        } catch (error) {
            logger.error(error);
            return res.status(500).json({ success: false, error });
        }
    },
    /**
     * Updates a user with given user id
     * 
     * @function onUpdateUserById
     * @param {Object} body - object representing field you would like to update
     * @param {String} id - id of the user you would like to update
     * @returns {Object} A User object representing the updated user
     */
    onUpdateUserById: async (req, res) => {
        try {
            const id = req.params.id;
            const {firstName, lastName, 
                age, gender, email, geoLocation, preferences,
                interests, description, FCMToken, profileURL} = req.body;
            var updateInfo = {
                firstName, lastName, 
                age, gender, email, geoLocation, preferences,
                interests, description, FCMToken, profileURL
            };    
            const updatedUser = await UserModel.updateUser(id, updateInfo);
            return res.status(200).json({ success: true, user: updatedUser});
        } catch (error) {
            logger.error(error);
            return res.status(500).json({ success: false, error });
        }
    },
    /**
     * Deletes a user with given Id
     * also removes their matching info from the db
     * 
     * @function onDeleteUserById
     * @param {String} id - id of the user you would like to delete
     * @returns {String} A Message specifying the how many users were deleted
     */
    onDeleteUserById: async (req, res) => {
        try {
            const id = req.params.id;
            const user = await UserModel.deleteUserById(id);
            await MatchVertexModel.deleteMatchVertex(id);
            await MatchEdgeModel.deleteEdgesWithId(id);
            await ChatRoomModel.deleteUserFromChatRooms(id);
            return res.status(200).json({ 
                success: true, 
                message: `Deleted a count of ${user.deletedCount} user.` 
            });
        } catch (error) {
            logger.error(error);
            return res.status(500).json({ success: false, error });
        }
    },
    /**
     * Updates a users FCMToken
     * 
     * @function onRegisterFCMToken
     * @param {String} id - id of the user you would like to update the FCMToken of
     * @param {String} token - the new FCMToken for the user of given id
     * @returns {String} A Message specifying that the token was successfully updated
     */
    onRegisterFCMToken: async (req, res) => {
        try {
            const id = req.params.id;
            await UserModel.registerFCMToken(id, req.params.token);
            return res.status(200).json({
                success: true, 
                message: `Token: ${req.params.token} successfully registed with User(ID): ${id}` 
            });
        } catch(error) {
            logger.error(error);
            return res.status(500).json({ success:false, error });
        }
    }
};

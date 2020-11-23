import UserModel from "../models/User.js";
import { MatchEdgeModel, MatchVertexModel } from "../models/Match.js";

export default {
    onGetAllUsers: async (req, res) => {
        try {
            const users = await UserModel.getUsers();
            return res.status(200).json({ success: true, users });
        } catch (error) {
            return res.status(500).json({ success: false, error });
        }
    },
    onGetUserById: async (req, res) => {
        try {
            const user = await UserModel.getUserById(req.params.id);
            return res.status(200).json({ success: true, user });
        } catch (error) {
            return res.status(500).json({ success: false, error });
        }
    },
    onCreateUser: async (req, res) => {
        try {
            // TODO: validate input
            const { _id, firstName, lastName, 
                age, gender, email, location, preferences,
                interests, description, FCMToken, profileURL} = req.body;
            const user = await UserModel.createUser(_id, firstName, lastName, 
                age, gender, email, location, preferences,
                interests, description, FCMToken, profileURL);
            await MatchVertexModel.createMatchVertex(user, []);
            
            return res.status(200).json({ success: true, user });
        } catch (error) {
            return res.status(500).json({ success: false, error });
        }
    },
    onDeleteUserById: async (req, res) => {
        try {
            const user = await UserModel.deleteUserById(req.params.id);
            return res.status(200).json({ 
                success: true, 
                message: `Deleted a count of ${user.deletedCount} user.` 
            });
        } catch (error) {
            return res.status(500).json({ success: false, error });
        }
    },
    onRegisterFCMToken: async (req, res) => {
        try {
            await UserModel.registerFCMToken(req.params.id, req.params.token);
            return res.status(200).json({
                success: true, 
                message: `Token: ${req.params.token} successfully registed with User(ID): ${req.params.id}` 
            });
        } catch(error) {
            return res.status(500).json({ success:false, error });
        }
    }
};

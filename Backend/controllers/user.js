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
            const id = req.params.id;
            const user = await UserModel.getUserById(id);
            return res.status(200).json({ success: true, user });
        } catch (error) {
            return res.status(500).json({ success: false, error });
        }
    },
    onCreateUser: async (req, res) => {
        try {
            const {_id, firstName, lastName, 
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
    onUpdateUserById: async (req, res) => {
        try {
            const id = req.params.id;
            const {firstName, lastName, 
                age, gender, email, location, preferences,
                interests, description, FCMToken, profileURL} = req.body;
            var updateInfo = {
                firstName, lastName, 
                age, gender, email, location, preferences,
                interests, description, FCMToken, profileURL
            };    
            const updatedUser = await UserModel.updateUser(id, updateInfo);
            await MatchVertexModel.updateMatchVertex(id, updateInfo);
            await MatchEdgeModel.updateEdgesWithId(id, updateInfo);
            return res.status(200).json({ success: true, user: updatedUser});
        } catch (error) {
            return res.status(500).json({ success: false, error });
        }
    },
    onDeleteUserById: async (req, res) => {
        try {
            const id = req.params.id;
            const user = await UserModel.deleteUserById(id);
            await MatchVertexModel.deleteMatchVertex(id);
            await MatchEdgeModel.deleteEdgesWithId(id);
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
            const id = req.params.id;
            await UserModel.registerFCMToken(id, req.params.token);
            return res.status(200).json({
                success: true, 
                message: `Token: ${req.params.token} successfully registed with User(ID): ${id}` 
            });
        } catch(error) {
            return res.status(500).json({ success:false, error });
        }
    }
};

import UserModel from '../models/User.js';
import { MatchEdgeModel, MatchVertexModel } from '../models/Match.js';

export default {
    onGetAllUsers: async (req, res) => {
        try {
            const users = await UserModel.getUsers();
            return res.status(200).json({ success: true, users });
        } catch (error) {
            return res.status(500).json({ success: false, error: error })
        }
    },
    onGetUserById: async (req, res) => {
        try {
            const user = await UserModel.getUserById(req.params.id);
            return res.status(200).json({ success: true, user });
        } catch (error) {
            return res.status(500).json({ success: false, error })
        }
    },
    onCreateUser: async (req, res) => {
        try {
            // TODO: validate input
            const { _id, firstName, lastName, 
                age, gender, email, location, preferences,
                interests, description} = req.body;
            const curUser = await UserModel.createUser(_id, firstName, lastName, 
                age, gender, email, location, preferences,
                interests, description);
            
            var potentialMatches = []; 
            // go through each vertex and find if interests are same 
            // on success create bidirectional edge 
            const curInterests = new Set(curUser.interests); 
            const users = await UserModel.getUsers(); 
            users.forEach((user) => { 
                if (user._id != curUser._id) { 
                    var sameInterests = 0; 
                    user.interests.forEach((interest) => { 
                        if (curInterests.has(interest)) { 
                            sameInterests++; 
                        } 
                    }) 
                    if (sameInterests > 0) { 
                        potentialMatches.push(user); 
                        const edge = MatchEdgeModel.createBidirectionalEdge(sameInterests, curUser._id, user._id); 
                    } 
                } 
            });
            const vertex = await MatchVertexModel.createMatchVertex(curUser, potentialMatches);
            
            return res.status(200).json({ success: true, user: curUser });
        } catch (error) {
            return res.status(500).json({ success: false, error })
        }
    },
    onDeleteUserById: async (req, res) => {
        try {
            const user = await UserModel.deleteByUserById(req.params.id);
            return res.status(200).json({ 
                success: true, 
                message: `Deleted a count of ${user.deletedCount} user.` 
            });
        } catch (error) {
            return res.status(500).json({ success: false, error })
        }
    },
    onRegisterFCMToken: async (req, res) => {
        try {
            const user = await UserModel.registerFCMToken(req.params.id, req.params.token);
            return res.status(200).json({
                success: true, 
                message: `Token: ${user.FCMToken} successfully registed with User(ID): ${user.id}` 
            });
        } catch(error) {
            return res.status(500).json({ success:false, error })
        }
    }
}
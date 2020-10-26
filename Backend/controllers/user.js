import UserModel, { USER_TYPES } from '../models/User.js';

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
        return res.status(500).json({ success: false, error: error })
    }
  },
  onCreateUser: async (req, res) => {
    try {
        // TODO: validate input
      
        const { firstName, lastName, type } = req.body;
        const user = await UserModel.createUser(firstName, lastName, type);
        return res.status(200).json({ success: true, user });
    } catch (error) {
        return res.status(500).json({ success: false, error: error })
    }
  },
  onDeleteUserById: async (req, res) => {
    try {
        const user = await UserModel.deleteByUserById(req.params.id);
        return res.status(200).json({ 
            success: true, 
            message: `Deleted a count of ${user.deletedCount} user.` 
        });s
    } catch (error) {
        return res.status(500).json({ success: false, error: error })
    }
  },
}
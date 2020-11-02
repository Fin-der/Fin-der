import { MatchVertexModel, MatchEdgeModel } from '../models/match'

export default {
    getPotentialMatches: async (req, res) => {
        try {
            const userId = req.params.userId;
            const matches = await MatchVertexModel.getPotentialMatches(userId);
            return res.status(200).json({ success: true, matches });
        } catch (error) {
            return res.status(500).json({ success: false, error: error })
        }
    },
    approveMatch: async (req, res) => {
        try {
            const userId = req.param.userId;
            const matchId = req.param.matchId;
            const match = await MatchEdgeModel.changeMatchStatus(matchId, userId, "approved")
            return res.status(200).json({ success: true, match });
        } catch (error) {
            return res.status(500).json({ success: false, error: error })
        }
    },
    declineMatch: async (req, res) => {
        try {
            const userId = req.param.userId;
            const matchId = req.param.matchId;
            const match = await MatchEdgeModel.changeMatchStatus(matchId, userId, "declined")
            return res.status(200).json({ success: true, match });
        } catch (error) {
            return res.status(500).json({ success: false, error: error })
        }
    },
    getFriendMatches: async (req, res) => {
        try {
            const userId = req.params.userId;
            const friends = await MatchVertexModel.getFriendMatches(userId);
            return res.status(200).json({ success: true, friends });
        } catch (error) {
            return res.status(500).json({ success: false, error: error })
        }
    }
}


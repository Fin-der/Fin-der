import { MatchVertexModel, MatchEdgeModel } from "../models/Match.js";
import UserModel from "../models/User.js";
import FirebaseMessaging from "../utils/FirebaseMessaging.js";

export default {
    getPotentialMatches: async (req, res) => {
        try {
            const userId = req.params.userId;
            const curUser = await UserModel.getUserById(userId);
            const options = {
                page: parseInt(req.query.page, 10) || 0,
                limit: parseInt(req.query.limit, 10) || 25,
            };
            const matches = await MatchEdgeModel.getPotentialMatches(userId);
            let matchesId = new Set();
            matches.forEach((match) => {
                matchesId.add(match.to._id);
            });
            
            var potentialMatches = []; 
            const curInterests = new Set(curUser.interests); 
            // prioritizes mutual friends
            const users = await MatchVertexModel.getUsersForMatching(userId, options); 
            // Go through each given user and find if interests are same 
            // on success create bidirectional edge and adds the info to
            // userVertex
            // Note: this is an parallel asynchronous for each loop
            await Promise.all(users.map(async (user) => {
                if (user._id !== userId && !matchesId.has(user._id)) { 
                    var sameInterests = 0; 
                    user.interests.forEach((interest) => { 
                        if (curInterests.has(interest)) { 
                            sameInterests++; 
                        } 
                    }); 
                    if (sameInterests > 0) { 
                        // notify other user of potential match
                        const FCMToken = await UserModel.getTokensByIds([user._id]);
                        const msgBody = "You have a new potential match. Someone else on Fin-der seems to be a good match";
                        FirebaseMessaging.sendNotifMsg(FCMToken, msgBody);
                        
                        potentialMatches.push(user); 
                        await MatchVertexModel.addPotentialMatches(user._id, [curUser]);
                        await MatchEdgeModel.createBidirectionalEdge(sameInterests, userId, user._id); 
                    } 
                } 
            }));
            await MatchVertexModel.addPotentialMatches(userId, potentialMatches);
            const updatedMatches = await MatchEdgeModel.getPotentialMatches(userId);
            
            return res.status(200).json({ success: true, matches: updatedMatches });
        } catch (error) {
            return res.status(500).json({ success: false, error });
        }
    },
    approveMatch: async (req, res) => {
        try {
            const userId = req.params.userId;
            const matchId = req.params.matchId;
            const match = await MatchEdgeModel.changeMatchStatus(matchId, userId, "approved");
            if (match.status === "approved") {
                const FCMToken = await UserModel.getTokensByIds([match.to._id]);
                const msgBody = "You have a new friend! Open Fin-der to find out who";
                FirebaseMessaging.sendNotifMsg(FCMToken, msgBody);
            }
            return res.status(200).json({ success: true, match });
        } catch (error) {
            return res.status(500).json({ success: false, error });
        }
    },
    declineMatch: async (req, res) => {
        try {
            const userId = req.params.userId;
            const matchId = req.params.matchId;
            const match = await MatchEdgeModel.changeMatchStatus(matchId, userId, "declined");
            return res.status(200).json({ success: true, match });
        } catch (error) {
            return res.status(500).json({ success: false, error });
        }
    },
    getFriendMatches: async (req, res) => {
        try {
            const userId = req.params.userId;
            const friends = await MatchEdgeModel.getFriendMatches(userId);
            return res.status(200).json({ success: true, friends });
        } catch (error) {
            return res.status(500).json({ success: false, error });
        }
    }
};

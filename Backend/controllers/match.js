/**
 * @module controller/match
 */
import { MatchVertexModel, MatchEdgeModel } from "../models/Match.js";
import UserModel from "../models/User.js";
import FirebaseMessaging from "../utils/FirebaseMessaging.js";
import {logger} from "../app.js";

export default {
    /**
     * Finds potential matches for given user
     * will first find suitable users to match with using user defined preferences
     * will then match based off of similar interests
     * 
     * @function getPotentialMatches
     * @param {String} userId - the id of the user to get matches of
     * @param {Number} query.page - used for pagination
     * @param {Number} query.limit - used for pagination
     * @returns {Array} An array of matches of the user
     */
    getPotentialMatches: async (req, res) => {
        try {
            const userId = req.params.userId;
            const curUser = await UserModel.getUserById(userId);
            const options = {
                page: parseInt(req.query.page, 10) || 0,
                limit: parseInt(req.query.limit, 10) || 25,
            };
            // prevent rematching
            const vertex = await MatchVertexModel.getMatchVertex(userId);
            let matchesId = new Set(vertex.matchesId);
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
                        await FirebaseMessaging.sendNotifMsg(FCMToken[0], msgBody);
                        
                        potentialMatches.push(user); 
                        await MatchVertexModel.addPotentialMatches(user._id, [curUser._id]);
                        await MatchEdgeModel.createBidirectionalEdge(sameInterests, userId, user._id); 
                    } 
                } 
            }));
            await MatchVertexModel.addPotentialMatches(userId, potentialMatches.map((user) => {return user._id;}));
            var updatedMatches = await MatchEdgeModel.getPotentialMatches(userId, options);
            // populate field with user
            await Promise.all(updatedMatches.map(async (match) => {
                match.to = await UserModel.getUserById(match.toId);
                match.from = await UserModel.getUserById(match.fromId);
            }));
            return res.status(200).json({ success: true, matches: updatedMatches });
        } catch (error) {
            logger.error(error);
            return res.status(500).json({ success: false, error });
        }
    },
    /**
     * Approves the per side status of match
     * 
     * @function approveMatch
     * @param {String} matchId - the id of the match 
     * @param {String} userId - the id of the side of the match you would like to change
     * @returns {Object} An object representing the updated match
     */
    approveMatch: async (req, res) => {
        try {
            const userId = req.params.userId;
            const matchId = req.params.matchId;
            const match = await MatchEdgeModel.changeMatchStatus(matchId, userId, "approved");
            if (match.status === "approved") {
                const FCMToken = await UserModel.getTokensByIds([match.toId]);
                const msgBody = "You have a new friend! Open Fin-der to find out who";
                await FirebaseMessaging.sendNotifMsg(FCMToken[0], msgBody);
            }
            return res.status(200).json({ success: true, match });
        } catch (error) {
            logger.error(error);
            return res.status(500).json({ success: false, error });
        }
    },
    /**
     * Declines the per side status of match
     * 
     * @function declineMatch
     * @param {String} matchId - the id of the match 
     * @param {String} userId - the id of the side of the match you would like to change
     * @returns {Object} An object representing the updated match
     */
    declineMatch: async (req, res) => {
        try {
            const userId = req.params.userId;
            const matchId = req.params.matchId;
            const match = await MatchEdgeModel.changeMatchStatus(matchId, userId, "declined");
            return res.status(200).json({ success: true, match });
        } catch (error) {
            logger.error(error);
            return res.status(500).json({ success: false, error });
        }
    },
    /**
     * Retrieves mutual approved or "friend" matches
     * 
     * @function getFriendMatches
     * @param {String} userId - the id of the user to get friends of
     * @param {Number} query.page - used for pagination
     * @param {Number} query.limit - used for pagination
     * @returns {Array} An array of friends of the user
     */
    getFriendMatches: async (req, res) => {
        try {
            const options = {
                page: parseInt(req.query.page, 10),
                limit: parseInt(req.query.limit, 10) || 25
            };
            const userId = req.params.userId;
            const friends = await MatchEdgeModel.getFriendMatches(userId, options);
            await Promise.all(friends.map(async (friend) => {
                friend.to = await UserModel.getUserById(friend.toId);
                friend.from = await UserModel.getUserById(friend.fromId);
            }));
            return res.status(200).json({ success: true, friends });
        } catch (error) {
            logger.error(error);
            return res.status(500).json({ success: false, error });
        }
    }
};

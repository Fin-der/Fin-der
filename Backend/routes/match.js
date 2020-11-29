import express from "express";
// controllers
import match from "../controllers/match.js";

const router = express.Router();
// route: ip:port/match
router
    /**
     * Route serving getting the potential matches of given user
     * @module route/match
     * @name get/:userId
     * @param {String} userId - the id of the user to get matches of
     * @param {Number} query.page - used for pagination
     * @param {Number} query.limit - used for pagination
     * @returns {Array} An array of matches of the user
     */
    .get("/:userId", match.getPotentialMatches)
    /**
     * Routes serving approving the status of matches
     * @module route/match
     * @name put/approve/:matchId/:userId
     * @param {String} matchId - the id of the match 
     * @param {String} userId - the id of the side of the match you would like to change
     * @returns {Object} An object representing the updated match
     */
    .put("/approve/:matchId/:userId", match.approveMatch)
    /**
     * Routes serving declining the status of matches
     * @module route/match
     * @name put/decline/:matchId/:userId
     * @param {String} matchId - the id of the match 
     * @param {String} userId - the id of the side of the match you would like to change
     * @returns {Object} An object representing the updated match
     */
    .put("/decline/:matchId/:userId", match.declineMatch)
    /**
     * Routes serving getting mutually approved or "friend" matches
     * @module route/match
     * @name get/friend/:userId
     * @param {String} userId - the id of the user to get friends of
     * @returns {Array} An array of friends of the user
     */
    .get("/friend/:userId", match.getFriendMatches);

export default router;
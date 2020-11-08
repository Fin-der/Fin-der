import express from "express";
// controllers
import match from "../controllers/match.js";

const router = express.Router();
// route: ip:port/matching
router
    // params: userId
    // returns: list of "potential" matches
    .get("/:userId", match.getPotentialMatches)
    // params: userId, matchId
    // returns success/fail  
    .put("/approve/:matchId/:userId", match.approveMatch)
    .put("/decline/:matchId/:userId", match.declineMatch)
    // params: userId
    // returns: list of mutually "approved" matches
    .get("/friend/:userId", match.getFriendMatches)

export default router;
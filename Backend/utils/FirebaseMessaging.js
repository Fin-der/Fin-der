import admin from "../config/firebase-config.js";
import {logger} from "../app.js";

export default {
    /**
     * Sends a push notification message to a recipient
     * @param {string} FCMToken - The FCMToken of the recipient user
     * @param {string} msgBody - The message to send to the recipient user
     */
    sendNotifMsg: async (FCMToken, msgBody) => {
        // Checks if FCMToken is not null and has positive length
        if (FCMToken?.length) {
            var notifMessage = {
                "notification": {
                    "title": "Fin-der",
                    "body": msgBody
                },
                "token": FCMToken
            };
            admin.messaging().send(notifMessage)
                .then((response) => {
                    logger.info("Successfully sent message:", response);
                })
                .catch((error) => {
                    logger.info("Error sending message:", error);
                });
        }
    },
    /**
     * Sends a push notification message to multiple recipients
     * @param {string} FCMTokens - The FCMTokens of the recipient user
     * @param {string} msgBody - The message to send to the recipient user
     */
    sendMultiNotifMsg: async (FCMTokens, msgBody) => {
        // Checks if FCMTokens is not null and has positive length
        if (FCMTokens?.length) {
            var notifMessage = {
                "notification": {
                    "title": "Fin-der",
                    "body": msgBody
                },
                "tokens": FCMTokens
            };
            admin.messaging().sendMulticast(notifMessage)
                .then((response) => {
                    logger.info(response.successCount + " messages were sent successfully");
                });
        }
    }
};
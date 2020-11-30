import admin from "../config/firebase-config.js";

const notificationOptions = {
    priority: "high",
    timeToLive: 60 * 60 * 24 // a days worth of seconds
};

export default {
    /**
     * Starts the Firebase Cloud Messaging Service for sending push notifications
     */
    initFCM: async () => {
        admin.initializeApp();
    },
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
            admin.messaging().sendToDevice(notifMessage, notificationOptions);
        }
    },
    /**
     * Sends a push notification message to multiple recipients
     * @param {string} FCMTokens - The FCMTokens of the recipient user
     * @param {string} msgBody - The message to send to the recipient user
     */
    sendMultiNotifMsg: (FCMTokens, msgBody) => {
        // Checks if FCMTokens is not null and has positive length
        if (FCMTokens?.length) {
            var notifMessage = {
                "notification": {
                    "title": "Fin-der",
                    "body": msgBody
                },
                "tokens": FCMTokens
            };
            admin.messaging().sendMulticast(notifMessage, notificationOptions);
        }
    }
};
import admin from "../config/firebase-config.js";

const notificationOptions = {
    priority: "high",
    timeToLive: 60 * 60 * 24 // a days worth of seconds
};

export default {
    initFCM: async () => {
        admin.initializeApp();
    },
    sendNotifMsg: async (FCMToken, msgBody) => {
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
    sendMultiNotifMsg: (FCMTokens, msgBody) => {
        if (FCMToken?.length) {
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
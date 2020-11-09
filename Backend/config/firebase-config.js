import admin from "firebase-admin";

import serviceAccount from "./fin-der-firebase-adminsdk-xpplc-a1b59b7301.json";

// const notificationOptions = {
//     priority: "high",
//     timeToLive: 60 * 60 * 24 // a days worth of seconds
// };

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
    databaseURL: "https://fin-der.firebaseio.com"
});

export default admin;
var admin = require("firebase-admin");

var serviceAccount = require("./fin-der-firebase-adminsdk-xpplc-a1b59b7301.json");

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
    databaseURL: "https://fin-der.firebaseio.com"
});

module.exports.admin = admin

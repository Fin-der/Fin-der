import express from 'express'
import bodyparser from 'body-parser'
import { admin } from './config/firebase-config'

const app = express()
const mongo = require('mongodb')

app.use(bodyparser.json())

const port = 3000
const notification_options = {
    priority: "high",
    timeToLive: 60 * 60 * 24 // a days worth of seconds
  };
const MongoClient = mongo.MongoClient
const mongo_url = ''

app.post('/registerToken',(req, res)=>{
    // TODO: send to database
    MongoClient.connect(url, (err, db) => {
        if (err) throw err
        else {
            db.collection('tokens').insertOne(req.body, (err, body) => {
                if (err) throw err
                res.sendStatus(200)
            })
        }
        db.close()
    })
});

app.post('/firebase/notification', (req, res)=>{
    const registrationToken = req.body.registrationToken
    const message = req.body.message
    const options = notification_options

    //TODO: get token of other user from database
    var notif_message = {
        "notification": {
            "title": "Message From ",
            "body": message
        }
    }
    admin.messaging().sendToDevice(registrationToken, notif_message, options)
    .then( response => {
        console.log('Successfully sent message:', response);
    })
    .catch( error => {
        console.log('Error sending message:', error);
    });
})
app.listen(port, () =>{
    console.log("listening to port"+port)
})
import express from 'express'
import bodyparser from 'body-parser'
import admin from './config/firebase-config.js'
import cors from 'cors'
import socketio from 'socket.io'
import http from 'http'
// mongodb connection
import './config/mongo.js'
// socket configuration
import WebSockets from "./utils/WebSockets.js"

const app = express();
app.use(bodyparser.json());

//const mongoClient = require('mongodb').MongoClient;
import mongoClient from 'mongodb'
const mongoURL = 'mongodb://localhost:27017';

var user_db;
var chat_db;

const port = 3000
const notification_options = {
    priority: "high",
    timeToLive: 60 * 60 * 24 // a days worth of seconds
  };

mongoClient.connect(mongoURL, {useNewUrlParser: true, useUnifiedTopology: true}, (err, client) => {
    if (err) return console.log(err);

    user_db = client.db('user');
    chat_db = client.db('chat');
})

app.post('/users', (req, res) => {
    user_db.collection("users").insertOne({"username":req.body.username, "first_name":req.body.first_name, "last_name": req.body.last_name, "email": req.body.email, "password": req.body.password}, (err, result) => {
        if (req.body.username == null || req.body.first_name == null || req.body.password == null || req.body.email == null){
            res.status(400).send("error, username not passed or real name not passed");
            return;
        }
        if (err) return console.log(err);
        res.send("saved\n");
    })
})

app.put('/users', (req, res) => {
    user_db.collection("users").updateOne({"username":req.body.username}, {$set:{"first_name":req.body.first_name, "last_name": req.body.last_name, "email": req.body.email, "password": req.body.password}}, (err, result) => {
        if (req.body.username == null || req.body.first_name == null || req.body.password == null || req.body.email == null){
            res.status(400).send("error, username not passed or real name not passed");
            return;
        }
        if (err) return console.log(err);
        res.send("updated\n");
    })

})


app.get('/users', (req, res) => {
    user_db.collection("users").find().toArray((err, result) => {
        res.send(result);
    })
})

app.delete('/users', (req, res) => {
    user_db.collection("users").deleteOne({"username": req.body.username}, (err, result) =>{
        if (req.body.username == null){
            res.status(400).send("error, username not passed or real name not passed");
            return;
        }
        if(err) return console.log(err);
        res.send("removed\n");
    })
})



app.post('/registerToken',(req, res)=>{
    // TODO: send to database
    // mongoClient.connect(mongoURL, {useNewUrlParser: true, useUnifiedTopology: true}, (err, client) => {
    //     if (err) return console.log(err);

    //     dbTokens = client.db('tokens');
    //     dbTokens.collection('tokens').insertOne(req.body, (err, result) => {
    //         if (err) throw console.log(err);
    //         res.sendStatus(200);
    //     })
        
    //     dbTokens.close();
    // })
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

app.use('*', (req, res) => {
    return res.status(404).json({
        success: false,
        message: 'API endpoint doesnt exist'
    })
});

const server = http.createServer(app)

global.io = socketio.listen(server)
global.io.on('connection', WebSockets.connection)

server.listen(port)

server.on("listening", () => {
    console.log(`Listening on port:: http://localhost:${port}/`)
});
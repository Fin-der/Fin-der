import express from 'express'
import bodyparser from 'body-parser'
import { admin } from './config/firebase-config'


const express = require('express');
const app = express();
app.use(bodyparser.json());


const mongoClient = require('mongodb').MongoClient;
const mongoURL = 'mongodb://localhost:27017';

var db;
var dbTokens;

const port = 3000
const notification_options = {
    priority: "high",
    timeToLive: 60 * 60 * 24 // a days worth of seconds
  };

mongoClient.connect(mongoURL, {useNewUrlParser: true, useUnifiedTopology: true}, (err, client) => {
    if (err) return console.log(err);

    db = client.db('users');
    app.listen(3000, function() {
        console.log('Database server exists');
    })

})


app.post('/users', (req, res) => {
    db.collection("users").insertOne({"username":req.body.username, "first_name":req.body.first_name, "last_name": req.body.last_name, "email": req.body.email, "password": req.body.password}, (err, result) => {
        if (req.body.username == null || req.body.first_name == null || req.body.password == null || req.body.email == null){
            res.status(400).send("error, username not passed or real name not passed");
            return;
        }
        if (err) return console.log(err);
        res.send("saved\n");
    })
})

app.put('/users', (req, res) => {
    db.collection("users").updateOne({"username":req.body.username}, {$set:{"first_name":req.body.first_name, "last_name": req.body.last_name, "email": req.body.email, "password": req.body.password}}, (err, result) => {
        if (req.body.username == null || req.body.first_name == null || req.body.password == null || req.body.email == null){
            res.status(400).send("error, username not passed or real name not passed");
            return;
        }
        if (err) return console.log(err);
        res.send("updated\n");
    })

})


app.get('/users', (req, res) => {
    db.collection("users").find().toArray((err, result) => {
        res.send(result);
    })
})

app.delete('/users', (req, res) => {
    db.collection("users").deleteOne({"username": req.body.username}, (err, result) =>{
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
    mongoClient.connect(mongoURL, {useNewUrlParser: true, useUnifiedTopology: true}, (err, client) => {
        if (err) return console.log(err);

        dbTokens = client.db('tokens');
        dbTokens.collection('tokens').insertOne(req.body, (err, result) => {
            if (err) throw console.log(err);
            res.sendStatus(200);
        })
        
        dbTokens.close();
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
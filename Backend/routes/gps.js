const express = require('express');
const router = express.Router();
const uuid = require('uuid');
const mongoose = require('mongoose');
const ObjectId = require('mongoose').Types.ObjectId;

// Load User model
const User = require('../models/User');
const GPS = require('../models/GPS');

/**
 * Enpunkt zum Upload der Bewegungsdaten entwender eine einzelne Position oder ein Array an Positionen
 * token: API Zugriffstoken
 * latitude: Array oder Double der Latitude
 * longitude: Array oder Double der Longitude
 * date: Array oder String des Datums
 */
router.post('/setLocation', (req,res,next) =>{
    const { token } = req.query;
    const { latitude,longitude,date } = req.body;


    // Safe new Location Data
    User.findOne({
        token: token
    }).then(user =>{
        console.log("Upload Trace User: "+user.name)
        console.log("Infected: "+user.infected)
        var traceid = uuid.v4();
        if (Array.isArray(latitude)){
            for (let i= 0;i<latitude.length;i++){
                var data = {
                    traceid: traceid,
                    user: user._id,
                    date: new Date(date[i]*1),
                    location: {type: "Point", coordinates: [ longitude[i], latitude[i] ]},
                    infected: user.infected
                };
                //console.log(data);

                var newGPS = new GPS(data);
                newGPS.save(function(err, res) {
                    if(err) {
                        console.log(err);
                    }
                    else {
                        analyseTrace(traceid, user)
                    }
                });
            }
        }
        else {
            var data= {
                traceid: traceid,
                    user: user._id,
                date: new Date(date*1),
                location: {type: "Point", coordinates: [ longitude, latitude ]},
                infected: user.infected
            };
            //console.log(data);
            var newGPS = new GPS(data);
            newGPS.save(function(err, res) {
                if(err) {
                    console.log(err);
                }
                else {
                    analyseTrace(traceid, user)
                }
            });
        }
    });
    res.status(200).json("OK");
});

/**
 * Hilfsfunktion welche Kontaktpunkte zwischen dem übergebenen Trace des Users und anderen Usern berechnet
 *
 * @param traceid ID des Traces der verarbeitet werden soll
 * @param user User Objekt des Users dessen Trace verarbeitet wird
 */
function analyseTrace(traceid, user) {
    // Analyse Location Data for contacts
    var MS_PER_MINUTE = 60000;
    var diffInMinutes = 1;
    // {traceid: "061e0050-afa7-43c7-b44d-ece159a26efb"}
    console.log("######START#######")
    console.log(user.name)
    //console.log(traceid)
    GPS.find({traceid: traceid}).then(function (gpstrace) {
        //console.log(gps);
        //time limits
        console.log('Found Points in Trace:'+gpstrace.length);
        gpstrace.forEach(function(gps) {
            var pre = new Date(gps.date - diffInMinutes * MS_PER_MINUTE);
            var post = new Date(gps.date + diffInMinutes * MS_PER_MINUTE);
            //console.log("pre: "+pre)
            //console.log("post: "+post)

            // Find Contacts and save both directions
            GPS.find({
                user: {$ne: new ObjectId(gps.user)},
                location: {$near: {$geometry: gps.location, $minDistance: 0, $maxDistance: 5}},
                date: {$gte: pre, $lt: post},
            }).then(function (contacts) {
                if (contacts.length > 0) {
                    console.log("Contact[s] found: ");
                    //Update contacts lists
                    contacts.forEach(function(c) {
                        //console.log(c)
                        GPS.updateOne({_id: c._id}, {$push: {contacts: gps._id}}, function (err, res) {
                            //console.log(err)
                            console.log(res)
                        })
                        GPS.updateOne({_id: gps._id}, {$push: {contacts: c._id}}, function (err, res) {
                            //console.log(err)
                            console.log(res)
                        })
                    });
                } else {
                    console.log("no contact")
                }
            });
        });
    })
}

/**
 * Endpunkt zum Abrufen des Bewegungsprofiles eines Nutzers
 * token: API Zugriffstoken
 *
 * Gibt zwei Arrays zurück, die Latitude und Longitude
 */
router.post('/getPoints', (req,res,next) =>{
    const {token,gi} = req.query;
    User.findOne({
        token: token
    }).then(user =>{

        try {
            GPS.find({user: user._id}).then(function (docs) {
                let latitude= [];
                let longitude = [];
                console.log('Found Points:'+docs.length);
                docs.forEach(function(doc) {
                    latitude.push(doc.location.coordinates[1]);
                    longitude.push(doc.location.coordinates[0]);
                });
                res.status(200).json({latitude: latitude, longitude: longitude});
            });
        } catch (err) {
            throw err;
        }
    })
});

/**
 * Gibt die Punkte zurück bei denen eine Risikoperson getroffen wurde
 * token: API Zugriffstoken
 *
 * Gibt zwei Arrays zurück, die Latitude und Longitude
 */
router.post('/getContactPoints', (req,res,next) =>{
    const {token,gi} = req.query;
    User.findOne({
        token: token
    }).then(user =>{

        try {
            GPS.find({user: user._id, contacts: {$not: {$size: 0}}}).then(function (docs) {
                let latitude= [];
                let longitude = [];
                console.log('Found Points:'+docs.length);
                docs.forEach(function(doc) {
                    latitude.push(doc.location.coordinates[1]);
                    longitude.push(doc.location.coordinates[0]);
                });
                res.status(200).json({latitude: latitude, longitude: longitude});
            });
        } catch (err) {
            throw err;
        }
    })
});

/**
 * Adminendpunkt der die Kontaktpunkte von einer Person und Datum berechnet
 * token: API Zugriffstoken
 * userid: ID der Person für die die Kontaktkette berechnet werden soll
 * date: Datum ab dessen Zeitpunkt die Kontakte berechnet werden sollen
 */
router.post('/getInfectionTrace', (req,res,next) => {
    const {token, gi} = req.query;
    const {userid,date} = req.body;

    pdate = Date(date)
    console.log(userid)
    console.log(pdate)

    User.findOne({
        token: token
    }).then(user => {
        GPS.find({user: new ObjectId(userid), date: {$gte: Date.parse(date)}}).then(docs =>{
            let latitude= [];
            let longitude = [];
            console.log('Found Points:'+docs.length);
            docs.forEach(function(doc) {
                latitude.push(doc.location.coordinates[1]);
                longitude.push(doc.location.coordinates[0]);
            });
            res.status(200).json({latitude: latitude, longitude: longitude});
        });
    });
});

/**
 * Adminendpunkt der die Kontaktkette ausgehend von einer infizierten Person zum Zeitpunkt seiner Infektion berechnet
 * token: API Zugriffstoken
 * username: Name der Person für die die Kontaktkette berechnet werden soll
 */
router.post('/getUserTrace', (req,res,next) => {
    const {token, gi} = req.query;
    const {username} = req.body;

    console.log(username)

    User.findOne({
        token: token
    }).then(user => {
        User.findOne({name: username}).then(x => {
            if(!x.infected) {
                res.status(200).json(false);
            } else {
                try {
                    findNextStep(x._id, x.infectedSince).then(function (result) {
                        console.log(result)
                        res.status(200).json(result);
                    })
                } catch (err) {
                    throw err;
                }
            }
        });
    });
});

/**
 * Adminendpunkt der die Kontaktkette ausgehend von einer Person und Datum berechnet
 * token: API Zugriffstoken
 * userid: ID der Person für die die Kontaktkette berechnet werden soll
 * date: Datum ab dessen Zeitpunkt die Kontakte berechnet werden sollen
 */
router.post('/getContactTrace', (req,res,next) =>{
    const {token,gi} = req.query;
    const { userid,date } = req.body;
    User.findOne({
        _id: userid
    }).then(user =>{
        console.log(user.name);
        console.log(Date.parse(date))
        try {
            findNextStep(user._id, Date.parse(date)).then(function(result) {
                res.status(200).json(result);
            })
        } catch (err) {
            throw err;
        }
    })
});

/**
 * Rekursive Hilfsfunktion für die Kontaktketten Berechnung
 * @param user UserID
 * @param date Datum
 * @returns Kontaktkette
 */
function findNextStep(user, date) {
    return new Promise(function(resolve, reject)
    {
        console.log("SEARCH:")
        console.log(user)
        console.log(date)
        GPS.find({user: new ObjectId(user), contacts: {$not: {$size: 0}}, date: {$gt: date}}).populate('contacts').populate('user').then(function (docs) {
            //console.log(docs)
            //no other contacts
            if (docs.length == 0) {
                resolveUserdata({
                    user: user,
                    name: null,
                    risk: 0.00,
                    date: (new Date(date)).toLocaleString("de-DE"),
                    isodate: date,
                    contacts: []
                }).then(function(res)
                {
                    resolve(res);
                });
            } else {
                recursiveDocList(docs,null, user, date).then(function (res){
                   resolve(res)
                });

            }
        });
    });
}

/**
 * Rekursive Hilfsfunktion für die Kontaktketten Berechnung
 * @param list Liste der noch zu prüfenden Elemente
 * @param result Liste der Ergebnisse
 * @param user UserID
 * @param date Datum
 * @returns Kontaktkette
 */
function recursiveDocList(list, result, user, date) {
    return new Promise(function (resolve, reject) {
        if(list.length == 0) {
            resolve(result)
        }
        var doc = list.shift()
        if(result == null) {
            result = {
                user: user,
                name: doc.user.name,
                risk: doc.user.risk,
                date: (new Date(date)).toLocaleString("de-DE"),
                isodate: date,
                location: null,
                contacts: []
            }
        }
        recursiveList(doc.contacts, []).then(function(res) {
            res.forEach(function (e){
                e.location = [ doc.location.coordinates[0], doc.location.coordinates[1] ]
            })
            result.contacts = result.contacts.concat(res)
            recursiveDocList(list, result, user, date).then(function (res2) {
                resolve(res2)
            })
        })
    });
}

/**
 * Hilfsfunktion der Benutzerinformationen zu einem Benutzer abruft
 * @param data Abzugragender Benutzer
 * @returns Benutzerinformationen
 */
function resolveUserdata(data) {
    return new Promise(function( resolve, reject){
        User.findOne({_id: data.user}).then(userobject => {
            if(userobject == null) {
                console.log('<'+data.user+'>')
                reject("Benutzer nicht gefunden")
            }
            data.name = userobject.name
            data.risk = userobject.risk
            resolve(data)
        });
    });
}

/**
 * Rekursive Hilfsfunktion der eine Liste von Elementen durcharbeitet
 * @param list List der zu bearbeitenden Elemente
 * @param result Zwischenergebnis
 * @returns Endergebnis
 */
function recursiveList(list, result) {
    return new Promise(function (resolve, reject) {
        if(list.length == 0) {
            //console.log("FIN:")
            //console.log(result)
            resolve(result)
        }
        var c = list.shift()
        findNextStep(c.user, c.date).then(function(res) {
            result.push(res)
            //console.log(result)
            recursiveList(list, result).then(function (res2) {
                resolve(res2)
            })
        });
    });
}

/**
 * Adminendpunkt: Berechnet den Risikofaktor für alle Benutzer
 */
router.post('/recalculateRisk', (req,res,next) =>{
    const {token,gi} = req.query;
    const { userid,date } = req.body;
    User.updateMany({},{risk: 0}).then(function(){
        User.find({
            infected: true
        }).then(users =>{
            console.log(users);
            users.forEach(user =>{
                calculateNextStep(user._id, user.infectedSince, 0);
            })
        })
    });
    res.status(200).json({status: "started"});
});

/**
 * Hilfsfunktion zur Berechnung des Risikofaktors
 * @param user User für den berechnet wird
 * @param date Datum des Kontakts
 * @param step Kontakt Tiefe
 */
function calculateNextStep(user, date, step) {
    console.log(user)
    console.log(date)
    console.log(step)
    if(step > 4) {
        return;
    }
        User.findOne({_id: user}).then(currentuser=>{
            if(currentuser == null) {
                console.log('Benutzer nicht gefunden: <'+user+'>')
                return
            }
            var newrisk = Math.min(currentuser.risk + 1/(step*10), 1);
            Object.assign(currentuser, {risk: newrisk}).save().then(function() {
                GPS.find({
                    user: new ObjectId(user),
                    contacts: {$not: {$size: 0}},
                    date: {$gt: date},
                }).populate('contacts').then(function (points) {
                    // other contacts
                    if (points.length > 0) {
                        points.forEach(point=>{
                            point.contacts.forEach(contact=>{
                                calculateNextStep(contact.user, contact.date, step + 1);
                            })
                        });
                    }
                });
            })
        })

}


module.exports = router;
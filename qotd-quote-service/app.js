require('dotenv').config();
if (process.env.ENABLE_INSTANA == "true") {
    require('@instana/collector')();
}
const express = require('express');
const fs = require('fs');
const requestIp = require('request-ip');
const mysql = require('mysql');
const serviceManager = require('@quote-of-the-day/service-control');

const utils = serviceManager.utils;

app = express();
app.set('port', 3001)
app.enable('trust proxy');
app.use(express.json());
app.use(requestIp.mw());

//================================================================================================
// service management
const anomalyConfig = { 
    "url": process.env.ANOMALY_GENERATOR_URL,
    "logLevel": process.env.LOG_LEVEL
}

if( typeof process.env.POLLING_FREQUENCY != 'undefined' && Number.isInteger(process.env.POLLING_FREQUENCY) ) {
    anomalyConfig.pollingFrequency = parseInt(process.env.POLLING_FREQUENCY);
} else {
    anomalyConfig.pollingFrequency = 5000;
}

serviceManager.config(anomalyConfig, app);

//================================================================================================
// Endpoints 

var dbport = 3306;
if( typeof process.env.DB_PORT != 'undefined' ){
    var p = parseInt(process.env.DB_PORT);
    if( Number.isInteger(p) && p > 0 && p < 65336 ) dbport = p;
}

const pool = mysql.createPool({
    host: process.env.DB_HOST,
    user: process.env.DB_USER,
    port: dbport,
    password: process.env.DB_PASS,
    database: 'qotd',

    insecureAuth: true
});

var getConnection = function (res, token, callback) {
    utils.log('Getting connection from pool', token);
    pool.getConnection(function (err, connection) {
        if (err) {
            utils.log('Error getting connection: ' + err, token);
            res.status(500).json({ "error": err });
            return;
        }
        callback(connection);
    });
};

function handler(req,res){
    var requestToken = req.query.requestToken; 
    var quote_id = req.params.id;

    utils.log("Quote request for " + quote_id, requestToken, "INFO");

    getConnection(res, requestToken, function (connection) {
        var sql = "SELECT quotes.quote_id, quotes.quote, authors.author_id, authors.author, genres.genre FROM quotes, authors, genres WHERE quote_id=? and quotes.author_id=authors.author_id and quotes.genre_id=genres.genre_id ;";
        connection.query(sql, [quote_id], function (err, rows, fields) {
            if (err) {
                res.status(500).json({ "error": err });
                utils.log(error, requestToken,"INFO");
            } else {
                if (rows.length > 0) {
                    res.json({ "quote": rows[0].quote, "id": rows[0].quote_id, "author_id": rows[0].author_id, "author": rows[0].author, "genre": rows[0].genre });
                    utils.log("Daily quote sql returned rows: " + rows.length, requestToken,"INFO");
                } else {
                    res.status(500).json({ "error": "quote id " + quote_id + " doesn't exist." });
                    utils.log("quote id " + quote_id + " doesn't exist.", requestToken,"INFO");
                }
                connection.release();
            }
        });
    });    

}


serviceManager.endpointGet('/quotes/:id', "Processing quote request", handler);


//================================================================================================
// Common app endpoints

app.get('/',
    function (req, res) {
        serviceManager.log('/',null,"DEBUG");
        res.redirect('/version');
    }
);

app.get('/version',
    function (req, res) {
        serviceManager.log('/version',null,"DEBUG");
        var ip = req.clientIp;
        res.send(`${appName} v${appVersion}, build: ${buildInfo}.  Your IP: ${ip}`);
    }
);

//================================================================================================
// Main app

const package = require('./package.json');
const appName = package.name;
const appVersion = package.version;
const buildInfo = fs.readFileSync('build.txt');

appService = app.listen(app.get('port'), '0.0.0.0', function () {
    console.log(`Starting ${appName} v${appVersion}, ${buildInfo} on port ${app.get('port')}`);
});

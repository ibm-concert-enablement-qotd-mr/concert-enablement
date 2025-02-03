require('dotenv').config();
if (process.env.ENABLE_INSTANA == "true") {
    require('@instana/collector')();
}
const express = require('express');
const fs = require('fs');
const http = require('http');
const requestIp = require('request-ip');
const mysql = require('mysql');
const parseUrl = require('parse-url');
const serviceManager = require('@quote-of-the-day/service-control');

const utils = serviceManager.utils;

//================================================================================================
// Express setup

app = express();
app.set('port', 3002);
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

const pool = mysql.createPool({
    host: process.env.DB_HOST,
    user: process.env.DB_USER,
    password: process.env.DB_PASS,
    database: 'qotd',
    insecureAuth: true
});

var getConnection = function (res, callback) {
    pool.getConnection(function (err, connection) {
        if (err) {
            utils.log('Error getting connection: ' + err);
            res.status(500).json({ "error": err });
            return;
        }
        callback(connection);
    });
};

function authorHandler(req,res){
    var id = req.params.id;
    var requestToken = req.query.requestToken;
    utils.log("Author bio request for author_id: " + id , requestToken, "INFO");

    getConnection(res, function (connection) {
        var sql = "SELECT authors.author_id, authors.author FROM authors WHERE authors.author_id=?;";
        connection.query(sql, [id], function (error, rows, fields) {
            if (error) {
                utils.log("Author request sql, id: " + id + " Error: " + error, requestToken);
                res.status(500).json(error);
            } else {
                if (rows.length > 0) {
                    try {
                        var data = fs.readFileSync('./summaries/' + id + '.txt');
                        var bio = data.toString().trim();
                        bio = utils.replaceAll('\n', '</p><p>', bio);
                        bio = "<p>" + bio + "</p>";
                        var obj = {
                            "author": rows[0].author,
                            "author_id": rows[0].author_id,
                            "bio": bio
                        }

                        utils.log("Author summary supplied for author_id: " + id, requestToken);
                        res.json(obj);
                    } catch (err) {
                        utils.log("Author summary " + id + " file read failure. Err: " + err, requestToken);
                        res.status(500).send("Author summary " + id + " not found");
                    }
                } else {
                    var erObj = { "error": "author id '" + author_id + "' doesn't exist." };
                    utils.log("Error: Author with id [" + id + "] not found. 404 returned.", requestToken);
                    res.status(404).json(erObj);
                }
            }
            connection.release();
        });
    });
}

serviceManager.endpointGet('/authors/:id', "Processing author request", authorHandler);

function imageHandler(req,res){
    var id = req.params.id;
    var requestToken = req.query.requestToken;
    utils.log("Incoming request for author image with author_id: " + id, requestToken,"INFO");
    
    var parsedUrl = parseUrl(process.env.IMAGE_SVC);
    var hostname = parsedUrl.resource;
    var port = 80;
    if (parsedUrl.port != null) port = parsedUrl.port;  

    utils.log("Requesting image from image service for author_id: " + id + ".", requestToken,"INFO");

    const options = {
        "headers": { "Accept": "image/jpeg" },
        "method": 'GET',
        "hostname": hostname,
        "port": port,
        "timeout": 5000,
        "path": '/images/' + id + '?requestToken=' + requestToken
    }

    http.request(options, function (imgRes) {
        var rawData = [];

        imgRes.on('data', (chunk) => {
            rawData.push(chunk);
        });

        imgRes.on('end', () => {
            utils.log(`Image service status code response: ${imgRes.statusCode}`,requestToken,"INFO");

            if (imgRes.statusCode == 200) {
                var buffer = Buffer.concat(rawData);
                res.writeHead(200, {
                    'Content-Type': 'image/jpeg',
                    'Content-Length': buffer.length
                });
                utils.log("Author image for id " + id + " provided.",requestToken,"INFO");
                res.end(Buffer.from(buffer, 'binary'));
            } else {
                // assume any error will report a 404
                utils.log("Author image service returning  " + imgRes.statusCode + " for image id " + id + ".", requestToken,"INFO");
                res.status(404).send("Author image " + id + " not found");
            }
        });

    })
    .on('timeout', () => {
        utils.log(`Timeout http request ${options.method} http://${options.hostname}:${options.port}${options.path}`, options.requestToken,"INFO");
        res.status(500).send("Author image request timed out");
    })
    .on('error', error => {
        utils.log("Call to get image for author: " + id + " failed. " + error,requestToken,"INFO");
        res.status(404).send("Author image not found");
    })
    .end();

}

serviceManager.endpointGet('/images/:id', "Processing image request", imageHandler);



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


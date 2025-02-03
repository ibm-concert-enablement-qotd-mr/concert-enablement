require('dotenv').config();
if (process.env.ENABLE_INSTANA == "true") {
    require('@instana/collector')();
}
const express = require('express');
const fs = require('fs');
const requestIp = require('request-ip');
const parseUrl = require('parse-url');
const moment = require('moment');
const serviceManager = require('@quote-of-the-day/service-control');

const utils = serviceManager.utils;

//================================================================================================
// Express setup

app = express();
app.set('port', 3006);
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


const SUPPLY_CHAIN_URL = process.env.SUPPLY_CHAIN_URL;
const SUPPLY_CHAIN_SIMULATE = (process.env.SUPPLY_CHAIN_SIMULATE == 'true');

function handler(req,res){
    var payload = JSON.stringify(req.body);
    var requestToken = req.query.requestToken;
    utils.log("Incoming request for quote engraving. " + payload, requestToken, "INFO");
    
    if( SUPPLY_CHAIN_SIMULATE == true ) {
        // simulate it
        var timestr = new moment().toISOString();
        var supplyRes = {
            "code": "CP4I"+utils.intBetween(1000,9999),
            "msg": "Sucess simulating order request",
            "time": timestr
        }
        utils.log("Engraving service simulating supply request and response.  Simulated order response code: "+supplyRes.code, requestToken, "INFO");
        res.status(200).json(supplyRes);

    } else {
        var parsedUrl = parseUrl(SUPPLY_CHAIN_URL);
        var hostname = parsedUrl.resource;
        var port = 80;
        if (parsedUrl.port != null) port = parsedUrl.port;    
        var path = parsedUrl.pathname;
        const options = {
            "headers": { "Content-Type": "application/json", "Accept": "application/json" },
            "Content-Length": payload.length,
            "method": 'POST',
            "hostname": hostname,
            "port": port,
            "path": path,
            "secureProtocol": "TLSv1_2_method"
        }

        var httpRequester = utils.httpRequest;
        if( SUPPLY_CHAIN_URL.startsWith("https://")) {
            httpRequester = utils.httpsRequest;
        }

        httpRequester(options,payload)
        .then( (confirmationCode) => {
            res.status(201).json(confirmationCode);
            utils.log("Engraving requested sucessfully submitted.", requestToken, "INFO");
        })
        .catch( (error) => {
            utils.log("Problem submitting engraving request. Error: " + error.message, requestToken, "INFO");
            var errObj = {
                "error": 'Rejecting supply chain call.  Status: ' + error.statusCode,
                "resource": SUPPLY_CHAIN_URL
            }
            res.status(error.code).json(errObj);
        });
    }
                
};

serviceManager.endpointPost('/order', "Processing order request", handler);



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


require('dotenv').config();
if (process.env.ENABLE_INSTANA == "true") {
    require('@instana/collector')();
}
const express = require('express');
const fs = require('fs');
const requestIp = require('request-ip');
const serviceManager = require('@quote-of-the-day/service-control');

//================================================================================================
// Express setup

app = express();
app.set('port', 3004);
app.enable('trust proxy');
app.use(express.json());
app.use(requestIp.mw());

//================================================================================================
// service management
const anomalyConfig = { 
    "url": process.env.ANOMALY_GENERATOR_URL,
    "logLevel": process.env.LOG_LEVEL
}

if( typeof process.env.POLLING_FREQUENCY != 'undefined' && Number.parseInt(process.env.POLLING_FREQUENCY) ) {
    anomalyConfig.pollingFrequency = parseInt(process.env.POLLING_FREQUENCY);
} else {
    anomalyConfig.pollingFrequency = 5000;
}



serviceManager.config(anomalyConfig, app);

//================================================================================================
// Endpoints 

function handler(req,res){
    var id = req.params.id;
    var requestToken = req.query.requestToken;

    serviceManager.log("Setting up dart board." , requestToken); 
    var rating = serviceManager.utils.genNormalInt(7, 10, 0, 10);
    var payload = {
        "quote_id": id,
        "rating": rating
    }
    serviceManager.log("The monkey's dart hit the " + rating, requestToken); 
    res.status(200).json(payload);
}

serviceManager.endpointGet('/ratings/:id', "Processing rating request", handler);

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

require('dotenv').config();
if (process.env.ENABLE_INSTANA == "true") {
    require('@instana/collector')();
}
const express = require('express');
const fs = require('fs');
const requestIp = require('request-ip');
const parseUrl = require("parse-url");
const { PDFDocument, StandardFonts, rgb } = require('pdf-lib');

const serviceManager = require('@quote-of-the-day/service-control');

const utils = serviceManager.utils;

app = express();
app.set('port', 3005)
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


async function buildPdf(quote, author, requestToken) {
    utils.log('Building PDF for ' + author + ' quote.', requestToken, "INFO");
    const fontSize = 14

    const pdfDoc = await PDFDocument.create();

    // Embed the Times Roman font
    const timesRomanFontItalic = await pdfDoc.embedFont(StandardFonts.TimesRomanItalic);
    const timesRomanFont = await pdfDoc.embedFont(StandardFonts.TimesRoman);

    // Add a blank page to the document
    const page = pdfDoc.addPage();

    // Get the width and height of the page
    // const width, height } = page.getSize();

    var size = page.getSize();
    var pageWidth = size.width;
    var pageHeight = size.height;
    const textBoxWidth = pageWidth - 100;

    var words = quote.split(' ');
    var lines = [];
    var lineWidth = 0;
    var line = '';
    for (const word of words) {
        var lineWidth = timesRomanFontItalic.widthOfTextAtSize(line + ' ' + word, fontSize);
        if (lineWidth > textBoxWidth) {
            // start a new line
            lines.push(line);
            line = word;
        } else {
            line += ' ' + word;
        }
    }
    lines.push(line);

    var lineHeight = pageHeight - pageHeight / 4;
    for (const line of lines) {

        page.drawText(line, {
            x: 50,
            y: lineHeight,
            size: fontSize,
            font: timesRomanFontItalic,
            color: rgb(0.2, 0.2, 0.2),
        });

        lineHeight -= timesRomanFontItalic.heightAtSize(fontSize) * 1.25;
    }

    line = '- ' + author;
    lineHeight -= timesRomanFontItalic.heightAtSize(fontSize) * 1.5;
    page.drawText(line, {
        x: 50,
        y: lineHeight,
        size: 10,
        font: timesRomanFont,
        color: rgb(0.2, 0.2, 0.2),
    });

    // Serialize the PDFDocument to bytes (a Uint8Array)
    const pdfBytes = await pdfDoc.save()
    return pdfBytes;

}


function getQuote(id,requestToken){
    return new Promise((resolve, reject) => { 
        var parsedUrl = parseUrl(process.env.QUOTE_SVC);
        var hostname = parsedUrl.resource;
        var port = 80;
        if (parsedUrl.port != null) port = parsedUrl.port;  

        utils.log("Requesting quote " + id + ".", requestToken, "INFO");

        const options = {
            "headers": { "Accept": "application/json" },
            "method": 'GET',
            "hostname": hostname,
            "port": port,
            "path": '/quotes/' + id + '?requestToken=' + requestToken
        }

        utils.httpRequest(options)
        .then( (quote) => {
            utils.log("Quote service request for quote details sucessfull ["+id+"].", requestToken, "INFO");
            resolve(quote);
        })
        .catch( (error) => {
            utils.log("Problem submitting quote service quote request. Error: " + error.message, requestToken, "INFO");
            var errObj = {
                "error": 'Rejecting quote service.  Status: ' + error.statusCode,
                "resource": `http://${options.hostname}:${options.port}${options.path}`
            }
            reject(errObj);
        });
    });
}

function handler(req,res){
    var requestToken = req.query.requestToken; 
    var quote_id = req.params.id;

    getQuote(quote_id, requestToken)
    .then( async (data) => {
        utils.log("Obtained rating for quote: "+quote_id+".", requestToken, "INFO");

        try{
            var pdf = await buildPdf(data.quote, data.author, requestToken)
            res.writeHead(200, {
                'Content-Type': 'application/pdf',
                'Content-Length': pdf.length
            });
            res.end(Buffer.from(pdf, 'binary'));

        } catch( error ) {

        }
    } );
}


serviceManager.endpointGet('/pdf/:id', "Processing PDF request", handler);


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








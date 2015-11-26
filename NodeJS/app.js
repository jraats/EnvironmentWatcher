var tokenValidator = require("./controller/tokenValidator.js");
var express 	= require('express');
var path		= require('path');
var bodyParser 	= require('body-parser');
var fs 			= require('fs');
var moment		= require('moment');
var mysql		= require('mysql');
var app 		= express();


// Read all app settings 
var settings = require('./config.json');
app.set('secretkey', settings.secretkey);
app.set('username', settings.username);
app.set('password', settings.password);
app.set('webPort', settings.webPort);
var connection = mysql.createConnection(
{
	host : settings.dbServer,
	user : settings.dbUsername,
	password : settings.dbPassword,
	database : settings.dbScheme
});
app.set('dbConnection', connection);


// everything to JSON
app.use(bodyParser.urlencoded({ extended:true }));
app.use(bodyParser.json());

// Middelware, voor alle /api/* request
app.all('/api/*', function(req, res, next) 
{
	// Set respons header (geen idee of dit compleet is)
	res.header("Access-Control-Allow-Origin","*");
	res.header("Access-Control-Allow-Methods","GET,PUT,POST,DELETE,OPTIONS");
	res.header("Access-Control-Allow-Headers","X-Requested-With,Content-type,Accept,X-Access-Token,X-Key");

	// Set response contenttype
	res.contentType('application/json');

	next();
});

/*
 * /api/login is the only route without auth, /api/login generates API key
 */
app.post('/api/login', require('./routes/auth.js').login);

// All other /api/* API request routing via JWT validation
app.all('/api/*', tokenValidator);

// Process all routes using express router
app.use('/', require('./routes/index.js'));

// Static files (if we want a website or something)
app.use(express.static(__dirname + '/www'));

// Start server
var port = process.env.PORT || app.get('webPort');
var server = app.listen( port , function() {
	console.log('Listening server on port ' + server.address().port );
});

var jwt = require('jwt-simple');
var util		= require('util');
var mysql		= require('mysql');

var tokenValidator = {
		createMysqlConnection: function(app){
		var settings = require('../../config.json');
		var connection = mysql.createConnection(
		{
			host : settings.dbServer,
			user : settings.dbUsername,
			password : settings.dbPassword,
			database : settings.dbScheme
		});
		app.set('dbConnection', connection);
	},
	checkToken: function (req, res, next) {

		var token = (req.header('X-Access-Token')) || '';
		
		if (token) {
			try {
				var somet;
				var decoded = jwt.decode(token, req.app.get('secretkey'));

				// Check if token is from known user
				// for now ..
				var userName = decoded.iss
				console.log(userName);
				
				var done = tokenValidator.checkAccount(req, userName, function(result){
					console.log(result);
					if(0 == result) {
						somet = 0;
						req.app.set("userid", decoded.iss);
						console.log("Userid: " + req.app.get('userid'));
						return next();
					}
					else {
						somet = 1;
						res.status(401);
						res.json({
							"status": 401, "message": "unknown userid, bye"
						});
					}
					
				});
			}
			catch (err) {
				console.log("Authorization failed: " + err);
			}
		}else{
			res.status(401);
			res.json({
			"status": 401, "message": "niet geautoriseerd, bye"
		});
		}

		
	},
	checkAccount: function(req, username, done) {

		var db = req.app.get('dbConnection');
         db.query("SELECT * FROM `user` WHERE `username` = '" + username + "'",function(err,rows){	
			if (err){
				done(1);
			}
			else if (!rows.length) {
                done(2);
            } 
             
			else{
				// all is well, return successful user
				done(0);	
			}
					
		
		});
    }
}

module.exports = tokenValidator;

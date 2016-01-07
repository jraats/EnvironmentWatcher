var jwt = require('jwt-simple');
var util		= require('util');
var mysql		= require('mysql');

var auth = {
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
	login: function(req, res) {

		var username = req.body.username || '';
		var password = req.body.password || '';
		console.log(username);
		
		var done = auth.checkAccount(req, username, password, function(result){

			console.log(result);

			// Check for valid user/passwd combo
			if (result == 0) {
				var now = new Date();
				var expires = now.setHours(now.getDay() + 10);
				var token = jwt.encode({
					iss: username,
					exp: expires
				}, req.app.get('secretkey'));

				res.status = 200;
				res.json({
					token: token,
					expires: expires,
					user: username
				});
			}
			else {
				res.status(401);
				res.json({
					"status": 401,
					"message": "Unknown USER, bye"
				});
			}
		});
		
		
    },
	checkAccount: function(req, username, password, done) {

		var db = req.app.get('dbConnection');
		console.log(username);
         db.query("SELECT * FROM `user` WHERE `username` = '" + username + "'",function(err,rows){	
			if (err){
				done(1);
			}
			else if (!rows.length) {
                done(2);
            } 
			
			// if the user is found but the password is wrong
            else if (!( rows[0].password == password))
			{
				done(3);
			}
             
			else{
				// all is well, return successful user
				done(0);	
			}
					
		
		});
    }
}

module.exports = auth;

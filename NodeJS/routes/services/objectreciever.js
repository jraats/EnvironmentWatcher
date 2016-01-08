var mysql		= require('mysql');
var util		= require('util');
var objectreciever = { 
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
	getObject: function(req, res, query, objectResult){
		
			console.log(query);
		if(objectResult === undefined){
			objectResult = "results";
		}
		var results = {};
		var db = req.app.get('dbConnection');
		db.query(query, function (err, rows, fields) {
			if(err){
				objectreciever.createMysqlConnection(req.app);
				res.json({status: "ERROR"});
			}else{
				results[objectResult] = rows;
				res.json(results);
			}
		});
	},
	createObject: function(req, res, object, tableName){
		var count = 0;
		var lengthObject = Object.keys(object).length;
		
		var keys = "";
		var values = "";

		Object.keys(object).forEach(function(key) {
			count++;

		  	keys += key;
		  	values += "'"+object[key]+"'";
		  	
		  	if(lengthObject > count){
				keys+= ",";
				values+= ",";		  		
		  	}
		});
		var query = "INSERT INTO "+tableName+" ("+keys+") VALUES ("+values+");";
		
		console.log(query);
		var results = {};
		var db = req.app.get('dbConnection');
		db.query(query, function (err, rows, fields) {
			if(err){ 
				objectreciever.createMysqlConnection(req.app);
				res.json({status: "ERROR"});
			}else{
				res.json({status: "OK"});	
			}
		});
	},
	updateObject: function(req, res, object, tableName){
		if(object.columnItem){
			var count = 0;
			var lengthObject = Object.keys(object).length;
			
			var sets = "";

			Object.keys(object).forEach(function(key) {
				count++;
				if(key !== "columnName" && key !== "columnItem"){
					if(object[key] !== null)
						sets += key+"='"+object[key]+"'";
					else
						sets += key+"="+object[key];
				  	
				  	if(lengthObject > count){
						sets+= ",";
				  	}
			  	}
			});
			var query = "UPDATE "+tableName+" SET "+sets+" WHERE "+object.columnName+"='"+object.columnItem+"';";
			console.log(query);
			var results = {};
			var db = req.app.get('dbConnection');
			db.query(query, function (err, rows, fields) {
				if(err){ 
					objectreciever.createMysqlConnection(req.app);
					res.json({status: "ERROR"}); 
				}else{
					res.json({status: "OK"});	
				}
			});
		}else{
			res.json({status: "ERROR, id is not set"}); 	
		}
		
	},
	DeleteObject: function(req, res, object, tableName){
		
		var query = "DELETE FROM "+tableName+" WHERE "+object.columnName+" = '"+object.columnItem+"';";
		var results = {};
		var db = req.app.get('dbConnection');
		db.query(query, function (err, rows, fields) {
			if(err){ 
				console.log(err);
				objectreciever.createMysqlConnection(req.app);
				res.json({status: "ERROR"}); 
			}else{
				res.json({status: "OK"});	
			}
		});
	},
	CheckForInjection: function(value)
	{
		if (/\s/.test(value)){
			return true;
		}
		return false;
	}
	
}
module.exports = objectreciever;
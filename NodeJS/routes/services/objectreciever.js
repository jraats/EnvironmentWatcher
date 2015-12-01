var util = require('util');

objectreciever = { 
	getObject: function(req, res, query, objectResult){
		if(objectResult === undefined){
			objectResult = "results";
		}
		var results = {};
		var db = req.app.get('dbConnection');
		db.query(query, function (err, rows, fields) {
			if(err) throw err;
			results[objectResult] = rows;
			res.json(results);
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
		var results = {};
		var db = req.app.get('dbConnection');
		db.query(query, function (err, rows, fields) {
			if(err){ 
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
				  	sets += key+"='"+object[key]+"'";
				  	if(lengthObject > count){
						sets+= ",";
				  	}
			  	}
			});
			var query = "UPDATE "+tableName+" SET "+sets+" WHERE "+object.columnName+"="+object.columnItem+";";
			var results = {};
			var db = req.app.get('dbConnection');
			db.query(query, function (err, rows, fields) {
				if(err){ 
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
		
		var query = "DELETE "+object.item+" FROM "+tableName+" WHERE "+object.columnName+"="+object.columnItem+";";
		
		var results = {};
		var db = req.app.get('dbConnection');
		db.query(query, function (err, rows, fields) {
			if(err){ 
				res.json({status: "ERROR"}); 
			}else{
				res.json({status: "OK"});	
			}
		});
	}
	
}
module.exports = objectreciever;
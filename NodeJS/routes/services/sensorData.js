var objectreciever = require ('./objectreciever.js');
var sensorData = {
	getSensorData: function(req, res) {
		var query = "SELECT * FROM sensorData;";
		objectreciever.getObject(req, res, query, "sensorData");
	},
	getDataByProductId: function(req, res){
		if (!isNaN(req.params.productId)) {
			var query = "SELECT * FROM sensorData WHERE productId = "+req.params.productId+";";
			objectreciever.getObject(req, res, query, "sensorData");
		}else{
		res.json({error: "ID must be a number"});
		}
	},
	getLatestDataByProductId: function(req, res){
		if (!isNaN(req.params.productId)) {
			var query = "SELECT * FROM sensorData WHERE productId = "+req.params.productId+" ORDER BY time DESC LIMIT 1;";
			objectreciever.getObject(req, res, query, "sensorData");
		}else{
		res.json({error: "ID must be a number"});
		}
	},
	getDataByTime: function(req, res){
		if (!isNaN(req.params.productId)) {
			var query = "SELECT * FROM sensorData WHERE time BETWEEN '"+req.params.d1+" 00:00:00' AND '"+req.params.d2+" 23:59:59' AND productId = "+req.params.productId+";";
			objectreciever.getObject(req, res, query, "sensorData");
		}else{
		res.json({error: "must have a timestamp"});
		}
	},
	createSensorData: function(req, res){
		var object = { 
			time: req.body.time || '',
			productId: req.body.productId || '',
			light: req.body.light || '',
			temperature: req.body.temperature || ''
		};
		objectreciever.createObject(req, res, object, "sensorData");
	}
};

module.exports = sensorData;
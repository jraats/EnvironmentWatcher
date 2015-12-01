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
		res.json({error: "id moet numeriek zijn."});
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
var objectreciever = require ('./objectreciever.js');

var preferences = {
	getPreferences: function(req, res) {
		var query = "SELECT * FROM preferences;";
		objectreciever.getObject(req, res, query, "preferences");
	},
	getPreferencesByUsername: function(req, res){
		if (!isNaN(req.params.productId)) {
			var query = "SELECT * FROM preferences WHERE userUsername = "+req.params.productId+";";
			objectreciever.getObject(req, res, query, "preferences");
		}else{
		res.json({error: "id moet numeriek zijn."});
		}
	},
	createPreferences: function(req, res){
		var object = { 
			userUsername: req.body.userUsername || '',
			lightTreshold: req.body.lightTreshold || '',
			temperatureTreshold: req.body.temperatureTreshold || ''
		};
		objectreciever.createObject(req, res, object, "preferences");
	},
	updatePreferenceByUsername: function(req, res){
		var object = {
			columnName: "userUsername",
			columnItem: req.params.userUsername,
		};
		
		//set only values we want to update..
		if(req.body.lightTreshold) 			object['lightTreshold'] = req.body.lightTreshold;
		if(req.body.temperatureTreshold) 	object['temperatureTreshold'] = req.body.temperatureTreshold;

		objectreciever.updateObject(req, res, object, "preferences");
	}
};

module.exports = preferences;
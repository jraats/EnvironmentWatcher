var objectreciever = require ('./objectreciever.js');

var product = {
	getproducts: function(req, res) {
		var query = "SELECT * FROM product;";
		objectreciever.getObject(req, res, query, "products");
	},
	getProductById: function(req, res){
		if (!isNaN(req.params.id)) {
			var query = "SELECT * FROM product WHERE id = "+req.params.id+";";
			objectreciever.getObject(req, res, query, "product");
		}else{
		res.json({error: "id moet numeriek zijn."});
		}
	},
	createProduct: function(req, res){
		var object = { 
			roomName: req.body.roomName || '',
			location: req.body.location || ''
		};
		objectreciever.createObject(req, res, object, "product");
	},
	updateProductById: function(req, res){
		var object = {
			columnName: "id",
			columnItem: req.params.id,
		};
		
		//set only values we want to update..
		if(req.body.roomName) 		object['roomName'] = req.body.roomName;
		if(req.body.location) 		object['location'] = req.body.location;

		objectreciever.updateObject(req, res, object, "product");
	}
};

module.exports = product;
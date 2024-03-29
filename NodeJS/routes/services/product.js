var objectreciever = require ('./objectreciever.js');
var product = {
	getproducts: function(req, res) {
		var query = "SELECT * FROM product;";
		objectreciever.getObject(req, res, query, "product");
	},
	getProductById: function(req, res){
		if (!isNaN(req.params.id)) {
			var query = "SELECT * FROM product WHERE id = "+req.params.id+";";
			objectreciever.getObject(req, res, query, "product");
		}else{
		res.json({error: "id must be an number"});
		}
	},
	createProduct: function(req, res){
		console.log("Hallo");
		var object = { 
			roomName: req.body.roomName || '',
			location: req.body.location || ''
		};
		objectreciever.createObject(req, res, object, "product");
	},
	updateProductById: function(req, res){
		if (!isNaN(req.params.id))
		{
			var object = {
			columnName: "id",
			columnItem: req.params.id,
			};
			
			//set only values we want to update..
			if(req.body.roomName) 		object['roomName'] = req.body.roomName;
			if(req.body.location) 		object['location'] = req.body.location;

			objectreciever.updateObject(req, res, object, "product");
		}else{
			res.json({error: "id must be an number"});
		}
	},
	deleteProductById: function(req, res){
		if(!isNaN(req.params.id)){
			var object = {
			columnName: req.body.columnName,
			//ID of the product
			columnItem: req.params.username
			}
			
			objectreciever.DeleteObject(req, res, object, "product");
		}
		else{
			res.json({error: "ID must be a number"});
		}
	}
};

module.exports = product;
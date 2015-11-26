var express = require('express');
var router = express.Router();

var user = 	require ('./services/user.js');

router.get('/api/ping', function(req, res){
		res.json({status: "OK"});
});

//Get data from database
//router.get('/api/organisations', organisations.getOrganisations);
//router.get('/api/organisations/:id', organisations.getOrganisationById);
//router.get('/api/organisations/:id/buildings', buildings.getBuildingsByOrganisationId);

router.get('/api/users', user.getUsers);

//New data or update data in the database
//router.post('/api/organisations', organisations.createOrganisation);
//router.put('/api/organisations/:id', organisations.updateOrganisationById);

//
module.exports = router;


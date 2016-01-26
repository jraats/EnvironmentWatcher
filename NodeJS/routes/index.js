var express = require('express');
var router = express.Router();
var user = 			require ('./services/user.js');
var product = 		require ('./services/product.js');
var preferences = 	require ('./services/preferences.js');
var sensorData = 	require ('./services/sensorData.js');

router.get('/api/ping', function(req, res){
		res.json({status: "OK"});
});

//Get data from database
router.get('/api/user', user.getUsers);
router.get('/api/user/:username', user.getUserByUsername);

router.get('/api/product', product.getproducts);
router.get('/api/product/:id', product.getProductById);

router.get('/api/preferences', preferences.getPreferences);
router.get('/api/preferences/:userUsername', preferences.getPreferencesByUsername);
router.get('/api/preferences/getByProductID/:productId', preferences.getPreferencesByProductID);

router.get('/api/sensorData', sensorData.getSensorData);
router.get('/api/sensorData/:productId', sensorData.getDataByProductId);
router.get('/api/sensorData/getLatest/:productId', sensorData.getLatestDataByProductId);
router.get('/api/sensorData/getDataByTime/:productId/:d1/:d2', sensorData.getDataByTime);

//New data or update data in the database
router.post('/api/user', user.createUser);
router.put('/api/user/:username', user.updateUserByUsername);

router.post('/api/product', product.createProduct);
router.put('/api/product/:id', product.updateProductById);

router.post('/api/preferences', preferences.createPreferences);
router.put('/api/preferences/:userUsername', preferences.updatePreferenceByUsername);

router.post('/api/sensorData', sensorData.createSensorData);

//Deleting data from database
router.delete('/api/user/:username', user.deleteUserByUsername);

module.exports = router;


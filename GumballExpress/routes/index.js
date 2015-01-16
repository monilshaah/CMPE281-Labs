
/*
 * GET home page.
 */

var Client = require('node-rest-client').Client;

client = new Client();
var data = '';

exports.index = function(req, res){
	client.get("http://localhost:8080/GumballMachineREST/gumball/1", function(data, response){
	    // parsed response body as js object
	    console.log(data);
	    // raw response
	    //console.log(response);
	    res.render('index', { title: 'Gumball Machine', state: data.state, model: data.model, serial: data.serial, count: data.gumballCount, hash: data.hash });
	});	
};

exports.handle_post = function(req, res) {
	var args = {
			headers:{"Content-Type":"application/json"},
			//data:{state: req.body.state},
			parameters:{state: req.body.state, model: req.body.model, serial: req.body.serial, gumballCount: req.body.count, hash: req.body.hash}
	};
	client.post("http://localhost:8080/GumballMachineREST/gumball/1", args, function(data, response){
		console.log(data);
		//console.log(response);
		res.render('index', { title: 'Gumball Machine', state: data.state, model: data.model, serial: data.serial, count: data.gumballCount, hash: data.hash });
	});
};
	
exports.handle_put = function(req, res) {
		var args = {
				headers:{"Content-Type":"application/json"},
				//data:{state: req.body.state},
				parameters:{state: req.body.state, model: req.body.model, serial: req.body.serial, gumballCount: req.body.count, hash: req.body.hash}
		};
		client.put("http://localhost:8080/GumballMachineREST/gumball/1", args, function(data, response){
			console.log(data);
			//console.log(response);
			res.render('index', { title: 'Gumball Machine', state: data.state, model: data.model, serial: data.serial, count: data.gumballCount, hash: data.hash });
		});
};
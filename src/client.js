/**
* Depends on jQuery and server.js
* Made by MLB
**/
var client = {
	
	getBalance: function(number, callback) {
	  $.get(server.ip + "/balance/" + number)
	  .done(function(data) {
	  	callback(data);
	  })
	  .fail(function(error) {
		callback(null, error);
	  });
	},

	setBalance: function(number, increment) {
		$.post(server.ip + "/balance/" + number)
		.done(function() {

		})
		.fail(function() {

		});
	}

};

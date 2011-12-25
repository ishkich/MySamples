var http = require('http');
var util = require('util');
var memcached = require('/home/y-okumura/work/sources/node-npms/node_modules/memcache');
var mcClient = new memcached.Client();
mcClient.connect();

// Pattern 1: split server creation and add listener
// var server = http.createServer();
// server.listen(1337, "127.0.0.1");
// server.on("request", function(req, rsp) {
// 	    console.info("on request in");
// 	    console.info("start mcClient.get");
// 	    mcClient.get('test', function(err, data) {
// 			   console.info("mcClient.get in");
// 			   rsp.write(data);
// 			   rsp.end();
// 			 });
// 	    console.info("finish mcClient.get");
// 	  });

// Pattern 2:
http.createServer(
  function (req, res) {
    res.writeHead(200, {'Content-Type': 'text/plain'});

    mcClient.get('test', function(err, data) {
		   res.end(data);
		 });

  }).listen("1337", "127.0.0.1");
console.info("Listening 127.0.0.1:1337");
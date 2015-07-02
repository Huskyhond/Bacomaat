var express  = require("express"),
    path     = require("path"),
    db       = require("dataModel"),
    fs       = require("fs"),
    bp       = require("body-parser"),
    https    = require("https"),
    http     = require("http");
var app = express();
app.set('json spaces', 10); // Voor de shine
app.use(bp.urlencoded({
  extended: true
}));
var privateKey = fs.readFileSync('openssl/server.key');
var certificate = fs.readFileSync('openssl/server.crt');

var credentials = {key: privateKey, cert: certificate};

var host= "";

function getPublicIp(bankId) {
  console.log(bankId);
  var code = bankId.substring(0, 4).toLowerCase();
  var ip = { path: "", host: "", port:80, extension: "" };
  switch(code) {
    case "mlbi": ip.host = ""; break;
    case "proh": ip.host = "145.24.222.156"; ip.port = 8000; break;
    case "ilmg": ip.host = "145.24.222.103"; ip.port = 8080; break;
    case "atmb": ip.host = "145.24.222.217"; ip.port = 8080; break;
    case "sker": ip.host = "145.24.222.112";
		 ip.path = "/api"; break;
    case "copo": ip.host = "145.24.222.150"; ip.extension = ".php"; break;
  }
  return ip;
}

app.get(["*.js", "*.html"], function(req, res) {
  res.sendFile(path.resolve(__dirname + "/../Client"+ req.path));
});

app.get("/bank", function(req, res) {
  res.json(db.getBank());
});

app.get("/tempremovedit", function(req, res) { // pin token voor import mysql (DIT MOET WEG ALS AUTIMATISCHE REGISTRATIE GEMAAKT IS!!!!)
  db.temp(req.query.nr, req.query.pin, function(item) {
    res.json(item); 
  });
});

/**
* @param path = /login or /balance or whatever
* @param remote = remote object gained from getPublicIp(bankId)
* @param extra = used for token header (token: blabla) and query string for POST forms (reqString)
* @param callback = used for data callback from the remote server
*/
function doPost(path, remote, extra, callback) {
  if(!extra.reqString) { extra.reqString = ""; }
  if(!extra.token) { extra.token = ""; }
  var options = {
    host: remote.host,
    path: remote.path+path+remote.extension,
    port: remote.port,
    method: "POST",
    headers: { 'Content-Type': 'application/x-www-form-urlencoded',
               'Content-Length': extra.reqString.length,
               'token': extra.token
             }
  };
  try {
    var remoteReq = http.request(options, function(res) {
        res.setEncoding('utf8');
        res.on('data', callback);
        res.on('timeout', function() {
          extra.res.json({ success: {}, error: { code: 7, message: "Remote host not available" } });
        });
    });
    remoteReq.on("error", function() {
      console.log("Server not reachable");
      extra.res.json({ success: {}, error: { code: 7, message: "Remote host not available" } });
    });
    remoteReq.write(extra.reqString);
    remoteReq.end();
  }
  catch(e) {
    console.log("Host werkt niet", e);
    extra.res.json({ success: {}, error: { code: 7, message: "Remote host not available" } });
  }
}

app.post("/login", function(req, res) {
  console.log("Login request", req.connection.remoteAddress, req.body);
  if(!req.body.cardId) {
     res.json({"success": {}, "error": { "code": 10, "message": "Banknummer niet opgegeven, gebruik: cardId"  }});
  }
  else if(!req.body.pin) {
     res.json({"success": {}, "error": { "code": 11, "message": "Pincode niet opgegeven, gebruik: pin"  }});
  }
  else if(!req.body.pin.length == 4 || !/^\d+$/.test(req.body.pin)) {
     res.json({"success":{}, "error": { "code": 13, "message": "Pin in onjuist formaat"}});
  }
  else if(req.body.cardId.length < 14 || !/^\d+$/.test(req.body.cardId.substring(4))) {
     res.json({"success":{}, "error": { "code": 12, "message": "Pasnummer onjuist formaat" }});
  }
  else {
     var remote = getPublicIp(req.body.cardId);
     if(remote.host.length < 1) { // Localhost
       host = "";
       db.auth(req.body.cardId, req.body.pin, function(item) {
         console.log(item);
         res.json(item);
       });
     }
     else {
      host = remote;
      doPost("/login", remote, { res: res, reqString: "cardId="+req.body.cardId+"&pin="+req.body.pin }, function (chunk) {
         console.log('Response: ' + chunk);
         
        var parsed = "{ success: {}, error: { code: 7, message: \"SERVER RETURNED NON JSON CODE\" }}";
        try {
          parsed = JSON.parse(chunk);
        }
        catch(e) {
          console.log("host:", host, "failed to parse json", e);
        }
        res.json(parsed);
      });
     }
  }
});


app.post("/logout", function(req, res) {
  console.log("Logout request", req.connection.remoteAddress);
  if(host.length < 1) {
    db.deAuth(req.headers.token, function(item) {
      res.json(item);   
    });
  }
  else {
    doPost("/logout", host, { res: res, token: req.headers.token }, function(chunk) {
      console.log("Logout Response: " + chunk);
      
      var parsed = "{ success: {}, error: { code: 7, message: \"SERVER RETURNED NON JSON CODE\" }}";
      try {
        parsed = JSON.parse(chunk);
      }
      catch(e) {
        console.log("host:", host, "failed to parse json", e);
      }
      res.json(parsed);
    });
  }
});

app.get("/account/:nr/:qu", function(req, res) {
  console.log("API Request in /account ", req.query, "accountNumber", req.params.nr);
  if(req.headers.token) {
    if(req.params.qu == "failed") {
      db.setFail(req.params.nr, function(item) {
        res.json(item);
      });
    }
    else if (req.params.qu == "passed") {
      db.setPassed(req.params.nr, function(item) {
        res.json(item);
      });
    }
  }
  else {
    res.json(db.getError(3)); // Error 5 = No token / No token rights
  }
});

app.post("/balance", function(req, res) {
  console.log("API Request in /balance ", req.body, "token", req.headers.token);
  if(req.headers.token) {
    db.isTokenActive(req.headers.token, function(item) {
      if(item.error.code) {
        res.json(item);
      }
      else {
        db.getBalance(req.headers.token, function(balanceObj) {
          res.json(balanceObj);
        });
      }
    });
  }
  else {
    res.json(db.getError(4)); 
  }
});

app.post("/withdraw", function(req, res) {
  var changeBalance = req.body.amount;

  if(changeBalance) {
     if(host.length < 1) { // Localhost
      db.changeBalance(changeBalance, req.headers.token, function(item) {
        if(item.error.code) { res.json(item); }
        else {
          res.json(item);
        }
      });
     }
     else {
      doPost("/withdraw", host, { res: res, reqString: "amount="+changeBalance, token: req.headers.token }, function (chunk) {
         console.log('Response: ' + chunk);
         var parsed = "{ success: {}, error: { code: 7, message: \"SERVER RETURNED NON JSON CODE\" }}";
         try {
            parsed = JSON.parse(chunk);
         }
         catch(e) {
           console.log("host:", host, "failed to parse json", e);
         }
         res.json(parsed);
      });
     }
  }
  else {
    res.json(db.getError(30));
  }

});

app.get("/", function(req, res) {
  res.send("Hoi");
});

app.post("/", function(req, res) {
  res.send("Hoi");
});

var server = https.createServer(credentials, app).listen(443, function() {
  
  var host = server.address().address;
  var port = server.address().port;

  console.log("Gestart op: https://%s:%s", host, port);

});

var noobServer = http.createServer(app).listen(80, function() {
  console.log("port 80 onveilige rotzooi gestart op http://%s:%s", noobServer.address().address, noobServer.address().port);
});

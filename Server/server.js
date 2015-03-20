var express  = require("express"),
    path     = require("path"),
    db       = require("dataModel"),
    fs       = require("fs"),
    exec     = require("exec"),
    https    = require("https");
var app = express();
app.set('json spaces', 10);

var privateKey = fs.readFileSync('openssl/server.key');
var certificate = fs.readFileSync('openssl/server.crt');

var credentials = {key: privateKey, cert: certificate};

app.get(["*.js", "*.html"], function(req, res) {
  res.sendFile(path.resolve(__dirname + "/../Client"+ req.path));
});

app.get("/bank", function(req, res) {
  res.json(db.getBank());
});

app.get("/balance/:nr", function(req, res) {
  console.log(req.query.token);
  if(req.query.token && db.getTokenRights(req.query.token) > 0) {
    db.getBalance(req.params.nr, function(balanceObj) {
       if(req.query.changeBalance) {
           if(!balanceObj.error) {
               db.changeBalance(req.params.nr, req.query.changeBalance, req.params.token, function(item) {
                   if(item.error) { res.json(item); }
                   else {
                     res.json(item);
                   }
               });
            }
            else {
                res.json(balanceObj);
            }
       }
       else {
           res.json(balanceObj);
       }
    });
  }
  else {
    res.json(db.getError(5)); 
  }
});

var server = https.createServer(credentials, app).listen(443, function() {
  
  var host = server.address().address;
  var port = server.address().port;

  console.log("Gestart op: http://%s:%s", host, port);

});

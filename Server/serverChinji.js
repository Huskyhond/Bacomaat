var express  = require("express"),
    path     = require("path"),
    db       = require("dataModelChinji");

var app = express();
app.set('json spaces', 10);


app.get(["*.js", "*.html"], function(req, res) {
  res.sendFile(path.resolve(__dirname + "/../Client"+ req.path));
});

app.get("/bank", function(req, res) {
  res.json(db.getBank());
});

app.get("/balance/:nr", function(req, res) {
  db.getBalance(req.params.nr, function(balanceObj) {
    if(req.query.changeBalance) {
      if(!balanceObj.error) {
        db.changeBalance(req.params.nr, req.query.changeBalance, function(item) {
          if(item.error) { res.json(item); }
          else {
            res.json(item);
          }
        });
      }
      else
        res.json(balanceObj);
    }
    else {
      console.log(balanceObj, req.params.nr); 
      res.json(balanceObj);
    }
  });
});

var server = app.listen(80, function() {
  
  var host = server.address().address;
  var port = server.address().port;

  console.log("Gestart op: http://%s:%s", host, port);

});

var check = require('check-types');
var mysql = require('mysql');
var AES = require("crypto-js/aes");
var SHA256 = require("crypto-js/sha256");
var MD5 = require("crypto-js/md5");
var db = mysql.createConnection({
  host: "localhost",
  user: "atm",
  password: "Emorage00",
  database: "banknode"
});

db.connect();

module.exports = {

    getBank: function() {
      return {
        id: 02,
        Naam: "MLB"
      };
    },
    temp: function(accnr, pin, callback) { // Deze functie dient verwijdert te worden als we bank pas regisration hebben. 
      var pincrypted = SHA256(accnr+pin).toString();
      console.log(accnr, pin, pincrypted);
      callback({"REMOVE DIT LATER": pincrypted });
    },
    deAuth: function(token, callback) {
      db.query("UPDATE Tokens SET active=0 WHERE token=\"" + token + "\"", function(err, result) {
        if(err) { console.log(err); callback(error(1)); }
        else {
          console.log(result);
          if(result.affectedRows < 1) {
            callback(error(4));
          }
          else {
            callback(success({ code: 1337, token: token, active: 0 }));
          }
        }
      });
    },
    auth: function(accnr, pin, callback) {
      var pincrypted = SHA256(accnr+pin).toString();
      var date = new Date().toMysqlFormat();
      var token = MD5(accnr+date).toString();
      console.log("Auth request", accnr, pin, pincrypted);
      db.query("SELECT accountNumber, failCount FROM Account WHERE accountNumber=\"" + accnr + "\"", function(err, rows) {
          var failcount = 0;
          if(err){ console.log(err); callback(error(1)); }
          else{
            if(rows.length < 1) {
              callback(error(14)); // Bank nr unknown
            }
            else if(rows[0].failCount > 2) {
              callback(error(16)); //Pas blokked
            }
            else { // Bank nummer bekend, pin check doen.
              failcount = rows[0].failCount;
              db.query("SELECT pin FROM Account WHERE pin = \""+pincrypted+"\" AND accountNumber=\""+ accnr + "\"", function(err, rows) {
                if(err){ console.log(err); callback(error(1)); }
                else {
                  if(rows.length < 1) { 
                    failcount = failcount + 1;
                                    
                    db.query("UPDATE Account SET failCount=failCount+1 WHERE accountNumber = \"" + accnr + "\"", function(err, res) {
                      if (err) {
                        console.log("setFail Update", err);
                        callback(error(1));
                      } else {
                          callback({"success": {}, "error": { code: 15, "message": "Pincode onjuist", "failedAttempts": failcount } }); // Pin foutief
                      }
                    });   
                  }
                  else {
                    db.query("INSERT INTO Tokens SET ?", { 
                      accountNumber: accnr,
                      token: token,
                      creationDate: date,
                      active: 1
                    }, function(err, result) {
                       if(err) { console.log(err); callback(error(1)); } else { 
                         db.query("UPDATE Account SET failCount=0 WHERE accountNumber = \"" + accnr + "\"", function(err, res) {
                           if(err) { console.log(err); callback(error(1)); } else {
                             callback({"success": { "token": token }, "error": {} });
                           }
                         });
                       }
                    });
                  }
                }
              });
            }
          }
        });
    },
    isTokenActive: isTokenActive,

    getBalance: function(token, callback) {
      var errorNr = 0;
      var rtn = {};

      db.query("SELECT Account.accountNumber, balance, failCount, dailyLimit, active FROM Account INNER JOIN Tokens ON Account.accountNumber = Tokens.accountNumber WHERE token = \"" + token + "\"" , 
        function(err, rows, fields) {
          if (err) {
            console.log(err);
            callback(error(1));
            return;
          } else {
            if (rows.length < 1)
              errorNr = 4;
            else if (parseInt(rows[0].failCount) > 2)
              errorNr = 16;
            else {
              var mySaldo = rows[0].balance;
              rtn = {
                bankid: 02,
                cardId: rows[0].accountNumber,
                saldo: parseInt(mySaldo),
                failCount: parseInt(rows[0].failCount),
                dailyLimit: parseInt(rows[0].dailyLimit)
              };
            }

            if (errorNr > 0)
              callback(error(errorNr));
            else
              callback(success(rtn));
          }
      });
  },

  setFail: function(accnr, callback) {
    db.query("UPDATE Account SET failCount=failCount+1 WHERE accountNumber = \"" + accnr + "\"", function(err, res) {
      if (err) {
        console.log("setFail Update", err);
        callback(error(1));
      } else {
        db.query("SELECT failCount FROM Account WHERE accountNumber = \"" + accnr + "\"", function(err, rows, fields) {
          if (err) {
            console.log("setFail Select", err);
            callback(error(1))
          } else {
            callback(success({
              pasid: accnr,
              failCount: parseInt(rows[0].failCount)
            }));
          }
        });
      }
    });
  },

  setPassed: function(accnr, callback) {
    db.query("UPDATE Account SET failCount=0 WHERE accountNumber = \"" + accnr + "\"", function(err, res) {
      callback(success({
        pasid: accnr,
        failCount: 0
      }));
    });
  },

  changeBalance: function(changedValue, token, cBCallback) {
    var errorNr = 0;
    var rtn = {};
    changedValue = parseInt(changedValue);
    isTokenActive(token, function(result) {
      if(result.error.code) {
        console.log("changeBalance request with invalid token", token);
        cBCallback(error(4));
      }
      else {
          module.exports.getBalance(token, function(item) {
          if (item.error.code) {
            console.log("error in get balance", item);
            cBCallback(item); // success/error already in get req. 
          }
	      else if((item.success.saldo - changedValue) < 0) {
                console.log(error(32));
		cBCallback(error(32));
              }
              else {
                console.log(item, changedValue);
                db.query("UPDATE Account SET balance = " + (item.success.saldo - changedValue) + " WHERE accountNumber = \"" + item.success.cardId + "\"", function(err, res) {
                  if (err) {
                    console.log(err);
                    cBCallback(error(1));
                  } 
                  else {
                    item.success.saldo = parseInt((item.success.saldo - changedValue));
                    db.query("INSERT INTO Transaction SET ?", {
                      amount: changedValue,
                      accountNumber: item.success.cardId,
                      machineID: 1
                    }, function(err, result) {
                      if (err) {
                        console.log(err);
                        cBCallback(error(1))
                      } 
                      else {
                        var transId = result.insertId;
            			item.success.code = 1337;
                        item.success.transaction = {};
                        item.success.transaction.id = transId;
                        item.success.transaction.cardId = item.success.pasid;
                        item.success.transaction.amount = changedValue;
                        item.success.transaction.machineID = 1;
                        item.success.transaction.date = new Date().toMysqlFormat();
                        console.log("###########", item);
			cBCallback(item);
                      }
                    });
                  }
                });
              }
          });
      }
    });
  },
  getError: function(nr) {
    return error(nr);
  }

};

Date.prototype.toMysqlFormat = function() {
  return this.getFullYear() + "-" + twoDigits(1 + this.getMonth()) + "-" + twoDigits(this.getDate()) + " " + twoDigits(this.getHours()) + ":" + twoDigits(this.getMinutes()) + ":" + twoDigits(this.getSeconds());
};

function twoDigits(d) {
  if (0 <= d && d < 10) return "0" + d.toString();
  if (-10 < d && d < 0) return "-0" + (-1 * d).toString();
  return d.toString();
}

function isTokenActive(token, callback) {
  db.query("SELECT active, creationDate FROM Tokens WHERE token=\"" + token + "\"", function(err, rows) {
    if(err) { console.log(err); callback(error(1)); }
    else if(rows.length < 1) { 
      callback(error(4));
    }
    else if(rows[0].active == 1) {
      var creationDate = new Date(rows[0].creationDate+"".replace(' ', 'T'));
      var now = new Date().getTime();
      creationDate.setMinutes(creationDate.getMinutes()+7); // 7 mins expire date
      var timestamp = creationDate.getTime();
      if((timestamp - now) > 0) {
        callback(success({active: 1}));
      }
      else {
        callback({ success: {},  error: { code: 4, message: "Token is verlopen." } });
      }
    }
    else {
      callback(error(4));
    }
  });
}

function success(obj) {
  return {
    success: obj,
    error: {}
  };
}

function error(nr) {
  var msg = "";
  switch (nr) {
    case 14:
      msg = "Bank nr unknown!";
    break;
    case 1:
      msg = "Database error.";
    break;
    case 3:
      msg = "Geef de token op.";
    case 32:
      msg = "Not enough balance.";
    break;
    case 4:
      msg = "Token niet bekend.";
    break;
    case 15:
      msg = "Pincode onjuist.";
    break;
    case 16:
      msg = "Card Geblokkeerd.";
    break;
    case 30:
      msg = "Geen bedrag opgegeven om af te schrijven, gebruik: amount";
  }
  return {
    success: {},
    error: {
      code: nr,
      message: msg
    }
  };
}

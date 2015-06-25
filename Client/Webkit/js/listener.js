function getUrlVars() {
    var vars = {};
    var parts = window.location.href.replace(/[?&]+([^=&]+)=([^&]*)/gi, function(m,key,value) {
        vars[key] = value;
    });
    return vars;
}

var socket = global.socket;

socket.on("update", function(data) {
  var data = JSON.parse(data);
  console.log(data);
  if(data.page) {
    if(data.page == "code" && global.currentPage != "code") {
      var codelength = (data.codelength ? data.codelength : 0);
      location.href = "code.html?codelength=" + codelength;
    }
    else if(data.page == "scan" && global.currentPage != "scan") {
      location.href = "scan.html";
    }
    else if(data.page == "select" && global.currentPage != "select") {
      var remote = (data.local  == 0 ? data.local : 1);
    	location.href = "select.html?local=" + remote;
    }
    else if(data.page == "money" && global.currentPage != "money") {
    	var message = (data.message ? data.message : "");
    	location.href = "money.html?message=" + message; 
    }
    else if(data.page == "receipt" && global.currentPage != "receipt") {
    	location.href = "receipt.html";
    }
    else if(data.page == "finish" && global.currentPage != "finish") {
    	var message = (data.message ? data.message : "");
    	location.href = "finish.html?message=" + message;
    }
    else if(data.page == "balance" && global.currentPage != "balance") {
      var balance = (data.amount ? data.amount : "");
      location.href = "balance.html?balance=" + balance;
    }
    else if(data.page == "confirm" && global.currentPage != "confirm") {
      var amount = (data.amount ? data.amount : "");
      var message = (data.message ? data.message : "");
      location.href = "confirm.html?message=" + message + "&amount=" + amount;
    }
  }
});
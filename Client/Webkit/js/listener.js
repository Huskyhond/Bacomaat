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
  if(data.page) {
    
    if(data.page == "code" && global.currentPage != "code") {
      var codelength = (data.codelength ? data.codelength : 0);
      location.href = "code.html?codelength=" + codelength;
    }
    else if(data.page == "scan" && global.currentPage != "scan") {
      location.href = "scan.html";
    }
    else if(data.page == "select" && global.currentPage != "select") {
    	location.href = "select.html";
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

  }
});
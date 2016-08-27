/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


//first link the login button with the enter key.

  window.onkeyup = function(event) {

                event = event || window.event; // IE-ism

                var keyCode = event.keyCode;               
                if (keyCode === 13) { //click the next                     
                    checkLoginForm();
                }
               
            };


function login(){ 
   var command = "login";
   
   var username = document.getElementById("username").value;
   var password = document.getElementById("password").value;
       
   var url = "UserAccount?command="+command
             +"&username="+username
             +"&password="+password;
    
    //alert(url);
    var xmlHttpRequest = getXMLHttpRequest();
    xmlHttpRequest.onreadystatechange = function()
    {
        if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status === 200)
        {               
            if(xmlHttpRequest.responseText === "false"){
                document.getElementById("wrongPassword").style.display="block";
            }
            else{
                window.open("managestudies.html");
            }
            
            
        }
    };
    xmlHttpRequest.open("GET", url, true);
    xmlHttpRequest.send();
}

function defaultLogin(){
     var command = "login";
   
   var username = "mershack";
   var password = "abc";
       
   var url = "UserAccount?command="+command
             +"&username="+username
             +"&password="+password;
    
    //alert(url);
    var xmlHttpRequest = getXMLHttpRequest();
    xmlHttpRequest.onreadystatechange = function()
    {
        if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status === 200)
        {               
            if(xmlHttpRequest.responseText === "false"){
                document.getElementById("wrongPassword").style.display="block";
            }
            else{
                window.location = "index.html";
            }
        }
    };
    xmlHttpRequest.open("GET", url, true);
    xmlHttpRequest.send();
}


function checkLoginForm(){
    //get the parameters.
    //if any of them is empty, show the error, otherwise submit form
    
   var username = document.getElementById("username").value;
   var password = document.getElementById("password").value;
   
   var error = false;
   
  
   
   if(username === ""|| password===""){
       //show the missing error form       
       document.getElementById("missingEntries").style.display="block";       
       error = true;
   }
   
   
  if(error===false){
        login();
   }
   
    
    
    
}

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

function createNewAccount(){    
    var command = "createNewAccount";
   
    //alert("over here");
    
    var username = document.getElementById("username").value;
   var firstname = document.getElementById("firstname").value;
   var lastname = document.getElementById("lastname").value;
   
   //alert(username);
   var password = document.getElementById("password").value;
       
   var url = "UserAccount?command=" + command
             +"&username="+username
             +"&firstname="+firstname
             +"&lastname="+lastname
             +"&password="+password;
    
    //alert(url);
    var xmlHttpRequest = getXMLHttpRequest();
    xmlHttpRequest.onreadystatechange = function()
    {
        if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status === 200)
        {           
            if(xmlHttpRequest.responseText === "false"){
               document.getElementById("usernameError").style.display = "block";
            }
            else{
                window.open("managestudies.html");
            }
        }
    };
    xmlHttpRequest.open("GET", url, true);
    xmlHttpRequest.send();
}

function checkNewAccountForm(){
    //get the parameters.
    //if any of them is empty, show the error, otherwise submit form
    
   var username = document.getElementById("username").value;
   var firstname = document.getElementById("firstname").value;
   var lastname = document.getElementById("lastname").value;
   
   var password = document.getElementById("password").value;
   var retypepass = document.getElementById("retypePassword").value;
   
   var error = false;
   
   if(password !== retypepass){
       //show the password-mismatch form
       document.getElementById("passMismatch").style.display="block";
       
       error = true;
   }
   
   if(username === ""|| firstname=== "" || lastname=== ""
           || password===""|| retypepass === ""){
       //show the missing error form       
       document.getElementById("missingEntries").style.display="block";       
       error = true;
   }
   
   
  if(error===false){
        createNewAccount();
   }
   
    
    
    
}

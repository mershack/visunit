function getXMLHttpRequest() {
    var xmlHttpReq;
    // to create XMLHttpRequest object in non-Microsoft browsers  
    if (window.XMLHttpRequest) {
        xmlHttpReq = new XMLHttpRequest();
    } else if (window.ActiveXObject) {
        try {
            //to create XMLHttpRequest object in later versions of Internet Explorer  
            xmlHttpReq = new ActiveXObject("Msxml2.XMLHTTP");
        } catch (exp1) {
            try {
                //to create XMLHttpRequest object in later versions of Internet Explorer  
                xmlHttpReq = new ActiveXObject("Microsoft.XMLHTTP");
            } catch (exp2) {
                //xmlHttpReq = false;  
                alert("Exception in getXMLHttpRequest()!");
            }
        }
    }
    return xmlHttpReq;
}

function removeDivChildren(div) {
    var last;
    while (last = div.lastChild)
        div.removeChild(last);
}


function removeHTMLElement(element){
    
}


function setUserId(func) {
    var command = "getUserId";



    var url = "UserAccount?command=" + command;
    var xmlHttpRequest = getXMLHttpRequest();
    xmlHttpRequest.onreadystatechange = function()
    {
        if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status === 200)
        {
            //alert(xmlHttpRequest.responseText);
            //create the task option box.      
            //alert(document.getElementById("userid").value);

            if (xmlHttpRequest.responseText.trim() !== "") {
                document.getElementById("userid").value = xmlHttpRequest.responseText;
                // alert(document.getElementById("userid").value);
                if (func) {
                    func();
                }
            }
            else{
                window.location = "login.html";
            }
        }
    };
    xmlHttpRequest.open("GET", url, true);
    xmlHttpRequest.send();
}

/**
 * capitalize the first letter.
 * @param {type} string
 * @returns {unresolved}
 */
function capitalizeFirstLetter(string) {
    return string.charAt(0).toUpperCase() + string.slice(1);
}
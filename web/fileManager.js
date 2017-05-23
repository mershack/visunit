/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

function fileManagerCreate(div, whendone){
    
    var thisObject = new Object();
    
    ////////////   Server COMM ////////////////
    
    //Loads the list of all directories, then passes that array to a callback function. 
    //Each directory will be characterized by its name and a list of files in it. Json example:
    //{"name" : "directory1", "files":["file1", "file2", "file3"]}
    //The function receives as argument a callback function to be called once the loading is done.
    //This callback function should be called with three paramaters: (i) a bool indicating success (true) or failure (false); and
    //(ii) an error message in case of failure; (iii) the data array.
    var serverLoad = function(whendone) {

        var theURL = "FileManagement?command=load";
        $.ajax({
            url: theURL,
            success: function(data, status) {
                whendone(true, "", data);
            },
            error: function(data, status) {
                //handle error
                alert("there was an error when loading the directories" + data + status);
            }
        });
    };
    
    //loads the files into the user's dir directory. The dir directory can be assumed to exist. 
    var serverUpload = function(dir, files, whendone) {

        var formData = new FormData();
        for (var i = 0; i < files.length; i++)
            formData.append("File", files[i]);
        
        var theURL = "FileManagement?command=upload&path=" + dir;

        var xmlHttpRequest = getXMLHttpRequest();
        xmlHttpRequest.onreadystatechange = function()
        {
            if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status === 200)
            {
                //use the callback
               // alert("Files have been successfully loaded");
                whendone(true, "");
            }
           // else
               // alert("Couldn't upload files.")
        };
        xmlHttpRequest.open("POST", theURL, true);
        xmlHttpRequest.send(formData);
    }

    //creates a new directory on the server, in the users' managed space
    var serverMkdir = function(name, whendone) {
        var theURL = "FileManagement?command=mkDir&path=" + name;
       
        $.ajax({
            url: theURL,
            success: function(data, status) {
                whendone(true, "", data);
            },
            error: function(data, status) {
                //handle error
                alert("there was an error when creating the directory ___ STATUS: " + status);
            }
        });
    }

    //deletes a file or a directory 
    var serverRm = function(path, whendone) {
        var theURL = "FileManagement?command=rm&path=" + path;
     

        $.ajax({
            url: theURL,
            success: function(data, status) {
                alert("File successfully removed");
                whendone(true, "", data);
            },
            error: function(data, status) {
                //handle error
                alert("there was an error when creating the directory ___ STATUS: " + status);
            }
        });
    }
    
    
    
    ///////////////  GUI setup  //////////////////    
    var guiSetup = function(div){
    
        div.css("font-size","11px");
        div.css("background-color","rgba(0,0,0,0.1)");
        div.css("display","inline-block");
        div.css("padding","5px 5px 5px 5px");
        
        div.append("<div style='font-weight:bold'> Manage your files</div>");
        //on top create a bunch of action buttons
        var actions = div.append("<div style='margin-top:10px'></div>");

        var addFolderButton = $("<span></span>")
                .addClass("actionButton")
                .append("<img src=plus-icon.png width=12px height=12px></img>");
        addFolderButton.click(function(){
            if ($(this).hasClass("pressed"))
                $(this).removeClass("pressed");
            else{
                $(".actionButton").removeClass("pressed");
                $(".actionButton").css("background-color","rgba(0,150,0,0.1)");
                $(this).css("background-color","rgba(0,150,0,0.2)");
                $(this).addClass("pressed");
            }
           
           // $(".actionButton").css("background-color","rgba(0,150,0,0.1)");
           //$(this).css("background-color","rgba(0,150,0,0.2)");
           addFileSection.hide();
           addFolderSection.toggle(); 
        });
        actions.append(addFolderButton);

        var addFilesButton = $("<span></span>")
                .addClass("actionButton")
                .append("<img src=down-icon.png width=12px height=12px></img>");
        addFilesButton.click(function(){
            if ($(this).hasClass("pressed"))
                $(this).removeClass("pressed");
            else{
                $(".actionButton").removeClass("pressed");
                $(".actionButton").css("background-color","rgba(0,150,0,0.1)");
                $(this).css("background-color","rgba(0,150,0,0.2)");
                $(this).addClass("pressed");
            }

           //$(".actionButton").css("background-color","rgba(0,150,0,0.1)");
           //$(this).css("background-color","rgba(0,150,0,0.2)");
           addFolderSection.hide();
           addFileSection.toggle(); 
        });
        actions.append(addFilesButton);

        var removeButton = $("<span></span>")
                .addClass("actionButton")
                .append("<img src=x-icon.png width=12px height=12px></img>");
        removeButton.click(function(){
           $(".actionButton").removeClass("pressed");
           $(".actionButton").css("background-color","rgba(0,150,0,0.1)");
           addFolderSection.hide();
           addFileSection.hide();
           if (thisObject.selected == null)
               alert("Nothing selected.");
           else
               rm(thisObject.selected);

        });
        actions.append(removeButton);
        
        $(".actionButton").css("padding","5px 4px 1px 4px");
        $(".actionButton").css("margin-right","3px");
        $(".actionButton").css("background-color","rgba(0,150,0,0.1");
        $(".actionButton").mouseenter(function(){if (!$(this).hasClass("pressed")) $(this).css("background-color","rgba(0,150,0,0.2)");});
        $(".actionButton").mouseleave(function(){if (!$(this).hasClass("pressed")) $(this).css("background-color","rgba(0,150,0,0.1)");});
 

        var addFolderSection = $("<div style='padding:5px 2px 2px 2px; margin-top:1px; background-color:rgba(0,150,0,0.2)'></div>"); 
        div.append(addFolderSection);
        var newFolderName = $("<input type=text></input>");
        var addNewFolder = $("<input type=button value=add></input>");
        addFolderSection.append("<span>folder name:&nbsp</span>", newFolderName, addNewFolder);
        addNewFolder.click(function(){
            if (thisObject.selected == null)
                alert("No destination selected.");
            else
                mkdir(newFolderName.val(), thisObject.selected)
        });
        addFolderSection.hide();

        var addFileSection = $("<div style='padding:5px 2px 2px 2px; margin-top:1px; background-color:rgba(0,150,0,0.2)'></div>"); 
        div.append(addFileSection);
        var newFiles = $("<input type=file multiple></input>");
        var addNewFiles = $("<input type=button value=add></input>");
        addFileSection.append(newFiles, addNewFiles);
        addNewFiles.click(function(){
            if (thisObject.selected == null)
                alert("No destination selected.");
            else{
                upload(newFiles[0].files, thisObject.selected);
            }
        });
        addFileSection.hide();

        var tree = $("<div style='margin-top:10px'></div>");
        div.append(tree);
        thisObject.treeSlot = tree;
    }
    
    
    var createFolder = function(parentDiv, beforeDiv, root){

        var div = $("<div></div>");
        if (beforeDiv == null)
            parentDiv.append(div);
        else
            div.insertBefore(beforeDiv);
             
        //set up the folder label
        var rtdiv = $("<div><img src=folder-icon.png width=10px height=10px></img>&nbsp" +root.name+ "</div>");
        div.append(rtdiv);
        rtdiv.mouseenter(function(){if (!$(this)[0].selected) $(this).css("background-color","rgba(0,0,0,0.05"); });
        rtdiv.mouseleave(function(){if (!$(this)[0].selected) $(this).css("background-color","rgba(0,0,0,0");  });
        
        //under it, set up the folder content div, offset to the right
        var contentDiv = $("<div style='margin-left:10px'></div>");
        div.append(contentDiv);
        contentDiv.hide();
        
        root.parentDiv = parentDiv;
        root.div = div;
        root.labelDiv = rtdiv;
        root.contentDiv = contentDiv;
         
        rtdiv.click(clickFunction(root, rtdiv, contentDiv));
        
        for (var i=0; i<root.children.length; i++){   
            root.children[i].path = root.path + "/" + root.children[i].name;
            if (root.children[i].isDir)
                createFolder(contentDiv, null, root.children[i]);
        }
        for (var i=0; i<root.children.length; i++){   
            root.children[i].path = root.path + "/" + root.children[i].name;
            if (!root.children[i].isDir)
                createFile(contentDiv, null, root.children[i]);   
        }
            
    };
    
    var createFile = function(parentDiv, beforeDiv, file){
                var fdiv = $("<div>"+ file.name + "</div>");
                if (beforeDiv == null)
                    parentDiv.append(fdiv);
                else 
                    fdiv.insertBefore(beforeDiv);
                fdiv.mouseenter(function(){if (!$(this)[0].selected) $(this).css("background-color","rgba(0,0,0,0.05"); });
                fdiv.mouseleave(function(){if (!$(this)[0].selected)  $(this).css("background-color","rgba(0,0,0,0");  });                
                fdiv.click(clickFunction(file,fdiv));                
                file.parentDiv = parentDiv;
                file.div = fdiv;
    };
    
    var clickFunction = function(child, div, contentDiv){
                     return function(){
                        if (thisObject.selectedDiv != null)
                            thisObject.selectedDiv.css("background-color", "rgba(0,0,0,0)");
                        div.css("background-color", "rgba(0,0,0,0.2)");
                        div[0].selected = true;
                        thisObject.selectedDiv = div;
                        thisObject.selected = child;
                        div.isSelected = true;
                        if (child.isDir) contentDiv.toggle(); 
                        thisObject.selectionChanged(child);
                    }
    };
    
     var findParent = function(root, target){
            if (root.children.length == 0)
                return null;

            for (var i=0; i<root.children.length; i++)
                if (root.children[i].name === target.name)
                    return root;
                else{
                    var p = findParent(root.children[i], target);
                    if (p != null) return p;
                }
            return null;  
    };
        
    var mkdir = function(folderName, targetFolder){
        
        if (!targetFolder.isDir)
            targetFolder = findParent(thisObject.root, targetFolder);
        
        //does the folder already exist? Then skip.
        for (var i=0; i<targetFolder.children.length; i++)
            if (targetFolder.children[i].name === folderName) return;
        
        //this creates the function that adds the folder to the file manager (GUI+root)
        var createf = function(folderName, targetFolder){
          return function(success, msg, data){
                if (!success){
                    alert("Couldn't create new directory in your account:" + msg);
                    return;
                }
                //find the right position where to insert it
                var added = false;
                var newFolder= {name:folderName, isDir:true, children:[], path:targetFolder.path + "/" + folderName};
                for (var i=0; i<targetFolder.children.length; i++){
                        if (folderName < targetFolder.children[i].name || !targetFolder.children[i].isDir){ 
                          //  alert("insert before " + targetFolder.children[i].name);
                            createFolder(targetFolder.contentDiv, targetFolder.children[i].div, newFolder); 
                            targetFolder.children.splice(i,0,newFolder);
                            added = true;
                            break;
                        }
                }
                if (!added){
                    //alert("addding here");
                    createFolder(targetFolder.contentDiv,null,newFolder);
                    targetFolder.children.push(newFolder);
                }
            }  
        };
        
        //try to add the folder to the server; if that worked add it to the GUI
        serverMkdir(targetFolder.path + "/" + folderName, createf(folderName, targetFolder));
    };
    
    
    var addFile = function(file, targetFolder){
    
        for (var i=0; i<targetFolder.children.length; i++)
            if (targetFolder.children[i].name === file)
                return;
    
        var added = false;
        var newFile= {name:file, isDir:false, children:[], path:targetFolder.path + "/" + file};
        for (var i=0; i<targetFolder.children.length; i++){
            if (file < targetFolder.children[i].name && !targetFolder.children[i].isDir){ 
                createFile(targetFolder.contentDiv, targetFolder.children[i].div, newFile); 
                targetFolder.children.splice(i,0,newFile);
                added = true;
                break;
            }
        }
        if (!added){
            createFile(targetFolder.contentDiv,null,newFile);
            targetFolder.children.push(newFile);
        }
    };
    
    var upload = function(files, targetFolder){
        
        if (!targetFolder.isDir) 
            targetFolder = findParent(thisObject.root, targetFolder);
      
        var createf = function(files, targetFolder){
          return function(success, msg, data){
              if (!success){
                  alert("Couldn't upload files to your account: " + msg);
                  return;
              }
              for (var i=0; i<files.length; i++)
              addFile(files[i].name, targetFolder);
          };
        };
        
        //upload all files to server and check success
        serverUpload(targetFolder.path, files, createf(files, targetFolder));
    };

    var rm = function(target){

        var createf = function(target){            
            return function(success, msg, data){
                if (!success){
                    alert("Couldn't delete file: " + msg);
                    return;
                }
                
                var p = findParent(thisObject.root, target);
                if (p == null)
                    alert("An error occured in fileManagerDelete.");
                else{
                    var index = p.children.indexOf(target);
                    p.children.splice(index,1);
                    target.div.remove();
                    if (thisObject.selected == target)
                        thisObject.selected = null;
                }
            };
        };
        
        serverRm(target.path, createf(target));
    };
    
    serverLoad(function(status, message, root){
        if (!status) //some error occured
            whendone(false, message, null);
        else{
            
            root.path = "";
            
            guiSetup(div);
            
            createFolder(thisObject.treeSlot, null, root);
            
            thisObject.selectionChanged = function(obj){};
            thisObject.root = root;
            
            whendone(true, "", thisObject);
        }
        
    });
}












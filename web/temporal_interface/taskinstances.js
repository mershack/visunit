var viewerDatasetInterval;
function createTasksInstancesManually() {
    //first remove the div.
    
   
    
    
    var taskName = $("#taskinstanceform_availabletasks").val();
    var dataName = $("#taskinstanceform_availabledatasets").val();
    var viewerName = $("#taskinstanceform_availableviewers").val();



    getViewerDatasetTask(viewerName, dataName, taskName, function(success, data) {
        var viewer = data.viewer;
        var task = data.task;
        var dataset = data.dataset;

        var ts_viewerDiv = document.getElementById("taskInstanceViewerFrameDiv");
         removeDivChildren(ts_viewerDiv);
        
        var myframe = document.createElement("iframe");
        myframe.setAttribute("id", "taskInstanceViewerFrame");
        myframe.setAttribute("src", viewer.url); //NB: the .. is because of temporal_interface
        ts_viewerDiv.appendChild(myframe);
        
        //now set the dataset.
        viewerDatasetInterval = setInterval(function() {
                setFrameDataset(dataset.url);
            }, 200);
    });
    //getViewerDatasetTask
    //
    //$("#taskinstanceform_taskcreator").html('Your task instance generating html');
    $("#taskinstanceform_taskcreator").show();
}

function setFrameDataset(dataset) {
    var iframe = document.getElementById("taskInstanceViewerFrame");
    var dataset2 = dataset;
    //check the setdataset type that has been implemented
    if (typeof iframe.contentWindow.setDataset == "function") { 
       // alert("hey i'm here");
        iframe.contentWindow.setDataset(dataset2);
        clearInterval(viewerDatasetInterval);
    }   
}




function hideSteps() {
    document.getElementById("steps").style.display = "none";
    document.getElementById("showStepsButton").style.display = "block";
    document.getElementById("hideStepsButton").style.display = "none";
}

function showSteps() {
    document.getElementById("steps").style.display = "block";
    document.getElementById("showStepsButton").style.display = "none";
    document.getElementById("hideStepsButton").style.display = "block";
}
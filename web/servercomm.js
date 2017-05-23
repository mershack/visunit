
//Loads the list of all studies managed by a user, then passes them into a callback. 
//Each study has the following json structure: 
//			{"name" : "study1", 
//			 "viewers" : ["viewer1", "viewer2"],
//			 "viewerDesign" : "Within",
//			 "datasets" : ["dataset1", "dataset2"],
//			 "datasetDesign" : "Within",
//			 "tasks" : [{"name" : "Degree", "count" : "5", "time" : "60", "training" : "2"}, {...}],
//			 "intros" : [{"name" : "Intro1", "match" : "viewer1"}],
//			 "tests"  : [{"name" : "Test1", "interface1" : "inter1", "interface2" : "inter2"}],
//			 "width"  : "100"
//			 "height" : "100"
//
//
//The function receives as argument a callback function to be called once the loading is done.
//This callback function should be called with three paramaters: (i) a bool indicating success (true) or failure (false); and
//(ii) an error message in case of failure; (iii) the data array.
function loadStudies(whendone) {
    var theURL = "StudySetup?command=loadDetailsOfAllStudies";

    $.ajax({
        url: theURL,
        success: function(data, status) {
            //handle success. data is [{name:"abc",...}, {name:"bcd",...}]
            for (var i=0; i<data.length; i++)
                data[i].resultsCount = parseInt(data[i].resultsCount);
            whendone(true, "", data);
        },
        error: function(data, status) {
            //handle error
            alert("there was an error when loading the study files__" + status);
        }

    });
}


//Loads the list of all viewers managed by a user into an array, then passes the array to a callback.  
//Each viewer has the following json structure: 
//			{"name" : "viewer1", 
//			 "description" : "blah blah",
//			 "soureDirectory" : "directory1",
//			 "sourceFile" : "file1"}
//
//The function receives as argument a callback function to be called once the loading is done.
//This callback function should be called with three paramaters: (i) a bool indicating success (true) or failure (false); and
//(ii) an error message in case of failure; (iii) the data array.
function loadViewers(whendone) {

    var theURL = "StudySetup?command=loadViewers";
    $.ajax({
        url: theURL,
        success: function(data, status) {
            //handle success. data is [{name:"abc",...}, {name:"bcd",...}] 

            //   alert("here");

            whendone(true, "", data);
        },
        error: function(data, status) {
            //handle error
            alert("there was an error when loading the viewers ___ STATUS: " + status);
        }
    });
}

//Loads the list of all datasets managed by a user into an array, then passes the array to a callback.  
//Each viewer has the following json structure: 
//			{"name" : "dataseet1", 
//			 "description" : "blah blah",
//			 "soureDirectory" : "directory1",
//			 "sourceFile" : "file1"}
//
//The function receives as argument a callback function to be called once the loading is done.
//This callback function should be called with three paramaters: (i) a bool indicating success (true) or failure (false); and
//(ii) an error message in case of failure; (iii) the data array.
function loadDatasets(whendone) {

    var theURL = "StudySetup?command=loadDatasets";
    $.ajax({
        url: theURL,
        success: function(data, status) {
            //handle success. data is [{name:"abc",...}, {name:"bcd",...}] 

            //   alert("here");

            whendone(true, "", data);
        },
        error: function(data, status) {
            //handle error
            alert("there was an error when loading the datasets ___ STATUS: " + status);
        }
    });

}




//Loads the list of all task prototypes managed by a user into an array, then passes this array to a callback 
//Each task proto has the following json structure: 
//			{"name" : "Degree estimation", 
//			 "description" : "A user estimates a nodes graph degree",
//			 "question" : "What is the degree of the highlighted node?",
//			 "inputs" : [{"typeName" : "node", "description" : "The highlighted node", "showInVis" : "yes", "specifyInVis" : "yes"}, {...}],
//			 answer: {type : "Options(fixed)", options : ["option1, "option2"], correctness: yes}
//     or    answer: {type : "Interface/Custom", customTypeName : "node", correctness: no}
//     or    answer: {type : "Number", correctness: yes}
//			}
//
//The function receives as argument a callback function to be called once the loading is done.
//This callback function should be called with three paramaters: (i) a bool indicating success (true) or failure (false); and
//(ii) an error message in case of failure; (iii) the data array.
function loadTaskprotos(whendone) {

    var theURL = "StudySetup?command=loadTasks";
    $.ajax({
        url: theURL,
        success: function(data, status) {
            //handle success. data is 

            whendone(true, "", data);
        },
        error: function(data, status) {
            //handle error
            alert("there was an error when loading the tasks ___ STATUS: " + status);
        }
    });

}

//TBD
function loadTaskinstances(whendone) {

    var theURL = "StudySetup?command=loadTaskInstances";
    $.ajax({
        url: theURL,
        success: function(data, status) {
            //handle success.          

            whendone(true, "", data);
        },
        error: function(data, status) {
            //handle error
            alert("there was an error when loading the task instances ___ STATUS: " + status);
        }
    });


}

/**
 * We will be saving a task instance on the server.
 * @param {type} instanceData
 * @returns {undefined}
 */
function updateTaskInstance(taskName, viewerName, datasetName, instanceData, whendone) {
//we will be sending the task instance data to the server using ajax
    var theURL = "TaskInstancesCreator?command=updateTaskInstanceData";
    theURL += "&taskName=" + taskName + "&viewerName=" + viewerName + "&datasetName=" + datasetName;
    $.ajax({
        url: theURL,
        type: 'GET',
        data: {instanceData: JSON.stringify(instanceData)},
        success: function(data, status) {
            whendone();
        },
        error: function(data, status) {
            //handle error
            alert("there was an errror saving the"
                    + " task instance on the server___ STATUS: " + status);
        }
    });
}

/**
 * This function will get the count of existing task instances.
 * @param {type} taskName
 * @param {type} viewerName
 * @param {type} datasetName
 * @returns {undefined}
 */
function getCountOfTaskInstanceData(taskName, viewerName, datasetName, whendone) {
    //we will be sending the task instance data to the server using ajax
    var theURL = "TaskInstancesCreator?command=getCountOfTaskInstanceData";
    theURL += "&taskName=" + taskName + "&viewerName=" + viewerName + "&datasetName=" + datasetName;
    $.ajax({
        url: theURL,
        type: 'GET',
        dataType: 'json',
        success: function(data, status) {
            whendone(data);
        },
        error: function(data, status) {
            //handle error
            alert("there was an errror when getting the count of existing task instances__STATUS:" + status);
        }
    });
}




//Loads the list of all intros managed by a user into an array, then sends that array to a callback. 
//Each intro has the following json structure: 
//			{"name" : "intro1", 
//			 "description" : "blah blah",
//			 "soureDirectory" : "directory1",
//			 "sourceFile" : "file1"}
//
//The function receives as argument a callback function to be called once the loading is done.
//This callback function should be called with three paramaters: (i) a bool indicating success (true) or failure (false); and
//(ii) an error message in case of failure; (iii) the data array.
function loadIntros(whendone) {

    var theURL = "StudySetup?command=loadIntros";
    $.ajax({
        url: theURL,
        success: function(data, status) {
            //handle success.     
            //alert(data.toSource());

            whendone(true, "", data);
        },
        error: function(data, status) {
            //handle error
            alert("there was an error when loading the Intros ___ STATUS: " + status);
        }
    });

}


//Loads the list of all standardized tests managed by a user into an array, then sends the array to a callback.  
//Each viewer has the following json structure: 
//			{"name" : "test1", 
//			 "description" : "blah blah",
//			 "soureDirectory" : "directory1",
//			 "sourceFile" : "file1"}
//
//The function receives as argument a callback function to be called once the loading is done.
//This callback function should be called with three paramaters: (i) a bool indicating success (true) or failure (false); and
//(ii) an error message in case of failure; (iii) the data array.
function loadTests(whendone) {

    var theURL = "StudySetup?command=loadTests";
    $.ajax({
        url: theURL,
        success: function(data, status) {
            //handle success.     
            //alert(data.toSource());

            whendone(true, "", data);
        },
        error: function(data, status) {
            //handle error
            alert("there was an error when loading the tests ___ STATUS: " + status);
        }
    });
}
/*
 * Get the viewer name and dataset. This function will return a JSON file as follows:
 * { "viewer": {}, "dataset": {} }
 */
function getViewerDatasetTask(viewerName, datasetName, taskName, whendone) {
    var theURL = "StudySetup?command=getViewerDatasetTask";
    theURL += "&taskName=" + taskName;
    if (viewerName !== "no viewer") theURL += "&viewerName=" + viewerName;
    if (datasetName !== "no data") theURL += "&datasetName=" + datasetName;
    $.ajax({
        url: theURL,
        success: function(data, status) {
            whendone(true, data);
        },
        error: function(data, status) {
            //handle error
            alert("there was an error when getting the viewerDataTask from server ___ STATUS: " + status);
        }
    });
}

//the params are: (1) is this a new study (true), or an existing one (false); (2) the study data to save; (3) a function
//to be called when the response arrives; pass two params to this callback: a boolean to indicate success (true) or not (false), and 
//an error message in case of failure.
//check the loadStudies for info on the studydata object structure
function updateStudyDataOnServer(newstudy, oldname, studydata, callback) {
    //alert("update study data")
    //we will be sending the study data to the server using ajax
    var theURL = "StudySetup?command=updateStudyData&new=" + newstudy + "&oldname=" + oldname;
    $.ajax({
        url: theURL,
        type: 'POST',
        data: {studyData: JSON.stringify(studydata)},
        dataType: 'json',
        success: function(data, status) {
            callback(true, "");
        },
        error: function(data, status) {
            //handle error
            alert("there was an errror saving the"
                    + " study on the server___ STATUS: " + status);
        }
    });
}


//the params are: (1) is this a new viewer (true), or an existing one (false); (2) the viewer data to save; (3) a function
//to be called when the response arrives; pass two params to this callback: a boolean to indicate success (true) or not (false), and 
//an error message in case of failure.
//check the loadViewers for info on the viewerData object structure
function updateViewerDataOnServer(newViewer, oldname, viewerData, callback) {
    var theURL = "StudySetup?command=updateViewerData&new=" + newViewer + "&oldname="+oldname;
    $.ajax({
        url: theURL,
        type: 'GET',
        data: {viewerData: JSON.stringify(viewerData)},
        dataType: 'json',
        success: function(data, status) {
            callback(true, "");
        },
        error: function(data, status) {
            //handle error
            alert("there was an errror saving the"
                    + " viewer on the server___ STATUS: " + status);
        }
    });

}


//the params are: (1) is this a new dataset (true), or an existing one (false); (2) the dataset data to save; (3) a function
//to be called when the response arrives; pass two params to this callback: a boolean to indicate success (true) or not (false), and 
//an error message in case of failure.
//check the loadDatasets for info on the datasetdata object structure
function updateDatasetDataOnServer(newDataset, oldname,datasetData, callback) {
    //alert("not implemented: updateDatasetDataOnServer");
    var theURL = "StudySetup?command=updateDatasetData&new=" + newDataset + "&oldname="+oldname;
    $.ajax({
        url: theURL,
        type: 'GET',
        data: {datasetData: JSON.stringify(datasetData)},
        dataType: 'json',
        success: function(data, status) {
            callback(true, "");
        },
        error: function(data, status) {
            //handle error
            alert("there was an errror saving the"
                    + " dataset on the server___ STATUS: " + status);
        }
    });

}

//the params are: (1) is this a new task (true), or an existing one (false); (2) the task data to save; (3) a function
//to be called when the response arrives; pass two params to this callback: a boolean to indicate success (true) or not (false), and 
//an error message in case of failure.
//check the loadTaskprotos for info on the taskData object structure
function updateTaskprotoDataOnServer(newTask, taskData, callback) {

    var theURL = "StudySetup?command=updateTaskData";
    $.ajax({
        url: theURL,
        type: 'GET',
        data: {taskData: JSON.stringify(taskData)},
        dataType: 'json',
        success: function(data, status) {
            callback(true, "");
        },
        error: function(data, status) {
            //handle error
            alert("there was an errror saving the"
                    + " task on the server___ STATUS: " + status);
        }
    });
}

//the params are: (1) is this a new intro (true), or an existing one (false); (2) the intro data to save; (3) a function
//to be called when the response arrives; pass two params to this callback: a boolean to indicate success (true) or not (false), and 
//an error message in case of failure.
//check the loadIntros for info on the introData object structure
function updateIntroDataOnServer(newIntro, oldname, introData, callback) {
    
    var theURL = "StudySetup?command=updateIntroData&new=" + newIntro+ "&oldname="+oldname;;
    $.ajax({
        url: theURL,
        type: 'GET',
        data: {introData: JSON.stringify(introData)},
        dataType: 'json',
        success: function(data, status) {
            callback(true, "");
        },
        error: function(data, status) {
            //handle error
            alert("there was an errror saving the"
                    + " intro on the server___ STATUS: " + status);
        }
    });
}


//the params are: (1) is this a new test (true), or an existing one (false); (2) the test data to save; (3) a function
//to be called when the response arrives; pass two params to this callback: a boolean to indicate success (true) or not (false), and 
//an error message in case of failure.
//check the loadTests for info on the testData object structure
function updateTestDataOnServer(newTest, oldname, testData, callback) {

    var theURL = "StudySetup?command=updateTestData&new=" + newTest + "&oldname="+oldname;;
    $.ajax({
        url: theURL,
        type: 'GET',
        data: {testData: JSON.stringify(testData)},
        dataType: 'json',
        success: function(data, status) {
            callback(true, "");
        },
        error: function(data, status) {
            //handle error
            alert("there was an errror saving the"
                    + " test on the server___ STATUS: " + status);
        }
    });


}

//creates a task from an xml file; if a task with the same name exists, return error. 
//the function should call the callback function once the server returns the result of the operation
//the callback takes three params: (i) a boolean ndicating success (true) or failure (false); (ii) a message of the error;
//(iii) and a task object of the created task.
function loadTaskXMLFile(file, callback) {
    var taskObj = new Object();
    taskObj.name = "xmltask";
    taskObj.description = "xmldescription";
    taskObj.question = "xmlquestion";
    taskObj.inputs = [];
    taskObj.answer = {type: "Number", correctness: "yes"};
    callback(true, "", taskObj);
}

function updateTaskInstanceWithAJSONFile(instanceDetail, file, whendone) {
    var taskName = instanceDetail.taskName;
    var dataName = instanceDetail.dataName;
    var viewerName = instanceDetail.viewerName;

    //we will be sending the task instance json file to the server

    var theURL = "TaskInstancesCreator?command=updateTaskInstanceWithAFile";
    theURL += "&taskName=" + taskName + "&viewerName=" + viewerName + "&datasetName=" + dataName;


    var formData = new FormData();
    formData.append("File", file);

    var xmlHttpRequest = getXMLHttpRequest();
    xmlHttpRequest.onreadystatechange = function()
    {
        if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status === 200)
        {
            whendone(true, "", xmlHttpRequest.responseText);
        }

    };
    xmlHttpRequest.open("POST", theURL, true);
    xmlHttpRequest.send(formData);

}

//this function will remove the taskinstance file on the server, and return
//success or failure
function removeTaskInstanceFile(taskName, datasetName, viewerName, whendone){
    var theURL = "TaskInstancesCreator?command=removeTaskInstanceFile";
    theURL += "&taskName=" + taskName + "&viewerName=" + viewerName + "&datasetName=" + datasetName;
    $.ajax({
        url: theURL,
        type: 'GET',
        dataType: 'json',
        success: function(data, status) {           
            whendone(true);
        },
        error: function(data, status) {
            //handle error
            alert("there was when removing the taskInstance on the server"
                    + " ___ STATUS: " + status);
            
            whendone(false);
        }
    });
}

function removeStudyFile(studyName, whendone){
    var theURL = "StudySetup?command=removeStudyFile";
    theURL += "&studyName=" + studyName;
    $.ajax({
        url: theURL,
        type: 'GET',
        dataType: 'json',
        success: function(data, status) {
            whendone(true);
        },
        error: function(data, status) {
            //handle error
            alert("there was when removing the study on the server"
                    + " ___ STATUS: " + status);
            
            whendone(false);
        }
    });
}

function removeViewerFile(viewerName, whendone){
    var theURL = "StudySetup?command=removeViewerFile";
    theURL += "&viewerName=" + viewerName;
    $.ajax({
        url: theURL,
        type: 'GET',
        dataType: 'json',
        success: function(data, status) {
            whendone(true);
        },
        error: function(data, status) {
            //handle error
            alert("there was when removing the viewer file on the server"
                    + " ___ STATUS: " + status);
            
            whendone(false);
        }
    });
}

function removeDatasetFile(name, whendone){
     var theURL = "StudySetup?command=removeDatasetFile";
    theURL += "&datasetName=" + name;
    $.ajax({
        url: theURL,
        type: 'GET',
        dataType: 'json',
        success: function(data, status) {
            whendone(true);
        },
        error: function(data, status) {
            alert("there was when removing the test file on the server"
                    + " ___ STATUS: " + status);
            
            whendone(false);
        }
    });
}

function removeTaskprotoFile(name, whendone){
     var theURL = "StudySetup?command=removeTaskprotoFile";
    theURL += "&taskprotoName=" + name;
    $.ajax({
        url: theURL,
        type: 'GET',
        dataType: 'json',
        success: function(data, status) {
            whendone(true);
        },
        error: function(data, status) {
            alert("there was when removing the intro file on the server"
                    + " ___ STATUS: " + status);
            
            whendone(false);
        }
    });
}

function removeTestFile(testName, whendone){
     var theURL = "StudySetup?command=removeTestFile";
    theURL += "&testName=" + testName;
    $.ajax({
        url: theURL,
        type: 'GET',
        dataType: 'json',
        success: function(data, status) {
            whendone(true);
        },
        error: function(data, status) {
            alert("there was when removing the test file on the server"
                    + " ___ STATUS: " + status);
            
            whendone(false);
        }
    });
}

function removeIntroFile(name, whendone){
     var theURL = "StudySetup?command=removeIntroFile";
    theURL += "&introName=" + name;
    $.ajax({
        url: theURL,
        type: 'GET',
        dataType: 'json',
        success: function(data, status) {
            whendone(true);
        },
        error: function(data, status) {
            alert("there was when removing the test file on the server"
                    + " ___ STATUS: " + status);
            
            whendone(false);
        }
    });
}

//get the results of the study
function getResultUrls(studyName, whendone){
    var theURL = "StudyResults?command=getResultUrls";
    theURL += "&studyName=" + studyName;
    $.ajax({
        url: theURL,
        type: 'GET',
        dataType: 'json',
        success: function(data, status) {
            whendone(data, status);
        },
        error: function(data, status) {                   
            whendone(false);
        }
    });
}
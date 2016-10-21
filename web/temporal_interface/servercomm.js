
//Loads the list of all directories, then passes that array to a callback function. 
//Each directory will be characterized by its name and a list of files in it. Json example:
//{"name" : "directory1", "files":["file1", "file2", "file3"]}
//The function receives as argument a callback function to be called once the loading is done.
//This callback function should be called with three paramaters: (i) a bool indicating success (true) or failure (false); and
//(ii) an error message in case of failure; (iii) the data array.
function loadDirectories(whendone) {

    var theURL = "../StudySetup?command=loadDirectories";
    $.ajax({
        url: theURL,
        success: function(data, status) {
            //handle success. data is [{name:"abc",...}, {name:"bcd",...}]

            whendone(true, "", data);
        },
        error: function(data, status) {
            //handle error
            alert("there was an error when loading the directories__" + status);
        }
    });




//	$.get("directories.txt", function(data,status){
//		
//		var dataLines = data.trim().split("\n");
//		var dirs = [];
//		for (var i=0; i<dataLines.length; i++){
//			var line = dataLines[i].trim().split(" || ");
//			var dir = new Object();
//			dir.name = line[0];
//			dir.files = [];
//			for (var j=1; j<line.length; j++)
//				dir.files.push(line[j]);
//		
//			dirs.push(dir);
//		}
//		
//		whendone(true,"", dirs);
//	});

}


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

    var theURL = "../StudySetup?command=loadDetailsOfAllStudies";

    $.ajax({
        url: theURL,
        success: function(data, status) {
            //handle success. data is [{name:"abc",...}, {name:"bcd",...}]
            whendone(true, "", data);
        },
        error: function(data, status) {
            //handle error
            alert("there was an error when loading the study files__" + status);
        }

    });




    //some ajax call that gets all the study data
    /*$.get("studies.txt", function(data,status){
     
     var dataLines = data.trim().split("\n");
     var _studies = [];
     var ind = 0;
     
     for (var i=0; i<dataLines.length; i++){
     var line = dataLines[i].trim().split(" || ");
     var study = new Object();
     study.name = line[0];
     study.viewers = [];
     var nrViewers = parseInt(line[1]);
     for (var j=0; j<nrViewers; j++)
     study.viewers.push(line[2+j]);
     study.viewerDesign = line[2 + nrViewers];
     ind = 2 + nrViewers + 1;
     
     study.datasets = [];
     var nrData = parseInt(line[ind]); ind++;
     for (var j=0; j<nrData; j++)
     study.datasets.push(line[ind + j]);
     study.dataDesign = line[ind + nrData];
     ind = ind + nrData + 1;
     
     var nrTasks = parseInt(line[ind]); ind++;
     study.tasks = [];
     for (var j=0; j<nrTasks; j++){
     var taskobj = new Object();
     taskobj.name = line[ind + (j*4)];
     taskobj.count = line[ind + (j*4) + 1];
     taskobj.time = line[ind + (j*4) + 2];
     taskobj.training = line[ind +(j*4)+3];
     study.tasks.push(taskobj);
     }
     ind = ind + 4*nrTasks;
     
     var nrIntros = parseInt(line[ind]); ind++;
     study.intros = [];
     for (var j=0; j<nrIntros; j++){
     var introObj = new Object();
     introObj.name = line[ind + j*2];
     introObj.match = line[ind + j*2 +1];
     study.intros.push(introObj);
     
     }
     ind = ind + nrIntros*2;
     
     var nrTests = parseInt(line[ind]); ind++;
     study.tests = [];
     for (var j=0; j<nrTests; j++){
     var testobj = new Object();
     testobj.name = line[ind + j*3];
     testobj.interface1 = line[ind + j*3 +1];
     testobj.interface2 = line[ind + j*3 +2];
     study.tests.push(testobj);
     
     }
     ind = ind  + nrTests*3;
     
     study.width = line[ind];
     study.height = line[ind+1];
     
     study.results = null;
     study.entryTasks = ["age task", "experience task"];
     study.exitTasks = ["liked it task", "comments task"];
     
     
     
     _studies.push(study);
     }		
     //study data loaded
     
     
     whendone(true, "", _studies);
     });  */

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

    var theURL = "../StudySetup?command=loadViewers";
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


//    $.get("viewers.txt", function(data, status) {
//        var dataLines = data.trim().split("\n");
//        var _viewers = [];
//        for (var i = 0; i < dataLines.length; i++) {
//            var line = dataLines[i].trim().split(" || ");
//            var viewer = new Object();
//            viewer.name = line[0];
//            viewer.description = line[1];
//            viewer.sourceDirectory = line[2];
//            viewer.sourceFile = line[3];
//            _viewers.push(viewer);
//        }
//        //viewers loaded
//
//
//        whendone(true, "", _viewers);
//    });
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

    var theURL = "../StudySetup?command=loadDatasets";
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

//    $.get("datasets.txt", function(data, status) {
//        var dataLines = data.trim().split("\n");
//        var _datasets = [];
//        for (var i = 0; i < dataLines.length; i++) {
//            var line = dataLines[i].trim().split(" || ");
//            var dataset = new Object();
//            dataset.name = line[0];
//            dataset.description = line[1];
//            dataset.sourceDirectory = line[2];
//            dataset.sourceFile = line[3];
//            _datasets.push(dataset);
//        }
//        //datasets loaded
//
//        whendone(true, "", _datasets);
//    });

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

    var theURL = "../StudySetup?command=loadTasks";
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



//
//    $.get("taskproto.txt", function(data, status) {
//        var dataLines = data.trim().split("\n");
//        var _tasks = [];
//        for (var i = 0; i < dataLines.length; i++) {
//            var line = dataLines[i].trim().split(" || ");
//            var task = new Object();
//            task.name = line[0];
//            task.description = line[1];
//            task.question = line[2];
//            task.inputs = [];
//            var nrInputs = parseInt(line[3]);
//            for (var j = 0; j < nrInputs; j++) {
//                var input = new Object();
//                input.typeName = line[4 + j * 4];
//                input.description = line[4 + j * 4 + 1];
//                input.showInVis = line[4 + j * 4 + 2];
//                input.specifyInVis = line[4 + j * 4 + 3]
//                task.inputs.push(input);
//            }
//            task.answer = new Object();
//            task.answer.type = line[4 + nrInputs * 4];
//            if (task.answer.type === "Options(fixed)") {
//                task.answer.options = [];
//                var nrOptions = parseInt(line[4 + nrInputs * 4 + 1]);
//                for (var j = 0; j < nrOptions; j++)
//                    task.answer.options.push(line[4 + nrInputs * 4 + 2 + j]);
//                task.answer.correctness = line[4 + nrInputs * 4 + 2 + nrOptions];
//            }
//            else if (task.answer.type === "Interface/Custom") {
//                task.answer.customTypeName = line[4 + nrInputs * 4 + 1];
//                task.answer.correctness = line[4 + nrInputs * 4 + 2];
//            }
//            else
//                task.answer.correctness = line[4 + nrInputs * 4 + 1];
//
//            _tasks.push(task);
//        }
//
//        whendone(true, "", _tasks);
//    });

}

//TBD
function loadTaskinstances(whendone) {

    var theURL = "../StudySetup?command=loadTaskInstances";
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





//    $.get("taskinstances.txt", function(data, status) {
//        var dataLines = data.trim().split("\n");
//        var _tasks = [];
//        for (var i = 0; i < dataLines.length; i++) {
//            var line = dataLines[i].trim().split(" || ");
//            var task = new Object();
//            task.dataset = line[0];
//            task.taskproto = line[1];
//            task.viewer = line[2]
//            task.instanceCount = parseInt(line[3]);
//            _tasks.push(task);
//        }
//        //task instance info loaded
//
//
//        whendone(true, "", _tasks);
//    });

}

/**
 * We will be saving a task instance on the server.
 * @param {type} instanceData
 * @returns {undefined}
 */
function updateTaskInstance(taskName, viewerName, datasetName, instanceData, whendone) {
//we will be sending the task instance data to the server using ajax
    var theURL = "../TaskInstancesCreator?command=updateTaskInstanceData";
    theURL += "&taskName=" + taskName + "&viewerName=" + viewerName + "&datasetName=" + datasetName;
    $.ajax({
        url: theURL,
        type: 'POST',
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
    var theURL = "../TaskInstancesCreator?command=getCountOfTaskInstanceData";
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

    var theURL = "../StudySetup?command=loadIntros";
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
//
//    $.get("intros.txt", function(data, status) {
//        var dataLines = data.trim().split("\n");
//        var _intros = [];
//        for (var i = 0; i < dataLines.length; i++) {
//            var line = dataLines[i].trim().split(" || ");
//            var intro = new Object();
//            intro.name = line[0];
//            intro.description = line[1];
//            intro.sourceDirectory = line[2];
//            intro.sourceFile = line[3];
//            _intros.push(intro);
//        }
//        //intros loaded
//        whendone(true, "", _intros);
//    });

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

    var theURL = "../StudySetup?command=loadTests";
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
    var theURL = "../StudySetup?command=getViewerDatasetTask";
    theURL += "&viewerName=" + viewerName + "&datasetName=" + datasetName + "&taskName=" + taskName;
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
function updateStudyDataOnServer(newstudy, studydata, callback) {
    //we will be sending the study data to the server using ajax
    var theURL = "../StudySetup?command=updateStudyData";
    $.ajax({
        url: theURL,
        type: 'GET',
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
function updateViewerDataOnServer(newViewer, viewerData, callback) {
    //alert("not implemented: updateViewerDataOnServer");

    var theURL = "../StudySetup?command=updateViewerData";
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
function updateDatasetDataOnServer(newDataset, datasetData, callback) {
    //alert("not implemented: updateDatasetDataOnServer");

    var theURL = "../StudySetup?command=updateDatasetData";
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

    var theURL = "../StudySetup?command=updateTaskData";
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
function updateIntroDataOnServer(newIntro, introData, callback) {
    //alert("not implemented: updateIntroDataOnServer");

    var theURL = "../StudySetup?command=updateIntroData";
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
function updateTestDataOnServer(newTest, testData, callback) {

    var theURL = "../StudySetup?command=updateTestData";
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

//creates a new directory on the server, in the users' managed space
function createNewDirectoryOnServer(name, callback) {
    //alert("not implemented: createNewDirectoryOnServer; Create on the server a folder with name " + name);

    var theURL = "../StudySetup?command=createNewDirectory&directoryName=" + name;

    $.ajax({
        url: theURL,
        success: function(data, status) {
            //handle success.             

            whendone(true, "", data);
        },
        error: function(data, status) {
            //handle error
            alert("there was an error when creating the directory ___ STATUS: " + status);
        }
    });

}

//loads the files into the user's dir directory. The dir directory can be assumed to exist. 
function addNewFilesOnServer(dir, files, callback) {
    //we will be adding the files on the server.

    var formData = new FormData();
    for (var i = 0; i < files.length; i++)
        formData.append("File", files[i]);


    var theURL = "../StudySetup?command=addNewFiles&directory=" + dir;

    var xmlHttpRequest = getXMLHttpRequest();
    xmlHttpRequest.onreadystatechange = function()
    {
        if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status === 200)
        {
            //use the callback
            alert("Files have been successfully loaded");
            callback(true, "");
        }

    };
    xmlHttpRequest.open("POST", theURL, true);
    xmlHttpRequest.send(formData);
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

    var theURL = "../TaskInstancesCreator?command=updateTaskInstanceWithAFile";
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
    var theURL = "../TaskInstancesCreator?command=removeTaskInstanceFile";
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
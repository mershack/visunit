
//Loads the list of all directories, then passes that array to a callback function. 
//Each directory will be characterized by its name and a list of files in it. Json example:
//			{"name" : "directory1", "files":["file1", "file2", "file3"]}
//The function receives as argument a callback function to be called once the loading is done.
//This callback function should be called with three paramaters: (i) a bool indicating success (true) or failure (false); and
//(ii) an error message in case of failure; (iii) the data array.
function loadDirectories(whendone){
	$.get("directories.txt", function(data,status){
		
		var dataLines = data.trim().split("\n");
		var dirs = [];
		for (var i=0; i<dataLines.length; i++){
			var line = dataLines[i].trim().split(" || ");
			var dir = new Object();
			dir.name = line[0];
			dir.files = [];
			for (var j=1; j<line.length; j++)
				dir.files.push(line[j]);
		
			dirs.push(dir);
		}
		
		whendone(true,"", dirs);
	});

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
function loadStudies(whendone){
	//some ajax call that gets all the study data
        var theURL = "../StudySetup?command=loadDetailsOfAllStudies"; 
        
        $.ajax({
           url: theURL ,
           success: function(data, status){
               //handle success. data is [{name:"abc",...}, {name:"bcd",...}]
               /*Below an example that contain only one study
                * [
	             	{"name":"study1","viewerDesign":"Between",
                             "dataDesign":"Between","viewerWidth":"860",
                             "viewerHeight":"800","trainingSize":"2",
                             "conditions": [{"url": "circularDendrogram/circular.html","shortname": "cond1"},
                                 {"url": "horizontalDedrogram/horizontal.html","shortname": "cond2"}],
                             "datasets": [{"name": "life","format": ".txt"}],                             
                             "intros": [{"name": "group_intro2","directory": "group1_v1","file": "intro_graph_interpLong.html","cond": "MAT", "description": "A sample introduction description2"},
                                        {"name": "group_intro","directory": "group1_v1","file": "intro_matrix_interpLong.html","cond": "NL", "description": "A sample introduction description3"}],
                             "standardTests": [{"name": "Color Blindness","description": "This is a test for color blindness","directory": "ColorBlindnessViewer","file": "color-blind-test.html"}],
                             "tasks": [{"name": "selectNodeWithHighestDegree2","question": "Select the most connected node","size": "3","time": "15"},
                                         {"name": "selectAllNeighborsOf1Node","question": "Select all the neighbors of the highlighted node","size": "3","time": "25"}]}
                        }
                   ]
]
                 */
               alert("study name: "+ data[0].name
                       + " and viewerDesign: "+ data[0].viewerDesign);
                            
           },
           error: function(data, status){
               //handle error
               alert("there was an error when loading the study files");
           }
           
        });
    
            $.get("studies.txt", function(data,status){
		
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
			study.datadesign = line[ind + nrData];
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
			
			
			
			_studies.push(study);
		}		
		//study data loaded
		
		whendone(true, "", _studies);
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
function loadViewers(whendone){

	$.get("viewers.txt", function(data,status){
		var dataLines = data.trim().split("\n");
		var _viewers = [];
		for (var i=0; i<dataLines.length; i++){
			var line = dataLines[i].trim().split(" || ");
			var viewer = new Object();
			viewer.name = line[0];
			viewer.description = line[1];
			viewer.sourceDirectory = line[2];
			viewer.sourceFile = line[3];
			_viewers.push(viewer);
		}
		//viewers loaded
		
		
		whendone(true, "", _viewers);
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
function loadDatasets(whendone){

	
	$.get("datasets.txt", function(data,status){
		var dataLines = data.trim().split("\n");
		var _datasets = [];
		for (var i=0; i<dataLines.length; i++){
			var line = dataLines[i].trim().split(" || ");
			var dataset = new Object();
			dataset.name = line[0];
			dataset.description = line[1];	
			dataset.sourcedirectory = line[2];
			dataset.sourcefile = line[3];
			_datasets.push(dataset);
		}
		//datasets loaded
		
		whendone(true, "", _datasets);
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
function loadTaskprotos(whendone){

	$.get("taskproto.txt", function(data,status){
		var dataLines = data.trim().split("\n");
		var _tasks = [];
		for (var i=0; i<dataLines.length; i++){
			var line = dataLines[i].trim().split(" || ");
			var task = new Object();
			task.name = line[0];
			task.description = line[1];	
			task.question = line[2];
			task.inputs = [];
			var nrInputs = parseInt(line[3]);
			for (var j=0; j<nrInputs; j++){
				var input = new Object();
				input.typeName = line[4 + j*4];
				input.description = line[4 + j*4 + 1];
				input.showInVis = line[4 + j*4 + 2];
				input.specifyInVis = line[4 + j*4 + 3]
				task.inputs.push(input);
			}
			task.answer = new Object();
			task.answer.type = line[4 + nrInputs*4];
			if (task.answer.type === "Options(fixed)"){
				task.answer.options = [];
				var nrOptions = parseInt(line[4 +nrInputs*4 +1]);
				for (var j=0; j<nrOptions; j++)
					task.answer.options.push(line[4 + nrInputs*4 + 2 + j]);
				task.answer.correctness = line[4 + nrInputs*4 + 2 + nrOptions];
			}
			else if (task.answerType === "Interface/Custom"){
				task.answer.customTypeName = line[4+ nrInputs*4 + 1];
				task.answer.correctness = line[4+ nrInputs*4 + 2];
			}
			else
				task.answer.correctness = line[4 + nrInputs*4 + 1];
			
			_tasks.push(task);
		}
		
		whendone(true, "", _tasks);
	});

}

//TBD
function loadTaskinstances(whendone){

	$.get("taskinstances.txt", function(data,status){
		var dataLines = data.trim().split("\n");
		var _tasks = [];
		for (var i=0; i<dataLines.length; i++){
			var line = dataLines[i].trim().split(" || ");
			var task = new Object();
			task.dataset = line[0];
			task.taskproto = line[1];
			task.viewer = line[2]
			task.instances = parseInt(line[3]);
			_tasks.push(task);
		}
		//task instance info loaded

		
		whendone(true, "", _tasks);
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
function loadIntros(whendone){

	
	$.get("intros.txt", function(data,status){
		var dataLines = data.trim().split("\n");
		var _intros = [];
		for (var i=0; i<dataLines.length; i++){
			var line = dataLines[i].trim().split(" || ");
			var intro = new Object();
			intro.name = line[0];
			intro.description = line[1];
			intro.sourceDirectory = line[2];
			intro.sourceFile = line[3];
			_intros.push(intro);
		}
		//intros loaded
		whendone(true, "",_intros);
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
function loadTests(whendone){

	
	$.get("tests.txt", function(data,status){
		var dataLines = data.trim().split("\n");
		var _tests = [];
		for (var i=0; i<dataLines.length; i++){
			var line = dataLines[i].trim().split(" || ");
			var test = new Object();
			test.name = line[0];
			test.description = line[1];
			test.sourcedirectory = line[2];
			test.sourcefile = line[3];
			_tests.push(test);
		}
		//tests loaded
		
		whendone(true, "", _tests);
	});

}



//the params are: (1) is this a new study (true), or an existing one (false); (2) the study data to save; (3) a function
//to be called when the response arrives; pass two params to this callback: a boolean to indicate success (true) or not (false), and 
//an error message in case of failure.
//check the loadStudies for info on the studydata object structure
function updateStudyDataOnServer(newstudy, studydata, callback){
	alert("not implemented: updateStudyDataOnServer");
	callback(true, "");
}


//the params are: (1) is this a new viewer (true), or an existing one (false); (2) the viewer data to save; (3) a function
//to be called when the response arrives; pass two params to this callback: a boolean to indicate success (true) or not (false), and 
//an error message in case of failure.
//check the loadViewers for info on the viewerData object structure
function updateViewerDataOnServer(newViewer, viewerData, callback){
	alert("not implemented: updateViewerDataOnServer");
	callback(true, "");
}


//the params are: (1) is this a new dataset (true), or an existing one (false); (2) the dataset data to save; (3) a function
//to be called when the response arrives; pass two params to this callback: a boolean to indicate success (true) or not (false), and 
//an error message in case of failure.
//check the loadDatasets for info on the datasetdata object structure
function updateDatasetDataOnServer(newDataset, datasetData, callback){
	alert("not implemented: updateDatasetDataOnServer");
	callback(true, "");
}

//the params are: (1) is this a new task (true), or an existing one (false); (2) the task data to save; (3) a function
//to be called when the response arrives; pass two params to this callback: a boolean to indicate success (true) or not (false), and 
//an error message in case of failure.
//check the loadTaskprotos for info on the taskData object structure
function updateTaskprotoDataOnServer(newTask, taskData, callback){
	alert("not implemented: updateTaskprotoDataOnServer");
	callback(true, "");
}

//the params are: (1) is this a new intro (true), or an existing one (false); (2) the intro data to save; (3) a function
//to be called when the response arrives; pass two params to this callback: a boolean to indicate success (true) or not (false), and 
//an error message in case of failure.
//check the loadIntros for info on the introData object structure
function updateIntroDataOnServer(newIntro, introData, callback){
	alert("not implemented: updateIntroDataOnServer");
	callback(true, "");
}


//the params are: (1) is this a new test (true), or an existing one (false); (2) the test data to save; (3) a function
//to be called when the response arrives; pass two params to this callback: a boolean to indicate success (true) or not (false), and 
//an error message in case of failure.
//check the loadTests for info on the testData object structure
function updateTestDataOnServer(newTest, testData, callback){
	alert("not implemented: updateTestDataOnServer");
	callback(true, "");
}

//creates a new directory on the server, in the users' managed space
function createNewDirectoryOnServer(name, callback){
	alert("not implemented: createNewDirectoryOnServer; Create on the server a folder with name " + name);
	callback("true","");
}

//loads the files into the user's dir directory. The dir directory can be assumed to exist. 
function addNewFilesOnServer(dir, files, callback){
	alert("not implemented: createNewDirectoryOnServer; Create on the server a folder with name " + name);
	callback("true","");
}


//creates a task from an xml file; if a task with the same name exists, return error. 
//the function should call the callback function once the server returns the result of the operation
//the callback takes three params: (i) a boolean ndicating success (true) or failure (false); (ii) a message of the error;
//(iii) and a task object of the created task.
function loadTaskXMLFile(file, callback){
	var taskObj = new Object();
	taskObj.name = "xmltask";
	taskObj.description = "xmldescription";
	taskObj.question = "xmlquestion";
	taskObj.inputs = [];
	taskObj.answer = {type:"Number", correctness:"yes"};
	callback(true, "", taskObj);
}
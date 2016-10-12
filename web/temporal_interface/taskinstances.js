var viewerDatasetInterval;
var inputsCnt = 0;
var task;
var instanceInputs = [];
var instanceOptions = [];
var instanceAnswers = [];
var taskInstancesInputs = []; //

var taskInstancesCounter = 1;
var taskName;
var dataName;
var viewerName;

function createTasksInstancesManually() {
    //first remove the div.
     taskName = $("#taskinstanceform_availabletasks").val();
     dataName = $("#taskinstanceform_availabledatasets").val();
     viewerName = $("#taskinstanceform_availableviewers").val();



    getViewerDatasetTask(viewerName, dataName, taskName, function(success, data) {
        var viewer = data.viewer;
        task = data.task;
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

        //set the task question
        setTheTaskQuestion();
        //create the input type and descriptions
        setInputTypeAndInputDescriptions(task);
    });

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


function setTheTaskQuestion() {
    qn = task.question;
    var taskPara = document.getElementById("task");
    taskPara.innerHTML = "<strong>Task: </strong>" + qn;
}

/**
 * Task - task is the task object.
 */
function setInputTypeAndInputDescriptions() {

    //get the task inputs
    var inputs = task.inputs;

    //alert("the input typeName is  "+inputs[0].typeName);


    if (inputs.length > 0) {
        //specify the typeName of the input.
        document.getElementById("inputType").innerHTML = inputs[inputsCnt].typeName;
        //specify the description of the input.
        document.getElementById("inputDescription").innerHTML = inputs[inputsCnt].description;

        var inputMedium = inputs[inputsCnt].inputMedium; //the input medium of that input.


        //NB: currently the input medium can be of two types (i.e. "from-visualization"  and "by-typing".

        //depending on the input type, provide the appropriate answering method.
        if (inputMedium.trim() === "from-visualization") {
            //show the input from visualization
            document.getElementById("input-from-visualization").style.display = "block"
            document.getElementById("input-from-typing").style.display = "none";
        }
        else {
            // alert("kiddo -- ");
            //show the input by typing
            document.getElementById("input-from-visualization").style.display = "none"
            document.getElementById("input-from-typing").style.display = "block";
        }


        //Determine if we should ask for the answer now.
        if (inputsCnt < inputs.length) {

            if (inputs.length === 1 || inputsCnt === (inputs.length - 1)) {

                //disable the next input button.
                document.getElementById("next-input-button").style.display = "none";


                //show this button only if this task requires an answer.
                if (task.answer.correctness === "yes") {
                    document.getElementById("provide-answer-button").style.display = "block";
                }
                else {
                    document.getElementById("nextInstance").disabled = false;
                }
            }
            else {
                //we will show the next input method
                document.getElementById("next-input-button").style.display = "block";
                document.getElementById("provide-answer-button").style.display = "none";
            }
        }
    }
}


function doneWithInputs() {
    //first save the last input provided
    if (saveInputs() === false) {
        return false;
    }

    // alert("taskInstances.length  -- "+taskInstances.length);
    //enable the next button so that it can be clicked on after the inputs has been provided
    document.getElementById("nextInstance").disabled = false;

    //hide inputs div and show answersDivcontainer
    document.getElementById("inputsDiv").style.display = "none";
    document.getElementById("answersDivContainer").style.display = "block";

    //the answer type can be an "interface" type or an inbuilt type
    if ((task.answer.type === "interface")) {
        // do this if it is an interface type

        document.getElementById("answer-from-visualization").style.display = "block";
        document.getElementById("answer-by-typing").style.display = "none";
    }
    else if (task.answer.type === "options(dynamic)") {//if it is options dynamic
        //we will ask the user to specify the options, and then select the correct option as answer
        document.getElementById("dynamic-optionsDiv").style.display = "block";
    }
    //TODO: What do we do if the type of the answer is options(fixed).
    //
    //else if(task.answer.type === "options(fixed)"){}
    else {
        //the user will type their answer using a text box.
        document.getElementById("answer-from-visualization").style.display = "none";
        document.getElementById("answer-by-typing").style.display = "block";
    }
}

/*
 * Saving the inputs the user has selected.
 */
function saveInputs() {
    //check if there is an input value for the medium chosen

    //if there is an input, save it.
    currentInput = "";

    var inputMedium = task.inputs[inputsCnt].inputMedium;

    if (inputMedium === "from-visualization") {
        getSelectedInput();
        currentInput = document.getElementById("selectedInput").value;
        if (currentInput.trim() === "" && does) {
            alert("No inputs have been provided");
            return false;
        }
        document.getElementById("selectedInput").value = "";
        document.getElementById("selectedInput").style.display = "none";

        resetSelectedInput();
    }
    else if (inputMedium === "by-typing") {
        // alert("here we are---bytypeing");
        currentInput = document.getElementById("typedInput").value;

        // alert("currentInput is "+currentInput);

        if (currentInput.trim() === "") {
            alert("No inputs have been provided");
            return false;
        }
        document.getElementById("typedInput").value = "";
    }

    instanceInputs.push(currentInput);
    inputsCnt++;



    //set up the field for the next inputs 
    if (inputsCnt < task.inputs.length) {
        createInputDescriptions();
    }


    return true;

}

/**
 * What to do if the task does not have inputs
 * @returns {undefined}
 */
function noInputs() {

    alert("there were no inputs for this task -- check the noInputs() function implementation");

//    //enable the next button so that it can be clicked on after the inputs has been provided
//    document.getElementById("nextInstance").disabled = false;
//
//    //hide inputs div and show answersDivcontainer
//    document.getElementById("inputsDiv").style.display = "none";
//    document.getElementById("answersDivContainer").style.display = "block";
//
//    //alert("answers group is "+ getAnswerGroup());
//
//    //alert("there are no inputs hooray!");
//
//    //check the answer group
//    if (!(getAnswerGroup().trim() === "widget")) {
//        document.getElementById("answer-from-visualization").style.display = "block";
//        document.getElementById("answer-by-typing").style.display = "none"
//    }
//    else if (getAnswerDataType().trim() === "options-dynamic") {
//        //we will ask the user to specify the options, 
//        //then select the correct option as answer
//        document.getElementById("dynamic-optionsDiv").style.display = "block";
//    }
//    else {
//        //
//    }
}


/** 
 * How to save a task instance. 
 * @returns {Boolean}
 */
function saveInstance() {
    //first the answer
    var ianswer = getInstanceAnswer();

    //does task have correct answer
    if (task.answer.correctness === "no") {
        //There are currently two conditions that will warrant task instances for this.
        //1. If there are inputs to this task.
        //2. if the answer type is dynamic options.

        //1. save inputs if this task required inputs
        if (task.inputs.length > 0) {
            var inputMedium = task.inputs[inputsCnt].inputMedium;

            var currentInput = "";

            if (inputMedium === "from-visualization") {
                getSelectedInput();
                currentInput = document.getElementById("selectedInput").value;
            }
            else if (inputMedium === "by-typing") {
                currentInput = document.getElementById("typedInput").value;
            }

            //save the inputs if there are unsaved inputs.
            if (currentInput !== "") {
                saveInputs();
            }
        }
        //TODO: 2. What to do if the answer type is dynamic options 
    }

    //Make sure an answer is provided for tasks that have correct answer.
    if (ianswer.trim() === "" && task.answer.correctness === "yes") {
        alert("Please provide an answer for the current instance before continuing");
        return false;
    }
    //compose the instance object and send it to be updated on the server.
    var instance = {};
    instance.taskName = taskName;
    instance.viewerName = viewerName;
    instance.datasetName = dataName;
    instance.inputs = instanceInputs;
    instance.answer = ianswer;
    instance.options = instanceOptions;

    updateTaskInstance(instance);
    //reset the instanceInputs and cnt
    instanceInputs = [];
    inputsCnt = 0;

    if (task.answer.type === "options(dynamic)") {
        // alert("about to save the dynamic answer types");
        //allOptionsArr.push(allOptions);

        //TODO: Let's get all the options.
               //resetting the options also
    }

    //unselect the previous answers
    if (task.answer.type.trim() === "interface") {
        //we will be resetting the answer selected in the visualization

        var omutatorMethod = "set" + capitalizeFirstLetter(task.answer.customTypeName);

        var iframe = document.getElementById("taskInstanceViewerFrame");

        if (typeof iframe.contentWindow.window[omutatorMethod] == "function") {
            iframe.contentWindow.window[omutatorMethod]("");
        }
        else {
            alert("Your visualization does not implement the mutator method --" + omutatorMethod + "() for this input");
        }
    }
    else {

        document.getElementById("typedAnswer").value = "";

    }

    //get the instance answer
    incrementTaskInstanceCounter();

    //hide answersDivcontainer and show the inputs div    
    document.getElementById("answersDivContainer").style.display = "none";

    //disable the next instance button
    document.getElementById("nextInstance").disabled = true;

    //now let's create the input description for the next instance.

    // setUpAnswerControllers(taskAnswerType);

    if (task.inputs.length > 0) { //create the next input stringsif this task has an input
        document.getElementById("inputsDiv").style.display = "block";
        setInputTypeAndInputDescriptions();
    }
    else {
        noInputs();
    }



    return true;
}

/**
 * get the answer for the instance that was specified by the user
 * @returns 
 */
function getInstanceAnswer() {
    var answer = "";
    /*NB: if the answer medium is an interface, the answer can either come
     * from the visualization, or the user can choose to manually type the answers.
     */
    if (task.answer.type === "interface") {

        getSelectedAnswer();

        var answerFromVis = document.getElementById("selectedAnswerFromVis").value;

        var answermedium = document.getElementById("answerMedium").value;

        var answerbytyping = "";
        if (answermedium === "by-typing" &&
                (document.getElementById("selectedAnswerFromTyping").value).trim() !== "")
            answerbytyping = document.getElementById("selectedAnswerFromTyping").value;


        document.getElementById("selectedAnswerFromVis").value = "";
        document.getElementById("selectedAnswerFromTyping").value = "";

        document.getElementById("selectedAnswerFromVis").style.display = "none";
        document.getElementById("selectedAnswerFromVis").style.display = "none";
        document.getElementById("answerMedium").value = "";


        if (answerbytyping !== "") {
            answer = answerbytyping;
        }
        else {
            answer = answerFromVis;
        }
    }
    else {
        // alert("+++");
        //check if its dynamic options
        if (task.answer.type.trim() === "options(dynamic)") {
            saveOptionsAndAnswer();
            answer = document.getElementById("selectedAnswer").value;
        }
        //TODO: for options(fixed), we will find the selected answer

        else {
            //the answer will be the typed ansewr
            answer = document.getElementById("typedAnswer").value;
        }
    }

    return answer;
}


/**
 * This function gets the selected answer from the visualization
 * @returns {undefined}
 */

function getSelectedAnswer() {
    //get the answer from the visualization and set it 
    //first get the output interface name, and use it to call and get the answer
    var outputAccessorMethod = getOutputAccessorMethod();

    //now we will actuallly get the answer from the visualization
    var iframe = document.getElementById("viewerFrame");
    if (typeof iframe.contentWindow.window[outputAccessorMethod] == "function") {
        var selectedAnswer = iframe.contentWindow.window[outputAccessorMethod]();

        document.getElementById("selectedAnswerFromVis").value = selectedAnswer;
    }
    else {
        alert("The output method that returns the output is not implemented.");
    }
}



function showAnswerByTyping() {
    //alert("here");
    document.getElementById("selectedAnswerFromTyping").style.display = "inline";
    document.getElementById("answerMedium").value = "by-typing";
}

function getAndShowSelectedInput() {
    getSelectedInput();
    document.getElementById("selectedInputSpan").style.display = "inline";
}

function getAndShowSelectedAnswer() {
    getSelectedAnswer();
    document.getElementById("selectedAnswerFromVis").style.display = "inline";
}


//TODO
function getSelectedInput() {

    var inputAccessorName = getInputAccessorMethods() [inputsCnt];

    var iframe = document.getElementById("viewerFrame");
    if (typeof iframe.contentWindow.window[inputAccessorName] == "function") {
        var input = iframe.contentWindow.window[inputAccessorName]();
        document.getElementById("selectedInput").value = input;
    }
    else {
        alert("You visualization does not implement the accessor method --" + inputAccessorName + "() for this input");
    }
}

//TODO:
function resetSelectedInput() {


    var inputMutatorName = getInputMutatorMethods() [inputsCnt];

    var iframe = document.getElementById("viewerFrame");
    if (typeof iframe.contentWindow.window[inputMutatorName] == "function") {
        var input = iframe.contentWindow.window[inputMutatorName]("");
    }
    else {
        alert("Your visualization does not implement the necessary mutator method --" + inputMutatorName + "() for this input");
    }
}

//TODO: 
function setDynamicOptionAnswer(element) {
    var value = element.value;
    document.getElementById("selectedAnswer").value = document.getElementById("option" + value).value;
    document.getElementById("selectedOption").value = value;
}

//TODO: 
function saveOptionsAndAnswer() {
    //get the size of the options that were provided
    var optionsSize = document.getElementById("numberOfOptions").value;

    allOptions = "";
    optionsAnswer = "";


    var answer = document.getElementById("selectedAnswer").value;
    var selectedOption = document.getElementById("selectedOption").value;
    var selectedOptionValue = "";


    //if selected option has not been selected return false

    if (selectedOption.trim() === "" && doesTaskHaveCorrectAnswer === "yes") {
        return false;
    }
    else if (doesTaskHaveCorrectAnswer === "yes") {
        selectedOptionValue = document.getElementById("option" + selectedOption).value;
    }

    if ((answer.trim() === "" || selectedOptionValue.trim() === "") && doesTaskHaveCorrectAnswer === "yes") {
        //alert("Provide the correct option before proceeding");
        return false;
    }

    optionsAnswer = answer;

    var cnt = 0;

    //alert("optionSize is "+ optionsSize);

    for (var i = 0; i < optionsSize; i++) {
        var option = document.getElementById("option" + (i + 1)).value;

        if (option.trim() !== "") {
            cnt++;

            if (cnt === 1) {
                allOptions = option;
            }
            else {
                allOptions += " :: " + option;
            }
        }
    }


    //reset all the other variables we used here
    //try to remove an additional child if any

    if (optionsSize > 4) {
        var optionsDiv = document.getElementById("optionsDiv").value;

        for (var i = 5; i < optionsSize; i++) {
            optionsDiv.removeChild(document.getElementById("option" + i));
            optionsDiv.removeChild(document.getElementById("optionRadio" + i));
        }
        //reduce the inputCnt

    }
    //reset the values for the options
    for (var i = 0; i < 4; i++) {

        var theOption = document.getElementById("option" + (i + 1));

        //theOption.removeAttribute("checked");
        theOption.value = "";

    }

    //remove the selected paragraph and recreate it. 
    //this is because we couldn't uncheck the radio buttons for some reason

    // alert("parag_option"+selectedOption);


    //do this if an answer was given
    if (doesTaskHaveCorrectAnswer === "yes") {

        var optionDiv = document.getElementById("div_option" + selectedOption);
        removeDivChildren(optionDiv);

        //optionParag.removeChild("option"+selectedOption);
        //optionParag.removeChild("optionRadio"+selectedOption);

        //now lets re-add the radio button and the option text box.
        var parag = document.createElement("p");
        var radioBtn = document.createElement("input");
        radioBtn.setAttribute("type", "radio");
        radioBtn.setAttribute("id", "optionRadio" + selectedOption);
        radioBtn.setAttribute("name", "answerOption")
        radioBtn.setAttribute("value", selectedOption);
        radioBtn.setAttribute("onclick", "setDynamicOptionAnswer(this)");

        var input = document.createElement("input");
        input.setAttribute("type", "text");
        input.setAttribute("id", "option" + selectedOption);
        input.setAttribute("size", "15");


        parag.appendChild(radioBtn);
        parag.appendChild(input);
        optionDiv.appendChild(parag);

        document.getElementById("numberOfOptions").value = 4;
        document.getElementById("selectedOption").value = "";
    }

    return true;
}
/**
 * This will be used to increment the task instance counter
 * @returns {undefined}
 */
function incrementTaskInstanceCounter() {
    taskInstancesCounter++;
    var tnum = document.getElementById("taskInstanceNumber");
    tnum.innerHTML = taskInstancesCounter;
}


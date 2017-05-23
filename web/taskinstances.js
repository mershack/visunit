
var inputsCnt = 0;
var task;
var instanceInputs = [];
var instanceOptions = [];
var instanceAnswers = [];
var taskInstancesInputs = []; //
var instance = null;

var taskInstancesCounter = 1;
var taskName;
var dataName;
var viewerName;

var showVisAnswerInterval;
var showVisAnswerInterval2;

function createTasksInstancesManually() {
    //first remove the div.
    taskName = $("#taskinstanceform_availabletasks").val();
    dataName = $("#taskinstanceform_availabledatasets").val();
    viewerName = $("#taskinstanceform_availableviewers").val();
    
    getViewerDatasetTask(viewerName, dataName, taskName, function(success, data) {
        
        var viewer = data.viewer;
        task = data.task;
        var dataset = data.dataset;
        taskInstancesCounter = 0;
        activateGetAndShowSelectedAnswer(false);
        activateGetAndShowSelectedInput(false);
        
        if (task.inputs.length > 0 && dataName ==="no data"){
            alert("Use the 'no data'/'no viewer' option only for entry and exist tasks.");
            return;
        }
        
         $("#taskinstanceform_taskcreator").show();
        
        var ts_viewerDiv = document.getElementById("taskInstanceViewerFrameDiv");
        removeDivChildren(ts_viewerDiv);
       

        if (viewerName !== "no viewer"){
            var myframe = document.createElement("iframe");
            myframe.setAttribute("id", "taskInstanceViewerFrame");
            myframe.setAttribute("frameBorder","0");
            myframe.setAttribute("src", viewer.url); //NB: the .. is because of temporal_interface
            ts_viewerDiv.appendChild(myframe);
                      
            window.dataLoaded = function(){
                 //set the task question
                setTheTaskQuestion();
                //create the input type and descriptions
                setInputTypeAndInputDescriptions(task);
            };
            myframe.onload = function(){
                myframe.contentWindow.visunitCreatingInstances = true;            
                setFrameDataset(dataset.url, function(){ parent.dataLoaded();})};
        }
        else{
                //set the task question
                setTheTaskQuestion();
                //create the input type and descriptions
                setInputTypeAndInputDescriptions(task);
        }
 
    });
}

function setFrameDataset(dataset, callback) {

    var iframe = document.getElementById("taskInstanceViewerFrame");
    var dataset2 = dataset;
    //check the setdataset type that has been implemented
    if (typeof iframe.contentWindow.setDataset === "function") {
        iframe.contentWindow.setDataset(dataset2, callback);
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
   
    $("#taskinstanceform_taskQuestion").html(task.question);
    $("#taskinstanceform_taskName").html(task.name);
    if (task.description.trim().length === 0)
        $("#taskinstanceform_taskDescription").html("no description available");
    else
        $("#taskinstanceform_taskDescription").html(task.description);
    
    if (task.inputs.length > 0 && (task.answer.correctness ==="yes" || task.answer.type.startsWith("Options(d"))){
        var str = "This task relies on " + "<b>" + task.inputs.length + " inputs</b>" +
                " and an <b> answer</b>" + ". Provide them/it below.";
        $("#taskinstanceform_nrinputsanswers").html(str);
    }
    else if (task.inputs.length > 0)
        $("#taskinstanceform_nrinputsanswers").html("This task relies on <b>" + task.inputs.length + " inputs</b>. Provide them below.");
    else if (task.answer.correctness === "yes" || task.answer.type.toLowerCase().trim().startsWith("options(d"))
        $("#taskinstanceform_nrinputsanswers").html("There can only be one instance of this task as it has no inputs.  Provide answer details for this one and you're done!");
    else
        $("#taskinstanceform_nrinputsanswers").html("There can only be one instance of this task. Just save this one and you're done! ");
}

/**
 * Task - task is the task object.
 */
function setInputTypeAndInputDescriptions() {
     
    //get the task inputs
    var inputs = task.inputs;

    instance = new Object();

    if (inputs.length == 0){
        noInputs();
    }
    else {
     
        $("#taskinstanceform_inputiofn").show();
        $("#taskinstanceform_inputiofn").html("Input " + (inputsCnt+1) + "/" + task.inputs.length);
        //specify the typeName of the input.
        $("#taskinstanceform_inputType").html(inputs[inputsCnt].typeName);
        if (inputs[inputsCnt].description.trim().length !== 0)
            $("#taskinstanceform_inputDescription").html(inputs[inputsCnt].description);
        else
            $("#taskinstanceform_inputDescription").html("none available");

       
        //depending on the input type, provide the appropriate answering method.
        if (inputs[inputsCnt].specifyInVis=== "yes") {
            //show the input from visualization
            $("#input-from-visualization").show();
            document.getElementById('taskInstanceViewerFrame').focus();
            activateGetAndShowSelectedInput(true);
            $("#input-from-typing").hide();
            //try to show help for how to specify the input
            var help = "";
            try{
                var evalstring = "document.getElementById('taskInstanceViewerFrame').contentWindow.help" + inputs[inputsCnt].typeName + "()"; 
                help = eval(evalstring);
             }catch(err){help = ""; };
             if (help.length != 0)
                 $("#taskinstanceform_selecthelp").html("(" + help + ")");

        }
        else {
            //show the input by typing
            $("#input-from-visualization").hide();
            activateGetAndShowSelectedInput(false);
            $("#input-from-typing").show();
        }

        //Determine if we should ask for the answer now.
        if (inputsCnt < inputs.length) {
            
            if (inputsCnt < inputs.length-1){
                //we will show the next input method
                document.getElementById("next-input-button").style.display = "block";
                document.getElementById("provide-answer-button").style.display = "none";
            }
            else{
                $("#next-input-button").hide();
                $("#provide-answer-button").show();
            }
        }
    }
}


function doneWithInputs() {
        
    //first save the last input provided
    if (saveInputs() === false) {
        return false;
    }
    
    $("#inputsDiv").hide();
    $("#answersDiv").show();
    $("#taskinstanceform_inputiofn").html("Answer");
    
    if (!task.answer.type.startsWith("Options(d") && task.answer.correctness !== "yes"){
        saveAnswer(true); //true = no saving of result needed
        return;
    }
    
    var answersDiv = document.getElementById("answersDiv");

    var labelText = "Provide the Correct Answer: ";  
    //the answer type can be an "interface" type or an inbuilt type
    if ((task.answer.type.toLowerCase().startsWith("interface"))) {
        // do this if it is an interface type
        $("#answer-from-visualization").show();
        document.getElementById('taskInstanceViewerFrame').focus();
        
        activateGetAndShowSelectedAnswer(true);
        var help = "";
        try{
            var evalstring = "document.getElementById('taskInstanceViewerFrame').contentWindow.help" + task.answer.customTypeName + "()"; 
            help = eval(evalstring);
        }catch(err){help = "";};
        if (help.length != 0)
        $("#taskinstanceform_selectAnswerHelp").html("(" + help + ")");
    }
    else if (task.answer.type.toLowerCase() === "options(dynamic)") {//if it is options dynamic
        //we will ask the user to specify the options, and then select the correct option as answer
        $("#answer-from-visualization").hide();
        activateGetAndShowSelectedAnswer(false);
        $("#answer_from_typing").hide();
        $("#dynamic-optionsDiv").show();
        $("#taskinstanceform_deloption").attr("disabled",false);
        $("#taskinstanceform_addoption").attr("disabled",false);
        for (var i=0; i<parseInt($("#numberOfOptions").val()); i++)
            $("#option"+(i+1)).attr("disabled", false);
    }
    else if (task.answer.type.toLowerCase() === "options(fixed)"){
        $("#answer-from-visualization").hide();
        activateGetAndShowSelectedAnswer(false);
        $("#answer_from_typing").hide();
        $("#dynamic-optionsDiv").show();
        $("#taskinstanceform_deloption").attr("disabled",true);
        $("#taskinstanceform_addoption").attr("disabled",true);
        var options = task.answer.options;
        while (parseInt($("#numberOfOptions").val()) < options.length){
           addMoreDynamicOptions();
        }
        for (var i=0; i<options.length; i++){
            $("#option"+(i+1)).val(options[i]);
            $("#option"+(i+1)).attr("disabled", true);
        }
    }
    else if (task.answer.type.toLowerCase() === "number" || task.answer.type.toLowerCase() === "text"){
        
        $("#dynamic-optionsDiv").hide();
        $("#answer-from-visualization").hide();
        $("#answer-from-typing").show();
        activateGetAndShowSelectedAnswer(false);
        
    }
        //TODO: What do we do if the type of the answer is options(fixed).

        
}

function saveAnswer(noneNeeded){
   
   if (!noneNeeded){
    var answer = getInstanceAnswer();

    if (answer == null || answer.length === 0){
        alert("It seems you haven't selected an answer.");
        return;
    }
    
    instance.answer = answer;
    
   }  
   
   
   $("#taskinstanceform_inputiofn").hide();
   $("#answersDiv").hide();
   $("#taskinstanceform_instanceComplete").show();
   $("#nextInstance").show();
}

/*
 * Saving the inputs the user has selected.
 */
function saveInputs() {
    
       
    if (task.inputs.length == 0)
        return;
  
    currentInput = "";

    //var inputMedium = ;

    if (task.inputs[inputsCnt].specifyInVis === "yes") {


        getSelectedInput();
        currentInput = $("#selectedInput").html();
        if (currentInput.trim() === "") {
            alert("No inputs have been provided");
            return false;
        }
        $("#selectedInput").html("");
        $("#selectedInputSpan").hide();

        resetSelectedInput();
    }
    else {
        currentInput = document.getElementById("typedInput").value;
        
        if (currentInput.trim() === "") {
            alert("No inputs have been provided");
            return false;
        }
        else{
            if (task.inputs[inputsCnt].specifyInVis)
                try{
                    
                    var setter = "set" + task.inputs[inputsCnt].typeName + "(\"" + currentInput + "\")";
                  
                    var evalstring = "document.getElementById('taskInstanceViewerFrame').contentWindow." + setter; 
                    
                    eval(evalstring);
                }catch(err){}
        }
        document.getElementById("typedInput").value = "";
    }

    instanceInputs.push(currentInput);
    inputsCnt++;
    //set up the field for the next inputs
    if (inputsCnt < task.inputs.length) {
        setInputTypeAndInputDescriptions();
    }
    
    instance.inputs = instanceInputs;


    return true;

}

/**
 * What to do if the task does not have inputs
 * @returns {undefined}
 */
function noInputs() {
    doneWithInputs();
}


/** 
 * How to save a task instance. 
 * @returns {Boolean}
 */
function saveInstance() {

    updateTaskInstance(taskName, viewerName, dataName, instance, function() {
        taskInstanceCreated(1);
    });
    //reset the instanceInputs and cnt
    instanceInputs = [];
    inputsCnt = 0;
    instanceOptions = [];

    if (task.answer.type.toLowerCase() === "options(dynamic)") {
        
    }
     //unselect the previous answers
    if (task.answer.type.toLowerCase() === "interface") {
        //we will be resetting the answer selected in the visualization
        var omutatorMethod = "set" + task.answer.customTypeName;
        var iframe = document.getElementById("taskInstanceViewerFrame");
       
        if (typeof iframe.contentWindow.window[omutatorMethod] == "function") {
            iframe.contentWindow.window[omutatorMethod]("");            
            $("#selectedAnswerFromVis").html("");
        }
        else {
            alert("Your visualization does not implement the mutator method --" + omutatorMethod + "() for this input");
        }
        
        $("#selectedAnswerFromVis").html(" ");
    }
    else {

        document.getElementById("providedAnswer").value = "";

    }

    //get the instance answer
    incrementTaskInstanceCounter();

    //hide answersDivcontainer and show the inputs div    
    document.getElementById("answersDiv").style.display = "none";
    document.getElementById("answer-from-visualization").style.display = "none";
    activateGetAndShowSelectedAnswer(false);
   $("#selectedInput").html("");
   $("#selectedInputSpan").hide();
          
    document.getElementById("typedInput").value = "";


    //now let's create the input description for the next instance.
    if (task.inputs.length > 0) { //create the next input stringsif this task has an input
        document.getElementById("inputsDiv").style.display = "block";
        setInputTypeAndInputDescriptions();
    }
    else {
        noInputs();
    }
    
    //try to clear the vis
    try{
          document.getElementById('taskInstanceViewerFrame').contentWindow.resetVisualization();
    }catch(err){};

    $("#taskinstanceform_instanceComplete").hide();
    $("#nextInstance").hide();


    return true;
}

/**
 * get the answer for the instance that was specified by the user
 * @returns 
 */
function getInstanceAnswer() {
    var answer = "";
    
    if (task.answer.type.toLowerCase().startsWith("interf")) {
        answer = $("#selectedAnswerFromVis").html();
    }
    else {
        if (task.answer.type.toLowerCase().startsWith("options"))
            answer = saveOptionsAndAnswer();   
        else
            answer = $("#typedAnswer").val();
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
    var outputAccessorMethod = "get" + task.answer.customTypeName;
            
     //now we will actuallly get the answer from the visualization
    var iframe = document.getElementById("taskInstanceViewerFrame");
    if (typeof iframe.contentWindow.window[outputAccessorMethod] == "function") {
        var selectedAnswer = iframe.contentWindow.window[outputAccessorMethod]();
     
        $("#selectedAnswerFromVis").html(selectedAnswer);
    }
    else {
        alert("The output method that returns the output is not implemented.");
    }
}

function showAnswerByTyping() {
    
    document.getElementById("selectedAnswerFromTyping").style.display = "inline";
    document.getElementById("answerMedium").value = "by-typing";
}

function getAndShowSelectedInput() {
    try{
        getSelectedInput();
        document.getElementById("selectedInputSpan").style.display = "inline";
    }
    catch(err){};
}

function getAndShowSelectedAnswer() {
    try{
       // alert("here");
        getSelectedAnswer();
        document.getElementById("selectedAnswerSpan").style.display = "inline";
    }
    catch(err){};
}


//TODO
function getSelectedInput() {

      var inputAccessorName = "get" + task.inputs[inputsCnt].typeName;

    var iframe = document.getElementById("taskInstanceViewerFrame");
    
    if (typeof iframe.contentWindow.window[inputAccessorName] == "function") {
        var input = iframe.contentWindow.window[inputAccessorName]();
        //alert("the input is "+ input);
        $("#selectedInput").html(input);
    }
    else {
       // alert("Your visualization does not implement the accessor method --" + inputAccessorName + "() for this input");
    }
}

//TODO:
function resetSelectedInput() {
}

//TODO: 
function setDynamicOptionAnswer(element) {
    var value = element.value;
    document.getElementById("providedAnswer").value = document.getElementById("option" + value).value;
    document.getElementById("selectedOption").value = value;
}

//TODO: 
function saveOptionsAndAnswer() {
    //get the size of the options that were provided
    var optionsSize = $("#numberOfOptions").val();

    var answer = "";
    var selectedOption;
    $("#optionsDiv").find("input").each(function(index,elem){
        if (elem.id.startsWith("optionRadio") && elem.checked){
            selectedOption = elem.id.replace("optionRadio","");
            answer = $("#option"+selectedOption).val();
        }
    });
    var selectedOptionValue = "";
    
    


    if ((answer === null || typeof answer === "undefined" || answer.trim() === "") && task.answer.correctness === "yes")
        return null;
    else selectedOptionValue = answer;

    instance.options = [];
    for (var i = 0; i < optionsSize; i++) {
        var option = $("#option" + (i + 1)).val();
        instance.options.push(option);
    }

    //uncheck
    for (var i=1; i<=optionsSize; i++)
        $("#optionRadio"+i).attr("checked","");

    //if this is a dynamic option then do the rest, otherwise we are done
    if (!$("#taskinstanceform_addoption").attr("disabled")){
        if (optionsSize > 2) {
            var optionsDiv = document.getElementById("optionsDiv");
            for (var i = 3; i <= optionsSize; i++)
                optionsDiv.removeChild(document.getElementById("div_option" + i)); 
        }
        //reset the values for the options
        for (var i = 0; i < 2; i++) {
            var theOption = document.getElementById("option" + (i + 1));

            //theOption.removeAttribute("checked");
            theOption.value = "";
        }    
        document.getElementById("numberOfOptions").value = 2;
    }
    //now hide the div.
     document.getElementById("dynamic-optionsDiv").style.display = "none";

    return selectedOptionValue;
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

function addMoreDynamicOptions() {
    //first get the highest number of boxes,
    //add a new box, and increment the highest number of boxes.
    var optionsSize = parseInt(document.getElementById("numberOfOptions").value);
    optionsSize++;

    
    //get the option's div
    var optionsDiv = document.getElementById("optionsDiv");
    
    //create a new option and add it to it.
    var currentOptionsDiv = document.createElement("div");
    currentOptionsDiv.setAttribute("id", "div_option" + optionsSize);
    //now lets re-add the radio button and the option text box.
    var parag = document.createElement("p");
    var radioBtn = document.createElement("input");
    radioBtn.setAttribute("type", "radio");
    radioBtn.setAttribute("id", "optionRadio" + optionsSize);
    radioBtn.setAttribute("name", "answerOption");
    radioBtn.setAttribute("value", optionsSize);
    radioBtn.setAttribute("onclick", "setDynamicOptionAnswer(this)");

    var input = document.createElement("input");
    input.setAttribute("type", "text");
    input.setAttribute("id", "option" + optionsSize);
    input.setAttribute("size", "15");

    parag.appendChild(radioBtn);
    parag.appendChild(input);

    currentOptionsDiv.appendChild(parag);
    optionsDiv.appendChild(currentOptionsDiv);

    document.getElementById("numberOfOptions").value = optionsSize;
}

function removeDynamicOption(){
    var optionsSize = parseInt(document.getElementById("numberOfOptions").value);
    if (optionsSize == 2){
        alert("Can't have fewer than two options");
        return;
    }
    
    optionsSize--;
    document.getElementById("numberOfOptions").value = optionsSize;   
    var optionsDiv = document.getElementById("optionsDiv");
    optionsDiv.removeChild(optionsDiv.children[optionsDiv.children.length-1]);
}

function activateGetAndShowSelectedInput(yes){
    if (yes)
        showVisAnswerInterval = setInterval(getAndShowSelectedInput, 100);
    else clearInterval(showVisAnswerInterval);
}
function activateGetAndShowSelectedAnswer(yes){
    if (yes)
        showVisAnswerInterval2 = setInterval(getAndShowSelectedAnswer, 100);
    else clearInterval(showVisAnswerInterval2);
}
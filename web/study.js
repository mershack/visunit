standardTestsDetails = [];
standardizedTestCounter = 0;
var taskCounter = 0;
var stage = 0;
var question = "";
var isTutorial = "true";
var viewersChanged = false;
var instruction;
var countdownInterval;
var maxtime = 1;
var questionDurationIntv;
var maxQuestionDuration = 10;
var currDuration;
var currQuestionTime = 0;
var gettingTurkID = "false";
var durationRecorded = false;
var timedout = false;
var nodePosInterval;
var datasetInterval;
var questionsPrepared = false;
var propControlWidth = 350;
var visNoteShown = false;
var inputInterfaceInterval;
var inputValues;
var windowWidth, windowHeight;
var previousQuestion = "";
var colorBlindnessTestAnswers = "";

/**For a color blindness test that we will be doing **/
var checkColorBlindess = true;
var colorBlindnessChecked = false;
var some_answers = [];
var some_answersArrCnt = 0;
var two_hours_default_time_limit = 7200;   //default time for questions which are unlimited time



//var blankViewer;
//var viewersFrame;
window.onload = function() {

    if (checkBrowserSupport() === true) {

        //   viewersFrame = document.getElementById("viewers");
        // blankViewer = document.getElementById("blankViewer");

        getInstruction();
    }
    else {
        document.getElementById("introduction").style.display = "none";
        document.getElementById("browserSupport").style.display = "block";
    }

};


window.onkeyup = function(event) {

    /*  event = event || window.event; // IE-ism
     
     var keyCode = event.keyCode;
     // alert(studystarted);
     //alert(keyCode);
     // if(study)
     if (keyCode == 13 && (studystarted == true || trainingstarted == true)) { //click the next                     
     advanceButtonClicked();
     }
     else if (keyCode == 13 && (gettingTurkId == true)) {
     startStudy();  //call the start study method, which the button would have called anyway.
     }
     
     */


};

function startTraining() {
    trainingstarted = true;
    getQuestion();
}






/*
 * We will be getting the introduction file specified during the 
 * user study design
 * */
function getIntroduction() {

    var command = "getIntroduction";
    var studyid = document.getElementById("studyid").value;

    var url = "StudyManager?command=" + command + "&studyid=" + studyid;

    var xmlHttpRequest = getXMLHttpRequest();

    xmlHttpRequest.onreadystatechange = function()
    {
        if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status === 200)
        {                        //alert(xmlHttpRequest.responseText);
            //  alert(xmlHttpRequest.responseText); 

            if (xmlHttpRequest.responseText.trim() !== "") {

                var gi = document.getElementById("introductionframe");
                gi.setAttribute("src", xmlHttpRequest.responseText);

                document.getElementById("defaultIntroduction").style.display = "none";
                document.getElementById("givenIntroduction").style.display = "block";
            }
            else {
                alert("no introduction file has been specified");
            }


            //   $('.myIframe').css('height', $(window).height() + 'px'); //the height of that window


        }
    };

    xmlHttpRequest.open("GET", url, true);
    xmlHttpRequest.send();





    /*   var iframe = document.getElementById("viewerFrame");
     
     var introductionPageName = "";
     var iframeurl = iframe.src;
     //alert(iframeurl);
     var ind = iframeurl.lastIndexOf("/");
     var pageLocation = iframeurl.substring(0, ind);
     iframe.onload = function() {
     
     if (typeof iframe.contentWindow.setIntroduction == "function") {
     
     introductionPageName = iframe.contentWindow.setIntroduction();
     
     if (introductionPageName && introductionPageName !== "") {
     document.getElementById("defaultIntroduction").style.display = "none";
     document.getElementById("givenIntroduction").style.display = "block";
     
     $('.myIframe').css('height', $(window).height() + 'px'); //the height of that window
     
     }
     }
     };
     
     if (typeof iframe.contentWindow.setIntroduction == "function") {
     introductionPageName = iframe.contentWindow.setIntroduction();
     
     if (introductionPageName && introductionPageName !== "") {
     var introPageUrl = pageLocation + "/" + introductionPageName;
     //alert(introPageUrl);
     
     var gi = document.getElementById("introductionframe");
     gi.setAttribute("src", introPageUrl);
     
     document.getElementById("defaultIntroduction").style.display = "none";
     document.getElementById("givenIntroduction").style.display = "block";
     
     }
     }*/


}

/**
 * Get the Instruction for the user study
 */
function getInstruction() {
    var command = "instruction";

    var url = "StudyManager?command=" + command;

    var xmlHttpRequest = getXMLHttpRequest();

    xmlHttpRequest.onreadystatechange = function()
    {
        if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status === 200)
        {                        //alert(xmlHttpRequest.responseText);
            //alert(xmlHttpRequest.responseText);
            var split = (xmlHttpRequest.responseText).split("::");
            var header = split[0];
            var note = split[1];
            document.getElementById("noteHeader").innerHTML = header;
            document.getElementById("note").innerHTML = note;

            document.getElementById("studyid").value = split[2];
            getFirstConditionViewer(); //call the first condition vie                                
        }
    };

    xmlHttpRequest.open("GET", url, true);
    xmlHttpRequest.send();
}

/**
 * This method gets the url of the first condition viewer 
 */
function getFirstConditionViewer() {
    var command = "firstViewerUrl";

    var studyid = document.getElementById("studyid").value;
    //  "&studyid="+studyid;

    // alert(studyid  + " -- studyid in firstcondit");
    var url = "StudyManager?command=" + command + "&studyid=" + studyid;

    var xmlHttpRequest = getXMLHttpRequest();

    xmlHttpRequest.onreadystatechange = function()
    {
        if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status === 200)
        {
            // alert(xmlHttpRequest.responseText);
            // alert(xmlHttpRequest.responseText);
            createViewerFrame(xmlHttpRequest.responseText);
            //set the introduction                           
            getIntroduction();
            getStandardizedTests();
            getViewerDimensions();
        }
    };

    xmlHttpRequest.open("GET", url, true);
    xmlHttpRequest.send();
}

//method to get the specified width and height of the specified by the evaluator
function getViewerDimensions() {
    var command = "getViewerDimensions";
    var studyid = document.getElementById("studyid").value;

    var url = "StudyManager?command=" + command
            + "&studyid=" + studyid;

    var xmlHttpRequest = getXMLHttpRequest();

    xmlHttpRequest.onreadystatechange = function()
    {
        if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status === 200)
        {
            //alert();
            var dimensions = xmlHttpRequest.responseText;
            var split = dimensions.split("x"); //e.g w x h  e.g. 800 x 600

            //TODO: check if the they are actually integer values.

            var width = Number(split[0]);
            var height = Number(split[1]);

            //set the dimensions of the viewers
            var vframe = document.getElementById("viewerFrame");
            vframe.style.width = width + "px";
            vframe.style.height = height + "px";

            //resize the viewers div
            var vParentFrame = document.getElementById("viewers");
            vParentFrame.style.width = width + "px";
            vParentFrame.style.height = height + "px";

            //resize the blankviewer div
            var blankviewer = document.getElementById("blankViewer");
            blankviewer.style.width = width + "px";
            blankviewer.style.height = height + "px";

            //resize the study div //it should be the viewer width + the prop-control width
            var studydiv = document.getElementById("study");



            //get the window width
            windowWidth = window.innerWidth
                    || document.documentElement.clientWidth
                    || document.body.clientWidth;

            windowHeight = window.innerHeight
                    || document.documentElement.clientHeight
                    || document.body.clientHeight;

            var totalWidth = width + propControlWidth + 50;

            //set the width of the window in case it is >= total width

            //  if(windowWidth > totalWidth){
            studydiv.style.width = totalWidth + "px";
            /* }
             else{ //redirect to a page  that shows that there is an error.
             window.location.replace("screen-dimension-error.jsp?width="+totalWidth + "&height="+height);
             } */







        }
    };

    xmlHttpRequest.open("GET", url, true);
    xmlHttpRequest.send();

}


function  prepareQuestions() {
    var command = "prepareQuestions";
    var studyid = document.getElementById("studyid").value;
    var graphType = "";    //"undirected"; //this is the default graph type in case the user does not specify one
    //get the graph type from the viewer iframe
    //  getGraphType()
    var iframe = document.getElementById("viewerFrame");
    if (typeof iframe.contentWindow.getGraphType == "function") {
        graphType = iframe.contentWindow.getGraphType();
    }

    var url = "StudyManager?command=" + command
            + "&studyid=" + studyid
            + "&graphType=" + graphType;


    var xmlHttpRequest = getXMLHttpRequest();

    xmlHttpRequest.onreadystatechange = function()
    {
        if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status === 200)
        {
            //
        }
    };

    xmlHttpRequest.open("GET", url, true);
    xmlHttpRequest.send();
}

/*
 * get the question from the server
 * 
 */
function getQuestion() {
    //  alert("over here");
    if (stage === 0) {
        //hide the beginTutorial button and show the next button
        document.getElementById("beginTutorial").style.display = "none";
        document.getElementById("nextButton").style.display = "block";
        stage++;
    }
    //get the question
    //get the nodes involved in the question
    var previousAnswer = document.getElementById("selectedAnswer").value; //get the value of the previous Answer

    if (isTutorial === "true") {
        document.getElementById("correctnessOfAnswer").innerHTML = "";
        checkIsTutorial();
    }
    else {
        some_answers.push(previousAnswer);
        // alert(some_answers);
    }

    var studyid = document.getElementById("studyid").value;
    //  "&studyid="+studyid;
    var command = "getQuestion";
    var accuracy = "";

    //get the answer if the answer group is interface

    //get the correctAnswer and the selected answer, And pass it to the visualization


    var correctAnswer = getCorrectAnswer();

    var hasCorrectAns = getHasCorrectAnswer();

    var validateAnswerInterface = getInterfaceForValidatingAnswers();
    if (validateAnswerInterface !== "" && getAnswerGroup() === "interface"
            && previousAnswer != "" && hasCorrectAns === "yes") {

        var iframeContentWindow = document.getElementById("viewerFrame").contentWindow;
        //if the method exists do it
        if (typeof iframeContentWindow.window[validateAnswerInterface] === "function") {

            //this function will be passed the correct answer, and 
            //the answer given by the user
            accuracy = iframeContentWindow.window[validateAnswerInterface](
                    previousAnswer, correctAnswer);
        }
        else {
            alert("The interface for validating answers has not been implemented");
        }
    }
    else {

        if (previousAnswer.trim() === correctAnswer.trim()) {
            accuracy = 1;
        }
        else {
            accuracy = 0;
        }

        //alert(previousAnswer+ "____" +correctAnswer);
    }



    var url = "StudyManager?command=" + command + "&previousAnswer="
            + previousAnswer + "&previousTime=" + currQuestionTime + "&studyid=" + studyid
            + "&accuracy=" + accuracy;

    var xmlHttpRequest = getXMLHttpRequest();

    xmlHttpRequest.onreadystatechange = function()
    {
        if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status === 200)
        {

            //alert("--from getQuestion ---  "+xmlHttpRequest.responseText);
            var split = (xmlHttpRequest.responseText).split("::");
            if (split[0] === "ChangeViewers") {
                //TODO: //change the url of the viewer Frame to the new viewer
                var newViewerUrl = split[1].trim();
                var viewerFrame = document.getElementById("viewerFrame");

                viewerFrame.setAttribute("src", newViewerUrl);
                getAndSetNodePositions();

                //NB: get and set datasets is currently also called in 
                //in the getAndSetNodePositions() function. Ideally, we will change
                //the get and setnode-positions file

                getAndSetDataset();


                setTimeout(getQuestion, 1000);
            }
            else if (split[0] === "EndOfQuantitative") {
                // alert("here1");
                //this is our cue to save the miscellanous information before the actual completion.
                saveMiscellaneousInfo();
            }
            else if (split[0] === "Finished") {
                //alert("Finished Study");
                var turkcode = split[1];
                endOfQuantitative(turkcode);
            }
            else if (split[0] === "Feedback") {
                //call the qualitative function
                showPostStudyQualitativeQuestions(xmlHttpRequest.responseText);

            }
            else {
                var qnheader = split[0];
                var question = split[1];

                //alert(split);
                //alert(split[0]);
                //alert(split[1]);
                var originalqn = question.trim();
                // alert(question);

                //reset the selected answer for subsequent questions
                document.getElementById("selectedAnswer").value = "";

                if (question.trim() === "Remember answers found for previously answered connectivity questions") {
                    //we will hide the viewer for this task. //this is a hack.
                    showBlankViewer();
                }



                //change the question now
                var iframe = document.getElementById("viewerFrame");
                if (typeof iframe.contentWindow.changeQuestion == "function") {
                    var newquestion = iframe.contentWindow.changeQuestion(question.trim());
                    if (newquestion !== "") { //if a newquestion was returned that set the question with  the new question
                        question = newquestion;
                    }
                }

                //Notify the user about the question group they will be working on.
                if (previousQuestion !== "" && previousQuestion !== question

                        && ((qnheader.indexOf("Training Question") > -1)
                                ||
                                qnheader.indexOf("Study Question") > -1)
                        && (qnheader.indexOf("(1/") < 0)

                        ) {
                    //alert("The current question you will be working on is " + question);

                    var qnChgdNotify = document.getElementById("questionChangedNotification");

                    document.getElementById("propControl").style.display = "none";
                    qnChgdNotify.style.display = "block";

                    document.getElementById("questionChangedNotificationQuestion").innerHTML = question;
                    document.getElementById("currentQuestion").value = question;
                    document.getElementById("currentQnHeader").value = qnheader;
                    document.getElementById("currentMaxTime").value = split[2];
                    previousQuestion = question;


                }
                else {
                    document.getElementById("currentQuestion").value = question;
                    document.getElementById("currentQnHeader").value = qnheader;
                    document.getElementById("currentMaxTime").value = split[2];


                    //check if the method of the viewer needs the question to be changed. 
                    //And change it accordingly.
                    /*    var iframe = document.getElementById("viewerFrame");
                     if (typeof iframe.contentWindow.changeQuestion == "function") {
                     var newquestion = iframe.contentWindow.changeQuestion(question.trim());
                     if (newquestion !== "") { //if a newquestion was returned that set the question with  the new question
                     question = newquestion;
                     }
                     }
                     */


                    previousQuestion = question;

                    //get the legend of the question if there is some 
                    if (typeof iframe.contentWindow.getQuestionLegend == "function") {
                        var imgname = "";
                        imgname = iframe.contentWindow.getQuestionLegend(question.trim());
                        showTaskLegend(imgname);
                    }
                    //get the note for the visualization if there is some.
                    if (typeof iframe.contentWindow.getVisualizationNote == "function" && !visNoteShown) {
                        var note = "";
                        note = iframe.contentWindow.getVisualizationNote();
                        showVisualizationNote(note);
                        visNoteShown = true;
                    }

                    if (typeof iframe.contentWindow.getVisualizationHint == "function") {
                        var hint = "";
                        hint = iframe.contentWindow.getVisualizationHint();
                        showVisualizationHint(hint);
                    }

                    maxQuestionDuration = parseInt(split[2]);  // the maximum time for this task
                    document.getElementById("noteHeader").innerHTML = qnheader;
                    document.getElementById("note").innerHTML = question;

                    document.getElementById("selectedAnswer").value = "";

                    getInputsAndAnswerControllers();
                    //do not show the nodes if the study has not been started, 
                    // in that case,they will be shown when the start study button is clicked.
                    if (!(isTutorial === "false" && studystarted === false)) {
                        getNodes();
                    }
                    else {
                        //deselect the previous nodes 
                        deselectPrevNodes();
                    }

                    // alert(question);
                    if (originalqn === "Remember answers found for previously answered connectivity questions") {
                        //we will hide the viewer for this task. //this is a hack.
                        showBlankViewer();
                    }
                    else {
                        revealViewer();
                    }

                    if (studystarted == true) {
                        startQuestionDurationCountDown();
                    }
                }

            }
        }
    };
    xmlHttpRequest.open("GET", url, false);
    xmlHttpRequest.send();
}

function showVisualizationHint(hint) {
    var visHint = document.getElementById("visHint");
    visHint.innerHTML = hint;
}


function showVisualizationNote(note) {

    var visnoteDiv = document.getElementById("visnoteDiv");

    visnoteDiv.innerHTML =
            visnoteDiv.innerHTML + note;
    visnoteDiv.style.display = "block";



    var iframe = document.getElementById("viewerFrame");
    if (typeof iframe.contentWindow.getVisualizationNoteImages == "function") {
        var imageurls = "";
        imageurls = iframe.contentWindow.getVisualizationNoteImages();
        showVisualizationNoteImages(imageurls);
    }
}

function showVisualizationNoteImages(imageurls) {
    if (imageurls !== "") {
        var split = imageurls.split(":");

        var iframeurl = document.getElementById("viewerFrame").src;

        var ind = iframeurl.lastIndexOf("/");
        var pageLocation = iframeurl.substring(0, ind);

        for (var i = 0; i < split.length; i++) {
            var imgurl = pageLocation + "/" + split[i];
            var imgElement = document.createElement("img");
            imgElement.setAttribute("src", imgurl);
            imgElement.setAttribute("width", "110px");
            imgElement.setAttribute("height", "110px");

            var visnoteDiv = document.getElementById("visnoteDiv");

            visnoteDiv.appendChild(imgElement);
            visnoteDiv.style.display = "block";
        }

    }
}



/*
 * function to get the nodes from the server 
 */
function getNodes() {
    // Give a command to get the nodes from the server
    var studyid = document.getElementById("studyid").value;
    var command = "getNodes";
    var url = "StudyManager?command=" + command + "&studyid=" + studyid;
    var xmlHttpRequest = getXMLHttpRequest();

    xmlHttpRequest.onreadystatechange = function()
    {
        if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status === 200)
        {
            //alert("in getting nodes +  "+ xmlHttpRequest.responseText);
            //alert("input values returnede is +" + xmlHttpRequest.responseText);

            if (xmlHttpRequest.responseText.trim() !== "") {
                //if there are actual input values.
                inputValues = xmlHttpRequest.responseText;

                // alert("input values are " + inputValues);

                var inpMutatorMethods = getInputMutatorMethods();
//                            /alert("input mutator methods is "+inpMutatorMethods);

                //alert(inputMutatorMethods);



                // alert(inpMutatorMethods.length);
                if (!(inpMutatorMethods.length > 0) || inpMutatorMethods[0] === "") {
                    inputInterfaceInterval = setInterval("checkIfInputMutatorMethodsIsSet()", 200);
                }
                else {
                    displayNodes(inputValues, inpMutatorMethods);
                }

            }



        }
    };
    xmlHttpRequest.open("GET", url, false);
    xmlHttpRequest.send();
}


function checkIfInputMutatorMethodsIsSet() {
    // alert("hallo");
    //get the array of the input mutator methods
    var mutatorMethodsArr = getInputMutatorMethods();
    //check if at least the first mutator method has been set
    if (mutatorMethodsArr.length > 0 && mutatorMethodsArr[0] !== "") {
        // alert("it is now set  "+ mutatorMethodsArr);
        // alert("input --values is " + inputValues ); 
        clearInterval(inputInterfaceInterval);

        displayNodes(inputValues, mutatorMethodsArr);

    }
}


function deselectPrevNodes() {
    var mutatorMethodsArr = getInputMutatorMethods();
    displayNodes("", mutatorMethodsArr); // send an empty string.
}

/**
 * function to highlight the nodes involved in the question
 *  
 **/
function displayNodes(inputsString, inputMutatorMethodsArr) {

    var theInputs = inputsString.split(":::::"); //NB the inputs are separated by 5 semi-colons

    //now let's check the question to see if any of the inputs will be part of the question               

    //   alert(theInputs);



    var question = document.getElementById("currentQuestion").value;


    //alert("here");

    if (inputsString !== "") {

        var params = findInputParametersInQuestion(question);

        //  alert("params is " + params);

        for (var i = 0; i < theInputs.length; i++) {
            //first check if the input is a parameter or not
            var currParam = i + 1;
            var paramExists = false;
            for (var j = 0; j < params.length; j++) {
                if (currParam === Number(params[j])) {
                    //parameter exists.
                    paramExists = true;
                }
            }

            if (paramExists === false) {
                //pass this input to the visualization using its mutator methods
                var iframeContentWindow = document.getElementById("viewerFrame").contentWindow;

                //if the method exists do it
                if (typeof iframeContentWindow.window[inputMutatorMethodsArr[i]] === "function") {

                    //reset the previous inputs to empty strings
                    iframeContentWindow.window[inputMutatorMethodsArr[i]]("");
                    //now pass the actual input.
                    iframeContentWindow.window[inputMutatorMethodsArr[i]](theInputs[i]);
                }
                else {
                    alert("the interface method " + inputMutatorMethodsArr[i]
                            + "() has not been implemented in your visualization");
                }
            }
        }

        //now replace the params with the inputs
        for (var i = 0; i < params.length; i++) {
            question = question.replace("$" + params[i], theInputs[params[i] - 1]);
        }

        if (params.length > 0) {
            document.getElementById("currentQuestion").value = question;
            document.getElementById("note").innerHTML = question;
        }
    }
    else {



        //send an empty string to each of the input accessor methods
        //if they were not inputs that belong to part of the question text

        if (inputMutatorMethodsArr) {
            for (var i = 0; i < inputMutatorMethodsArr.length; i++) {

                var params = findInputParametersInQuestion(question);
                var currParam = i + 1;
                var paramExists = false;

                for (var j = 0; j < params.length; j++) {
                    if (currParam === Number(params[j])) {
                        //parameter exists.
                        paramExists = true;
                    }
                }

                if (paramExists === false) {
                    var iframeContentWindow = document.getElementById("viewerFrame").contentWindow;
                    //if the method exists do it
                    if (typeof iframeContentWindow.window[inputMutatorMethodsArr[i]] === "function") {
                        iframeContentWindow.window[inputMutatorMethodsArr[i]]("");
                    }
                    else {
                        alert("the interface method " + inputMutatorMethodsArr[i]
                                + "() has not been implemented in your visualization");
                    }
                }

            }
        }

    }

}

function findInputParametersInQuestion(question) {
    //this will find the parameters and return them e.g. if it finds "$1,  $2" it will return [1,2]

    //alert("question is "+ question);
    var params = [];
    var regex = new RegExp('\\d');

    for (var i = 0; i < question.length; i++) {

        if (question.charAt(i) === "$") {

            // alert("found the dollar sign");

            //find out if what follows is a number or not
            var beginIndex;

            //check what follows if it is a number or not
            var regex = new RegExp('\\d');

            if (regex.test(question.charAt(i + 1)) === true) {

                beginIndex = i + 1;
                var j = i + 1;
                for (j = i + 1; j < question.length; j++) {
                    //stop the moment you find a non number value
                    if (regex.test(question.charAt(j)) === false) {
                        break;
                    }
                }

                i = j;

                params.push(Number(question.substring(beginIndex, j)));

            }
        }
    }
    return params;
}



/**
 * function that makes and returns an HttpRequest object 
 */
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

/**
 * function that checks if the current question is a tutorial or not 
 */
function checkIsTutorial() {

    var studyid = document.getElementById("studyid").value;
    var command = "checkIsTutorial";
    var url = "StudyManager?command=" + command + "&studyid=" + studyid;

    var xmlHttpRequest = getXMLHttpRequest();

    xmlHttpRequest.onreadystatechange = function()
    {
        if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status === 200)
        {
            if (xmlHttpRequest.responseText === "true") {
                showCheckAnswerButton();
            }
            else {
                isTutorial = "false";
                gettingTurkID = "true";
                hideCheckAnswerButton();
            }

        }
    };

    xmlHttpRequest.open("GET", url, false);
    xmlHttpRequest.send();
}

function startQuestionDurationCountDown() {
    currDuration = maxQuestionDuration;

    if (maxQuestionDuration <= 0) {
        currDuration = two_hours_default_time_limit;
    }

    durationRecorded = false;
    timedout = false;
    questionDurationIntv = setInterval("questionDurationCountDown()", 1000);

    if (!(maxQuestionDuration > 0)) { //hide the countdown
        document.getElementById("durationCountDown").innerHTML = " ";
    }
}

/**
 * function to countdown the timing for the question
 * 
 */
function questionDurationCountDown() {
    currDuration--;


    if (maxQuestionDuration > 0) { //only show the timer if the max-duration is greater than 0
        document.getElementById("durationCountDown").innerHTML = "Duration:  " + currDuration;
    }
    else {
        document.getElementById("durationCountDown").innerHTML = " ";
    }

    if (currDuration === 0) {
        //show blank screen for the viewer
        timedout = true;
        continueAfterQuestionDurationOver();
        showBlankViewer();
    }
}
/**
 * function to hide the viewer 
 */
function showBlankViewer() {
    //document.getElementById("viewers").style.display = "none";
    //document.getElementById("blankViewer").style.display = "block";

    //   document.getElementById("viewers").style.display = "none";
    document.getElementById("blankViewer").style.display = "block";
}
/**
 *  function to reveal the viewer
 */
function revealViewer() {
    $("#blankViewer").hide();
    // $("#viewers").show();

    //document.getElementById("blankViewer").style.display = "none";
    //document.getElementById("viewers").style.display = "block";
    //document.getElementById("blankViewer").style.display = "none";
    //document.getElementById("viewers").style.display = "block";
    //alert("hey2");
}
/**
 * function that records the duration of the current question and continues to the next question.
 */
function continueAfterQuestionDurationOver() {


    //check if the time was unlimited
    if (maxQuestionDuration > 0) {
        currQuestionTime = maxQuestionDuration - currDuration;//record the time
    }
    else {
        currQuestionTime = two_hours_default_time_limit - currDuration;//record the time
    }


    clearInterval(questionDurationIntv);  //clear the interval variable                
    durationRecorded = true;
    //  alert("here");
}
/*
 * function that is executed when the next button is clicked.   
 */
function advanceButtonClicked() {

    //for widgets we will get the answer from the answer widget
    if (getAnswerGroup() === "widget") {
        var answer = document.getElementById("selectedAnswer").value;
        if (answer === "") {
            alert("Please provide a valid answer before continuing");
            return false;
        }
    }
    //for interface we will get the answer from the interface method of the visualization
    else if (getAnswerGroup() === "interface") {
        //we will get the output by calling the interface method to get the output
        var iframe = document.getElementById("viewerFrame");

        //var outputMutatorMethod = getOutputMutatorMethod();

        var outputAccessorMethod = getOutputAccessorMethod();


        // alert("outputMutator method is + "+outInterfaceName);


        if (typeof iframe.contentWindow.window[outputMutatorMethod] === "function") {
            var outputStr = iframe.contentWindow.window[outputAccessorMethod]();
            //if user was timedout before they could select an answer.

            if (outputStr === "" && timedout === true) {
                outputStr = "timedout";

            }
            if (outputStr === "") {
                alert("Please provide a valid answer before continuing");
                return false;
            }



            document.getElementById("selectedAnswer").value = outputStr;

        }
        else {
            alert("The output method that returns the output is not implemented.");
        }
    }

    if (durationRecorded === false) {

        continueAfterQuestionDurationOver();
    }

    //now call the question get again.
    getQuestion();


}

/**
 * This method creates a frame whose source will be the url that is passed to it.
 */
function createViewerFrame(url) {

    var viewers = document.getElementById("viewers");
    var myframe = document.createElement("iframe");
    myframe.setAttribute("id", "viewerFrame");
    myframe.setAttribute("src", url);
    viewers.appendChild(myframe);
    //get the dataset      

    getAndSetNodePositions();
    getAndSetDataset();

    if (!questionsPrepared) { //questions will be prepared only once.
        prepareQuestions();
        questionsPrepared = true;
    }

}
//this function gets the dataset from the servlet and sets it for the iframe
function getAndSetDataset() {

    // alert(document.getElementById(viewerFrame).src);

    var studyid = document.getElementById("studyid").value;
    //  "&studyid="+studyid;
    var command = "getDataset";
    var url = "StudyManager?command=" + command + "&studyid=" + studyid;


    var xmlHttpRequest = getXMLHttpRequest();

    xmlHttpRequest.onreadystatechange = function()
    {
        if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status === 200)
        {
            var dataset = xmlHttpRequest.responseText;

            datasetInterval = setInterval(function() {
                setFrameDataset(dataset);
            }, 200);

        }
    };

    xmlHttpRequest.open("GET", url, true);
    xmlHttpRequest.send();
}

function setFrameDataset(dataset) {
    var iframe = document.getElementById("viewerFrame");

    //alert(dataset);
    var dataset2 = dataset;

    //  alert(dataset);
    //check the setdataset type that has been implemented
    if (typeof iframe.contentWindow.setDataset == "function") { //json
        //dataset2 += ".json";
        //alert(dataset2);
        iframe.contentWindow.setDataset(dataset2);
        clearInterval(datasetInterval);
    }



    /*
     //check the setdataset type that has been implemented
     if (typeof iframe.contentWindow.setJSONdataset == "function") { //json
     //dataset2 += ".json";
     //alert(dataset2);
     iframe.contentWindow.setJSONdataset(dataset2);
     clearInterval(datasetInterval);
     }
     else if (typeof iframe.contentWindow.setCSVdataset == "function") {
     //dataset2 += ".csv";
     iframe.contentWindow.setCSVdataset(dataset2);
     clearInterval(datasetInterval);
     }
     else if (typeof iframe.contentWindow.setTSVdataset == "function") {
     //dataset2 += ".tsv";
     iframe.contentWindow.setTSVdataset(dataset2);
     clearInterval(datasetInterval);
     }m */
    // }


}
function setFrameNodePositions(nodePositions) {
    var iframe = document.getElementById("viewerFrame"); //set the node positions                        

    if (typeof iframe.contentWindow.setNodePositions == "function") {
        //alert("here");
        getAndSetDataset();


        iframe.contentWindow.setNodePositions(nodePositions);
        clearInterval(nodePosInterval);
    }


    if (!questionsPrepared) { //questions will be prepared only once.
        prepareQuestions();
        questionsPrepared = true;
    }

}


function getAndSetNodePositions() {
    var studyid = document.getElementById("studyid").value;
    //  "&studyid="+studyid;

    var command = "getNodePositions";
    var url = "StudyManager?command=" + command + "&studyid=" + studyid;

    var xmlHttpRequest = getXMLHttpRequest();

    xmlHttpRequest.onreadystatechange = function()
    {
        if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status === 200)
        {
            // alert(xmlHttpRequest.responseText);

            var nodesAndPositions = xmlHttpRequest.responseText.split("::::");
            var nodePositions = [];

            for (var i = 0; i < nodesAndPositions.length; i++) {
                var posValues = nodesAndPositions[i].split("::");
                var name = posValues[0];
                var x = Number(posValues[1]);
                var y = Number(posValues[2]);
                nodePositions[name] = {"x": x, "y": y};
            }
            nodePosInterval = setInterval(function() {
                setFrameNodePositions(nodePositions);
            }, 200);
        }
    };



    xmlHttpRequest.open("GET", url, true);
    xmlHttpRequest.send();
}


function showTheStudy() {
    //hide the introduction div and show the study div.
    document.getElementById("introduction").style.display = "none";
    document.getElementById("standardizedTestDiv").style.display = "none";

    //save the standardized tests if there were any.
    saveTheStandardizedTests();
    document.getElementById("study").style.display = "block";
}

/**
 * Save the standardized tests if there are any
 * @returns {undefined}
 */
function saveTheStandardizedTests() {
    //send the responses to the standardized tests to the server
    if (standardTestsDetails.length > 0) {

        var paramStr;

        for (var i = 0; i < standardTestsDetails.length; i++) {
            var userResp = standardTestsDetails[i].userResponse;
            var userPerf = standardTestsDetails[i].userPerformance;

            paramStr = "&userResponse=" + userResp + "&userPerformance=" + userPerf;
        }





        var studyid = document.getElementById("studyid").value;
        var command = "saveStandardizedTestResponses";
        var url = "StudyManager?command=" + command + paramStr+ "&studyid=" + studyid;

        var xmlHttpRequest = getXMLHttpRequest();

        xmlHttpRequest.onreadystatechange = function()
        {
            if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status === 200)
            {
                alert("saved the standardized tests");
            }
        };
        xmlHttpRequest.open("GET", url, true);
        xmlHttpRequest.send();




    }
}


function getPreQualitativeQuestions() {
    /* if (checkColorBlindess === true && colorBlindnessChecked === false) { //hide the introduction div and show the study div.
     document.getElementById("introduction").style.display = "none";
     showColorBlindnessTest();
     }
     else {//do    */
    //for this current study (i.e. Nov-Dec 2015, I will like to check for color blindness test before the qualitative questions

    var studyid = document.getElementById("studyid").value;


    var command = "getPreQualitativeQuestions";
    var url = "StudyManager?command=" + command + "&studyid=" + studyid;

    var xmlHttpRequest = getXMLHttpRequest();

    xmlHttpRequest.onreadystatechange = function()
    {
        if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status === 200)
        {
            // alert(xmlHttpRequest.responseText);

            if (xmlHttpRequest.responseText === "") {
                showTheStudy();
            }
            else {
                showTheStudy();
                showPreQualitativeQuestions(xmlHttpRequest.responseText);
            }
        }
    };
    xmlHttpRequest.open("GET", url, true);
    xmlHttpRequest.send();
    /*    }*/


}


function showTaskLegend(imgname) {
    if (imgname !== "") {
        var iframeurl = document.getElementById("viewerFrame").src;

        var ind = iframeurl.lastIndexOf("/");
        var pageLocation = iframeurl.substring(0, ind);

        var imgurl = pageLocation + "/" + imgname;
        var imgElement = document.createElement("img");
        imgElement.setAttribute("src", imgurl);
        // imgElement.setAttribute("width", "200px");
        //imgElement.setAttribute("height", "200px");

        var legendDiv = document.getElementById("legendDiv");
        removeDivChildren(legendDiv);
        //Legend

        //create a title for the legend.
        var title = document.createElement("h3");
        title.setAttribute("class", "legendTitle");
        title.innerHTML = "Legend";

        legendDiv.appendChild(title);
        legendDiv.appendChild(imgElement);
        legendDiv.style.display = "block";
    }

}

function checkBrowserSupport() {

    if ((navigator.userAgent.search("Chrome") >= 0
            || navigator.userAgent.search("Firefox") >= 0)) {
        return true;
    }
    return false;
}

function saveMiscellaneousInfo() {
    //I will save the offFocusTime
    //I will save the window width and the window height

    // alert("here2");
    //var offFocusTime = getTotalOffFocusTime();

    var offFocusTime = "notRecorded";

    if (colorBlindnessTestAnswers === "") {
        colorBlindnessTestAnswers = "no color-blind-test";
    }

    //get the window width
    windowWidth = window.innerWidth
            || document.documentElement.clientWidth
            || document.body.clientWidth;

    windowHeight = window.innerHeight
            || document.documentElement.clientHeight
            || document.body.clientHeight;





    //  alert(offFocusTime + " is the offFocus time ");

    var dateAndTime = Date();

    //alert("about to start saving miscellaneous info");

    //alert(dateAndTime);
    var command = "saveMiscellaneousInfo";
    var studyid = document.getElementById("studyid").value;





    var url = "StudyManager?command=" + command
            + "&offFocusTime=" + offFocusTime
            + "&windowWidth=" + windowWidth
            + "&windowHeight=" + windowHeight
            + "&dateAndTime=" + dateAndTime
            + "&colorBlindnessTestAnswers=" + colorBlindnessTestAnswers
            + "&studyid=" + studyid;

    //   alert(url);

    // alert(url);

    var xmlHttpRequest = getXMLHttpRequest();

    xmlHttpRequest.onreadystatechange = function()
    {
        if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status === 200)
        {
            //now get the question method again after saving the miscellaneous information
            getQuestion();
        }
    };

    xmlHttpRequest.open("GET", url, true);
    xmlHttpRequest.send();





}


function afterQuestionChangedNotification() {

    var qnheader = document.getElementById("currentQnHeader").value;
    var question = document.getElementById("currentQuestion").value;
    var maxtime = document.getElementById("currentMaxTime").value;


    document.getElementById("questionChangedNotification").style.display = "none";
    document.getElementById("propControl").style.display = "block";

    // previousQuestion = question;
    //check if the method of the viewer needs the question to be changed. And change it accordingly.
    var iframe = document.getElementById("viewerFrame");
    if (typeof iframe.contentWindow.changeQuestion == "function") {
        var newquestion = iframe.contentWindow.changeQuestion(question.trim());
        if (newquestion !== "") { //if a newquestion was returned that set the question with  the new question
            question = newquestion;
        }
    }

    //get the legend of the question if there is some 
    if (typeof iframe.contentWindow.getQuestionLegend == "function") {
        var imgname = "";
        imgname = iframe.contentWindow.getQuestionLegend(question.trim());
        showTaskLegend(imgname);
    }
    //get the note for the visualization if there is some.
    if (typeof iframe.contentWindow.getVisualizationNote == "function" && !visNoteShown) {
        var note = "";
        note = iframe.contentWindow.getVisualizationNote();
        showVisualizationNote(note);
        visNoteShown = true;
    }

    if (typeof iframe.contentWindow.getVisualizationHint == "function") {
        var hint = "";
        hint = iframe.contentWindow.getVisualizationHint();
        showVisualizationHint(hint);
    }

    maxQuestionDuration = parseInt(maxtime);  // the maximum time for this task
    document.getElementById("noteHeader").innerHTML = qnheader;
    document.getElementById("note").innerHTML = question;

    document.getElementById("selectedAnswer").value = "";

    getInputsAndAnswerControllers();
    //getAnswerControllers();

    //do not show the nodes if the study has not been started, 
    // in that case,they will be shown when the start study button is clicked.
    if (!(isTutorial === "false" && studystarted === false)) {
        getNodes();
    }
    else {
        //deselect the previous nodes 
        deselectPrevNodes();
    }
    //this is also a hack
    // alert(question);
    if (question.trim() === "Remember answers found for previously answered connectivity questions") {
        //  alert("okay got it");
        //we will hide the viewer for this task. //this is a hack.
        showBlankViewer();
    }
    else {
        revealViewer();
    }

    if (studystarted == true) {
        startQuestionDurationCountDown();
    }

}

/**
 * first get the standardized tests, and then get the 
 * pre-study survey questions
 * @returns {undefined}
 */
function getAfterIntroductionActivities() {
    //get the standardized tests

    if (standardTestsDetails.length > 0) {
        // alert("over");
        standardizedTestCounter = 0;
        showStandardizedTests();
    }
    else {
        //we will move on to the pre-study survey questions
    }
}
/**
 * Get the standardized tests from the server
 * @returns {undefined}
 */
function getStandardizedTests() {

    var command = "getStandardizedTests";
    var studyid = document.getElementById("studyid").value;

    var url = "StudyManager?command=" + command + "&studyid=" + studyid;

    var xmlHttpRequest = getXMLHttpRequest();

    xmlHttpRequest.onreadystatechange = function()
    {
        if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status === 200)
        {
            if (xmlHttpRequest.responseText.trim() !== "") {

                var split = xmlHttpRequest.responseText.split("::::");


                for (var i = 0; i < split.length; i++) {

                    var split2 = split[i].split(":::");
                    var stdTest = {"url": split2[0], "userResponseInterface": split2[1],
                        "userPerformanceInterface": split2[2], "userResponse": "",
                        "userPerformance": ""};

                    standardTestsDetails.push(stdTest);
                    //alert("*** " +stdTest.url);
                }



                /*    var gi = document.getElementById("introductionframe");
                 gi.setAttribute("src", xmlHttpRequest.responseText);
                 
                 document.getElementById("defaultIntroduction").style.display = "none";
                 document.getElementById("givenIntroduction").style.display = "block";  */
            }
            else {
                //alert("no standardized tests specified");

                //do what we will do if there is no standardized tests

            }
        }
    };

    xmlHttpRequest.open("GET", url, true);
    xmlHttpRequest.send();
}

/**
 * get the url of the current standardized test and display it.
 * @returns {undefined}
 */
function  showStandardizedTests() {
    var prevUserResp, prevUserPerf;

    //if counter is at least one (i.e. a previous test has been shown),
    //then get the response to the previous standardized test and validate it.
    if (standardizedTestCounter > 0) {
        var respInterface = standardTestsDetails[standardizedTestCounter - 1].userResponseInterface;

        var perfInterface = standardTestsDetails[standardizedTestCounter - 1].userInterface;
        //get the respInterface

        var iframeContentWindow = document.getElementById("standardizedTestFrame").contentWindow;
        //check if the response interface has been implemented and get the value if it has been
        if (typeof iframeContentWindow.window[respInterface] === "function") {

            prevUserResp = iframeContentWindow.window[respInterface]();
            standardTestsDetails[standardizedTestCounter - 1].userResponse = prevUserResp;
        }
        //check if the validation interface has been implemented and get the value if it has. 
        if (typeof iframeContentWindow.window[perfInterface] === "function") {
            prevUserPerf = iframeContentWindow.window[perfInterface]();
            standardTestsDetails[standardizedTestCounter - 1].userPerformance = prevUserPerf;
        }
    }





    //if we've already got the previous response (i.e. for situations     
    if ((prevUserResp && prevUserResp !== false) || (standardizedTestCounter === 0)) {


        //either show the next standardized test button if there are more than one 
        //standardized test, or show the continue to study button.
        if ((standardTestsDetails.length === 1)
                || (standardizedTestCounter === standardTestsDetails.length - 1)) {
            //show the continue to study button

            document.getElementById("lastStandardTest").style.display = "block";
            document.getElementById("notLastStandardTest").style.display = "none";


        }
        else {
            //show the continue to the next standardized test button.
            document.getElementById("lastStandardTest").style.display = "none";
            document.getElementById("notLastStandardTest").style.display = "block";
        }


        //show standardized test if there are more.
        if (standardizedTestCounter < standardTestsDetails.length) {
            var url = standardTestsDetails[standardizedTestCounter].url;
            var gi = document.getElementById("standardizedTestFrame");
            gi.setAttribute("src", url);

            document.getElementById("introduction").style.display = "none";
            document.getElementById("standardizedTestDiv").style.display = "block";

            $('.myIframe').css('height', windowHeight + 'px');
            $('.myIframe').css('width', windowWidth + 'px');

            standardizedTestCounter++;
        }
        else {
            //continue to the study if this is the last standardized test.
            // alert("continue to the study");

            showTheStudy();
        }

    }
    else {
        // the evaluator wants the participant to fix something in the form and resubmit it.
    }













}

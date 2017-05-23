//entry point into the study
window.onload = function() {
    
    var params = decodeURL();
     
    var state = new Object();
    state.user = params["user"];
    state.study  = params["study"];
    state.debug = params["debug"];
    $("#commands")[0].state = state;
    $("#commands")[0].state.results = {results:[]};
    state.viewer = null;
    state.dataset = null;
    
    $("#viewers").hide();

    
    if (checkBrowserSupport() === true){
        getInitStep(state.user, state.study, state.debug, function(stepAndResults){
            $("#commands")[0].state.results = stepAndResults.results;
            $("#commands")[0].state.step = stepAndResults.step;
                       
            processStep(stepAndResults.step);
        });
    }   
    else{
        $("#error").show();
        $("#errorMesssage").html("Please use Chrome or Firefox. We appologize for the inconvenience.");
        $("#error_next").hide();
    }
};

function processStep(step){
     $("#commands")[0].state.step = step;
     
     if (!setSizes(step.width, step.height))
         return;
     
     var state = $("#commands")[0].state;
        if (step.type === "debug")
            doDebug(step, state);
        else if (step.type === "training")
            doTraining(step, state);
        else if (step.type === "task")
            doTask(step, state);
        else if (step.type === "viewerSwitch")
            doViewerSwitch(step,state);
         else if (step.type === "entry" || step.type === "exit")
            doEntryExit(step.type.substring(0,step.type.length-4),step,state);
        else if (step.type === "test")
            doTest(step, state);
        else if (step.type === "intro")
            doIntro(step, state);
        else if (step.type === "thankyou")
            doThankyou(step, state);
    
}

function doStep(){
    var state = $("#commands")[0].state;
    var json = getNextStep(state.user, state.study, processStep);
}

function test(){
    eval("document.getElementById('viewer').contentWindow.sethighlightedPoint(14)");
}


function doDebug(step, state){
    var req = step.resources;
    $(".viewers").hide();
    $("#noViewer").show();
    $(".commandOption").hide();
    $("#error").show();
    $("#errorMessage").html("Please wait while we proof your study...");
    resourcesExist(req, "", 1, function(html){
       if (html.length != 0){
           html = "<div>There seem to be some potential errors in your study:</div>" +  html;                   
           $("#errorMessage").html(html); 
       }
       else
           doStep();
    });
    
    
}

function resourcesExist(resources, html, errCnt, callback){
   var state = $("#commands")[0].state;
   
    if (resources.length == 0){
        callback(html);
        return;
    }
    var res = resources[0];
    resources.splice(0,1);
    if (res.type === "error"){
        html += "<div style='margin-bottom:3px'>" + (errCnt++) + ". Error: " + res.message + "</div>";
        resourcesExist(resources, html, errCnt, callback);
        
    }
    else if (res.type === "resource"){

        if (UrlExists("/visunit/users/" + state.user + "/" + res.url))
            resourcesExist(resources, html, errCnt, callback);
        else{
            html += "<div style='margin-bottom:3px'>" + (errCnt++) + ". Missing resource: " + res.url + "</div>";
            resourcesExist(resources, html, errCnt, callback);
        }
    }
    else{
        //this is a function; first, load the url in the viewer frame
        var iframe = $("#viewer");    
        iframe[0].onload = function(){
            typeof iframe[0].contentWindow.clearViewer == "function"
            var cmd = "document.getElementById('viewer').contentWindow." + res.name; 
            try{
                if (typeof eval(cmd) === "function")
                    resourcesExist(resources, html, errCnt, callback);
                else{
                    html += "<div style='margin-bottom:3px'>" + (errCnt++) + ". Missing  function: " + res.name + " in " + res.url + "</div>";
                    resourcesExist(resources, html, errCnt, callback);
                }
            }catch(err){
                alert("couldn't check for function: " + cmd);
                html += "<div style='margin-bottom:3px'>" + (errCnt++) + ". Missing function: " + res.name + " in " + res.url + "</div>";
                resourcesExist(resources, html, errCnt, callback);
            }
        };
        iframe.attr("src", "/visunit/users/" + state.user + "/" + res.url);
    }
};

function UrlExists(url)
{
    var http = new XMLHttpRequest();
    http.open('HEAD', url, false);
    http.send();
    return http.status!=404;
}


//////////////////////////////  Do Intro //////////////////////////////
function doIntro(step, state){
    $(".viewers").hide();
    $(".commandOption").hide();
    
    $("#intro").show();
    $("#intro_frame").show();
    $("#intro_frame").attr("src","/visunit/users/" + state.user + "/" + step.intro.source);
}

function doThankyou(step,state){
    $(".viewers").hide();
    $(".commandOption").hide();
    
    $("#thankyou").show();
    if (step.thankyou === null)
        $("#thankyoudefault_frame").show();
    else{
        $("#thankyou_frame").show();
        $("#thankyou_frame").attr("src","/visunit/users/" + state.user + "/" + step.intro.source);
    }
}


////////////////////////////// Viewer changes ////////////////////////////
function doViewerSwitch(step, state){
    $(".viewers").hide();
    $(".commandOption").hide();
    
    
    $("#changeViewer").show();
    $("#changeViewer_introduction").hide();
    $("#changeViewer_training").hide();
    
    
    if (step.training === "yes")
        $("#changeViewer_training").show();
    if (step.viewer.introduction !== null && step.viewer.introduction.trim().length > 0){
        $("#changeViewer_frame").show();
        $("#changeViewer_introduction").show();
        $("#changeViewer_frame").attr("src","/visunit/users/" + state.user + "/" + step.viewer.introduction);
    }
    else
        $("#noViewer").show();
}




////////////////////////////// Viewer changes ////////////////////////////
function doTest(step, state){
    $("#commands")[0].state.startTime = (new Date()).getTime();
    $(".viewers").hide();
    $(".commandOption").hide();
    
    $("#test").show();
    $("#test_frame").show();
    $("#test_frame").attr("src","/visunit/users/" + state.user + "/" + step.test.source);
}

function afterTest(){
    $("#test_frame").hide();
    
    var cutoff = $("#commands")[0].state.step.cutoff;
    var group = $("#commands")[0].state.step.group;
    
    var response = {response:null,accuracy:-1};
    try{
        var ret = eval("document.getElementById('test_frame').contentWindow.getResponse()");
        response.response = ret;
    }catch(ee){};
    try{
        var ret = eval("document.getElementById('test_frame').contentWindow.getAccuracy()");
        response.accuracy = ret;
    }catch(ee){};
    
    var time = (new Date()).getTime() - $("#commands")[0].state.startTime;
    saveTestResult($("#commands")[0].state.step.test.name,  group, response, time);
    
    if (response.accuracy !== null && response.accuracy >= 0 && response.accuracy < cutoff){
        doSorry();
    }
    else
        doStep();
}

function saveTestResult(testname, group, response, time){
    
    $("#commands")[0].state.results.results.push({type:"test"+testname, 'group': group, 'task':null,
        'taskInstance':null,'response':response.response, 'accuracy':response.accuracy, 'time':time});
    
    
}

////////////////////////// SORRY ////////////////////////////////
function doSorry(){
    //jump to the last step in the script
     getLastStep($("#commands")[0].state.user, $("#commands")[0].state.study, function(step){
         processStep(step);
     });
}


/////////////////////////////////////// TRAINING STUFF ////////////////////////

function doTraining(step, state){
   
    //check below it on how it's called
    window.dataLoaded = function(){               
        $(".commandOption").hide();
        $("#training").show();
        
        $(".viewers").hide();
        $("#viewer").show();

        $("#training_question").html(step.task.question);

        for (var i=0; i<step.task.visSetup.length; i++)
          eval("document.getElementById('viewer').contentWindow." + step.task.visSetup[i]);

        $(".answerWidget").hide();
        if (step.task.answer[0].startsWith("vis")){
             var func = step.task.answer[0].substring(7);
             var type = func.substring(0,func.indexOf("("));
             try{
                var help = eval("document.getElementById('viewer').contentWindow.help" + type + "()");
                $("#training_answerInterface").html(help);
                $("#training_answerInterface").show();
             }catch(err){};
        }
        else if (step.task.answer[0].startsWith("gui")){ //this will require a gui response
            //get the type
            var type = step.task.answer[0].substring(7);
            if (type.startsWith("Number")){
                $("#training_answerNumber").val(0);
                $("#training_answerNumber").show();
            }
            else if (type.startsWith("Text")){
                $("#training_answerText").val(0);
                $("#training_answerText").show();            
            }
            else if (type.startsWith("Options")){
                $("#training_answerOption").show();
                $("#training_answerOption").html("");
                var options = type;
                if (type.startsWith("Options(dynamic)"))
                    options = type.substring(17, type.length-1);
                else
                    options = type.substring(15, type.length-1);
                    var split = options.split("|,|");
                    
                    $("#task_answerOption").html("");
                    for (var i=0; i<split.length; i++)
                        $("#training_answerOption").append($("<option value='" + split[i] + "'>" + split[i] + "</option>"));
                    
            }
        }
        if (step.task.answer.length == 3){ //has correct answer
            $("#training_noCorrectAnswer").hide();
            $("#training_checkAnswer").show();
        }
        else{
            $("#training_noCorrectAnswer").show();
            $("#training_checkAnswer").hide();
        }
    };
    
    if (state.viewer !== step.viewer || state.dataset !== step.dataset)
        loadViewer(step.viewer, step.dataset);
    else{
        clearViewer();
        f();
    }
}

function afterTraining(){ //what happens when user presses next in training mode
    var step = $("#commands")[0].state.step;
    
    var response = getResponse("training", step);
    if (step.task.answer.length ==3){
        var accuracy = getAccuracy(step, response);

        if (accuracy == 0) alert("Not the correct answer. Try again.");
        else   doStep();
    }
    else doStep();
}

function checkTrainingAnswer(){
    var step = $("#commands")[0].state.step;
    
    var response = getResponse("training",step);
    var accuracy = getAccuracy(step, response);
    
    if (accuracy == 0) 
        alert("incorrect");
    else alert ("correct");
}


/////////////////////////////// TASKS //////////////////////////////////////

function doTask(step, state){
   
    //check below it on how it's called
    window.dataLoaded = function(){ 
        
        $(".viewers").hide();
        $("#viewer").show();

         
        var step = $("#commands")[0].state.step;
        $("#commands")[0].state.startTime = (new Date()).getTime();
        
        var interval = setInterval(function(){
            var step = $("#commands")[0].state.step;
            var maxTime = parseInt(step.task.maxTime)*1000;
            var elapsed = (new Date()).getTime() - $("#commands")[0].state.startTime;
            var remaining = maxTime - elapsed;
            if (remaining < 0) remaining = 0;
            
            $("#task_time").html((remaining/1000) + "sec");
            
            if (remaining <= 0){ //hide visualization
                $("#viewer").hide();
                $("#timeoutViewer").show();
            }
        }, 100);        
        $("#commands")[0].state.interval = interval;
        
        $(".commandOption").hide();
        $("#task").show();

        $("#task_question").html(step.task.question);

        for (var i=0; i<step.task.visSetup.length; i++)
          eval("document.getElementById('viewer').contentWindow." + step.task.visSetup[i]);

        $(".answerWidget").hide();
         if (step.task.answer[0].startsWith("vis")){
             var func = step.task.answer[0].substring(7);
             var type = func.substring(0,func.indexOf("("));
             try{
                var help = eval("document.getElementById('viewer').contentWindow.help" + type + "()");
                $("#task_answerInterface").html(help);
                $("#task_answerInterface").show();
            }catch(err){};
         }
        if (step.task.answer[0].startsWith("gui")){ //this will require a gui response
            //get the type
            var type = step.task.answer[0].substring(7);
            if (type.startsWith("Number")){
                $("#task_answerNumber").val(0);
                $("#task_answerNumber").show();
            }
            else if (type.startsWith("Text")){
                $("#task_answerText").val(0);
                $("#task_answerText").show();            
            }
            else if (type.startsWith("Options")){
                $("#task_answerOption").show();
                $("#task_answerOption").html("");
                var options = type;
                if (type.startsWith("Options(dynamic)"))
                    options = type.substring(17, type.length-1);
                else
                    options = type.substring(15, type.length-1);
                    var split = options.split("|,|");
                    
                    $("#task_answerOption").html("");
                    for (var i=0; i<split.length; i++)
                        $("#task_answerOption").append($("<option value='" + split[i] + "'>" + split[i] + "</option>"));
            }
        }
    };
    
    if (state.viewer !== step.viewer || state.dataset !== step.dataset)
        loadViewer(step.viewer, step.dataset);
    else{
        clearViewer();
        window.dataLoaded();
    }
}

function afterTask(){ //what happens when user presses next after a task mode
    clearInterval($("#commands")[0].state.interval);
    var step = $("#commands")[0].state.step;
    var startTime = $("#commands")[0].state.startTime;
    
    var response = getResponse("task", step);
    var accuracy = -1;
    
    if (step.task.answer.length ==3)
        accuracy = getAccuracy(step, response);
    
    var time = (new Date()).getTime() - startTime;
   
    
    saveTaskInstanceAnswer("task", response, accuracy, time, 
    step.task.task, step.task.taskInstance, step.group, step.viewer.name, step.dataset.name);
    
    //cleaer the interval
    clearInterval($("#commands")[0].state.interval);
    $("timeoutViewer").hide();
    $("viewer").show();
    doStep();
}

function saveTaskInstanceAnswer(tpe, rsponse, accracy, tme, tsk, tskInstance, grp, view, data){
    //alert("saving result: " + view + " " + data + " " + tpe + " " + tsk + " " + rsponse + " " + accracy + " " + tme);
    $("#commands")[0].state.results.results.push({
        type:tpe,
        task: tsk,
        taskInstance: tskInstance,
        response: rsponse,
        accuracy: accracy,
        time: tme,
        viewer: view,
        dataset: data,
        group: grp
    });
}


///////////////////////////////  ENTRY and exit TASKS //////////////////////////////////////

function doEntryExit(which, step, state){
   
   
   $("#commands")[0].state.startTime = (new Date()).getTime();
    $(".viewers").hide();
    $("#noViewer").show();
    
    $("#entryHeader").hide();
    $("#exitHeader").hide();
    $("#"+which+"Header").show();
    
    var step = $("#commands")[0].state.step;
    
    $(".commandOption").hide();
    $("#entryExit").show();

    $("#entryExit_question").html(step.task.question);

    $(".answerWidget").hide();
    if (step.task.answer[0].startsWith("gui")){ //this will require a gui response
            //get the type
        var type = step.task.answer[0].substring(7);
        if (type.startsWith("Number")){
            $("#entryExit_answerNumber").val(0);
            $("#entryExit_answerNumber").show();
        }
        else if (type.startsWith("Text")){
            $("#entryExit_answerText").val(0);
            $("#entryExit_answerText").show();            
        }
        else if (type.startsWith("Options")){
            $("#entryExit_answerOption").show();
            $("#entryExit_answerOption").html("");
            var options = type;
            if (type.startsWith("Options(dynamic)"))
                options = type.substring(17, type.length-1);
            else
                options = type.substring(15, type.length-1);
            var split = options.split("|,|");

                $("#entryExit_answerOption").html("");
                for (var i=0; i<split.length; i++)
                    $("#entryExit_answerOption").append($("<option value='" + split[i] + "'>" + split[i] + "</option>"));
        }
    }
}

function afterEntryExit(){ //what happens when user presses next after a task mode

    var step = $("#commands")[0].state.step;
    
    var response = getResponse("entryExit", step);
   
    var time = (new Date()).getTime() - $("#commands")[0].state.startTime;
    
    var accuracy = -1; 
    if (step.task.answer.length ==3)
        accuracy = getAccuracy(step, response);
     
    saveTaskInstanceAnswer($("#commands")[0].state.step.type, response, accuracy, time, 
    step.task.task, step.task.taskInstance, $("#commands")[0].state.step.group, null, null);
    
    doStep();
}


///////////Other
function getResponse(trainingOrTask, step){
    if (step.task.answer[0].startsWith("gui")){
        var type = step.task.answer[0].substring(7);
        if (type.startsWith("Number"))
            return $("#" + trainingOrTask + "_answerNumber").val();
        else if (type.startsWith("Text"))
            $("#" + trainingOrTask + "_answerText").val();
        else if (type.startsWith("Options")){
            //alert("get value of: " + "#" + trainingOrTask + "_answerOption");
            return $("#" + trainingOrTask + "_answerOption").val();}
    }
    else{
        var func = step.task.answer[0].substring(4);
        var ret = eval("document.getElementById('viewer').contentWindow." + func);
        return ret;
    }
}

function getAccuracy(step, response){
    var correctAnswer = step.task.answer[2];
    if (step.task.answer[0].startsWith("gui")){
        var type = step.task.answer[0].substring(7);
        //alert("get accuracy gui type: " + type);
        if (type.startsWith("Number"))
            return getNumberAccuracy(response, correctAnswer);
        else if (type.startsWith("Text"))
            return getTextAccuracy(response, correctAnswer);
        else if (type.startsWith("Options"))
            return getOptionAccuracy(response, correctAnswer);
    }
    else{
        var func = step.task.answer[1].substring(4);
        var ev = "document.getElementById('viewer').contentWindow." + func + "('" + response + "','" + correctAnswer + "')";
        var ret = (response === correctAnswer ? 1 : 0);
        try{
           // alert("eval: " + ev);
            ret = eval(ev);
        }catch(err){alert("Checking accuracy error: " + err.message);};
        return ret;
    }
}

function getNumberAccuracy(v1, v2){
    if (v1 != v2) return 0;
    return 1;
}

function getTextAccuracy(v1, v2){
    if (v1 !== v2) return 0;
    return 1;
}

function getOptionAccuracy(v1, v2){
    if (v1 !== v2) return 0;
    return 1;
}


function loadViewer(viewer, data){
    var state = $("#commands")[0].state;
    var viewerURL = "/visunit/users/" + state.user + "/" + viewer.source;
    var dataURL = "/visunit/users/" + state.user + "/" + data.source;
   
    var step = $("#commands")[0].state.step;
    var iframe = $("#viewer");    
    iframe[0].onload = function(){
        var cmd = "document.getElementById('viewer').contentWindow.setDataset('" + 
                dataURL + "', function(){ parent.dataLoaded();} )";
        eval(cmd);
    };
    iframe.attr("src", viewerURL);
}

function clearViewer(){
    var iframe = $("#viewer");    
    if (typeof iframe[0].contentWindow.clearViewer == "function")
        iframe.contentWindow.clearViewer();    
}

function checkBrowserSupport() {

    if ((window.navigator.userAgent.search("Chrome") >= 0
            || window.navigator.userAgent.search("Firefox") >= 0)) {
        return true;
    }
    return false;
}

function getNextStep(user, study, whendone) {
    var theURL = "StudyManager_1?user=" + user  + "&study=" + study + "&command=step";
    $.ajax({
       url: theURL,
        type: 'POST',
        data: {results: JSON.stringify($("#commands")[0].state.results)},
        dataType: 'json',
        success: function(data, status) {
            whendone(data);
        },
        error: function(data, status) {
            //handle error
            alert("there was an error when loading the study files__" + status);
        }
    });
}

function getInitStep(user, study, debug, whendone) {
    var theURL = "StudyManager_1?user=" + user  + "&study=" + study + "&command=init";
    if (debug === "true") theURL += "&debug=true";
    $.ajax({
       url: theURL,
        success: function(stepAndResults, status) {
            whendone(stepAndResults);
        },
        error: function(data, status) {
            //handle error
            alert("there was an error when loading the study files__" + status);
        }
    });
}

function getLastStep(user, study, whendone){
    var theURL = "StudyManager_1?user=" + user  + "&study=" + study + "&command=finish";
    $.ajax({
       url: theURL,
        success: function(stepAndResults, status) {
            whendone(stepAndResults);
        },
        error: function(data, status) {
            //handle error
            alert("there was an error when loading the study files__" + status);
        }
    });   
}

function setSizes(width, height){
  //  alert(parseInt(height) + "," + window.innerHeight);
    $(".viewers").css("width",width + "px").css("height", height + "px");
    if (parseInt(height) > window.innerHeight-20 || parseInt(width)+200 > window.innerWidth-20){
        $(".viewers").hide();
        $("#noViewer").show();
        
        $(".commandOption").hide();
        $("#error").show();
        
        $("#errorMessage").html("Please enlarge or maximize your browser. To do this study its content needs"
                                                 + " to fit in your browser without scrolling");
        $("#error_next")[0].onclick = (function(){processStep($("#commands")[0].state.step);});
        
        return false;
    }
    else return true;
}

function decodeURL() {
  // This function is anonymous, is executed immediately and 
  // the return value is assigned to QueryString!
  var query_string = {};
  var query = window.location.search.substring(1);
  var vars = query.split("&");
  for (var i=0;i<vars.length;i++) {
    var pair = vars[i].split("=");
        // If first entry with this name
    if (typeof query_string[pair[0]] === "undefined") {
      query_string[pair[0]] = decodeURIComponent(pair[1]);
        // If second entry with this name
    } else if (typeof query_string[pair[0]] === "string") {
      var arr = [ query_string[pair[0]],decodeURIComponent(pair[1]) ];
      query_string[pair[0]] = arr;
        // If third or later entry with this name
    } else {
      query_string[pair[0]].push(decodeURIComponent(pair[1]));
    }
  } 
  return query_string;
}





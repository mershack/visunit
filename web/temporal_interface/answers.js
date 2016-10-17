/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


function setUpAnswerControllers(answerType, answerOptions, answerDiv, ansLabel) {

    removeDivChildren(answerDiv);

    // alert(answer.type);

    if (answerType.toLowerCase() === "options(fixed)" || answerType.toLowerCase() === "options(dynamic)") {
        if (answerOptions)
            createAnswerOption(answerOptions, answerDiv, ansLabel);
    }
    else if (answerType.toLowerCase() === "number") {
        //create a numeric input 
        createNumberInput(answerDiv, ansLabel);
    }
    else if (answerType.toLowerCase() === "text") {
        createStringInput();
    }
    else if (answerType.toLowerCase() === "interface") {
        //alert("this is an interface type");
        //we will not do anything now 
    }
    else if (answerType.toLowerCase() === "color-options(fixed)") {
//        var split2 = split[1].split("::");
//
//        for (var i = 0; i < split2.length; i++)
//            createColorOption(split2[i], i);
    }


    /* else if (dataType === "inputOptions") {
     //the inputOptions should already be set by now.
     setUpInputOptionsAnswerController();
     }*/
    //show the answer div
    document.getElementById("answersDiv").style.display = "block";
}

function createNumberInput(answerDiv, ansLabel) {
    // alert("I'm going to create the integer controller");
    //create a numeric input that accepts numeric numbers 
    var input = document.createElement("input");
    input.setAttribute("type", "number");
    input.setAttribute("name", "answer");
    input.setAttribute("min", "0");
    input.setAttribute("onKeyUp", "setProvidedAnswer(this)");
    input.setAttribute("oninput", "setProvidedAnswer(this)");  //this will be triggered when the spinner control is used


    //create label
    var label = document.createElement("label");
    //label.innerHTML = "Your Answer : ";
    label.innerHTML = ansLabel;

    //create a paragraph
    var paragraph = document.createElement("p");
    //append the inputBox and label to the paragraph
    paragraph.appendChild(label);
    paragraph.appendChild(input);
    var form = document.createElement("form");
    form.setAttribute("onsubmit", "return false;");
    form.appendChild(paragraph);
    //var answerDiv = document.getElementById("answersDiv");
    answerDiv.appendChild(form);

    input.focus();

}

function setProvidedAnswer(element) {
    document.getElementById("providedAnswer").value = element.value;
}


function  createAnswerOption(options, answerDiv, ansLabel) {

    var paragraph = document.createElement("p");
    paragraph.innerHTML = ansLabel;
    answerDiv.appendChild(paragraph);

    for (var i = 0; i < options.length; i++) {

        if (options[i] !== "") { //not empty
            //createRadio button
            var radio = document.createElement("input");
            radio.setAttribute("type", "radio");
            radio.setAttribute("name", "answer");
            radio.setAttribute("value", options[i]);
            radio.setAttribute("onclick", "setProvidedAnswer(this)");
            //create label
            var label = document.createElement("label");
            label.innerHTML = options[i];

            //create a paragraph
            var paragraph = document.createElement("p");

            //append the radio and label to the paragraph
            paragraph.appendChild(radio);
            paragraph.appendChild(label);

            answerDiv.appendChild(paragraph);

        }

    }


}
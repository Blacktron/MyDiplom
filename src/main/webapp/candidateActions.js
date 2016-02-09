// =================================== Add Candidate ===================================
var techs = null;
/*
 Function which adds new Candidate into the database.
 */
function addCandidate() {
    var candidateFirstName = $('#candidate-first-name').val();
    var candidateLastName = $('#candidate-last-name').val();
    var age = $('#candidate-age').val();
    var candidateEmail = $('#candidate-email').val();

    var candidate = {
        candidateFirstName: candidateFirstName,
        candidateLastName: candidateLastName,
        age: age,
        candidateEmail: candidateEmail,
        experience: []
    };

    // Get the data from the Technology and Years fields.
    var expArray = $('#experience-list').children();

    $.each(expArray, function (index, element) {
        var technologyId = $(element).find('#technology-container').val();
        var years = $(element).find('#years').val();
        candidate.experience.push({"technology": technologyId, "years": years});
    });

    console.log("New Candidate: " + JSON.stringify(candidate));

    $.ajax({
        method: 'POST',
        url: '/rest/candidates',
        dataType: 'json',
        data: JSON.stringify(candidate),
        contentType: "application/json; charset=utf-8",
        success: function () {
            alert("Operation completed successfully");
        },
        error: function () {
            alert("Something broke");
        }
    });
}

/*
 Function which populates the Technology drop down with Technologies.
 */
function populateTechsOn(element) {
    $.each(techs, function(i, option) {
        element.append(
            $('<option />').text(option.technologyName).val(option.id)
        );
        //$('.technology-container').last().val() // Get the id of the technology from the 'value' attribute.
    });
}

// =================================== Search Candidate ===================================

function searchCandidate() {
    var candidateID = $('#candidate-ID').val();
    var candidateFirstName = $('#candidate-first-name').val();
    var candidateLastName = $('#candidate-last-name').val();
    var age = $('#candidate-age').val();
    var email = $('#candidate-email').val();
    var technology = $('#candidate-technology').val();

    var url = "/rest/candidates";

    if (candidateID !== "") {
        if (candidateID > 0 && candidateFirstName === "" && candidateLastName === "" && age === "" && email === "" && technology === "") {
            url += "/" + candidateID;
        } else if (candidateID <= 0) {
            alert("Candidate ID must be > 0");
        } else {
            url += "?candidateId=" + candidateID;
        }
    }

    if (candidateFirstName !== "") {
        url += checkIfFirstParameterInURL(url) + "candidateFirstName=" + candidateFirstName;
    }

    if (candidateLastName !== "") {
        url += checkIfFirstParameterInURL(url) + "candidateLastName=" + candidateLastName;
    }

    if (age !== "") {
        if (age > 0) {
            url += checkIfFirstParameterInURL(url) + "age=" + age;
        } else if (age <= 0) {
            alert("Age must be > 0");
        }
    }

    if (email !== "") {
        url += checkIfFirstParameterInURL(url)  + "email=" + email;
    }

    if (technology !== "") {
        url += checkIfFirstParameterInURL(url) + "technology=" + technology;
    }

    console.log(url);
}

function checkIfFirstParameterInURL(url) {
    var character;
    if (url.indexOf("?") >= 0) {
        character = "&";
    } else {
        character = "?";
    }

    return character;
}
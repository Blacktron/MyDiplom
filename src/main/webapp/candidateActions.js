var techs = null;
var positions = null;
var searchResult = null;

// =================================== Add Candidate ===================================

/**
 * Function which adds new Candidate into the database.
 */
function addCandidate() {
    var $candidateFirstName = $('#candidate-first-name').val();
    var $candidateLastName = $('#candidate-last-name').val();
    var $age = $('#candidate-age').val();
    var $candidateEmail = $('#candidate-email').val();

    var candidate = {
        candidateFirstName: $candidateFirstName,
        candidateLastName: $candidateLastName,
        age: $age,
        candidateEmail: $candidateEmail,
        experience: [],
        applications: []
    };

    // Get the data from the Technology and Years fields.
    var $expArray = $('#experience-list').children();

    $.each($expArray, function (index, element) {
        var $technologyId = $(element).find('.technology-container').val();
        var $years = $(element).find('.years').val();
        candidate.experience.push({"technologyId": $technologyId, "years": $years});
    });

    // Get the data from the Application field.
    var $appArray = $('#applications-list').children();

    $.each($appArray, function (index, element) {
        var $positionId = $(element).find('.position-container').val();
        candidate.applications.push({"positionId": $positionId});
    });

    //console.log("New Candidate: " + JSON.stringify(candidate));

    // Check if all data is present and execute the ajax call.
    var isExecutableAjax = false;

    if ($candidateFirstName !== "" && $candidateFirstName !== undefined
        && $candidateLastName != "" && $candidateLastName != undefined
        && $age != "" && $age != undefined
        && $candidateEmail != "" && $candidateEmail != undefined
        && $expArray.length > 0 && $appArray.length > 0) {
        isExecutableAjax = true;
    }

    // Execute the ajax call.
    if (isExecutableAjax) {
        $.ajax({
            method: 'POST',
            url: '/rest/candidates',
            dataType: 'json',
            data: JSON.stringify(candidate),
            contentType: "application/json; charset=utf-8",
            success: function (data) {
                alert(data.Status);
            },
            error: function (data) {
                console.log(data);
                alert(data.responseText);
            }
        });
    }
}

/**
 * Function which populates the Technology drop down with Technologies.
 */
function populateTechsOn(element) {
    $.each(techs, function(i, option) {
        element.append(
            $('<option />').text(option.technologyName).val(option.id)
        );
        //$('.technology-container').last().val() // Get the id of the technology from the 'value' attribute.
    });
}

/**
 * Function which populates the Position drop down with Positions.
 */
function populatePositionsOn(element) {
    $.each(positions, function(i, option) {
        element.append(
            $('<option />').text(option.positionName + "-" + option.companyName).val(option.id)
        );
    });
}

// =================================== Search Candidate ===================================

/**
 * Gets the data from the search form and executes the ajax call to the server.
 */
function searchCandidate() {
    var $candidateID = $('#candidate-ID').val();
    var $candidateFirstName = $('#candidate-first-name').val();
    var $candidateLastName = $('#candidate-last-name').val();
    var $age = $('#candidate-age').val();
    var $email = $('#candidate-email').val();
    var $technology = $('#candidate-technology').val();
    var $years = $('#candidate-years').val();

    var searchUrl = "/rest/candidates";
    var isInformationMissing = false;

    if ($candidateID !== "" && $candidateID !== undefined) {
        isInformationMissing = false;
        if ($candidateID > 0 && $candidateFirstName === "" && $candidateLastName === "" && $age === "" && $email === "" && $technology === "") {
            searchUrl += "/" + $candidateID;
        } else if ($candidateID <= 0) {
            alert("Candidate ID must be > 0");
            isInformationMissing = true;
        } else {
            searchUrl += "?candidate.candidateId=" + $candidateID;
        }
    }

    if ($candidateFirstName !== "" && $candidateFirstName !== undefined) {
        searchUrl += checkIfFirstParameterInURL(searchUrl) + "candidateFirstName=" + $candidateFirstName;
    }

    if ($candidateLastName !== "" && $candidateLastName !== undefined) {
        searchUrl += checkIfFirstParameterInURL(searchUrl) + "candidateLastName=" + $candidateLastName;
    }

    if ($age !== "" && $age !== undefined) {
        if ($age > 0) {
            searchUrl += checkIfFirstParameterInURL(url) + "age=" + $age;
            isInformationMissing = false;
        } else if ($age <= 0) {
            alert("Age must be > 0");
            isInformationMissing = true;
        }
    }

    if ($email !== "" && $email !== undefined) {
        searchUrl += checkIfFirstParameterInURL(searchUrl)  + "candidateEmail=" + $email;
    }

    if ($technology !== "" && $technology !== undefined) {
        searchUrl += checkIfFirstParameterInURL(searchUrl) + "technologyName=" + $technology;
    }

    if ($years !== "" && $years !== undefined) {
        if ($years > 0) {
            searchUrl += checkIfFirstParameterInURL(searchUrl) + "years=" + $years;
            isInformationMissing = false;
        } else if ($years <= 0) {
            alert("Years must be > 0");
            isInformationMissing = true;
        }
    }

    //console.log(searchUrl);

    if (!isInformationMissing) {
        $.ajax({
            method: 'GET',
            url: searchUrl,
            success: function(data) {
                searchResult = data;
                displaySearchResult();
            },
            error: function(data) {
                alert(data.responseText);
            }
        });
    }
}

/**
 * Show the result of the search on the page.
 */
function displaySearchResult() {
    //console.log(searchResult.length);
    //console.log(searchResult);
    if (searchResult.length > 0) {
        $('#search-result-table').find('tr:gt(0)').remove();
        $.each(searchResult, function(i, candidateObject) {
            var candidateId = candidateObject.id;
            var candidateFirstName = candidateObject.candidateFirstName;
            var candidateLastName = candidateObject.candidateLastName;
            var age = candidateObject.age;
            var email = candidateObject.candidateEmail;
            var experience = candidateObject.experiences;
            var applications = candidateObject.applications;
            var row = "<tr><td class='candidateId'>" + candidateId + "</td><td class='candidateFirstName'>"
                + candidateFirstName + "</td><td class='candidateLastName'>" + candidateLastName
                + "</td><td class='candidateAge'>" + age + "</td><td class='candidateEmail'>" + email + "</td>";

            var candidateExperience = "";
            $.each(experience, function(i, experienceObject) {
                if (i < experience.length - 1) {
                    candidateExperience += experienceObject.technologyName + "-" + experienceObject.years + ", ";
                } else if (i == experience.length - 1) {
                    candidateExperience += experienceObject.technologyName + "-" + experienceObject.years;
                }
            });
            row += "<td class='candidateExperience'>" + candidateExperience + "</td>";

            var candidateApplication = "";
            $.each(applications, function(i, applicationObject) {
                if (i < applications.length - 1) {
                    candidateApplication += applicationObject.positionName + "-" + applicationObject.companyName + ", ";
                } else if (i == applications.length - 1) {
                    candidateApplication += applicationObject.positionName + "-" + applicationObject.companyName;
                }
            });
            row += "<td class='candidateApplication'>" + candidateApplication + "</td>";

            // Add Edit and Delete buttons.
            row += "<td><button type='button' class='btn btn-primary edit-candidate' data-toggle='modal' data-target='#candidateModal'>Edit</button></td>";
            row += "<td><button type='button' class='btn btn-danger delete-candidate'>Delete</button></td></tr>";

            $('#search-result-table').find('tbody').append(row);
        });
    }
}

/**
 * Function which checks if the current parameter is the first one in the URL or not
 * and decides which symbol to put.
 */
function checkIfFirstParameterInURL(url) {
    var character;
    if (url.indexOf("?") >= 0) {
        character = "&";
    } else {
        character = "?";
    }

    return character;
}

// =================================== Delete Candidate ===================================

/**
 * Delete a Candidate on Delete button click.
 */
// 'button[type="button"]'
$('.table').on('click', '.delete-candidate', function(e) {
    var $id = $(this).closest('tr').find('.candidateId').text();
    var deleteUrl = '/rest/candidates/' + $id;

    $.ajax({
        method: 'DELETE',
        url: deleteUrl,
        success: function (data) {
            console.log(data);
            alert(data.Status);
        },
        error: function (data) {
            alert(data);
        }
    });

    $(this).closest('tr').remove();
});

// =================================== Edit Candidate ===================================

/**
 * Get the Candidate details and put them in the dialog on Edit button click.
 * On Save Changes button click the ajax call is prepared and executed in order
 * to make the modifications in the database.
 */
$('.table').on('click', '.edit-candidate', function(e) {

    // Get the data of the edited Candidate from the table row.
    var $currentCandidate = $(this).closest('tr');
    var $candidateId = $currentCandidate.find('.candidateId').text();
    var $candidateFirstName = $currentCandidate.find('.candidateFirstName').text();
    var $candidateLastName = $currentCandidate.find('.candidateLastName').text();
    var $age = $currentCandidate.find('.candidateAge').text();
    var $candidateEmail = $currentCandidate.find('.candidateEmail').text();
    var $candidateExperience = $currentCandidate.find('.candidateExperience').text();
    var $candidateApplication = $currentCandidate.find('.candidateApplication').text();

    // Put the data in the dialog form fields.
    $('#dialog-candidate-ID').val($candidateId);
    $('#dialog-candidate-first-name').val($candidateFirstName);
    $('#dialog-candidate-last-name').val($candidateLastName);
    $('#dialog-candidate-age').val($age);
    $('#dialog-candidate-email').val($candidateEmail);

    var editUrl = '/rest/candidates/' + $candidateId;
    var operations = [];
    var newCandidate = {
        addExperience: [],
        removeExperience: [],
        addApplication: [],
        removeApplication: []
    };

    // Fill the Experience table in the dialog.
    var dialogExpArray = $candidateExperience.split(",");
    $('#experience-table').find('tr:gt(0)').remove();
    $.each(dialogExpArray, function(index, entry) {
        var exp = entry.split("-");
        var dialogRow = "<tr><td class='dialog-experience-field'>" + exp[0] + "</td><td>" + exp[1] + "</td><td><button type='button' class='btn btn-danger' id='dialog-experience-remove-button'>X</button></td></tr>";
        $('#experience-table').find('tbody').append(dialogRow);
    });

    // Fill the Application table in the dialog.
    var dialogAppArray = $candidateApplication.split(",");
    $('#application-table').find('tr:gt(0)').remove();
    $.each(dialogAppArray, function(index, entry) {
        var application = entry.split("-");
        var dialogRow = "<tr><td class='dialog-application-field'>" + application[0] + "</td><td>" + application[1] + "</td><td><button type='button' class='btn btn-danger' id='dialog-application-remove-button'>X</button></td></tr>";
        $('#application-table').find('tbody').append(dialogRow);
    });

    /**
     * Display new Technology and Years fields on button click in the dialog.
     * Used to add Candidate's years of experience with a Technology.
     */
    $('#dialog-add-experience-button').on('click', function() {
        // Add new row
        $.get("/candidate/addExperience.html", function(data) {
            $('.dialog-experience-list').append(data);

            // Populate the Technology select box.
            populateTechsOn($('.technology-container').last());
        });
    });

    /**
     * Display new Position field on button click in the dialog.
     * Used to add Candidate's application on a Position.
     */
    $('#dialog-add-application-button').on('click', function() {
        // Add new row
        $.get("/candidate/addApplication.html", function(data) {
            $('.dialog-application-list').append(data);

            // Populate the Position select box.
            populatePositionsOn($('.position-container').last());
        });
    });

    $('#close-candidate-changes-button').click(function() {
        $('#dialog-add-experience-button').unbind('click');
        $('#dialog-add-application-button').unbind('click');
    });

    $('#save-candidate-changes-button').click(function() {
        $('#dialog-add-experience-button').unbind('click');
        $('#dialog-add-application-button').unbind('click');
    });

    // On remove an Experience from the dialog, the object is prepared.
    $('#experience-table').on('click', '#dialog-experience-remove-button', function(e) {
        var $removeTechnologyName = $(this).closest('tr').find('.dialog-experience-field').text();
        $removeTechnologyName = $removeTechnologyName.trim();

        // Check if the operation is already added to the array of actions.
        var removeExpFound = $.inArray("removeExperience", operations) > -1;

        if (!removeExpFound) {
            operations.push("removeExperience");
            newCandidate.operations = operations;
        }

        newCandidate.removeExperience.push({"technologyNameToRemove": $removeTechnologyName});

        $(this).closest('tr').remove();
    });

    // On remove an Application from the dialog, the object is prepared.
    $('#application-table').on('click', '#dialog-application-remove-button', function(e) {
        var $removeApplicationName = $(this).closest('tr').find('.dialog-application-field').text();
        $removeApplicationName = $removeApplicationName.trim();

        // Check if the operation is already added to the array of actions.
        var removeAppFound = $.inArray("removeApplication", operations) > -1;

        if (!removeAppFound) {
            operations.push("removeApplication");
            newCandidate.operations = operations;
        }

        newCandidate.removeApplication.push({"positionNameToRemove": $removeApplicationName});

        $(this).closest('tr').remove();
    });

    /*
        On Save Changes button click - get the data from the dialog form fields
        and execute the ajax call to insert the modifications of the Candidate in the database.
     */
    $('#save-candidate-changes-button').one('click', function(e) {
        var $newCandidateFirstName = $('#dialog-candidate-first-name').val();
        var $newCandidateLastName = $('#dialog-candidate-last-name').val();
        var $newAge = $('#dialog-candidate-age').val();
        var $newCandidateEmail = $('#dialog-candidate-email').val();

        if ($candidateFirstName !== $newCandidateFirstName) {
            operations.push("modifyFirstName");

            newCandidate.operations = operations;
            newCandidate.candidateFirstName = $newCandidateFirstName;
        }

        if ($candidateLastName !== $newCandidateLastName) {
            operations.push("modifyLastName");

            newCandidate.operations = operations;
            newCandidate.candidateLastName = $newCandidateLastName;
        }

        if ($age !== $newAge) {
            operations.push("modifyAge");

            newCandidate.operations = operations;
            newCandidate.age = $newAge;
        }

        if ($candidateEmail !== $newCandidateEmail) {
            operations.push("modifyEmail");

            newCandidate.operations = operations;
            newCandidate.candidateEmail = $newCandidateEmail;
        }

        var $expArray = $('.dialog-experience-list').children();

        if ($expArray.length > 0) {
            // Check if the operation is already added to the array of actions.
            var addExpFound = $.inArray("addExperience", operations) > -1;

            if (!addExpFound) {
                operations.push("addExperience");
                newCandidate.operations = operations;
            }
        }

        $.each($expArray, function (index, element) {
            var $technologyId = $(element).find('.technology-container').val();
            var $years = $(element).find('.years').val();
            newCandidate.addExperience.push({"technologyId": $technologyId, "expYears": $years});
        });

        var $appArray = $('.dialog-application-list').children();

        if ($appArray.length > 0) {
            // Check if the operation is already added to the array of actions.
            var addAppFound = $.inArray("addApplication", operations) > -1;

            if (!addAppFound) {
                operations.push("addApplication");
                newCandidate.operations = operations;
            }
        }

        $.each($appArray, function (index, element) {
            var $positionId = $(element).find('.position-container').val();
            console.log($positionId);
            newCandidate.addApplication.push({"positionId": $positionId});
        });

        console.log("OPERATIONS: " + operations);
        console.log("NEW CANDIDATE: " + JSON.stringify(newCandidate));

        // Execute the ajax call to edit the Candidate.
        $.ajax({
            method: 'POST',
            url: editUrl,
            dataType: 'json',
            data: JSON.stringify(newCandidate),
            contentType: "application/json; charset=utf-8",
            success: function (data) {
                alert(data.Status);
            },
            error: function (data) {
                console.log(data);
                alert(data.responseText);
            }
        });
    });
});
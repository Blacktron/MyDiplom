var hrs = null;
var candidates = null;
var technologies = null;
var positionsMatch = null;
var positionCompanies = null;
var searchMatchResult = null;

// =================================== Add Position ===================================

/**
 * Function which adds new Candidate into the database.
 */
function addPosition() {
    var $positionName = $('#position-name').val();
    var $companyId = $('#position-company-list').find('.position-company-select-container').val();
    var $hrId = $('#position-hr-list').find('.position-hr-select-container').val();

    var position = {
        positionName: $positionName,
        companyId: $companyId,
        hrId: $hrId,
        requirements: []
    };

    // Get the data from the Technology, Years and Priority fields.
    var $reqArray = $('#position-requirement-list').children();

    $.each($reqArray, function (index, element) {
        var $technologyId = $(element).find('.position-requirement-select-container').val();
        var $years = $(element).find('.years').val();
        var $priority = $(element).find('.priority').val();
        position.requirements.push({"technologyId": $technologyId, "years": $years, "priority": $priority});
    });

    // Check if all data is present and execute the ajax call.
    var isExecutableAjax = false;

    if ($positionName !== "" && $positionName !== undefined
        && $companyId != "" && $companyId != undefined
        && $hrId != "" && $hrId != undefined
        && $reqArray.length > 0) {
        isExecutableAjax = true;
    }

    // Execute the ajax call.
    if (isExecutableAjax) {
        $.ajax({
            method: 'POST',
            url: '/rest/positions',
            dataType: 'json',
            data: JSON.stringify(position),
            contentType: "application/json; charset=utf-8",
            success: function (data) {
                alert(data.Status);
            },
            error: function (data) {
                alert(data.responseText);
            }
        });
    }
}

/**
 * Function which populates the Company drop down with Companies.
 */
function populateCompaniesOn(element) {
    $.each(positionCompanies, function(i, option) {
        element.append(
            $('<option />').text(option.companyName).val(option.id)
        );
    });
}

/**
 * Function which populates the HR drop down with HRs.
 */
function populateHROn(element) {
    $.each(hrs, function(i, option) {
        element.append(
            $('<option />').text(option.hrFirstName + " " + option.hrLastName + " <" + option.hrEmail + ">").val(option.id)
        );
    });
}

/**
 * Function which populates the Technology drop down with Technologies.
 */
function populateRequirementOn(element) {
    $.each(technologies, function(i, option) {
        element.append(
            $('<option />').text(option.technologyName).val(option.id)
        );
    });
}

/**
 * Function which populates the Candidate drop down with Candidates.
 */
function populateApplicantOn(element) {
    $.each(candidates, function(i, option) {
        element.append(
            $('<option />').text(option.id + " " + option.candidateFirstName + " " + option.candidateLastName + " " + option.candidateEmail).val(option.id)
        );
    });
}

function populatePositionsMatchOn(element) {
    $.each(positionsMatch, function(i, option) {
        element.append(
            $('<option />').text(option.positionName + "-" + option.companyName).val(option.id)
        );
    });
}

// =================================== Search Position ===================================

/**
 * Gets the data from the search form and executes the ajax call to the server.
 */
function searchPosition() {
    var $positionID = $('#position-ID').val();
    var $positionName = $('#position-name').val();
    var $companyName = $('#company-name').val();

    var searchUrl = "/rest/positions";
    var isInformationMissing = false;

    if ($positionID !== "" && $positionID !== undefined) {
        isInformationMissing = false;
        if ($positionID > 0 && $positionName === "" && $companyName === "") {
            searchUrl += "/" + $positionID;
        } else if ($positionID <= 0) {
            alert("Position ID must be > 0");
            isInformationMissing = true;
        } else {
            searchUrl += "?positions.positionId=" + $positionID;
        }
    }

    if ($positionName !== "" && $positionName !== undefined) {
        searchUrl += checkIfFirstParameterInURL(searchUrl) + "positionName=" + $positionName;
    }

    if ($companyName !== "" && $companyName !== undefined) {
        searchUrl += checkIfFirstParameterInURL(searchUrl) + "companyName=" + $companyName;
    }

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
    if (searchResult.length > 0) {
        $('#search-result-table').find('tr:gt(0)').remove();
        $.each(searchResult, function(i, positionObject) {
            var positionId = positionObject.id;
            var positionName = positionObject.positionName;
            var companyName = positionObject.companyName;
            var hrId = positionObject.hrId;
            var hrFirstName = positionObject.hrFirstName;
            var hrLastName = positionObject.hrLastName;
            var hrEmail = positionObject.hrEmail;
            var requirements = positionObject.requirements;
            var applicants = positionObject.applicants;

            var row = "<tr><td class='positionId'>" + positionId + "</td><td class='positionName'>"
                + positionName + "</td><td class='companyName'>" + companyName + "</td><td class='hrId'>" + hrId
                + "</td><td class='hrFirstName'>" + hrFirstName + "</td><td class='hrLastName'>" + hrLastName
                + "</td><td class='hrEmail'>" + hrEmail + "</td>";

            var positionRequirement = "";
            $.each(requirements, function(i, requirementObject) {
                if (i < requirements.length - 1) {
                    positionRequirement += requirementObject.technologyName + "-" + requirementObject.years + "-" + requirementObject.priority + ", ";
                } else if (i == requirements.length - 1) {
                    positionRequirement += requirementObject.technologyName + "-" + requirementObject.years + "-" + requirementObject.priority;
                }
            });
            row += "<td class='positionRequirement'>" + positionRequirement + "</td>";

            var positionApplicant = "";
            $.each(applicants, function(i, applicationObject) {
                if (i < applicants.length - 1) {
                    positionApplicant += applicationObject.id + "-" + applicationObject.candidateFirstName + "-" + applicationObject.candidateLastName + "-" + applicationObject.age + ", ";
                } else if (i == applicants.length - 1) {
                    positionApplicant += applicationObject.id + "-" + applicationObject.candidateFirstName + "-" + applicationObject.candidateLastName + "-" + applicationObject.age;
                }
            });
            row += "<td class='positionApplicant'>" + positionApplicant + "</td>";

            // Add Edit and Delete buttons.
            row += "<td><button type='button' class='btn btn-primary edit-position' data-toggle='modal' data-target='#positionModal'>Edit</button></td>";
            row += "<td><button type='button' class='btn btn-danger delete-position'>Delete</button></td></tr>";

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

// =================================== Delete Position ===================================

/**
 * Delete a Delete on Delete button click.
 */
$('.table').on('click', '.delete-position', function(e) {
    var $id = $(this).closest('tr').find('.positionId').text();
    var deleteUrl = '/rest/positions/' + $id;

    $.ajax({
        method: 'DELETE',
        url: deleteUrl,
        success: function (data) {
            alert(data.Status);
        },
        error: function (data) {
            alert(data);
        }
    });

    $(this).closest('tr').remove();
});

// =================================== Edit Position ===================================

/**
 * Get the Position details and put them in the dialog on Edit button click.
 * On Save Changes button click the ajax call is prepared and executed in order
 * to make the modifications in the database.
 */
$('.table').on('click', '.edit-position', function(e) {

    // Get the data of the edited Position from the table row.
    var $currentPosition = $(this).closest('tr');
    var $positionId = $currentPosition.find('.positionId').text();
    var $positionName = $currentPosition.find('.positionName').text();
    var $companyName = $currentPosition.find('.companyName').text();
    var $hr = $currentPosition.find('.hrId').text() + " " + $currentPosition.find('.hrFirstName').text() + " " + $currentPosition.find('.hrLastName').text() + " " + $currentPosition.find('.hrEmail').text();
    var $positionRequirements = $currentPosition.find('.positionRequirement').text();
    var $positionApplicants = $currentPosition.find('.positionApplicant').text();


    // Put the data in the dialog form fields.
    $('#dialog-position-ID').val($positionId);
    $('#dialog-position-name').val($positionName);
    $('#dialog-company-name').val($companyName);

    var editUrl = '/rest/positions/' + $positionId;
    var operations = [];
    var newPosition = {
        addRequirement: [],
        removeRequirement: [],
        addApplicant: [],
        removeApplicant: []
    };

    // Fill the HR table in the dialog.
    var dialogHRArray = $hr.split(" ");
    $('#hr-table').find('tr:gt(0)').remove();
    var dialogRow = "<tr><td class='dialog-hr-field'>" + dialogHRArray[0] + "</td><td>" + dialogHRArray[1] + "</td><td>" + dialogHRArray[2] + "</td><td>" + dialogHRArray[3] + "</td><td><button type='button' class='btn btn-danger' id='dialog-hr-remove-button'>X</button></td></tr>";
    $('#hr-table').find('tbody').append(dialogRow);


    // Fill the Requirements table in the dialog.
    var dialogReqArray = $positionRequirements.split(",");
    $('#requirements-table').find('tr:gt(0)').remove();
    $.each(dialogReqArray, function(index, entry) {
        var requirement = entry.split("-");
        var dialogRow = "<tr><td class='dialog-requirement-field'>" + requirement[0] + "</td><td>" + requirement[1] + "</td><td>" + requirement[2] + "</td><td><button type='button' class='btn btn-danger' id='dialog-requirement-remove-button'>X</button></td></tr>";
        $('#requirements-table').find('tbody').append(dialogRow);
    });

    // Fill the Applications table in the dialog.
    var dialogAppArray = $positionApplicants.split(",");
    $('#applications-table').find('tr:gt(0)').remove();
    $.each(dialogAppArray, function(index, entry) {
        var application = entry.split("-");
        var dialogRow = "<tr><td class='dialog-application-field'>" + application[0] + "</td><td>" + application[1] + "</td><td>" + application[2] + "</td><td>" + application[3] + "</td><td><button type='button' class='btn btn-danger' id='dialog-applicant-remove-button'>X</button></td></tr>";
        $('#applications-table').find('tbody').append(dialogRow);
    });

    /**
     * Display new HR fields on button click in the dialog.
     * Used to add Positions's HR representative.
     */
    $('#dialog-add-position-hr-button').one('click', function() {
        // Add new row
        $.get("/position/addHRs.html", function(data) {
            $('.dialog-hr-list').append(data);
            // Populate the HR select box.
            populateHROn($('.position-hr-select-container').last());
        });
    });

    /**
     * Display new Requirement fields on button click in the dialog.
     * Used to add Position's requirement on a Position.
     */
    $('#dialog-add-requirements-button').on('click', function() {
        // Add new row
        $.get("/position/addRequirement.html", function(data) {
            $('.dialog-requirements-list').append(data);

            // Populate the Requirement select box.
            populateRequirementOn($('.position-requirement-select-container').last());
        });


    });

    /**
     * Display new Applicant fields on button click in the dialog.
     * Used to add Position's applicant on a Position.
     */
    $('#dialog-add-applications-button').on('click', function() {
        // Add new row
        $.get("/position/addApplicant.html", function(data) {
            $('.dialog-application-list').append(data);

            // Populate the Applicant select box.
            populateApplicantOn($('.applicant-container').last());
        });
    });

    $('#close-position-changes-button').click(function() {
        $('#dialog-add-requirements-button').unbind('click');
        $('#dialog-add-applications-button').unbind('click');
        $('#dialog-add-position-hr-button').unbind('click');
    });

    $('#save-position-changes-button').click(function() {
        $('#dialog-add-requirements-button').unbind('click');
        $('#dialog-add-applications-button').unbind('click');
        $('#dialog-add-position-hr-button').unbind('click');
    });

    // On remove an HR from the dialog, the object is prepared.
    $('#hr-table').on('click', '#dialog-hr-remove-button', function(e) {
        var $removeHRid = $(this).closest('tr').find('.dialog-hr-field').text();
        $removeHRid = $removeHRid.trim();

        // Check if the operation is already added to the array of actions.
        var removeHRFound = $.inArray("removeHR", operations) > -1;

        if (!removeHRFound) {
            operations.push("removeHR");
            newPosition.operations = operations;
        }

        newPosition.removeHRId = $removeHRid;

        $(this).closest('tr').remove();
    });

    // On remove an Requirement from the dialog, the object is prepared.
    $('#requirements-table').on('click', '#dialog-requirement-remove-button', function(e) {
        var $removeRequirement = $(this).closest('tr').find('.dialog-requirement-field').text();
        $removeRequirement = $removeRequirement.trim();

        // Check if the operation is already added to the array of actions.
        var removeReqFound = $.inArray("removeRequirement", operations) > -1;

        if (!removeReqFound) {
            operations.push("removeRequirement");
            newPosition.operations = operations;
        }

        newPosition.removeRequirement.push({"technologyNameToRemove": $removeRequirement});

        $(this).closest('tr').remove();
    });

    // On remove an Applicant from the dialog, the object is prepared.
    $('#applications-table').on('click', '#dialog-applicant-remove-button', function(e) {
        var $removeApplicant = $(this).closest('tr').find('.dialog-application-field').text();
        $removeApplicant = $removeApplicant.trim();

        // Check if the operation is already added to the array of actions.
        var removeAppFound = $.inArray("removeApplicant", operations) > -1;

        if (!removeAppFound) {
            operations.push("removeApplicant");
            newPosition.operations = operations;
        }

        newPosition.removeApplicant.push({"candidateId": $removeApplicant});

        $(this).closest('tr').remove();
    });

    /*
     On Save Changes button click - get the data from the dialog form fields
     and execute the ajax call to insert the modifications of the Candidate in the database.
     */
    $('#save-position-changes-button').one('click', function(e) {
        var $newPositionName = $('#dialog-position-name').val();
        var $newCompanyName = $('#dialog-company-name').val();
        var $newHR = $('.dialog-hr-list').find('.position-hr-select-container').val();

        if ($positionName !== $newPositionName) {
            operations.push("modifyPositionName");

            newPosition.operations = operations;
            newPosition.positionName = $newPositionName;
        }

        if ($companyName !== $newCompanyName) {
            operations.push("modifyCompany");

            newPosition.operations = operations;
            newPosition.companyName = $newCompanyName;
        }

        if ($newHR !== undefined) {
            var hrArray = $hr.split(" ");
            if (hrArray[0] !== $newHR) {
                operations.push("addHR");

                newPosition.operations = operations;
                newPosition.newHRId = $newHR;
            }
        }

        var $reqArray = $('.dialog-requirements-list').children();

        if ($reqArray.length > 0) {
            // Check if the operation is already added to the array of actions.
            var addReqFound = $.inArray("addRequirement", operations) > -1;

            if (!addReqFound) {
                operations.push("addRequirement");
                newPosition.operations = operations;
            }
        }

        $.each($reqArray, function (index, element) {
            var $technologyId = $(element).find('.position-requirement-select-container').val();
            var $years = $(element).find('.years').val();
            var $priority = $(element).find('.priority').val();
            newPosition.addRequirement.push({"technologyId": $technologyId, "years": $years, "priority": $priority});
        });

        var $appArray = $('.dialog-application-list').children();

        if ($appArray.length > 0) {
            // Check if the operation is already added to the array of actions.
            var addAppFound = $.inArray("addApplicant", operations) > -1;

            if (!addAppFound) {
                operations.push("addApplicant");
                newPosition.operations = operations;
            }
        }

        $.each($appArray, function (index, element) {
            var $candidateId = $(element).find('.applicant-container').val();
            newPosition.addApplicant.push({"candidateId": $candidateId});
        });

        console.log("NEW POSITION");
        console.log(newPosition);

        // Execute the ajax call to edit the Candidate.
        $.ajax({
            method: 'POST',
            url: editUrl,
            dataType: 'json',
            data: JSON.stringify(newPosition),
            contentType: "application/json; charset=utf-8",
            success: function (data) {
                alert(data.Status);
            },
            error: function (data) {
                alert(data.responseText);
            }
        });
    });
});

// =================================== Search Match ===================================

function searchMatch() {
    var $positionID = $('#positions-list').find('.positions-select-container').val();
    var searchUrl = "/rest/positions/match/" + $positionID;

    $.ajax({
        method: 'GET',
        url: searchUrl,
        success: function(data) {
            searchMatchResult = data;
            displaySearchMatchResult();
        },
        error: function(data) {
            alert(data.responseText);
        }
    });

}

function displaySearchMatchResult() {
    console.log("SEARCH MATCH RESULT");
    console.log(searchMatchResult);
    if (searchMatchResult.length > 0) {
        $('#search-match-result-table').find('tr:gt(0)').remove();
        $.each(searchMatchResult, function(i, applicantObject) {
            console.log("APPLICATION OBJECT");
            console.log(applicantObject);
            var candidates = applicantObject.applicants;
            console.log("CANDIDATES");
            console.log(candidates);

            var row = "<tr>";
            $.each(candidates, function(i, applicationObject) {
                if (i < candidates.length - 1) {
                    row += "<td class='candidateId'>" + applicationObject.id + "</td>";
                    row += "<td class='candidateFirstName'>" + applicationObject.candidateFirstName + "</td>";
                    row += "<td class='candidateLastName'>" + applicationObject.candidateLastName + "</td>";
                    row += "<td class='candidateAge'>" + applicationObject.age + "</td>";
                    row += "<td class='candidateEmail'>" + applicationObject.candidateEmail + "</td>";
                    row += "<td class='candidateRating'>" + applicationObject.rating + "\n";
                } else if (i == candidates.length - 1) {
                    row += "<td class='candidateId'>" + applicationObject.id + "</td>";
                    row += "<td class='candidateFirstName'>" + applicationObject.candidateFirstName + "</td>";
                    row += "<td class='candidateLastName'>" + applicationObject.candidateLastName + "</td>";
                    row += "<td class='candidateAge'>" + applicationObject.age + "</td>";
                    row += "<td class='candidateEmail'>" + applicationObject.candidateEmail + "</td>";
                    row += "<td class='candidateRating'>" + applicationObject.rating;
                }
                row += "</tr>";
            });

            $('#search-match-result-table').find('tbody').append(row);
        });
    }
}
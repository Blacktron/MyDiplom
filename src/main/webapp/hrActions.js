var companies = null;

// =================================== Add HR ===================================

/**
 * Function which adds new HR into the database.
 */
function addHR() {
    var $hrFirstName = $('#hr-first-name').val();
    var $hrLastName = $('#hr-last-name').val();
    var $phone = $('#hr-phone').val();
    var $hrEmail = $('#hr-email').val();
    var $hrCompany = $('#company-list').find('.company-container').val();

    var hr = {
        hrFirstName: $hrFirstName,
        hrLastName: $hrLastName,
        phone: $phone,
        hrEmail: $hrEmail,
        hrCompany: $hrCompany
    };

    // Check if all data is present and execute the ajax call.
    var isExecutableAjax = false;

    if ($hrFirstName !== "" && $hrFirstName !== undefined
        && $hrLastName != "" && $hrLastName != undefined
        && $phone != "" && $phone != undefined
        && $hrEmail != "" && $hrEmail != undefined
        && $hrCompany != "" && $hrCompany != undefined) {
        isExecutableAjax = true;
    }

    // Execute the ajax call.
    if (isExecutableAjax) {
        $.ajax({
            method: 'POST',
            url: '/rest/hrs',
            dataType: 'json',
            data: JSON.stringify(hr),
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
    $.each(companies, function(i, option) {
        element.append(
            $('<option />').text(option.companyName).val(option.id)
        );
    });
}

// =================================== Search HR ===================================

/**
 * Gets the data from the search form and executes the ajax call to the server.
 */
function searchHR() {
    var $hrID = $('#hr-ID').val();
    var $hrFirstName = $('#hr-first-name').val();
    var $hrLastName = $('#hr-last-name').val();
    var $phone = $('#hr-phone').val();
    var $hrEmail = $('#hr-email').val();
    var $company = $('#hr-company').val();

    var searchUrl = "/rest/hrs";
    var isInformationMissing = false;

    if ($hrID !== "" && $hrID !== undefined) {
        isInformationMissing = false;
        if ($hrID > 0 && $hrFirstName === "" && $hrLastName === "" && $phone === "" && $hrEmail === "" && $company === "") {
            searchUrl += "/" + $hrID;
        } else if ($hrID <= 0) {
            alert("HR ID must be > 0");
            isInformationMissing = true;
        } else {
            searchUrl += "?hr.hrID=" + $hrID;
        }
    }

    if ($hrFirstName !== "" && $hrFirstName !== undefined) {
        searchUrl += checkIfFirstParameterInURL(searchUrl) + "hrFirstName=" + $hrFirstName;
    }

    if ($hrLastName !== "" && $hrLastName !== undefined) {
        searchUrl += checkIfFirstParameterInURL(searchUrl) + "hrLastName=" + $hrLastName;
    }

    if ($phone !== "" && $phone !== undefined) {
        searchUrl += checkIfFirstParameterInURL(searchUrl)  + "phone=" + $phone;
    }

    if ($hrEmail !== "" && $hrEmail !== undefined) {
        searchUrl += checkIfFirstParameterInURL(searchUrl)  + "hrEmail=" + $hrEmail;
    }

    if ($company !== "" && $company !== undefined) {
        searchUrl += checkIfFirstParameterInURL(searchUrl) + "company=" + $company;
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
        $.each(searchResult, function(i, hrObject) {
            var hrId = hrObject.id;
            var hrFirstName = hrObject.hrFirstName;
            var hrLastName = hrObject.hrLastName;
            var phone = hrObject.phone;
            var hrEmail = hrObject.hrEmail;
            var company = hrObject.companyName;

            var row = "<tr><td class='hrId'>" + hrId + "</td><td class='hrFirstName'>" + hrFirstName
                + "</td><td class='hrLastName'>" + hrLastName + "</td><td class='phone'>" + phone
                + "</td><td class='hrEmail'>" + hrEmail + "</td><td class='company'>" + company + "</td>";

            // Add Edit and Delete buttons.
            row += "<td><button type='button' class='btn btn-primary edit-hr' data-toggle='modal' data-target='#hrModal'>Edit</button></td>";
            row += "<td><button type='button' class='btn btn-danger delete-hr'>Delete</button></td></tr>";

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

// =================================== Delete HR ===================================

/**
 * Delete an HR on Delete button click.
 */
$('.table').on('click', '.delete-hr', function(e) {
    var $id = $(this).closest('tr').find('.hrId').text();
    var deleteUrl = '/rest/hrs/' + $id;

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

// =================================== Edit HR ===================================

/**
 * Get the Candidate details and put them in the dialog on Edit button click.
 * On Save Changes button click the ajax call is prepared and executed in order
 * to make the modifications in the database.
 */
$('.table').on('click', '.edit-hr', function(e) {

    // Get the data of the edited HR from the table row.
    var $currentHR = $(this).closest('tr');
    var $hrId = $currentHR.find('.hrId').text();
    var $hrFirstName = $currentHR.find('.hrFirstName').text();
    var $hrLastName = $currentHR.find('.hrLastName').text();
    var $phone = $currentHR.find('.phone').text();
    var $hrEmail = $currentHR.find('.hrEmail').text();
    var $company = $currentHR.find('.company').text();

    // Put the data in the dialog form fields.
    $('#dialog-hr-ID').val($hrId);
    $('#dialog-hr-first-name').val($hrFirstName);
    $('#dialog-hr-last-name').val($hrLastName);
    $('#dialog-hr-phone').val($phone);
    $('#dialog-hr-email').val($hrEmail);
    $('#dialog-hr-company').val($company);

    var editUrl = '/rest/hrs/' + $hrId;
    var operations = [];
    var newHR = {};

    /*
     On Save Changes button click - get the data from the dialog form fields
     and execute the ajax call to insert the modifications of the HR in the database.
     */
    $('#save-hr-changes-button').one('click', function(e) {
        var $newHRFirstName = $('#dialog-hr-first-name').val();
        var $newHRLastName = $('#dialog-hr-last-name').val();
        var $newPnone = $('#dialog-hr-phone').val();
        var $newHREmail = $('#dialog-hr-email').val();
        var $newCompany = $('#dialog-hr-company').val();

        if ($hrFirstName !== $newHRFirstName) {
            operations.push("modifyFirstName");

            newHR.operations = operations;
            newHR.hrFirstName = $newHRFirstName;
        }

        if ($hrLastName !== $newHRLastName) {
            operations.push("modifyLastName");

            newHR.operations = operations;
            newHR.hrLastName = $newHRLastName;
        }

        if ($phone !== $newPnone) {
            operations.push("modifyPhone");

            newHR.operations = operations;
            newHR.phone = $newPnone;
        }

        if ($hrEmail !== $newHREmail) {
            operations.push("modifyEmail");

            newHR.operations = operations;
            newHR.hrEmail = $newHREmail;
        }

        if ($company !== $newCompany) {
            operations.push("modifyCompany");

            newHR.operations = operations;
            newHR.companyName = $newCompany;
        }

        // Execute the ajax call to edit the HR.
        $.ajax({
            method: 'POST',
            url: editUrl,
            dataType: 'json',
            data: JSON.stringify(newHR),
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
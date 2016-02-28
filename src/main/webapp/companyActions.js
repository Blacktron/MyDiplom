// =================================== Add Company ===================================

/**
 * Function which adds new Company into the database.
 */
function addCompany() {
    var $companyName = $('#company-name').val();

    var company = {
        companyName: $companyName
    };

    // Check if all data is present and execute the ajax call.
    var isExecutableAjax = false;

    if ($companyName !== "" && $companyName !== undefined) {
        isExecutableAjax = true;
    }

    // Execute the ajax call.
    if (isExecutableAjax) {
        $.ajax({
            method: 'POST',
            url: '/rest/companies',
            dataType: 'json',
            data: JSON.stringify(company),
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

// =================================== Search Company ===================================

/**
 * Gets the data from the search form and executes the ajax call to the server.
 */
function searchCompany() {
    var $companyID = $('#company-ID').val();
    var $companyName = $('#company-name').val();

    var searchUrl = "/rest/companies";
    var isInformationMissing = false;

    if ($companyID !== "" && $companyID !== undefined) {
        isInformationMissing = false;
        if ($companyID > 0 && $companyName === "") {
            searchUrl += "/" + $companyID;
        } else if ($companyID <= 0) {
            alert("Company ID must be > 0");
            isInformationMissing = true;
        } else {
            searchUrl += "?company.companyId=" + $companyID;
        }
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
        $.each(searchResult, function(i, companyObject) {
            var companyId = companyObject.id;
            var companyName = companyObject.companyName;

            var row = "<tr><td class='companyId'>" + companyId + "</td><td class='companyName'>" + companyName;
            row += "<td><button type='button' class='btn btn-primary edit-company' data-toggle='modal' data-target='#companyModal'>Edit</button></td>";
            row += "<td><button type='button' class='btn btn-danger delete-company'>Delete</button></td></tr>";

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

// =================================== Delete Company ===================================

/**
 * Delete a Company on Delete button click.
 */
$('.table').on('click', '.delete-company', function(e) {
    var $id = $(this).closest('tr').find('.companyId').text();
    var deleteUrl = '/rest/companies/' + $id;

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

// =================================== Edit Company ===================================

/**
 * Get the Company details and put them in the dialog on Edit button click.
 * On Save Changes button click the ajax call is prepared and executed in order
 * to make the modifications in the database.
 */
$('.table').on('click', '.edit-company', function(e) {

    // Get the data of the edited Company from the table row.
    var $currentCompany = $(this).closest('tr');
    var $companyId = $currentCompany.find('.companyId').text();
    var $companyName = $currentCompany.find('.companyName').text();

    // Put the data in the dialog form fields.
    $('#dialog-company-ID').val($companyId);
    $('#dialog-company-name').val($companyName);

    var editUrl = '/rest/companies/' + $companyId;
    var newCompany = {};

    /*
     On Save Changes button click - get the data from the dialog form fields
     and execute the ajax call to insert the modifications of the Company in the database.
     */
    $('#save-company-changes-button').one('click', function(e) {
        var $newCompanyName = $('#dialog-company-name').val();

        if ($companyName !== $newCompanyName) {
            newCompany.operations = "modifyCompany";
            newCompany.companyName = $newCompanyName;
        }

        // Execute the ajax call to edit the Technology.
        $.ajax({
            method: 'POST',
            url: editUrl,
            dataType: 'json',
            data: JSON.stringify(newCompany),
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
// =================================== Add Technology ===================================

/**
 * Function which adds new Technology into the database.
 */
function addTechnology() {
    var $technologyName = $('#technology-name').val();

    var technology = {
        technologyName: $technologyName
    };

    // Check if all data is present and execute the ajax call.
    var isExecutableAjax = false;

    if ($technologyName !== "" && $technologyName !== undefined) {
        isExecutableAjax = true;
    }

    // Execute the ajax call.
    if (isExecutableAjax) {
        $.ajax({
            method: 'POST',
            url: '/rest/technologies',
            dataType: 'json',
            data: JSON.stringify(technology),
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

// =================================== Search Technology ===================================

/**
 * Gets the data from the search form and executes the ajax call to the server.
 */
function searchTechnology() {
    var $technologyID = $('#technology-ID').val();
    var $technologyName = $('#technology-name').val();

    var searchUrl = "/rest/technologies";
    var isInformationMissing = false;

    if ($technologyID !== "" && $technologyID !== undefined) {
        isInformationMissing = false;
        if ($technologyID > 0 && $technologyName === "") {
            searchUrl += "/" + $technologyID;
        } else if ($technologyID <= 0) {
            alert("Technology ID must be > 0");
            isInformationMissing = true;
        } else {
            searchUrl += "?technology.technologyId=" + $technologyID;
        }
    }

    if ($technologyName !== "" && $technologyName !== undefined) {
        searchUrl += checkIfFirstParameterInURL(searchUrl) + "technologyName=" + $technologyName;
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
        $.each(searchResult, function(i, technologyObject) {
            var technologyId = technologyObject.id;
            var technologyName = technologyObject.technologyName;

            var row = "<tr><td class='technologyId'>" + technologyId + "</td><td class='technologyName'>" + technologyName;
            row += "<td><button type='button' class='btn btn-primary edit-technology' data-toggle='modal' data-target='#technologyModal'>Edit</button></td>";
            row += "<td><button type='button' class='btn btn-danger delete-technology'>Delete</button></td></tr>";

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

// =================================== Delete Technology ===================================

/**
 * Delete a Technology on Delete button click.
 */
$('.table').on('click', '.delete-technology', function(e) {
    var $id = $(this).closest('tr').find('.technologyId').text();
    var deleteUrl = '/rest/technologies/' + $id;

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

// =================================== Edit Technology ===================================

/**
 * Get the Technology details and put them in the dialog on Edit button click.
 * On Save Changes button click the ajax call is prepared and executed in order
 * to make the modifications in the database.
 */
$('.table').on('click', '.edit-technology', function(e) {

    // Get the data of the edited Technology from the table row.
    var $currentTechnology = $(this).closest('tr');
    var $technologyId = $currentTechnology.find('.technologyId').text();
    var $technologyName = $currentTechnology.find('.technologyName').text();

    // Put the data in the dialog form fields.
    $('#dialog-technology-ID').val($technologyId);
    $('#dialog-technology-name').val($technologyName);

    var editUrl = '/rest/technologies/' + $technologyId;
    var newTechnology = {};

    /*
         On Save Changes button click - get the data from the dialog form fields
         and execute the ajax call to insert the modifications of the Technology in the database.
     */
    $('#save-technology-changes-button').one('click', function(e) {
        var $newTechnologyName = $('#dialog-technology-name').val();

        if ($technologyName !== $newTechnologyName) {
            newTechnology.operations = "modifyTechnology";
            newTechnology.technologyName = $newTechnologyName;
        }

        // Execute the ajax call to edit the Technology.
        $.ajax({
            method: 'POST',
            url: editUrl,
            dataType: 'json',
            data: JSON.stringify(newTechnology),
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
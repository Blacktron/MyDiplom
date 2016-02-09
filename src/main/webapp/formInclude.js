$(function() {
	/*
		Display the addCandidate form on Add Candidate button click located in the navigation.
		Used to enter a new Candidate into the database.
	*/
	$('#add-candidate-button').on('click', function() {
		$('.content-container').load('addCandidate.html');
	});

	/*	
		Display new Technology and Years fields on button click.
		Used to add Candidate's years of experience with a Technology. 
	*/
	$('#add-experience-button').on('click', function() {
		// Add new row
		$.get("addExperience.html", function(data) {
		    $('#experience-list').append(data);

			//populateTechsOn($('#experience-list>div:last-child'));
			populateTechsOn($('.technology-container').last());
		});
	});

	/*
		Displays the searchCandidate form on Search Candidate button click located in the navigation.
		Used to search for Candidate(s) based on the provided criteria.
	*/
	$('#search-candidate-button').on('click', function() {
		$('.content-container').load('searchCandidate.html');
	});
});
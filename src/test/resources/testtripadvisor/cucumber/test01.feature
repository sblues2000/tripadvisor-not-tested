Feature: Tripadvisor search for Attractions
 
 Background:
	Given I used Chrome browser

 Scenario: Search for an outdoor activity with max price
	 Given I am on Trip advisor attractions page page
     When I search for the term California
 	 When I select the Sights & Landmarks link
     When I select the Attraction Tickets link
     When I navigate through the search result pages until I find and select "Monterey Bay Aquarium Admission"
     Then the product’s price will not be greater than £90

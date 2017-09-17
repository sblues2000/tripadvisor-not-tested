Feature: Tripadvisor search for Attractions
 
 Background:
	Given I used Chrome browser

 Scenario: Search for an outdoor activity with max price
	 Given I am on Trip advisor attractions page page
     When I search for the term California
 	 When I select the Outdoor Activities option
     When I navigate through the search result pages until I find and select hiking link for "Hills and Hidden Gems"
     Then the product’s price will not be greater than £50

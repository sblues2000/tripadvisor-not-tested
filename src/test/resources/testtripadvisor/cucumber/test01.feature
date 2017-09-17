Feature: Tripadvisor search for Attractions
 
 Background:
	Given I used SomeNotSpecified browser

 Scenario: Search for an outdoor activity with max price
	 Given I am on Trip advisor attractions page page
     When I search for the term 'California'
 	 When I select the ‘Outdoor Activities’ option
     When I navigate through the search result pages until I find the link for “San Francisco Urban Hike: Hills and Hidden Gems”
     When I click the link for “San Francisco Urban Hike: Hills and Hidden Gems”
     Then the product’s price is not greater than £50.

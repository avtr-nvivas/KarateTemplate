
Feature: Test for the Home Page
    Background: Define Url
        * def articleRequestBody = read('classpath:serviceApp/data/json/datos.json')
        * def userUrl = articleRequestBody.url
        * print "-- userUrl  -->> [", userUrl, "]"
        Given url userUrl
 
@name=Get-all-tags
    Scenario: Get-all-tags
        Given path 'tags'
        And header karate-name = 'Get-Tags'
        When method Get
        Then status 200
        And match response ==
        """
            {
                "tags":"#array"
            }
        """
@name=Get-10-articles
    Scenario: Get-10-articles
        * def timeValidator = read('classpath:helpers/time-validator.js')
        Given params { limit : 10 , offset : 0}
        Given path 'articles'
        And header karate-name = 'Get-Articles'
        When method Get
        Then status 200
        And match response == {"articles": "#[10]" ,"articlesCount":10}
        And match each response.articles ==
        """
              {
                "slug":"#string",
                "title":"#string",
                "description":"#string",
                "body":"#string",
                "tagList":"#array",
                "createdAt":"#? timeValidator(_)",
                "updatedAt":"#? timeValidator(_)",
                "favorited":"#boolean",
                "favoritesCount":"#number",
                "author":{
                    "username":"#string",
                    "bio":"##string",
                    "image":"#string",
                    "following":"#boolean"
                }
            }
        """

            # Ejecutar Karate mvn test -D"karate.options= --tags @debug"
            # Ejecutar Performance mvn clean test-compile gatling:test


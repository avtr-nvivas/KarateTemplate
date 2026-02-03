
Feature: Home Work
    Background: Preconditions
        * def articleRequestBody = read('classpath:serviceApp/data/json/datos.json')
        * def userUrl = articleRequestBody.url
        * print "-- userUrl  -->> [", userUrl, "]"
        Given url userUrl

        * def timeValidator = read('classpath:helpers/time-validator.js')
        * def dataGenerator = Java.type('helpers.DataGenerator')
        * def tagListimestamp = dataGenerator.getTimestamp()

        * def tokenResponse = callonce read('classpath:serviceApp/feature/CreateToken.feature')
        * def token = tokenResponse.authToken

@name=Home-Work
    Scenario: Home-Work
        # Step 1: Get atricles of the global feed
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
                "tagList":"##array",
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
        # Step 2: Get the favorites count and slug ID for the first arice, save it to variables
        * def slug = response.articles[0].slug
        * def favoritesCount = response.articles[0].favoritesCount
        * print "----------------------------------------------------------------------------"
        * print "- response.articles[0] => ", response.articles[0]
        * print "- slug                 => ", slug
        * print "- favoritesCount       => ", favoritesCount
        * print "----------------------------------------------------------------------------"

        # Step 3: Make POST request to increse favorites count for the first article
        Given header Authorization = 'Token ' + token
        Given path 'articles' + '/' + slug + '/favorite'
        And request {}
        And header karate-name = 'Post-Articles-ID-favorite'
        When method Post
        Then status 200
        # Step 4: Verify response schema
        And match response.article ==
        """
              {
                "id":"#number",
                "slug":"#string",
                "title":"#string",
                "description":"#string",
                "body":"#string",
                "tagList":"##array",
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
        And match response.article.slug == slug
        And match response.article.favorited == true
        # Step 5: Verify that favorites article incremented by 1
        And match response.article.favoritesCount == favoritesCount + 1
        * def favorites = response.article.favorites
        * print "----------------------------------------------------------------------------"
        * print "- response.article => ", response.article
        * print "- slug             => ", slug
        * print "- favorited        => ", favoritesCount
        * print "----------------------------------------------------------------------------"

        # Step 6: Get all favorite articles
        Given params { favorited: nvivlas, limit : 10 , offset : 0}
        Given path 'articles'
        And header karate-name = 'Get-Articles'
        When method Get
        Then status 200
        # Step 7: Verify response schema
        And match response.articles[0] ==
        """
              {
                "slug":"#string",
                "title":"#string",
                "description":"#string",
                "body":"#string",
                "tagList":"##array",
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
        * print "----------------------------------------------------------------------------"
        * print "- response.articles => ", response.articles[0]
        * print "- response.articles.slug => ", response.articles[0].slug
        * print "- slug                   => ", slug
        * print "- favorited              => ", favoritesCount
        * print "----------------------------------------------------------------------------"
        # Step 8: Verify that slug ID from Step 2 exist in one of the favorite articles
        And match response.articles[0].slug == slug
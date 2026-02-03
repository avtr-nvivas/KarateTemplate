@debug
Feature: Articles
    Background: Preconditions
        * def articleRequestBody = read('classpath:serviceApp/data/json/datos.json')
        * def userUrl = articleRequestBody.url
        Given url userUrl

        * def articleRequestBody = read('classpath:serviceApp/data/json/newArticleRequest.json')
        * def vUser = "Nvivas16"
        * set articleRequestBody.article.title = vUser + "_Title"
        * set articleRequestBody.article.description = vUser + "_Description"
        * set articleRequestBody.article.body = vUser + "_body"

        * def tokenResponse = callonce read('classpath:serviceApp/feature/CreateToken.feature')
        * def token = tokenResponse.authToken
        
    @name=Create-new-and-delete
    Scenario: Create-new-and-delete
        Given header Authorization = 'Token ' + token
        Given path 'articles'
        And request articleRequestBody
        And header karate-name = 'Create-Article'
        When method POST
        Then status 201
        And match response.article.title == articleRequestBody.article.title
        * def slug = response.article.slug 

        Given header Authorization = 'Token ' + token
        Given path 'articles',slug
        And header karate-name = 'Delete-Article'
        When method Delete
        Then status 204
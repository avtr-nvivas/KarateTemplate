Feature: Create Token
    Background: Datos
        * def articleRequestBody = read('classpath:serviceApp/data/json/datos.json')
        * def userUrl = articleRequestBody.url
        * def userEmail = articleRequestBody.user.email
        * def userPass = articleRequestBody.user.password
      
    Scenario: Create Token
        Given url userUrl
        Given path 'users/login'
        And request {"user":{"email":"#(userEmail)","password":"#(userPass)"}}  
        And header karate-name = 'Post-Create-Token'
        When method Post
        Then status 200
        And match response.user.email == 'nicolas.r.vivas@gmail.com'
        * def authToken = response.user.token 


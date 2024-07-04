(config
(text-field
:name "clientId"
:label "Client ID"
:placeholder "Please enter your clientid")

(password-field
:name "clientSecret"
:label "Client Secret"
:placeholder "Please enter your clientsecret")


(oauth2/authorization-code-with-client-credentials
(authorization-code
(source
(http/get
:base-url "https://oauth.employmenthero.com"
:url "/oauth2/authorize"
(query-params
"response_type" "code"
"client_id" "{clientId}"
"redirect_uri" "$FIVETRAN-APP-URL/integrations/employmenthero/oauth2/return" ))))

(access-token
(source
(http/post
:base-url "https://oauth.employmenthero.com"
:url "/oauth2/token"
(body-param-format "application/x-www-form-urlencoded")
(body-params
"code" "$AUTHORISATION-CODE"
"client_id" "{clientId}"
"client_secret" "{clientSecret}"
"grant_type" "authorization_code"
"redirect_uri" "$FIVETRAN-APP-URL/integrations/employmenthero/oauth2/return")))
(fields 
access_token :<= "access_token"
refresh_token :<= "refresh_token"
token_type :<= "token_type"
scope :<= "scope"
realm_id :<= "realm-id"
expires_at :<= "expires_at"))

(refresh-token
(source
(http/post
:base-url "https://oauth.employmenthero.com"
:url "/oauth2/token"
(query-params
"code" "$AUTHORISATION-CODE"
"client_id" "{clientId}"
"client_secret" "{clientSecret}"
"grant_type" "refresh_token"
"redirect_uri" "$FIVETRAN-APP-URL/integrations/employmenthero/oauth2/return"
"refresh_token" "$REFRESH-TOKEN")))
(fields 
refresh_token :<= "refresh_token"
access_token :<= "access_token"))))


(default-source (http/get :base-url "https://api.employmenthero.com/api/v1"
(header-params "Content-Type" "application/json"))
(paging/page-number:
        page-number-query-param-initial-value  1
        page-number-query-param-name  "page_index"
        limit  100
        limit-query-param-name "item_per_page")
(auth/oauth2)
(error-handler
(when :status 200 :message "Sucess"))
)

(entity ORGANISATIONS
(api-docs-url "https://developer.employmenthero.com/api-references/#get-all-organisations")
(source (http/get :url "/organisations")
    (extract-path "data.items")
    (setup-test
        (upon-receiving :code 200 (pass)))
                  )
(fields
    id    :id
    name
    phone
    country
    logo_url ))
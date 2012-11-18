defaulthttp-restclient
======================

This generic REST client (module `defaulthttp-restclient`) is based on Apache's
`org.apache.http.impl.client.DefaultHttpClient`. It can set up
GET, PUT, POST, and DELETE requests with Basic HTTP authentication over HTTP 
or HTTPS.

                        [DefaultHttpClient]
                             (Apache)
                                |
                                |
                                |
                      [DefaultHttpRestClient]
                        (Basic REST Client)
                         |              |
                        |                |
                       |                  |
     [DefaultHttpAuthRestClient]   [DefaultHttpsRestClient]
   (REST Client w. Authentication)     (SSL REST Client)
                 |
                 |
                 |
    [DefaultHttpsAuthRestClient]
 (SSL REST Client w. Authentication)
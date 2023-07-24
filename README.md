# Identity Reconciliation
#### Note : 
This application is using free tier of Render, so the server shuts down automatically when not in use.
Therefore, it will take 2 mins for the first request to process(Since starting server takes 2 mins). 
Once server is started request will be processed instantly.

#### Endpoint 
<ul>
  <li>POST request: https://identity-reconciliation-8va6.onrender.com/identify</li>
</ul>

#### Request Body Example: 
```json
{
  "email": "lorraine@hillvalley.edu",
  "phoneNumber": "123456"
}
```

Response body Example:
```json
{
    "contact": {
        "primaryContactId": 1,
        "emails": [
            "lorraine@hillvalley.edu"
        ],
        "phoneNumbers": [
            "123456"
        ],
        "secondaryContactIds": []
    }
}
```

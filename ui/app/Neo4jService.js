/**
 * Created by Philipp on 21.12.2015.
 */
"use strict";
class Neo4jQuery {

    constructor(statement) {
        this.statement = statement;
    }

    getQuery() {
        return {
            "statements": [
                {
                    "statement": this.statement,
                    "resultDataContents": ["graph"], "includeStats": true
                }
            ]
        };
    }

}

class Neo4jService {
    constructor(host, auth) {
        if (host == undefined)
            host = "http://localhost:7474";
        if (auth == undefined)
            auth = "Basic bmVvNGo6QklHZGF0YQ==";


        this.host = host;
        this.auth = auth;
    }

    query(statement,progressHandler) {
        var self = this;
        return new Promise(function (resolve, reject) {
            $.ajax({
                url: self.host + "/db/data/transaction/commit",
                type: 'POST',
                data: JSON.stringify(statement.getQuery()),
                datatype: 'json',
                success: resolve,
                error: reject,
                headers: {
                    'Authorization': self.auth,
                    'Content-Type': "application/json"
                }
            });
        });
    }
}
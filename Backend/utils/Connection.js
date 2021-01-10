const MongoClient = require('mongodb').MongoClient

class Connection {
    static connectToMongo() {
        if ( this.db ) return Promise.resolve(this.db)
        return MongoClient.connect(this.url, this.options)
            .then(db => this.db = db)
    }
    // // or in the new async world
    // static async connectToMongo() {
    //     if (this.db) return this.db
    //     this.db = await MongoClient.connect(this.url, this.options)
    //     return this.db
    // }
}

Connection.connectionOptions= {
    user: process.env.MONGO_INITDB_ROOT_USERNAME || 'root',
    host: process.env.MONGO_HOST || 'localhost',
    database: process.env.MONGO_INITDB_DATABASE || 'corona',
    password: process.env.MONGO_INITDB_ROOT_PASSWORD,
    port: process.env.MONGO_PORT || 5432,
}
Connection.db = null
Connection.url = 'mongodb://'+Connection.connectionOptions.user+':'+Connection.connectionOptions.password+'@'+Connection.connectionOptions.host+':'+Connection.connectionOptions.port+'/'+Connection.connectionOptions.database;
Connection.options = {
    bufferMaxEntries:   0,
    reconnectTries:     5000,
    useNewUrlParser:    true,
    useUnifiedTopology: true,
}

module.exports = { Connection }
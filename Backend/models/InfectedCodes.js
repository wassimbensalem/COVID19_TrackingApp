const mongoose = require('mongoose');


const UserSchema = new mongoose.Schema({
    codes: String
});

const InfectedCodes = mongoose.model('InfectedCodes', UserSchema);

module.exports = InfectedCodes;
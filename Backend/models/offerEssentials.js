const mongoose = require('mongoose');
const User = require('./User');
const Schema = mongoose.Schema;
const propertiesSchema = new Schema({
  quantity: String,
  price: String,
  ownerName: String,
  ownerLocation: [] //[<longitude>, <latitude> ]
});
const offerEssentialsSchema = new Schema({
  name: String,
  category: String,
  essentialProperties: [propertiesSchema]
  
});

const offerEssentials = mongoose.model('offerEssentials', offerEssentialsSchema);

module.exports = offerEssentials;

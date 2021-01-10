const mongoose = require('mongoose');
const User = require('./User');
const Schema = mongoose.Schema;
const propertiesSchema = new Schema({
  quantity: String,
  price: String,
  ownerName: String,
  ownerLocation: [], //[<longitude>, <latitude> ],
  email: String
});
const EssentialsSchema = new Schema({
  name: String,
  category: String,
  essentialProperties: [propertiesSchema]
});

const Essentials = mongoose.model('Essentials', EssentialsSchema);

module.exports = Essentials;

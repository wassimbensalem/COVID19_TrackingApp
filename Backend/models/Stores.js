const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const StoresSchema = new Schema({
  name: String,
  location: [],
  essentials: [
    {
      name: String,
      price: String,
      quantity: String
    }
  ]
});

const Stores = mongoose.model('Stores', StoresSchema);

module.exports = Stores;

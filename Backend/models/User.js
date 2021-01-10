const mongoose = require('mongoose');

const UserSchema = new mongoose.Schema({
  name: {
    type: String,
    required: true
  },

  password: {
    type: String,
    required: true
  },
  date: {
    type: Date,
    default: Date.now
  },
  token: String,
  latitude: [],
  longitude: [],
  dates: [],
  points: [],

  infected: {
    type: Boolean,
    default: false
  },

  infectedSince: {
    type: Date
  },

  risk: {
    type: Number,
    default: 0.0
  },
  previousRisk: {
    type: Number,
    default: 0.0
  },

  isAdmin: {
    type: Boolean,
    default: false
  }
});

const User = mongoose.model('User', UserSchema);

module.exports = User;

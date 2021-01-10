

const mongoose = require('mongoose');

const GPSSchema = new mongoose.Schema({
  traceid: {
    type: String,
    required: true
  },

  user: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },

  date: {
    type: Date,
  },

  infected: {
    type: Boolean,
    default: false
  },

  location: {
    type: {
      type: String, // Don't do `{ location: { type: String } }`
      enum: ['Point'], // 'location.type' must be 'Point'
      required: true
    },
    coordinates: {
      type: [Number],
      required: true,
      index: '2dsphere'
    }
  },

  contacts: [
    { type: mongoose.Schema.Types.ObjectId, ref: 'GPS' }
  ]


});

GPSSchema.index({location: '2dsphere'});
const GPS = mongoose.model('GPS', GPSSchema);

module.exports = GPS;

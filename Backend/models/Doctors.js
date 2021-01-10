const mongoose = require('mongoose');
const Schema = mongoose.Schema;
const propertiesSchema = new Schema({
  '@id': String,
  addrcity: String,
  addrcountry: String,
  addrhousenumber: String,
  addrpostcode: String,
  addrstreet: String,
  amenity: String,
  building: String,
  buildingheight: String,
  buildingmin_level: String,
  opening_hours: String,
  rooflevels: String,
  roofshape: String,
  healthcare: String,
  healthcarespeciality: String,
  name: String,
  wheelchair: String,
  opening_hours: String
});

const polygonSchema = new mongoose.Schema({
  type: {
    type: String,
    enum: ['Polygon'],
    required: true
  },
  coordinates: {
    type: [[[Number]]], // Array of arrays of arrays of numbers
    required: true,
    default: [
      [
        [8.6844238, 49.8198815],
        [8.6844343, 49.8198476],
        [8.6843874, 49.8198407],
        [8.6844066, 49.819776],
        [8.6844535, 49.8197844],
        [8.6844657, 49.8197557],
        [8.6845239, 49.8197616],
        [8.6845315, 49.8197376],
        [8.6846608, 49.8197611],
        [8.6848241, 49.8198495],
        [8.6848048, 49.819872],
        [8.6849939, 49.8199496],
        [8.6848792, 49.8200599],
        [8.6845912, 49.8199429],
        [8.6846059, 49.8199036],
        [8.6844238, 49.8198815]
      ]
    ]
  }
});
const DoctorsSchema = new Schema({
  type: String,
  properties: propertiesSchema,
  geometry: polygonSchema,
  adress: String,
  id: String
});

const Doctors = mongoose.model('Doctors', DoctorsSchema);

module.exports = Doctors;

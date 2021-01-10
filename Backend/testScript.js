const mongoose = require('mongoose');

mongoose.connect('mongodb://localhost:27017/corona_app_db');

mongoose.connection
  .once('open', function() {
    console.log('Connection succesfull ');
  })
  .on('error', function(error) {
    console.log(error);
  });

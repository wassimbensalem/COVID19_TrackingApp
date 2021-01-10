const mocha = require('mocha');
const assert = require('assert');
const essentials = require('../models/Essentials');
describe('Saving Records', function() {
  it('Save an Essential to DB', function() {
    var essential = new essentials({
      id: 0,
      name: 'Toilet Paper',
      quantity: 25,
      category: 'House Stuffs',
      price: '22$'
    });
    essential.save().then(function(done) {
      assert(essential.isNew() === false);
      done();
    });
  });
});

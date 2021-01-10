'use strict';

const { Connection } = require('../utils/Connection')
let dbo = Connection.db;

/**
 * Add a new profile
 *
 * body Profile PProfile object
 * no response value expected for this operation
 **/
exports.addProfile = function(body) {
  return new Promise(function(resolve, reject) {
    dbo.collection("customers").insertOne(body, function(err, res) {
      if (err) {
        reject(err)
      }
      console.log("1 document inserted");
      resolve();
    });

  });
}


/**
 * Deletes a profile
 *
 * profileId Long Profile id to delete
 * api_key String  (optional)
 * no response value expected for this operation
 **/
exports.deleteProfile = function(profileId,api_key) {
  return new Promise(function(resolve, reject) {
    dbo.collection("profile").deleteOne({'_id': profileId}, function(err, obj) {
      if (err) {
        reject(err)
      }
      console.log("1 document deleted");
      resolve();
    });
  });
}


/**
 * Find profile by ID
 * Returns a single profile
 *
 * profileId Long ID of pet to return
 * returns Profile
 **/
exports.getProfileById = function(profileId) {
  return new Promise(function(resolve, reject) {
    dbo.collection('profile').findOne({'_id': profileId}, function(err, result) {
      if (err) {
        reject(err)
      }
      console.log("Found the following record");
      console.log(result);
      resolve(result)
    });
  });
}


/**
 * Update an existing profile
 *
 * body Profile Profile object
 * no response value expected for this operation
 **/
exports.updateProfile = function(body) {
  return new Promise(function(resolve, reject) {
    resolve();
  });
}


/**
 * Updates a profile with form data
 *
 * name String  (optional)
 * status String  (optional)
 * profileId Long ID of profile that needs to be updated
 * no response value expected for this operation
 **/
exports.updateProfiletWithForm = function(name,status,profileId) {
  return new Promise(function(resolve, reject) {
    resolve();
  });
}


/**
 * uploads an image
 *
 * additionalMetadata String  (optional)
 * file byte[]  (optional)
 * profileId Long ID of profile to update
 * returns ApiResponse
 **/
exports.uploadFile = function(additionalMetadata,file,profileId) {
  return new Promise(function(resolve, reject) {
    var examples = {};
    examples['application/json'] = {
  "code" : 0,
  "type" : "type",
  "message" : "message"
};
    if (Object.keys(examples).length > 0) {
      resolve(examples[Object.keys(examples)[0]]);
    } else {
      resolve();
    }
  });
}


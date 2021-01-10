'use strict';

var utils = require('../utils/writer.js');
var Profile = require('../service/ProfileService');

module.exports.addProfile = function addProfile (req, res, next, body) {
  Profile.addProfile(body)
    .then(function (response) {
      utils.writeJson(res, response);
    })
    .catch(function (response) {
      utils.writeJson(res, response);
    });
};

module.exports.deleteProfile = function deleteProfile (req, res, next, profileId, api_key) {
  Profile.deleteProfile(profileId, api_key)
    .then(function (response) {
      utils.writeJson(res, response);
    })
    .catch(function (response) {
      utils.writeJson(res, response);
    });
};

module.exports.getProfileById = function getProfileById (req, res, next, profileId) {
  Profile.getProfileById(profileId)
    .then(function (response) {
      utils.writeJson(res, response);
    })
    .catch(function (response) {
      utils.writeJson(res, response);
    });
};

module.exports.updateProfile = function updateProfile (req, res, next, body) {
  Profile.updateProfile(body)
    .then(function (response) {
      utils.writeJson(res, response);
    })
    .catch(function (response) {
      utils.writeJson(res, response);
    });
};

module.exports.updateProfiletWithForm = function updateProfiletWithForm (req, res, next, name, status, profileId) {
  Profile.updateProfiletWithForm(name, status, profileId)
    .then(function (response) {
      utils.writeJson(res, response);
    })
    .catch(function (response) {
      utils.writeJson(res, response);
    });
};

module.exports.uploadFile = function uploadFile (req, res, next, additionalMetadata, file, profileId) {
  Profile.uploadFile(additionalMetadata, file, profileId)
    .then(function (response) {
      utils.writeJson(res, response);
    })
    .catch(function (response) {
      utils.writeJson(res, response);
    });
};

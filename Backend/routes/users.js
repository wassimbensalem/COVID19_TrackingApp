const express = require('express');
const router = express.Router();
const bcrypt = require('bcryptjs');
const passport = require('passport');
const jwt = require('jsonwebtoken');
const User = require('../models/User');
const InfectedCodes = require('../models/InfectedCodes');
const Essentials = require('../models/Essentials');
const offerEssentials = require('../models/offerEssentials');
const Stores = require('../models/Stores');

const Doctors = require('../models/Doctors');

const GPS = require('../models/GPS');
const { forwardAuthenticated } = require('../config/auth');
const { ensureAuthenticated } = require('../config/auth');
const { overpass } = require('query-overpass');

let loggedUser = undefined;

router.get('/dashboard', verifyToken, (req, res) => {
  jwt.verify(req.token, 'secretkey', (err, authData) => {
    if (err) {
      res.sendStatus(403);
    } else {
      res.json({
        message: 'Dashboard Accessed...',
        authData,
        loggedUser
      });
    }
  });
});

router.get('/login', forwardAuthenticated, (req, res) => res.render('login'));

router.get('/register', forwardAuthenticated, (req, res) => res.render('register'));
// register to the app
router.post('/register', (req, res) => {
  const { name, password, password2, isAdmin } = req.query;
  let error = [];

  if (!name || !password || !password2) {
    error.push('Please enter all fields');
  }

  if (password != password2) {
    error.push('Passwords do not match');
  }

  if (password.length < 6) {
    error.push('Password must be at least 6 characters');
  }

  if (error.length > 0) {
    res.status(200).json({
      error,
      name,
      password,
      password2,
      isAdmin
    });
  } else {
    User.findOne({ name: name }).then(user => {
      if (user) {
        error.push('User name already exists');
        res.status(200).json({ error, name, password, password2, isAdmin });
      } else {
        const newUser = new User({
          name: name,
          password: password,
          token: null,
          latitude: [],
          longitude: [],
          dates: [],
          points: []
        });

        bcrypt.genSalt(10, (err, salt) => {
          bcrypt.hash(newUser.password, salt, (err, hash) => {
            if (err) throw err;
            newUser.password = hash;
            newUser
              .save()
              .then(user => {
                res.status(200).json({ error: [] });
              })
              .catch(err => console.log(err));
          });
        });
      }
    });
  }
});
// login to the app
router.post('/login', (req, res, next) => {
  // generate a token for the user to use everytime he logs in
  const { name, password } = req.query;
  const loginUser = {
    name: name,
    password: password
  };
  let error = [];
  let message = [];
  // Match user
  User.findOne({
    name: name
  }).then(user => {
    if (!user) {
      error = [];
      message.push('That Username is not registered');
      loggedUser = undefined;
      res.json({ loggedUser: loggedUser, message, error });
      next();
    }
    // Match password
    else {
      bcrypt.compare(password, user.password, (err, isMatch) => {
        if (err) throw err;
        if (isMatch) {
          loggedUser = user;
          loggedUser.token = null;
          loggedUser.save();
          jwt.sign({ loggedUser }, 'secretkey', (err, token) => {
            Object.assign(loggedUser, { token: token }).save();
            res.json({
              token,
              loggedUser,
              message,
              error
            });
          });
        } else {
          error = [];
          message.push('Password incorrect');
          loggedUser = undefined;
          res.json({ loggedUser: loggedUser, message, error });
        }
      });
    }
  });
});

// Logout
router.get('/logout', (req, res) => {
  req.logout();
  res.redirect('/users/login');
});

// Set user locationt
router.post('/setLocation', (req, res, next) => {
  const { latitude, longitude, date, token } = req.query;

  User.findOne({
    token: token
  }).then(user => {
    if (Array.isArray(latitude)) {
      for (let i = 0; i < latitude.length; i++) {
        let x = { type: 'Point', coordinates: [latitude[i], longitude[i]], time: date[i] };
        user.points.push(x);
      }
    } else {
      user.latitude.push(latitude);
      user.longitude.push(longitude);
      user.dates.push(date);
      let x = { type: 'Point', coordinates: [latitude, longitude], time: date };
      user.points.push(x);
    }

    Object.assign(user, { points: user.points }).save();
  });
  res.status(200).json('OK');
});

router.post('/getPoints', (req, res, next) => {
  const { token, gi } = req.query;
  User.findOne({
    token: token
  }).then(user => {
    let latitude = [];
    let longitude = [];
    for (let i = 0; i < user.points.length; i++) {
      latitude.push(user.points[i].coordinates[0]);
      longitude.push(user.points[i].coordinates[1]);
    }
    res.status(200).json({ latitude: latitude, longitude: longitude });
    next();
  });
});

// delete specific user
router.post('/delete', (req, res, next) => {
  const { token, username } = req.query;
  User.findOne({
    token: token
  }).then(user => {
    User.deleteOne({ name: username }, function(err, result) {
      if (err) {
        res.status(200).json('username not found');
      } else {
        if (result.deletedCount == 0) res.status(200).json('username not found');
        else res.status(200).json(username + ' deleted');
      }
    });
  });
});
// mark user as infected
router.post('/markInfected', (req, res, next) => {
  const { token, username } = req.query;
  User.findOne({
    token: token
  }).then(user => {
    User.findOne({ name: username }).then(x => {
      if (x == null) res.status(200).json('username not found');
      else {
        x.infected = true;
        Object.assign(x, {
          infected: x.infected,
          infectedSince: Date.now()
        }).save();
        res.status(200).json(x.name + ' mark infected');
      }
    });
  });
});

router.post('/addSetInfected', (req, res, next) => {
  const { code } = req.query;
  const newCode = new InfectedCodes({ codes: code });
  newCode.save().then(x => {
    res.status(200).json('new Code: ' + code);
  });
});
router.post('/setInfected', (req, res, next) => {
  const { token, code } = req.query;

  User.findOne({
    token: token
  }).then(user => {
    InfectedCodes.findOneAndDelete({ codes: code }).then(code => {
      if (code == null) res.status(200).json('code not found');
      if (user == null) res.status(200).json('no user not found');
      user.infected = true;
      Object.assign(user, {
        infected: user.infected,
        infectedSince: Date.now()
      }).save();
      res.status(200).json(user.name + ' mark infected');
    });
  });
});

// mark user as Healthy
router.post('/markHealthy', (req, res, next) => {
  const { token, username } = req.query;
  User.findOne({
    token: token
  }).then(user => {
    User.findOne({ name: username }).then(x => {
      if (x == null) res.status(200).json('username not found');
      else {
        x.infected = false;
        Object.assign(x, { infected: x.infected, infectedSince: null }).save();
        res.status(200).json(x.name + ' mark healthy');
      }
    });
  });
});

// Verify Token
function verifyToken(req, res, next) {
  // Get auth header value
  const bearerHeader = req.headers['authorization'];
  // Check if bearer is undefined
  if (typeof bearerHeader !== 'undefined') {
    // Split at the space
    const bearer = bearerHeader.split(' ');

    // Get token from array
    const bearerToken = bearer[1];
    // Set the token
    req.token = bearerToken;
    // Next middleware
    next();
  } else {
    // Forbidden
    res.sendStatus(403);
  }
}

// add Request-essentials to the DB, or update already found

router.post('/essentials', verifyToken, (req, res, next) => {
  // owner location in 2 vars unterteilen

  let { objectName, category, quantity, price, ownerName, ownerLocation, email } = req.query;
  //dummy values to save into db
  //update the values if alresady exists
  Essentials.findOne({ name: objectName }).then(response => {
    // if name found , we need to push the new object to the list of essentialProperties
    elementExists = false;
    if (response) {
      // if name found , we need to push the new object to the list of essentialProperties
      for (let i = 0; i < response.essentialProperties.length; i++) {
        // if name found and from the same user  , we update the old values of this object
        if (response.essentialProperties[i].ownerName === ownerName) {
          response.essentialProperties[i].ownerLocation = ownerLocation;
          response.essentialProperties[i].price = price;
          response.essentialProperties[i].quantity = quantity;
          response.essentialProperties[i].email = email;

          response.save();
          elementExists = true;
          res.status(200).json({
            object: objectName,
            message: 'Object Name from this user already exists , the values are updated in DB ... '
          });
          break;
        }
      }
      // if the element found was not from the same user , we just push it into essentials of that category
      if (!elementExists) {
        response.essentialProperties.push({ quantity, price, ownerName, ownerLocation });
        response.save();
        res.status(200).json({
          object: objectName,
          message: 'Object Name already exists but from another User , and is now added to the DB ... '
        });
      }

      next();
    } else {
      // if elements was totaly new , we add it to DB
      var newEssential = new Essentials({
        name: objectName,
        category: category,
        essentialProperties: [
          {
            ownerName: ownerName,
            ownerLocation: ownerLocation,
            quantity: quantity,
            price: price,
            email: email
          }
        ]
      });
      newEssential.save();
      res.status(200).json({
        object: newEssential,
        message: 'Object Name is new and is added to the DB ...'
      });
      next();
    }
  });
});

// Add Offer-Essentials to the DB , or update already found
router.post('/essentials/offer', verifyToken, (req, res, next) => {
  // owner location in 2 vars unterteilen

  let { objectName, category, quantity, price, ownerName, ownerLocation, email } = req.query;
  //dummy values to save into db
  //update the values if alresady exists
  offerEssentials.findOne({ name: objectName }).then(response => {
    // if name found , we need to push the new object to the list of essentialProperties
    elementExists = false;
    if (response) {
      // if name found , we need to push the new object to the list of essentialProperties
      for (let i = 0; i < response.essentialProperties.length; i++) {
        // if name found and from the same user  , we update the old values of this object
        if (response.essentialProperties[i].ownerName === ownerName) {
          response.essentialProperties[i].ownerLocation = ownerLocation;
          response.essentialProperties[i].price = price;
          response.essentialProperties[i].quantity = quantity;
          response.essentialProperties[i].email = email;
          response.save();
          elementExists = true;
          res.status(200).json({
            object: objectName,
            message: 'Object Name from this user already exists , the values are updated in DB ... '
          });
          break;
        }
      }
      // if the element found was not from the same user , we just push it into essentials of that category
      if (!elementExists) {
        response.essentialProperties.push({ quantity, price, ownerName, ownerLocation, email });
        response.save();
        res.status(200).json({
          object: objectName,
          message: 'Object Name already exists but from another User , and is now added to the DB ... '
        });
      }

      next();
    } else {
      // if elements was totaly new , we add it to DB
      var newEssential = new offerEssentials({
        name: objectName,
        category: category,
        essentialProperties: [
          {
            ownerName: ownerName,
            ownerLocation: ownerLocation,
            quantity: quantity,
            price: price,
            email: email
          }
        ]
      });
      newEssential.save();
      res.status(200).json({
        object: newEssential,
        message: 'Object Name is new and is added to the DB ...'
      });
      next();
    }
  });
});

// you want to searrch for the Request-essentials on the map using the name
router.get('/essentials', verifyToken, (req, res, next) => {
  let { objectName } = req.query;
  Essentials.find({ name: new RegExp('^' + objectName, 'i') }).then(response => {
    if (response) {
      res.json({
        object: response,
        message: 'Essential found'
      });
    } else {
      res.json({
        object: null,
        message: 'Essential not found'
      });
    }
  });
});
// you want to searrch for the Offer-essentials on the map using the name

router.get('/essentials/offer', verifyToken, (req, res, next) => {
  let { objectName } = req.query;
  offerEssentials.find({ name: new RegExp('^' + objectName, 'i') }).then(response => {
    if (response) {
      res.json({
        object: response,
        message: 'Essential found'
      });
    } else {
      res.json({
        object: null,
        message: 'Essential not found'
      });
    }
  });
});

// Request-Essnetials of a specific user
router.get('/essentials/user', verifyToken, (req, res, next) => {
  let { userName } = req.query;
  Essentials.find(
    {
      'essentialProperties.ownerName': userName
    },
    { 'essentialProperties.ownerName.$': 1 }
  ).then(response => {
    if (response.length > 0) {
      res.json({
        object: response,
        message: 'User found'
      });
    } else {
      res.json({
        object: null,
        message: 'User not found'
      });
    }
  });
});

// get Offer-Essnetials of a specific user
router.get('/essentials/offer/user', verifyToken, (req, res, next) => {
  let { userName } = req.query;
  offerEssentials
    .find(
      {
        'essentialProperties.ownerName': userName
      },
      { 'essentialProperties.ownerName.$': 1 }
    )
    .then(response => {
      if (response.length > 0) {
        res.json({
          object: response,
          message: 'User found'
        });
      } else {
        res.json({
          object: null,
          message: 'User not found'
        });
      }
    });
});

// delete Request-Essential from DB for Users
router.delete('/essentials', verifyToken, (req, res, next) => {
  let { objectName, userName } = req.query;
  Essentials.findOne({
    name: objectName,
    'essentialProperties.ownerName': userName
  }).then(response => {
    if (response) {
      for (let i = 0; i < response.essentialProperties.length; i++) {
        if (response.essentialProperties[i].ownerName === userName) {
          response.essentialProperties.splice(i, 1);
          response.save();
        }
      }
      res.json({
        message: 'Object Found and deleted Successfully',
        response
      });
    } else {
      res.json({
        message: 'Object not Found'
      });
    }
  });
});
// delete Offer-Essential from DB for Users

router.delete('/essentials/offer', verifyToken, (req, res, next) => {
  let { objectName, userName } = req.query;
  offerEssentials
    .findOne({
      name: objectName,
      'essentialProperties.ownerName': userName
    })
    .then(response => {
      if (response) {
        for (let i = 0; i < response.essentialProperties.length; i++) {
          if (response.essentialProperties[i].ownerName === userName) {
            response.essentialProperties.splice(i, 1);
            response.save();
          }
        }
        res.json({
          message: 'Object Found and deleted Successfully',
          response
        });
      } else {
        res.json({
          message: 'Object not Found'
        });
      }
    });
});

// delete Request-Essential from DB for Admins
router.delete('/essentials/admin', verifyToken, (req, res, next) => {
  let { objectName, toDeleteUserName, userName } = req.query;
  Essentials.findOne({
    name: objectName
  }).then(response => {
    if (response) {
      User.findOne({ name: userName }).then(user => {
        if (user.isAdmin) {
          for (let i = 0; i < response.essentialProperties.length; i++) {
            if (response.essentialProperties[i].ownerName === toDeleteUserName) {
              response.essentialProperties.splice(i, 1);
              response.save();
            }
          }
          res.json({
            message: 'Object Found and deleted Successfully',
            response
          });
        } else {
          res.json({
            message: 'You are not allowed to delete the item'
          });
        }
      });
    }
  });
});

// delete Offer Essential from DB for Admins

router.delete('/essentials/offer/admin', verifyToken, (req, res, next) => {
  let { objectName, toDeleteUserName, userName } = req.query;
  offerEssentials
    .findOne({
      name: objectName
    })
    .then(response => {
      if (response) {
        User.findOne({ name: userName }).then(user => {
          if (user.isAdmin) {
            for (let i = 0; i < response.essentialProperties.length; i++) {
              if (response.essentialProperties[i].ownerName === toDeleteUserName) {
                response.essentialProperties.splice(i, 1);
                response.save();
              }
            }
            res.json({
              message: 'Object Found and deleted Successfully',
              response
            });
          } else {
            res.json({
              message: 'You are not allowed to delete the item'
            });
          }
        });
      }
    });
});
// ADMIN FUNCTIONS

function checkErrors(password, repeatedPassword, name) {
  let error = [];

  if (!name || !password || !repeatedPassword) {
    error.push('Please enter all fields');
  }

  if (password != repeatedPassword) {
    error.push('Passwords do not match');
  }

  if (password.length < 6) {
    error.push('Password must be at least 6 characters');
  }

  return error;
}

// For Admins :  add users as normal or admin Users
router.post('/admin/addUser', verifyToken, (req, res) => {
  const { requestorName, name, password, repeatedPassword, isAdmin } = req.query;
  let error = checkErrors(password, repeatedPassword, name);

  if (error.length > 0) {
    res.status(200).json({
      error,
      name,
      password,
      repeatedPassword
    });
  } else {
    User.findOne({ name: requestorName }).then(user => {
      if (user) {
        if (user.isAdmin) {
          User.findOne({ name: name }).then(user => {
            if (user) {
              error.push('User name already exists');
              res.status(400).json({ error, name, password, repeatedPassword });
            } else {
              const newUser = new User({
                name,
                password,
                token: null,
                latitude: [],
                longitude: [],
                dates: [],
                points: [],
                isAdmin: isAdmin
              });

              bcrypt.genSalt(10, (err, salt) => {
                bcrypt.hash(newUser.password, salt, (err, hash) => {
                  if (err) throw err;
                  newUser.password = hash;
                  newUser
                    .save()
                    .then(user => {
                      res.status(200).json({ message: 'User added ... ', error: [] });
                    })
                    .catch(err => console.log(err));
                });
              });
            }
          });
        } else {
          res.status(403).json({
            message: 'You are not authorized'
          });
        }
      } else {
        res.status(404).json({
          message: 'User not found'
        });
      }
    });
  }
});

// For Admins : Change a username
router.post('/admin/changeUsername', verifyToken, (req, res) => {
  const { requestorName, name, newName } = req.query;

  if (newName.length === 0) {
    res.status(200).json({
      error: ['New username is empty']
    });
  } else {
    User.findOne({ name: requestorName }).then(response => {
      if (response) {
        if (response.isAdmin) {
          User.findOne({ name: name }).then(user => {
            if (user) {
              // if username not already assigned
              User.findOne({ name: newName }).then(newName => {
                if (newName) {
                  res.status(404).json({
                    error: ['Username already used']
                  });
                } else {
                  user.name = newName;
                  user.save();
                  res.status(200).json({
                    message: 'Username updated',
                    error: []
                  });
                }
              });
            } else {
              res.status(404).json({
                error: ['User not found']
              });
            }
          });
        } else {
          res.status(403).json({
            error: ['You are not authorized']
          });
        }
      } else {
        res.status(404).json({
          error: ['User not found']
        });
      }
    });
  }
});
// For admins : mark user as admin
router.post('/admin/markAsAdmin', verifyToken, (req, res) => {
  const { requestorName, name, isAdmin } = req.query;

  if (name.length === 0) {
    res.status(200).json({
      error: ['username is empty']
    });
  } else {
    User.findOne({ name: requestorName }).then(response => {
      if (response) {
        if (response.isAdmin) {
          User.findOne({ name: name }).then(user => {
            if (user) {
              // if username not already assigned
              user.isAdmin = isAdmin;
              user.save();
              res.status(404).json({
                message: 'role changed',
                error: []
              });
            } else {
              res.status(404).json({
                error: ['User not found']
              });
            }
          });
        } else {
          res.status(403).json({
            error: ['You are not authorized']
          });
        }
      } else {
        res.status(404).json({
          error: ['User not found']
        });
      }
    });
  }
});
// For admins : get All users
router.get('/admin/getAllUsers', verifyToken, (req, res) => {
  let { requestorName } = req.query;
  User.findOne({ name: requestorName }).then(user => {
    if (user) {
      if (user.isAdmin) {
        User.find({}).then(response => {
          if (response) {
            res.status(200).send(response);
          } else {
            res.status(404).json({
              error: ['Error Occured']
            });
          }
        });
      } else {
        res.status(404).json({
          error: ['You are not authorized']
        });
      }
    } else {
      res.status(404).json({
        error: ['username not found']
      });
    }
  });
});

// For Admins : get All Request-essentials

router.get('/admin/getAllEssentials', verifyToken, (req, res) => {
  let { requestorName } = req.query;
  User.findOne({ name: requestorName }).then(user => {
    if (user) {
      Essentials.find({}).then(response => {
        if (response) {
          res.status(200).send(response);
        } else {
          res.status(404).json({
            error: ['Error Occured']
          });
        }
      });
    } else {
      res.status(404).json({
        error: ['username not found']
      });
    }
  });
});
// For Admins : get All Request-essentials

router.get('/admin/getAllEssentials/offer', verifyToken, (req, res) => {
  let { requestorName } = req.query;
  User.findOne({ name: requestorName }).then(user => {
    if (user) {
      if (user.isAdmin) {
        offerEssentials.find({}).then(response => {
          if (response) {
            res.status(200).send(response);
          } else {
            res.status(404).json({
              error: ['Error Occured']
            });
          }
        });
      } else {
        res.status(404).json({
          error: ['You are not authorized']
        });
      }
    } else {
      res.status(404).json({
        error: ['username not found']
      });
    }
  });
});

// Get doctors/CoronaTestCenters from DB

router.get('/getDoctors', (req, res) => {
  Doctors.find({}).then(response => {
    if (response) {
      res.status(200).send(response);
    } else {
      res.status(404).json({
        error: ['Error Occured']
      });
    }
  });
});

// Add new doctors/CoronaTestCenters to DB

router.post('/admin/addDoctor', (req, res) => {
  const { city, country, postcode, name, street, addrhousenumber, adress } = req.query;

  if (name.length === 0) {
    res.status(200).json({
      error: ['doctorname is empty']
    });
  } else {
    Doctors.find({}).then(response => {
      if (response) {
        const doc = new Doctors({
          type: 'Feature',
          'properties.addrcity': city,
          'properties.addrcountry': country,
          'properties.addrpostcode': postcode,
          'properties.addrstreet': street,
          'properties.name': name,
          'properties.addrhousenumber': addrhousenumber,
          adress: adress,
          id: 'way/746154322'
        });

        doc
          .save()
          .then(doct => {
            res.status(200).json({ error: [] });
          })
          .catch(err => console.log(err));
      } else {
        res.status(404).json({
          error: ['User not found']
        });
      }
    });
  }
});
// ADD a new STORE to DB
router.post('/addStore', (req, res) => {
  const { storeName, storeLocation } = req.query;

  if (storeName.length === 0) {
    res.status(200).json({
      error: ['storeName is empty']
    });
  } else {
    Stores.findOne({ name: storeName }).then(response => {
      if (response) {
        eror.push('Store name already exists');
        res.status(400).json({ error });
      } else {
        const storeValue = new Stores({
          name: storeName,
          location: storeLocation
        });

        storeValue
          .save()
          .then(doct => {
            res.status(200).json({ error: [] });
          })
          .catch(err => console.log(err));
      }
    });
  }
});
//get Stores
router.get('/getStores', (req, res) => {
  Stores.find({}).then(response => {
    if (response) {
      res.status(200).send(response);
    } else {
      res.status(404).json({
        error: ['Error Occured']
      });
    }
  });
});

// add Essentials to a specific Store

router.post('/addStoreEssentials', (req, res) => {
  const { storeName, essentialName, essentialQuantity, essentialPrice } = req.query;

  Stores.findOne({ name: storeName }).then(store => {
    if (store) {
      store.essentials.push({ name: essentialName, price: essentialPrice, quantity: essentialQuantity });

      store.save();
      res.status(200).json({
        error: []
      });
    } else {
      res.status(404).json({
        error: ['Store not found']
      });
    }
  });
});

// get User status : infected or healthy
router.get('/getUserStatus', (req, res) => {
  var status;
  var error = [];
  const { username } = req.query;
  User.findOne({ name: username }).then(user => {
    if (user) {
      status = user.infected;
      res.status(200).json({ error, status });
    } else {
      res.status(404).json({
        error: ['user not found'],
        status
      });
    }
  });
});
// get User Risk Factor

router.get('/getUserRiskFactor', (req, res) => {
  var riskFactor;
  var error = [];
  const { username } = req.query;
  User.findOne({ name: username }).then(user => {
    if (user) {
      riskFactor = user.risk;
      res.status(200).json({ error, riskFactor });
    } else {
      res.status(404).json({
        error: ['user not found'],
        riskFactor
      });
    }
  });
});

module.exports = router;

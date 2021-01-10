const express = require('express');
const expressLayouts = require('express-ejs-layouts');
const mongoose = require('mongoose');
const passport = require('passport');
const flash = require('connect-flash');
const session = require('express-session');

const app = express();

// Passport Config
require('./config/passport')(passport);

// DB Config
const db = require('./config/keys').mongoURI;

// Connect to MongoDB
var connectionOptions = {
  user: process.env.MONGO_INITDB_ROOT_USERNAME || 'root',
  host: process.env.MONGO_HOST || 'localhost',
  database: process.env.MONGO_INITDB_DATABASE || 'corona_app_db',
  password: process.env.MONGO_INITDB_ROOT_PASSWORD,
  port: process.env.MONGO_PORT || 27017
};
var url =
  'mongodb://' +
  connectionOptions.user +
  ':' +
  connectionOptions.password +
  '@' +
  connectionOptions.host +
  ':' +
  connectionOptions.port +
  '/' +
  connectionOptions.database;
mongoose
  .connect(url, { useNewUrlParser: true })
  .then(() => console.log('MongoDB Connected'))
  .catch(err => console.log(err));

// EJS
app.use(expressLayouts);
app.set('view engine', 'ejs');

// Express body parser
app.use(express.urlencoded({ extended: true }));

// Express session
app.use(
  session({
    secret: 'secret',
    resave: true,
    saveUninitialized: true
  })
);

// Passport middleware
app.use(passport.initialize());
app.use(passport.session());

// Connect flash
app.use(flash());

// Global variables
app.use(function(req, res, next) {
  res.locals.success_msg = req.flash('success_msg');
  res.locals.error_msg = req.flash('error_msg');
  res.locals.error = req.flash('error');
  next();
});

//Allow Url encoded bodys
app.use(express.urlencoded());

// Routes
app.use('/', require('./routes/index.js'));
app.use('/users', require('./routes/users.js'));
app.use('/gps', require('./routes/gps.js'));
//app.use('/users/admin', require('./routes/admin.js'));
const PORT = process.env.PORT || 8080;

app.listen(PORT, console.log(`Server started on port ${PORT}`));

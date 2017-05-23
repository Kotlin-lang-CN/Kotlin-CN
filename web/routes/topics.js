var express = require('express');
var router = express.Router();

/* GET topics page. */
router.get('/', function(req, res, next) {
  res.render('topics', { title: 'Topics' });
});

module.exports = router;
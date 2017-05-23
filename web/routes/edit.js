var express = require('express');
var router = express.Router();

/* GET edit article page. */
router.get('/', function(req, res, next) {
  res.render('edit', { title: 'Editing' });
});

module.exports = router;
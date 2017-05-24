var express = require('express');
var router = express.Router();

/* GET account page. */
router.get('/', function(req, res, next) {
    res.render('account', { title: 'Account' });
});

module.exports = router;

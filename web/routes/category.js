var express = require('express');
var router = express.Router();

/* GET edit article page. */
router.get('/', function (req, res, next) {
    res.render('category', {title: 'Category'});
});

module.exports = router;
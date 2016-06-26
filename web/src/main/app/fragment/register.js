var React = require('react');
var SignUpForm = require('./app/component/signup-form');
module.exports = function () {
    React.render(<SignUpForm/>, document.getElementById('app'))
};

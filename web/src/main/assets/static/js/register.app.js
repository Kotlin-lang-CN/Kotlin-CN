var React = require('react');
var SignUpForm = require('./app/component/signup-form');
module.exports = function () {
    React.render(React.createElement(SignUpForm, null), document.getElementById('app'))
};

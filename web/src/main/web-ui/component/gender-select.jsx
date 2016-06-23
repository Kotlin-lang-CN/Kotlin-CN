var React = require('react');
var GenderSelect = React.createClass({
    render: function () {
        return (
            <select onchange={this.props.handleSelect}>
                <option value="0">男</option>
                <option value="1">女</option>
            </select>
        )
    }
});
module.exports = GenderSelect;

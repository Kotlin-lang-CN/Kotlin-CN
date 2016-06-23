var React = require('react');
var GenderSelect = require('./component/gender-select');
var SignUpForm = React.createClass({
    getInitialState: function () {
        return {name: '', password: '', gender: ''}
    },
    handleChange: function (name, event) {
        var newState = {};
        newState[name] = event.target.value;
        this.setState(newState)
    },
    handleSelect: function (event) {
        this.setState({gender: event.target.value})
    },
    render: function () {
        return (
            <Form>
                <input type="text" placeholder="请输入用户名" onchange={this.handleChange.bind(this, 'name')}/>
                <input type="password" placeholder="请输入密码" onchange={this.handleChange.bind(this, 'password')}/>
                <GenderSelect handleSelect={this.handleSelect}/>
            </Form>
        )
    }
});
module.exports = SignUpForm;
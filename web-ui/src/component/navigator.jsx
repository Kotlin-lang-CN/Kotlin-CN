var React = require('react');
var Bootstrap = require('react-bootstrap');
var Auth = require('../framework/authorization.js');

var Navbar = Bootstrap.Navbar;
var Nav = Bootstrap.Nav;
var NavItem = Bootstrap.NavItem;
var NavDropdown = Bootstrap.NavDropdown;
var MenuItem = Bootstrap.MenuItem;

var Navigator = React.createClass({
    getInitialState: function () {
        try {
            if (Auth.isLogin())
                return {login: true, token: Auth.getToken(), profile: Auth.getProfile()};
            else
                return {login: false};
        } catch (e) {
            Auth.logout();
            return {login: false};
        }
    },
    render: function () {
        return <Navbar>
            <Navbar.Header>
                <Navbar.Brand>
                    <a href="/">Kotlin-CN</a>
                </Navbar.Brand>
                <Navbar.Toggle/>
            </Navbar.Header>
            <Navbar.Collapse>
                <Nav>
                    <NavItem href="community.html">社区</NavItem>
                    <NavItem href="document.html">中文教程</NavItem>
                </Nav>
                <Nav pullRight>{this.getSideMenu()}</Nav>
            </Navbar.Collapse>
        </Navbar>
    },
    getSideMenu: function () {
        var login = "https://github.com/login/oauth/authorize?" +
            "client_id=56ac3aefad86b012320e&" +
            "redirect_uri=http://localhost:8080/account/github&" +
            "scope=user&" +
            "state=kotlin_china";
        if (this.state.login) {
            return <NavDropdown title={this.state.profile.name} id="basic-nav-dropdown">
                <MenuItem href="publish.html">写文章</MenuItem>
                <MenuItem divider/>
                <MenuItem onClick={this.logout}>注销</MenuItem>
            </NavDropdown>
        } else {
            return <NavItem href={login} onClick={this.login}>使用Github登录</NavItem>
        }
    },
    login: function () {
    },
    logout: function () {
        Auth.logout();
        this.setState({login: false, profile: null, token: null})
    }
});

module.exports = Navigator;

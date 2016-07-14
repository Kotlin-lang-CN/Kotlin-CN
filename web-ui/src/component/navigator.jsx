const React = require('react'),
    Bootstrap = require('react-bootstrap'),
    Auth = require('../framework/authorization.js'),

    Navbar = Bootstrap.Navbar,
    Nav = Bootstrap.Nav,
    NavItem = Bootstrap.NavItem,
    NavDropdown = Bootstrap.NavDropdown,
    MenuItem = Bootstrap.MenuItem,

    login = "https://github.com/login/oauth/authorize?" +
        "client_id=56ac3aefad86b012320e&" +
        "redirect_uri=http://localhost:8080/account/github&" +
        "scope=user&" +
        "state=kotlin_china";

const Navigator = React.createClass({
    getDefaultProps: function () {
        if (Auth.isLogin()) {
            return {
                sideMenus: (
                    <NavDropdown title={Auth.getProfile().login} id="basic-nav-dropdown">
                        <MenuItem href="publish.html">写文章</MenuItem>
                        <MenuItem divider/>
                        <MenuItem onClick={() => {
                            Auth.logout();
                            window.open(document.documentURI, '_self');
                        }}>注销</MenuItem>
                    </NavDropdown>
                )
            }
        } else {
            return {sideMenus: <NavItem href={login}>使用Github登录</NavItem>}
        }
    },
    render: function () {
        return <Navbar>
            <Navbar.Header>
                <Navbar.Brand><a href="/">Kotlin-CN</a></Navbar.Brand>
                <Navbar.Toggle/>
            </Navbar.Header>
            <Navbar.Collapse>
                <Nav>
                    <NavItem href="community.html">社区</NavItem>
                    <NavItem href="document.html">中文教程</NavItem>
                </Nav>
                <Nav pullRight>{this.props.sideMenus}</Nav>
            </Navbar.Collapse>
        </Navbar>
    }
});

module.exports = Navigator;

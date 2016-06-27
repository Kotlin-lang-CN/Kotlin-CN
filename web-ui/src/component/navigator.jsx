var React = require('react');
var Bootstrap = require('react-bootstrap');
var Navbar = Bootstrap.Navbar;
var Nav = Bootstrap.Nav;
var MenuItem = Bootstrap.MenuItem;
var NavItem = Bootstrap.NavItem;
var NavDropdown = Bootstrap.NavDropdown;
var FormGroup = Bootstrap.FormGroup;
var FormControl = Bootstrap.FormControl;
var Button = Bootstrap.Button;

var AccountMenus = React.createClass({
    render: function () {
        return <Nav pullRight>
            <NavDropdown eventKey={4} title="chpengzh">
                <MenuItem eventKey={4.1} href="/document#my-account">我的账号</MenuItem>
                <MenuItem eventKey={4.2} href="/document#sign-out">退出登录</MenuItem>
                <MenuItem divider/>
                <MenuItem eventKey={4.3} href="/document#publish">有话要说</MenuItem>
            </NavDropdown>
        </Nav>
    }
});

var SearchMenu = React.createClass({
    render: function () {
        return <Navbar.Form pullRight>
            <FormGroup>
                <FormControl type="text" placeholder="Kotlin 中的运算符重载"/>
            </FormGroup>
            {' '}
            <Button type="submit"><span className="glyphicon glyphicon-search"/></Button>
        </Navbar.Form>
    }
});

module.exports = React.createClass({
    render: function () {
        return <Navbar staticTop="true">
            <Navbar.Header>
                <Navbar.Brand><a href="#">Kotlin CN</a></Navbar.Brand>
            </Navbar.Header>
            <Navbar.Collapse>
                <Nav>
                    <NavItem eventKey={1} href="/">众说纷纭</NavItem>
                    <NavDropdown eventKey={2} title="快速开始">
                        <MenuItem eventKey={2.1} href="/document#why-kotlin">为什么 Kotlin?</MenuItem>
                        <MenuItem divider/>
                        <MenuItem eventKey={2.2} href="/document#get-start">从这里开始</MenuItem>
                        <MenuItem eventKey={2.3} href="/document#primary">基本规则</MenuItem>
                        <MenuItem divider/>
                        <MenuItem eventKey={2.4} href="/document#className-and-object">类与对象</MenuItem>
                        <MenuItem eventKey={2.5} href="/document#function-and-lambda">函数与Lambda</MenuItem>
                        <MenuItem divider/>
                        <MenuItem eventKey={2.6} href="/document#from-java-to-kotlin">从 Java 到 Kotlin</MenuItem>
                    </NavDropdown>
                    <NavItem eventKey={3} href="/everyday">每日一Kotlin</NavItem>
                </Nav>
                <AccountMenus/>
                <SearchMenu/>
            </Navbar.Collapse>
        </Navbar>
    }
});
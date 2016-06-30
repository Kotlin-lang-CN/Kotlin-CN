var React = require('react');
var Bootstrap = require('react-bootstrap');
var Cookies = require('js-cookie');
var Navbar = Bootstrap.Navbar;
var Nav = Bootstrap.Nav;
var MenuItem = Bootstrap.MenuItem;
var NavItem = Bootstrap.NavItem;
var NavDropdown = Bootstrap.NavDropdown;
var FormGroup = Bootstrap.FormGroup;
var FormControl = Bootstrap.FormControl;
var Button = Bootstrap.Button;
var Modal = Bootstrap.Modal;
var ControlLabel = Bootstrap.ControlLabel;

var AccountMemu = React.createClass({
    getInitialState: function () {
        return {
            username: this.props.username,
            token: this.props.token
        }
    },
    render: function () {
        return <Nav pullRight>
            <NavDropdown title={this.state.username}>
                <MenuItem href="javascript:void(0);">我的账号</MenuItem>
                <MenuItem href="javascript:void(0);">退出登录</MenuItem>
                <MenuItem divider/>
                <MenuItem href="javascript:void(0);">有话要说</MenuItem>
            </NavDropdown>
        </Nav>
    }
});

var GuestMenu = React.createClass({
    getInitialState: function () {
        return {
            showSignIn: false,
            showSignUp: false,
            username: '',
            password: ''
        }
    },
    render: function () {
        return <NavDropdown title="未登录">
            <MenuItem href="javascript:void(0);"
                      onClick={this.showSignIn}>我要登录</MenuItem>
            <MenuItem href="javascript:void(0);"
                      onClick={this.showSignUp}>注册账号</MenuItem>
            <Modal show={this.state.showSignIn || this.state.showSignUp } onHide={this.close}>
                <Modal.Header>
                    <Modal.Title>{this.state.showSignIn ? '用户登录' : '用户注册'}</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <FormGroup controlId="formControlsText">
                        <ControlLabel>用户名</ControlLabel>
                        <FormControl type="text" placeholder="邮箱账号" value={this.state.username}/>
                    </FormGroup>
                    <FormGroup controlId="formControlsEmail">
                        <ControlLabel>密码</ControlLabel>
                        <FormControl type="password" placeholder="密码! 密码!" value={this.state.password}/>
                    </FormGroup>
                </Modal.Body>
                {this.getFooter()}
            </Modal>
        </NavDropdown>
    },
    getFooter: function () {
        if (this.state.showSignIn) {
            return <Modal.Footer>
                <Button onClick={this.dismissSignIn}>等会儿</Button>
                <Button onClick={this.dismissSignIn}>恩, 我要登录</Button>
                <Button href="/rest/sign-in/github">使用Gitbub登录</Button>
            </Modal.Footer>
        } else {
            return <Modal.Footer>
                <Button onClick={this.dismissSignUp}>取消注册</Button>
                <Button onClick={this.dismissSignUp}>确认注册</Button>
            </Modal.Footer>
        }
    },
    showSignIn: function () {
        this.setState({showSignIn: true})
    },
    showSignUp: function () {
        this.setState({showSignUp: true})
    },
    dismissSignIn: function () {
        this.setState({showSignIn: false})
    },
    dismissSignUp: function () {
        this.setState({showSignUp: false})
    }
});

var SearchMenu = React.createClass({
    render: function () {
        return <Navbar.Form pullRight>
            <FormGroup bsSize="sm">
                <FormControl type="text" placeholder="Kotlin 中的运算符重载"/>
            </FormGroup>
            {' '}
            <Button type="submit"><span className="glyphicon glyphicon-search"/></Button>
        </Navbar.Form>
    }
});

var Navigator = React.createClass({
    getInitialState: function () {
        return {cookie: Cookies.get("kotlin_cn")}
    },
    render: function () {
        return <Navbar>
            <Navbar.Header>
                <Navbar.Brand><a href="index.html">Kotlin CN</a></Navbar.Brand>
                <Navbar.Toggle />
            </Navbar.Header>
            <Navbar.Collapse>
                <Nav>
                    <NavItem href="community.html">社区</NavItem>
                    <NavDropdown title="快速开始">
                        <MenuItem header>宝贝, 看这里</MenuItem>
                        <MenuItem href="document.html#what_is_kotlin">什么是Kotlin</MenuItem>
                        <MenuItem href="document.html#what_is_kotlin">为什么Kotlin?</MenuItem>
                        <MenuItem divider/>
                        <MenuItem header>都是套路...</MenuItem>
                        <MenuItem href="document.html#what_is_kotlin">基本数据类型</MenuItem>
                        <MenuItem href="document.html#what_is_kotlin">类与对象</MenuItem>
                        <MenuItem href="document.html#what_is_kotlin">函数与Lambda</MenuItem>
                        <MenuItem divider/>
                        <MenuItem header>再见 Java</MenuItem>
                        <MenuItem href="document.html#what_is_kotlin">从 Java 到 Kotlin</MenuItem>
                        <MenuItem href="document.html#what_is_kotlin">第一个Android App</MenuItem>
                        <MenuItem href="document.html#what_is_kotlin">第一个Web App</MenuItem>
                    </NavDropdown>
                    <NavItem href="everyday.html">每日一Kotlin</NavItem>
                </Nav>
                <Nav pullRight>
                    {this.state.cookie == null ? <GuestMenu/> : <AccountMemu
                        username={this.state.cookie.username}
                        token={this.state.cookie.token}/>}
                </Nav>
                <SearchMenu/>
            </Navbar.Collapse>
        </Navbar>
    }
});

module.exports = Navigator;
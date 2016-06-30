var React = require('react');
var Bootstrap = require('react-bootstrap');
var Jumbotron = Bootstrap.Jumbotron;
var Col = Bootstrap.Col;
var Thumbnail = Bootstrap.Thumbnail;

var SectionHeader = React.createClass({
    render: function () {
        return <Jumbotron>
            <h1>Kotlin China</h1>
            <br/>
            <p>Kotlin 是一个由 JetBrains 开发的JVM语言, 它支持与 Java 函数库之间的无缝调用</p>
            <p>现在被越来越多的使用于 Android App 和 浏览器 开发</p>
            <br/><br/>
            <p>
                <small>我们致力于提供最好的Kotlin中文教程</small>
            </p>
            <p>
                <small>共建最潮流的Kotlin中文社区</small>
            </p>
            <Bootstrap.Pager>
                <Bootstrap.PageItem
                    next href="http://try.kotlinlang.org"
                    target="_blank">在线尝试 &rarr;
                </Bootstrap.PageItem>
            </Bootstrap.Pager>
        </Jumbotron>
    }
});

var SectionNavigatorTitle = React.createClass({
    render: function () {
        return <Bootstrap.PageHeader>在这里
            <small> 我们竭诚为您提供</small>
        </Bootstrap.PageHeader>
    }
});

var SectionNavigator = React.createClass({
    render: function () {
        return <Bootstrap.Row>
            <Col xs={4} md={3}>
                <Thumbnail src="https://react-bootstrap.github.io/assets/thumbnaildiv.png" alt="242x200">
                    <h3>更优秀的技术文档</h3>
                    <p>每日一篇Kotlin小贴士</p>
                    <p>可以,这很Kotlin!</p>
                </Thumbnail>
            </Col>
            <Col xs={4} md={3}>
                <Thumbnail src="https://react-bootstrap.github.io/assets/thumbnaildiv.png" alt="242x200">
                    <h3>更良好的社区生态</h3>
                    <p>加入我们</p>
                    <p>有问必有答</p>
                </Thumbnail>
            </Col>
            <Col xs={4} md={3}>
                <Thumbnail src="https://react-bootstrap.github.io/assets/thumbnaildiv.png" alt="242x200">
                    <h3>更赞的中文教程</h3>
                    <p>我们系统地收纳并翻译 Kotlin 教程</p>
                    <p>欢迎参与 Kotlin 学习</p>
                </Thumbnail>
            </Col>
            <Col xs={4} md={3}>
                <Thumbnail src="https://react-bootstrap.github.io/assets/thumbnaildiv.png" alt="242x200">
                    <h3>更多的 Kotlin 招聘</h3>
                    <p>有求必有供, 有问必有答</p>
                    <p>招贤纳士</p>
                </Thumbnail>
            </Col>
        </Bootstrap.Row>
    }
});

var MainHeader = React.createClass({
    render: function () {
        return <Bootstrap.Grid>
            <SectionHeader/>
            <SectionNavigatorTitle/>
            <SectionNavigator/>
        </Bootstrap.Grid>
    }
});

module.exports = MainHeader;
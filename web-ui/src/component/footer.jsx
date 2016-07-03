var React = require('react');
var Bootstrap = require('react-bootstrap');
var Row = Bootstrap.Row;
var Grid = Bootstrap.Grid;

var Footer = React.createClass({
    render: function () {
        //noinspection CheckTagEmptyBody
        return <Grid className="text-center">
            <Bootstrap.PageHeader/>
            <Row>
                <i className="kuma-icon kuma-icon-tag"></i>
                <a href="https://github.com/Kotlin-lang-CN/Kotlin-CN.git" target="_blank">本项目</a> 后端服务完全使用 <a
                href="http://kotlin-lang.org/" target="_blank">Kotlin</a> 编写
            </Row>
            <Row>
                <span className="copyright"> © Since 2016. All rights reserved by <a
                    href="http://kotlin-cn.tech" target="_blank">kotlin-cn.tech</a></span><br/><br/>
            </Row>
        </Grid>
    }
});

module.exports = Footer;
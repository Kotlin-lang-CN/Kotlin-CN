var React = require('react');
var Bootstrap = require('react-bootstrap');
var Grid = Bootstrap.Grid;
var Row = Bootstrap.Row;
var PageHeader = Bootstrap.PageHeader;

var Footer = React.createClass({
    render: function () {
        //noinspection CheckTagEmptyBody
        return <footer className="footer common-footer text-center" role="contentinfo">
            <Grid>
                <Row>
                    <PageHeader/>
                    <span className="glyphicon glyphicon-console" aria-hidden="true"></span> 本项目完全使用 <a
                    href="https://github.com/Kotlin-lang-CN/Kotlin-CN.git">Kotlin</a> 编写
                </Row>
                <Row>
                    <span className="copyright"> © Since 2016. All rights reserved by <a
                        href="http://kotlin-cn.org">kotlin-cn.org</a></span>
                </Row>
            </Grid>
        </footer>
    }
});

module.exports = Footer;
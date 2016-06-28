var React = require('react');
var Grid = require('react-bootstrap').Grid;
var Row = require('react-bootstrap').Row;

var Footer = React.createClass({
    render: function () {
        //noinspection CheckTagEmptyBody
        return <footer className="bs-docs-footer text-center" role="contentinfo">
            <Grid>
                <Row>
                    <span className="glyphicon glyphicon-console" aria-hidden="true"></span> 本项目完全使用 <a
                    href="http://kotlinlang.org/">Kotlin</a> 编写
                </Row>
                <Row>
                    <span className="copyright"> © Since 2016. All rights reserved by <a
                        href="http://kotlin-cn.tech">kotlin-cn.tech</a></span>
                </Row>
            </Grid>
        </footer>
    }
});

module.exports = Footer;
var React = require('react');

module.exports = React.createClass({
    render: function () {
        //noinspection CheckTagEmptyBody
        return (
            <footer className="bs-docs-footer text-center" role="contentinfo">
                <div className="container">
                    <p><span className="glyphicon glyphicon-console" aria-hidden="true"></span> 本项目完全使用 <a
                        href="http://kotlinlang.org/">Kotlin</a> 编写</p>
                    <p className="copyright">© Since 2016. All rights reserved by <a
                        href="http://kotlin-cn.tech">kotlin-cn.tech</a></p>
                </div>
            </footer>
        )
    }
});
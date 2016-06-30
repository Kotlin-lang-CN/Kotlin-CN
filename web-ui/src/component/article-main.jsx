var React = require('react');
var Bootstrap = require('react-bootstrap');
var showdown = require('showdown');
var Col = Bootstrap.Col;

var ArticleTitle = React.createClass({
    render: function () {
        return <Bootstrap.Row>
            <Col sm={12} md={6}>
                <Bootstrap.PageHeader>简单易用的远程方法调用库 EasyRPC</Bootstrap.PageHeader>
            </Col>
        </Bootstrap.Row>
    }
});

var ArticleBody = React.createClass({
    converter: new showdown.Converter(),
    getInitialState: function () {
        return {
            'data': '#server\n监听一个address\n##由client\n通过该ip addr<script>alert("jb")</script>'
        }
    },
    render: function () {
        return <Bootstrap.Row>
            <div dangerouslySetInnerHTML={{__html: this.converter.makeHtml(this.state.data)}}></div>
        </Bootstrap.Row>
    }
});

var ArticleMeta = React.createClass({
    render: function () {
        return <Bootstrap.Row>
            <Col sm={12} md={6}>
                <Bootstrap.PageHeader/>
                <p>by. chpengzh 一小时之前</p>
            </Col>
        </Bootstrap.Row>
    }
});

var ArticleMain = React.createClass({
    render: function () {
        return <Bootstrap.Grid>
            <ArticleTitle/>
            <ArticleBody/>
            <ArticleMeta/>
        </Bootstrap.Grid>
    }
});

module.exports = ArticleMain;
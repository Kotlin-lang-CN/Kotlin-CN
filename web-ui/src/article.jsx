const ReactDOM = require('react-dom'),
    React = require('react'),
    Bootstrap = require('react-bootstrap'),
    Footer = require('./component/footer.jsx'),
    Navigator = require('./component/navigator.jsx'),
    Conf = require('./framework/config.js'),
    FriendLink = require('./component/friend-link.jsx'),
    Req = require('./framework/ajax.js');

const Row = Bootstrap.Row,
    Grid = Bootstrap.Grid,
    Col = Bootstrap.Col,
    Media = Bootstrap.Media;

const Param = function getParameterByName(name, url) {
    if (!url) url = window.location.href;
    name = name.replace(/[\[\]]/g, "\\$&");
    var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, " "));
};

const MarkdownArticle = React.createClass({
    getDefaultProps: function () {
        return {aid: Param('aid')}
    },
    getInitialState: function () {
        //标题，作者， 作者头像，创作时间，文章内容和评论数量
        return {
            title: '',
            author: '',
            content: "##别急, 文章正在加载...",
            html_url: '',
            avatar_url: '',
            create_time: '',
            email: '',
            category: '',
            comment: 0,
            flower: 0
        }
    },
    componentWillMount: function () {
        const _this = this;
        Req.get({
            url: '/article/' + this.props.aid,
            success: function (resp) {
                _this.setState(resp.data)
            },
            fail: function (error) {
                console.log(error)
            }
        })
    },
    render: function () {
        return (
            <Media><Media.Body>
                <div id="articlePreview" className="markdown-block" style={{paddingRight: 15, wordBreak: 'break-all'}}
                     dangerouslySetInnerHTML={{__html: this.renderContent()}}></div>
            </Media.Body></Media>
        )
    },
    renderContent: function () {
        const title = this.state.title == '' ? '' :
        "#" + this.state.title + '\n';
        return Conf.MarkdownConverter.makeHtml(title + this.state.content);
    }
});

ReactDOM.render((
    <div>
        <Navigator/>
        <Grid><Row className="show-grid">
            <Col sm={12} md={9} className="markdown-body" style={{ paddingRight: 0 }}>
                <MarkdownArticle/>
            </Col>
            <Col xs={6} md={3}><FriendLink/></Col>
        </Row></Grid>
        <Footer/>
    </div>
), document.body);
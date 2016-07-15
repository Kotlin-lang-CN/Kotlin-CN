const React = require('react'),
    ReactDOM = require('react-dom'),
    Bootstrap = require('react-bootstrap'),
    $ = require('jquery'),
    Row = Bootstrap.Row,
    Grid = Bootstrap.Grid,
    Col = Bootstrap.Col,
    Media = Bootstrap.Media,
    NavDropdown = Bootstrap.NavDropdown,
    MenuItem = Bootstrap.MenuItem,
    Conf = require('./framework/config.js'),
    Req = require('./framework/ajax.js'),
    Footer = require('./component/footer.jsx'),
    Navigator = require('./component/navigator.jsx'),
    Auth = require('./framework/authorization.js');



const MDEditor = React.createClass({
    getSideMenus: function () {
        return [
            <NavDropdown title={this.state.category == undefined ? '选择类型' : Conf.ArticleCategory[this.state.category]}>
                <MenuItem onClick={() => this.setState({category: 'question'})}>寻求解答</MenuItem>
                <MenuItem divider/>
                <MenuItem onClick={() => this.setState({category: 'beginner'})}>入门者说</MenuItem>
                <MenuItem divider/>
                <MenuItem onClick={() => this.setState({category: 'share'})}>技术分享</MenuItem>
                <MenuItem divider/>
                <MenuItem onClick={() => this.setState({category: 'translation'})}>文章翻译</MenuItem>
                <MenuItem divider/>
                <MenuItem onClick={() => this.setState({category: 'other'})}>社区杂谈</MenuItem>
            </NavDropdown>,
            <NavDropdown title={Auth.getProfile().login} id="side-menu-dropdown">
                <MenuItem onClick={this.cancelEdit}>放弃编辑</MenuItem>
                <MenuItem onClick={this.publishArticle}>发布文章</MenuItem>
                <MenuItem divider/>
                <MenuItem onClick={this.logout}>注销</MenuItem>
            </NavDropdown>
        ]
    },
    getInitialState: function () {
        if (!Auth.isLogin()) window.open('/community.html', '_self');
        return {title: '', content: '', category: undefined}
    },
    renderPreview: function () {
        const title = this.state.title == '' ? '' :
        "#" + this.state.title + '\n';
        return Conf.MarkdownConverter.makeHtml(title + this.state.content);
    },
    cancelEdit: function () {

    },
    publishArticle: function () {
        console.log("publish!");
        Req.post({
            url: '/article/publish',
            data: {
                title: this.refs.titleInput.value,
                content: this.refs.contentInput.value,
                category: this.state.category
            }
        })
    },
    logout: function () {
        Auth.logout();
        window.open('/community.html', '_self');
    },
    render: function () {
        return (
            <div>
                <Navigator sideMenus={this.getSideMenus()}/>
                <Grid><Row>
                    <Col sm={12} md={6}>
                        <div className="form-group"><label className="control-label" for="articleTitle">文章标题</label>
                            <input type="text" placeholder="请输入文章标题" ref="titleInput"
                                   className="form-control" id="articleTitle"
                                   onChange={() => this.setState({title: this.refs.titleInput.value})}/>
                        </div>
                        <div className="form-group">
                            <label className="control-label" for="articleContent">文章内容</label>
                        <textarea style={{resize: 'none'}} placeholder="请输入文章内容" ref="contentInput"
                                  className="form-control" id="articleContent"
                                  onChange={() => this.setState({content: this.refs.contentInput.value})}/>
                        </div>
                    </Col>
                    <Col sm={12} md={6} className="markdown-body"
                         style={{borderStyle: 'solid',borderWidth: 0.5,borderRadius: 3, paddingRight: 0 }}>
                        <Media><Media.Body>
                            <div id="articlePreview" className="markdown-block"
                                 style={{overflowY: 'auto', paddingRight: 15, wordBreak: 'break-all'}}
                                 dangerouslySetInnerHTML={{__html: this.renderPreview()}}></div>
                        </Media.Body></Media>
                    </Col>
                </Row></Grid>
            </div>
        )
    }
});

ReactDOM.render((
    <div>
        <MDEditor/>
        <Footer/>
    </div>
), document.body);

// textarea full screen
$(document).ready(function () {
    $('#articleContent').height($(window).height() - 340);
    $('#articlePreview').height($(window).height() - 273 - 525 + 539 + 30);
});

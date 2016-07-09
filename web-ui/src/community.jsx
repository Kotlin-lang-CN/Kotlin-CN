var ReactDOM = require('react-dom');
var React = require('react');
var Bootstrap = require('react-bootstrap');
var Footer = require('./component/footer.jsx');
var Navigator = require('./component/navigator.jsx');
var FriendNavigator = require('./component/friend-navigator.jsx');

var Badge = Bootstrap.Badge;
var Label = Bootstrap.Label;
var Pagination = Bootstrap.Pagination;
var Row = Bootstrap.Row;
var Grid = Bootstrap.Grid;
var Col = Bootstrap.Col;
var Media = Bootstrap.Media;
var ListGroup = Bootstrap.ListGroup;
var ListGroupItem = Bootstrap.ListGroupItem;
var Nav = Bootstrap.Nav;
var NavItem = Bootstrap.NavItem;

const CommunityList = React.createClass({
    getInitialState: function () {
        return {
            tab: 1, 
            page: 1, 
            articles: [{"create_time":"2 天 前","avatar_url":"https://avatars.githubusercontent.com/u/7821898?v=3","author":7821898,"html_url":"https://github.com/chpengzh","name":"手不要乱摸","comment":0,"title":"我是来测试发送的4","category":"tag3","aid":4,"email":"chpengzh@foxmail.com","flower":0},{"create_time":"2 天 前","avatar_url":"https://avatars.githubusercontent.com/u/7821898?v=3","author":7821898,"html_url":"https://github.com/chpengzh","name":"手不要乱摸","comment":0,"title":"我是来测试发送的3","category":"tag3","aid":3,"email":"chpengzh@foxmail.com","flower":1},{"create_time":"2 天 前","avatar_url":"https://avatars.githubusercontent.com/u/7821898?v=3","author":7821898,"html_url":"https://github.com/chpengzh","name":"手不要乱摸","comment":2,"title":"我是来测试发送的2","category":"tag2","aid":2,"email":"chpengzh@foxmail.com","flower":0},{"create_time":"2 天 前","avatar_url":"https://avatars.githubusercontent.com/u/7821898?v=3","author":7821898,"html_url":"https://github.com/chpengzh","name":"手不要乱摸","comment":0,"title":"我是来测试发送的","category":"tag1","aid":1,"email":"chpengzh@foxmail.com","flower":0}],
            nav: [ 
                {tab: '最新更新', category: 'all'},
                {tab: '问答板',  category: 'question'},
                {tab: '入门者说', category: 'beginner'},
                {tab: '技术分享', category: 'share'},
                {tab: '精品区', category: 'awesome'}
            ],
            loading: true
        }
    },
    render: function () {
        return <Row>
            <Col sm={12} md={9}>
                <Nav bsStyle="tabs" activeKey={this.state.tab} style={{marginBottom:5}}>{this.getNavList()}</Nav>
                <ListGroup>{this.getArticleView()}</ListGroup>
                <Pagination prev next first last ellipsis boundaryLinks
                            items={20} maxButtons={5} activePage={this.state.page}
                            onSelect={this.handleSelectPage}/>
            </Col>
            <Col sm={6} md={3}><FriendNavigator/></Col>
        </Row>
    },
    handleSelectPage: function (eventKey) {
        this.setState({page: eventKey});
    },
    getNavList: function () {
        var view = [];
        for (var i = 1; i <= this.state.nav.length; i++) {
            const index = i;
            view.push(
                <NavItem className={(this.state.tab == index) ? 'active' : ''} onClick={() => this.setState({tab: index})}>
                    {this.state.nav[index - 1].tab}
                </NavItem>
            );
        }
        return view
    },
    getArticleView: function () {
        var view = [];
        for (var i = 1; i <= this.state.articles.length; i++) {
            const index = i;
            const article = this.state.articles[index - 1];
            view.push(
                <ListGroupItem href={article.html_url}>
                    <Media>
                        <Media.Left align="top">
                            <img width={64} height={64} src={article.avatar_url} alt="Avatar"/>
                        </Media.Left>
                        <Media.Body>
                            <Media.Heading style={{'marginTop': '10px'}}>{article.title}</Media.Heading>
                            <small><Label bsStyle="success">{article.create_time}</Label> • chpengzh • 赞 <Badge
                                style={{padding: '1px 7px'}}>{article.flower}</Badge> • 评论 <Badge
                                style={{padding: '1px 7px'}}>{article.comment}</Badge></small>
                        </Media.Body>
                    </Media>
                </ListGroupItem>
            );
        }
        return view;
    }
});

ReactDOM.render((
    <div>
        <Navigator/>
        <Grid>
            <CommunityList/>
        </Grid>
        <Footer/>
    </div>
), document.body);

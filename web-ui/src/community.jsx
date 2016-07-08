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
        return {tab: 1, page: 1, articles: [], loading: true}
    },
    render: function () {
        return <Row>
            <Col sm={12} md={9}>
                <Nav bsStyle="tabs" activeKey={2} style={{marginBottom:5}}>
                    <NavItem eventKey={1} onClick={this.handleSelectTab}>最新更新</NavItem>
                    <NavItem eventKey={2} onClick={this.handleSelectTab}>问答板</NavItem>
                    <NavItem eventKey={3} onClick={this.handleSelectTab}>入门者说</NavItem>
                    <NavItem eventKey={4} onClick={this.handleSelectTab}>技术分享</NavItem>
                    <NavItem eventKey={5} onClick={this.handleSelectTab}>每日精品</NavItem>
                </Nav>
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
    handleSelectTab: function (eventKey) {
        this.setState({tag: eventKey})
    },
    getArticleView: function () {
        var view = [];
        this.state.articles.forEach(function (article) {
            view.push(
                <ListGroupItem href="/">
                    <Media>
                        <Media.Left align="top">
                            <img width={64} height={64}
                                 src={article.get('avatar_url')}
                                 alt="Image"/>
                        </Media.Left>
                        <Media.Body>
                            <Media.Heading style={{'marginTop': '10px'}}>article.get('title')</Media.Heading>
                            <small><Label bsStyle="success">片刻之前</Label> • chpengzh • 赞 <Badge
                                style={{padding: '1px 7px'}}>10</Badge> • 评论 <Badge
                                style={{padding: '1px 7px'}}>20</Badge></small>
                        </Media.Body>
                    </Media>
                </ListGroupItem>
            )
        })
    }
});


ReactDOM.render((
    <div>
        <Navigator/>
        <Grid>
            <CommunityNavigation/>
            <CommunityList/>
        </Grid>
        <Footer/>
    </div>
), document.body);
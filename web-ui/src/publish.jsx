var React = require('react')
var ReactDOM = require('react-dom');
var Bootstrap = require('react-bootstrap');
var $ = require('jquery');
var Footer = require('./component/footer.jsx');
var Navigator = require('./component/navigator.jsx');

var Row = Bootstrap.Row;
var Grid = Bootstrap.Grid;
var Col = Bootstrap.Col;
var FormGroup = Bootstrap.FormGroup;
var ControlLabel = Bootstrap.ControlLabel;
var FormControl = Bootstrap.FormControl;
var Media = Bootstrap.Media;

const MDEditor = React.createClass({
    getInitialState: function () {
        return {title: '', content: ''}
    },
    render: function() {
        return (
            <Row>
            <Col sm={12} md={6}>
            <FormGroup controlId="articleTitle">
            <ControlLabel>文章标题</ControlLabel>
            <FormControl type="text" inputRef={ref => this.setState({title: ref})} placeholder="请输入文章标题"/>
            </FormGroup>
            <FormGroup controlId="articleContent">
            <ControlLabel>文章内容</ControlLabel>
            <FormControl componentClass="textarea" inputRef={ref => this.setState({content: ref})} 
            placeholder="请输入文章内容" style={{resize: 'none', height: '100%'}}/>
            </FormGroup>       
            </Col>
            <Col sm={12} md={6}>
            </Col>
            </Row>
        )
    }
});

ReactDOM.render((
    <div>
    <Navigator/>
    <Grid><MDEditor/></Grid>
    <Footer/>
    </div>
), document.body);

// textarea full screen
$(document).ready(function () {
    $('#articleContent').height($(window).height() - 340);
    $('#articlePreview').height($(window).height() - 266);
});

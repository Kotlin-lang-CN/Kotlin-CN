var ReactDOM = require('react-dom');
var Bootstrap = require('react-bootstrap');
var Footer = require('./component/footer.jsx');
var Navigator = require('./component/navigator.jsx');
var MainPage = require('./component/main-page.jsx');
var Grid = Bootstrap.Grid;

ReactDOM.render(<div>
    <Navigator/>
    <Grid><MainPage/></Grid>
    <Footer/>
</div>, document.body);
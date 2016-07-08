var ReactDOM = require('react-dom');
var Bootstrap = require('react-bootstrap');
var Footer = require('./component/footer.jsx');
var Navigator = require('./component/navigator.jsx');
var Row = Bootstrap.Row;
var Grid = Bootstrap.Grid;
var Col = Bootstrap.Col;

ReactDOM.render(<div>
    <Navigator/>
    <Grid><Row className="show-grid">

    </Row></Grid>
    <Footer/>
</div>, document.body);
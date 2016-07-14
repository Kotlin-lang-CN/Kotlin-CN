var ReactDOM = require('react-dom'),
    React = require('react'),
    Footer = require('./component/footer.jsx'),
    Navigator = require('./component/navigator.jsx');

ReactDOM.render((
    <div>
        <Navigator/>
        <Footer/>
    </div>
), document.body);
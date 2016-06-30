var ReactDOM = require('react-dom');
var Footer = require('./component/footer.jsx');
var Navigator = require('./component/navigator.jsx');
var ArticleMain = require('./component/article-main.jsx');

ReactDOM.render(<div>
    <Navigator/>
    <ArticleMain/>
    <Footer/>
</div>, document.body);
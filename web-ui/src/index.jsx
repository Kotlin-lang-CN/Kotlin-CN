var ReactDOM = require('react-dom'),
    Bootstrap = require('react-bootstrap'),
    Footer = require('./component/footer.jsx'),
    Navigator = require('./component/navigator.jsx'),
    Grid = Bootstrap.Grid,
    Pager = Bootstrap.Pager,
    Jumbotron = Bootstrap.Jumbotron;

const SectionMainHelloWorld = require('react').createClass({
    render: function () {
        return <section className="kotlin-overview-section _get-kotlin" id="get-kotlin">
            <h2 className="section-header" style={{fontSize: '26px'}}>快速开始你的 Kotlin 之旅</h2>
            <div className="section-content">
                <div className="get-kotlin-options-list">
                    <div className="option-item _try-online">
                        <div className="option-image _try-online"></div>
                        <div className="option-title-pre-text">使用浏览器</div>
                        <h3 className="option-title">在线编译器</h3>
                        <p className="option-description" style={{fontSize: '15px'}}>
                            无需配置环境<br/>
                            立即使用浏览器尝试 Kotlin<br/>
                            本技术由<a href="http://kotlinlang.org" target="_blank">kotlinlang.org</a>提供
                        </p>
                        <div className="option-link-wrap">
                            <a href="http://try.kotlinlang.org" className="option-link" target="_blank">马上尝试</a>
                        </div>
                    </div>
                    <div className="option-item _standalone-compiler">
                        <div className="option-image _standalone-compiler"></div>
                        <div className="option-title-pre-text">生产环境</div>
                        <h3 className="option-title">独立编译器</h3>
                        <p className="option-description" style={{fontSize: '15px'}}>
                            在命令行中编译 Kotlin 代码<br/>
                            能在常用的构建工具中配置使用<br/>
                            支持 Ant, Gradle 和 Maven</p>
                        <div className="option-link-wrap"><a href="#" className="option-link">下载编译器</a></div>
                    </div>
                    <div className="option-item _source-code">
                        <div className="option-image _source-code"></div>
                        <div className="option-title-pre-text">使用 IDE</div>
                        <h3 className="option-title">编辑器插件</h3>
                        <p className="option-description" style={{fontSize: '15px'}}>
                            在你心爱的<br/>
                            IntelliJ IDEA 或 Eclips Luna 中<br/>
                            快速安装 Kotlin 插件支持
                        </p>
                        <div className="option-link-wrap">
                            <a href="#" className="option-link">安装插件</a>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    }
});
const MainPage = require('react').createClass({
    render: function () {
        return <Grid><Jumbotron style={{paddingBottom: '30px'}}>
            <h1 style={{fontSize: '50px'}}>Kotlin CN</h1>
            <br/>
            <p style={{fontSize: '16px'}}>还在为 Java 复杂繁琐的语法而头痛么?</p>
            <p style={{fontSize: '16px'}}>还在苦恼于 Android Native 开发糟糕的函数编程支持么?</p>
            <p style={{fontSize: '16px'}}>你是否期待过一门 JVM 语言, 能让你使用海量的 Java 库, 却不需要拘束于肮脏的 Java 语法?</p>
            <br/>
            <h3>为什么不试试Kotlin呢?!</h3>
            <br/>
            <SectionMainHelloWorld/>
            <br/>
            <br/>
            <Pager>
                <li className="next">
                    <a href="/community.html" style={{fontSize: '15px', padding: '10px 20px'}}>进入社区 &rarr;</a>
                </li>
            </Pager>
            <div className="text-right">
                <p style={{fontSize: '17px'}}>我们致力于提供更好更专业的Kotlin中文教程</p>
                <p style={{fontSize: '17px'}}>共建最潮流的Kotlin中文社区</p>
            </div>
        </Jumbotron></Grid>
    }
});
ReactDOM.render((
    <div>
        <Navigator/>,
        <MainPage/>,
        <Footer/>
    </div>
), document.body);
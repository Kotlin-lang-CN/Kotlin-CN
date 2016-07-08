var React = require('react');

var FriendNavigator = React.createClass({
    render: function () {
        return <div className="panel panel-default">
            <div className="panel-heading">友情社区</div>
            <ul className="list-group" style={{'textAlign': 'center'}}>
                <li className="list-group-item">
                    <a href="http://cnodejs.org" rel="nofollow" title="CNode 社区" target="_blank"><img
                        style={{width: '130px'}}
                        src="//ruby-china-files.b0.upaiyun.com/photo/2016/d427ef3efd33b57721df152c2aa1735e.png"/>
                    </a>
                </li>
                <li className="list-group-item">
                    <a href="http://golangtc.com/" rel="nofollow" title="Golang 中国" target="_blank"><img
                        src="//ruby-china-files.b0.upaiyun.com/photo/2016/3b0fc569b40157a397143d121fea7e6f.png"
                        style={{width: '130px'}}/>
                    </a>
                </li>
                <li className="list-group-item">
                    <a href="http://phphub.org" target="_blank" rel="nofollow"><img
                        src="//ruby-china-files.b0.upaiyun.com/photo/2016/d7782871f3fac7e85a95d20c74046909.png"
                        style={{width: '130px'}}/>
                    </a>
                </li>
                <li className="list-group-item">
                    <a href="http://segmentfault.com" target="_blank" rel="nofollow"><img
                        src="//ruby-china-files.b0.upaiyun.com/photo/2016/e91d14ee109ed066e215057ab40257b7.png"
                        style={{width: '150px'}}/>
                    </a>
                </li>
                <li className="list-group-item">
                    <a href="http://elixir-cn.com" target="_blank" rel="nofollow"><img
                        src="//ruby-china-files.b0.upaiyun.com/photo/2015/f65fb5a10d3392a1db841c85716dd8f6.png"
                        style={{width: '140px'}}/>
                    </a>
                </li>
                <li className="list-group-item">
                    <a href="http://ionichina.com/" target="_blank" rel="nofollow"><img
                        src="//ruby-china-files.b0.upaiyun.com/photo/2016/62e5d33d4f90ead9e798e3f8ae085f16.png"
                        style={{width: '140px'}}/></a>
                </li>
                <li className="list-group-item">
                    <a href="https://testerhome.com/" target="_blank" rel="nofollow"><img
                        src="//ruby-china-files.b0.upaiyun.com/photo/2016/5cd78b730062ab3c768bcc2592c5c7fa.png"
                        style={{width: '150px'}}/>
                    </a>
                </li>
            </ul>
        </div>
    }
});

module.exports = FriendNavigator;
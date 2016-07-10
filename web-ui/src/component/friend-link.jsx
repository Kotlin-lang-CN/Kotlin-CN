var React = require('react');
/***
 * 侧边栏友情链接
 */
const FriendNavigator = React.createClass({
    getDefaultProps: function () {
        return {
            link: [{
                href: 'http://cnodejs.org',
                logo: '//ruby-china-files.b0.upaiyun.com/photo/2016/d427ef3efd33b57721df152c2aa1735e.png'
            }, {
                href: 'http://golangtc.com/',
                logo: '//ruby-china-files.b0.upaiyun.com/photo/2016/3b0fc569b40157a397143d121fea7e6f.png'
            }, {
                href: 'http://phphub.org',
                logo: '//ruby-china-files.b0.upaiyun.com/photo/2016/d427ef3efd33b57721df152c2aa1735e.png'
            }, {
                href: 'http://segmentfault.com',
                logo: '//ruby-china-files.b0.upaiyun.com/photo/2016/e91d14ee109ed066e215057ab40257b7.png'
            }, {
                href: 'http://elixir-cn.com',
                logo: '//ruby-china-files.b0.upaiyun.com/photo/2015/f65fb5a10d3392a1db841c85716dd8f6.png'
            }, {
                href: 'http://ionichina.com/',
                logo: '//ruby-china-files.b0.upaiyun.com/photo/2016/62e5d33d4f90ead9e798e3f8ae085f16.png'
            }, {
                href: 'https://testerhome.com/',
                logo: '//ruby-china-files.b0.upaiyun.com/photo/2016/5cd78b730062ab3c768bcc2592c5c7fa.png'
            }, {
                href: 'http://rust-lang-cn.org',
                logo: '//raw.githubusercontent.com/rust-cn/rust-china-web-design/master/Output/Logov1/logo.png'
            }]
        }
    },
    render: function () {
        return (
            <div className="panel panel-default">
                <div className="panel-heading">友情社区</div>
                <ul className="list-group" style={{'textAlign': 'center'}}>
                    {this.getLinkList()}
                </ul>
            </div>
        )
    },
    getLinkList: function () {
        var list = [];
        for (var i = 0; i < this.props.link.length; i++) {
            const index = i;
            const link = this.props.link[i];
            list.push(
                <li className="list-group-item" ref={'friend-link-' + index}>
                    <a href={link.href} rel="nofollow" target="_blank"><img style={{width: 140}} src={link.logo}/></a>
                </li>
            )
        }
        return list;
    }
});
module.exports = FriendNavigator;
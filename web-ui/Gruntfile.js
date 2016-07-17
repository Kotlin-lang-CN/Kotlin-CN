var Webpack = require('webpack');
module.exports = function (grunt) {
    // Project configuration.
    grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),
        webpack: {
            build_react: {
                // webpack options
                entry: {
                    article: './src/article.jsx',
                    community: './src/community.jsx',
                    document: './src/document.jsx',
                    index: './src/index.jsx',
                    publish: './src/publish.jsx'
                },
                output: {
                    path: './src/app/js',
                    filename: '[name].app.js'
                },
                plugins: [
                    new Webpack.optimize.CommonsChunkPlugin("lib.app.js")
                ],
                module: {
                    loaders: [{
                        test: /\.jsx$/,
                        loader: 'jsx-loader'
                    }, {
                        test: /\.json$/,
                        loader: 'json'
                    }, {
                        // I want to uglify with mangling only app files, not thirdparty libs
                        test: /\.js$/,
                        exclude: /.app.js/,
                        loader: "uglify"
                    }]
                }
            }
        }
    });

    grunt.loadNpmTasks('grunt-webpack');
    // 默认被执行的任务列表。
    grunt.registerTask('default', ['webpack']);
};

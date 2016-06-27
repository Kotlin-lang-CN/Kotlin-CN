var Webpack = require('webpack');
module.exports = function (grunt) {
    // Project configuration.
    grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),
        webpack: {
            build_react: {
                // webpack options
                entry: {
                    index: './src/index.jsx',
                    register: './src/register.jsx'
                },
                output: {
                    path: './src/app/static/js',
                    filename: '[name].app.js'
                },
                plugins: [
                    new Webpack.optimize.CommonsChunkPlugin("common.js")
                ],
                module: {
                    loaders: [{
                        test: /\.jsx$/,
                        loader: 'jsx-loader'
                    }]
                }
            }
        },
        uglify: {
            options: {
                banner: '/*! <%= pkg.name %> <%= grunt.template.today("yyyy-mm-dd") %> */\n'
            },
            build: {
                src: 'src/app/static/js/common.js',
                dest: 'src/app/static/js/common.min.js'
            }
        }
    });
    // 加载包含 "uglify" 任务的插件。
    grunt.loadNpmTasks('grunt-webpack');
    grunt.loadNpmTasks('grunt-contrib-uglify');
    // 默认被执行的任务列表。
    grunt.registerTask('default', ['webpack', 'uglify']);
};
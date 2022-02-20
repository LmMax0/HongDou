const path = require("path");
const HtmlWebpackPlugin = require("html-webpack-plugin");
const { VueLoaderPlugin } = require("vue-loader");
const CopyPlugin  = require('copy-webpack-plugin');
const { DefinePlugin } = require("webpack");

module.exports = {

    mode: "development",
    devtool: "source-map",
    entry: path.resolve(__dirname, "./src/main.js"),
    output: {
        path: path.resolve(__dirname, "dist"),
        filename: "[name].js",
        assetModuleFilename: 'images/[hash][ext][query]'
    },

    module: {
        rules: [
            {
                test: /\.vue$/,
                loader: "vue-loader",
            },
            // 应用到普通的 `.css` 文件
            // 以及 `.vue` 文件中的 `<style>` 块
            {
                test: /\.css$/,
                use: ["vue-style-loader", {
                    loader: 'css-loader',
                    options: {
                        // 开启 CSS Modules
                        modules: false,
                    }
                }],
            },

            {
                test: /\.less$/,
                use: ["vue-style-loader", "css-loader", "less-loader"],
            },

            {
                test: /\.m?js$/,
                exclude: /(node_modules|bower_components)/,
                use: {
                    loader: 'babel-loader',
                    options: {
                        presets: ['@babel/preset-env']
                    }
                }
            },

            {
                test: /\.jpg/,
                type: 'asset/resource'
            }

        ],
    },
    plugins: [
        new HtmlWebpackPlugin({
            template: path.resolve(__dirname, "./index.html"), // html 模板地址
            filename: "index.html", // 打包后输出的文件名
            title: "手动搭建 Vue 项目",
        }),
        new VueLoaderPlugin(),
        new CopyPlugin({
            patterns: [
                { from: "src/media", to: "media" },
            ],
        }),
        new DefinePlugin({
            // BASE_URL: "'./'",
            __VUE_OPTIONS_API__: true,
            __VUE_PROD_DEVTOOLS__: false
          }),
    ],
    devServer:{
        proxy: [
            // 例如将'localhost: 8080/api/xxx'代理到'http:www.baidu.com/api/xxx
            // {
            //     context: ['/media'],
            //     target: 'https://muiplayer.js.org/',//接口域名
            //     changeOrigin: true, //如果是https需要配置该参数
            //     secure: false, //如果接口跨域需要进行该配置
            // },
        ],
        port:8888

    }
};


var webpack = require("webpack");

module.exports = {
    entry: {
        "home": "./src/main/resources/assets/js/home/main.js",
        "login": "./src/main/resources/web/admin/apps/login/js/main.ts"
    },
    output: {
        filename: "./target/resources/main/web/admin/apps/[name]/js/_all.js"
    },
    resolve: {
        extensions: ['', '.js', '.ts']
    },
    devtool: 'source-map',
    module: {
        loaders: [
            {
                test: /\.ts/,
                loaders: ['ts-loader'],
                exclude: /node_modules/
            }
        ]
    }
};

const { overrideDevServer } = require('customize-cra');

const devServerConfig = () => config => {
    config.allowedHosts = ['localhost'];
    return config;
};

module.exports = {
    devServer: overrideDevServer(devServerConfig())
};

module.exports = function(config) {

var preprocessors = config.preprocessors;
  // put JSON data into a mock
  preprocessors['**/*.json'] = 'json2js';
  preprocessors['**/*.html'] = 'ng-html2js';

  config.set({
    // base path, that will be used to resolve files and exclude
    basePath: '../../../',

    // frameworks to use
    frameworks: ['jasmine'],

    plugins: [
        'karma-junit-reporter',
        'karma-jasmine',
        'karma-phantomjs-launcher',
        'karma-ng-json2js-preprocessor',
        'karma-ng-html2js-preprocessor'
    ],

    junitReporter: {
        outputFile: 'target/surefire-reports/TEST-phantomjsTest.xml',
        suite: ''
    },

    // list of files / patterns to load in the browser
    files: [
        //TODO replace by a function which adds files automatically
        'src/main/resources/static/bower_components/html-md/dist/md.min.js',
        'src/main/resources/static/bower_components/he/he.js',
        'src/main/resources/static/bower_components/angular/angular.js',
        'node_modules/angular-mocks/angular-mocks.js',
        'src/main/resources/static/bower_components/angular-resource/angular-resource.js',
        'src/main/resources/static/bower_components/angular-route/angular-route.js',
        'src/main/resources/static/bower_components/angular-sanitize/angular-sanitize.js',
        'src/main/resources/static/bower_components/angular-local-storage/dist/angular-local-storage.js',
        'src/main/resources/static/bower_components/textAngular/dist/textAngular-rangy.min.js',
        'src/main/resources/static/bower_components/textAngular/dist/textAngular-sanitize.min.js',
        'src/main/resources/static/bower_components/textAngular/dist/textAngular.min.js',
        'src/main/resources/static/templates/*.html',
        'src/test/resources/json/*.json' ,
        'src/main/resources/static/js/*.js',
        'src/test/javascript/test.js',
        'src/test/javascript/testDirectives.js'
    ],

    ngJson2JsPreprocessor: {
      // strip this from the file path
      stripPrefix: 'src/test/resources/json/',
      prependPrefix : 'fixture/'
    },

    ngHtml2JsPreprocessor: {
      stripPrefix: 'src/main/resources/static/templates/',
      prependPrefix: 'html/',
      moduleName: 'htmlTemplates'
    },

    // list of files to exclude
    exclude: [

    ],

    // test results reporter to use
    // possible values: 'dots', 'progress', 'junit', 'growl', 'coverage'
    reporters: ['progress', 'junit'],

    // web server port
    port: 9876,

    // enable / disable colors in the output (reporters and logs)
    colors: false,

    // level of logging
    // possible values: config.LOG_DISABLE || config.LOG_ERROR || config.LOG_WARN || config.LOG_INFO || config.LOG_DEBUG
    logLevel: config.LOG_WARN,

    // enable / disable watching file and executing tests whenever any file changes
    autoWatch: true,

    // Start these browsers, currently available:
    // - Chrome
    // - ChromeCanary
    // - Firefox
    // - Opera (has to be installed with `npm install karma-opera-launcher`)
    // - Safari (only Mac; has to be installed with `npm install karma-safari-launcher`)
    // - PhantomJS
    // - IE (only Windows; has to be installed with `npm install karma-ie-launcher`)
    browsers: ['PhantomJS'],

    // If browser does not capture in given timeout [ms], kill it
    captureTimeout: 60000,

    // Continuous Integration mode
    // if true, it capture browsers, run tests and exit
    singleRun: true
  });
};

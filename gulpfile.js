var uglify = require('gulp-uglify'),
    concat = require('gulp-concat'),
    minifyCss = require('gulp-minify-css'),
    gulp = require('gulp'),
    minifyHtml = require('gulp-minify-html'),
    htmlreplace = require('gulp-html-replace'),
    ghtmlSrc = require('gulp-html-src'),
    debug = require('gulp-debug'),
    del = require('del'),
    rev = require('gulp-rev'),
    through = require('through2'),
    gulpSequence = require('gulp-sequence'),
    base64 = require('gulp-base64'),
    gutil = require('gulp-util'),
    addSrc = require('gulp-add-src'),
    Server = require('karma').Server,
    path = require('path'),
    bower = require('gulp-bower'),
    inject = require('gulp-inject'),
    wiredep = require('wiredep').stream;

var paths = {
    static: 'src/main/resources/static/',
    index: 'src/main/resources/static/index.html',
    login: 'src/main/resources/static/login.html',
    img: 'src/main/resources/static/images',
    ngTemplates: 'src/main/resources/static/templates',
    bower: 'src/main/resources/static/bower_components/',
    dest: {
        root: 'target/classes/static',
        app: 'target/classes/static/app',
        images: 'target/classes/static/images',
        static: 'target/classes/static',
        templates: 'target/classes/static/templates',
    },
    compress: {
        css: 'main.css',
        js: 'main.js'
    },
    compressed: {}
};

function memorizeCompressedFilename() {
    return through.obj(function(file, enc, cb) {
        var revOrigPathComponents = file.revOrigPath.split(path.sep);
        var fileName = revOrigPathComponents[revOrigPathComponents.length -1];
        var revPathComponents = file.path.split(path.sep);
        var revFileName = revPathComponents[revPathComponents.length -1];
        paths.compressed[fileName] = revFileName;
        cb();
    });
}

function from(attributeName) {
    return function(node) {
        return node.attr(attributeName);
    }
}

gulp.task('copy-angular-templates', function() {
    return gulp.src([paths.ngTemplates + '/*'])
        .pipe(minifyHtml())
        .pipe(gulp.dest(paths.dest.templates))
        .pipe(debug({title: 'copying asset'}));
});

gulp.task('copy-assets', function() {
    return gulp.src([paths.img + '/*'])
        .pipe(gulp.dest(paths.dest.images))
        .pipe(debug({title: 'copying asset'}));
});

gulp.task('process-login-file', function() {
    return gulp.src(paths.login)
        .pipe(debug({title: 'processing login html file'}))
        .pipe(htmlreplace({
            'css': "app/" + paths.compressed[paths.compress.css],
            'js': "app/" + paths.compressed[paths.compress.js]
        }))
        .pipe(minifyHtml())
        .pipe(gulp.dest(paths.dest.root))
        .pipe(debug({title: 'processed login html file'}));
});

gulp.task('process-index-file', function() {
    return gulp.src(paths.index)
        .pipe(debug({title: 'processing index html file'}))
        .pipe(htmlreplace({
            'css': "app/" + paths.compressed[paths.compress.css],
            'js': "app/" + paths.compressed[paths.compress.js]
        }))
        .pipe(minifyHtml())
        .pipe(gulp.dest(paths.dest.root))
        .pipe(debug({title: 'processed index html file'}));
});

gulp.task('process-js', function() {
    return gulp.src(paths.index)
        .pipe(debug({title: 'looking for javascript files in'}))
        .pipe(ghtmlSrc({presets: 'script', getFileName: from('src')}))
        .pipe(debug({title: 'found javascript file'}))
        .pipe(concat(paths.compress.js))
        .pipe(uglify())
        .pipe(rev())
        .pipe(gulp.dest(paths.dest.app))
        .pipe(debug({title: 'processed javascript file'}))
        .pipe(memorizeCompressedFilename())
});

gulp.task('process-css', function() {
    return gulp.src(paths.login)
        .pipe(debug({title: 'looking for css files in'}))
        .pipe(ghtmlSrc({presets: 'css', getFileName: from('href')}))
        .pipe(debug({title: 'found css file'}))
        .pipe(base64({debug:true, maxImageSize: 32768000000}))
        .pipe(concat(paths.compress.css))
        .pipe(minifyCss())
        .pipe(rev())
        .pipe(gulp.dest(paths.dest.app))
        .pipe(debug({title: 'processed css file'}))
        .pipe(memorizeCompressedFilename())
});

gulp.task('karma', function (done) {
    gulp.src(paths.index)
        .pipe(ghtmlSrc({presets: 'script', getFileName: from('src')}))
        .pipe(addSrc.append(['node_modules/angular-mocks/angular-mocks.js', 'src/test/javascript/**.js', 'src/test/resources/json/**', paths.ngTemplates + '/**'], { base: '.' }))
        .pipe(debug({title: 'javascript file(s) for testing'}))
        .pipe(gutil.buffer(function(error, files) {
            if(error) {
                throw error;
            }
            var processedFiles = files.map(function(file) {
                var relativeFile = file.relative;
                if(relativeFile.match(/^(node|src)/)) {
                    return relativeFile;
                }  else {
                    return paths.static + relativeFile;
                }
            });

            new Server({
                basePath: '.',
                preprocessors: {
                    '**/*.json': 'ng-json2js',
                    '**/*.html': 'ng-html2js',
                    'src/main/resources/static/js/**': 'coverage'
                },
                logLevel: 'INFO',
                frameworks: ['jasmine'],
                junitReporter: {
                    outputFile: 'target/surefire-reports/TEST-phantomjsTest.xml',
                    suite: ''
                },
                ngJson2JsPreprocessor: {
                    // strip this from the file path
                    stripPrefix: 'src/test/resources/json/',
                    prependPrefix : 'fixture/'
                },
                ngHtml2JsPreprocessor: {
                    stripPrefix: paths.static,
                    moduleName: 'htmlTemplates'
                },
                coverageReporter: {
                    type : 'lcov',
                    dir : 'target/coverage/'
                },
                files: processedFiles,
                reporters: ['progress', 'junit', 'coverage'],
                browsers: ['PhantomJS'],
                autoWatch: false,
                captureTimeout: 60000,
                singleRun: true
            }, function(errorCode) {
                if (errorCode !== 0) {
                    gutil.log("There are failing unit tests");
                    done();
                    return process.exit(errorCode);
                }
                done();
            }).start();
        }));
});

gulp.task('bower', function() {
    return bower(paths.bower);
});

gulp.task('inject-js-index', function() {
    var sources = gulp.src([paths.static + '/js/*.js'], {read: false});
    return gulp.src(paths.index)
        .pipe(inject(sources, {relative: true}))
        .pipe(gulp.dest(paths.static));
});

gulp.task('inject-js-login', function() {
    var sources = gulp.src([paths.static + '/js/*.js'], {read: false});
    return gulp.src(paths.login)
        .pipe(inject(sources, {relative: true}))
        .pipe(gulp.dest(paths.static));
});

gulp.task('wiredep-js-index', function() {
    return gulp.src(paths.index)
        .pipe(wiredep({directory : paths.bower}))
        .pipe(gulp.dest(paths.static));
});

gulp.task('wiredep-js-login', function() {
    return gulp.src(paths.login)
        .pipe(wiredep({directory : paths.bower}))
        .pipe(gulp.dest(paths.static));
});

gulp.task('wire-js', gulpSequence('bower', 'wiredep-js-login', 'inject-js-login', 'wiredep-js-index', 'inject-js-index'));
gulp.task('test', gulpSequence('wire-js', 'karma'));
gulp.task('process-assets', gulpSequence('process-css', 'process-js', 'process-login-file', 'process-index-file', 'copy-angular-templates'));
gulp.task('default', gulpSequence('wire-js', 'process-assets'));

// used by Maven
gulp.task('mvn-validate', gulpSequence('wire-js'));
gulp.task('mvn-test', gulpSequence('karma'));
gulp.task('mvn-prepare-package', gulpSequence('process-assets'));

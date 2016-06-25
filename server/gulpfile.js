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
    vinylPaths = require('vinyl-paths'),
    base64 = require('gulp-base64'),
    gutil = require('gulp-util'),
    addSrc = require('gulp-add-src'),
    karma = require('karma').server,
    path = require('path');

var paths = {
    index: 'src/main/resources/static/index.html',
    login: 'src/main/resources/static/login.html',
    img: 'src/main/resources/static/images',
    ngTemplates: 'src/main/resources/static/templates',
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

function replaceNodeModulesPath(attributeName) {
    var back = '..' + path.sep + '..' + path.sep + '..' + path.sep + '..' + path.sep;

    return function(node) {
        var filenameWithPath = node.attr(attributeName);
        return filenameWithPath.match(/^node_.+/) ? back + filenameWithPath : filenameWithPath;
    }
}

gulp.task('copy-angular-templates', function() {
    return gulp.src([paths.ngTemplates + '/*'])
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
            'css': "app/" + paths.compressed[paths.compress.css]
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
        .pipe(ghtmlSrc({presets: 'script', getFileName: replaceNodeModulesPath('src')}))
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
        .pipe(ghtmlSrc({presets: 'css', getFileName: replaceNodeModulesPath('href')}))
        .pipe(debug({title: 'found css file'}))
        .pipe(concat(paths.compress.css))
        .pipe(minifyCss())
        .pipe(rev())
        .pipe(gulp.dest(paths.dest.app))
        .pipe(debug({title: 'processed css file'}))
        .pipe(memorizeCompressedFilename())
});

gulp.task('build', gulpSequence('process-css', 'process-js', 'process-login-file', 'process-index-file', 'copy-assets', 'copy-angular-templates'));
var uglify = require('gulp-uglify'),
    concat = require('gulp-concat'),
    minifyCss = require('gulp-minify-css'),
    gulp = require('gulp'),
    debug = require('gulp-debug'),
    del = require('del'),
    gulpSequence = require('gulp-sequence'),
    vinylPaths = require('vinyl-paths'),
    path = require('path');

var paths = {
    scripts: [
        "node_modules/moment/moment.js",
        'node_modules/html-md/dist/md.min.js',
        'node_modules/he/he.js',
        "node_modules/jquery/dist/jquery.js",
        "node_modules/angular/angular.js",
        'node_modules/angular-resource/angular-resource.js',
        'node_modules/angular-route/angular-route.js',
        'node_modules/angular-sanitize/angular-sanitize.js',
        'node_modules/angular-local-storage/src/angular-local-storage.js',
        'node_modules/textangular/dist/textAngular-rangy.min.js',
        'node_modules/textangular/dist/textAngular-sanitize.min.js',
        'node_modules/textangular/dist/textAngular.min.js',
        "src/main/resources/static/js/*.js"
    ],
    css: [ "src/main/resources/static/css/*.css" ],

    index: 'src/index.html',
    img: 'src/app/img',
    dest: {
        root: 'dist',
        app: 'target/classes/static'
    },
    compress: {
        css: 'main.css',
        js: 'main.js'
    },
    compressed: {}
};

function replaceNodeModulesPath(attributeName) {
    var back = '..' + path.sep;

    return function(node) {
        var filenameWithPath = node.attr(attributeName);
        return filenameWithPath.match(/^node_.+/) ? back + filenameWithPath : filenameWithPath;
    }
}

gulp.task('process-js', function() {
    return gulp.src(paths.scripts)
      //  .pipe(debug({title: 'looking for javascript files in'}))

     //   .pipe(ghtmlSrc({presets: 'script', getFileName: replaceNodeModulesPath('src')}))

        .pipe(debug({title: 'found javascript file'}))
        .pipe(concat(paths.compress.js))
        .pipe(uglify())
        .pipe(gulp.dest(paths.dest.app))
        .pipe(debug({title: 'processed javascript file'}))
});

gulp.task('process-css', function() {
    return gulp.src(paths.css)
       // .pipe(debug({title: 'looking for css files in'}))

     //   .pipe(ghtmlSrc({presets: 'css', getFileName: replaceNodeModulesPath('href')}))

        .pipe(debug({title: 'found css file'}))
        .pipe(concat(paths.compress.css))
        .pipe(minifyCss())
        .pipe(gulp.dest(paths.dest.app))
        .pipe(debug({title: 'processed css file'}))
});

gulp.task('clean', function () {
    return gulp.src(paths.dest.root)
        .pipe(debug({title: 'cleaning folder'}))
        .pipe(vinylPaths(del))
        .pipe(gulp.dest('.'));
});

gulp.task('build', gulpSequence('clean', 'process-js', 'process-css'));
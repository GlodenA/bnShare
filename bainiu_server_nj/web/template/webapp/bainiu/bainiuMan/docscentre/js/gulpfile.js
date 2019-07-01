var project = './static';

var gulp = require('gulp'),
	sass = require('gulp-sass'),
	babel = require('gulp-babel'),
	package = require('./package.json'), //package.json
	through = require('through2'), //获取文件名
	rename = require('gulp-rename'), //重命名
	insert = require('gulp-insert'), //插入内容
	replace = require('gulp-replace'), //替换字符
	autoprefixer = require('gulp-autoprefixer');


var header = `/*-----------------------
* Site:  {{ name }}
* Author: {{ author }}
* Updated: {{ date }}
* Version: {{ version }}
* -----------------------*/\n`;
var nowTime = new Date().getTime();




function getDate(time, format) {
	var t = new Date(time);
	var tf = function(i) {
		return (i < 10 ? '0' : '') + i
	};
	return format.replace(/yyyy|MM|dd|HH|mm|ss/g,
		function(a) {
			switch (a) {
				case 'yyyy':
					return tf(t.getFullYear());
					break;
				case 'MM':
					return tf(t.getMonth() + 1);
					break;
				case 'mm':
					return tf(t.getMinutes());
					break;
				case 'dd':
					return tf(t.getDate());
					break;
				case 'HH':
					return tf(t.getHours());
					break;
				case 'ss':
					return tf(t.getSeconds());
					break;
			};
		});
};


let scss = [];
gulp.task('addScss', function() {
	return gulp.src('./' + project + '/css/scss/**.scss')
		.pipe(through.obj(function(file, enc, cb) {
			scss.push(file.relative.replace('.scss', ''));
			this.push(file);
			cb();
		}));
});

gulp.task('sass', ['addScss'], function() {
	var name, stream;
	for (var i = 0; i < scss.length; i++) {
		name = scss[i];
		stream = gulp.src('./' + project + '/css/scss/' + name + '.scss')
			.pipe(sass())
			.pipe(rename(name + '.css'))
			.pipe(autoprefixer({
				browsers: ['last 2 versions', 'Android >= 5.5', 'last 3 Safari versions', 'last 3 Chrome versions', 'iOS 	>= 9.0'],
				cascade: true,
				remove: true
			}))
			.pipe(insert.prepend(header))
			.pipe(replace('{{ date }}', getDate(nowTime, 'yyyy-MM-dd HH:mm')))
			.pipe(replace('{{ author }}', package.author))
			.pipe(replace('{{ version }}', package.version))
			.pipe(replace('{{ name }}', name))
			.pipe(gulp.dest('./static/css/'));
	};
	return stream;
});

gulp.task('ES62ES5', function() {
	return gulp.src('./' + project + '/js/ES6/*.js')
		.pipe(babel({
			presets: ['es2015']
		}))
		.pipe(insert.prepend(header))
		.pipe(replace('{{ date }}', getDate(nowTime, 'yyyy-MM-dd HH:mm')))
		.pipe(replace('{{ author }}', package.author))
		.pipe(replace('{{ version }}', package.version))
		.pipe(replace('{{ name }}', package.name))
		.pipe(gulp.dest('./static/js/'));
});

gulp.task('watch', function() {
	gulp.watch('./' + project + '/css/scss/**.scss', ['sass']);
	gulp.watch('./' + project + '/js/ES6/**.js', ['ES62ES5']);
});

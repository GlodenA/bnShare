require.config({
	baseUrl:'biz/js',
	//指定别名. zepto.event如果不使用引号,会导致异常
	paths:{
		'jquery' : 'common/jquery',
		'common' : 'common/common',
 		'util' : 'common/util',
		'pagination' : 'common/pagination',
		'placeholder' : 'common/placeholder',
		'layer' : 'webjs/layer',
		'pickmeup':'common/pickmeup',
		'ZeroClipboard':'webjs/ueditor/third-party/zeroclipboard/ZeroClipboard'
	}
});
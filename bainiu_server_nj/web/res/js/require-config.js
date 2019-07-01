/**
 * baseUrl的优先级:require.config>data-main>html文件路径
 * 如果模块包含如下的字符,不按照baseUrl+paths的方式来寻找模块,而是采用全路径(URL)的方式:
 * 1.如果以".js"结尾
 * 2.如果以"/"开头
 * 3.如果以"http:"或者"https:"开头
 */
require.config({
	baseUrl:'res/js',
	//指定别名. zepto.event如果不使用引号,会导致异常
	paths:{
		'domReady' : 'base/require-domReady',
		'jcl' : 'base/jcl',
		'zepto' : 'base/zepto1.1.6',
		'iScroll' : 'base/iscroll',
		'iScroll5' : 'base/iscroll5',
		'hammer' : 'base/hammer',
		'o' : 'frame/o',
		'oEvent' : 'frame/o-event',
		'oInput' : 'frame/o-input',
		'tap' : 'frame/tap',
		'browserTool' : 'mobile/browser-toolkit',
		'clientTool' : 'mobile/client-toolkit',
		'mobileBrower' : 'mobile/mobile-browser',
		'mobileClient' : 'mobile/mobile-client',
		'wadeMobile' : 'mobile/wade-mobile',//这里同时会引入expand-mobile和biz-mobile
		'mobile' : 'mobile/mobile',
		'base64' : 'mobile/base64',
		'chart' : 'ui/chart',
		'wmWebUI' : 'ui/wm-webui',
		'wmTab' : 'ui/wm-tab',
		'wmTabbar' : 'ui/wm-tabbar',
		'wmPopup' : 'ui/wm-popup',
		'wmBase' : 'ui/wm-base',
		'wmAnimate' : 'ui/wm-animate',
		'wmCss3animate' : 'ui/wm-css3animate',
		'wmTouchLayer' : 'ui/wm-touchLayer.js',
		'wmUI' : 'ui/wm-ui',
		'wmNavBar' : 'ui/wm-navbar',
		'wmToolTip' : 'ui/wm-tooltip',
		'wmSwitch' : 'ui/wm-switch',
		'wmSlider' : 'ui/wm-slider',
		'wmProgress' : 'ui/wm-progress',
		'wmSegment' : 'ui/wm-segment',
		'wmDialog' : 'ui/wm-dialog',
		'wmDropmenu' : 'ui/wm-dropmenu'
		
	},
	deps: [],
	callback: function(){
	},
	//非AMD规范的js输出对象
	shim: {
		/*'underscore':{
			exports: '_'
		},*/
		/*"zepto":{
			exports: '$'
		}*/
	},
	//设置超时时间,默认7秒
	waitSeconds:7/*,
	//缓存
	urlArgs: "bust=" + (new Date()).getTime()*/
});

/*引入util对应的js文件*/
define(['util'], function(){
	var content = (function() {
		return document.body;
	})();
	
	var px = (function(){
		var div = document.createElement("div");
		div.style.cssText = "width:1rem; height:1rem; position:absolute; top:-1rem; left:0;";
		document.body.appendChild(div);
		var value = div.offsetWidth / 100;
		document.body.removeChild(div);
		return value;
	})();
	/*WmTab对象定义*/
	function WmTab(id){
		this.listeners = new Array(); //存储监听事件
		/*常用对象*/
		this.tab = (function(obj){
			if(typeof(obj)=="object"){
				obj = $(obj);
			}else if(typeof(obj)=="string"){
				obj = $("#"+obj);
			}else{
				alert("没有匹配类型");
				return null;
			}
			return obj;
		})(id);
		this.parLabels = this.tab.find('div .title ul');
		this.labels = this.parLabels.find('li');
		this.parPages = this.tab.find('div .pages');
		this.pages = this.parPages.find('div .page');
	}
	/*刷新全局变量*/
	WmTab.prototype.refresh = function(){
		this.parLabels = this.tab.find('div .title ul');
		this.labels = this.parLabels.find('li');
		this.parPages = this.tab.find('div .pages');
		this.pages = this.parPages.find('div .page');
		
		$.each(this.labels, function(index, label){
			label.setAttribute("tab_index", index);
		});
	}
	/*初始化Page内容*/
	WmTab.prototype.initPage = function (index, label){
		var that = this;
		label.setAttribute("tab_index", index);
		O.domClick(label, (function() {
			that.active(parseInt(label.getAttribute("tab_index")));
		}));
		this.pages[index].style.height = tabContentHeight;
		new iScroll(this.pages[index]);
	}
	
	/*创建Tab对象*/
	WmTab.prototype.create = function(){
		var that = this;
		if(!this.tab){
			return;
		}
		this.parPages[0].className = this.parPages[0].className+" pages-"+this.pages.length; //class="pages pages-2"
		
		/*tab页面的高度*/
		space = 12*px;
		var title = this.tab.find("div .title")[0];
		tabContentHeight = content.offsetHeight - title.offsetHeight - 3*space + "px";
		
		$.each(this.labels, function(index, label){
			that.initPage(index, label);
		});
	};
	/*选中标签页*/
	WmTab.prototype.active = function(index){
		if(!this.labels[index]){
			throw "越界";//错误信息如何处理
			return;
		}
		for (var i = 0; i < this.labels.length; i++) {
			this.labels[i].className = "";
		}
		this.labels[index].className = "on";
		this.parPages[0].className = "pages pages-" + this.pages.length + " pages-current-" + (index+1);
		var listener = this.listeners[index];
		if(listener){
			listener();
		}
	};
	/*增加点击标签的监听*/
	WmTab.prototype.addListener = function(index, callback) {
		this.listeners.splice(index, 0, callback);
	};
	/*增加Tab标签页*/
	WmTab.prototype.addItem = function(label,content,index){
		if (index!=undefined && !isNaN(index)) {
			$(this.labels[index]).before('<li>' + label + '</li>');
			$(this.pages[index]).before('<div class="page">' + content + '</div>');
		}else{
			this.parLabels.append('<li>'+label+'</li>');
			this.parPages.append('<div class="page">'+content+'</div>');
			index = this.pages.length;
		}
		this.refresh();
		this.addListener(index,null);
		this.parPages[0].className = this.parPages[0].className+" pages-"+this.pages.length; 
		this.initPage(index,this.labels[index]);
	};
	/*删除标签页*/
	WmTab.prototype.removeItem = function(index){
		if(!this.labels[index]){
			throw "越界";
			return;
		}
		
		$(this.labels[index]).remove();
		$(this.pages[index]).remove();
		refresh();
		this.listeners.splice(index,1);
	};
	/*获取标签页的数量*/
	WmTab.prototype.length = function(){
		return this.labels.length;
	}
	/*导出WmTab*/
	return WmTab;
})

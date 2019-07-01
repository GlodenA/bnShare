/*引入util对应的js文件*/

define(['util'], function(){
	/*
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
	*/
	/*WmNavBar对象定义*/
	function WmNavBar(id){
		this.listeners = new Array(); //存储监听事件
		this.id = id;
		/*常用对象*/
		this.bar = (function(obj){
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
	}
	/*设置按钮事件*/
	WmNavBar.prototype.setFuncAction = function(searchBy,action){
		var ele = this.bar.find(searchBy);
		if(ele.length > 0){
			if(typeof action !="function"){
				action = new Function("return "+action)();
			}
			if(ele[0].parentNode.tagName.toLowerCase()=="button"){
				$(ele[0].parentNode).tap(action);
			}else{
				$(ele[0]).tap(action);
			}
		}else{
			alert("Element not found");
		}
	};
	/*设置添加按钮事件*/
	WmNavBar.prototype.setAddAction = function(action){
		this.setFuncAction('span.e_ico-add',action);
	};
	/*设置返回按钮事件*/
	WmNavBar.prototype.setBackAction = function(action){
		this.setFuncAction('span.e_ico-back',action);
	};
	/*设置删除按钮事件*/
	WmNavBar.prototype.setDelAction = function(action){
		this.setFuncAction('span.e_ico-delete',action);
	};
	/*设置日期按钮事件*/
	WmNavBar.prototype.setDateAction = function(action){
		this.setFuncAction('span.e_ico-date',action);
	};
	/*设置查找按钮事件*/
	WmNavBar.prototype.setSearchAction = function(action){
		this.setFuncAction('span.e_ico-search',action);
	};
	/*设置标题*/
	WmNavBar.prototype.setTitle = function(title){
		var titleEle = this.bar.find('div.title');
		if(titleEle.length < 1){
			titleEle = this.bar.find('span.text');
		}
		if(titleEle.length > 0){
			$(titleEle[0]).text(title);
		}else{
			alert("title not found");
		}
	};
	/*获取标题*/
	WmNavBar.prototype.getTitle = function(){
		var titleEle = this.bar.find('div.title');
		if(titleEle.length < 1){
			titleEle = this.bar.find('span.text');
		}
		if(titleEle.length > 0){
			var title = $(titleEle[0]).text();
			return title;
		}else{
			return "";
		}
	};
	
	/*设置TabItem事件*/
	WmNavBar.prototype.setTabItemAction = function(index,action){
		this.listeners[index] = action;
		this.bindTabItemAction(index,action);
	};
	/*绑定TabItem事件*/
	WmNavBar.prototype.bindTabItemAction = function(index,action){
		var tapAction = function(e){
			var ele = e.target;
			var oldEle = $(ele.parentNode).find('div.tab li.on');
			if(oldEle[0] == ele){
				//选中后再次点击
			}else{
				var re;
				if(typeof action =="function"){
					re = action();
				}else{
					re = new Function("return "+action)().call(window);
				}
				if(false != re){
					$(oldEle[0]).removeClass("on");
					$(ele).addClass("on");
				}
			}
		};
		var liEle = this.bar.find('div.tab li');
		$(liEle[index]).tap(tapAction);
	};
	/*选中标签页*/
	WmNavBar.prototype.activeTabItem = function(index){
		var lis = this.bar.find('div.tab li');
		var oldEle = this.bar.find('div.tab li.on');
		if(oldEle[0] == lis[index]){
			//选中后再次点击
		}else{
			var action = this.listeners[index];
			if(typeof action !="function"){
				action = new Function("return "+action)();
			}
			if(false != action()){
				$(oldEle[0]).removeClass("on");
				$(lis[index]).addClass("on");
			}
		}
	};
	/*增加Tab标签页*/
	WmNavBar.prototype.addTabItem = function(label,action,index){
		var labelTag = '<li>' + label + '</li>';
		var lis = this.bar.find('div.tab li');
		if (index != undefined && !isNaN(index) && index<lis.length) {
			$(lis[index]).before(labelTag);
			this.bindTabItemAction(index,action);
			this.listeners.splice(index,0,action);
		}else{
			$(this.bar.find('div.tab ul')[0]).append(labelTag);
			this.setTabItemAction(lis.length,action);
		}
	};
	/*删除标签页*/
	WmNavBar.prototype.removeTabItem = function(index){
		var lis = this.bar.find('div.tab li');
		if(index < lis.length){
			$(lis[index]).remove();
			this.listeners.splice(index,1);
		}else{
			alert("out of bounds");
		}
	};
	
	/*分组标签页面，初始化*/
	WmNavBar.prototype.initTabGroup = function(){
		var tip = this.bar.find('div.buttonList div.tip');
		for(var i=0;i<tip.length;i++){
			$(tip[i]).tap(function(e){
				var fold = $(e.target).find('span');
				var option = $(e.target).parent().find('div.option');
				if($(option[0]).css("display")=="block"){
					$(fold[0]).removeClass("e_ico-fold").addClass("e_ico-unfold");
					$(option[0]).css("display","none");
				}else{
					$(fold[0]).removeClass("e_ico-unfold").addClass("e_ico-fold");
					$(option[0]).css("display","block");
				}
			});
		}
	};
	/*设置TabGroupItem事件*/
	WmNavBar.prototype.setTabGroupItemAction = function(groupIndex,itemIndex,action){
		//this.listeners[index] = action;
		this.bindTabGroupItemAction(groupIndex,itemIndex,action);
		
	};
	/*绑定TabGroupItem事件*/
	WmNavBar.prototype.bindTabGroupItemAction = function(groupIndex,itemIndex,action){
		var tapAction = function(e){
			$(e.target.parentNode.parentNode).css("display","none");
			var fold = $(e.target.parentNode.parentNode).prev().find('span');
			$(fold[0]).removeClass("e_ico-fold").addClass("e_ico-unfold");
			if(typeof action =="function"){
				action();
			}else{
				new Function("return "+action)().call(window);
			}
		}
		var tabGroup = this.bar.find('div.buttonList');
		if(groupIndex < tabGroup.length){
			var lis = $(tabGroup[groupIndex]).find('div.option li');
			if(itemIndex < lis.length){
				$(lis[itemIndex]).tap(tapAction);
			}
		}
	};
	/*增加Option*/
	WmNavBar.prototype.addTabGroupItem = function(groupIndex,itemIndex,label,action){
		var labelTag = '<li>' + label + '</li>';
		var tabGroup = this.bar.find('div.buttonList');
		if(groupIndex < tabGroup.length){
			var lis = $(tabGroup[groupIndex]).find('div.option li');
			if(itemIndex < lis.length){
				$(lis[itemIndex]).before(labelTag);
				this.setTabGroupItemAction(groupIndex,itemIndex,action);
			}
		}
	};
	/*删除Option*/
	WmNavBar.prototype.removeTabGroupItem = function(groupIndex,itemIndex){
		var tabGroup = this.bar.find('div.buttonList');
		if(groupIndex < tabGroup.length){
			var lis = $(tabGroup[groupIndex]).find('div.option li');
			if(itemIndex < lis.length){
				$(lis[itemIndex]).remove();
			}
		}
	};
	
	/*导出WmTab*/
	return WmNavBar;
});

/*引入util对应的js文件*/

define(['util'], function(){
	/*WmSegment对象定义*/
	function WmSegment(id,hiddenId,initAction){
		this.id = id;
		this.hiddenId = hiddenId;//隐藏域id、name
		/*常用对象*/
		this.segment = (function(obj){
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
		if(initAction !== false){
			this.bindAction();
		}
	}
	/*绑定事件*/
	WmSegment.prototype.bindAction = function(ele){
		var action = this.action;
		var hiddenId = this.hiddenId;
		var tapEvent = function(e){
			var ele = e.target;
			var re;
			if(typeof action =="function"){
				re = action($(ele).attr("segValue"),$(ele).text());
			}else{
				re = new Function("return "+action)().call(window,$(ele).attr("segValue"),$(ele).text());
			}
			if(false != re){
				$($(ele.parentNode).find("span.e_segmentOn")).removeClass("e_segmentOn");
				$(ele).addClass("e_segmentOn");
				$("#"+hiddenId).val($(ele).attr("segValue"));
			}
		};
		if(ele){
			$(ele).tap(tapEvent);
		}else{
			$(this.segment.find('span.e_segmentLi')).tap(tapEvent);
		}
	};

	/*选中项，根据segValue*/
	WmSegment.prototype.activeItem = function(value){
		$(this.segment.find("span.e_segmentOn")).removeClass("e_segmentOn");
		$(this.segment.find("span[segValue="+value+"]")).addClass("e_segmentOn");
		$("#"+this.hiddenId).val(value);
	};
	/*选中项，根据index*/
  	WmSegment.prototype.activeItemIndex = function(index){
		$(this.segment.find("span.e_segmentOn")).removeClass("e_segmentOn");
		$(this.segment.find("span.e_segmentLi")[index]).addClass("e_segmentOn");
		$("#"+this.hiddenId).val($(this.segment.find("span.e_segmentLi")[index]).attr("segValue"));
	};
	/*添加项，segValue text index*/
  	WmSegment.prototype.addItem = function(value,text,index){
		var labelTag = '<span class="e_segmentLi" segValue="'+value+'">'+text+'</span>';
		var lis = this.segment.find('span.e_segmentLi');
		var ele;
		if (index != undefined && !isNaN(index) && index<lis.length) {
			ele = $(lis[index]).before(labelTag);
			ele = $(ele[0]).prev();
		}else{
			ele = $(this.segment.find('span.e_segmentWrapper')[0]).append(labelTag);
			ele = ele[0];
		}
		this.bindAction(ele);
	};
	/*移除项，根据segValue*/
  	WmSegment.prototype.removeItem = function(value){
		$(this.segment.find("span[segValue="+value+"]")).remove();
	};
	/*移除项，根据index*/
  	WmSegment.prototype.removeItemIndex = function(index){
		$(this.segment.find("span.e_segmentLi")[index]).remove();
	};
	/*选中项文本*/
  	WmSegment.prototype.getTextByIndex = function(index){
		return $(this.segment.find("span.e_segmentLi")[index]).text();
	};
	/*根据index取segValue*/
  	WmSegment.prototype.getValueByIndex = function(index){
		return $(this.segment.find("span.e_segmentLi")[index]).attr("segValue");
	};
	/*根据index取文本*/
  	WmSegment.prototype.getSelectedText = function(){
		return $(this.segment.find("span.e_segmentOn")[0]).text();
	};
	/*选中项segValue*/
  	WmSegment.prototype.getSelectedValue = function(){
		return $(this.segment.find("span.e_segmentOn")[0]).attr("segValue");
	};
	/*选中项index*/
  	WmSegment.prototype.getSelectedIndex = function(){
		var lis = this.segment.find("span.e_segmentLi");
		for(var i=0;i<lis.length;i++){
			if($(lis[i]).hasClass("e_segmentOn")){
				return i;
			}
		}
		return ;
	};
	/*项数目*/
  	WmSegment.prototype.getCount = function(){
		return this.segment.find("span.e_segmentLi").length;
	};
	/*选中项触发事件*/
	WmSegment.prototype.setAction = function(action){
		if(typeof action =="string"){
			action = new Function("return "+action)();
		}
		this.action = action;
		this.bindAction();
	};
  	
	/*导出WmSegment*/
	return WmSegment;
});

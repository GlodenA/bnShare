define(["module","util"],function(module){
	var WmTabbar = function(id){
		/*常用对象*/
		this.listeners = new Array(); //存储监听事件
		this.tabbar = (function(obj){
			if(typeof(obj)=="object"){
				obj = $(obj);
			}else if(typeof(obj)=="string"){
				obj = $("#"+obj);
			}else{
				throw "没有匹配类型";
			}
			return obj;
		})(id);
	
		this.navs = this.tabbar.find(".m_nav .wrapper .nav ");
		this.footers = this.tabbar.find("div[class=m_footer] li");
		this.actTabIdx;
	}
	
	WmTabbar.prototype.create = function(){
		that = this;
		this.footers.each(function(idx){
			$(this).attr("tabbar_index", idx);
			if("on" == this.className)
				that.actTabIdx = idx;
		});
		
		if(!this.actTabIdx){
			this.actTabIdx = 0;
			this.footers[0].className = "on";
		}
		
		this.navs.each(function(idx){
			//$(this).scroller();
			new iScroll(this)
			if(that.actTabIdx != idx)
				$(this).css("display","none");
		});

		this.tabbar.find(".m_nav").addClass("m_nav-col-2");//一定是2

		this.footers.tap(function(){
			var el = this;
			if(!el || !el.nodeType)return;

			//查找li element
			i=0;
			while(!$.nodeName(el,"li") && i<3){
				el = el.parentNode;
				i++
			}
			var curIdx = parseInt($(el).attr("tabbar_index"));
			var prevIdx = that.actTabIdx;
			if(prevIdx == curIdx) return;
				
			that.navs.each(function(idx){
				if(prevIdx != idx && curIdx != idx)
					$(this).css("display","none");
				else 
					$(this).css("display","");
			});

			
			var wrapper = that.tabbar.find(".m_nav .wrapper");
			$.ui.css3animate(wrapper, {
				x: ((curIdx > prevIdx)?"0":"-50") + "%",
				y: "0%",
				complete:function(){
					$.ui.css3animate(that.tabbar.find(".m_nav .wrapper"), {
						x: ((curIdx > prevIdx)?"-50":"0") + "%",
						y: "0%",
						time: $.ui.transitionTime,
						complete:function(){
							
						}
					});
				}
			});
			$(that.footers[prevIdx]).removeClass("on");
			el.className="on";
			
			that.actTabIdx = curIdx;
		});
	}
	
	module.exports = WmTabbar;
});

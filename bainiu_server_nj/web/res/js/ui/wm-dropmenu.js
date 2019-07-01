define(["util"],function(){
	function DropmenuItem(obj,parent){
		this.entity=obj;
		this.parent=parent;
	}
	DropmenuItem.prototype.remove=function(){
		this.entity.parentNode.removeChild(this.entity);
		this.parent.initMenu();
		this.parent.items.splice(this.getIndex(),1)
	}
	DropmenuItem.prototype.html=function(html){
		if(html==undefined){
			return this.entity.innerHTML;
		}else{
			this.entity.innerHTML=html;
		}
	}
	DropmenuItem.prototype.click=function(callback){
		var that=this;
		this.entity.onclick=function(){
			callback(that);
		}
	}
	DropmenuItem.prototype.getIndex=function(){
		for(var i=0;i<this.parent.items.length;i++){
			if(this.parent.items[i]==this){
				return i;
			}
		}
		return -1;
	}
	DropmenuItem.prototype.getParent=function(){
		return this.parent;
	}
	function WmDropmenu(id){
		this.entity=(function(obj){
			if(typeof(obj)=="object"){
				obj=$(obj);
			}else if(typeof(obj)=="string"){
				obj=$("#"+obj);
			}else{
				alert("没有匹配的类型");
				return null;
			}
			return obj;
		})(id);
		this.btn=this.entity.children("div").eq(0).children("span").eq(0);
		this.menu=this.entity.children("div").eq(1);
		this.closeAction=function(){};
		this.openAction=function(){};
		this.items=new Array();
		this.lis=this.menu.children("ul").children("li");
		this.temp=false;
		var that=this;
		$.each(this.lis,function(index,obj){
			var item=new DropmenuItem(obj,that);
			that.items.push(item);
		});
	}
	WmDropmenu.prototype.initMenu=function(){
		this.menu.css("display","");
		//设置 dropmenu
		//初始化（CSS已将它默认放到了界面之外，以下设置不会造成闪烁）
		this.menu.children("ul").css("top",this.menu.height()*(-1) + "px");
		this.menu.children("ul").css("transition","transform 0.2s ease-out");
		this.btn.children(".e_ico-unfold").css("transition","transform 0.2s ease-out");
		this.menu.css("visibility","hidden");
		//位置
		this.menu.css("left",this.btn.offset().left + this.btn.width() - this.menu.width());//靠右要减去这两个 width()，靠左则不需要
		this.menu.css("top",this.btn.offset().top + this.btn.height());
		this.menu.css("display","none");
		this.menu.css("visibility","visible");
	}
	WmDropmenu.prototype.create=function(){
		this.initMenu();
		var that=this;
		//显隐
		this.btn.click(function(){
			if(that.invisible()){
				that.show();
			}else{
				that.hidden();
			}
		});
	};
	WmDropmenu.prototype.invisible=function(){
		return this.menu.css("display") == "none";
	};
	WmDropmenu.prototype.show=function(){
		this.openAction();
		this.menu.css("display","");
		this.menu.children("ul").css("transform","translateY(" + this.menu.height() + "px)");
		this.btn.find(".e_ico-unfold").css("transform","rotate(180deg)");
	};
	WmDropmenu.prototype.hidden=function(){
		this.closeAction();
		this.menu.children("ul").css("transform","translateY(0)");
		this.btn.find(".e_ico-unfold").css("transform","rotate(0)");
		this.menu.css("display","none");
	};
	WmDropmenu.prototype.removeAll=function(){
		this.menu.children("ul")[0].innerHTML="";
		this.items=new Array();
	};
	WmDropmenu.prototype.push=function(obj){
		this.menu.children("ul").append("<li>"+obj+"</li>");
		this.initMenu();
		var li=this.menu.children("ul").children("li").last()[0];
		var item=new DropmenuItem(li,this);
		this.items.push(item);
		return item;
	};
	WmDropmenu.prototype.setLabel=function(label){
		this.btn.children("span")[0].innerHTML=label;
	};
	WmDropmenu.prototype.getLabel=function(){
		return this.btn.children("span")[0].innerHTML;
	};
	WmDropmenu.prototype.setCloseAction=function(callback){
		this.closeAction=callback;
	};
	WmDropmenu.prototype.setOpenAction=function(callback){
		this.openAction=callback;
	};
	WmDropmenu.prototype.getItems=function(){
		return this.items;
	};
	return WmDropmenu;
});

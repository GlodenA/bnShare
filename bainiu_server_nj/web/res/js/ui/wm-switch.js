define(["util"],function(){
	function WmSwitch(id){
		this.entity=(function(obj){
			if(typeof(obj)=="object"){
				obj=$(obj);
			}else if(typeof(obj)=="string"){
				obj=$("#"+obj);
			}else{
				alert("没有匹配类型");
				return null;
			}
			return obj;
		})(id);
		//存储开关状态的值
		this.input=this.entity.find("input");
		//三个span，用来存储开、关、及开关滑块
		this.span=this.entity.find("span");
		//根据开关的默认状态，计算开关滑块到开关控件最边缘的间隙
		var space=0;
		/*
		if(this.getValue()){
			space=this.span[2].offsetLeft-this.span[2].offsetWidth;	
		}else{
			this.span[2].style.webkitTransform = "translateX(0px)"
			space=this.span[2].offsetLeft;
			this.span[2].style.webkitTransform = "translateX(-100%)"
		}*/
		space=this.span[2].offsetLeft-this.span[2].offsetWidth;	
		console.log("space:"+space)
		//计算滑块左右应当移动的距离
		this.distance=this.entity[0].offsetWidth-this.span[2].offsetWidth-2*space;
		console.log("distancd:"+this.distance)
		//设置开关是否可用
		this.enable=true;
		//当开关状态改变，或试图改变开关状态时调用(开关不可用时)。
		this.changeAction=function(){};
		//当开关被打开时调用。
		this.onAction=function(){};
		//当开关被关闭时调用。
		this.offAction=function(){};
	}

	WmSwitch.prototype.create=function(){
		var that=this;
		if(!this.entity){
			alert("无法获取网页元素");
			return;
		}
		dragEvent(this);
	};
	//鼠标拖拽事件
	function dragEvent(that){
		//开关控件本身
		var obj=that.entity[0];
		//滑块
		var blockObj=obj.children[2];
		//是否为触屏
		var hasTouch = "ontouchstart" in window;
		var START_EVENT = hasTouch ? "touchstart" : "mousedown";
		var MOVE_EVENT = hasTouch ? "touchmove" : "mousemove";
		var END_EVENT = hasTouch ? "touchend" : "mouseup";
		var CANCEL_EVENT = hasTouch ? "touchcancel" : "mouseup";
		//开始移动的位置
		var startX=-1;
		//当前被操作的元素
		var currElement;
		//是否在移动
		var isMoving=false;
		//正则，用于获取translateX的数值
		var reg=/\-?[0-9]+/g;
		//移动标志，用于区分是单击，还是移动事件
		var moveFlag=false;
		//body上的鼠标结束移动回调函数
		var handler=function(event){
			switchEvent(event);
		}
		//body上的鼠标移动回调函数
		var moveHandler=function(event){
			moveEvent(event);
		}
		//滑块上添加拖拽开始事件
		blockObj.addEventListener(START_EVENT,function(event){
			//如果正在移动，则忽略
			if(!isMoving){
				//记录当前被单击的对象
				currElement=obj;
				startX=event.clientX;
				document.body.addEventListener(END_EVENT,handler,false);
				document.body.addEventListener(MOVE_EVENT,moveHandler,false);
			}
		},false);
		//在开关控件上添加移动事件
		obj.addEventListener(MOVE_EVENT,function(event){
			moveEvent(event);
		},false);
		//在开关控件上添加移动结束事件
		obj.addEventListener(END_EVENT,function(event){
			if(currElement!=obj){
				if(that.enable){
					that.setValue(!that.getValue());
				}else{
					that.changeAction(that.enable);	
				}
			}
			switchEvent(event);
		},false);
		//在开关控件上添加移动取消事件
		obj.addEventListener(CANCEL_EVENT,function(event){
			switchEvent(event);
		},false);
		//移动事件
		function moveEvent(event){
			if(currElement==obj){
				if(that.enable){
					//如果进入了移动事件，刚将移动状态设置为true，以区分单击事件
					moveFlag=true;
					//鼠标当前位置与起始位置的偏移量
					var leftX=event.clientX-startX;
					//如果开关为打开状态，则leftX为负数，that.distance为正数，并且不会执行下面一段逻辑
					//如果开关为关闭状态，则leftX为正数，that.distance为正数，执行下面一段逻辑后，得到一个绝对值较小的负数。
					if(!that.getValue()){
						leftX=leftX-that.distance;
					}
					//计算出界。
					if(leftX>=-that.distance && leftX<=0 ){
						blockObj.style.webkitTransform = "translateX(" + leftX + "px)"
					}
				}
			}
		}
		//松开滑块后，自动执行动画的事件
		function switchEvent(event){
			if(currElement==obj){
				//此方法根据currElement对象来区分是否执行。当执行了一次此方法后，在将currElement置空，表示一次流程已经走完。
				//也用currElement来区分是那个对象触发的事件。
				currElement=null;
				if(that.enable){
					if(moveFlag){
						//当执行拖拽完成后的小动画的时候，通过一个状态位，将开关控件锁定。
						isMoving=true;
						blockObj.style.webkitTransition="none";//关闭css3动画
						//获取到当前translateX的值
						var translateX=parseInt(blockObj.style.webkitTransform.match(reg));
						//判断此时滑块在哪一边
						if(translateX<-that.distance/2 && translateX<0){
							blockObj.style.webkitTransition="-webkit-transform 0.2s ease-out";
							blockObj.style.webkitTransform="translateX(-"+that.distance+"px)";
							if(that.getValue()){
								that.changeAction(that.enable);
								//console.log(that.offAction());
								that.offAction();
							}
							that.input.val(false);
						}else{
							blockObj.style.webkitTransition="-webkit-transform 0.2s ease-out";
							blockObj.style.webkitTransform="translateX(0px)";
							if(!that.getValue()){
								that.changeAction(that.enable);
								//console.log(that.onAction());
								that.onAction();
							}
							that.input.val(true);
						}
						setTimeout(function(){
							//关闭动画
							blockObj.style.webkitTransition="none";
							//当动画完成，自动解除锁定
							isMoving=false;
						},200);
					}else{
						//如果是单击事件，则转换一次开关的状态
						that.setValue(!that.getValue());
					}
				}else{
					//如果开关被关闭，则仅执行changeAction回调方法。
					that.changeAction(that.enable);	
				}
				document.body.removeEventListener(END_EVENT,handler,false);
				document.body.removeEventListener(MOVE_EVENT,moveHandler,false);
				moveFlag=false;
			}
		}
	}
	WmSwitch.prototype.isEnable=function(enable){
		if(enable==undefined){
			return this.enable;
		}
		if(enable){
			this.enable=true;
			this.entity.removeClass("e_dis");
		}else{
			this.enable=false;
			this.entity.addClass("e_dis");
		}
	};
	WmSwitch.prototype.setLabel=function(label){
		var strs=label.split("|");
		this.span[0].innerHTML=strs[0];
		this.span[1].innerHTML=strs[1];
	};
	WmSwitch.prototype.getLabel=function(){
		return this.span[0].innerHTML+"|"+this.span[1].innerHTML;
	};
	WmSwitch.prototype.setValue=function(val){
		if(this.getValue()!=val){
			if(val){
				if(this.onAction){
					this.changeAction(this.enable);
					//console.log(this.onAction());
					this.onAction();
				}
				this.span[2].style.webkitTransition="-webkit-transform 0.4s ease-out";
				this.span[2].style.webkitTransform="translateX(0px)";
			}else{
				if(this.offAction){
					this.changeAction(this.enable);
					//console.log(this.offAction());
					this.offAction();
				}
				this.span[2].style.webkitTransition="-webkit-transform 0.4s ease-out";
				this.span[2].style.webkitTransform="translateX(-"+this.distance+"px)";
			}
			this.input.val(val);
		}
	};
	WmSwitch.prototype.getValue=function(){
		return this.input.val()=="true";
	};
	WmSwitch.prototype.setChangeAction=function(callback){
		this.changeAction=callback;
	}
	WmSwitch.prototype.setOnAction=function(callback){
		this.onAction=callback;	
	};
	WmSwitch.prototype.setOffAction=function(callback){
		this.offAction=callback;
	};
	return WmSwitch;
});

define(["iScroll","util"],function(iScroll){
	function SliderItem(obj){	
		this.entity=$(obj).children("img")[0];
	}
	SliderItem.prototype.setUrl=function(url){
		this.entity.src=url;
		return this;
	}
	SliderItem.prototype.getUrl=function(){
		return this.entity.src;
	}
	SliderItem.prototype.setAlt=function(alt){
		this.entity.alt=alt;
		return this;
	}
	SliderItem.prototype.getAlt=function(){
		return this.entity.alt;
	}
	SliderItem.prototype.setAction=function(callback){
		this.entity.onclick=callback;
		return this;
	}
	function WmSlider(id){
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
		this.duration=1000;
		this.intervalId=-1;
		this.timeoutId=-1;
		this.wrapper=this.entity.children("div").eq(0);
		this.page=this.entity.children("div").eq(1);
		this.lis=this.wrapper.find("li");
		this.items=new Array();
		console.log(this.lis.size());
		var that=this;
		$.each(this.lis,function(index,item){
			var sliderItem=new SliderItem(item);
			that.items.push(sliderItem);
		});
		console.log("size:"+this.items.length)
		this.isAutoPlay=false;
		this.playing=false;
	}	
	WmSlider.prototype.getItems=function(){
		return this.items;
	}
	WmSlider.prototype.create=function(){
		var that=this;
		if(!this.entity){
			alert("无法获取网页元素");
			return;
		}
		this.iScroll=new iScroll(this.wrapper[0],{
			snap:true,
			momentum:false,
			hScrollbar:false,
			onScrollStart:function(){
			},
			onScrollMove:function(){
				if(that.isAutoPlay && that.playing){
					that.pause();
					clearTimeout(that.timeoutId);
				}
			},
			onTouchEnd:function(){
				if(that.isAutoPlay && !that.playing){
					this.timeoutId=setTimeout(function(){
						that.play();
					},5000);	
				}	
			},
			onScrollEnd:function(){
				that.page.children("div .on").removeClass("on");
				that.page.children("div:nth-child("+(this.currPageX+1)+")").addClass("on");
			}
		});
		return this;
	};
	WmSlider.prototype.prev=function(){
		this.iScroll.scrollToPage("prev",0);
		return this;
	};
	WmSlider.prototype.next=function(){
		if(this.iScroll.currPageX+1==this.iScroll.pagesX.length){
			this.iScroll.scrollToPage(0,0);
		}else{
			this.iScroll.scrollToPage("next",0);	
		}
		return this;
	};
	WmSlider.prototype.active=function(num){
		this.iScroll.scrollToPage(parseInt(num),0);
		return this;
	};
	WmSlider.prototype.setDuration=function(duration){
		this.duration=duration;
		return this;
	}
	WmSlider.prototype.getDuration=function(duration){
		return this.duration;
	}
	WmSlider.prototype.isPlaying=function(){
		return this.playing;
	}
	WmSlider.prototype.play=function(){
		if(!this.playing){
			this.isAutoPlay=true;
			this.playing=true;
			var that=this;
			this.intervalId=setInterval(function(){
				that.next();
			},this.duration);
		}
	}
	WmSlider.prototype.pause=function(){
		this.playing=false;
		clearInterval(this.intervalId);
	}
	WmSlider.prototype.stop=function(){
		this.playing=false;
		this.isAutoPlay=false;
		clearInterval(this.intervalId);
	}
	return WmSlider;
});


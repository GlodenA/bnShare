// 代码整理：懒人之家

var FancyForm=function(){
	return{
		inputs:".FancyForm input, .FancyForm textarea",
		setup:function(){
			var a=this;
			this.inputs=$(this.inputs);
			a.inputs.each(function(){
				var c=$(this);
				a.checkVal(c)
			});
			a.inputs.on("keyup blur",function(){
				var c=$(this);
				a.checkVal(c);
			});
		},checkVal:function(a){
			a.val().length>0?a.parent("li").addClass("val"):a.parent("li").removeClass("val")
		}
	}
}();




$(document).ready(function() {
	FancyForm.setup();
});




var searchAjax=function(){};
var G_tocard_maxTips=5;

$(function(){(
	function(){
		
		var a=$(".plus-tag");
		
		$("a em",a).on("click",function(){
			var c=$(this).parents("a"),b=c.attr("title"),d=c.attr("value");
			delTips(b,d)
		});
		
		hasTips=function(b){
			var d=$("a",a),c=false;
			d.each(function(){
				if($(this).attr("title")==b){
					c=true;
					return false
				}
			});
			return c
		};

		isMaxTips=function(){
			return	 $("a",a).length>=G_tocard_maxTips;
		};

		setTips=function(c,d){
			if(hasTips(c)){
				return false
			}if(isMaxTips()){
				$('span.error').html("最多添加"+G_tocard_maxTips+"个标签！");
				return false;
			}
			$('span.error').html("");
			var b=d?'value="'+d+'"':"";
			a.append($("<a "+b+' title="'+c+'" href="javascript:void(0);" ><span>'+c+"</span><em></em></a>"));
			 
			return true;
		};

		delTips=function(b,c){
			if(!hasTips(b)){
				return false
			}
			$("a",a).each(function(){
				var d=$(this);
				if(d.attr("title")==b){
					d.remove();
					return false;
				}
			});
			searchAjax(b,c,false);
			return true
		};

		getTips=function(){
			var b=[];
			$("a",a).each(function(){
				b.push($(this).attr("title"))
			});
			return b
		};

		getTipsId=function(){
			var b=[];
			$("a",a).each(function(){
				b.push($(this).attr("value"))
			});
			return b
		};
		
		getTipsIdAndTag=function(){
			var b=[];
			$("a",a).each(function(){
				b.push($(this).attr("value")+"##"+$(this).attr("title"))
			});
			return b
		}
	}
	
)()});



(function(){
	var $i = $('.plus-tag-add input');
	$i.keydown(function(e){
		
		if(e&&e.keyCode == 13){
			var name = $i.val().toLowerCase();
			if(name != '') setTips(name,-1);
			$i.val('');
		  return false;
		}
	});
	 
})();

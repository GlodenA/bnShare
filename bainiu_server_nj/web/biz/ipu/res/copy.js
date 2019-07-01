var ue = {}
ue.CACHE = {};


function getClipboardData(){
    window.document.ff_clipboard_swf.SetVariable('str', ue.CACHE.CLIPBOARD_TEXT);
}

function hideMenu(){

}


ue.CopyMsg = function(result){ 
    if(result){
        alert("内容已复制至剪贴板!");
    }
    else{
        alert("复制失败! 您使用的浏览器不支持复制功能.");
    }
}



/*
*
* 复制
*/
ue.Copy = function(pStr,hasReturn, isdo){
    var result = false;
    //IE
    if(window.clipboardData)
    {
        window.clipboardData.clearData();
        result = window.clipboardData.setData("Text", pStr);
    }
    //FireFox
    else
    {
        if(top.window && !isdo){
            top.window.ue.Copy(pStr,hasReturn, true);
            return;
        }
        ue.CACHE.CLIPBOARD_TEXT = pStr;
        if(!ue.CACHE.CopyBox){
            ue.CACHE.CopyBox = $('<div id="ff_clipborad_swf_box" style="position:absolute;background: #f8f8f8;top:1px;right:1px;text-decoration: none;z-index:99999999999;border-bottom: 1px solid #ddd;border-left: 1px solid #ddd;color: #777;font-size: 12px; ">'+
                '<div class="copyCut" style="position:relative;overflow:hidden;width:50px; height:30px; line-height:30px;text-align:center;text-decoration:none;" id="js_ff_copy_box_con">Copy</div>'+
                '</div>');
            //创建Flash按钮
            $(document.body).append(ue.CACHE.CopyBox);
        }
        if(document.getElementById('ff_clipboard_swf')){
            $("#ff_clipboard_swf").empty().remove();
        }
        var html = [];
        html.push('<object type="application/x-shockwave-flash" data="clipboard.swf" width="200" height="35" style="position:relative;top:-35px;" id="ff_clipboard_swf">');
        html.push('<param name="quality" value="high" />');
        html.push('<param name="allowScriptAccess" value="sameDomain" />');
        html.push('<param name="allowFullScreen" value="true" />');
        html.push('<param name="wmode" value="transparent" />');
        html.push('</object>');
        $("#js_ff_copy_box_con").append(html.join(""));
        $("#example-content-code").append(ue.CACHE.CopyBox);
        ue.CACHE.CopyBox.show();
        return;
    }
    if(hasReturn){
        return result;
    }
    else{
        ue.CopyMsg(result);
    }
}


//标签控制
ue.Layer = (function(){
    var _cache = {},Return = {};	//缓存类
	
    //最小化
    Return.Min = function(box,mBox,callback){
        if(!_cache.MinBorder){
            _cache.Minborder = $('<div style="z-index:10000000;background: none repeat scroll 0 0 #fff;filter:alpha(opacity=30);-moz-opacity:0.3;opacity:0.3;border:1px soild #ccc;position:absolute;top:0;left0;display:none;"></div>');
            $(document.body).append(_cache.Minborder);
        }
        //mBox.show();
        var w = box.width(), h = box.height(), t = box.offset().top, l = box.offset().left, eT = mBox.offset().top, eL = mBox.offset().left, eW = mBox.width(), eH = mBox.height();
        _cache.Minborder.width(w).height(h).css({
            left:l,
            top:t
        }).show();
        _cache.Minborder.animate({
            width:eW,
            height:eH,
            top:eT,
            left:eL
        },300,function(){
            _cache.Minborder.hide();
        });

        if(callback){
            callback();
        }
    }
	
    /*
	设置标签居中(左右居中，上下1:2)
	
	*/
    Return.Center = function(box,setting){
        var mainBox;
        var cut = 0, t = 0, l = 0;
        if(setting){
            if(setting.Main){
                mainBox = setting.Main;
                t = mainBox.offset().top;
                l = mainBox.offset().left;
            }
            else{
                mainBox = $(window);
            }
            if(setting.Cut != undefined){
                cut = setting.Cut;
            }
        }
        else{
            mainBox = $(window);
        }
		
        var cssT = (mainBox.height() - box.height())/3 + cut + t;
        var cssL = (mainBox.width() - box.width())/2 + cut + l;
        if(cssT < 0){
            cssT = 0;
        }
        if(cssL < 0){
            cssL = 0;
        }
        var st = mainBox.scrollTop();
        if(st){
            cssT += st;
        }
        box.css({
            top: cssT, 
            left: cssL
        });
    }
		
    return Return;
})();
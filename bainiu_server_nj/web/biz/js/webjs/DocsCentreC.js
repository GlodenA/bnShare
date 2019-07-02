require(["mobile","jquery","jcl","layer","common","util"],function(Mobile,$,Wade,Layer,Common,Util){

    // //tab切换
    // //dataTable显示隐藏切换错位重绘
    $('.title-wrap li').click(function (e) {
        e.preventDefault();
        var param = Wade.DataMap();
        if($(this).attr("id")=="apply_query")
        {
            Common.openPage("DocsCentre",param) ;
        }
        if($(this).attr("id")=="fur_apply")
        {
            Common.openPage("DocsCentreC",param) ;
        }
    });

    var form = document.getElementById("dataForm");
    $("#upload_doc").click(function(event) {
        var param = Wade.DataMap();
        //阻止默认事件
        event.preventDefault();
        if(Util.isEmpty($("#DOC_TYPE").val()))
        {
            Common.showFail('请您选择资料类型');
            return;
        }
        if(Util.isEmpty($("#DOC_WRITER").val()))
        {
            Common.showFail("请您输入作者！");
            return ;
        }
        if(Util.isEmpty($("#DOC_LABEL").val()))
        {
            Common.showFail("请您输入资料标签！");
            return ;
        }
        if(Util.isEmpty($("#choice_doc").val()))
        {
            Common.showFail("请您选择文件！");
            return ;
        }
        //上传文件
        $.ajax({
            url: 'fileUploadServlet',
            type: 'POST',
            dataType: 'text',
            data: new FormData(form),
            processData: false,
            contentType: false,
            success: function (responseText) {

                Common.showSuccess(responseText);
                $("#DOC_WRITER").val('');
                $("#DOC_LABEL").val('');
                $("#choice_doc").val('');
                Common.callSvc("DocsCentre.init",param,function(resultData){});	//上传后参数未传递到界面上，初始化
            },
            error: function(){
                Common.showFail("上传失败！");
                console.log("error");
                Common.callSvc("DocsCentre.init",param,function(resultData){});	//上传后参数未传递到界面上，初始化
            }
        });
    });
    // $('.circle').on('click',function(){
    //        $(this).addClass('bule').siblings().removeClass('bule');
    //        //$(this).siblings().removeChild();
    //        $('.cir').remove();
    //        var span = document.createElement('span');
    //        span.setAttribute("class", "cir");
    //        $(this).append(span);
    // })
    $('.radioself').click(function() {
        $(this).siblings().children('.circle').removeClass('blue');
        $(this).children('.circle').addClass('blue');
        $('#DOC_TYPE').val($(this).attr('id')) ;
    });
    $('.paper').click(function() {
        var fileInput = document.getElementById("choice_doc");
        fileInput.click();
    });

    $('.uploading-container   .link .async_area ul li').mouseenter(function(){
        $(this).addClass('active')
    }).mouseleave(function(){
        $(this).removeClass('active')
    });
    $('.uploading-container   .link .async_area ul li').click(function(){
        $(' .link input').val($(this).text());
        var lis = $('.link .async_area ul li');//选择后清空历史
        for(var i=0;i<5;i++){
            lis[i].innerHTML='';
        }
        $('.async_area').css('display','none');
    });
    //热词联想
    $(' .link input').keyup(function(){
        var nttext = $(' .link input').val();
        if(nttext.length<2) {
            $('.async_area').css('display','none');//太短不联想
            return;
        }
        var timer;
        clearTimeout(timer);
        timer = setTimeout(function(){
            var param = Wade.DataMap();
            param.put("USER_ACCT",nttext);
            Common.callSvc("User.getLinkAcct",param,function(res){
                if(res.get("X_RESULTCODE")=="0"){
                    var list = res.get("KEY_LIST");
                    var lis = $('.link .async_area ul li');
                    for(var i=0;i<5;i++){//只联想5个
                        lis[i].innerHTML='';
                    }
                    if(list.length>0){//新增节点
                        for(var i=0;i<list.length && i<5;i++){
                            var listsub = list.get(i);
                            lis[i].innerHTML=listsub.get("NAME")+"|"+listsub.get("USER_ACCT");
                        }
                        $('.link .async_area').css('display', 'block');
                    }
                    else
                        //lis[0].innerHTML="作者不存在于本系统";
                        $('.link .async_area').css('display', 'none');

                }
            });
        }, 500);
    });
    // $(' .link input').mouseleave(function(){
    //     $('.link .async_area').css('display','none');
    // });
    //意见反馈和批次号底部显示
    function footerClass(){
        var windowFlow = $(window).height()-$(".frame_content").height()-60>0?true:false;
        if(windowFlow){
            $("#frame_footer").attr("style","position:absolute; bottom:10px; color:#999; width:100%; text-align:left; line-height:150%;");
        }else{
            $("#frame_footer").attr("style","bottom:5px;text-align: center; width:100%");
        }
    }
});
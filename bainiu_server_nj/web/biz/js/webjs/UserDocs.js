require(["mobile","jquery","jcl","chart","layer","common"],function(Mobile,$,Wade,ChartPie,Layer,Common){

    Common.pagination("UserDocs");
    $(function(){
        var len = $('#example1 tr').length;
        for(var i = 1;i<len;i++){
            $('#example1 tr:eq('+i+') td:first').text(i);
        }
    });

    $('#example1 a[name=downLoad]').bind("click",function(){
        var docId=$(this).parent().attr("DOC_ID");
        location.href = "/fileDownLoadServlet?DOWNLOAD_DOC_ID="+docId;
        var param = Wade.DataMap();
        Common.callSvc("DocsCentre.init",param,function(resultData){});	//上传后参数未传递到界面上，初始化

    });

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
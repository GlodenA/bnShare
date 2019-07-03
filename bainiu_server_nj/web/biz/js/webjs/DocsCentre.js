require(["mobile","jquery","jcl","chart","layer","common"],function(Mobile,$,Wade,ChartPie,Layer,Common){

    Common.pagination("DocsCentre");
    console.log($("#queryTag").val());
    if($("#queryTag").val()=="1"){
        $('#middle-left0').css('display','none');
        $('#middle-content').css('display','none');
        $('#query_table_list').css('display','none');
        $('#middle-left1').css('display','block');
    }
    // //tab切换
    // //dataTable显示隐藏切换错位重绘
    $('.title-wrap li').click(function (e) {
        e.preventDefault();
        var param = Wade.DataMap();
        param.put("QUERY_TAG","0");
        if($(this).attr("id")=="fur_apply")
        {
            Common.openPage("DocsCentreC",param) ;
        }
        if($(this).attr("id")=="apply_query")
        {
            Common.openPage("DocsCentre",param) ;
        }
    });

    $('#example1 a[name=downLoad]').bind("click",function(){
        var docId=$(this).parent().attr("DOC_ID");
        location.href = "/fileDownLoadServlet?DOWNLOAD_DOC_ID="+docId;
        var param = Wade.DataMap();
        Common.callSvc("DocsCentre.init",param,function(resultData){});	//上传后参数未传递到界面上，初始化

    });
    $('#example1 a[name=DOC_AUTHOR_NAME]').bind("click",function(){

        var DOC_AUTHOR_ACCT=$(this).parent().attr("DOC_AUTHOR_ACCT");
        var DOC_AUTHOR_NAME=$(this).html();
        var param = Wade.DataMap();
        param.put("DOC_AUTHOR_ACCT",DOC_AUTHOR_ACCT);
        param.put("DOC_AUTHOR_NAME",DOC_AUTHOR_NAME);
        Common.openPage("UserDocs",param);
        footerClass();
    });

    $('.matter').click(function() {
        $(this).addClass('matteren').siblings().removeClass('matteren');

        var vaule = $(this).attr('id');
        $('#DOC_TYPE').val(vaule);
    })
    $('#dOCSQuery').click(function()//查询
    {
        var param = Wade.DataMap();
        console.log('---DOC_TYPE-----'+$("#DOC_TYPE").val());
        param.put("DOC_TYPE",$("#DOC_TYPE").val());
        param.put("DOC_NAME",$("#HOT_KEY").val());
        param.put("DOC_LABEL",$("#HOT_KEY").val());
        param.put("HOT_KEY",$("#HOT_KEY").val());
        param.put("QUERY_TAG","1");
        Common.openPage("DocsCentre",param) ;

        footerClass();
    });

    //给图标label换行
     function formatter(val) {
        var strs = val.split(''); //字符串数组
        var str = ''
        for(var i = 0, s; s = strs[i++];) { //遍历字符串数组
            str += s;
            if(!(i % 10)) str += '\n'; //按需要求余
        }
        return str
    }
    //报表
    var pieData1= $("#recRank").attr("chartData");
    var pieData= $("#sumRank").attr("chartData");

    var totalDataMap = new Wade.DataMap(pieData);
    var totallist = totalDataMap.get("LINEDATA");
    var totallabels = new Array();
    var totaldata = new Array();

    for(var i=0;i<totallist.length;i++){
        totallabels[i]=formatter(totallist.get(i).get("NAME"));
        totaldata[i]=totallist.get(i).get("COU");
    }

    var monDataMap = new Wade.DataMap(pieData1);
    var monlist = monDataMap.get("LINEDATA");
    var monlabels = new Array();
    var mondata = new Array();

    for(var i=0;i<monlist.length;i++){
        monlabels[i]=formatter(monlist.get(i).get("NAME"));
        mondata[i]=monlist.get(i).get("COU");
    }

    var totalChart = echarts.init($('.download-rank-total')[0]);

    var option = {
        title: {
            text: '下载排行榜',
            x: 'center',
            y: 'top',
            textAlign: 'center',
            padding: 40,
        },
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            }
        },
        legend: {
            data: [{
                icon: 'rect',
                name: '总下载量'
            }],
            right: '20%',
            top: 26,
        },
        grid: {
            left: '15%',
            right: '4%',
            bottom: '9%',
            containLabel: false
        },
        xAxis: {
            type: 'value',
            axisLine: {
                show: false
            },
            axisTick: {
                show: false
            },
            splitLine: {
                lineStyle: {
                    type: 'dashed'
                }
            }
        },
        yAxis: {
            type: 'category',
            axisLabel: {
                align: 'left',
                margin: 120,
                textStyle: {
                    fontSize: 14,
                    color: '#545454',
                },
                //rotate:30,
                interval: 0
            },
            axisLine: {
                lineStyle: {
                    color: '#c3c3c3'
                }
            },
            data: totallabels,
        },
        series: [{
            name: '总下载量',
            type: 'bar',
            barWidth: 24,
            itemStyle: {
                borderWidth: 1,
                borderColor: '#1f97ff'
            },
            data: totaldata
        }],
        color: new echarts.graphic.LinearGradient(
            1, 0, 0, 1, [{
                offset: 0,
                color: '#2094ff'
            }, {
                offset: 1,
                color: '#51c8ff'
            }]
        )
    };
    totalChart.setOption(option);

    var monthChart = echarts.init($('.download-rank-month')[0]);

    var monthOption = {
        title: {
            show: false,
        },
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            }
        },
        legend: {
            data: [{
                icon: 'rect',
                name: '近一月下载量'
            }],
            right: '20%',
            top: 26,
        },
        grid: {
            left: '15%',
            right: '4%',
            bottom: '9%',
            containLabel: false
        },
        xAxis: {
            type: 'value',
            axisLine: {
                show: false
            },
            axisTick: {
                show: false
            },
            splitLine: {
                lineStyle: {
                    type: 'dashed'
                }
            }
        },
        yAxis: {
            type: 'category',
            axisLabel: {
                align: 'left',
                margin: 120,
                textStyle: {
                    fontSize: 14,
                    color: '#545454',
                }
            },
            axisLine: {
                lineStyle: {
                    color: '#c3c3c3'
                }
            },
            data: monlabels,
        },
        series: [{
            name: '近一月下载量',
            type: 'bar',
            barWidth: 24,
            itemStyle: {
                borderWidth: 1,
                borderColor: '#7179ff'
            },
            data: mondata
        }],
        color: new echarts.graphic.LinearGradient(
            1, 0, 0, 1, [{
                offset: 0,
                color: '#515bff'
            }, {
                offset: 1,
                color: '#cfd2ff'
            }]
        )
    };
    monthChart.setOption(monthOption);


    var queryhisData = $("#queryHotKey").attr("chartData");
    console.log("queryhisData===="+queryhisData);
    var pieDataMap = new Wade.DataMap(queryhisData);
    var list = pieDataMap.get("QUERYDATA");
    for(var i=0;i<list.length;i++){
        var hotKey = list.get(i).get("HOT_KEY");
        $("#hisQryKeyWord").append(" <div class=\"case\"> <span class=\"hisqrydata\">"+hotKey+"</span></div>");
    }
    $('.hisqrydata').click(function() {
        $('#HOT_KEY').val($(this).html()) ;
    })
    /*tab切换*/
    $('.data_analysis_panel .header_title .title li').on('click',function(){
        var index=$(this).index();
        $(this).addClass('active')
        $(this).siblings().removeClass('active')
        console.info($(this).parents('.data_analysis_panel').find('.con'))
        $(this).parents('.data_analysis_panel').find('.con').children('.con_item').css('display','none')
        $(this).parents('.data_analysis_panel').find('.con').children('.con_item').eq(index).show();
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
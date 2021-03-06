require(["mobile", "jquery", "jcl", "chart", "layer", "common"], function (Mobile, $, Wade, ChartPie, Layer, Common) {

    Common.pagination("DocsCentre");

    $(function(){
        var len = $('#example1 tr').length;
        for(var i = 1;i<len;i++){
            $('#example1 tr:eq('+i+') td:first').text(i);
        }

        var len1 = $('#example2 tr').length;
        for(var i = 1;i<len1;i++){
            $('#example2 tr:eq('+i+') td:first').text(i);
        }
    });

    if ($("#queryTag").val() == "1") {
        $('#middle-left0').css('display', 'none');
        $('#middle-content').css('display', 'none');
        $('#query_table_list').css('display', 'none');
        $('#middle-left1').css('display', 'block');
    }
    if ($("#queryTag").val() == "2") {
        $('#middle-left0').css('display', 'block');
        $('#middle-content').css('display', 'block');
        $('#query_table_list').css('display', 'block');
        $('#middle-left1').css('display', 'none');
    }

    // //tab切换
    // //dataTable显示隐藏切换错位重绘
    $('.title-wrap li').click(function flush(e) {
        e.preventDefault();
        var param = Wade.DataMap();
        param.put("QUERY_TAG", "0");
        if ($(this).attr("id") == "fur_apply") {
            Common.openPage("DocsCentreC", param);
        }
        if ($(this).attr("id") == "apply_query") {
            Common.openPage("DocsCentre", param);
        }
    });

    $('a[name=downLoad]').bind("click", function () {
        var docId = $(this).parent().attr("DOC_ID");
        location.href = "/fileDownLoadServlet?DOWNLOAD_DOC_ID=" + docId;
        var param = Wade.DataMap();
        Common.callSvc("DocsCentre.init", param, function (resultData) {
        });	//上传后参数未传递到界面上，初始化

    });
    $('a[name=DOC_AUTHOR_NAME]').bind("click", function () {

        var DOC_AUTHOR_ACCT = $(this).parent().attr("DOC_AUTHOR_ACCT");
        var DOC_AUTHOR_NAME = $(this).html();
        var param = Wade.DataMap();
        param.put("DOC_AUTHOR_ACCT", DOC_AUTHOR_ACCT);
        param.put("DOC_AUTHOR_NAME", DOC_AUTHOR_NAME);
        Common.openPage("UserDocs", param);
        footerClass();
    });
    $('a[name=HOTKEYSORT]').bind("click", function () {
        var HOT_KEY = document.getElementById("HOT_KEY");
        HOT_KEY.value = $(this).html();
        var param = Wade.DataMap();
        console.log('---DOC_TYPE-----' + $("#DOC_TYPE").val());
        param.put("DOC_TYPE", $("#DOC_TYPE").val());
        param.put("DOC_NAME", $("#HOT_KEY").val());
        param.put("DOC_LABEL", $("#HOT_KEY").val());
        param.put("HOT_KEY", $("#HOT_KEY").val());
        param.put("QUERY_TAG", "1");
        Common.openPage("DocsCentre", param);
        footerClass();
    });

    $('a[name=UpData]').bind("click", function() {
        var id = document.getElementById("id"),
            name = document.getElementById("name"),
            author = document.getElementById("author"),
            time = document.getElementById("time"),
            tag = document.getElementById("tag"),
            info = document.getElementById("info");
        id.value=$(this).parent().attr("DOC_ID");
        name.value= $(this).parent().attr("DOC_NAME");
        author.value= $(this).parent().attr("DOC_AUTHOR_NAME");
        time.value=$(this).parent().attr("INS_TIME");
        tag.value=$(this).parent().attr("DOC_LABEL");
        info.value=$(this).parent().attr("DOC_SUMMARY");
        var sbtitle = document.getElementById("doc_up");
        sbtitle.style.display = 'block';
    });

    $('a[name=UpDataBak]').bind("click", function() {
        var id = document.getElementById("id1"),
            name = document.getElementById("name1"),
            author = document.getElementById("author1"),
            time = document.getElementById("time1"),
            tag = document.getElementById("tag1"),
            info = document.getElementById("info1");
        id.value=$(this).parent().attr("DOC_ID");
        name.value= $(this).parent().attr("DOC_NAME");
        author.value= $(this).parent().attr("DOC_AUTHOR_NAME");
        time.value=$(this).parent().attr("INS_TIME");
        tag.value=$(this).parent().attr("DOC_LABEL");
        info.value=$(this).parent().attr("DOC_SUMMARY");
        var sbtitle = document.getElementById("doc_up_1");
        sbtitle.style.display = 'block';
    });

    $('a[name=Delete]').bind("click", function () {
        var is_id = $(this).parent().attr("DOC_ID");
        var param = Wade.DataMap();
        param.put("DOC_ID", is_id);
        param.put("DATA_DOCSCENTRE_OPER",document.getElementById("DATA_DOCSCENTRE_OPER").value);
        Common.callSvc("DocsCentre.DeleDocByID", param, function (res) {
            if(res.get("result")==0){
                Common.showFail("对不起，您没有删除权限！");
            }
            else {
                if (res.get("delete") == 0) {
                    Common.showFail("删除失败，路径不存在");
                }
                else {
                    Common.showSuccess("删除成功");
                    var params = Wade.DataMap();
                    params.put("QUERY_TAG", "2");
                    Common.openPage("DocsCentre", params);
                }
            }
        });
    })
    $('a[name=updoc]').bind("click", function () {
        var param = Wade.DataMap();
        param.put("DOC_ID", $("#id").val());
        param.put("DOC_NAME", $("#name").val());
        param.put("DOC_LABEL", $("#tag").val());
        param.put("DOC_SUMMARY", $("#info").val());
        Common.callSvc("DocsCentre.updateDocsByID", param, function () {
            var sbtitle = document.getElementById("query_table_list");
            sbtitle.style.display = 'none';
            Common.showSuccess("保存成功");
            var params = Wade.DataMap();
            params.put("QUERY_TAG", "2");
            Common.openPage("DocsCentre", params);
        });
    })

    $('a[name=updoc_1]').bind("click", function () {
        var param = Wade.DataMap();
        param.put("DOC_ID", $("#id1").val());
        param.put("DOC_NAME", $("#name1").val());
        param.put("DOC_LABEL", $("#tag1").val());
        param.put("DOC_SUMMARY", $("#info1").val());
        Common.callSvc("DocsCentre.updateDocsByID", param, function () {
            var sbtitle = document.getElementById("query_table_list");
            sbtitle.style.display = 'none';
            Common.showSuccess("保存成功");
            var params = Wade.DataMap();
            params.put("QUERY_TAG", "2");
            Common.openPage("DocsCentre", params);
        });
    })

    $('.matter').click(function () {
        $(this).addClass('matteren').siblings().removeClass('matteren');

        var vaule = $(this).attr('id');
        $('#DOC_TYPE').val(vaule);
    })
    $('#dOCSQuery').click(function ()//查询
    {
        var param = Wade.DataMap();
        console.log('---DOC_TYPE-----' + $("#DOC_TYPE").val());
        param.put("DOC_TYPE", $("#DOC_TYPE").val());
        param.put("DOC_NAME", $("#HOT_KEY").val());
        param.put("DOC_LABEL", $("#HOT_KEY").val());
        param.put("HOT_KEY", $("#HOT_KEY").val());
        param.put("QUERY_TAG", "1");
        Common.openPage("DocsCentre", param);
        footerClass();
    });

    //给图标label换行
    function formatter(val) {
        var strs = val.split(''); //字符串数组
        var str = ''
        for (var i = 0, s; s = strs[i++];) { //遍历字符串数组
            str += s;
            if (!(i % 10)) str += '\n'; //按需要求余
        }
        return str
    }

    //报表
    var pieData1 = $("#recRank").attr("chartData");
    var pieData = $("#sumRank").attr("chartData");

    var totalDataMap = new Wade.DataMap(pieData);
    var totallist = totalDataMap.get("LINEDATA");
    var totallabels = new Array();
    var totaldata = new Array();

    for (var i = 0; i < totallist.length; i++) {
        totallabels[i] = formatter(totallist.get(i).get("NAME"));
        totaldata[i] = totallist.get(i).get("COU");
    }

    var monDataMap = new Wade.DataMap(pieData1);
    var monlist = monDataMap.get("LINEDATA");
    var monlabels = new Array();
    var mondata = new Array();

    for (var i = 0; i < monlist.length; i++) {
        monlabels[i] = formatter(monlist.get(i).get("NAME"));
        mondata[i] = monlist.get(i).get("COU");
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
            left: '20%',
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
                margin: 160,
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
    totalChart.on('click', function (params) {
        var n = params.name;
        var name = n.replace(/\n/,"");
        var param = Wade.DataMap();
        param.put("DOC_NAME", name.toString());
        Common.callSvc("DocsCentre.queryDocsSum", param, function (res){
            var list = res.get("DOC_INFO");
            var li = list.get(0);
            console.log(list);
            var innerHtml = "";
            var is_author = li.get("DOC_AUTHOR_NAME"),
                upd_time = li.get("INS_TIME"),
                is_tag = li.get("DOC_LABEL"),
                is_downnum =li.get("DOWNLOAD_CNT"),
                // is_path=$(this).parent().attr("DOC_PATH"),
                is_info = li.get("DOC_SUMMARY");

            innerHtml = innerHtml
                + '<tr>'
                + '	<td class="active" width="25%">资料名称</td>'
                + '	<td width="75%">' + name + '</td>'
                + '</tr>'
                + '<tr>'
                + '	<td class="active" width="25%">上传时间</td>'
                + '	<td>' + upd_time + '</td>'
                + '</tr>'
                + '<tr>'
                + '	<td class="active" width="25%">作者</td>'
                + '	<td>' + is_author + '</td>'
                + '</tr>'
                + '<tr>'
                + '	<td class="active" width="25%">资料标签</td>'
                + '	<td>' + is_tag + '</td>'
                + '</tr>'
                + '<tr>'
                + '	<td class="active" width="25%">下载次数</td>'
                + '	<td>' + is_downnum + '</td>'
                + '</tr>'
                + '<tr>'
                + '	<td class="active" width="25%" >简介</td>'
                + '	<td>' + is_info + '</td>'
                + '</tr>';
            $('#querygroup').html(innerHtml);
            $(".modal-box").show();
            $("#bg").height(document.body.clientHeight);
            var windowFlow = $(window).height() - $(".frame_content").height() - 60 > 0 ? true : false;
            if (windowFlow) {
                $("#bg").height($(window).height());
            } else {
                $("#bg").height(document.body.clientHeight);
            }
            $("#bg").show();
        });

    });

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
            left: '20%',
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
                margin: 160,
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
    monthChart.on('click', function (params) {
        var n = params.name;
        var name = n.replace(/\n/,"");
        var param = Wade.DataMap();
        param.put("DOC_NAME", name.toString());
        Common.callSvc("DocsCentre.queryDocsSum", param, function (res){
            var list = res.get("DOC_INFO");
            var li = list.get(0);
            console.log(list);
            var innerHtml = "";
            var is_author = li.get("DOC_AUTHOR_NAME"),
                upd_time = li.get("INS_TIME"),
                is_tag = li.get("DOC_LABEL"),
                is_downnum =li.get("DOWNLOAD_CNT"),
                // is_path=$(this).parent().attr("DOC_PATH"),
                is_info = li.get("DOC_SUMMARY");

            innerHtml = innerHtml
                + '<tr>'
                + '	<td class="active" width="25%">资料名称</td>'
                + '	<td width="75%">' + name + '</td>'
                + '</tr>'
                + '<tr>'
                + '	<td class="active" width="25%">上传时间</td>'
                + '	<td>' + upd_time + '</td>'
                + '</tr>'
                + '<tr>'
                + '	<td class="active" width="25%">作者</td>'
                + '	<td>' + is_author + '</td>'
                + '</tr>'
                + '<tr>'
                + '	<td class="active" width="25%">资料标签</td>'
                + '	<td>' + is_tag + '</td>'
                + '</tr>'
                + '<tr>'
                + '	<td class="active" width="25%">下载次数</td>'
                + '	<td>' + is_downnum + '</td>'
                + '</tr>'
                + '<tr>'
                + '	<td class="active" width="25%" >简介</td>'
                + '	<td>' + is_info + '</td>'
                + '</tr>';
            $('#querygroup').html(innerHtml);
            $(".modal-box").show();
            $("#bg").height(document.body.clientHeight);
            var windowFlow = $(window).height() - $(".frame_content").height() - 60 > 0 ? true : false;
            if (windowFlow) {
                $("#bg").height($(window).height());
            } else {
                $("#bg").height(document.body.clientHeight);
            }
            $("#bg").show();
        });
    });

    var queryhisData = $("#queryHotKey").attr("chartData");
    console.log("queryhisData====" + queryhisData);
    var pieDataMap = new Wade.DataMap(queryhisData);
    var list = pieDataMap.get("QUERYDATA");
    for (var i = 0; i < list.length; i++) {
        var hotKey = list.get(i).get("HOT_KEY");
        $("#hisQryKeyWord").append(" <div class=\"case\"> <span class=\"hisqrydata\">" + hotKey + "</span></div>");
    }
    $('.hisqrydata').click(function () {
        $('#HOT_KEY').val($(this).html());
    })
    /*tab切换*/
    $('.data_analysis_panel .header_title .title li').on('click', function () {
        var index = $(this).index();
        $(this).addClass('active')
        $(this).siblings().removeClass('active')
        console.info($(this).parents('.data_analysis_panel').find('.con'))
        $(this).parents('.data_analysis_panel').find('.con').children('.con_item').css('display', 'none')
        $(this).parents('.data_analysis_panel').find('.con').children('.con_item').eq(index).show();
    });
    $('.time').click(function () {

        $('.attention').css("background", "#ffffff");
        $('.attention').css("color", "#333333");
        $('.attention').css("border", "2px solid #dddddd");

        $('.time').css("background", "#22A4F0");
        $('.time').css("color", "#ffffff");
        $('.time').css("border", "2px solid #1098f4");
        var param = Wade.DataMap();
        param.put("TYPE_KEY", "ONE");//查询今日关注
        Common.callSvc("DocsCentre.queryHotKeySort", param, function (res) {
            if (res.get("X_RESULTCODE") == "0") {
                var list = res.get("HOTKEY_LIST");
                var lis = $('.middle-right .list ul li');
                for(var i=0;i<5;i++){//先置空
                    var a = lis[i + 1].children[1];
                    a.children[0].innerHTML="";
                    lis[i + 1].children[2].innerHTML="";
                }
                if (list.length > 0) {//新增节点
                    for (var i = 0; i < list.length; i++) {
                        var listsub = list.get(i);
                        var a = lis[i + 1].children[1];
                        a.children[0].innerHTML = listsub.get("HOT_KEY");
                        lis[i + 1].children[2].innerHTML = listsub.get("VALUE");
                    }
                }

            }
        });

    });
    $('.time').click();
    $('.attention').click(function () {
        $('.time').css("background", "#ffffff");
        $('.time').css("color", "#333333");
        $('.time').css("border", "2px solid #dddddd");

        $('.attention').css("background", "#22A4F0");
        $('.attention').css("color", "#ffffff");
        $('.attention').css("border", "2px solid #1098f4");
        var param = Wade.DataMap();
        param.put("TYPE_KEY", "seven");//查询周关注
        Common.callSvc("DocsCentre.queryHotKeySort", param, function (res) {
            if (res.get("X_RESULTCODE") == "0") {
                var list = res.get("HOTKEY_LIST");
                var lis = $('.middle-right .list ul li');
                for(var i=0;i<5;i++){//先置空
                    var a = lis[i + 1].children[1];
                    a.children[0].innerHTML="";
                    lis[i + 1].children[2].innerHTML="";
                }
                if (list.length > 0) {//新增节点
                    for (var i = 0; i < list.length; i++) {
                        var listsub = list.get(i);
                        var a = lis[i + 1].children[1];
                        a.children[0].innerHTML = listsub.get("HOT_KEY");
                        lis[i + 1].children[2].innerHTML = listsub.get("VALUE");
                    }
                }

            }
        });
    });

    //热词联想
    $('#HOT_KEY').keyup(function(){
        var timer;
        clearTimeout(timer);
        timer = setTimeout(function(){
            $.ajax({
                url:'mobiledata?action=DocsCentre.getHotKey&data={"KEY_WORD":"'+$('#HOT_KEY').val()+'"}',
                type:'POST',
                dataType:'html',
                //data:'UTF-8',
                success:function(res){
                    var msg =  eval("(" + res + ")");;
                    if(0 == msg.X_RESULTCODE){
                        var list = msg.KEY_LIST;
                        var lis = $('.async_area ul li');
                        if(list.length>0){
                            //新增节点
                            for(var i=0;i<6;i++){
                                lis[i].innerHTML='';
                            }
                            for(var i=0;i<list.length && i<6;i++){
                                lis[i].innerHTML=list[i].KEYWORD;
                            }

                        }

                    }

                }
            })
        }, 500);
        $('.async_area').css('display','block');

    });
    //意见反馈和批次号底部显示
    function footerClass() {
        var windowFlow = $(window).height() - $(".frame_content").height() - 60 > 0 ? true : false;
        if (windowFlow) {
            $("#frame_footer").attr("style", "position:absolute; bottom:10px; color:#999; width:100%; text-align:left; line-height:150%;");
        } else {
            $("#frame_footer").attr("style", "bottom:5px;text-align: center; width:100%");
        }
    }

    $("#modal-close").bind("click", function () {
        $(".modal-box").hide();
        $("#bg").hide();
    });
    // 表格点击事件
    $("tbody tr td[name='itemCkecked']").bind("click", function () {//点击浮层
        var innerHtml = "";
        var is_name = $(this).parent().attr("DOC_NAME"),
            is_author = $(this).parent().attr("DOC_AUTHOR_NAME"),
            upd_time = $(this).parent().attr("INS_TIME"),
            is_tag = $(this).parent().attr("DOC_LABEL"),
            is_downnum = $(this).parent().attr("DOWNLOAD_CNT"),
            // is_path=$(this).parent().attr("DOC_PATH"),
            is_info = $(this).parent().attr("DOC_SUMMARY");

        innerHtml = innerHtml
            + '<tr>'
            + '	<td class="active" width="25%">资料名称</td>'
            + '	<td width="75%">' + is_name + '</td>'
            + '</tr>'
            + '<tr>'
            + '	<td class="active" width="25%">上传时间</td>'
            + '	<td>' + upd_time + '</td>'
            + '</tr>'
            + '<tr>'
            + '	<td class="active" width="25%">作者</td>'
            + '	<td>' + is_author + '</td>'
            + '</tr>'
            + '<tr>'
            + '	<td class="active" width="25%">资料标签</td>'
            + '	<td>' + is_tag + '</td>'
            + '</tr>'
            + '<tr>'
            + '	<td class="active" width="25%">下载次数</td>'
            + '	<td>' + is_downnum + '</td>'
            + '</tr>'
            + '<tr>'
            + '	<td class="active" width="25%" >简介</td>'
            + '	<td>' + is_info + '</td>'
            + '</tr>';
        $('#querygroup').html(innerHtml);
        $(".modal-box").show();
        $("#bg").height(document.body.clientHeight);
        var windowFlow = $(window).height() - $(".frame_content").height() - 60 > 0 ? true : false;
        if (windowFlow) {
            $("#bg").height($(window).height());
        } else {
            $("#bg").height(document.body.clientHeight);
        }
        $("#bg").show();

    })
});
/**
 * Created by Administrator on 2015/9/7 0007.
 */
$(function(){

    $.Bn={};
    $.Bn= {
        PhaceHolder: function () {
            $('.sh_ipt').placeholder();
        },
        HomeShSub: function () {
            $('.home_sh_form .sh_ipt input').focus();
            $('.home_sh_form .sh_ipt input').focus(function(){
              $(this).parent().css({'borderColor':'#ff9831'})
            }).blur(function(){
                $(this).parent().css({'borderColor':'#d5d5d5'})
            });
            $('.home_sh_form .sh_sub').click(function () {
                $('.home_sh_form').submit();
            });
        },
        Nav: function () {
            $('.nav').on('click', 'dl dt', function () {
                var _obj = $(this).parent('dl');
                if (_obj.hasClass('active')) {
                    _obj.removeClass('active');
                    if(_obj.children('dd').size()){
                        $('.nav dl dd').slideUp();
                    }
                } else {
                    _obj.addClass('active');
                    _obj.siblings('dl').removeClass('active');
                    $('.nav dl').not('.active').each(function () {
                        $(this).children('dd').css({'display': 'none'});
                    });
                    if(_obj.children('dd').size()){
                        $('.nav dl.active dd').slideDown();
                    }
                }
            });
            $('.nav').on('click', 'dl dd', function () {
                $('.nav dl dd').removeClass('active')
                $(this).addClass('active')
            });
            $('.main_wrap .admin_ipt').mouseenter(function(){
                $(this).addClass('active');
                $(this).children('dl').fadeIn();
            }).mouseleave(function(){
                $(this).removeClass('active');
                $(this).children('dl').css({'display':'none'});
            });
        }

    }

    $.Bn.PhaceHolder();
    $.Bn.HomeShSub();
    $.Bn.Nav();


});

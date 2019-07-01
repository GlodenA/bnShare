/**
 * Created by Administrator on 2015/9/7 0007.
 */
$(function(){

    $.Bn={};
    $.Bn= {
        PhaceHolder: function () {
            $('.sh_ipt input').placeholder();
        },
        HomeShSub: function () {
            $('.home_sh_form .sh_ipt input').focus();
            $('.bn_home .async_area ul li').mouseenter(function(){
                $(this).addClass('active')
            }).mouseleave(function(){
                $(this).removeClass('active')
            });
            $('.bn_home .async_area ul li').click(function(){
            $('.home_sh_form .sh_ipt input').val($(this).text());
            $('.async_area').css('display','none');
            });

        },
        Nav: function () {

            $('.main_wrap .admin_ipt').mouseenter(function(){
                $(this).addClass('active');
                $(this).children('dl').fadeIn();
            }).mouseleave(function(){
                $(this).removeClass('active');
                $(this).children('dl').css({'display':'none'});
            });
        },
        Select:function(){
          $('.sel_ipt').on({
              'mouseenter': function () {
                  $(this).children('.choose_pop').css({'display': 'block'});
              },
              'mouseleave':function(){
                  $(this).children('.choose_pop').css({'display': 'none'});
              }
          });

            $('.choose_pop').on('click','ul li a',function(){
                $('.choose_list').text($(this).text())
            });

        }

    }

    $.Bn.PhaceHolder();
    $.Bn.HomeShSub();
    $.Bn.Nav();
    $.Bn.Select();


});

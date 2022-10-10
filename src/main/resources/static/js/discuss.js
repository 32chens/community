function like(btn, entityType, entityId) {
    $.post(
        CONTEXT_PATH + "/like",
        {"entityType":entityType,"entityId":entityId},
        function(data) {
            data = $.parseJSON(data);
            if(data.code == 0) {
                $(btn).children("i").text(data.likeCount);
                $(btn).children("b").text(data.likeStatus==1?'已赞':"赞");
            } else if (data.code == 403){
                alert(data.msg,{ icon: 1, closeBtn: 0 }, function () {
                    window.location = CONTEXT_PATH+"login"
                    console.log("用户未登录")
                });
            }else {
                alert(data.msg);
            }
        }
    );
}
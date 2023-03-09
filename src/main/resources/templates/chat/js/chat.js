const channelUrl = "192.168.0.8:7080";
const mainUrl = window.location.href.split('http://')[1];

var requestDomain;
if(mainUrl.indexOf('.com')>0||mainUrl.indexOf('.co.kr')){
	requestDomain = mainUrl.split('/')[0]+'/'+mainUrl.split('/')[1];
}else{
	requestDomain = mainUrl.split('/')[0];
}

$(document).ready(function(){
  	var preloadbg = document.createElement("img");
  	preloadbg.src = "https://s3-us-west-2.amazonaws.com/s.cdpn.io/245657/timeline1.png";
	
  	$("#searchfield").focus(function(){
		if($(this).val() == "Search contacts..."){
			$(this).val("");
		}
	});
	$("#searchfield").focusout(function(){
		if($(this).val() == ""){
			$(this).val("Search contacts...");
			
		}
	});
	
	$("#sendmessage input").focus(function(){
		if($(this).val() == "Send message..."){
			$(this).val("");
		}
	});
	$("#sendmessage input").focusout(function(){
		if($(this).val() == ""){
			$(this).val("Send message...");
			
		}
	});
	refreshUserlist();
	const websocket = new WebSocket('ws://'+channelUrl+'/ws/channel');
	websocket.onmessage = onMessage
	
    //websocket.onopen = onOpen;
    //websocket.onclose = onClose;
});
function onMessage(msg) {
	console.log('onMessage', msg)
	if(msg){
		var data = JSON.parse(msg.data);
		var message = null;
		talkMaker(data)
	}
}
function sendMessage(p_channelCd){
    let msg = document.getElementById("msg");
    let messageEntity = new Object();
    
    messageEntity.channelCd = p_channelCd;
    messageEntity.userCd = $("#chatbox input[name='USER_CD_LOGIN']").val();
    messageEntity.sendType = 'COMM';
    messageEntity.messageType = '';
    messageEntity.message = msg.value;
    
    axios.post('http://'+channelUrl+'/channel/sendMessage', messageEntity)
    .then(response => {
        console.log(response)
    })
    .catch(response => {
        console.log(response)
    });
    msg.value = '';
}
$.fn.hasScrollBar = function() {
    return (this.prop("scrollHeight") == 0 && this.prop("clientHeight") == 0) || (this.prop("scrollHeight") > this.prop("clientHeight"));
};
var OPEN_TIMELINE_YN = false;
function refreshUserlist(p_page) {
	if (OPEN_TIMELINE_YN == false) {
		OPEN_TIMELINE_YN = true;
		var strParam = "";
		if(!p_page){
			var p_page = $("#chat_container input[name='current_page_num']").val();
			console.log(p_page)
		}else{
			if(p_page==1){
				$("#friends").html('');
			}
		}
		strParam += "&p_page=" + p_page;
		$.post("chat_user_list.jsp", strParam, function(p_data, p_status) {
			if (p_data.result == "Y") {
				var arrRecordBean = p_data.list;
				$("#chat_container input[name='current_page_num']").val(p_data.p_page);
				$("#chatbox input[name='USER_CD_LOGIN']").val(p_data.clsUserBean_Login.UserCD);
				$("#chatbox input[name='USER_NM_LOGIN']").val(p_data.clsUserBean_Login.UserNM);
				var html = '';
				$("#friends").append(p_data.html);
			} else {
			}
			OPEN_TIMELINE_YN = false;
		},"JSON");
	}
	$('#friends').scroll(function(){
		var noMore = false;
		var scrollTop = $('#friends').scrollTop();
		var innerHeight = $('#friends').height();
		var scrollHeight = $('#friends')[0].scrollHeight;
		var current_page_num = $("#chat_container input[name='current_page_num']").val()
		if (!noMore) {
			if (scrollTop + innerHeight >= scrollHeight) {
				if(innerHeight!=0){
					//스크롤이 바닥치면 뭐할지 여기에 정의 시작
					$('#friends').scrollTop(scrollHeight-150);
					$("#friends input[name='current_page_num']").val(Number(current_page_num)+Number(1))
					refreshUserlist();
					noMore = (true);
				}
			}
		}
	});
}
function refreshChatList(){
	
}
function rowClick(p_obj){
	$('#chat-messages').html('');
	var createRoomPromise = createRoom(p_obj);
	createRoomPromise
	.then( (p_data) => {
		console.log(p_data.message);
		let channelCd = p_data.result.channel.channelCd;
		refreshMessagelist(channelCd)
		$('#sendmessage #send').on("click", () => {
			sendMessage(channelCd);
		})
	})
	.catch( (p_data) => {
		
	})
	var childOffset = $(p_obj).offset();
	var parentOffset = $(p_obj).parent().parent().offset();
	var childTop = childOffset.top - parentOffset.top;
	var clone = $(p_obj).find('img').eq(0).clone();
	var top = childTop+12+"px";
	
	$(clone).css({'top': top}).addClass("floatingImg").appendTo("#chatbox");									
	
	setTimeout(function(){$("#profile p").addClass("animate");$("#profile").addClass("animate");}, 100);
	setTimeout(function(){
		$("#chat-messages").addClass("animate");
		$('.cx, .cy').addClass('s1');
		setTimeout(function(){$('.cx, .cy').addClass('s2');}, 100);
		setTimeout(function(){$('.cx, .cy').addClass('s3');}, 200);			
	}, 150);														
	
	$('.floatingImg').animate({
		'width': "68px",
		'top':'20px'
	}, 200);
	
	var name = $(p_obj).find("p strong").html();
	var email = $(p_obj).find("p span").html();														
	$("#profile p").html(name);
	$("#profile span").html(email);			
	
	$(".message").not(".right").find("img").attr("src", $(clone).attr("src"));									
	$('#chat_container').fadeOut();
	$('#chatview').fadeIn();
	
	$('#close').unbind("click").click(function(){				
		$("#chat-messages, #profile, #profile p").removeClass("animate");
		$('.cx, .cy').removeClass("s1 s2 s3");
		$('.floatingImg').animate({
			'width': "40px",
			'top':top,
			'left': '12px'
		}, 200, function(){$('.floatingImg').remove()});				
		
		setTimeout(function(){
			$('#chatview').fadeOut();
			$('#chat_container').fadeIn();				
		}, 50);
	});
}
function createRoom(p_obj) {
	return new Promise((resolve, reject) => {
		var strParam = '';
		strParam += "&loginUserCd="+$("#chatbox input[name='USER_CD_LOGIN']").val();
		strParam += "&toUserCd="+$(p_obj).find('.USER_CD').val();
		
		/*axios.post('http://'+channelUrl+'/channel/createChannelWithUser', strParam)
	    .then(response => {
	    	console.log(response)
	    })
	    .catch(response => {
	        console.log(response)
	    });*/
		
		$.post('http://'+channelUrl+'/channel/createChannelWithUser', strParam, (p_data, p_status) => {
			if(p_data.statusCode=='200'){
				resolve(p_data)
			}else{
				reject(p_data);
			}
		},"JSON");
	});
}
function refreshMessagelist(p_channelCd){
   var params = new URLSearchParams();
   axios.get('http://'+channelUrl+'/channel/loadChannel?channelCd='+p_channelCd, params)
   .then(response => {
  	   let chatArr = response.data.result.chatArr;
       for(var i=0; i<chatArr.length; i++){
  	 		talkMaker(chatArr[i])
       }
   })
   .catch(response => {
       console.log(response)
   });
}
function talkMaker(chat){
	//현재 세션에 로그인 한 사람
	var cur_session = $("#chatbox input[name='USER_CD_LOGIN']").val();
    var chatStr = '';
    //로그인 한 클라이언트와 타 클라이언트를 분류하기 위함
    if(cur_session==chat.userCd){
    	chatStr += "<div class='message right'>";
    }else{
    	chatStr += "<div class='message'>";
    }
    chatStr += 			"<img src='../../img/faces/face_common.jpg' alt=''>";
    chatStr += 			"<div class='bubble'>";
    chatStr += 				chat.message;
    chatStr += 				"<div class='corner'></div>";
    chatStr += 				"<span>3 min</span>"
	chatStr += 			"</div>";
	chatStr += 		"</div>";
	
	$('#chat-messages').append(chatStr)
}

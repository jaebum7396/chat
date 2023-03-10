package chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import chat.model.MessageEntity;
import chat.service.ChannelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Api(tags = "ChannelController")
@Tag(name = "ChannelController", description = "채팅 방 서비스 제공")
@Controller
@RequiredArgsConstructor
@RequestMapping("/channel")
public class ChannelController {
	@Autowired
	ChannelService chatService;
	
	@ApiOperation(value = "방 개설", notes = "ChannelName 입력 방개설")
	@PostMapping("/createChannel")
	@ResponseBody
	public ResponseEntity createChannel(@RequestParam String name) {
		return chatService.createChannel(name);
	}
	
	@ApiOperation(value = "toUser 방 개설", notes = "toUser 방개설")
	@PostMapping("/createChannelWithUser")
	@ResponseBody
	public ResponseEntity createChannelWithUser(@RequestParam Long loginUserCd, @RequestParam Long toUserCd) {
		return chatService.createChannelWithUser(loginUserCd, toUserCd);
	}
	
	@ApiOperation(value = "메시지전송", notes = "메시지전송")
	@PostMapping("/sendMessage")
	@ResponseBody
	public ResponseEntity sendMessage(@RequestBody MessageEntity messageEntity) {
		return chatService.sendMessage(messageEntity);
	}
	
	@ApiOperation(value = "해당 방 대화 조회", notes = "해당 방 대화 조회")
	@GetMapping("/loadChannel")
	@ResponseBody
	public ResponseEntity loadChannel(@RequestParam Long channelCd) {
		return chatService.loadChannel(channelCd);
	}

	@ApiOperation(value = "모든 방 조회", notes = "모든 방 조회")
	@GetMapping("/activeChannelList")
	@ResponseBody
	public ResponseEntity activeChannelList() {
		return chatService.activeChannelList();
	}
	
	@ApiOperation(value = "나의 활성 채널 조회", notes = "나의 활성 채널 조회")
	@GetMapping("/activeMyChannelList")
	@ResponseBody
	public ResponseEntity activeMyChannelList(@RequestParam Long loginUserCd) {
		return chatService.activeMyChannelList(loginUserCd);
	}
	
	//@ApiOperation(value = "방 퇴장", notes = "")
	//@PostMapping("/exitChannel")
	//@ResponseBody
	//public ResponseEntity exitChannel(@RequestParam Long userCd,@RequestParam Long channelCd) {
	//	return chatService.exitChannel(userCd,channelCd);
	//}
	
	@GetMapping("/channel_list")
    public String channelList(){
        return "chat/channel_list";
    }
	
	@GetMapping("/channel_detail/{channelCd}")
    public String channelDetail(Model model, @PathVariable Long channelCd){
		model.addAttribute("channelCd", channelCd);
        return "chat/channel_detail";
    }
}

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

import chat.model.ChatMessage;
import chat.service.ChannelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Api(tags = "ChannelController")
@Tag(name = "ChannelController", description = "채팅 방 서비스 제공")
@Controller
@RequiredArgsConstructor
@RequestMapping("/chat")
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
	public ResponseEntity createChannelWithUser(@RequestParam String LOGIN_USER_ID, @RequestParam String TO_USER_ID) {
		return chatService.createChannelWithUser(LOGIN_USER_ID, TO_USER_ID);
	}
	
	@ApiOperation(value = "toUser 방 개설", notes = "toUser 방개설")
	@PostMapping("/sendMessage")
	@ResponseBody
	public ResponseEntity sendMessage(@RequestBody ChatMessage chatMessage) {
		return chatService.sendMessage(chatMessage);
	}


	@ApiOperation(value = "모든 방 조회", notes = "모든 방 조회")
	@GetMapping("/activeChannelList")
	@ResponseBody
	public ResponseEntity activeChannelList() {
		return chatService.activeChannelList();
	}
	
	@ApiOperation(value = "모든 방 조회", notes = "모든 방 조회")
	@GetMapping("/activeMyChannelList")
	@ResponseBody
	public ResponseEntity activeMyChannelList() {
		return chatService.activeMyChannelList();
	}
	
	@ApiOperation(value = "방 퇴장", notes = "")
	@PostMapping("/exitChannel")
	@ResponseBody
	public ResponseEntity exitChannel(@RequestParam String userId,@RequestParam Long ChannelId) {
		return chatService.exitChannel(userId,ChannelId);
	}
	
	@ApiOperation(value = "멤버 조회", notes = "멤버 조회")
	@GetMapping("/findActiveMember")
	@ResponseBody
	public ResponseEntity findActiveMember(@RequestParam Long ChannelId) {
		return chatService.findActiveMember(ChannelId);
	}
	
	@ApiOperation(value = "해당 방 대화 조회", notes = "해당 방 대화 조회")
	@GetMapping("/loadChannel")
	@ResponseBody
	public ResponseEntity loadChannel(@RequestParam Long ChannelId) {
		return chatService.loadChannel(ChannelId);
	}
	
	@GetMapping("/channel_list")
    public String channelList(){
        return "chat/channel_list";
    }
	
	@GetMapping("/channel_detail/{ChannelId}")
    public String channelDetail(Model model, @PathVariable Long ChannelId){
		model.addAttribute("ChannelId", ChannelId);
        return "chat/channel_detail";
    }
}

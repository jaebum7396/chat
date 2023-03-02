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
import chat.service.ChatService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Api(tags = "ChatRoomController")
@Tag(name = "ChatRoomController", description = "채팅 방 서비스 제공")
@Controller
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatRoomController {
	@Autowired
	ChatService chatService;
	
	@ApiOperation(value = "방 개설", notes = "roomNm 입력 방개설")
	@PostMapping("/createRoom")
	@ResponseBody
	public ResponseEntity createRoom(@RequestParam String name) {
		return chatService.createRoom(name);
	}
	
	@ApiOperation(value = "toUser 방 개설", notes = "toUser 방개설")
	@PostMapping("/createRoomWithUser")
	@ResponseBody
	public ResponseEntity createRoomWithUser(@RequestParam String LOGIN_USER_ID, @RequestParam String TO_USER_ID) {
		return chatService.createRoomWithUser(LOGIN_USER_ID, TO_USER_ID);
	}
	
	@ApiOperation(value = "toUser 방 개설", notes = "toUser 방개설")
	@PostMapping("/sendMessage")
	@ResponseBody
	public ResponseEntity sendMessage(@RequestBody ChatMessage chatMessage) {
		return chatService.sendMessage(chatMessage);
	}


	@ApiOperation(value = "모든 방 조회", notes = "모든 방 조회")
	@GetMapping("/activeRoomList")
	@ResponseBody
	public ResponseEntity activeRoomList() {
		return chatService.activeRoomList();
	}
	
	@ApiOperation(value = "모든 방 조회", notes = "모든 방 조회")
	@GetMapping("/activeMyRoomList")
	@ResponseBody
	public ResponseEntity activeMyRoomList() {
		return chatService.activeMyRoomList();
	}
	
	@ApiOperation(value = "방 퇴장", notes = "")
	@PostMapping("/exitRoom")
	@ResponseBody
	public ResponseEntity exitRoom(@RequestParam String userCd,@RequestParam Long roomCd) {
		return chatService.exitRoom(userCd,roomCd);
	}
	
	@ApiOperation(value = "멤버 조회", notes = "멤버 조회")
	@GetMapping("/findActiveMember")
	@ResponseBody
	public ResponseEntity findActiveMember(@RequestParam Long roomCd) {
		return chatService.findActiveMember(roomCd);
	}
	
	@ApiOperation(value = "해당 방 대화 조회", notes = "해당 방 대화 조회")
	@GetMapping("/loadRoom")
	@ResponseBody
	public ResponseEntity loadRoom(@RequestParam Long roomCd) {
		return chatService.loadRoom(roomCd);
	}
	
	@GetMapping("/room_list")
    public String roomList(){
        return "chat/room_list";
    }
	
	@GetMapping("/room_detail/{roomCd}")
    public String roomDetail(Model model, @PathVariable Long roomCd){
		model.addAttribute("roomCd", roomCd);
        return "chat/room_detail";
    }
}

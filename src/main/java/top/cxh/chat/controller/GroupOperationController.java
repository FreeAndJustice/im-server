package top.cxh.chat.controller;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import top.cxh.chat.bean.GroupChat;
import top.cxh.chat.bean.GroupUser;
import top.cxh.chat.bean.Msg;
import top.cxh.chat.bean.MsgRecord;
import top.cxh.chat.service.GroupChatService;
import top.cxh.chat.service.GroupUserService;
import top.cxh.chat.service.MsgRecordService;
import top.cxh.chat.utils.Beans;
import top.cxh.chat.utils.Config;

@Controller
@RequestMapping("/group")
public class GroupOperationController {

	@Autowired
	GroupChatService groupChatService;
	
	@Autowired
	GroupUserService groupUserService;
	
	/**
	 * 
	 * @param account
	 * @param groupName
	 * @return
	 */
	@RequestMapping(value="/createGroup",method=RequestMethod.POST)
	@ResponseBody
	Msg createGroup(@RequestParam("account") String account,
					@RequestParam("groupName") String groupName,
					@RequestParam("users") String users) {
		Msg msg = new Msg();
		String groupId = Config.createGroupId();
		GroupChat gc = new GroupChat(groupId,groupName,new Date());
		boolean flag = groupChatService.addGroupChat(gc);
		if(flag) {
			msg.setCode(100);
			msg.setMsg("εε»Ίζε");
			GroupUser gu = new GroupUser(account,0,groupId);
			groupUserService.addGroupUser(gu);
			if(users != null  && !users.equals("")) {
				if(users.contains(",")) {
					String[] as = users.split(",");
					for(String a : as) {
						gu.setAccount(a);
						gu.setGroupRole(2);
						groupUserService.addGroupUser(gu);
					}
				}else {
					gu.setAccount(users);
					gu.setGroupRole(2);
					groupUserService.addGroupUser(gu);
				}
			}
		}else {
			msg.setCode(200);
			msg.setMsg("εε»ΊηΎ€θεΊι");
		}
		return msg;
	}
	
	/**
	 * θ·εζζε¨ηηΎ€θ
	 * @param account
	 * @return
	 */
	@RequestMapping(value="/getMyGroups",method=RequestMethod.GET)
	@ResponseBody
	Msg getMyGroups(@RequestParam("account") String account) {
		List<GroupUser> gus = groupUserService.getMyGroups(account);
		return Msg.success().add("data", gus);
	}
	
	/**
	 * ηΎ€ζε
	 * @param groupId
	 * @return
	 */
	@RequestMapping(value="/getGroupUsers",method=RequestMethod.GET)
	@ResponseBody
	Msg getGroupUsers(@RequestParam("groupId") String groupId) {
		List<GroupUser> gus = groupUserService.getGroupUsers(groupId);
		return Msg.success().add("data", gus);
	}
	
	/**
	 * θ·εηΎ€θ΅ζ
	 * @param groupId
	 * @return
	 */
	@RequestMapping(value="/getGroupData",method=RequestMethod.GET)
	@ResponseBody
	Msg getGroupData(@RequestParam("groupId") String groupId) {
		GroupChat gc = groupChatService.getGroupChatByGroupId(groupId);
		return Msg.success().add("data", gc);
	}
	
	/**
	 * δΏ?ζΉηΎ€ε€΄ε
	 * @param groupId
	 * @param groupImage
	 * @return
	 */
	@RequestMapping(value="/updateGroupImage",method=RequestMethod.POST)
	@ResponseBody
	Msg updateGroupImage(@RequestParam("groupId") String groupId,
						@RequestParam("groupImage") String groupImage) {
		System.out.println(groupId + ":" + groupImage);
		GroupChat gc = new GroupChat();
		gc.setGroupId(groupId);
		gc.setGroupImage(groupImage);
		boolean flag = groupChatService.updateGroupChat(gc);
		Msg msg = new Msg();
		if(flag) {
			msg.setCode(100);
			msg.setMsg("δΏ?ζΉζε");
			File file = new File(Config.storagePath + "\\userImage");
			if(file.isDirectory()) {
				File[] files = file.listFiles();
				for(File f : files) {
					if(!f.getName().equals(groupImage)) {
						if(f.getName().contains("-")) {
							if(f.getName().split("-")[0].equals(groupId)) {
								if(f.isFile()) {
									f.delete();
								}
							}
						}
					}
				}
			}
		}else {
			msg.setCode(200);
			msg.setMsg("δΏ?ζΉε€±θ΄₯");
		}
		return msg;
	}
	
	/**
	 * δΏ?ζΉηΎ€θ΅ζ
	 * @param groupId
	 * @param groupName
	 * @param groupImage
	 * @return
	 */
	@RequestMapping(value="/updateGroupData",method=RequestMethod.POST)
	@ResponseBody
	Msg updateGroupData(@RequestParam("groupId") String groupId,
						@RequestParam("groupName") String groupName,
						@RequestParam("groupImage") String groupImage) {
		GroupChat gc = new GroupChat();
		gc.setGroupId(groupId);
		if(!groupName.equals("")) {
			gc.setGroupName(groupName);
		}
		if(!groupImage.equals("")) {
			gc.setGroupImage(groupImage);
		}
		boolean flag = groupChatService.updateGroupChat(gc);
		Msg msg = new Msg();
		if(flag) {
			msg.setCode(100);
			msg.setMsg("δΏ?ζΉζε");
		}else {
			msg.setCode(200);
			msg.setMsg("δΏ?ζΉε€±θ΄₯");
		}
		return msg;
	}
	
	/**
	 * η§»ι€ηΎ€ζε
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/delGroupUser",method=RequestMethod.POST)
	@ResponseBody
	Msg delGroupUser(@RequestParam("id") Integer id) {
		Msg msg = new Msg();
		boolean flag = groupUserService.delGroupUser(id);
		if(flag) {
			msg.setCode(100);
			msg.setMsg("η§»ι€ζε");
		}else {
			msg.setCode(200);
			msg.setMsg("η§»ι€ε€±θ΄₯");
		}
		return msg;
	}
	
	/**
	 * εζη¨ζ·θΏηΎ€
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/agreeUserForGroup",method=RequestMethod.POST)
	@ResponseBody
	Msg agreeUserForGroup(@RequestParam("id") Integer id,
						@RequestParam("groupId") String groupId,
						@RequestParam("account") String account) {
		Msg msg = new Msg();
		GroupUser gu = new GroupUser();
		gu.setAccount(account);
		gu.setGroupId(groupId);
		gu.setGroupRole(2);
		System.out.println(gu);
		boolean flag = groupUserService.addGroupUser(gu);
		if(flag) {
			MsgRecord mr = new MsgRecord();
			mr.setId(id);
			mr.setMsgState(2);
			Beans.getBean("msgRecordService", MsgRecordService.class).updateMsgState(mr);
			msg.setCode(100);
			msg.setMsg("ε·²εζ");
		}else {
			msg.setCode(200);
			msg.setMsg("ζδ½ε€±θ΄₯");
		}
		return msg;
	}
	
	/**
	 * ζ·»ε ε₯½εθΏηΎ€
	 * @param groupId
	 * @param users
	 * @return
	 */
	@RequestMapping(value="/addGroupUser",method=RequestMethod.POST)
	@ResponseBody
	Msg addGroupUser(@RequestParam("groupId") String groupId,
					@RequestParam("users") String users) {
		Msg msg = new Msg();
		boolean flag = false;
		if(users.contains(",")) {
			String[] as = users.split(",");
			GroupUser gu = new GroupUser();
			gu.setGroupId(groupId);
			for(String a : as) {
				gu.setAccount(a);
				gu.setGroupRole(2);
				flag = groupUserService.addGroupUser(gu);
			}
		}else {
			GroupUser gu = new GroupUser();
			gu.setGroupId(groupId);
			gu.setAccount(users);
			gu.setGroupRole(2);
			flag = groupUserService.addGroupUser(gu);
		}
		if(flag) {
			msg.setCode(100);
			msg.setMsg("ζ·»ε ζε");
		}else {
			msg.setCode(200);
			msg.setMsg("ζ·»ε ε€±θ΄₯");
		}
		return msg;
	}
	
	/**
	 * η¨ζ·ζ―ε¦ε¨ηΎ€ι
	 * @param groupId
	 * @param account
	 * @return
	 */
	@RequestMapping(value="/userIsExistGroup",method=RequestMethod.POST)
	@ResponseBody
	Msg userIsExistGroup(@RequestParam("groupId") String groupId,
					@RequestParam("account") String account) {
		Msg msg = new Msg();
		boolean flag = groupUserService.isExistGroup(groupId, account);
		if(flag) {
			msg.setCode(200);
			msg.setMsg("η¨ζ·ε·²ε­ε¨");
		}else {
			msg.setCode(100);
			msg.setMsg("η¨ζ·δΈε­ε¨");
		}
		return msg;
	}
	/**
	 * θ?Ύη½?ηΎ€ζεθ§θ²
	 * @param groupId
	 * @param role
	 * @return
	 */
	@RequestMapping(value="/updateGroupUser",method=RequestMethod.POST)
	@ResponseBody
	Msg updateGroupUser(@RequestParam("id") Integer id,
					@RequestParam("role") Integer role) {
		Msg msg = new Msg();
		GroupUser gu = new GroupUser();
		gu.setId(id);
		gu.setGroupRole(role);
		boolean flag = groupUserService.updateGroupUser(gu);
		if(flag) {
			msg.setCode(100);
			msg.setMsg("θ?Ύη½?ζε");
		}else {
			msg.setCode(200);
			msg.setMsg("θ?Ύη½?ε€±θ΄₯");
		}
		return msg;
	}
	
	/**
	 * ιεΊηΎ€θ
	 * @param groupId
	 * @param account
	 * @return
	 */
	@RequestMapping(value="/outGroup",method=RequestMethod.POST)
	@ResponseBody
	Msg outGroup(@RequestParam("groupId") String groupId,
					@RequestParam("account") String account) {
		Msg msg = new Msg();
		boolean flag = groupUserService.outGroup(account, groupId);
		if(flag) {
			msg.setCode(100);
			msg.setMsg("ιεΊζε");
		}else {
			msg.setCode(200);
			msg.setMsg("ιεΊε€±θ΄₯");
		}
		return msg;
	}
	
	/**
	 * θ§£ζ£ηΎ€θ
	 * @param groupId
	 * @return
	 */
	@RequestMapping(value="/cancelGroup",method=RequestMethod.POST)
	@ResponseBody
	Msg cancelGroup(@RequestParam("groupId") String groupId) {
		Msg msg = new Msg();
		
		boolean flag = groupChatService.delGroupChat(groupId);
		if(flag) {
			flag = groupUserService.delGroupUsers(groupId);
		}
		if(flag) {
			msg.setCode(100);
			msg.setMsg("θ§£ζ£ζε");
		}else {
			msg.setCode(200);
			msg.setMsg("θ§£ζ£ε€±θ΄₯");
		}
		return msg;
	}
	
	/**
	 * ζη΄’ηΎ€θ
	 * @param keys
	 * @return
	 */
	@RequestMapping(value="/searchGroup",method=RequestMethod.GET)
	@ResponseBody
	Msg searchGroup(@RequestParam("keys") String keys) {
		List<GroupChat> gcs = groupChatService.searchGroup(keys);
		return Msg.success().add("data", gcs);
	}
}

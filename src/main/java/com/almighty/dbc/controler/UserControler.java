package com.almighty.dbc.controler;

import java.util.HashSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.almighty.dbc.model.Role;
import com.almighty.dbc.model.User;
import com.almighty.dbc.service.impl.RoleServiceImpl;
import com.almighty.dbc.service.impl.UserServiceImpl;

/**
 * @author trungnt
 *
 */
@RestController
@RequestMapping("/api/dbc/users")
public class UserControler {
	// private Log LOGGER = LogFactory.getLog(UserControler.class);

	@Value("${security.encoding-strength}")
	private Integer encodingStrength;

	@Autowired
	private UserServiceImpl userServiceImpl;

	@Autowired
	private RoleServiceImpl roleServiceImpl;

	@RequestMapping(value = "/get", method = RequestMethod.GET)
	public ResponseEntity<?> findUsers(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "start", defaultValue = "0") int start,
			@RequestParam(value = "end", defaultValue = "10") int end) {

		return ResponseEntity.status(HttpStatus.OK).body(userServiceImpl.getUsers(start, end));

	}

	@RequestMapping(value = "/{userName}", method = RequestMethod.GET)
	public ResponseEntity<?> findUserByUserName(HttpServletRequest request, HttpSession session,
			@PathVariable(value = "userName") String userName) {

		return ResponseEntity.status(HttpStatus.OK).body(userServiceImpl.findByUserName(userName));
	}

	@RequestMapping(value = "/delete/{userId}", method = RequestMethod.DELETE)
	public ResponseEntity<?> delete(HttpServletRequest request, HttpSession session,
			@PathVariable(value = "userId") long userId) {

		return ResponseEntity.status(HttpStatus.OK).body(userServiceImpl.deleteUser(userId));
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public ResponseEntity<?> addUser(@RequestBody User user,
			@RequestParam(value = "strRoleId", defaultValue = "") String strRoleId) {

		/*List<Long> roleIds = new ArrayList<>();
		
		long[] lonArray = null;
		
		if (strRoleId != null && !strRoleId.isEmpty()) {
			lonArray = Arrays.stream(strRoleId.split(",")).mapToLong(Long::valueOf).toArray();
		
			if (lonArray != null && lonArray.length > 0) {
				roleIds = Arrays.stream(lonArray).boxed().collect(Collectors.toList());
			}
		}*/

		user.setPasswordEncrypted(new BCryptPasswordEncoder(encodingStrength).encode(user.getPassword()));

		if (strRoleId != null && !strRoleId.isEmpty()) {
			List<Role> roles = roleServiceImpl.findRolesByIds(strRoleId);

			if (roles != null) {
				user.setRoles(new HashSet<>(roles));
			}
		}

		return ResponseEntity.status(HttpStatus.OK).body(userServiceImpl.addUser(user));

	}

	@RequestMapping(value = "/update/{userId}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateUser(HttpServletRequest request, HttpSession session,
			@PathVariable(value = "userId") long userId, @RequestBody User userModel,
			@RequestParam(value = "strRoleId", defaultValue = "") String strRoleId) {

		User user = userServiceImpl.findByPrimaryKey(userId);

		/*List<Long> roleIds = new ArrayList<>();
		
		long[] lonArray = null;
		
		if (strRoleId != null && !strRoleId.isEmpty()) {
			lonArray = Arrays.stream(strRoleId.split(",")).mapToLong(Long::valueOf).toArray();
		
			if (lonArray != null && lonArray.length > 0) {
				roleIds = Arrays.stream(lonArray).boxed().collect(Collectors.toList());
			}
		}*/

		user.setPasswordEncrypted(new BCryptPasswordEncoder(encodingStrength).encode(userModel.getPassword()));
		user.setAddress(userModel.getAddress());
		user.setEmail(userModel.getEmail());
		user.setFullName(userModel.getFullName());
		user.setImageURL(userModel.getImageURL());
		user.setPassword(userModel.getPassword());
		user.setStatus(userModel.getStatus());
		user.setTelNo(userModel.getTelNo());

		if (strRoleId != null && !strRoleId.isEmpty()) {
			List<Role> roles = roleServiceImpl.findRolesByIds(strRoleId);

			if (roles != null) {
				user.setRoles(new HashSet<>(roles));
			}
		}

		return ResponseEntity.status(HttpStatus.OK).body(userServiceImpl.updateUser(user));

	}
}

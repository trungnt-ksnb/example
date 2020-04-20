package com.almighty.dbc.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.almighty.dbc.model.User;
import com.almighty.dbc.service.UserService;

/**
 * @author trungnt
 *
 */
@Component
public class UserValidator implements Validator {

	@Autowired
	private UserService userService;

	@Override
	public boolean supports(Class<?> clazz) {
		return User.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {

		User _tmp = (User) target;

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "userName", "msg.user.username.notempty");

		if (_tmp.getUserName().length() <= 4 || _tmp.getUserName().length() > 32) {
			errors.reject("userName", new Object[] { 4, 32 }, "msg.user.username.size");
		}
		
		if(_tmp.getUserId() > 0 && userService.findByPrimaryKey(_tmp.getUserId()) == null) {
			errors.rejectValue("userId", "msg.user.userId.notexist");
		}

		User user = userService.findByUserName(_tmp.getUserName());
		
		if (user != null && _tmp.getUserId() == 0) {
			errors.rejectValue("userName", "msg.user.username.duplicate");
		} else if (user != null && _tmp.getUserId() > 0 && user.getUserId() != _tmp.getUserId()) {
			errors.rejectValue("userName", "msg.user.username.duplicate");
		}

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "msg.user.password.notempty");
	}

}

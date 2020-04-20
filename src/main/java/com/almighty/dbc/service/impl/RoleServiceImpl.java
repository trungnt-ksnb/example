package com.almighty.dbc.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.almighty.dbc.dao.RoleDAO;
import com.almighty.dbc.model.Role;
import com.almighty.dbc.service.RoleService;

/**
 * @author trungnt
 *
 */
@Service
public class RoleServiceImpl implements RoleService {
	@Autowired
	private RoleDAO roleDAO;

	@Override
	public List<Role> findAll() {
		return roleDAO.findAll();
	}

	@Override
	// @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	public Role findByPrimaryKey(long roleId) {
		String sql = "SELECT * FROM dbc_role WHERE role_id = ?";
		return roleDAO.findByPrimaryKey(sql, roleId);
	}

	@Override
	public Role findByUserName(String userName) {
		String sql = "SELECT * FROM dbc_role WHERE user_name = ?";
		return roleDAO.findByUserName(sql, userName);
	}

	@Override
	public List<Role> findRolesByIds(String roleIds) {
		String sql = "SELECT * FROM dbc_role WHERE role_id IN (?)";
		return roleDAO.findRolesByIds(sql, roleIds);
	}

	@Override
	public List<Role> findRolesByUserId(long userId) {
		String sql = "SELECT * FROM dbc_role WHERE role_id IN ((SELECT role_id FROM dbc_user_role where user_id = ?));";
		return roleDAO.findRolesByUserId(sql, userId);
	}

}

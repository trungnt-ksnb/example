package com.almighty.dbc.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.almighty.dbc.model.Role;

/**
 * @author trungnt
 *
 */
@Service
public interface RoleService {

	public Role findByPrimaryKey(long roleId);

	public Role findByUserName(String name);

	public List<Role> findAll();

	public List<Role> findRolesByIds(String roleIds);

	public List<Role> findRolesByUserId(long userId);
}

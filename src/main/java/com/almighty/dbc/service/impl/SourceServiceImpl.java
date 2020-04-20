package com.almighty.dbc.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.almighty.dbc.dao.SourceDAO;
import com.almighty.dbc.service.SourceService;

/**
 * @author trungnt
 *
 */
@Service
public class SourceServiceImpl implements SourceService {
	@Autowired
	private SourceDAO sourceDAO;

	@Override
	public Object findUser() {
		return sourceDAO.findUser();
	}

	@Override
	public List<Map<String, Object>> receiving(String sql) {
		return sourceDAO.execute(sql);
	}

	@Override
	public long getTotal(String sql) {
		return sourceDAO.getTotal(sql);
	}

}

package com.almighty.dbc.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

/**
 * @author trungnt
 *
 */
@Service
public interface SourceService {
	public Object findUser();
	public List<Map<String, Object>> receiving(String sql);
	public long getTotal(String sql);
}

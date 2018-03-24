package com.gcit.library.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import com.gcit.library.model.Publisher;

@Component
public class PublisherDao extends BaseDao<Publisher> implements ResultSetExtractor<List<Publisher>>{
	
	public List<Publisher> getPublishers(String sql, Object[]values) {
		return mysqlTemplate.query(sql,values,this);
	}
	
	public List<Publisher> extractData(ResultSet rs) throws SQLException  {
		List<Publisher> publishers = new LinkedList<Publisher>();
		Publisher publisher = null;
		while(rs.next()) {
			publisher = new Publisher();
			publisher.setId(rs.getInt(1));
			publisher.setName(rs.getString(2));
			publisher.setAddress(rs.getString(3));
			publisher.setPhone(rs.getString(4));
			publishers.add(publisher);
		}
		return publishers;
	}
}

package com.gcit.library.service;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.gcit.library.dao.PublisherDao;
import com.gcit.library.model.Publisher;


@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class PublisherService {
	
	@Autowired
	PublisherDao pdao;
	
	@Transactional
	@RequestMapping(value="getPublishers",method=RequestMethod.GET)
	public List<Publisher> getAllPublishers(Integer pageNo) throws SQLException{
		StringBuffer str = new StringBuffer("select * from tbl_publisher");
		if(pageNo != null) {
			str.append(" limit ?,?");
			return pdao.getAllPublishers(str.toString(),new Object[] {(pageNo-1)*10,10});
		}

		return pdao.getAllPublishers(str.toString(),null);
	}
	
//	public Publisher getPublisherByPK(Integer publisherId) throws SQLException {
//		Connection conn = null;
//		try {
//			conn = connUtil.getConnection();
//			PublisherDAO bdao = new PublisherDAO(conn);
//			return bdao.getByPK(publisherId).get(0);
//		} catch (ClassNotFoundException | SQLException e) {
//			e.printStackTrace();
//		} finally{
//			if(conn!=null){
//				conn.close();
//			}
//		}
//		return null;
//	}
	
//	public Integer getPublisherCount(String search) throws SQLException {
//		Connection conn = null;
//		try {
//			conn = connUtil.getConnection();
//			PublisherDAO bdao = new PublisherDAO(conn);
//			return bdao.getPublisherCount(search);
//		} catch (ClassNotFoundException | SQLException e) {
//			e.printStackTrace();
//		} finally{
//			if(conn!=null){
//				conn.close();
//			}
//		}
//		return null;
//	}
}

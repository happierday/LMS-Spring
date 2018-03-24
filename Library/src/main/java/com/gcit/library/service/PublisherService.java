package com.gcit.library.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gcit.library.dao.PublisherDao;
import com.gcit.library.model.Publisher;


@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class PublisherService {
	
	@Autowired
	PublisherDao pdao;
	
	@Transactional
	@RequestMapping(value="/publishers",method=RequestMethod.GET)
	public ResponseEntity<Object> getAllPublishers(@RequestParam(value="pageNo",required=false)Integer pageNo,
			@RequestParam(value="search",required=false) String search){
		StringBuffer str = new StringBuffer("select * from tbl_publisher");
		List<Publisher> pubs = null;
		try {
			if(pageNo != null && search != null) {
				String query = "%" + search + "%";
				str.append(" where publisherName = ? limit ?,?;");
				pubs = pdao.getPublishers(str.toString(), new Object[] {query,(pageNo-1)*10,10});
			} else {
				if(search != null) {
					String query = "%" + search + "%";
					str.append(" where publisherName = ?;");
					pubs = pdao.getPublishers(str.toString(), new Object[] {query});
				}else if(pageNo != null) {
					str.append(" limit ?,?;");
					pubs = pdao.getPublishers(str.toString(), new Object[] {(pageNo-1)*10,10});
				} else {
					pubs = pdao.getPublishers(str.toString(), null);
				}
			}
			return new ResponseEntity<Object>(pubs,HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Object>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
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

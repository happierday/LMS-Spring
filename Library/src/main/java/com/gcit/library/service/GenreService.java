package com.gcit.library.service;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gcit.library.dao.GenreDao;
import com.gcit.library.model.Genre;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class GenreService {
	
	@Autowired
	GenreDao gdao;
	
	@Transactional
	@RequestMapping(value="getGenres",method=RequestMethod.GET)
	public List<Genre> getAllGenres(@RequestParam(value="pageNo",required=false) Integer pageNo) throws SQLException{
		StringBuffer str = new StringBuffer("select * from tbl_genre");
		if(pageNo != null) {
			str.append(" limit ?,?");
			return gdao.getAllGenres(str.toString(),new Object[] {(pageNo-1)*10,10});
		}

		return gdao.getAllGenres(str.toString(),null);
	}
	
//	public Genre getGenreByPK(Integer genreId) throws SQLException {
//		Connection conn = null;
//		try {
//			conn = connUtil.getConnection();
//			GenreDAO bdao = new GenreDAO(conn);
//			return bdao.getByPK(genreId).get(0);
//		} catch (ClassNotFoundException | SQLException e) {
//			e.printStackTrace();
//		} finally{
//			if(conn!=null){
//				conn.close();
//			}
//		}
//		return null;
//	}
	
//	public Integer getGenreCount(String search) throws SQLException {
//		Connection conn = null;
//		try {
//			conn = connUtil.getConnection();
//			GenreDAO bdao = new GenreDAO(conn);
//			return bdao.getGenreCount(search);
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

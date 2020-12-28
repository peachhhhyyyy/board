package peachy.sv.board.pool;

import javax.servlet.http.*;
import javax.servlet.*;
import java.io.*;
import java.sql.*;
import soo.db.ConnectionPoolBean;

public class BoardServletContentPool extends HttpServlet {
	public ConnectionPoolBean getPool(){
        try{
			ServletContext application = getServletContext();
			ConnectionPoolBean pool = (ConnectionPoolBean)application.getAttribute("pool");
			if(pool == null){
				pool = new ConnectionPoolBean();
				application.setAttribute("pool", pool);
			}
			return pool;
		}catch(ClassNotFoundException cnfe){
			return null;	
		}catch(SQLException se){
			return null;
		}
	}
	public void service(HttpServletRequest req, HttpServletResponse res)
		throws ServletException, IOException { //요청될 때마다 
		int seq = -1;
		String seqStr = req.getParameter("seq");
		if(seqStr != null){
			seqStr = seqStr.trim();
			if(seqStr.length() != 0) {
				try{
					seq = Integer.parseInt(seqStr);
				}catch(NumberFormatException ne){
				}
			}
		}
      
		req.setCharacterEncoding("utf-8");
		seq = Integer.parseInt(req.getParameter("seq"));

		ConnectionPoolBean pool = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "select * from BOARD where SEQ=?";
		try{
			pool = getPool();
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, seq);
			pstmt.executeUpdate();
		}catch(SQLException se){}

		res.setContentType("text/html;charset=utf-8");
		PrintWriter pw = res.getWriter();

		try{
			rs = pstmt.executeQuery();
			boolean flag = false;

			if(rs.next()){
				flag = true;
				seq = rs.getInt(1);
				String writer = rs.getString(2);
				String email = rs.getString(3);
				String subject = rs.getString(4);
				String content = rs.getString(5);
				Date rdate = rs.getDate(6);
			
				pw.println("<meta charset='utf-8'>");
				pw.println("<style>");
				pw.println("table, th, td {");
				pw.println("border: 1px solid black;");
				pw.println(" border-collapse: collapse;");
				pw.println("}");
				pw.println("th, td {");
				pw.println("padding: 5px;");
				pw.println("}");
				pw.println("a { text-decoration:none }");
				pw.println("</style>");
				pw.println("<body style='text-align:center;'>");
				pw.println("<hr style='width:600px;'>");
				pw.println("<h3>");
				pw.println("Simple Board with Servlet");
				pw.println("</h3>");
				pw.println("<a href='input.html'>글쓰기</a>");
				pw.println("<hr style='width:600px;'>");
				pw.println("<div style='max-width:600px;margin:0 auto;text-align:center;'>");
				pw.println("<table border='2' cellpadding='7' cellspacing='2' width='600px'>");
				pw.println("<tr>");
				pw.println("<td style='width:100px;'>글번호</td>");
				pw.println("<td>"+seq+"</td>");
				pw.println("</tr>");

				pw.println("<tr>");
				pw.println("<td style='width:100px;'>글쓴이</td>");
				pw.println("<td>"+writer+"</td>");
				pw.println("</tr>");
				
				pw.println("<tr>");
				pw.println("<td style='width:100px;'>이메일</td>");
				pw.println("<td>"+email+"</td>");
				pw.println("</tr>");

				pw.println("<tr>");
				pw.println("<td style='width:100px;'>글제목</td>");
				pw.println("<td>"+subject+"</td>");
				pw.println("</tr>");

				pw.println("<tr>");
				pw.println("<td style='width:100px;'>글내용</td>");
				pw.println("<td>"+content+"</td>");
				pw.println("</tr>");

				pw.println("</table>");
				pw.println("</div>");
				pw.println("<hr style='width:600px;'>");
				pw.println("<a href='update.do?seq="+seq+"'>수정</a>");
				pw.println("<a href='del.do?seq="+seq+"'>삭제</a>");
				pw.println("<a href='list.do'>목록</a>");
				pw.println("</body>");
			}
		}catch(SQLException se){
			System.out.println("service catch se: "+se);
		}finally{
			try{
				if(pstmt != null) pstmt.close();
				if(con != null) con.close();
			}catch(SQLException se){
				System.out.println("destroy catch se: "+se);
			}
		}
	}
}
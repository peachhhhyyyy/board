package peachy.sv.board.pool;

import javax.servlet.http.*;
import javax.servlet.*;
import java.io.*;
import java.sql.*;
import soo.db.ConnectionPoolBean;

public class BoardServletUpdatePool extends HttpServlet {
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
		req.setCharacterEncoding("utf-8");

		String seqStr = req.getParameter("seq");
		String emailStr = req.getParameter("email");
		String subjectStr = req.getParameter("subject");
		String contentStr = req.getParameter("content");
		int seq = -1;
		if(seqStr != null){
			seqStr = seqStr.trim();
			if(seqStr.length() != 0) {
				try{
					seq = Integer.parseInt(seqStr);
				}catch(NumberFormatException ne){
				}
			}
		}
		
		ConnectionPoolBean pool = null;
		Connection con = null;
		PreparedStatement pstmt1 = null;
		PreparedStatement pstmt2 = null;
		String sql1 = "select * from BOARD where SEQ=?";
		String sql2 = "update BOARD set EMAIL=?, SUBJECT=?, CONTENT=?, RDATE=SYSDATE where SEQ=?";

		if(emailStr != null){
			emailStr = emailStr.trim();
			subjectStr = subjectStr.trim();
			contentStr = contentStr.trim();
			try{
				pool = getPool();
				con = pool.getConnection();
				pstmt2 = con.prepareStatement(sql2);
				seq = Integer.parseInt(seqStr);
				pstmt2.setString(1, emailStr);
				pstmt2.setString(2, subjectStr);
				pstmt2.setString(3, contentStr);
				pstmt2.setInt(4, seq);
				pstmt2.executeUpdate();
				res.sendRedirect("list.do");
			}catch(NumberFormatException ne){
			}catch(SQLException se){}
		}else{
			res.setContentType("text/html;charset=utf-8");
			PrintWriter pw = res.getWriter();
			ResultSet rs = null;
			try{
				pool = getPool();
				con = pool.getConnection();
				pstmt1 = con.prepareStatement(sql1);
				pstmt1.setInt(1, seq);
				rs = pstmt1.executeQuery();
				boolean flag = false;

				while(rs.next()){
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
					pw.println("<a href='list.do'>글목록</a>");
					pw.println("<hr style='width:600px;'>");
					pw.println("<form name='f' method='post' action='update.do'>");
                    pw.println("<input type='hidden' name='seq' value='"+seq+"'>");
					pw.println("<div style='text-align:center;max-width:600px;margin:0 auto;'>");
					pw.println("<table style='width:600px;'>");
					pw.println("<tr>");
					pw.println("<td style='width:20%;' align='center'>글쓴이</td>");
					pw.println("<td><input name='writer' value='"+writer+"' readonly='readonly' size='67'/></td>");
					pw.println("</tr>");

					pw.println("<tr>");
					pw.println("<td align='center'>글쓴이</td>");
					pw.println("<td><input name='email' value='"+email+"' size='67'/></td>");
					pw.println("</tr>");

					pw.println("<tr>");
					pw.println("<td align='center'>글제목</td>");
					pw.println("<td><input name='subject' value='"+subject+"' size='67'/></td>");
					pw.println("</tr>");

					pw.println("<tr>");
					pw.println("<td>글내용</td>");
					pw.println("<td><textarea id='ta' name='content' style='width:-webkit-fill-available;min-height:100px;resize:none;'>"+content+"</textarea></td>");
					pw.println("</tr>");

					pw.println("<tr>");
					pw.println("<td colspan='2' align='center'>");
					pw.println("<input type='submit' value='수정'>");	
					pw.println("</td>");
					pw.println("</tr>");
		
					pw.println("</table>");
					pw.println("</div>");
					pw.println("<hr style='width:600px;'>");
					pw.println("</body>");
				}
			}catch(SQLException se){
			}finally{
				try{
					if(pstmt1 != null) pstmt1.close();
					if(pstmt2 != null) pstmt2.close();
					if(con != null) con.close();
				}catch(SQLException se){}
			}
		}
	}
}
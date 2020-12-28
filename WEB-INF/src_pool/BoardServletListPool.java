package peachy.sv.board.pool;

import javax.servlet.http.*;
import javax.servlet.*;
import java.io.*;
import java.sql.*;
import soo.db.ConnectionPoolBean;

public class BoardServletListPool extends HttpServlet {
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
		res.setContentType("text/html;charset=utf-8");
		PrintWriter pw = res.getWriter();

		pw.println("<meta charset='utf-8'>");
		pw.println("<style>");
			pw.println("table, th, td {");
			   pw.println("border: 1px solid black;");
			   pw.println("border-collapse: collapse;");
			pw.println("}");
			pw.println("th, td {");
			   pw.println("padding: 5px;");
			pw.println("}");
			pw.println("a { text-decoration:none }");
		pw.println("</style>");
		pw.println("<body style='text-align:center;'>");
			pw.println("<hr style='width:600px;'>");
			pw.println("<h1>");
				pw.println("Address List");
			pw.println("</h1>");
			pw.println("<a href='../'>인덱스</a>");
			pw.println("&nbsp;&nbsp;&nbsp;");
			pw.println("<a href='input.html'>글쓰기</a>");
			pw.println("<hr style='width:600px;'>");
			pw.println("<div style='max-width:600px;margin:0 auto;text-align:center;'>");
			pw.println("<table border='1' cellpadding='7' cellspacing='2' width='600px'>");
			pw.println("<tr>");
				pw.println("<th>글번호</th>");
				pw.println("<th>작성자</th>");
				pw.println("<th>이메일</th>");
				pw.println("<th>글제목</th>");
				pw.println("<th>날짜</th>");
			pw.println("</tr>");

		ConnectionPoolBean pool = null;
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		String sql = "select * from BOARD order by SEQ desc";
		try{
			pool = getPool();
			con = pool.getConnection();
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			boolean flag = false;
			while(rs.next()){
				flag = true;
				int seq = rs.getInt(1);
				String writer = rs.getString(2);
				String email = rs.getString(3);
				String subject = rs.getString(4);
				String content = rs.getString(5);
				Date rdate = rs.getDate(6);
				pw.println("<tr>");
					pw.println("<td align='center'>"+seq+"</td>");
					pw.println("<td>"+writer+"</td>");
					pw.println("<td>"+email+"</td>");
					pw.println("<td><a href='content.do?seq="+seq+"'>"+subject+"</a></td>");
					pw.println("<td align='center'>"+rdate+"</td>");
				pw.println("</tr>");
			}
			if(!flag){
				pw.println("<tr>");
					pw.println("<td colspan='5' align='center'>데이터 없음</td>");
				pw.println("</tr>");
			}
		}catch(SQLException se){
		}finally{
			try{
				if(rs != null) rs.close();
				if(stmt != null) stmt.close();
				if(con != null) pool.returnConnection(con);
			}catch(SQLException se){}
		}
			pw.println("</table>");
			pw.println("</div>");
		pw.println("</body>");
	}
}
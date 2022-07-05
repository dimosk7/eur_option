import java.sql.*;


public class Sql
{
	
	private Connection con = null;
	private Statement stat = null;
	private ResultSet result= null;
	
	
	public double getVol(String ticker) {
		
		try 
		{
			Class.forName("com.mysql.cj.jdbc.Driver");
			String url = "jdbc:mysql://localhost:3306/stocks";
		        String user = "root";
		        String pw = "dimosk"; 
		        con = DriverManager.getConnection(url, user, pw);
			stat = con.createStatement();
			String query = String.format("SELECT STDDEV(`Daily Returns`)*sqrt(252) volatility, count(*) count\r\n"
					+ "FROM "
					+ "(\r\n"
					+ "SELECT\r\n"
					+ "        Ticker,\r\n"
					+ "        round(LN(`Close Price`) - LN(LAG(`Close Price`,1) OVER (PARTITION BY Ticker ORDER BY `Date`)),3) `Daily Returns`\r\n"
					+ "FROM \r\n"
					+ "        daily_prices\r\n"
					+ "WHERE\r\n"
					+ "        Ticker = \"%s\") returns;        \r\n"
					+ "         ", ticker);
			
			result = stat.executeQuery(query);
			result.next();
			double volat = result.getDouble("volatility");
			int count = result.getInt("count");
			if (count < 800) {
				System.out.println("Not enough historical data. Do not use this volatility value");
				volat = 0;
			}
			return volat;
	
		} 
		catch (Exception e)
		{
		    throw new Error("We have problem", e);
		}
			
	}
	
}

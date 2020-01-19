package GUIDesign;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.*;
import java.sql.Date;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;


class DataException extends Exception{
	private static final long serialVersionUID = 3257749654939520367L;
	public String errorinfo;
	public DataException(String p) {
		errorinfo = p;
	}
}
//说明:实验二使用的 DataProcessing类
public class DataProcessing extends JFrame
{
	private static final long serialVersionUID = 1L;
	static Hashtable<String, User> users;
	static Hashtable<String, Doc> docs;
	static String UPLOAD_PATH = "E:\\OOP\\uploadFile";
	static String DOWNLOAD_PATH = "E:\\OOP\\downloadFile";
    static Connection conn = null;
    static Statement stmt;
    static Socket socket;
    static String SERVER_IP = "localhost";
    static int SEVER_PORT = 5002;
	public static void connect()
	{
		String url = "jdbc:mysql://localhost:3306/document?" + "user=root&password=123456&useUnicode=true&characterEncoding=UTF8";
		try
		{
			Class.forName("com.mysql.jdbc.Driver");// 动态加载mysql驱动
			System.out.println("成功加载MySQL驱动程序");
			conn = DriverManager.getConnection(url);
			stmt = conn.createStatement();	 
		}
		catch (SQLException e)
		{
			System.out.println("MySQL操作错误");
			e.printStackTrace();
		} catch (Exception e)
		{
			System.out.println("MySQL错误");
			e.printStackTrace();
		} 
	}
	
	// 说明: 实验二、三、四 使用此处Init()函数
	public static void Init() throws IOException, DataException, SQLException
	{
		users = new Hashtable<String, User>();
		String name, password, role;
		String	sql = "select * from user_info";
		ResultSet rs = stmt.executeQuery(sql);// executeQuery会返回结果的集合，否则返回空值
		while (rs.next())
		{
			name = rs.getString(1).trim();
			password = rs.getString(2).trim();
			role = rs.getString(3).trim();
			if (role.equalsIgnoreCase("Operator"))
				users.put(name, new Operator(name, password, role));
			else if (role.equalsIgnoreCase("Browser"))
				users.put(name, new Browser(name, password, role));
			else if (role.equalsIgnoreCase("Administrator"))
				users.put(name, new Administrator(name, password, role));
			else
			{
				throw new DataException("用户身份信息格式错误！");
			}
		}			
	}
	
	//搜索用户信息
	public static User searchUser(String name)
	{
		if (users.containsKey(name))
		{
			User temp = users.get(name);
			return temp;
		}
		return null;
	}
	
    //搜索用户信息重载
	public static User searchUser(String name, String password)
	{
		if (users.containsKey(name))
		{
			User temp = users.get(name);
			if ((temp.getPassword()).equals(password))
				return temp;
		}
		return null;
	}
    
	//取哈希表中的所有用户信息
	public static Enumeration<User> getAllUser()
	{
		Enumeration<User> e = users.elements();
		return e;
	}
    
	// 更新哈希表和数据库中用户信息
	public static boolean updateUser(String name, String password, String role) throws IOException, DataException
	{
		User user;
		if (users.containsKey(name))
		{
			//更新哈希表中用户信息
			if (role.equalsIgnoreCase("Administrator"))
				user = new Administrator(name, password, role);
			else if (role.equalsIgnoreCase("Operator"))
				user = new Operator(name, password, role);
			else if(role.equalsIgnoreCase("Browser"))
				user = new Browser(name, password, role);
			else {
				throw new DataException("用户身份信息格式错误！");
			}
			users.put(name, user);
            //更新数据库中用户信息
			String	sql = "update user_info set password = '"+password+"',role='"+role+"' where username='"+name+"'";
			try {
				stmt.executeUpdate(sql);
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
			return true;	
	    } 
		else
			return false;	
	}
		
	// 增加新用户
	public static boolean insertUser(String name, String password, String role) throws IOException
	{
		User user;
		if (users.containsKey(name))
			return false;
		else
		{
			//添加用户信息到哈希表中
			if (role.equalsIgnoreCase("administrator"))
				user = new Administrator(name, password, role);
			else if (role.equalsIgnoreCase("operator"))
				user = new Operator(name, password, role);
			else
				user = new Browser(name, password, role);
			users.put(name, user);
			//添加用户信息到数据库中
			String sql = "insert into user_info values('" + name + "', '" + password + "', '" + role + "')";
			try {
				stmt.executeUpdate(sql);
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}
	}

	// 删除用户
	public static boolean deleteUser(String name) throws IOException
	{
		if (users.containsKey(name))
		{
			//删除哈希表中用户信息
			users.remove(name);
			//删除数据库中用户信息
			String	sql = "delete from user_info where username='" + name + "'";	
			try {
				stmt.executeUpdate(sql);
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}
		else
			return false;	
	}

	
	// 增加新的档案文件信息到数据库中
	public static boolean insertDoc(String ID, String creator, long timestamp, String description, String filename) throws IOException
	{
		java.util.Date jdate = new Date(timestamp);
		SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = dFormat.format(jdate);
		String	sql = " insert into doc_info(creator,timestamp,description,filename) values('" + creator + "', '" + time + "', '" + description + "', '" + filename + "')";	
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}   
		return true;
	}
	
	//文件列表
	public static void FileList()
	{
		JFrame frame = new JFrame();
		frame.setBounds((1920 - 1080)/2 ,(1080 - 700)/2,  1080, 700);
		frame.getContentPane().setLayout(new BorderLayout());
		
		String[] columnNames = {"文件编号","创建者","最后一次操作时间","文件描述","文件名"};
		DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
		
		try {
			DataProcessing.Init();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (DataException e1) {
			e1.printStackTrace();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		String sql = "select * from doc_info";
		try {
		    ResultSet rs = stmt.executeQuery(sql);// executeQuery会返回结果的集合，否则返回空值
		    DateFormat da = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			while (rs.next())
			{
				tableModel.addRow(new String[] {rs.getString(1),rs.getString(2),da.format(rs.getTimestamp(3)),rs.getString(4),rs.getString(5)}); 
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		JTable table = new JTable(tableModel);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		
		JScrollPane scrollPane = new JScrollPane(table);
		
		JButton button2 = new JButton("下载");
		
		
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(20));
		panel.add(button2);
		
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		frame.getContentPane().add(panel, BorderLayout.SOUTH);
		frame.setVisible(true);
		
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e)
			{
				if(JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(null, "是否退出", "询问", JOptionPane.OK_CANCEL_OPTION))
					frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				else
					frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			}
		});
		
		//下载文件
        button2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Thread()
				{
					public void run() {
						JProgressBar jBar = new JProgressBar();
						jBar.setPreferredSize(new Dimension(400, 50));
						
						JFrame barframe = new JFrame("下载文件");
						barframe.getContentPane().setLayout(new BorderLayout(5,5));
						barframe.getContentPane().add(jBar, BorderLayout.NORTH);
						barframe.pack();
						barframe.setVisible(true);
						barframe.setLocationRelativeTo(null);
						barframe.addWindowListener(new WindowAdapter() {
							public void windowClosing(WindowEvent e) {
								barframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
							}
						});
						
						int row = table.getSelectedRow();
						String filename = table.getValueAt(row, 4).toString();
						try {
							
							socket = new Socket(SERVER_IP, SEVER_PORT);
							System.out.println("连接成功");
							
							DataOutputStream fwriter = new DataOutputStream(socket.getOutputStream());
							DataInputStream in = new DataInputStream(socket.getInputStream());
							DataOutputStream out = new DataOutputStream(new FileOutputStream(new File("e:/OOP/downloadFile/"+filename)));
							//传输下载文件指令
							fwriter.writeUTF("download"); //线程进入阻塞态
							
						    //将想要下载的文件名传给服务器
						    fwriter.writeUTF(filename);
						   
						    //先接受文件长度
						    long filelength = Long.parseLong(in.readUTF());
						    
							//然后接受服务器端传过来的文件 
						    byte[] buffer = new byte[5000];
						    int len = 0;
						    long curlength = 0;
						    boolean mark = true;
						    while(mark)
						    {
						    	while((len = in.read(buffer, 0, buffer.length)) != -1)
							    {
							    	out.write(buffer, 0, len);
							    	curlength = curlength + len;
							    	long i = curlength*100/filelength;
							    	jBar.setValue((int)i);
							    	jBar.setString("已下载"+i+"%");	
							    	jBar.setStringPainted(true);
							    	if(i == 100) {
							    		JOptionPane.showMessageDialog(null, "下载成功", "通知", JOptionPane.INFORMATION_MESSAGE);
							    		mark = false;
							    	}
							    		
							    }
						    }
						    
						    //关闭各个流
						    out.close();
						    socket.close();
						} catch (UnknownHostException e1) {
							e1.printStackTrace();
						} catch (IOException e11) {
							e11.printStackTrace();
						}
					}
				}.start();	
			}
		});
	}
 
	
	
	//复制文件
	public static void copyFile(File sourse,File target) throws IOException
	 {
		 BufferedReader br = new BufferedReader(new FileReader(sourse));
		 PrintWriter bw = new PrintWriter(new BufferedWriter(new FileWriter(target)));
		 String line; 
		 while((line = br.readLine()) != null)
		 {
			 bw.println(line);
		 }	 
		 br.close();
		 bw.flush();
		 bw.close();
	 }
	  
}
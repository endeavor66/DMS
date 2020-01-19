package GUIDesign;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.*;
import java.io.*;

public class Browser extends User {
	private JFrame frame;
	private JPanel contentPane;
	private JPasswordField passwordField;
	private JPasswordField passwordField_1;
	private JPasswordField passwordField_2;
	private JTextField textField;
	
	
	public Browser(String name,String password,String role) {
		super(name,password,role);
	}

	public void showMenu() throws IOException, DataException {
		frame = new JFrame();
		frame.setTitle("档案录入员菜单");
		frame.setBounds((1920 - 1080)/2 ,(1080 - 700)/2,  1080, 700);
		
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		frame.setContentPane(contentPane); 
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu FileMenu = new JMenu("文件");
		menuBar.add(FileMenu);
		
		JMenuItem FileList_menuItem = new JMenuItem("文件列表");
		FileMenu.add(FileList_menuItem);
		FileList_menuItem.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	DataProcessing.FileList();
		    }
		});
		
		
		JMenuItem DownLoadFile_menuItem = new JMenuItem("下载文件");
		FileMenu.add(DownLoadFile_menuItem);
		DownLoadFile_menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    DataProcessing.FileList();
			}
		});
		
		
		JMenu UserMenu = new JMenu("用户");
		menuBar.add(UserMenu);
		
		JMenuItem ChangeInfo_MenuItem = new JMenuItem("修改密码");
		UserMenu.add(ChangeInfo_MenuItem);
		ChangeInfo_MenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				UserOperate();
			}			
		});
		
		//添加关闭窗口事件监听
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if(JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(frame, "是否退出","提示",JOptionPane.OK_CANCEL_OPTION))
				    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				else
				    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);	
			}
		});
		
		frame.setVisible(true);
	}
	
	//用户操作
	/**
	 * @wbp.parser.entryPoint
	 */
	public void UserOperate()
	{
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		
		JPanel Changeinfo_panel = new JPanel();
		Changeinfo_panel.setLayout(null);
		tabbedPane.addTab("修改密码", Changeinfo_panel);
		
		JLabel Label1 = new JLabel("当前用户");
		Label1.setBounds(239, 113, 72, 18);
		Changeinfo_panel.add(Label1);
		
	    textField = new JTextField();
		textField.setBounds(402, 110, 150, 24);
		Changeinfo_panel.add(textField);
		textField.setColumns(10);
		textField.setText(getName());
		textField.setEditable(false);
		
		JLabel Label2 = new JLabel("请输入旧密码");
		Label2.setBounds(239, 162, 91, 32);
		Changeinfo_panel.add(Label2);
		
		passwordField = new JPasswordField();
		passwordField.setColumns(20);
		passwordField.setBounds(402, 166, 150, 24);
		Changeinfo_panel.add(passwordField);
		
		JLabel Label3 = new JLabel("请输入新密码");
		Label3.setBounds(239, 219, 91, 18);
		Changeinfo_panel.add(Label3);
		
		passwordField_1 = new JPasswordField();
		passwordField_1.setText("");
		passwordField_1.setBounds(402, 216, 150, 24);
		Changeinfo_panel.add(passwordField_1);
		
		JLabel Label4 = new JLabel("请再次输入新密码");
		Label4.setBounds(239, 276, 120, 18);
		Changeinfo_panel.add(Label4);
		
		passwordField_2 = new JPasswordField();
		passwordField_2.setBounds(402, 273, 150, 24);
		Changeinfo_panel.add(passwordField_2);
		
		
		
		JButton btnNewButton_1 = new JButton("\u786E\u8BA4");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String opassword = new String(passwordField.getPassword());
				String newpassword1 = new String(passwordField_1.getPassword());
				String newpassword2 = new String(passwordField_2.getPassword());
				User user = DataProcessing.searchUser(getName(),opassword);
				if(user == null)
					JOptionPane.showMessageDialog(null, "旧密码有误", "UnSucceed", JOptionPane.ERROR_MESSAGE);
				else
				    try {
				    if(newpassword1.equals(newpassword2)) {
						if(newpassword1.equals(""))
							JOptionPane.showMessageDialog(null, "密码不可设置为空", "通知", JOptionPane.ERROR_MESSAGE);
						else {
							if(newpassword1.equals(opassword))
								JOptionPane.showMessageDialog(null, "新密码和原密码一样,不用修改", "通知", JOptionPane.INFORMATION_MESSAGE);
							else {
								DataProcessing.updateUser(getName(), newpassword1, getRole());
								JOptionPane.showMessageDialog(null, "修改成功", "Succeed", JOptionPane.INFORMATION_MESSAGE);
							}
						}
					}
					else
						JOptionPane.showMessageDialog(null, "新密码两次输入不一致", "修改失败", JOptionPane.ERROR_MESSAGE);		
				} catch (Exception e1) {
						e1.printStackTrace();
				} 
			}
		});
		btnNewButton_1.setBounds(324, 376, 156, 32);
		Changeinfo_panel.add(btnNewButton_1);
		
	}
	
}

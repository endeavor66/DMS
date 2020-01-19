package GUIDesign;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginUser extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	private JTextField textField;
	private JPasswordField passwordField;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DataProcessing.connect();
					DataProcessing.Init();
					LoginUser frame = new LoginUser();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public LoginUser() {
		super("档案管理系统");
		setBounds(100, 100, 590, 399);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		Toolkit toolkit = getToolkit();
		Dimension dimension = toolkit.getScreenSize();
		int ScreenHeight = dimension.height;
		int ScreenWidth = dimension.width;
		
		setLocation((ScreenWidth-getWidth())/2,(ScreenHeight-getHeight())/2); //setBounds(left, top, right, bottom)相对于父控件的位置
		
		JLabel label_Name = new JLabel("用户名");
		label_Name.setBounds(171, 100, 72, 18);
		contentPane.add(label_Name);
		
		textField = new JTextField();
		textField.setBounds(301, 97, 121, 24);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("密码");
		lblNewLabel.setBounds(171, 147, 72, 18);
		contentPane.add(lblNewLabel);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(301, 144, 121, 24);
		contentPane.add(passwordField);
		
		JButton btnNewButton = new JButton("登陆");
		btnNewButton.setBounds(171, 263, 251, 34);
		contentPane.add(btnNewButton);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				User user = DataProcessing.searchUser(textField.getText(), new String(passwordField.getPassword()));
				if(user == null)
					JOptionPane.showMessageDialog(null, "用户名密码错误", "登陆失败", JOptionPane.ERROR_MESSAGE);
				else
					try {
						user.showMenu();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
			} 
		});
		
		JRadioButton rdbtnNewRadioButton = new JRadioButton("\u8BB0\u4F4F\u5BC6\u7801");
		rdbtnNewRadioButton.setBounds(168, 197, 157, 27);
		contentPane.add(rdbtnNewRadioButton);
		
		JRadioButton rdbtnNewRadioButton_1 = new JRadioButton("\u81EA\u52A8\u767B\u9646");
		rdbtnNewRadioButton_1.setBounds(327, 197, 157, 27);
		contentPane.add(rdbtnNewRadioButton_1);
		
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if(JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(null, "是否退出","询问",JOptionPane.YES_NO_OPTION)) {
					try {
						Socket socket = new Socket("localhost", 5002);
						DataOutputStream order = new DataOutputStream(socket.getOutputStream());
						order.write(3);
						DataProcessing.conn.close();
						socket.close();
					} catch (SQLException | IOException e1) {
						e1.printStackTrace();
					}
					setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				}	
				else
					setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			}
		});
	}
	 
}

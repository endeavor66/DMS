package GUIDesign;
import javax.swing.*;
import java.io.IOException;
import java.util.Scanner;


public abstract class User
{

	protected String name;
	protected String password;
	protected String role;
	
	Scanner input = new Scanner(System.in);
	JPanel panel;
	JLabel l1,l2;
	JTextField t1, t2;
	JButton b1,b2;
	
	User(){}
	//构造函数
	User(String name, String password, String role)
	{
		this.name = name;
		this.password = password;
		this.role = role;
	}
		
    //显示菜单
	public abstract void showMenu() throws IOException,DataException;
	
    //退出系统
	public void exitSystem()
	{
		System.out.println("系统退出, 谢谢使用 ! ");
		System.exit(0);
	}
    //取用户名
	public String getName()
	{
		return name;
	}
    //设置用户名
	public void setName(String name)
	{
		this.name = name;
	}
    //取用户密码
	public String getPassword()
	{
		return password;
	}
    //设置用户密码
	public void setPassword(String password)
	{
		this.password = password;
	}
    //取用户角色
	public String getRole()
	{
		return role;
	}
    //设置用户角色
	public void setRole(String role)
	{
		this.role = role;
	}
    //打印用户信息
	public void print() {
		System.out.println(getName() + "\t" + getPassword() + "\t" + getRole());
	}
	
}
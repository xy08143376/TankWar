import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.swing.JOptionPane;

public class TankClient extends Frame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int Fram_width = 800; // 静态全局窗口大小
	public static final int Fram_length = 600;
	public static boolean printable = true;
	int comWallNum ;
	int metalWallNum;
	int treeNum;
	int[] comX = new int[4];
	int[] comY = new int[4];
	int[] metalX = new int[3];
	int[] metalY = new int[3];
	int[] treeX = new int[4];
	int[] treeY = new int[4];
	int riverX;
	int riverY;
	MenuBar jmb = null;
	Menu jm1 = null, jm2 = null, jm3 = null, jm4 = null;
	MenuItem jmi1 = null, jmi2 = null, jmi3 = null, jmi4 = null, jmi5 = null,
			jmi6 = null, jmi7 = null, jmi8 = null, jmi9 = null;
	Image screenImage = null;

	Tank homeTank = new Tank(300, 560, true, Direction.STOP, this);// 实例化坦克
	GetBlood blood = new GetBlood(); // 实例化生命
	Home home = new Home(373, 545, this);// 实例化home

	List<River> theRiver = new ArrayList<River>();
	List<Tank> tanks = new ArrayList<Tank>();
	List<BombTank> bombTanks = new ArrayList<BombTank>();
	List<Bullets> bullets = new ArrayList<Bullets>();
	List<Tree> trees = new ArrayList<Tree>();
	List<CommonWall> homeWall = new ArrayList<CommonWall>(); // 实例化对象容器
	List<CommonWall> otherWall = new ArrayList<CommonWall>();
	List<MetalWall> metalWall = new ArrayList<MetalWall>();
	

	public void update(Graphics g) {

		screenImage = this.createImage(Fram_width, Fram_length);

		Graphics gps = screenImage.getGraphics();
		Color c = gps.getColor();
		gps.setColor(Color.PINK);
		gps.fillRect(0, 0, Fram_width, Fram_length);
		gps.setColor(c);
		framPaint(gps);
		g.drawImage(screenImage, 0, 0, null);
	}

	public void framPaint(Graphics g) {

		Color c = g.getColor();
		g.setColor(Color.green); // 设置字体显示属性

		Font f1 = g.getFont();
		g.setFont(new Font("TimesRoman", Font.BOLD, 20));
		g.drawString("区域内还有敌方坦克: ", 200, 70);
		g.setFont(new Font("TimesRoman", Font.ITALIC, 30));
		g.drawString("" + tanks.size(), 400, 70);
		g.setFont(new Font("TimesRoman", Font.BOLD, 20));
		g.drawString("剩余生命值: ", 500, 70);
		g.setFont(new Font("TimesRoman", Font.ITALIC, 30));
		g.drawString("" + homeTank.getLife(), 650, 70);
		g.setFont(f1);

		if (tanks.size() == 0 && home.isLive() && homeTank.isLive()) {
			Font f = g.getFont();
			g.setFont(new Font("TimesRoman", Font.BOLD, 60)); // 判断是否赢得比赛
			this.otherWall.clear();
			g.drawString("你赢了！ ", 310, 300);
			g.setFont(f);
		}

		if (homeTank.isLive() == false) {
			Font f = g.getFont();
			g.setFont(new Font("TimesRoman", Font.BOLD, 40));
			tanks.clear();
			bullets.clear();
			this.metalWall.clear();
			this.otherWall.clear();
			this.bombTanks.clear();
			this.theRiver.clear();
			this.trees.clear();
			g.drawString("你输了！", 220, 250);
			g.drawString("游戏结束！", 220, 300);
			g.setFont(f);
		}
		g.setColor(c);

		for (int i = 0; i < theRiver.size(); i++) { // 画出河流
			River r = theRiver.get(i);
			r.draw(g);
		}

		for (int i = 0; i < theRiver.size(); i++) {
			River r = theRiver.get(i);
			homeTank.collideRiver(r);

			r.draw(g);
		}

		home.draw(g); // 画出home
		homeTank.draw(g);// 画出自己家的坦克
		homeTank.eat(blood);// 充满血--生命值

		for (int i = 0; i < bullets.size(); i++) { // 对每一个子弹
			Bullets m = bullets.get(i);
			m.hitTanks(tanks); // 每一个子弹打到敌方坦克上
			m.hitTank(homeTank); // 每一个子弹打到自己家的坦克上时
			m.hitHome(); // 每一个子弹打到家里是

			for (int j = 0; j < metalWall.size(); j++) { // 每一个子弹打到金属墙上
				MetalWall mw = metalWall.get(j);
				m.hitWall(mw);
			}

			for (int j = 0; j < otherWall.size(); j++) {// 每一个子弹打到其他墙上
				CommonWall w = otherWall.get(j);
				m.hitWall(w);
			}

			for (int j = 0; j < homeWall.size(); j++) {// 每一个子弹打到家的墙上
				CommonWall cw = homeWall.get(j);
				m.hitWall(cw);
			}
			m.draw(g); // 画出效果图
		}

		for (int i = 0; i < tanks.size(); i++) {
			Tank t = tanks.get(i); // 获得键值对的键

			for (int j = 0; j < homeWall.size(); j++) {
				CommonWall cw = homeWall.get(j);
				t.collideWithWall(cw); // 每一个坦克撞到家里的墙时
				cw.draw(g);
			}
			for (int j = 0; j < otherWall.size(); j++) { // 每一个坦克撞到家以外的墙
				CommonWall cw = otherWall.get(j);
				t.collideWithWall(cw);
				cw.draw(g);
			}
			for (int j = 0; j < metalWall.size(); j++) { // 每一个坦克撞到金属墙
				MetalWall mw = metalWall.get(j);
				t.collideWithWall(mw);
				mw.draw(g);
			}
			for (int j = 0; j < theRiver.size(); j++) {
				River r = theRiver.get(j); // 每一个坦克撞到河流时
				t.collideRiver(r);
				r.draw(g);
				// t.draw(g);
			}

			t.collideWithTanks(tanks); // 撞到自己的人
			t.collideHome(home);

			t.draw(g);
		}

		blood.draw(g);

		for (int i = 0; i < trees.size(); i++) { // 画出trees
			Tree tr = trees.get(i);
			tr.draw(g);
		}

		for (int i = 0; i < bombTanks.size(); i++) { // 画出爆炸效果
			BombTank bt = bombTanks.get(i);
			bt.draw(g);
		}

		for (int i = 0; i < otherWall.size(); i++) { // 画出otherWall
			CommonWall cw = otherWall.get(i);
			cw.draw(g);
		}

		for (int i = 0; i < metalWall.size(); i++) { // 画出metalWall
			MetalWall mw = metalWall.get(i);
			mw.draw(g);
		}

		homeTank.collideWithTanks(tanks);
		homeTank.collideHome(home);

		for (int i = 0; i < metalWall.size(); i++) {// 撞到金属墙
			MetalWall w = metalWall.get(i);
			homeTank.collideWithWall(w);
			w.draw(g);
		}

		for (int i = 0; i < otherWall.size(); i++) {
			CommonWall cw = otherWall.get(i);
			homeTank.collideWithWall(cw);
			cw.draw(g);
		}

		for (int i = 0; i < homeWall.size(); i++) { // 家里的坦克撞到自己家
			CommonWall w = homeWall.get(i);
			homeTank.collideWithWall(w);
			w.draw(g);
		}

	}
	
	
	public void init() throws FileNotFoundException{		//读入地图参数
		FileReader filein = new FileReader("MapParameter.txt");
		int c = 0;
		try {
			c = filein.read();
			char cc = (char)c;
			String s = "";
			s+=cc;
			while(c!=-1){
				c = filein.read();
				cc = (char)c;
				s+=cc;
			}
			filein.close();
		
			s = s.substring(0, s.length()-1);
			String[] ss = s.split("-");
			comWallNum = Integer.valueOf(ss[0]);
			metalWallNum = Integer.valueOf(ss[1]);
			treeNum = Integer.valueOf(ss[2]);
			for(int i=0;i<4;i++)
				comX[i] = Integer.valueOf(ss[3+i]);
			for(int i=0;i<4;i++)
				comY[i] = Integer.valueOf(ss[7+i]);
			for(int i=0;i<3;i++)
				metalX[i] = Integer.valueOf(ss[11+i]);
			for(int i=0;i<3;i++)
				metalY[i] = Integer.valueOf(ss[14+i]);
			for(int i=0;i<4;i++)
				treeX[i] = Integer.valueOf(ss[17+i]);
			for(int i=0;i<4;i++)
				treeY[i] = Integer.valueOf(ss[21+i]);
			riverX = Integer.valueOf(ss[25]);
			riverY = Integer.valueOf(ss[26]);
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		
	}
	
	public void reset() throws IOException{				//重置地图参数
		FileWriter out = new FileWriter("MapParameter.txt");
		String ss = String.valueOf(comWallNum+"-"+metalWallNum+"-"+treeNum);
		for(int i=0;i<4;i++)
			ss = ss+String.valueOf("-"+comX[i]);
		for(int i=0;i<4;i++)
			ss = ss+String.valueOf("-"+comY[i]);
		for(int i=0;i<3;i++)
			ss = ss+String.valueOf("-"+metalX[i]);
		for(int i=0;i<3;i++)
			ss = ss+String.valueOf("-"+metalY[i]);
		for(int i=0;i<4;i++)
			ss = ss+String.valueOf("-"+treeX[i]);
		for(int i=0;i<4;i++)
			ss = ss+String.valueOf("-"+treeY[i]);
		ss = ss+String.valueOf("-"+riverX+"-"+riverY);
		out.write(ss);
		out.close();
	}
	
	public TankClient() {
		// printable = false;
		try {
			init();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		jmb = new MenuBar();
		jm1 = new Menu("游戏");
		jm2 = new Menu("暂停/继续");
		jm3 = new Menu("帮助");
		jm4 = new Menu("游戏关卡");
		jm1.setFont(new Font("TimesRoman", Font.BOLD, 15));// 设置菜单显示的字体
		jm2.setFont(new Font("TimesRoman", Font.BOLD, 15));// 设置菜单显示的字体
		jm3.setFont(new Font("TimesRoman", Font.BOLD, 15));// 设置菜单显示的字体
		jm4.setFont(new Font("TimesRoman", Font.BOLD, 15));// 设置菜单显示的字体

		jmi1 = new MenuItem("开始新游戏");
		jmi2 = new MenuItem("退出");
		jmi3 = new MenuItem("暂停");
		jmi4 = new MenuItem("继续");
		jmi5 = new MenuItem("游戏说明");
		jmi6 = new MenuItem("关卡1");
		jmi7 = new MenuItem("关卡2");
		jmi8 = new MenuItem("关卡3");
		jmi9 = new MenuItem("关卡4");
		jmi1.setFont(new Font("TimesRoman", Font.BOLD, 15));
		jmi2.setFont(new Font("TimesRoman", Font.BOLD, 15));
		jmi3.setFont(new Font("TimesRoman", Font.BOLD, 15));
		jmi4.setFont(new Font("TimesRoman", Font.BOLD, 15));
		jmi5.setFont(new Font("TimesRoman", Font.BOLD, 15));

		jm1.add(jmi1);
		jm1.add(jmi2);
		jm2.add(jmi3);
		jm2.add(jmi4);
		jm3.add(jmi5);
		jm4.add(jmi6);
		jm4.add(jmi7);
		jm4.add(jmi8);
		jm4.add(jmi9);

		jmb.add(jm1);
		jmb.add(jm2);

		jmb.add(jm4);
		jmb.add(jm3);

		jmi1.addActionListener(this);
		jmi1.setActionCommand("NewGame");
		jmi2.addActionListener(this);
		jmi2.setActionCommand("Exit");
		jmi3.addActionListener(this);
		jmi3.setActionCommand("Stop");
		jmi4.addActionListener(this);
		jmi4.setActionCommand("Continue");
		jmi5.addActionListener(this);
		jmi5.setActionCommand("help");
		jmi6.addActionListener(this);
		jmi6.setActionCommand("level1");
		jmi7.addActionListener(this);
		jmi7.setActionCommand("level2");
		jmi8.addActionListener(this);
		jmi8.setActionCommand("level3");
		jmi9.addActionListener(this);
		jmi9.setActionCommand("level4");

		this.setMenuBar(jmb);// 菜单Bar放到JFrame上
		this.setVisible(true);

		for (int i = 0; i < 10; i++) { // 家的格局
			if (i < 4)
				homeWall.add(new CommonWall(350, 580 - 21 * i, this));		//家的左墙
			else if (i < 7)
				homeWall.add(new CommonWall(372 + 22 * (i - 4), 517, this));	//家的顶墙
			else
				homeWall.add(new CommonWall(416, 538 + (i - 7) * 21, this));	//家的右墙

		}
		
		

		for (int i = 0; i < comWallNum; i++) {
			if (i < comWallNum/2) {
				otherWall.add(new CommonWall(comX[0] + 20 * i, comY[0], this)); // 普通墙布局，每块墙长宽20
				otherWall.add(new CommonWall(comX[1] + 20 * i, comY[1], this));
				otherWall.add(new CommonWall(comX[2], comY[2] + 20 * i, this));
				otherWall.add(new CommonWall(comX[3], comY[3] + 20 * i, this));
			} else if (i < comWallNum) {
				otherWall.add(new CommonWall( comX[0]+ 20 * (i - comWallNum/2), comY[0]+20, this));
				otherWall.add(new CommonWall(comX[1] + 20 * (i - comWallNum/2), comY[1]+20, this));
				otherWall.add(new CommonWall(comX[2]+20, comY[2] + 20 * (i - comWallNum/2), this));
				otherWall.add(new CommonWall(comX[3]+20, comY[3] + 20 * (i - comWallNum/2), this));
			}
		}

		for (int i = 0; i < metalWallNum; i++) { // 金属墙布局，长30，宽30
			if (i < metalWallNum/2) {
				metalWall.add(new MetalWall(metalX[0] + 30 * i, metalY[0], this));
				metalWall.add(new MetalWall(metalX[1], metalY[1] + 20 * (i), this));
			} else if (i < metalWallNum)
				metalWall.add(new MetalWall(metalX[2] + 30 * (i - metalWallNum/2), metalY[2], this));
			else
				metalWall.add(new MetalWall(500 + 30 * (i - metalWallNum/2), 160, this));
		}

		for (int i = 0; i < treeNum; i++) { // 树的布局，长30，宽36
			if (i < treeNum) {
				trees.add(new Tree(treeX[0] + 30 * i, treeY[0], this));
				trees.add(new Tree(treeX[1] + 30 * i, treeY[1], this));
				trees.add(new Tree(treeX[2] + 30 * i, treeY[2], this));
				trees.add(new Tree(treeX[3] + 30 * i, treeY[3], this));
				//trees.add(new Tree(100, 400+36*i, this));
			}

		}

		theRiver.add(new River(riverX, riverY, this));		//布置河流

		for (int i = 0; i < 20; i++) { // 初始化20辆坦克
			if (i < 9) // 设置坦克出现的位置
				tanks.add(new Tank(150 + 70 * i, 40, false, Direction.D, this));
			else if (i < 15)
				tanks.add(new Tank(700, 100 + 50 * (i - 6), false, Direction.D,
						this));
			else
				tanks
						.add(new Tank(10, 50 * (i - 12), false, Direction.D,
								this));
		}

		this.setSize(Fram_width, Fram_length); // 设置界面大小
		this.setLocation(280, 50); // 设置界面出现的位置
		this
				.setTitle("坦克大战——(重新开始：R键  开火：F键)");

		this.addWindowListener(new WindowAdapter() { // 窗口监听关闭
					public void windowClosing(WindowEvent e) {
						comWallNum = 32;
						metalWallNum = 20;
						treeNum = 4;
						comX[0]=comX[2]=200;comX[1]=comX[3]=500;
						comY[0]=300;comY[1]=180;comY[2]=comY[3]=400;
						metalX[0]=metalX[2]=140;metalX[1]=600;
						metalY[0]=150;metalY[1]=400;metalY[2]=180;
						treeX[0]=0;treeX[1]=220;treeX[2]=440;treeX[3]=660;
						for(int i=0;i<4;i++)
							treeY[i]=360;
						riverX=85;riverY=100;
						
						try {
							reset();
						} catch (IOException e1) {
							// TODO 自动生成的 catch 块
							e1.printStackTrace();
						}
						
						System.exit(0);
					}
				});
		this.setResizable(false);
		this.setBackground(Color.GREEN);
		this.setVisible(true);

		this.addKeyListener(new KeyMonitor());// 键盘监听
		new Thread(new PaintThread()).start(); // 线程启动
	}

	public static void main(String[] args) {
		new TankClient(); // 实例化
	}

	private class PaintThread implements Runnable {
		public void run() {
			// TODO Auto-generated method stub
			while (printable) {
				repaint();
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private class KeyMonitor extends KeyAdapter {

		public void keyReleased(KeyEvent e) { // 监听键盘释放
			homeTank.keyReleased(e);
		}

		public void keyPressed(KeyEvent e) { // 监听键盘按下
			homeTank.keyPressed(e);
		}

	}

	public void actionPerformed(ActionEvent e) {

		if (e.getActionCommand().equals("NewGame")) {
			printable = false;
			Object[] options = { "确定", "取消" };
			int response = JOptionPane.showOptionDialog(this, "您确认要开始新游戏！", "",
					JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE, null,
					options, options[0]);
			if (response == 0) {

				printable = true;
				this.dispose();
				new TankClient();
			} else {
				printable = true;
				new Thread(new PaintThread()).start(); // 线程启动
			}

		} else if (e.getActionCommand().endsWith("Stop")) {
			printable = false;
			// try {
			// Thread.sleep(10000);
			//
			// } catch (InterruptedException e1) {
			// // TODO Auto-generated catch block
			// e1.printStackTrace();
			// }
		} else if (e.getActionCommand().equals("Continue")) {

			if (!printable) {
				printable = true;
				new Thread(new PaintThread()).start(); // 线程启动
			}
			// System.out.println("继续");
		} else if (e.getActionCommand().equals("Exit")) {
			printable = false;
			Object[] options = { "确定", "取消" };
			int response = JOptionPane.showOptionDialog(this, "您确认要退出吗", "",
					JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE, null,
					options, options[0]);
			if (response == 0) {
				comWallNum = 32;
				metalWallNum = 20;
				treeNum = 4;
				comX[0]=comX[2]=200;comX[1]=comX[3]=500;
				comY[0]=300;comY[1]=180;comY[2]=comY[3]=400;
				metalX[0]=metalX[2]=140;metalX[1]=600;
				metalY[0]=150;metalY[1]=400;metalY[2]=180;
				treeX[0]=0;treeX[1]=220;treeX[2]=440;treeX[3]=660;
				for(int i=0;i<4;i++)
					treeY[i]=360;
				riverX=85;riverY=100;
				
				try {
					reset();
				} catch (IOException e1) {
					// TODO 自动生成的 catch 块
					e1.printStackTrace();
				}
//				System.out.println("退出");
				System.exit(0);
			} else {
				printable = true;
				new Thread(new PaintThread()).start(); // 线程启动

			}

		} else if (e.getActionCommand().equals("help")) {
			printable = false;
			JOptionPane.showMessageDialog(null, "用→ ← ↑ ↓控制方向，F键盘发射，R重新开始！要切换英文输入法！",
					"提示！", JOptionPane.INFORMATION_MESSAGE);
			this.setVisible(true);
			printable = true;
			new Thread(new PaintThread()).start(); // 线程启动
		} else if (e.getActionCommand().equals("level1")) {
			comWallNum = 32;
			metalWallNum = 20;
			treeNum = 4;
			comX[0]=comX[2]=200;comX[1]=comX[3]=500;
			comY[0]=300;comY[1]=180;comY[2]=comY[3]=400;
			metalX[0]=metalX[2]=140;metalX[1]=600;
			metalY[0]=150;metalY[1]=400;metalY[2]=180;
			treeX[0]=0;treeX[1]=220;treeX[2]=440;treeX[3]=660;
			for(int i=0;i<4;i++)
				treeY[i]=360;
			riverX=85;riverY=100;
			
			try {
				reset();
			} catch (IOException e1) {
				// TODO 自动生成的 catch 块
				e1.printStackTrace();
			}
			Tank.count = 12;
			Tank.speedX = 6;
			Tank.speedY = 6;
			Bullets.speedX = 10;
			Bullets.speedY = 10;
			this.dispose();
			new TankClient();
		} else if (e.getActionCommand().equals("level2")) {
			comWallNum = 20;
			metalWallNum = 16;
			treeNum = 3;
			comX[0]=295;comX[1]=80;comX[2]=200;comX[3]=500;
			comY[0]=430;comY[1]=540;comY[2]=comY[3]=150;
			metalX[0]=95;metalX[1]=600;metalX[2]=435;
			metalY[0]=metalY[2]=350;metalY[1]=450;
			treeX[0]=100;treeX[1]=250;treeX[2]=410;treeX[3]=660;
			for(int i=0;i<4;i++)
				treeY[i] = 300;
			riverX=350;riverY=150;
			try {
				reset();
			} catch (IOException e1) {
				// TODO 自动生成的 catch 块
				e1.printStackTrace();
			}
			Tank.count = 12;
			Tank.speedX = 10;
			Tank.speedY = 10;
			Bullets.speedX = 12;
			Bullets.speedY = 12;
			this.dispose();
//			JOptionPane.showMessageDialog(null, comWallNum);
			new TankClient();
			

		} else if (e.getActionCommand().equals("level3")) {
			comWallNum = 30;
			metalWallNum = 16;
			treeNum = 3;
			comX[0]=230;comX[1]=230;comX[2]=540;comX[3]=190;
			comY[0]=330;comY[1]=370;comY[2]=400;comY[3]=0;
			metalX[0]=500;metalX[1]=190;metalX[2]=640;
			metalY[0]=150;metalY[1]=300;metalY[2]=480;
			treeX[0]=280;treeX[1]=370;treeX[2]=550;treeX[3]=640;
			treeY[0]=220;treeY[1]=220;treeY[2]=330;treeY[3]=330;
			riverX=80;riverY=400;
			try {
				reset();
			} catch (IOException e1) {
				// TODO 自动生成的 catch 块
				e1.printStackTrace();
			}
			Tank.count = 20;
			Tank.speedX = 14;
			Tank.speedY = 14;
			Bullets.speedX = 16;
			Bullets.speedY = 16;
			this.dispose();
			new TankClient();
		} else if (e.getActionCommand().equals("level4")) {
			comWallNum = 20;
			metalWallNum = 6;
			treeNum = 3;
			comX[0]=0;comX[1]=350;comX[2]=550;comX[3]=250;
			comY[0]=540;comY[1]=350;comY[2]=450;comY[3]=100;
			metalX[0]=350;metalX[1]=300;metalX[2]=600;
			metalY[0]=450;metalY[1]=450;metalY[2]=350;
			treeX[0]=440;treeX[1]=560;treeX[2]=50;treeX[3]=450;
			treeY[0]=550;treeY[1]=500;treeY[2]=150;treeY[3]=150;
			riverX=600;riverY=100;
			try {
				reset();
			} catch (IOException e1) {
				// TODO 自动生成的 catch 块
				e1.printStackTrace();
			}
			Tank.count = 20;
			Tank.speedX = 16;
			Tank.speedY = 16;
			Bullets.speedX = 18;
			Bullets.speedY = 18;
			this.dispose();
			new TankClient();
		}
	}
}

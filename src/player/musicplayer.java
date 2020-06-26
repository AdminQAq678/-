package player;
import java.awt.Color;
import java.awt.FileDialog;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JProgressBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JPanel;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.border.EmptyBorder;
import java.awt.Toolkit;
import java.awt.Font;


/**
 * @
 * @author 陈南忠、黄宜都、陈嘉仪、钟颖颖
 * UI界面设计：黄宜都
 * 播放功能、
 * 上一首下一首、
 * 播放模式、
 * 浏览文件、
 * 播放音乐、
 * 选择歌曲、
 * 进度条控制
 * 
*/
@SuppressWarnings("serial")
public class musicplayer extends JFrame{
	/***************************************************************************************************************/
	/*																											   */ 
	/*								这里只是类成员变量的定义而已，大概过一遍，不用仔细看											   */ 
	/*																											   */
	/***************************************************************************************************************/
	private boolean flag=false;																			//换曲标志
	private JLabel tips01;																				//提示
	private final ReentrantLock lock=new ReentrantLock();												//锁
	private ExecutorService pool=Executors.newCachedThreadPool();										//线程池
	boolean SelectedChangeFlag=false;																	//选择修改标志
	private String fileName="";																			//文件名称
	private Thread ProgressThread;																		//进度条线程
	private int modelSize=4;																			//实际只有3种播放模式，为了取余限制范围在1~3
	private int index=0;																				//正在播放的歌曲序号
	private String currentDirectory="";																	//当前目录
	private boolean hasStop=true;																		//歌曲播放停止标志
	private boolean PlayForFirstTime=true;																//是否第一次播放歌曲标志
	private ArrayList<String> MySongList=new ArrayList();												//歌曲列表
	private AudioInputStream audioInputStream ;															//音频输入流
	private AudioFormat audioformat;																	//音频格式类
	private Clip myclip;																				//音频剪辑对象
	private JButton play_pause ;																		//播放_暂停按钮
	private JButton pre_btn;																			//上一首
	private JButton next_btn;																			//下一首
	private JButton changeModel;																		//修改播放模式按钮
	boolean isPlaying=false;																			//是否正在播放歌曲
	private JProgressBar progress;																		//进度条
	private JLabel tips;																				//播放提示
	@SuppressWarnings("rawtypes")																		//压制警告
	private JComboBox songs;																			//下拉列表框
	private JMenuBar jmunubar;																			//菜单栏											
	private JMenuItem songsSelection;																	//下拉框，选歌的
	private JMenuItem fileOpen;																			//菜单项（浏览文件)
	private int PlayState=1;																			//循环播放
	private String path=System.getProperty("user.dir");													//获取用户当前文件夹路径
	private JLabel label;																				//歌名
	private JLabel songtime;																			//歌总时长
	private JLabel notesongtime;																		//播放时间进度
	private String picturefile="\\picture";
	/***************************************************************************************************************/
	/*																											   
	 *								                                         这里是图形界面的设计	
	 *
	 *
	 * 							知识点：1、程序的背景是一个label,通过载入图片来覆盖原来的默认背景，语句为：*.setIcon(图片路径);
	 * 								 2、通过：不再聚焦，语句为：*.setFocusPainted(true/false);
	 * 						       		          隐藏边框，语句为：*.setBorderPainted(true/false);
	 * 									          自定义字体，语句为：*.setFont(字体，形状，大小);
	 * 									          前景颜色（字体颜色），语句为：*.setForeground(颜色);				
	 * 								         来美观程序的界面
	 *											
	 *																											   
	/***************************************************************************************************************/
	@SuppressWarnings({ "deprecation", "rawtypes" })
	public musicplayer() throws UnsupportedAudioFileException, IOException {
		
		setForeground(new Color(255, 190, 3));															//设置前景颜色为橙色
		setIconImage(Toolkit.getDefaultToolkit().getImage(path+picturefile+"\\tubiao.png"));						//设置窗口图标
		setBackground(new Color(255, 190, 3));															//设置背景颜色
		getContentPane().setBackground(new Color(255, 190, 3));											//设置面板颜色
		
		jmunubar=new JMenuBar();
		jmunubar.setBorderPainted(false);																//不绘制边框
		jmunubar.setForeground(new Color(255, 190, 3));													//设置前景颜色
		jmunubar.setBackground(new Color(255, 190, 3));													//设置背景颜色
		this.setJMenuBar(jmunubar);
		
		songsSelection=new JMenuItem("Select song");													//为菜单栏添加“选择歌曲”菜单项
		songsSelection.setFont(new Font("Segoe UI Black", Font.ITALIC, 25));							//设置字体
		songsSelection.setBackground(new Color(255, 190, 3));											//设置背景颜色
		songsSelection.setForeground(Color.WHITE);														//设置前景颜色
		songsSelection.setBorderPainted(false);															//不绘制边框
		
	/*********************************************下面类似，不再注释****************************************************/
		
		fileOpen=new JMenuItem("Load files");
		fileOpen.setFont(new Font("Segoe UI Black", Font.ITALIC, 25));
		fileOpen.setBackground(new Color(255, 190, 3));
		fileOpen.setForeground(Color.WHITE);
		fileOpen.setFocusPainted(false);
		fileOpen.setBorderPainted(false);
		fileOpen.setContentAreaFilled(false);
		
		JMenu fileMenu=new JMenu("");
		fileMenu.setOpaque(false);
		jmunubar.add(fileMenu);
		fileMenu.setForeground(new Color(255, 190, 3));
		fileMenu.setFocusable(false);
		fileMenu.setFocusPainted(false);
		fileMenu.setContentAreaFilled(false);
		fileMenu.setIcon(new ImageIcon(path+picturefile+"\\doc1.png"));												//使用图标功能
		fileMenu.setBackground(new Color(255, 190, 3));
		fileMenu.add(fileOpen);
		fileMenu.add(songsSelection);
		
		
		tips = new JLabel("Please wait a few seconds");
		tips.setForeground(Color.WHITE);
		tips.setFont(new Font("Segoe UI Black", Font.ITALIC, 25));
		jmunubar.add(tips);
		tips.setVisible(false);
		
		songs=new JComboBox() ;
		songs.setFont(new Font("微软雅黑", Font.BOLD, 12));												//播放列表
		jmunubar.add(songs);
		songs.setVisible(false);																		//程序刚运行隐藏歌曲选择下拉框
		songs.setForeground(Color.WHITE);
		songs.setBorder(new EmptyBorder(3, 3, 3, 3));
		songs.setFocusable(false);
		songs.setBackground(new Color(255, 190, 3));
		
		getContentPane().setLayout(null);
		JPanel panel = new JPanel();
		panel.setBackground(new Color(255, 190, 3));
		panel.setBounds(10, 403, 368, 163);
		getContentPane().add(panel);
		panel.setLayout(null);
		
		play_pause=new JButton("");
		play_pause.setIcon(new ImageIcon(path+picturefile+"\\Play.png"));
		play_pause.setPressedIcon(new ImageIcon(path+picturefile+"\\PressIcon.png"));
		play_pause.setBounds(280, 70, 50, 50);
		play_pause.setBackground(new Color(255, 190, 3));
		play_pause.setFocusPainted(false);  
		play_pause.setBorderPainted(false);
		play_pause.setContentAreaFilled(false);
		panel.add(play_pause);
		
		pre_btn =new JButton("");
		pre_btn.setIcon(new ImageIcon(path+picturefile+"\\Lastsong.png"));
		pre_btn.setPressedIcon(new ImageIcon(path+picturefile+"\\PressIcon.png"));
		pre_btn.setBounds(120, 70, 50, 50);
		pre_btn.setBackground(new Color(255, 190, 3));
		pre_btn.setFocusPainted(false); 
		pre_btn.setBorderPainted(false);
		pre_btn.setContentAreaFilled(false);
		panel.add(pre_btn);
		
		next_btn =new JButton("");
		next_btn.setIcon(new ImageIcon(path+picturefile+"\\Nextsong.png"));
		next_btn.setPressedIcon(new ImageIcon(path+picturefile+"\\PressIcon.png"));
		next_btn.setBounds(200, 70, 50, 50);
		next_btn.setBackground(new Color(255, 190, 3));
		next_btn.setFocusPainted(false); 
		next_btn.setBorderPainted(false);
		next_btn.setContentAreaFilled(false);
		panel.add(next_btn);
		
		progress=new JProgressBar();
		progress.setBounds(72, 15, 217, 6);
		panel.add(progress);
		progress.setBackground(Color.WHITE);
		progress.setForeground(new Color(255, 190, 3));
		progress.setBorderPainted(false);
		progress.setMinimum(0);
		progress.setEnabled(true);
		
		changeModel=new JButton();
		changeModel.setIcon(new ImageIcon(path+picturefile+"\\xunhuan.png"));
		changeModel.setBounds(40, 70, 50, 50);
		changeModel.setBackground(new Color(255, 190, 3));
		changeModel.setFocusPainted(false); 
		changeModel.setBorderPainted(false);
		changeModel.setContentAreaFilled(false);
		panel.add(changeModel);
		
		label = new JLabel("");
		label.setForeground(Color.WHITE);
		label.setFont(new Font("微软雅黑", Font.ITALIC, 18));
		label.setBounds(72, 34, 217, 21);
		panel.add(label);
		
		JLabel background = new JLabel("");
		background.setLabelFor(background);
		background.setIcon(new ImageIcon(path+picturefile+"\\back.gif"));
		background.setBounds(0, 26, 383, 379);
		getContentPane().add(background);		
		
		songtime = new JLabel("New label");
		songtime.setFont(new Font("微软雅黑", Font.ITALIC, 13));
		songtime.setForeground(Color.WHITE);
		songtime.setBackground(new Color(255, 190, 3));
		songtime.setBounds(303, 7, 50, 21);
		panel.add(songtime);
		
		notesongtime = new JLabel("New label");
		notesongtime.setBackground(new Color(255, 190, 3));
		notesongtime.setFont(new Font("微软雅黑", Font.ITALIC, 13));
		notesongtime.setForeground(Color.WHITE);
		notesongtime.setBounds(27, 7, 44, 21);
		panel.add(notesongtime);
		songtime.setText(time(0)); 
		notesongtime.setText(time(0));
		
		tips01 = new JLabel("");
		tips01.setFont(new Font("微软雅黑", Font.ITALIC, 16));
		tips01.setBackground(new Color(255, 190, 3));
		tips01.setForeground(Color.WHITE);
		tips01.setBounds(122, 0, 247, 21);
		getContentPane().add(tips01);
		
			
		/***************************************************************************************************************/
		/*																											   */ 
		/*											         这里是各个组件监听											           */ 
		/*																											   */
		/***************************************************************************************************************/
		
		
		/*************************************************弹出下拉框*******************************************************/
		songsSelection.addActionListener((e)->{															//设置歌曲下拉框可见
			songs.setVisible(true);
			if(MySongList.isEmpty()==true) {
				label.setText("Please select a song");
			}
			
			tips01.setText("Double click to play");
			label.setVisible(true);
			tips01.setVisible(true);
		});
		
		
		/************************************************弹出文件浏览器*****************************************************/
		fileOpen.addActionListener((e)->{	
			OpenFile();																					//打开文件浏览器，用于显示浏览文件和选择歌曲菜单项
			songs.setVisible(true);
			label.setText("Please choose a song");
			tips01.setText("Double click to play");
			label.setVisible(true);
			tips01.setVisible(true);
			
		});
	
		/**********************************************双击选歌，并播放歌曲***************************************************/
		songs.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount()==2) {																//双击播放
					if(songs.getSelectedItem()==null) return ;											//没有选择歌曲
					if(myclip!=null&&myclip.isRunning()==true) {										//如果不是第一次播放，且前一首歌曲正在播放,则终止前一首歌的播放
						myclip.stop();					//***************************************************************************//
						myclip.close();					//知识点：  1、myclip是一个音频剪辑对象，通过它来进行歌词的播放等操作							 //
						ProgressThread.stop();			//	    2、myclip.stop,意思是暂停歌曲的播放，暂停歌曲数据流的输入和输出，与其相反的是myclip.start //
														//		3、myclip.close,意思是终止歌曲的播放，同时呢，释放占据的系统资源，也就是说歌曲的缓存会被清除        //
														//		4、ProgressThread是进度条的线程，既然歌都不放了，那么对应的进度线程也要关掉，注意这里的.stop //
														//        不是暂停的意思，而是这个线程被终止了                     									 //
					}									//***************************************************************************//		
					
					index=songs.getSelectedIndex();														//播放选择的歌曲
					fileName=currentDirectory+"\\"+songs.getSelectedItem().toString();					//拼接文件路径
					isPlaying=true;																		//时候正在播放歌曲的标志
					open(fileName);																		//播放歌曲
					play_pause.setIcon(new ImageIcon(path+picturefile+"\\Play.png"));								//改变播放按键的图案
					songs.setVisible(false);															//下拉列表消失，还原整洁的界面
													   //***************************************************************************//
													   //知识点：  1、index是歌曲列表的索引值，通过它来进行歌曲下一曲，上一曲等操作						    //
													   //	    2、isPlaying作为标志，用来判断歌曲是否正在播放，暂停和播放等功能会使用到它 				//
													   //***************************************************************************//	
					
					}}});

		/***************************************************播放模式*********************************************************/
		/**************************************************1是循环播放********************************************************/
		/**************************************************2是单曲循环********************************************************/
		/**************************************************3是随机播放********************************************************/
		changeModel.addActionListener((e)->{
			PlayState=(PlayState+1)%modelSize;//播放模式改变
			if(PlayState==0) PlayState=1;//以1开始
			if(PlayState==2) {//单曲循环
				changeModel.setIcon(new ImageIcon(path+picturefile+"\\danquxunhuan.png"));
			}
			else if(PlayState==1){
				changeModel.setIcon(new ImageIcon(path+picturefile+"\\xunhuan.png"));
			}
			else if (PlayState==3){
				changeModel.setIcon(new ImageIcon(path+picturefile+"\\suiji.png"));
			}
													  //***************************************************************************//
			   										  //					注意这里只是更换按键的图案，没有实际功能						       //
			   										  //	   				实际的功能在下面的进度条监听中实现					               //
													  //					为什么不把功能放在这里？                                                                                                                     //
													  //					原因是因为“歌曲的续播”是程序自动进行的，不是人为的               				   //
													  //					所以监听进度条来实现续播模式的功能								   //
			   										  //***************************************************************************//	

		});
		
		/**************************************************监听进度条的进度*********************************************************/
		
		progress.addChangeListener(new ChangeListener () {
			@SuppressWarnings("static-access")
			@Override
			public void stateChanged(ChangeEvent e) {
				if(progress.getValue()==progress.getMaximum()) {								//播放完毕，切换下一首，如果只有一首则循环，根据播放模式选择
					progress.setValue(0);														//设置进度条当前位置为0
					if(PlayState==2) {															//单曲循环
						myclip.loop(myclip.LOOP_CONTINUOUSLY);									//设置当前歌曲的循环次数为无限次，即单曲循环
					}else if(PlayState==1) {													//循环列表
						index=Math.abs(index+1)%songs.getItemCount();//*******************************************************************************//
					    songs.setSelectedIndex(index);				 //知识点：1、PlayState的值是播放模式的关键，PlayState的1，2，3，对应着“列表循环，单曲循环，随机循环”三个功能   //
					    myclip.stop();								 //		2、单曲循环用myclip自带的循环方法来实现，语句为：myclip.loop(myclip.LOOP_CONTINUOUSLY)  //	
					    myclip.close();								 //     3、 System.gc()，回收一下那些没有被使用的对象所占用的内存，就是回收垃圾，其实不用这条也没关系，知道就好     //
					    System.gc();								 //*******************************************************************************//		
						fileName=currentDirectory+"\\"+songs.getItemAt(index).toString();
						open(fileName);
						ProgressThread.stop();
					}else if (PlayState==3){													//随机播放
						myclip.stop();															//停止当前播放的歌曲并释放资源
						myclip.close();
						Random r=new Random();													//随机类
						index=r.nextInt(songs.getItemCount());									//获取[0，songs.getItemCount())范围的随机数，即在0到歌曲数目范围内的的随机数
						songs.setSelectedIndex(index);
						fileName=currentDirectory+"\\"+songs.getItemAt(index).toString();
						
						open(fileName);//
						ProgressThread.stop();
					}else if(PlayState==4){														//播放结束就停止（有待增加）
					}
					return ;
				}
				
			}
			
		});
		
		/***************************************************还是监听进度条，事件源是鼠标*******************************************************/
		/*****************************************************鼠标点哪里，歌仔就播哪里********************************************************/
		progress.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(myclip==null) return ;
				float tem=((float) e.getX()/(float) progress.getWidth());			//e.getX()为当前鼠标相对于进度条的横向坐标，progress.getWidth()为进度条的宽度
																					//tem为当前鼠标距离进度条x轴原点的值占进度条的百分比(小数表示)
				progress.setValue((int) (progress.getMaximum()*tem));			    //设置进度条的值
				myclip.setMicrosecondPosition( (long) (progress.getMaximum()*tem));	//用tem乘于进度条的最大值即为当前鼠标所在位置，设置微秒的播放位置
				//**************************************************************************************************//
				//			知识点：	 1、e.getX()，意思是鼠标在进度条上位置						   							//
				//	   				 2、progress.getWidth()，意思是进度条的总长度				  							//
				//					 3、tem=e.getX()/progress.getWidth()， 鼠标的位置占进度条的百分比               						//
				//					 4、 progress.getMaximum(), 进度条最大值，注意喔，每首歌的时间都是不同的，所以进度条的最大值也是不同滴 	//
				//					 5、progress.getMaximum()*tem，因为每首歌的时间都是不一样的，所以用百分比去控制播放点				//
				//**************************************************************************************************//	
			}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {	}});
		
		/*******************************************************上一曲，下一曲***********************************************************/
		next_btn.addActionListener((e)->nextOrPreSong(1));														//下一首歌曲
		pre_btn.addActionListener((e)->nextOrPreSong(-1));														//上一首歌曲
		//**************************************************************************************************//
		//			知识点：	 1、只有两行代码，这就是所谓的Lambda表达式写法，看起来高大上，其实没什么用，核心功能代码还是要自己写			//
		//	   				 2、nextOrPreSong()，才是这两句代码的核心所在，它是一个 封装的方法，写在最后面，1为上一曲，-1为下一曲		//
		//					 3、在nextOrPreSong()里面，将通过加减法，来选择播放哪首歌，+1就下一曲嘛，-1就上一曲嘛             			//
		//**************************************************************************************************//
		
		/*******************************************************播放和暂停*************************************************************/
			//*******************************************上部分*******************************************************//
			//			知识点：	 1、上部分主要是服务于第一次打开播放器的时候，或者重新载入歌曲的时候，你可以直接播放歌曲						 	//
			//	   				 2、这部分代码对应着下面第433-447行代码，先载入音乐文件，然后点播放键进行播放								//
			//******************************************************************************************************//
		play_pause.addActionListener((e)->{
			if(songs.getItemCount()!=0&&myclip==null) {															//加载了文件，但是没有播放过歌曲
				index=songs.getSelectedIndex();																	//选择当前选择的歌曲
				fileName=currentDirectory+"\\"+songs.getItemAt(index).toString();
				label.setText(fileName);																		//歌名提示
				open(fileName);																					//播放歌曲
			}			
			//*******************************************中部分*****************************************************//
			//			知识点：	 1、中部分代码，才是实际意义上的暂停和播放，主要服务于已经缓存过的歌曲，也就是正在播放的歌		  			  //
			//	   				 2、通过myclip!=null来判断是否有缓存，这里的意思是有滴										  //
			//					 3、通过isPlaying来判断是否正在播放             			                                          //
			//			         4 、通过flag来判断你是否在“暂停”的时候点了“上一曲”或者“下一曲”的功能，如果你点了，那么执行的是第三部分代码             	  //
			//****************************************************************************************************//
			if(isPlaying==false&&myclip!=null&&flag==false) {													//如果暂停，播放歌曲
				play_pause.setIcon(new ImageIcon(path+picturefile+"\\Play.png"));
				
			    /*******注意这里*********/
				/**/myclip.start();/**/
				/*********************/
				
				songs.setVisible(false);
				isPlaying=true;	
			}else if(isPlaying==true&&myclip!=null){															//如果正在播放，则暂停
				play_pause.setIcon(new ImageIcon(path+picturefile+"\\Suspend.png"));
				
				/*********注意这里*******/
				/**/myclip.stop(); /**/
				/*********************/
				
				songs.setVisible(true);																			//播放暂停显示选歌
				isPlaying=false;
			}
			//*******************************************下部分*******************************************************//
			//			知识点：	 1、下部分主要是服务于你在暂停的时候，点了上一曲或者下一曲的功能，通过flag来判断								//
			//	   				 2、既然已经点了换曲的功能，所以再次播放的时候，前一首歌需要终止，所以需要对myclip来进行一些操作，stop和close   	//
			//******************************************************************************************************//
			else if(isPlaying==false&&myclip!=null&&flag==true) {
				flag=false;
				myclip.stop();
				myclip.close();
				System.gc();
				index=songs.getSelectedIndex();
				fileName=currentDirectory+"\\"+songs.getItemAt(index).toString();
				label.setText(fileName);
				open(fileName);
				ProgressThread.stop();
			}});
		
		/*******************************************************第一次载入歌曲*************************************************************/
		//******************************************************************************************************//
		//			知识点：	 1、程序最开始运行的时候，通过这里录入程序自带的歌													//
		//	   				 2、   File songFiles[]=f.listFiles();通过则一行代码，可以得到path里面所有的文件名					//
		//			 		 3、然后再用一个FOR循环一个一个去判断时候为mp3或者wav文件							  				//
		//			 		问： 那可以播放不是MP3或者WAV的文件吗？							  								//
		//			 		答：当然不可以，现在播放器的功能只能进行MP3和WAV文件							  					//
		//		 			4、t.toString().toLowerCase().endsWith(".mp3")						  				//
		//			                                歌名转为字符	           所有字符转为小写		判断后缀			  									//
		//******************************************************************************************************//
		File f=new File(path);
		File songFiles[]=f.listFiles();	//保存歌曲名称的列表
		for (File t : songFiles) {
			if(t.toString().toLowerCase().endsWith(".mp3")||t.toString().toLowerCase().endsWith(".wav")) {		//筛选mp3或wav格式的文件
				MySongList.add(t.getName().toString().toLowerCase());
				songs.addItem(t.getName().toString().toLowerCase());											//将筛选出的歌曲加入到列表中
			}
		}
		currentDirectory=path;
		fileName=currentDirectory+"\\"+songs.getItemAt(index).toString();
		//System.out.println(currentDirectory+"  "+fileName);
		label.setText(songs.getItemAt(index).toString());
		/*******************************************************跳过这个*************************************************************/
		
		this.setResizable(false);								//边框不能被改变
		this.setSize(390,652);
		this.setLocationRelativeTo(null);						//根据以下方案设置窗口相对于指定组件的位置
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
	}
	
	/***************************************************************************************************************/
	/*																											   */ 
	/*											         这里是主函数												           */ 
	/*																											   */
	/***************************************************************************************************************/
	
	
	public static void main(String[] args) throws UnsupportedAudioFileException, IOException {
		new musicplayer();
	}
	
	
	/***************************************************************************************************************/
	/*																											   */ 
	/*											         这里封装着是各种功能函数							 					   */ 
	/*																											   */
	/***************************************************************************************************************/
	
	/*******************************************************音乐播放函数*************************************************************/
	
	public void open(String fileName){													//播放音乐函数
		pool.execute(new Runnable() {													//播放歌曲线程
			@Override				//*************************//
			public void run() {		//注意，这里是一个线程池线程		   //
				lock.lock();		//还有锁					   //
									//为什么用到锁呢，用着玩玩，没什么意思   //
									//*************************//
				label.setText(songs.getItemAt(index).toString());
				songs.setVisible(false);												//设置歌曲列表不可见
				tips.setVisible(true);
				play_pause.setEnabled(false);			//********************************//
				next_btn.setEnabled(false);				//这里限制缓存时一些播放器的功能，避免一些人为异常//
				pre_btn.setEnabled(false);				//********************************//
				songsSelection.setEnabled(false);
				tips01.setVisible(false);
				fileOpen.setEnabled(false);
							
				if(MySongList.size()==0) //音乐列表为空 
						return;		

				File f = new File(fileName);
				
				try {
					audioInputStream=AudioSystem.getAudioInputStream(f);		//获得音频输入流			
				} catch (UnsupportedAudioFileException e) {						//不支持的音乐格式
					return ;					
				} catch (IOException e) {
					e.printStackTrace();
				}
																		
				audioformat=audioInputStream.getFormat();						//获得音频格式
					
			    if (audioformat.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {// 转换mp3文件编码，判断编码格式是否为PCM格式，不是的话需要转码
			    	//******************************************************************************************************************************//
					//			知识点：	 1、AudioFormat.Encoding.PCM_SIGNED，指定有符号的线性 PCM 数据。														//
					//	   				 2、  audioFormat.getSampleRate()，波形采样率																		//
					//			 		 3、为什么audioFormat.getChannels(), audioFormat.getChannels()用了一次后还要再一个audioFormat.getChannels()*2			//
			    	//						因为每声道每帧字节数是2字节，再加上双声道所以用声道数（2）*字节数（2） = 每帧的总字节数												//
			    	//					 4、16，16位双字节采集audioFormat.getChannels()																	//
					//******************************************************************************************************************************//
			        audioformat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
			        audioformat.getSampleRate(),16,audioformat.getChannels(),
			        audioformat.getChannels()*2,audioformat.getSampleRate(),false);

			        audioInputStream = AudioSystem.getAudioInputStream(audioformat,audioInputStream);//获取转换格式后的音频输入流
			     }
			        
			        													
			     try {
					myclip=AudioSystem.getClip();									// 打开输出设备，获取音频的剪辑播放对象				
				} catch (LineUnavailableException e) {
					e.printStackTrace();
				}		
			        
				try {
					myclip.open(audioInputStream);									//打开音频流
				} catch (LineUnavailableException e) {	
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
										
					
			    myclip.start();														//开始播放 
			       													
			    isPlaying=true;														//正在播放标志置为true
			       
			    progress.setValue(0);												//进度条的值为0
			    progress.setMaximum((int)myclip.getMicrosecondLength());			//进度条最大值为歌曲的时间的长度
			    songtime.setText(time(myclip.getMicrosecondLength()));
			        													
			       
			    ProgressThread=new Thread(()->{ 									//进度条线程
			        	
			        	
			        	while(true) {												//设置进度条位置随歌曲的播放时间的 改变而改变
							
			        	progress.setValue((int)myclip.getMicrosecondPosition());
			        	if(myclip.getMicrosecondPosition()>=myclip.getMicrosecondLength()) {//如果歌曲的播放时间超过了它原本的时间，意味着正在进行单曲循环
			        																		//所以，这里判断是否是单曲循环，然后重置一下播放时间
							notesongtime.setText(time(myclip.getMicrosecondPosition()-myclip.getMicrosecondLength())); 
							}
			        	else{
			        		notesongtime.setText(time(myclip.getMicrosecondPosition()));
			        	}
			        	
			        	}
			        });
			        													
			    ProgressThread.start();
			    
			    //恢复原来的功能  
			    
				tips.setVisible(false);
				play_pause.setEnabled(true);
				next_btn.setEnabled(true);
				pre_btn.setEnabled(true);
				songsSelection.setEnabled(true);
				fileOpen.setEnabled(true);
				lock.unlock();
				
					}
		});
}
	/*******************************************************获取歌曲列表*************************************************************/	
	
	public void OpenFile() {			//打开文件，获取当前目录下所有的MP3文件
		String fileName="";
		FileDialog d=new FileDialog(this);			//文件选择对话框
		d.setVisible(true);							//设置选择文件窗口可见
		
		currentDirectory=d.getDirectory();
		if (d.getDirectory()==null) {
			return ;
		}
		File f=new File(d.getDirectory());
		File songFiles[]=f.listFiles();				//保存歌曲名称的列表
		songs.removeAllItems();				    	//移除歌曲下拉列表中的所有歌曲
		for (File t : songFiles) {					//筛选mp3或wav格式的文件
			if(t.toString().toLowerCase().endsWith(".mp3")||t.toString().toLowerCase().endsWith(".wav")) {
				MySongList.add(t.getName().toString().toLowerCase());
				songs.addItem(t.getName().toString().toLowerCase());//将筛选出的歌曲加入到列表中
			}
		}
		label.setText("Please select a song");
		tips01.setText("Double click to play the song");
		
	}
	
	/*******************************************************上一曲，下一曲*************************************************************/
	
	//播放下一首或者上一首歌曲，由参数nextOrPre决定。1为下一首，-1为上一首
	   public void nextOrPreSong(int nextOrPre ) {
		  
				if(isPlaying==false) {
					index=index+nextOrPre;				//********************************************************//
					if(index==songs.getItemCount()){	//			第一部分										  //
						index=0;						//			看见isPlaying了吗，这部分服务于在暂停的情况下进行换曲    		  //
						nextOrPreSong_01();				//			index，通过对index的加一减一来进行换曲			      //		
					}else if(index==-1) {				//********************************************************//
						index=songs.getItemCount()-1;
						nextOrPreSong_01();
					}else{
						nextOrPreSong_01();
					}
				}										//********************************************************//
				else if(isPlaying==true){				//			第二部分										  //
					index=index+nextOrPre;				//			这部分服务于在播放情况下进行下一曲						  //
					if(index==songs.getItemCount()){	//********************************************************//
						index=0;
						nextOrPreSong_02();
					}else if(index==-1) {
						index=songs.getItemCount()-1;
						nextOrPreSong_02();
					}else {
						nextOrPreSong_02();
					}	
				}
	    }
	   /*******************************************************暂停时，上下曲功能*************************************************************/
	public void nextOrPreSong_01() {
		flag=true;
		songs.setSelectedIndex(index);//5-20
		fileName=currentDirectory+"\\"+songs.getItemAt(index).toString();
		label.setText(songs.getItemAt(index).toString());
		play_pause.setIcon(new ImageIcon(path+picturefile+"\\Play.png"));
	}
	
	//**********************************************************************************************************************************//
	//								 播放时换曲，和暂停时播放曲有什么区别呢？																			//
	//	   				 			1、暂停时换曲，歌曲已经暂停了	，歌的缓存还在																		//
	//			 		 			2、播放时换曲，歌没有暂停，这个时候换曲，意味着要重新加载下一首歌的缓存，那么上一首的缓存就要被清除，这就是区别							//
	//								   所以，在播放时换曲时，要用到myclip.stop();myclip.close();ProgressThread.stop();System.gc();来终止歌曲，清理缓存	//
	//******************************************************************************************************************************//
	/*******************************************************正在播放时，上下曲功能*************************************************************/
	public void nextOrPreSong_02() {
		songs.setSelectedIndex(index);//5-20
		if(myclip!=null) {
			myclip.stop();														//停止播放歌曲，并关闭资源
			myclip.close();
			ProgressThread.stop();												//进度条线程停止
			System.gc();
		}
		fileName=currentDirectory+"\\"+songs.getItemAt(index).toString();
		label.setText(songs.getItemAt(index).toString());
		open(fileName);
	}
	/*******************************************************播放时间的格式*************************************************************/
	//就是把long类型的数值转为00：00这样的格式，显然，在做这种操作的时候，StringBuffer比较好用
	public static String time(long s){
		String S;
		s=s/1000000;							//歌曲时间是以微妙来计算的，所以除去1000000
		long second=s%60;						//秒钟
		long m=s/60;							//分钟
		s=m*100+second;							
		S=String.format("%tL",s);	//不满3位，前面补0，因为歌曲在一开始的时候时间是0，为了满足0：00的格式，所以前面补点0
		
		StringBuffer a=new StringBuffer(S);
//		System.out.println(S);
		a.insert(a.length()-2,":");
		a.toString();
//		System.out.println((a));
		return a.toString();
	}
	/*********************************************************结束*************************************************************/
}

//注释：黄宜都，陈南忠





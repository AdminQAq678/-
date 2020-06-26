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
 * @author �����ҡ����˶����¼��ǡ���ӱӱ
 * UI������ƣ����˶�
 * ���Ź��ܡ�
 * ��һ����һ�ס�
 * ����ģʽ��
 * ����ļ���
 * �������֡�
 * ѡ�������
 * ����������
 * 
*/
@SuppressWarnings("serial")
public class musicplayer extends JFrame{
	/***************************************************************************************************************/
	/*																											   */ 
	/*								����ֻ�����Ա�����Ķ�����ѣ���Ź�һ�飬������ϸ��											   */ 
	/*																											   */
	/***************************************************************************************************************/
	private boolean flag=false;																			//������־
	private JLabel tips01;																				//��ʾ
	private final ReentrantLock lock=new ReentrantLock();												//��
	private ExecutorService pool=Executors.newCachedThreadPool();										//�̳߳�
	boolean SelectedChangeFlag=false;																	//ѡ���޸ı�־
	private String fileName="";																			//�ļ�����
	private Thread ProgressThread;																		//�������߳�
	private int modelSize=4;																			//ʵ��ֻ��3�ֲ���ģʽ��Ϊ��ȡ�����Ʒ�Χ��1~3
	private int index=0;																				//���ڲ��ŵĸ������
	private String currentDirectory="";																	//��ǰĿ¼
	private boolean hasStop=true;																		//��������ֹͣ��־
	private boolean PlayForFirstTime=true;																//�Ƿ��һ�β��Ÿ�����־
	private ArrayList<String> MySongList=new ArrayList();												//�����б�
	private AudioInputStream audioInputStream ;															//��Ƶ������
	private AudioFormat audioformat;																	//��Ƶ��ʽ��
	private Clip myclip;																				//��Ƶ��������
	private JButton play_pause ;																		//����_��ͣ��ť
	private JButton pre_btn;																			//��һ��
	private JButton next_btn;																			//��һ��
	private JButton changeModel;																		//�޸Ĳ���ģʽ��ť
	boolean isPlaying=false;																			//�Ƿ����ڲ��Ÿ���
	private JProgressBar progress;																		//������
	private JLabel tips;																				//������ʾ
	@SuppressWarnings("rawtypes")																		//ѹ�ƾ���
	private JComboBox songs;																			//�����б��
	private JMenuBar jmunubar;																			//�˵���											
	private JMenuItem songsSelection;																	//������ѡ���
	private JMenuItem fileOpen;																			//�˵������ļ�)
	private int PlayState=1;																			//ѭ������
	private String path=System.getProperty("user.dir");													//��ȡ�û���ǰ�ļ���·��
	private JLabel label;																				//����
	private JLabel songtime;																			//����ʱ��
	private JLabel notesongtime;																		//����ʱ�����
	private String picturefile="\\picture";
	/***************************************************************************************************************/
	/*																											   
	 *								                                         ������ͼ�ν�������	
	 *
	 *
	 * 							֪ʶ�㣺1������ı�����һ��label,ͨ������ͼƬ������ԭ����Ĭ�ϱ��������Ϊ��*.setIcon(ͼƬ·��);
	 * 								 2��ͨ�������پ۽������Ϊ��*.setFocusPainted(true/false);
	 * 						       		          ���ر߿����Ϊ��*.setBorderPainted(true/false);
	 * 									          �Զ������壬���Ϊ��*.setFont(���壬��״����С);
	 * 									          ǰ����ɫ��������ɫ�������Ϊ��*.setForeground(��ɫ);				
	 * 								         �����۳���Ľ���
	 *											
	 *																											   
	/***************************************************************************************************************/
	@SuppressWarnings({ "deprecation", "rawtypes" })
	public musicplayer() throws UnsupportedAudioFileException, IOException {
		
		setForeground(new Color(255, 190, 3));															//����ǰ����ɫΪ��ɫ
		setIconImage(Toolkit.getDefaultToolkit().getImage(path+picturefile+"\\tubiao.png"));						//���ô���ͼ��
		setBackground(new Color(255, 190, 3));															//���ñ�����ɫ
		getContentPane().setBackground(new Color(255, 190, 3));											//���������ɫ
		
		jmunubar=new JMenuBar();
		jmunubar.setBorderPainted(false);																//�����Ʊ߿�
		jmunubar.setForeground(new Color(255, 190, 3));													//����ǰ����ɫ
		jmunubar.setBackground(new Color(255, 190, 3));													//���ñ�����ɫ
		this.setJMenuBar(jmunubar);
		
		songsSelection=new JMenuItem("Select song");													//Ϊ�˵�����ӡ�ѡ��������˵���
		songsSelection.setFont(new Font("Segoe UI Black", Font.ITALIC, 25));							//��������
		songsSelection.setBackground(new Color(255, 190, 3));											//���ñ�����ɫ
		songsSelection.setForeground(Color.WHITE);														//����ǰ����ɫ
		songsSelection.setBorderPainted(false);															//�����Ʊ߿�
		
	/*********************************************�������ƣ�����ע��****************************************************/
		
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
		fileMenu.setIcon(new ImageIcon(path+picturefile+"\\doc1.png"));												//ʹ��ͼ�깦��
		fileMenu.setBackground(new Color(255, 190, 3));
		fileMenu.add(fileOpen);
		fileMenu.add(songsSelection);
		
		
		tips = new JLabel("Please wait a few seconds");
		tips.setForeground(Color.WHITE);
		tips.setFont(new Font("Segoe UI Black", Font.ITALIC, 25));
		jmunubar.add(tips);
		tips.setVisible(false);
		
		songs=new JComboBox() ;
		songs.setFont(new Font("΢���ź�", Font.BOLD, 12));												//�����б�
		jmunubar.add(songs);
		songs.setVisible(false);																		//������������ظ���ѡ��������
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
		label.setFont(new Font("΢���ź�", Font.ITALIC, 18));
		label.setBounds(72, 34, 217, 21);
		panel.add(label);
		
		JLabel background = new JLabel("");
		background.setLabelFor(background);
		background.setIcon(new ImageIcon(path+picturefile+"\\back.gif"));
		background.setBounds(0, 26, 383, 379);
		getContentPane().add(background);		
		
		songtime = new JLabel("New label");
		songtime.setFont(new Font("΢���ź�", Font.ITALIC, 13));
		songtime.setForeground(Color.WHITE);
		songtime.setBackground(new Color(255, 190, 3));
		songtime.setBounds(303, 7, 50, 21);
		panel.add(songtime);
		
		notesongtime = new JLabel("New label");
		notesongtime.setBackground(new Color(255, 190, 3));
		notesongtime.setFont(new Font("΢���ź�", Font.ITALIC, 13));
		notesongtime.setForeground(Color.WHITE);
		notesongtime.setBounds(27, 7, 44, 21);
		panel.add(notesongtime);
		songtime.setText(time(0)); 
		notesongtime.setText(time(0));
		
		tips01 = new JLabel("");
		tips01.setFont(new Font("΢���ź�", Font.ITALIC, 16));
		tips01.setBackground(new Color(255, 190, 3));
		tips01.setForeground(Color.WHITE);
		tips01.setBounds(122, 0, 247, 21);
		getContentPane().add(tips01);
		
			
		/***************************************************************************************************************/
		/*																											   */ 
		/*											         �����Ǹ����������											           */ 
		/*																											   */
		/***************************************************************************************************************/
		
		
		/*************************************************����������*******************************************************/
		songsSelection.addActionListener((e)->{															//���ø���������ɼ�
			songs.setVisible(true);
			if(MySongList.isEmpty()==true) {
				label.setText("Please select a song");
			}
			
			tips01.setText("Double click to play");
			label.setVisible(true);
			tips01.setVisible(true);
		});
		
		
		/************************************************�����ļ������*****************************************************/
		fileOpen.addActionListener((e)->{	
			OpenFile();																					//���ļ��������������ʾ����ļ���ѡ������˵���
			songs.setVisible(true);
			label.setText("Please choose a song");
			tips01.setText("Double click to play");
			label.setVisible(true);
			tips01.setVisible(true);
			
		});
	
		/**********************************************˫��ѡ�裬�����Ÿ���***************************************************/
		songs.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount()==2) {																//˫������
					if(songs.getSelectedItem()==null) return ;											//û��ѡ�����
					if(myclip!=null&&myclip.isRunning()==true) {										//������ǵ�һ�β��ţ���ǰһ�׸������ڲ���,����ֹǰһ�׸�Ĳ���
						myclip.stop();					//***************************************************************************//
						myclip.close();					//֪ʶ�㣺  1��myclip��һ����Ƶ��������ͨ���������и�ʵĲ��ŵȲ���							 //
						ProgressThread.stop();			//	    2��myclip.stop,��˼����ͣ�����Ĳ��ţ���ͣ���������������������������෴����myclip.start //
														//		3��myclip.close,��˼����ֹ�����Ĳ��ţ�ͬʱ�أ��ͷ�ռ�ݵ�ϵͳ��Դ��Ҳ����˵�����Ļ���ᱻ���        //
														//		4��ProgressThread�ǽ��������̣߳���Ȼ�趼�����ˣ���ô��Ӧ�Ľ����߳�ҲҪ�ص���ע�������.stop //
														//        ������ͣ����˼����������̱߳���ֹ��                     									 //
					}									//***************************************************************************//		
					
					index=songs.getSelectedIndex();														//����ѡ��ĸ���
					fileName=currentDirectory+"\\"+songs.getSelectedItem().toString();					//ƴ���ļ�·��
					isPlaying=true;																		//ʱ�����ڲ��Ÿ����ı�־
					open(fileName);																		//���Ÿ���
					play_pause.setIcon(new ImageIcon(path+picturefile+"\\Play.png"));								//�ı䲥�Ű�����ͼ��
					songs.setVisible(false);															//�����б���ʧ����ԭ����Ľ���
													   //***************************************************************************//
													   //֪ʶ�㣺  1��index�Ǹ����б������ֵ��ͨ���������и�����һ������һ���Ȳ���						    //
													   //	    2��isPlaying��Ϊ��־�������жϸ����Ƿ����ڲ��ţ���ͣ�Ͳ��ŵȹ��ܻ�ʹ�õ��� 				//
													   //***************************************************************************//	
					
					}}});

		/***************************************************����ģʽ*********************************************************/
		/**************************************************1��ѭ������********************************************************/
		/**************************************************2�ǵ���ѭ��********************************************************/
		/**************************************************3���������********************************************************/
		changeModel.addActionListener((e)->{
			PlayState=(PlayState+1)%modelSize;//����ģʽ�ı�
			if(PlayState==0) PlayState=1;//��1��ʼ
			if(PlayState==2) {//����ѭ��
				changeModel.setIcon(new ImageIcon(path+picturefile+"\\danquxunhuan.png"));
			}
			else if(PlayState==1){
				changeModel.setIcon(new ImageIcon(path+picturefile+"\\xunhuan.png"));
			}
			else if (PlayState==3){
				changeModel.setIcon(new ImageIcon(path+picturefile+"\\suiji.png"));
			}
													  //***************************************************************************//
			   										  //					ע������ֻ�Ǹ���������ͼ����û��ʵ�ʹ���						       //
			   										  //	   				ʵ�ʵĹ���������Ľ�����������ʵ��					               //
													  //					Ϊʲô���ѹ��ܷ������                                                                                                                     //
													  //					ԭ������Ϊ���������������ǳ����Զ����еģ�������Ϊ��               				   //
													  //					���Լ�����������ʵ������ģʽ�Ĺ���								   //
			   										  //***************************************************************************//	

		});
		
		/**************************************************�����������Ľ���*********************************************************/
		
		progress.addChangeListener(new ChangeListener () {
			@SuppressWarnings("static-access")
			@Override
			public void stateChanged(ChangeEvent e) {
				if(progress.getValue()==progress.getMaximum()) {								//������ϣ��л���һ�ף����ֻ��һ����ѭ�������ݲ���ģʽѡ��
					progress.setValue(0);														//���ý�������ǰλ��Ϊ0
					if(PlayState==2) {															//����ѭ��
						myclip.loop(myclip.LOOP_CONTINUOUSLY);									//���õ�ǰ������ѭ������Ϊ���޴Σ�������ѭ��
					}else if(PlayState==1) {													//ѭ���б�
						index=Math.abs(index+1)%songs.getItemCount();//*******************************************************************************//
					    songs.setSelectedIndex(index);				 //֪ʶ�㣺1��PlayState��ֵ�ǲ���ģʽ�Ĺؼ���PlayState��1��2��3����Ӧ�š��б�ѭ��������ѭ�������ѭ������������   //
					    myclip.stop();								 //		2������ѭ����myclip�Դ���ѭ��������ʵ�֣����Ϊ��myclip.loop(myclip.LOOP_CONTINUOUSLY)  //	
					    myclip.close();								 //     3�� System.gc()������һ����Щû�б�ʹ�õĶ�����ռ�õ��ڴ棬���ǻ�����������ʵ��������Ҳû��ϵ��֪���ͺ�     //
					    System.gc();								 //*******************************************************************************//		
						fileName=currentDirectory+"\\"+songs.getItemAt(index).toString();
						open(fileName);
						ProgressThread.stop();
					}else if (PlayState==3){													//�������
						myclip.stop();															//ֹͣ��ǰ���ŵĸ������ͷ���Դ
						myclip.close();
						Random r=new Random();													//�����
						index=r.nextInt(songs.getItemCount());									//��ȡ[0��songs.getItemCount())��Χ�������������0��������Ŀ��Χ�ڵĵ������
						songs.setSelectedIndex(index);
						fileName=currentDirectory+"\\"+songs.getItemAt(index).toString();
						
						open(fileName);//
						ProgressThread.stop();
					}else if(PlayState==4){														//���Ž�����ֹͣ���д����ӣ�
					}
					return ;
				}
				
			}
			
		});
		
		/***************************************************���Ǽ������������¼�Դ�����*******************************************************/
		/*****************************************************����������оͲ�����********************************************************/
		progress.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(myclip==null) return ;
				float tem=((float) e.getX()/(float) progress.getWidth());			//e.getX()Ϊ��ǰ�������ڽ������ĺ������꣬progress.getWidth()Ϊ�������Ŀ��
																					//temΪ��ǰ�����������x��ԭ���ֵռ�������İٷֱ�(С����ʾ)
				progress.setValue((int) (progress.getMaximum()*tem));			    //���ý�������ֵ
				myclip.setMicrosecondPosition( (long) (progress.getMaximum()*tem));	//��tem���ڽ����������ֵ��Ϊ��ǰ�������λ�ã�����΢��Ĳ���λ��
				//**************************************************************************************************//
				//			֪ʶ�㣺	 1��e.getX()����˼������ڽ�������λ��						   							//
				//	   				 2��progress.getWidth()����˼�ǽ��������ܳ���				  							//
				//					 3��tem=e.getX()/progress.getWidth()�� ����λ��ռ�������İٷֱ�               						//
				//					 4�� progress.getMaximum(), ���������ֵ��ע��ร�ÿ�׸��ʱ�䶼�ǲ�ͬ�ģ����Խ����������ֵҲ�ǲ�ͬ�� 	//
				//					 5��progress.getMaximum()*tem����Ϊÿ�׸��ʱ�䶼�ǲ�һ���ģ������ðٷֱ�ȥ���Ʋ��ŵ�				//
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
		
		/*******************************************************��һ������һ��***********************************************************/
		next_btn.addActionListener((e)->nextOrPreSong(1));														//��һ�׸���
		pre_btn.addActionListener((e)->nextOrPreSong(-1));														//��һ�׸���
		//**************************************************************************************************//
		//			֪ʶ�㣺	 1��ֻ�����д��룬�������ν��Lambda���ʽд�����������ߴ��ϣ���ʵûʲô�ã����Ĺ��ܴ��뻹��Ҫ�Լ�д			//
		//	   				 2��nextOrPreSong()���������������ĺ������ڣ�����һ�� ��װ�ķ�����д������棬1Ϊ��һ����-1Ϊ��һ��		//
		//					 3����nextOrPreSong()���棬��ͨ���Ӽ�������ѡ�񲥷����׸裬+1����һ���-1����һ����             			//
		//**************************************************************************************************//
		
		/*******************************************************���ź���ͣ*************************************************************/
			//*******************************************�ϲ���*******************************************************//
			//			֪ʶ�㣺	 1���ϲ�����Ҫ�Ƿ����ڵ�һ�δ򿪲�������ʱ�򣬻����������������ʱ�������ֱ�Ӳ��Ÿ���						 	//
			//	   				 2���ⲿ�ִ����Ӧ�������433-447�д��룬�����������ļ���Ȼ��㲥�ż����в���								//
			//******************************************************************************************************//
		play_pause.addActionListener((e)->{
			if(songs.getItemCount()!=0&&myclip==null) {															//�������ļ�������û�в��Ź�����
				index=songs.getSelectedIndex();																	//ѡ��ǰѡ��ĸ���
				fileName=currentDirectory+"\\"+songs.getItemAt(index).toString();
				label.setText(fileName);																		//������ʾ
				open(fileName);																					//���Ÿ���
			}			
			//*******************************************�в���*****************************************************//
			//			֪ʶ�㣺	 1���в��ִ��룬����ʵ�������ϵ���ͣ�Ͳ��ţ���Ҫ�������Ѿ�������ĸ�����Ҳ�������ڲ��ŵĸ�		  			  //
			//	   				 2��ͨ��myclip!=null���ж��Ƿ��л��棬�������˼���е�										  //
			//					 3��ͨ��isPlaying���ж��Ƿ����ڲ���             			                                          //
			//			         4 ��ͨ��flag���ж����Ƿ��ڡ���ͣ����ʱ����ˡ���һ�������ߡ���һ�����Ĺ��ܣ��������ˣ���ôִ�е��ǵ������ִ���             	  //
			//****************************************************************************************************//
			if(isPlaying==false&&myclip!=null&&flag==false) {													//�����ͣ�����Ÿ���
				play_pause.setIcon(new ImageIcon(path+picturefile+"\\Play.png"));
				
			    /*******ע������*********/
				/**/myclip.start();/**/
				/*********************/
				
				songs.setVisible(false);
				isPlaying=true;	
			}else if(isPlaying==true&&myclip!=null){															//������ڲ��ţ�����ͣ
				play_pause.setIcon(new ImageIcon(path+picturefile+"\\Suspend.png"));
				
				/*********ע������*******/
				/**/myclip.stop(); /**/
				/*********************/
				
				songs.setVisible(true);																			//������ͣ��ʾѡ��
				isPlaying=false;
			}
			//*******************************************�²���*******************************************************//
			//			֪ʶ�㣺	 1���²�����Ҫ�Ƿ�����������ͣ��ʱ�򣬵�����һ��������һ���Ĺ��ܣ�ͨ��flag���ж�								//
			//	   				 2����Ȼ�Ѿ����˻����Ĺ��ܣ������ٴβ��ŵ�ʱ��ǰһ�׸���Ҫ��ֹ��������Ҫ��myclip������һЩ������stop��close   	//
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
		
		/*******************************************************��һ���������*************************************************************/
		//******************************************************************************************************//
		//			֪ʶ�㣺	 1�������ʼ���е�ʱ��ͨ������¼������Դ��ĸ�													//
		//	   				 2��   File songFiles[]=f.listFiles();ͨ����һ�д��룬���Եõ�path�������е��ļ���					//
		//			 		 3��Ȼ������һ��FORѭ��һ��һ��ȥ�ж�ʱ��Ϊmp3����wav�ļ�							  				//
		//			 		�ʣ� �ǿ��Բ��Ų���MP3����WAV���ļ���							  								//
		//			 		�𣺵�Ȼ�����ԣ����ڲ������Ĺ���ֻ�ܽ���MP3��WAV�ļ�							  					//
		//		 			4��t.toString().toLowerCase().endsWith(".mp3")						  				//
		//			                                ����תΪ�ַ�	           �����ַ�תΪСд		�жϺ�׺			  									//
		//******************************************************************************************************//
		File f=new File(path);
		File songFiles[]=f.listFiles();	//����������Ƶ��б�
		for (File t : songFiles) {
			if(t.toString().toLowerCase().endsWith(".mp3")||t.toString().toLowerCase().endsWith(".wav")) {		//ɸѡmp3��wav��ʽ���ļ�
				MySongList.add(t.getName().toString().toLowerCase());
				songs.addItem(t.getName().toString().toLowerCase());											//��ɸѡ���ĸ������뵽�б���
			}
		}
		currentDirectory=path;
		fileName=currentDirectory+"\\"+songs.getItemAt(index).toString();
		//System.out.println(currentDirectory+"  "+fileName);
		label.setText(songs.getItemAt(index).toString());
		/*******************************************************�������*************************************************************/
		
		this.setResizable(false);								//�߿��ܱ��ı�
		this.setSize(390,652);
		this.setLocationRelativeTo(null);						//�������·������ô��������ָ�������λ��
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
	}
	
	/***************************************************************************************************************/
	/*																											   */ 
	/*											         ������������												           */ 
	/*																											   */
	/***************************************************************************************************************/
	
	
	public static void main(String[] args) throws UnsupportedAudioFileException, IOException {
		new musicplayer();
	}
	
	
	/***************************************************************************************************************/
	/*																											   */ 
	/*											         �����װ���Ǹ��ֹ��ܺ���							 					   */ 
	/*																											   */
	/***************************************************************************************************************/
	
	/*******************************************************���ֲ��ź���*************************************************************/
	
	public void open(String fileName){													//�������ֺ���
		pool.execute(new Runnable() {													//���Ÿ����߳�
			@Override				//*************************//
			public void run() {		//ע�⣬������һ���̳߳��߳�		   //
				lock.lock();		//������					   //
									//Ϊʲô�õ����أ��������棬ûʲô��˼   //
									//*************************//
				label.setText(songs.getItemAt(index).toString());
				songs.setVisible(false);												//���ø����б��ɼ�
				tips.setVisible(true);
				play_pause.setEnabled(false);			//********************************//
				next_btn.setEnabled(false);				//�������ƻ���ʱһЩ�������Ĺ��ܣ�����һЩ��Ϊ�쳣//
				pre_btn.setEnabled(false);				//********************************//
				songsSelection.setEnabled(false);
				tips01.setVisible(false);
				fileOpen.setEnabled(false);
							
				if(MySongList.size()==0) //�����б�Ϊ�� 
						return;		

				File f = new File(fileName);
				
				try {
					audioInputStream=AudioSystem.getAudioInputStream(f);		//�����Ƶ������			
				} catch (UnsupportedAudioFileException e) {						//��֧�ֵ����ָ�ʽ
					return ;					
				} catch (IOException e) {
					e.printStackTrace();
				}
																		
				audioformat=audioInputStream.getFormat();						//�����Ƶ��ʽ
					
			    if (audioformat.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {// ת��mp3�ļ����룬�жϱ����ʽ�Ƿ�ΪPCM��ʽ�����ǵĻ���Ҫת��
			    	//******************************************************************************************************************************//
					//			֪ʶ�㣺	 1��AudioFormat.Encoding.PCM_SIGNED��ָ���з��ŵ����� PCM ���ݡ�														//
					//	   				 2��  audioFormat.getSampleRate()�����β�����																		//
					//			 		 3��ΪʲôaudioFormat.getChannels(), audioFormat.getChannels()����һ�κ�Ҫ��һ��audioFormat.getChannels()*2			//
			    	//						��Ϊÿ����ÿ֡�ֽ�����2�ֽڣ��ټ���˫������������������2��*�ֽ�����2�� = ÿ֡�����ֽ���												//
			    	//					 4��16��16λ˫�ֽڲɼ�audioFormat.getChannels()																	//
					//******************************************************************************************************************************//
			        audioformat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
			        audioformat.getSampleRate(),16,audioformat.getChannels(),
			        audioformat.getChannels()*2,audioformat.getSampleRate(),false);

			        audioInputStream = AudioSystem.getAudioInputStream(audioformat,audioInputStream);//��ȡת����ʽ�����Ƶ������
			     }
			        
			        													
			     try {
					myclip=AudioSystem.getClip();									// ������豸����ȡ��Ƶ�ļ������Ŷ���				
				} catch (LineUnavailableException e) {
					e.printStackTrace();
				}		
			        
				try {
					myclip.open(audioInputStream);									//����Ƶ��
				} catch (LineUnavailableException e) {	
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
										
					
			    myclip.start();														//��ʼ���� 
			       													
			    isPlaying=true;														//���ڲ��ű�־��Ϊtrue
			       
			    progress.setValue(0);												//��������ֵΪ0
			    progress.setMaximum((int)myclip.getMicrosecondLength());			//���������ֵΪ������ʱ��ĳ���
			    songtime.setText(time(myclip.getMicrosecondLength()));
			        													
			       
			    ProgressThread=new Thread(()->{ 									//�������߳�
			        	
			        	
			        	while(true) {												//���ý�����λ��������Ĳ���ʱ��� �ı���ı�
							
			        	progress.setValue((int)myclip.getMicrosecondPosition());
			        	if(myclip.getMicrosecondPosition()>=myclip.getMicrosecondLength()) {//��������Ĳ���ʱ�䳬������ԭ����ʱ�䣬��ζ�����ڽ��е���ѭ��
			        																		//���ԣ������ж��Ƿ��ǵ���ѭ����Ȼ������һ�²���ʱ��
							notesongtime.setText(time(myclip.getMicrosecondPosition()-myclip.getMicrosecondLength())); 
							}
			        	else{
			        		notesongtime.setText(time(myclip.getMicrosecondPosition()));
			        	}
			        	
			        	}
			        });
			        													
			    ProgressThread.start();
			    
			    //�ָ�ԭ���Ĺ���  
			    
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
	/*******************************************************��ȡ�����б�*************************************************************/	
	
	public void OpenFile() {			//���ļ�����ȡ��ǰĿ¼�����е�MP3�ļ�
		String fileName="";
		FileDialog d=new FileDialog(this);			//�ļ�ѡ��Ի���
		d.setVisible(true);							//����ѡ���ļ����ڿɼ�
		
		currentDirectory=d.getDirectory();
		if (d.getDirectory()==null) {
			return ;
		}
		File f=new File(d.getDirectory());
		File songFiles[]=f.listFiles();				//����������Ƶ��б�
		songs.removeAllItems();				    	//�Ƴ����������б��е����и���
		for (File t : songFiles) {					//ɸѡmp3��wav��ʽ���ļ�
			if(t.toString().toLowerCase().endsWith(".mp3")||t.toString().toLowerCase().endsWith(".wav")) {
				MySongList.add(t.getName().toString().toLowerCase());
				songs.addItem(t.getName().toString().toLowerCase());//��ɸѡ���ĸ������뵽�б���
			}
		}
		label.setText("Please select a song");
		tips01.setText("Double click to play the song");
		
	}
	
	/*******************************************************��һ������һ��*************************************************************/
	
	//������һ�׻�����һ�׸������ɲ���nextOrPre������1Ϊ��һ�ף�-1Ϊ��һ��
	   public void nextOrPreSong(int nextOrPre ) {
		  
				if(isPlaying==false) {
					index=index+nextOrPre;				//********************************************************//
					if(index==songs.getItemCount()){	//			��һ����										  //
						index=0;						//			����isPlaying�����ⲿ�ַ���������ͣ������½��л���    		  //
						nextOrPreSong_01();				//			index��ͨ����index�ļ�һ��һ�����л���			      //		
					}else if(index==-1) {				//********************************************************//
						index=songs.getItemCount()-1;
						nextOrPreSong_01();
					}else{
						nextOrPreSong_01();
					}
				}										//********************************************************//
				else if(isPlaying==true){				//			�ڶ�����										  //
					index=index+nextOrPre;				//			�ⲿ�ַ������ڲ�������½�����һ��						  //
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
	   /*******************************************************��ͣʱ������������*************************************************************/
	public void nextOrPreSong_01() {
		flag=true;
		songs.setSelectedIndex(index);//5-20
		fileName=currentDirectory+"\\"+songs.getItemAt(index).toString();
		label.setText(songs.getItemAt(index).toString());
		play_pause.setIcon(new ImageIcon(path+picturefile+"\\Play.png"));
	}
	
	//**********************************************************************************************************************************//
	//								 ����ʱ����������ͣʱ��������ʲô�����أ�																			//
	//	   				 			1����ͣʱ�����������Ѿ���ͣ��	����Ļ��滹��																		//
	//			 		 			2������ʱ��������û����ͣ�����ʱ��������ζ��Ҫ���¼�����һ�׸�Ļ��棬��ô��һ�׵Ļ����Ҫ����������������							//
	//								   ���ԣ��ڲ���ʱ����ʱ��Ҫ�õ�myclip.stop();myclip.close();ProgressThread.stop();System.gc();����ֹ������������	//
	//******************************************************************************************************************************//
	/*******************************************************���ڲ���ʱ������������*************************************************************/
	public void nextOrPreSong_02() {
		songs.setSelectedIndex(index);//5-20
		if(myclip!=null) {
			myclip.stop();														//ֹͣ���Ÿ��������ر���Դ
			myclip.close();
			ProgressThread.stop();												//�������߳�ֹͣ
			System.gc();
		}
		fileName=currentDirectory+"\\"+songs.getItemAt(index).toString();
		label.setText(songs.getItemAt(index).toString());
		open(fileName);
	}
	/*******************************************************����ʱ��ĸ�ʽ*************************************************************/
	//���ǰ�long���͵���ֵתΪ00��00�����ĸ�ʽ����Ȼ���������ֲ�����ʱ��StringBuffer�ȽϺ���
	public static String time(long s){
		String S;
		s=s/1000000;							//����ʱ������΢��������ģ����Գ�ȥ1000000
		long second=s%60;						//����
		long m=s/60;							//����
		s=m*100+second;							
		S=String.format("%tL",s);	//����3λ��ǰ�油0����Ϊ������һ��ʼ��ʱ��ʱ����0��Ϊ������0��00�ĸ�ʽ������ǰ�油��0
		
		StringBuffer a=new StringBuffer(S);
//		System.out.println(S);
		a.insert(a.length()-2,":");
		a.toString();
//		System.out.println((a));
		return a.toString();
	}
	/*********************************************************����*************************************************************/
}

//ע�ͣ����˶���������





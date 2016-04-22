package com.example.numberandmusic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.app.Activity;
import android.app.Service;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	View basicView;
	TextView viewQuestion;
	TextView viewAnswer;
	TextView viewRightNum;
	SeekBar timeBar;
	boolean isAnswerRight;
	int continueRightNumber=0;
	boolean submitAnswer=false;
	boolean isButtonEnable=true;
	int soundIndex=1;
	boolean appPause=false;
	
	int timeLimit=5000;;  

    SoundPool soundPool= new SoundPool(10,AudioManager.STREAM_MUSIC,0);
    HashMap<Integer, Integer> soundPoolMap = new HashMap<Integer, Integer>();   
    int hashMapCount=0;
    
    List<int[]> pianoList=new ArrayList();
    int[] backgroundColor={Color.rgb(251, 155, 104),Color.rgb(251, 218, 104),Color.rgb(211, 252, 103),
    				Color.rgb(122, 251, 104),Color.rgb(104, 251, 174),Color.rgb(103, 200, 252),
    				Color.rgb(102, 109, 253),Color.rgb(177, 102, 253),Color.rgb(253, 102, 234),Color.rgb(253, 102, 102)};

	Vibrator vib;
	
	Handler handler = new Handler();  
	Runnable newQuestion = new Runnable() {  
	        @Override  
	        public void run() {  
	        	MainActivity.this.newQuestion();
	      }  
	    };  
	    
	Runnable beginTimeCount = new Runnable() {  
			int i=4;
	        @Override  
	        public void run() {  
	        	i--;
	    		viewQuestion.setText(i+"");
	    		if (i>1)
	    			handler.postDelayed(beginTimeCount, 1000);  
	      }  
	    };  
	    
	Runnable limitTimeCount = new Runnable() {  
			int i=0;
	        @Override  
	        public void run() {  
	        	while (appPause)
	        	{
        			handler.postDelayed(newQuestion, 1000); 
        			return;
	        	}
	        	i++;
	        	timeBar.setProgress(i);
	        	if (submitAnswer) 
	        	{
	        		i=0;
	        		submitAnswer=false;
	        		isButtonEnable=false;
	        		if (continueRightNumber>0)
	        			handler.postDelayed(newQuestion, 500); 
	        		else 
	        		{
		                vib.vibrate(500);
	        			handler.postDelayed(newQuestion, 1000); 
	        		}
	        		return;
	        	}
	        	
	        	if (i<timeLimit/100)
	    			handler.postDelayed(limitTimeCount, 100);  
	        	else
	        	{
	        		MainActivity.this.checkClick(false);
	        		i=0;
	        		submitAnswer=false;
	        		isButtonEnable=false;
	                vib.vibrate(500);
	            	handler.postDelayed(newQuestion, 1000);
	        	}
	      }  
	    };
	    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        int[] musicSky={69,71,73,71,73,76,71,   64,69,68,69,73,68,   64,66,64,66,69,64,   73,73,71,66,66,71,   
        		69,71,73,71,73,76,71,   64,69,68,69,71,68,   63,64,69,66,73,71,73,75,75,76,73,   
        		73,71,69,71,68,69,   73,75,76,76,80,75,   68,73,71,73,76,76};
        int[] musicLittlestar={59,59,66,66,68,68,66,   64,64,63,63,61,61,59,   66,66,64,64,63,63,61,   
        		66,66,64,64,63,63,61,   59,59,66,66,68,68,66,   64,64,63,63,61,61,59};
       
        pianoList.add(musicLittlestar);
        
    	soundIndex=soundPool.load(this,R.raw.piano01,1);
        soundPoolMap.put(0, soundIndex); 

        basicView=(View) findViewById(R.id.basicView);//获取按钮资源
		basicView.setBackgroundColor(backgroundColor[new Random().nextInt(backgroundColor.length)]);
        
        viewQuestion=(TextView) findViewById(R.id.viewQuestion);//获取按钮资源 
        viewAnswer=(TextView) findViewById(R.id.viewAnswer);//获取按钮资源 
        viewRightNum=(TextView) findViewById(R.id.viewRightNum);//获取按钮资源 
        
        timeBar=(SeekBar) findViewById(R.id.timeBar);//获取按钮资源 
        timeBar.setMax(timeLimit/100);
        
        vib= ( Vibrator ) getApplication().getSystemService(Service.VIBRATOR_SERVICE); 
        
        Button button=(Button) findViewById(R.id.buttonYes);//获取按钮资源 
        button.setOnTouchListener(buttonTouchListener);
        button=(Button) findViewById(R.id.buttonNo);//获取按钮资源 
        button.setOnTouchListener(buttonTouchListener);

        
        this.gameBegin();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void wait(int time)
    {
    	try
    	{
    	Thread.currentThread().sleep(time);//毫秒 
    	}
    	catch(Exception e){}
    }
    
    public void gameBegin()
    {
    	
    	this.wait(1000);
    	
        handler.postDelayed(beginTimeCount, 1000);  

        handler.postDelayed(newQuestion, 4000);
    	//this.newQuestion();
    }
    
    public void newQuestion()
    {
    	isButtonEnable=true;
    			
        int a=new Random().nextInt(9) + 1;
        int b=new Random().nextInt(99) + 1;
        int answer=a*b;
        viewQuestion.setText(a+" X "+b);
        
        int num=answer+new Random().nextInt(2)*(new Random().nextInt(3)+1)*(new Random().nextInt(2)*9+1);
        viewAnswer.setText("="+num);
        
        if (answer==num)
        	isAnswerRight=true;
        else
        	isAnswerRight=false;
        
		handler.postDelayed(limitTimeCount, 0); 
    }
    
    public void click(View view)
    {
    	if (!isButtonEnable)
    		return;
    	
    	submitAnswer=true;
    	
    	if ((((Button)view).getId()==R.id.buttonYes)^(!isAnswerRight))
    		this.checkClick(true);
    	else
    		this.checkClick(false);
    }
    
    public void checkClick(boolean isRight)
    {
    	if (isRight)
    	{
    		continueRightNumber++;
    		this.playMusic();
    	}
    	else
    	{
    		continueRightNumber=0;
    		this.playMusic();
    	}
    	viewRightNum.setText(""+continueRightNumber);
		basicView.setBackgroundColor(backgroundColor[new Random().nextInt(backgroundColor.length)]);
    }

    public void playMusic()
    {
    	if (continueRightNumber==0)
    	{
        	soundIndex=soundPool.load(this,R.raw.piano01,1);
            soundPoolMap.put(continueRightNumber, soundIndex); 
        	this.wait(200);
        	soundPool.play(soundPoolMap.get(continueRightNumber) ,1, 1, 0, 0, 1);
        	return;
    	}
    	soundIndex=soundPool.load(this,R.raw.piano49-49+pianoList.get(0)[continueRightNumber-1],1);
        soundPoolMap.put(continueRightNumber, soundIndex); 
    	//handler.postDelayed(runnable3, 0);  
    	this.wait(200);
    	soundPool.play(soundPoolMap.get(continueRightNumber) ,2, 2, 0, 0, (float)0.5);
    }
    
    private OnTouchListener buttonTouchListener = new OnTouchListener() {
    	
    	public boolean onTouch(View view, MotionEvent event) {
    		// TODO Auto-generated method stub
    		Button button=((Button)view);
    		switch (event.getAction()) {
   
    		case MotionEvent.ACTION_DOWN:
    		{
    			if (button.getId()==R.id.buttonYes)
    				button.setBackgroundResource(R.raw.true2);
    			else
    				button.setBackgroundResource(R.raw.false2);
    			break;
    		}
    		case MotionEvent.ACTION_UP:
    		{
    			if (button.getId()==R.id.buttonYes)
    				button.setBackgroundResource(R.raw.true1);
    			else
    				button.setBackgroundResource(R.raw.false1);
    			break;
    		}
   
    		default:
    			break;
    		}
    		return false;
    	}
    };
    


    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){   
    		if((System.currentTimeMillis()-exitTime) > 2000){  
    			Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();                                
    			exitTime = System.currentTimeMillis();   
    		} 
    		else {
    			finish();
    			System.exit(0);
    		}
    		return true;   
    	}
    	return super.onKeyDown(keyCode, event);
    }
    
    @Override  
    protected void onStop(){  //onUserLeaveHint() 
    	appPause=true;
        super.onStop();  
    }  
    
    @Override 
    protected void onResume() {  
    	appPause=false;
        super.onResume();  
    }  

    @Override 
    protected void onRestart() {  
    	appPause=false;
        super.onRestart();  
    }  
}
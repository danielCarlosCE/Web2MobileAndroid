package br.ufc.web2mobile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class MainActivity extends Activity {

	private ImageView imagem;
	private TextView texto;
	private XMLReader xml;
	private AudioPlayer ap;
	private ViewFlipper flipper;
	private Boolean firstFlipperChild = true;

	private ImageButton btn_audio1;
	private ImageButton btn_audio2;
	private boolean mute = false;
	
	private float initialX = 0;
	private float initialY = 0;
	private float deltaX = 0;
	private float deltaY = 0;

	private int idTela = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		xml = new XMLReader();
		xml.read(getApplicationContext());

		ap = new AudioPlayer();

		flipper = (ViewFlipper) findViewById(R.id.flipper);
		imagem = new ImageView(this);
		texto = (TextView) findViewById(R.id.text1);

		File file = new File(xml.getImagens().get(idTela));
		if (file.exists()) {
			Bitmap bitmapImage = BitmapFactory.decodeFile(file
					.getAbsolutePath());
			imagem = (ImageView) findViewById(R.id.imageBackground1);
			imagem.setImageBitmap(bitmapImage);

		} else {
			// imagem = (ImageView) findViewById(R.id.imageView1);
		}
		
		ImageView i;
		Bitmap bm;
		try {
			bm = getBitmapFromAsset("dialog_box.png");
			i = (ImageView)findViewById(R.id.legendBox1);
			i.setImageBitmap(bm);
			i = (ImageView)findViewById(R.id.legendBox2);
			i.setImageBitmap(bm);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//botão de áudio da tela 1
		btn_audio1 = (ImageButton)findViewById(R.id.btnAudio1);
		btn_audio1.setBackgroundResource(R.drawable.btn_volume);
		btn_audio1.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				if(isMute()){
					btn_audio1.setBackgroundResource(R.drawable.btn_volume);				
					ap.unmute();
					setMute(false);
				}
				else{
					btn_audio1.setBackgroundResource(R.drawable.btn_mute);					
					ap.mute();
					setMute(true);
				}
				
			}
		});
		//botão de áudio da tela 2
		btn_audio2 = (ImageButton)findViewById(R.id.btnAudio2);
		btn_audio2.setBackgroundResource(R.drawable.btn_volume);
		btn_audio2.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				if(isMute()){
					btn_audio2.setBackgroundResource(R.drawable.btn_volume);				
					ap.unmute();
					setMute(false);
				}
				else{
					btn_audio2.setBackgroundResource(R.drawable.btn_mute);					
					ap.mute();
					setMute(true);
				}
						
			}
		});
		
		texto.setText(xml.getTextos().get(idTela));
		ap.start(xml.getAudios().get(idTela));

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// This prevents touchscreen events from flooding the main thread
		synchronized (event) {
			try {
				// Waits 16ms.
				event.wait(16);

				// when user touches the screen
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					// reset deltaX and deltaY
					deltaX = deltaY = 0;

					// get initial positions
					initialX = event.getRawX();
					initialY = event.getRawY();
				}

				// when screen is released
				if (event.getAction() == MotionEvent.ACTION_UP) {
					deltaX = event.getRawX() - initialX;
					deltaY = event.getRawY() - initialY;

					// swipped up
					if (deltaY > 0) {
						// make your object/character move right						
						proximaTela(getIdTela());
					} else if (deltaY < 0) {
						// make your object/character move left
						telaAnterior(getIdTela());
					}

					return true;
				}
			}

			catch (InterruptedException e) {
				return true;
			}
		}
		return false;
	}

	public void proximaTela(int id) {

		id++;
		
		if (id < xml.getNumTelas()) {
			firstFlipperChild = !firstFlipperChild;
			File file = new File(xml.getImagens().get(id));
			if (file.exists()) {
				Bitmap bitmapImage = BitmapFactory.decodeFile(file
						.getAbsolutePath());
				if(firstFlipperChild) {
					imagem = (ImageView) findViewById(R.id.imageBackground1);
					texto = (TextView) findViewById(R.id.text1);
				}	
				else {
					imagem = (ImageView) findViewById(R.id.imageBackground2);
					texto = (TextView) findViewById(R.id.text2);
				}
				imagem.setImageBitmap(bitmapImage);

			} else {
				// imagem = (ImageView)
				// findViewById(R.id.imageView1);
			}
			
			ap.stop();
			ap = new AudioPlayer();
			
			//verificar necessidade
			if(isMute()) {
				btn_audio1.setBackgroundResource(R.drawable.btn_mute);	
				btn_audio2.setBackgroundResource(R.drawable.btn_mute);	
				ap.mute();				
			}
			
			texto.setText(xml.getTextos().get(id));
			ap.start(xml.getAudios().get(id));

			setIdTela(id);
			
			//transição de tela
			flipper.setInAnimation(inFromRightAnimation());
			flipper.setOutAnimation(outToLeftAnimation());
			flipper.showNext();
		} else {
			Toast toast = Toast.makeText(getApplicationContext(),
					"Não há mais telas.", Toast.LENGTH_SHORT);
			toast.show();
		}
	}
	
	public void telaAnterior(int id) {

		id--;
		
		if (id >= 0) {
			firstFlipperChild = !firstFlipperChild;
			File file = new File(xml.getImagens().get(id));
			if (file.exists()) {
				Bitmap bitmapImage = BitmapFactory.decodeFile(file
						.getAbsolutePath());
				if(firstFlipperChild) {
					imagem = (ImageView) findViewById(R.id.imageBackground1);
					texto = (TextView) findViewById(R.id.text1);
				}	
				else {
					imagem = (ImageView) findViewById(R.id.imageBackground2);
					texto = (TextView) findViewById(R.id.text2);
				}
				imagem.setImageBitmap(bitmapImage);

			} else {
				// imagem = (ImageView)
				// findViewById(R.id.imageView1);
			}
			
			ap.stop();
			ap = new AudioPlayer();
			
			//verificar necessidade
			if(isMute()){
				btn_audio1.setBackgroundResource(R.drawable.btn_mute);
				btn_audio2.setBackgroundResource(R.drawable.btn_mute);
				ap.mute();
			}
			
			texto.setText(xml.getTextos().get(id));
			ap.start(xml.getAudios().get(id));

			setIdTela(id);
			
			//transição de tela
			flipper.setInAnimation(inFromLeftAnimation());
			flipper.setOutAnimation(outToRightAnimation());
			flipper.showNext();
		} else {
			Toast toast = Toast.makeText(getApplicationContext(),
					"Primeira tela.", Toast.LENGTH_SHORT);
			toast.show();
		}
	}
	
	private Bitmap getBitmapFromAsset(String strName) throws IOException
	{
	    AssetManager assetManager = getAssets();
	    InputStream istr = assetManager.open(strName);
	    Bitmap bitmap = BitmapFactory.decodeStream(istr);
	    return bitmap;
	 }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public int getIdTela() {
		return idTela;
	}

	public void setIdTela(int idTela) {
		this.idTela = idTela;
	}

	public boolean isMute() {
		return mute;
	}

	public void setMute(boolean mute) {
		this.mute = mute;
	}
	
	private Animation inFromRightAnimation() {

		Animation inFromRight = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT,  +1.0f, Animation.RELATIVE_TO_PARENT,  0.0f,
				Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f
		);
		inFromRight.setDuration(500);
		inFromRight.setInterpolator(new AccelerateInterpolator());
		return inFromRight;
	}
	
	private Animation outToLeftAnimation() {
		Animation outtoLeft = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,  -1.0f,
				Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f
		);
		outtoLeft.setDuration(500);
		outtoLeft.setInterpolator(new AccelerateInterpolator());
		return outtoLeft;
		}
	
	private Animation inFromLeftAnimation() {
		Animation inFromLeft = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT,  -1.0f, Animation.RELATIVE_TO_PARENT,  0.0f,
				Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f
		);
		inFromLeft.setDuration(500);
		inFromLeft.setInterpolator(new AccelerateInterpolator());
		return inFromLeft;
	}
	
	private Animation outToRightAnimation() {
		Animation outtoRight = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,  +1.0f,
				Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f
		);
		outtoRight.setDuration(500);
		outtoRight.setInterpolator(new AccelerateInterpolator());
		return outtoRight;
	}
}
package jp.anlab.kanna2;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Bitmap.CompressFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.text.Layout;
import android.text.format.Time;
import android.util.Log;

public class KannaActivity extends Activity implements SensorEventListener {

	private float y_sensor;
	private float x_sensor;
	private float goukei;
	private float goukei_x;
	private String str_rank;
	private float y_SensorValue;
	private float x_kosei;
	private float x_button;
	private float x_kosei2;
	private int maxInt = 0;
	private int minInt = 0;
	private int avarageInt;
	private int divInt;
	private boolean divflag;
	private boolean startFlag;
	private SharedPreferences modelpref;
	private long start;
	private long stop;
	private long alltime;
	private File file;
	private Runnable looper;
	private Time time;
	private String date;
	private String date2;
	private int toggle;
	private int point;
	private int[] currentlyInt;
	private int timesInt;
	// 繰り返し間隔（ミリ秒）
	private int REPEAT_INTERVAL;
	// 繰り返し処理を続けるかどうかのフラグ
	private boolean isRepeat;
	private Thread thread;
	private int wide;
	private int hight;
	private Bitmap bmp = null;
	private Bitmap bmp_model = null;
	private Canvas bmpCanvas;
	private TextView textV;
	private TextView countTextV;
	private Button countBtn;
	private LinearLayout layout;
	private float mYOffset;
	private MediaPlayer mp = null;
	Handler mHandler = new Handler();
	Handler dHandler = new Handler();
	SensorCalibration SC = new SensorCalibration();
	String modeldata_str;
	String modelName;
	String logsttr;
	int jikan;
	private Thread threads;
	String result;
	boolean finishflg = false;
	private SensorManager sensorManager;
	float gyro_value[];

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// SensorManagerインスタンスを取得
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		gyro_value=new float[4];

		modelpref = getSharedPreferences("modelpref", MODE_WORLD_READABLE
				| MODE_WORLD_WRITEABLE);

		modeldata_str = modelpref.getString("modelint", "");
		modelName = modelpref.getString("name", "");
		logsttr = modelpref.getString(modelName + "maxInt", "");

		// ここから，画面レイアウトについて/////////////////////////////////////////
		// 画面全体にレイアウト
		layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setBackgroundColor(Color.WHITE);
		setContentView(layout);

		// 画面上部のレイアウト
		LinearLayout upLayout = new LinearLayout(this);
		upLayout.setOrientation(LinearLayout.VERTICAL);
		upLayout.setBackgroundResource(R.drawable.start);

		textV = new TextView(this);
		textV.setText("ここに結果が表示されます");

		upLayout.addView(textV, new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));

		// 親レイアウトに上部レイアウトを追加
		layout.addView(upLayout, new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT, 400));

		// 親レイアウトにSampleViewクラスを追加
		layout.addView(new SampleView(this));
		// ここまで，画面レイアウトについて/////////////////////////////////////////

		// カウントダウンレイアウト///////////////////////////////////////////////
		LinearLayout countLayout = new LinearLayout(this);
		countLayout.setOrientation(LinearLayout.VERTICAL);
		countLayout.setBackgroundResource(R.drawable.start);
		setContentView(countLayout);

		countTextV = new TextView(this);
		countTextV.setText("↓");
		countTextV.setTextColor(Color.WHITE);
		countTextV.setTextSize(250);
		countTextV.setGravity(Gravity.CENTER | Gravity.CENTER);
		countBtn = new Button(this);
		countBtn.setBackgroundResource(R.drawable.go);

		countLayout.addView(countBtn, new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT, 250));

		countLayout.addView(countTextV, new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT));
		// カウントダウンレイアウト////////////////////////////////////////////////

		countBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mp = MediaPlayer.create(KannaActivity.this, R.raw.button);
				try {
					mp.prepare();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				mp.start();

				mp = MediaPlayer.create(KannaActivity.this, R.raw.count);
				try {
					mp.prepare();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				mp.start();
				CountDown();
				countBtn.setVisibility(View.GONE);
				countTextV.setTextSize(400);
			}
		});
	}

	int intCount = 4;
	Runnable countRunnable;

	public void CountDown() {
		final int REPEAT_INTERVAL = 1000;
		final Handler handler = new Handler();
		countRunnable = new Runnable() {
			public void run() {
				intCount--;
				// 2.繰り返し処理
				countTextV.setText(String.valueOf(intCount));
				Log.d("i", String.valueOf(intCount));
				if (intCount == -1) {
					handler.removeCallbacks(countRunnable);
					setContentView(layout);
					onBtn();
				} else {
					// 3.次回処理をセット
					handler.postDelayed(this, REPEAT_INTERVAL);
				}
			}
		};
		// 1.初回実行
		handler.postDelayed(countRunnable, REPEAT_INTERVAL);
	}

	public void onBtn() {
		currentlyInt = new int[10];
		mHandler = new Handler();
		startFlag = true;

		// 定期処理をオフに
		isRepeat = false;
		REPEAT_INTERVAL = 100;
		time = new Time("Asia/Tokyo");

		// 定期処理
		looper = new Runnable() {
			public void run() {
				// 2.isRepeatがtrueなら処理を繰り返す
				while (isRepeat) {
					try {
						Thread.sleep(REPEAT_INTERVAL);
					} catch (InterruptedException e) {
					}
					// 3.繰り返し処理
					// UIの操作はHandlerの中に記述
					mHandler.post(new Runnable() {
						public void run() {
							doSomething();
						}
					});
				}
			}
		};

		isRepeat = true;
		goukei = 0;
		goukei_x = 0;
		start = 0;
		stop = 0;
		maxInt = 0;
		minInt = 0;
		timesInt = 0;

		start = System.currentTimeMillis();
		time.setToNow();
		String username = modelpref.getString("username", "");
		date = username + "_" + (time.month + 1) + "_" + time.monthDay + " "
				+ time.hour + ":" + time.minute + ":" + time.second;
		String fileName = "/sdcard/" + getPackageName() + "/" + date + ".csv";
		file = new File(fileName);
		file.getParentFile().mkdir();

		// 1.スレッド起動
		thread = new Thread(looper);
		thread.start();
		textV.setTextSize(75);
		textV.setTextColor(Color.WHITE);
		textV.setText("計測中...");
		textV.setPadding(0, 100, 0, 0);
		textV.setGravity(Gravity.CENTER);

		float y_btn = y_sensor * 10;
		y_btn = Math.round(y_btn);
		y_btn = y_btn / 10;
		SC.setBtnSensorValue(y_btn);

		// x軸
		x_kosei = x_sensor * 10;
		x_kosei = Math.round(x_kosei);
		x_kosei = x_kosei / 10;
		x_button = x_kosei;
	}

	// 繰り返しの動作
	private void doSomething() {
		jikan++;
		gyro_value[3] = gyro_value[3]+ Math.abs(gyro_value[0]) + Math.abs(gyro_value[1])
				+ Math.abs(gyro_value[2]);
		int finishjikan = jikan / 10;
		y_SensorValue = SC.setNowSensorValue(y_sensor);

		// x軸
		x_kosei = x_sensor * 10;
		x_kosei = Math.round(x_kosei);
		x_kosei = x_kosei / 10;

		if (startFlag == true) {
			timesInt++;
			Log.d("timesInt", String.valueOf(timesInt));

			for (int i = 9; i >= 1; i--) {
				currentlyInt[i] = currentlyInt[i - 1];
			}
			currentlyInt[0] = (int) y_SensorValue;
			Log.d("0", String.valueOf(currentlyInt[0]));
			Log.d("9", String.valueOf(currentlyInt[9]));

			avarageInt = (currentlyInt[0] + currentlyInt[1] + currentlyInt[2]
					+ currentlyInt[3] + currentlyInt[4] + currentlyInt[5]
					+ currentlyInt[6] + currentlyInt[7] + currentlyInt[8] + currentlyInt[9]) / 10;
			Log.d("avarageInt", String.valueOf(avarageInt));

			int[] deviationInt = new int[10];
			for (int i = 0; i <= 9; i++) {
				deviationInt[i] = Math.abs(currentlyInt[i] - avarageInt);
			}
			divInt = (deviationInt[0] + deviationInt[1] + deviationInt[2]
					+ deviationInt[3] + deviationInt[4] + deviationInt[5]
					+ deviationInt[6] + deviationInt[7] + deviationInt[8] + deviationInt[9]) / 10;
			Log.d("divInt", String.valueOf(divInt));

			if (divflag == false && divInt > 0) {
				divflag = true;
			}

			if (divflag == true && divInt == 0 || finishjikan == 7) {
				divflag = false;
				finishflg = true;
				// ここにとまった時の処理を書く
				dostop();
			}

			if (maxInt < y_SensorValue) {
				maxInt = (int) y_SensorValue;
			}

			if (minInt > y_SensorValue) {
				minInt = (int) y_SensorValue;
			}

			goukei = goukei + Math.abs(y_SensorValue);

			x_kosei2 = ((x_kosei) * 10 - (x_button) * 10) / 10;
			goukei_x = goukei_x + Math.abs(x_kosei2);

			try {
				String write_int;
				time.setToNow();
				date2 = time.hour + ":" + time.minute + ":" + time.second + ":";
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(file, true), "UTF-8"));
				if (finishflg == true) {
					write_int = date2 + "," + y_SensorValue + ","
							+ gyro_value[0] + "," + gyro_value[1] + ","
							+ gyro_value[2] + "," + result+","+gyro_value[3] + "\n";
				} else {
					write_int = date2 + "," + y_SensorValue + ","
							+ gyro_value[0] + "," + gyro_value[1] + ","
							+ gyro_value[2] + "\n";
				}
				bw.write(write_int);
				bw.close();
			} catch (UnsupportedEncodingException k) {
				k.printStackTrace();
			} catch (FileNotFoundException k) {
				k.printStackTrace();
			} catch (IOException k) {
				k.printStackTrace();
			}
		}
	}

	// 止まった時の動作
	private void dostop() {
		// かんながけが止まった時の動作
		isRepeat = false;
		threads = null;
		stop = System.currentTimeMillis();
		alltime = (stop - start) / 1000;
		if (alltime == 0) {
			alltime = 1;
		}

		if (goukei_x < 5) {
			str_rank = "Ａ";
		} else if (goukei_x > 15) {
			str_rank = "Ｃ";
		} else {
			str_rank = "Ｂ";
		}
		float bairitu_flt = (float) 100
				/ (float) Integer.parseInt(modeldata_str);
		int tokuten_int = (int) (goukei * bairitu_flt);
		String krStr = null;

		if (tokuten_int < 60) {
			krStr = "遅すぎます！もっと速くかんなを引きましょう。";
		} else if (tokuten_int < 80) {
			krStr = "もう少し早くかんなを引きましょう。";
		} else if (tokuten_int < 120) {
			krStr = "ちょうど良い速さです！その速度を覚えましょう。";
		} else if (tokuten_int > 120) {
			krStr = "速すぎます。もう少し丁寧にかんなを引きましょう";
		}

		if (tokuten_int > 100) {
			tokuten_int = 100 - (tokuten_int - 100);
			if (tokuten_int < 0) {
				tokuten_int = 0;
			}
		}

		double d = ((float) timesInt * 0.1) - 0.5;
		Double objnum = new Double(d);
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(1);
		df.setMinimumFractionDigits(1);
		result = df.format(objnum);

		String ViewStr = "";


		textV.setTextSize(17);
		textV.setTextColor(Color.WHITE);
		textV.setText(ViewStr);
		textV.setPadding(0, 10, 0, 0);
		textV.setGravity(Gravity.LEFT);
		startFlag = false;

		mp = MediaPlayer.create(this, R.raw.result);

		try {
			mp.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		mp.start();

		time.setToNow();
		String now = String.valueOf(time.hour) + String.valueOf(time.minute)
				+ String.valueOf(time.second);
		Log.d("now", now);
		String filePath = Environment.getExternalStorageDirectory() + "/"
				+ getPackageName() + "/" + "kanna" + now + ".png";
		File file = new File(filePath);
		file.getParentFile().mkdir();
		FileOutputStream fos;

		try {
			fos = new FileOutputStream(file, true);
			bmp.compress(CompressFormat.PNG, 10, fos);
			fos.flush();
			fos.close();
		} catch (Exception e) {
			Log.e("not conplete wirte SD", "no");
		}
		mHandler.removeCallbacks(looper);
	}

	// //////////レイアウトクラス//////////////////////////////////////////////
	private class SampleView extends View {
		private Paint mPaint;
		private Paint mPaintLine;

		private float xGraph = 20;
		private float pastY = 200;

		public SampleView(Context context) {
			super(context);
			setFocusable(true);

			mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setStrokeWidth(6);
			mPaint.setColor(Color.BLACK);

			mPaintLine = new Paint(Paint.ANTI_ALIAS_FLAG);
			mPaintLine.setStyle(Paint.Style.STROKE);
			mPaintLine.setStrokeWidth(6);
			mPaintLine.setColor(Color.BLUE);

		}

		/** 画面サイズが変更された時 */
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			Log.v("DrawNoteK", "view.onSizeChanged");
			super.onSizeChanged(w, h, oldw, oldh);
			wide = w;
			hight = h;
			pastY = h / 2;
			Log.d("w+h", String.valueOf(w) + "+" + String.valueOf(h));
			if (bmp == null) {
				bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);
				bmp_model = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);
				bmp_model = BitmapFactory.decodeFile(Environment
						.getExternalStorageDirectory()
						+ "/"
						+ getPackageName()
						+ "/" + modelName + ".png");
			}
			bmpCanvas = new Canvas(bmp);
			bmpCanvas.drawColor(Color.argb(0, 200, 255, 255));
		}

		@Override
		protected void onDraw(Canvas canvas) {
			canvas.drawBitmap(bmp, 0, 0, null);
			// スレッド起動
			threads = new Thread(runnable);
			threads.start();
			if (startFlag == false) {

				canvas.drawBitmap(bmp_model, 0, 0, null);
			}
		}

		// Thread処理
		Runnable runnable = new Runnable() {
			public void run() {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {

				}
				// UIの操作はHandlerの中に記述
				dHandler.post(new Runnable() {
					public void run() {
						doDrow();
					}

					private void doDrow() {

						Log.d("doDrow()", "入りました");

						// 縦軸の倍率調整
						mYOffset = hight / 2 + (y_SensorValue) * 10;

						Paint paintSensor = new Paint();
						paintSensor.setColor(Color.BLUE);
						paintSensor.setStyle(Paint.Style.FILL);
						paintSensor.setStrokeWidth(4);
						paintSensor.setAntiAlias(true);

						Paint paintLine = new Paint();
						paintLine.setColor(Color.BLACK);
						paintLine.setStyle(Paint.Style.FILL);
						paintLine.setStrokeWidth(2);

						Paint paintLine2 = new Paint();
						paintLine2.setColor(Color.BLACK);
						paintLine2.setStyle(Paint.Style.FILL);
						paintLine2.setStrokeWidth(1);

						// 線を描画
						int y = (int) (mYOffset);
						if (startFlag == true) {
							bmpCanvas.drawLine(xGraph, pastY, xGraph + 14, y,
									paintSensor);
						} else {
							Log.d("doDrow()", "終わったよー");
							dHandler.removeCallbacks(runnable);
						}
						bmpCanvas.drawLine(20, hight / 2, wide - 20, hight / 2,
								paintLine);

						bmpCanvas.drawLine(20, (hight / 4) * 1, wide - 20,
								(hight / 4) * 1, paintLine2);
						bmpCanvas.drawLine(20, (hight / 4) * 3, wide - 20,
								(hight / 4) * 3, paintLine2);

						// 横軸の倍率調整(14で5s 20で3s)
						xGraph = xGraph + 14;
						pastY = y;
						// Log.d("y", String.valueOf(y));
						invalidate();
					}
				});
			}
		};

	}

	// //////////レイアウトクラス//////////////////////////////////////////////

	@Override
	public void onResume() {
		super.onResume();
		Sensor sensor = sensorManager
				.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
		Sensor sensor2 = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		sensorManager.registerListener(this, sensor,
				SensorManager.SENSOR_DELAY_GAME);
		sensorManager.registerListener(this, sensor2,
				SensorManager.SENSOR_DELAY_GAME);
	}

	@Override
	public void onPause() {
		super.onPause();
		sensorManager.unregisterListener(this);
	}

	public void onSensorChanged(SensorEvent event) {
		switch (event.sensor.getType()) {
		case Sensor.TYPE_LINEAR_ACCELERATION:
			y_sensor = event.values[1];
			x_sensor = event.values[0];
			break;
		case Sensor.TYPE_GYROSCOPE:
			gyro_value[0] = event.values[0];
			gyro_value[1] = event.values[1];
			gyro_value[2] = event.values[2];
			break;
		}
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO 自動生成されたメソッド・スタブ

	}
}

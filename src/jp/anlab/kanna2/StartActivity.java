package jp.anlab.kanna2;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import android.R.integer;
import android.R.string;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class StartActivity extends Activity {
	private int i_item = 0;
	private SharedPreferences pref;
	private SharedPreferences modelpref;
	private TextView tv_name;
	private MediaPlayer mp = null;
	static ArrayAdapter<String> adapter;
	ArrayList<String> arrayName;
	ArrayList<String> arrayKeyId;
	Editor e;
	int userId;
	android.app.AlertDialog.Builder dialog = null;
	AlertDialog dialog2;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start);
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
		.permitAll().build());
		mp = MediaPlayer.create(this, R.raw.button);

		pref = getSharedPreferences("pref", MODE_WORLD_READABLE
				| MODE_WORLD_WRITEABLE);
		modelpref = getSharedPreferences("modelpref", MODE_WORLD_READABLE
				| MODE_WORLD_WRITEABLE);
		e = modelpref.edit();
		getuser();
		tv_name = (TextView) findViewById(R.id.tv_name);
		String strname = modelpref.getString("name", "");
		String strint = modelpref.getString("modelint", "");

		if (strname != null) {
			tv_name.setText(strname + "," + strint);
			String logsttr = modelpref.getString(strname + "maxInt", "");
			Log.d("maxInt", logsttr);
		} else {
			Editor e = modelpref.edit();
			e.putString("name", "Nomal");
			e.putString("modelint", "60");
			e.putString("nomalmaxInt", "20");
			e.commit();
		}
		final Button btn_learn = (Button) findViewById(R.id.btn_learn);
		btn_learn.setOnClickListener(new View.OnClickListener() {
			// クリックしたときの動作
			public void onClick(View v) {
				try {
					mp.prepare();
				} catch (IllegalStateException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				} catch (IOException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
				mp.start();
				Uri uri = Uri
						.parse("http://www.nagatac.co.jp/animation/kan_na.htm");
				Intent i = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(i);
			}
		});
		final Button btn_practice = (Button) findViewById(R.id.btn_practice);
		btn_practice.setOnClickListener(new View.OnClickListener() {
			// クリックしたときの動作
			public void onClick(View v) {
				try {
					mp.prepare();
				} catch (IllegalStateException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				} catch (IOException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
				mp.start();
				Intent intent = new Intent(getApplication(),
						KannaActivity.class);
				startActivity(intent);
			}
		});
		final Button btn_setting = (Button) findViewById(R.id.btn_setting);
		btn_setting.setOnClickListener(new View.OnClickListener() {
			// クリックしたときの動作
			public void onClick(View v) {
				try {
					mp.prepare();
				} catch (IllegalStateException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				} catch (IOException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
				mp.start();
				Intent intent = new Intent(getApplication(),
						SettingActivity.class);
				startActivity(intent);
				// Toast.makeText(StartActivity.this,
				// "現在開発中の機能です。\nしばらくお待ちください。", Toast.LENGTH_SHORT).show();
			}
		});
	}

	// メニューが生成される際に起動される。
	// この中でメニューのアイテムを追加したりする。
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
		// メニューインフレーターを取得
		MenuInflater inflater = getMenuInflater();
		// xmlのリソースファイルを使用してメニューにアイテムを追加
		inflater.inflate(R.menu.menu, menu);
		// できたらtrueを返す
		return true;
	}

	// メニューのアイテムが選択された際に起動される。
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_dialog1:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("設定を初期化");
			final String message;
			message = "標準にもどしました";
			builder.setMessage(message);
			builder.setPositiveButton("OK",
					new android.content.DialogInterface.OnClickListener() {
						public void onClick(
								android.content.DialogInterface dialog,
								int whichButton) {
							Editor e = modelpref.edit();
							e.putString("name", "nomal");
							e.putString("modelint", "60");
							e.putString("nomalmaxInt", "20");
							e.commit();
							tv_name.setText("nomal" + "," + "60");
							setResult(RESULT_OK);
						}
					});
			builder.create();
			builder.show();
			break;
		case R.id.menu_dialog2:

			int i = 0;

			Map map = pref.getAll();
			final String[] nameitem = new String[map.size()];

			// Mapから全てのキーと値のエントリをSet型のコレクションとして取得する
			Set<Map.Entry<String, Integer>> entrySet = map.entrySet();
			// キーと値のコレクションの反復子を取得する
			Iterator<Map.Entry<String, Integer>> it = entrySet.iterator();
			// 次の要素がまだ存在する場合はtrueが返される
			while (it.hasNext()) {
				// キーと値をセットを持つ、Map.Entry型のオブジェクトを取得する
				Map.Entry<String, Integer> entry = it.next();
				// Map.Entry型のオブジェクトからキーを取得する
				String key = entry.getKey();
				// Map.Entry型のオブジェクトから値を取得する
				// Integer value = entry.getValue();
				// 標準出力に表示する
				Log.d("log", key);
				nameitem[i] = key;
				i++;
			}
			new AlertDialog.Builder(StartActivity.this)
					.setTitle("モデル選択")
					.setSingleChoiceItems(nameitem, 1,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int item) {
									i_item = item;
								}
							})
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
									String str_value = pref.getString(
											nameitem[i_item], "");
									Editor e = modelpref.edit();
									e.putString("name", nameitem[i_item]);
									e.putString("modelint", str_value);
									e.commit();
									tv_name.setText(nameitem[i_item] + ","
											+ str_value);
								}
							}).show();
			break;
		default:
			break;
		}
		return true;
	}
	public void getuser(){
		InputStream in = null;
		HttpURLConnection http = null;
		try {
			URL url = new URL(
					"http://160.28.60.103/study/output.txt");
			http = (HttpURLConnection) url.openConnection();
			http.setRequestMethod("GET");
			http.connect();
			// データを取得
			in = http.getInputStream();

			// ソースを読み出す
			byte[] data = new byte[2048];
			in.read(data);
			String src = new String(data);
			// ソースの分割
			String[] strAry = src.split("\n");
			arrayName = new ArrayList<String>();
			arrayKeyId = new ArrayList<String>();
			for (int i = 0; i < strAry.length - 1; i++) {
				String[] strAryPerson = strAry[i].split(",");
				arrayName.add(strAryPerson[3]);
				arrayKeyId.add(strAryPerson[5]);
			}
		} catch (Exception e) {
		} finally {
			try {
				if (http != null)
					http.disconnect();
				if (in != null)
					in.close();
			} catch (Exception e) {
			}
		}
		listview();
	}

	// /////////リストビュー////////////////////////////////////////////////////////
	public void listview() {
		ListView LV = new ListView(this);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, arrayName);
		LV.setAdapter(adapter);
        LV.setOnItemClickListener(new OnItemClickListener(){
            public void onItemClick(AdapterView<?> items, View view, int position, long id) {
//                Toast.makeText(NokogiriActivity.this, arrayKeyId.get(position).toString(), Toast.LENGTH_LONG).show();
                e.putString("username", arrayName.get(position).toString());
                e.putString("userid", arrayKeyId.get(position).toString());
				e.commit();
				userId = Integer.parseInt(arrayKeyId.get(position).toString());
				dialog2.dismiss();
            }
        });
		dialog = new AlertDialog.Builder(this);
		dialog2 = dialog.create();
        dialog2.setCanceledOnTouchOutside(false);
		dialog2.setTitle("使用者を選択してください");
		dialog2.setView(LV);
		dialog2.show();
	}
	///////////リストビュー//////////////////////////////////////////////////////////
}
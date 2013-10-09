package hide92795.android.remotecontroller.activity;

import hide92795.android.remotecontroller.R;
import hide92795.android.remotecontroller.Session;
import hide92795.android.remotecontroller.ui.adapter.ChatListAdapter;
import hide92795.android.remotecontroller.ui.adapter.ChatListAdapter.OnAddChatListener;
import hide92795.android.remotecontroller.util.ConfigDefaults;
import hide92795.android.remotecontroller.util.ConfigKeys;
import hide92795.android.remotecontroller.util.LogUtil;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class ChatActivity extends FragmentActivity implements OnClickListener, OnAddChatListener {
	private ScaleGestureDetector gesture_detector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.d("ChatActivity", "onCreate()");
		setContentView(R.layout.activity_chat);
		gesture_detector = new ScaleGestureDetector(this, onScaleGestureListener);
		setListener();
	}

	private void setListener() {
		ListView list = (ListView) findViewById(R.id.list_chat_chat);
		list.setAdapter(((Session) getApplication()).getChatAdapter());
		list.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				gesture_detector.onTouchEvent(event);
				return false;
			}
		});
		// list.setOnItemLongClickListener(this);
		list.setSelection(list.getCount() - 1);
		Button btn_send = (Button) findViewById(R.id.btn_chat_send);
		btn_send.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		LogUtil.d("ChatActivity", "onResume()");
		((Session) getApplication()).getChatAdapter().setOnAddChatListener(this);
		((Session) getApplication()).getChatAdapter().notifyDataSetChanged();
	}

	@Override
	protected void onPause() {
		super.onPause();
		LogUtil.d("ChatActivity", "onPause()");
		((Session) getApplication()).getChatAdapter().setOnAddChatListener(null);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		boolean b_ellipsize = pref.getBoolean(ConfigKeys.CHAT_ELLIPSIZE, ConfigDefaults.CHAT_ELLIPSIZE);
		boolean b_move_bottom = pref.getBoolean(ConfigKeys.CHAT_MOVE_BOTTOM, ConfigDefaults.CHAT_MOVE_BOTTOM);
		MenuItem ellipsize = menu.findItem(R.id.menu_chat_ellipsize);
		MenuItem move_bottom = menu.findItem(R.id.menu_chat_move_bottom);
		ellipsize.setChecked(b_ellipsize);
		move_bottom.setChecked(b_move_bottom);

		// HONEYCOMB以下だとチェックボックスがつかないので自前で追加
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			if (b_ellipsize) {
				ellipsize.setIcon(R.drawable.ic_checked);
			} else {
				ellipsize.setIcon(R.drawable.ic_not_checked);
			}
			if (b_move_bottom) {
				move_bottom.setIcon(R.drawable.ic_checked);
			} else {
				move_bottom.setIcon(R.drawable.ic_not_checked);
			}
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_chat, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_chat_ellipsize: {
			boolean ellipsize = !item.isChecked();
			PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(ConfigKeys.CHAT_ELLIPSIZE, ellipsize).commit();
			updateConsoleAdapter();
			return true;
		}
		case R.id.menu_chat_move_bottom: {
			boolean move_bottom = !item.isChecked();
			PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(ConfigKeys.CHAT_MOVE_BOTTOM, move_bottom).commit();
			updateConsoleAdapter();
			return true;
		}
		default:
		}
		return super.onOptionsItemSelected(item);
	}

	private void updateConsoleAdapter() {
		ListView list = (ListView) findViewById(R.id.list_chat_chat);
		ChatListAdapter adapter = (ChatListAdapter) list.getAdapter();
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_chat_send: {
			EditText edittext = (EditText) findViewById(R.id.edittext_chat_send);
			String message = edittext.getText().toString();
			if (message.length() != 0) {
				((Session) getApplication()).getConnection().requests.requestChat(message);
				edittext.setText("");
			}
			break;
		}
		default:
			break;
		}
	}

	@Override
	public void onAddChat() {
		ListView list = (ListView) findViewById(R.id.list_chat_chat);
		list.setSelection(list.getCount() - 1);
	}

	private final SimpleOnScaleGestureListener onScaleGestureListener = new SimpleOnScaleGestureListener() {
		private int fontsize;

		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector) {
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ChatActivity.this);
			fontsize = Integer.parseInt(pref.getString(ConfigKeys.CHAT_FONT_SIZE, ConfigDefaults.CHAT_FONT_SIZE));
			return super.onScaleBegin(gesture_detector);
		}

		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			int calced = Math.round(fontsize * detector.getScaleFactor());
			if (calced == 0) {
				calced = 1;
			}
			if (fontsize != calced) {
				SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ChatActivity.this);
				pref.edit().putString(ConfigKeys.CHAT_FONT_SIZE, Integer.toString(calced)).commit();
				((Session) getApplication()).getChatAdapter().notifyDataSetChanged();
			}
			return super.onScale(gesture_detector);
		}

		@Override
		public void onScaleEnd(ScaleGestureDetector detector) {
			int calced = Math.round(fontsize * detector.getScaleFactor());
			if (calced == 0) {
				calced = 1;
			}
			if (fontsize != calced) {
				SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ChatActivity.this);
				pref.edit().putString(ConfigKeys.CHAT_FONT_SIZE, Integer.toString(calced)).commit();
				((Session) getApplication()).getChatAdapter().notifyDataSetChanged();
			}
			LogUtil.d("ChatActivity", "Old size : " + fontsize + " ,New size : " + calced);
		}
	};
}
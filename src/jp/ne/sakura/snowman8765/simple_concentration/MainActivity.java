package jp.ne.sakura.snowman8765.simple_concentration;

import jp.maru.scorecenter.ScoreCenter;
import android.app.Activity;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class MainActivity extends Activity {
	public int disp_w, disp_h;
	public ScoreCenter scoreCenter;

	// Log用のTAG
	private static final String TAG = MainActivity.class.getSimpleName();

	// BackボタンPress時の有効タイマー
	private CountDownTimer keyEventTimer;

	// 一度目のBackボタンが押されたかどうかを判定するフラグ
	private boolean pressed = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Window window = getWindow();
		window.setFormat(PixelFormat.TRANSLUCENT);
		window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		WindowManager manager = window.getWindowManager();
		Display disp = manager.getDefaultDisplay();
		Display display = getWindowManager().getDefaultDisplay();
		if (Integer.valueOf(android.os.Build.VERSION.SDK_INT) < 13) {
			disp_w = display.getWidth();
			disp_w = display.getHeight();
		} else {
			Point size = new Point();
			disp.getSize(size);
			disp_w = size.x;
			disp_h = size.y;
		}
		// onFinish(), onTick()
		keyEventTimer = new CountDownTimer(1000, 100) {
			@Override
			public void onTick(long millisUntilFinished) {
				Log.d(TAG, "call onTick method");
			}

			@Override
			public void onFinish() {
				pressed = false;
			}
		};

		scoreCenter = ScoreCenter.getInstance();
		scoreCenter.initialize(getApplicationContext());
		// scoreCenter.setLogEnable(true);
		// scoreCenter.setKeepViewCacheEnable(true);

		setContentView(new MainLoop(this));
		// setContentView(R.layout.activity_main);

		scoreCenter.hello();
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {

		// Backボタン検知
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if (!pressed) {
				// Timerを開始
				keyEventTimer.cancel(); // いらない？
				keyEventTimer.start();

				// 終了する場合, もう一度タップするようにメッセージを出力する
				Toast.makeText(this, "終了する場合は、もう一度バックボタンを押してください",
						Toast.LENGTH_SHORT).show();
				pressed = true;
				return false;
			}

			// pressed=trueの時、通常のBackボタンで終了処理.
			return super.dispatchKeyEvent(event);
		}
		// Backボタンに関わらないボタンが押された場合は、通常処理.
		return super.dispatchKeyEvent(event);
	}
}

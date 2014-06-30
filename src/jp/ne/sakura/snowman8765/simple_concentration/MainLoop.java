package jp.ne.sakura.snowman8765.simple_concentration;

import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

public class MainLoop extends SurfaceView implements SurfaceHolder.Callback, Runnable {

	public static final int GAME_START = 0;// ゲーム状態固定値
	public static final int GAME_PLAY = 1;// ゲーム状態固定値
	public static final int GAME_CLEAR = 2;
	private int game_state;// ゲーム状態決定変数
	private Thread thread;
	private SurfaceHolder holder;
	private int disp_w, disp_h;// 画面の幅高さ
	private MainActivity Card1;// Activityクラス登録
	private int sleep;// 遅延時間
	private TextView tv = null;

	// 変数設定

	public static final int MAX_MAISUU = 52;// カード枚数
	public static final int SYURUI = 4;// カード種類の数
	public static final int MAISUU = 13;// カード１種類の枚数

	private Card buck;// 背景画像
	private Drawable[] card_img;// ５２枚カード
	private Drawable[] card_img2;// カード裏、使わないけどジョーカー用
	private ArrayList<Card> card = new ArrayList<Card>();// カード用オブジェクト
	private Rect[] card_xy;// カード座標用
	private ArrayList<Integer> card_list = new ArrayList<Integer>();

	private Bitmap back_img;
	private Rect src, dist;

	private boolean tap_rect = false;// １枚目タップ用
	private Card tap;// タップしたら色変更用
	private Rect tap_rect_old = new Rect(0, 0, 0, 0);// １枚目タップカード座標
	private boolean tap_tap = false;// ２枚目タップしたかどうか
	private int tap_one, tap_two;// １、２枚目タップのオブジェクト位置
	private int hantei_count = 0;// すぐ判定せずにカウントさせる用

	private int card_count = 0;// ５２枚めくれたかどうか用

	long startTime = 0L;
	long endTime = 0L;

	// 変数設定ここまで

	public MainLoop(Context context) {
		super(context);
		init(context);
	}

	public MainLoop(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public void init(Context context) {
		holder = getHolder();
		holder.addCallback(this);
		holder.setFixedSize(getWidth(), getHeight());
		Card1 = (MainActivity) context;
		disp_w = Card1.disp_w;// 画面幅取得
		disp_h = Card1.disp_h;// 画面高さ取得
		game_state = 1;// ゲーム状態
		tv = new TextView(Card1);
		tv.setVisibility(View.VISIBLE);

		// ここから初期化

		this.setSleep(0);// ループ中遅延時間

		buck = new Card(disp_w, disp_h);
		buck.Rectinit(0, 0, (int) disp_w, (int) disp_h, Color.BLACK, 100);
		tap = new Card(disp_w, disp_h);
		tap.Rectinit(0, 0, 100, 100, Color.RED, 30);

		Resources resources = context.getResources();// 画像登録準備
		card_img = new Drawable[MAX_MAISUU];
		Bitmap img = BitmapFactory.decodeResource(resources, R.drawable.card);
		card_xy = new Rect[MAX_MAISUU];
		for (int i = 0; i < SYURUI; i++) {
			for (int j = 0; j < MAISUU; j++) {
				card_img[j + i * MAISUU] = new BitmapDrawable(getResources(),
						Bitmap.createBitmap(img, j * (img.getWidth() / MAISUU),
								i * (img.getHeight() / SYURUI), img.getWidth()
										/ MAISUU, img.getHeight() / SYURUI));
				card_xy[j + i * MAISUU] = new Rect(j * (int) (disp_w / MAISUU),
						i * (int) (disp_h / SYURUI), (int) (disp_w / MAISUU),
						(int) (disp_h / SYURUI));
			}
		}

		back_img = BitmapFactory.decodeResource(resources, R.drawable.clear);
		src = new Rect(0,0,back_img.getWidth(),back_img.getHeight());
		dist = new Rect(0,0,disp_w,disp_h);

		for ( int i = 0; i < MAX_MAISUU; i++ ) {
			card_list.add(i);
		}

		card_img2 = new Drawable[2];
		img = BitmapFactory.decodeResource(resources, R.drawable.sonota);
		for (int i = 0; i < 2; i++) {
			card_img2[i] = new BitmapDrawable(getResources(),
					Bitmap.createBitmap(img, i * (img.getWidth() / 2), 0,
							img.getWidth() / 2, img.getHeight()));
		}

		for (int i = 0; i < MAX_MAISUU; i++) {
			card.add(new Card(disp_w, disp_h));// カードをオブジェクト登録
			card.get(i).Cardinit(card_img[i], card_img2[1], 0, 0, 70, 105);// カードを初期設定
			card.get(i).setCardRect(card_xy[i]);
		}
		for (int i = 0; i < SYURUI; i++) {
			for (int j = 0; j < MAISUU; j++) {
				card.get(j + i * MAISUU).setCardEl(j + 1, i);
			}
		}

		Syokika();

		// 初期化ここまで

	}

	// クリア後ここを呼び出せばリスタート
	private void Syokika() {
		for (int i = 0; i < MAX_MAISUU; i++) {
			card.get(i).setCardDraw(true);
			card.get(i).setObjState(1);
		}
		Shuffle();
		startTime = System.currentTimeMillis();
	}

	// カードシャッフル処理
	public void Shuffle() {
//		Random r = new Random(new Date().getTime());
//		Rect c = new Rect();
//		for (int i = 0; i < 1000; i++) {
//			int a = r.nextInt(MAX_MAISUU);
//			int b = r.nextInt(MAX_MAISUU);
//			c = card_xy[a];
//			card_xy[a] = card_xy[b];
//			card_xy[b] = c;
//		}

		Collections.shuffle(card_list);

		for (int i = 0; i < MAX_MAISUU; i++) {
			card.get(i).setCardRect(card_xy[card_list.get(i)]);
		}
	}

	// メインループ
	public void run() {
		Canvas c;
		Paint p = new Paint();
		p.setAntiAlias(true);

		while (thread != null) {
			c = holder.lockCanvas();
			if(c!=null) {
				// ゲーム状態によってスイッチ処理
				switch (game_state) {
				case GAME_START:
					StartDraw(c, p);
					break;
				case GAME_PLAY:
					PlayDraw(c, p);
					break;
				case GAME_CLEAR:
					Card1.scoreCenter.postScore("simple_concentration_scoreboard1", java.lang.String.valueOf(0.000f));
					drawString("GAME CLEAR!", 10, 200, 60, Color.BLUE, c, p);
					break;
				}
				holder.unlockCanvasAndPost(c);
			}

			try {
				Thread.sleep(sleep);
			} catch (Exception e) {
			}
		}
	}

	// タッチ処理
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		int x = (int) event.getX();
		int y = (int) event.getY();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			switch (game_state) {
			case GAME_START:
				break;
			case GAME_PLAY:
				if (tap_tap == false)
					RectTap(x, y);
				break;
			case GAME_CLEAR:
				Syokika();
				game_state = 1;
				break;
			}
			break;
		case MotionEvent.ACTION_UP:
			switch (game_state) {
			case GAME_START:
				break;
			case GAME_PLAY:
				break;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			switch (game_state) {
			case GAME_START:
				break;
			case GAME_PLAY:
				break;
			}
			break;
		}
		return true;
	}

	// タップ位置をカード位置比較用、範囲内ならtrue
	public boolean RectTap(int x, int y, Rect gazou) {
		return gazou.left < x && gazou.top < y && gazou.right > x
				&& gazou.bottom > y;
	}

	// 今回は未使用
	public void StartDraw(Canvas c, Paint p) {
	}

	// game_stateが１ならここをループ
	public void PlayDraw(Canvas c, Paint p) {
		// プレイ画面表示
		// 背景表示黒く塗りつぶすだけ
		buck.Rectdraw(c, p);

		c.drawBitmap(back_img, src, dist, p);

		// カード52枚表示
		for (int i = 0; i < card.size(); i++) {
			card.get(i).Carddraw(c, p);
		}
		// カードをタップすると短形表示
		if (tap_rect == true) {
			tap.Rectdraw(c, p);
		}
		// ２枚目タップしていればカウント＋１
		if (tap_tap == true)
			++hantei_count;
		// ２枚目タップしててカウントが５になれば判定
		// ここで２枚めくったらすぐ消えたりするのを防いでいます
		if (tap_tap == true && hantei_count == 5) {
			Hantei();
			hantei_count = 0;
			Shuffle();
			// ５２枚めくれたらクリア
			if (card_count == 52)
				game_state = 2;
		}

		endTime =System.currentTimeMillis();
		float erapsedTime = (endTime - startTime)/1000.0f;
		p.setColor(Color.BLUE);
		p.setTextSize(60);
		c.drawText("経過時間 : "+ erapsedTime +" 秒", 10, disp_h, p);
	}

	// タップした時の処理
	public void RectTap(int x, int y) {
		// すでにタップしていて同じカードをタップした場合
		if (RectTap(x, y, tap_rect_old) == true) {
			tap_rect = false;
			tap_rect_old = new Rect(0, 0, 0, 0);
			card.get(tap_one).setObjState(1);
		} else
		// 始めてカードをタップした場合
		if (tap_rect == false) {
			for (int i = 0; i < card.size(); i++) {
				if (card.get(i).getCardDraw() == true
						&& RectTap(x, y, card.get(i).getCardRect()) == true) {
					tap.getsetCardRect(card.get(i).getCardRect());
					tap_rect_old = card.get(i).getCardRect();
					tap_rect = true;
					card.get(i).setObjState(0);
					tap_one = i;
				}
			}
		} else
		// ２枚目のカードをタップした場合
		if (tap_rect == true) {
			for (int i = 0; i < card.size(); i++) {
				if (card.get(i).getCardDraw() == true && tap_tap == false
						&& tap_one != i
						&& RectTap(x, y, card.get(i).getCardRect()) == true) {
					tap.getsetCardRect(card.get(i).getCardRect());
					card.get(i).setObjState(0);
					tap_two = i;
					tap_tap = true;
				}
			}
		}
	}

	// 二回目のタップの時だけ２枚の数字が同じか判定
	// 同じならその２枚は表示させないようにする
	public void Hantei() {
		if (card.get(tap_one).getSuuji() == card.get(tap_two).getSuuji()) {
			card.get(tap_one).setCardDraw(false);
			card.get(tap_two).setCardDraw(false);
			tap_tap = false;
			tap_rect = false;
			tap_rect_old = new Rect(0, 0, 0, 0);
			card_count += 2;
		} else {
			card.get(tap_one).setObjState(1);
			card.get(tap_two).setObjState(1);
			tap_tap = false;
			tap_rect = false;
			tap_rect_old = new Rect(0, 0, 0, 0);
		}
	}

	// テスト表示用
	public void drawString(String str, int x, int y, int size, int col,
			Canvas c, Paint p) {
		p.setColor(col);
		p.setTextSize(size);
		c.drawText(str, x, y, p);
	}

	public void setSleep(int s) {
		sleep = s;
	}

	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
	}

	public void surfaceCreated(SurfaceHolder arg0) {
		thread = new Thread(this);
		thread.start();
	}

	public void surfaceDestroyed(SurfaceHolder arg0) {
		thread = null;
	}
}

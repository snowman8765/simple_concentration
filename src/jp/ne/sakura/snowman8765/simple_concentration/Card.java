package jp.ne.sakura.snowman8765.simple_concentration;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class Card {

	private double disp_w, disp_h;// ディスプレイ大きさ

	public float px, py; // オブジェクト座標
	private int w, h; // オブジェクトの横幅縦幅
	private Rect rect; // オブジェクトのRect
	private int alpha; // オブジェクトの透過度
	private int objs_state;// カード表示裏か表か
	private int col;// オブジェクトカラー
	private Drawable[] obj = new Drawable[2];// カード画像、表、裏
	private int suuji, syurui;// カード数字と種類
	private boolean card_draw = true;// カードを表示させるかどうか

	// コンストラクタ
	public Card() {
	}

	public Card(double ww, double hh) {
		disp_w = ww;
		disp_h = hh;
	}

	// カード初期化
	public void Cardinit(Drawable img, Drawable img2, float px, float py,
			int w, int h) {
		this.obj[0] = img;
		this.obj[1] = img2;
		this.px = px;
		this.py = py;
		this.w = w;
		this.h = h;
		objs_state = 1;
		//setgetDrawableSize();

	}

	// カードに数字と種類の要素代入
	public void setCardEl(int suu, int syu) {
		suuji = suu;
		syurui = syu;
	}

	// カード表示
	public void Carddraw(Canvas c, Paint p) {
		if (card_draw == true) {
			rect = new Rect((int) (px), (int) (py), (int) (px + w),
					(int) (py + h));
			obj[objs_state].setBounds(rect);
			obj[objs_state].draw(c);
		}
	}

	// 短形初期化
	public void Rectinit(float px, float py, int w, int h, int col, int al) {
		this.px = px;
		this.py = py;
		this.w = w;
		this.h = h;
		this.col = col;
		this.alpha = al;
		//setgetDrawableSize();
		rect = new Rect((int) (px), (int) (py), (int) (px + w), (int) (py + h));
	}

	// 短形表示
	public void Rectdraw(Canvas c, Paint p) {
		p.setColor(col);
		p.setAlpha(alpha);
		c.drawRect(rect, p);
	}

	// カード座標代入
	public void setCardRect(Rect rect1) {
		px = rect1.left;
		py = rect1.top;
		w = rect1.right;
		h = rect1.bottom;
		//setgetDrawableSize();
	}

	public boolean getCardDraw() {
		return card_draw;
	}

	public void setCardDraw(boolean t) {
		card_draw = t;
	}

	public int getSuuji() {
		return suuji;
	}

	public void setObjState(int o) {
		objs_state = o;
	}

	public void getsetCardRect(Rect rect1) {
		rect = rect1;
	}

	public Rect getCardRect() {
		return rect;
	}

	// テスト用
	public void drawString(String str, int x, int y, int size, int col,
			Canvas c, Paint p) {
		p.setColor(col);
		p.setTextSize(size);
		c.drawText(str, x, y, p);
	}

	// どのスマホの機種でもうまく表示出来るように、座標変換
	public void setgetDrawableSize() {
		double dw = disp_w / 854f;
		double dh = disp_h / 480f;

		px *= dw;
		py *= dh;
		w *= dw;
		h *= dh;
	}
}

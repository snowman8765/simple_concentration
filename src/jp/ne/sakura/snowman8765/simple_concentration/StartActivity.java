package jp.ne.sakura.snowman8765.simple_concentration;

import jp.maru.scorecenter.ScoreCenter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);

		Button start_btn = (Button)findViewById(R.id.start_btn);
		start_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// インテントのインスタンス生成
				Intent intent = new Intent(StartActivity.this, MainActivity.class);
				// 次画面のアクティビティ起動
				startActivity(intent);
			}
		});

		Button ranking_btn = (Button)findViewById(R.id.ranking_btn);
		ranking_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ScoreCenter scoreCenter = ScoreCenter.getInstance();
				scoreCenter.initialize(getApplicationContext());
				scoreCenter.show("simple_concentration_scoreboard1");
			}
		});

		Button end_btn = (Button)findViewById(R.id.end_btn);
		end_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
}

package dev.si.timeismoney.showApp;

import dev.si.timeismoney.MainActivity;
import dev.si.timeismoney.R;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;

public class DesignMoney{


	//timeによって画像が変わる
	public void setPicture(MainActivity mainActivity, int time){
		
		// ID=moneyのImgeViewをあらかじめ設定しておくこと
		ImageView imageView = (ImageView) mainActivity.findViewById(R.id.money);
		
		if(time > 100){
			
			// 一万円が増える感じ
	        imageView.setImageResource(R.drawable.itimanup);
			
		}else if(time > 50){
			
	        imageView.setImageResource(R.drawable.gosenup);
			
		}else if(time >= 0){

			imageView.setImageResource(R.drawable.gohyaku);
			
		}else if(time > -50){
			
	        imageView.setImageResource(R.drawable.gosendown);
			
		}else{
			
	        imageView.setImageResource(R.drawable.itimandown);

		}
	}
	
	/*使い方
	 *  private DesignMoney money;
	 *  money = new DesignMoney();
	 *  money.setPicture(this, 1);
	 * 
	 * こんな感じで！
	 * 
	 */
}

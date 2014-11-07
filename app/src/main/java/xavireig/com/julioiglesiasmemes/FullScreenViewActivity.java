package xavireig.com.julioiglesiasmemes;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;

import xavireig.com.julioiglesiasmemes.adapter.FullScreenImageAdapter;
import xavireig.com.julioiglesiasmemes.helper.Utils;

public class FullScreenViewActivity extends Activity{

	private Utils utils;
	private FullScreenImageAdapter adapter;
	private ViewPager viewPager;

    private int totalBackgrounds = 4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fullscreen_view);

		viewPager = (ViewPager) findViewById(R.id.pager);

		utils = new Utils(getApplicationContext());

		Intent positionIntent = getIntent();
		int position = positionIntent.getIntExtra("position", 0);

        File sdCardDirectory = new File(Environment.getExternalStorageDirectory() + File.separator + "JulioIglesias" + File.separator);
        sdCardDirectory.mkdirs();

        // load images on a temp folder
        for (int i = 0; i < totalBackgrounds; i++) {
            int id = this.getResources().getIdentifier("julio_bg" + i, "drawable", this.getPackageName());
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), id);
            File imageFile = new File(sdCardDirectory, "julio_bg"+ i + ".png");

            // Encode the file as a PNG image.
            FileOutputStream outStream;
            try {
                outStream = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 25, outStream);
                outStream.flush();
                outStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

		adapter = new FullScreenImageAdapter(FullScreenViewActivity.this,
				utils.getFilePaths());

		viewPager.setAdapter(adapter);

		// displaying selected image first
		viewPager.setCurrentItem(position);
	}

    public void onBtChooseClick(View v) {
        Intent resultIntent = new Intent();
        int i = viewPager.getCurrentItem();
        resultIntent.putExtra("picture_number", String.valueOf(i));
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}

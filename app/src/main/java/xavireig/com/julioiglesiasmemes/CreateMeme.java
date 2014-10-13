package xavireig.com.julioiglesiasmemes;

import android.app.Activity;
import android.app.Fragment;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

public class CreateMeme extends Activity {
    Bitmap originalBitmap, image;
    ImageView ivMeme;
    EditText txtText1, txtText2;
    ShareActionProvider mShareActionProvider;
    TextPaint paint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creatememe);
        //image view
        ivMeme = (ImageView) findViewById(R.id.ivMeme);

        //to get screen width and hight
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        //dimentions x,y of device to create a scaled bitmap having similar dimentions to screen size
        int height1 = displaymetrics.heightPixels;
        int width1 = displaymetrics.widthPixels;

        //loading bitmap from drawable
        originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.julio_bg);
        //scaling of bitmap
        originalBitmap =Bitmap.createScaledBitmap(originalBitmap, width1, height1, false);
        //creating anoter copy of bitmap to be used for editing
        image = originalBitmap.copy(Bitmap.Config.RGB_565, true);

        txtText1 =(EditText) findViewById(R.id.txtText1);
        txtText2 =(EditText) findViewById(R.id.txtText2);

        Button btGenerate = (Button) findViewById(R.id.btnGenerate);
        btGenerate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // hide keyboard

                InputMethodManager imm = (InputMethodManager)getSystemService(Service.INPUT_METHOD_SERVICE);
                EditText myEditText = (EditText) findViewById(R.id.txtText1);
                imm.hideSoftInputFromWindow(myEditText.getWindowToken(), 0);
                Button bt = (Button)findViewById(R.id.btnGenerate);
                bt.requestFocus();

                //loading original bitmap again (undoing all editing)
                image = originalBitmap.copy(Bitmap.Config.RGB_565, true);
                ivMeme.setImageBitmap(image);
                String topText = txtText1.getText().toString().toUpperCase();
                String bottomText = txtText2.getText().toString().toUpperCase();

                //function called to perform drawing
                createImage(topText, bottomText);
                // update the shared object
                setShareIntent();
            }
        });
    }

    /*void saveImage(Bitmap img) {
        File RootDir = Environment.getExternalStorageDirectory();
        File file = new File (RootDir, "tmp.png");
        try {
            FileOutputStream out = new FileOutputStream(file);
            img.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Toast.makeText(this, "Image saved to root folder", Toast.LENGTH_LONG).show();
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu.
        // Adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        // Access the Share Item defined in menu XML
        MenuItem shareItem = menu.findItem(R.id.menu_item_share);

        // Access the object responsible for
        // putting together the sharing submenu
        if (shareItem != null) {
            mShareActionProvider = (ShareActionProvider)shareItem.getActionProvider();
        }

        // Create an Intent to share your content
        setShareIntent();

        return true;
    }

    private void setShareIntent() {
        if (mShareActionProvider != null) {

            File sdCardDirectory = Environment.getExternalStorageDirectory();
            File imageFile = new File(sdCardDirectory,"temp.png");

            // Encode the file as a PNG image.
            FileOutputStream outStream;
            try {
                outStream = new FileOutputStream(imageFile);
                //Bitmap btmp = ((BitmapDrawable)ivMeme.getDrawable()).getBitmap();
                image.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                outStream.flush();
                outStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("image/png");
            sharingIntent.putExtra(android.content.Intent.EXTRA_STREAM, Uri.fromFile(imageFile));

            // Make sure the provider knows
            // it should work with that Intent
            mShareActionProvider.setShareIntent(sharingIntent);
        }
    }

    public Bitmap createImage(String topText, String bottomText){
        float x, y = 0.0f;
        //canvas object with bitmap image as constructor
        Canvas canvas = new Canvas(image);
        int viewTop = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
        x = canvas.getWidth() / 2;
        y = canvas.getHeight() / 8;

        String fontPath = "impact.ttf";
        Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);
        paint = new TextPaint();
        paint.setTypeface(tf);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeMiter(10);
        paint.setStrokeWidth(15);
        paint.setTextSize(150);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(Color.BLACK);

        canvas.save();
        StaticLayout layoutTop = new StaticLayout(""+topText, paint, canvas.getWidth(), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        canvas.translate(x, y);
        layoutTop.draw(canvas);
        canvas.restore();

        canvas.save();
        StaticLayout layoutBottom = new StaticLayout(""+bottomText, paint, canvas.getWidth(), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        canvas.translate(x, 6*y);
        layoutBottom.draw(canvas);
        canvas.restore();

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);

        canvas.save();
        StaticLayout layoutTop2 = new StaticLayout(""+topText, paint, canvas.getWidth(), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        canvas.translate(x, y);
        layoutTop2.draw(canvas);
        canvas.restore();

        canvas.save();
        StaticLayout layoutBottom2 = new StaticLayout(""+bottomText, paint, canvas.getWidth(), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        canvas.translate(x, 6*y);
        layoutBottom2.draw(canvas);
        canvas.restore();

        ivMeme.setImageBitmap(image);

        return image;
    }

    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_create, container, false);
            return rootView;
        }
    }

    public static class AdFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_ad_create, container, false);
        }

        @Override
        public void onActivityCreated(Bundle bundle) {
            super.onActivityCreated(bundle);
            AdView mAdView = (AdView) getView().findViewById(R.id.adViewCreate);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }
    }

}

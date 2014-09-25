package xavireig.com.julioiglesiasmemes;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;


public class Main extends Activity {

    ShareActionProvider mShareActionProvider;
    ImageView iv;
    Bitmap bitmap;
    int total_images;
    private Boolean exit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setProgressBarIndeterminateVisibility(false);
        // Create global configuration and initialize ImageLoader with this config
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);
        setContentView(R.layout.activity_main);
        StrictMode.enableDefaults();
        total_images = 0;
        getData();
    }

    @Override
    public void onBackPressed() {
        if (exit)
            this.finish();
        else {
            Toast.makeText(this, "Pulsa de nuevo para salir",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }

    }

    public void onButtonButtonClicked(View view) {
        ImageLoader il = ImageLoader.getInstance();
        iv = (ImageView)findViewById(R.id.imageView);
        // Select random image
        Random r = new Random();
        int pic = r.nextInt(total_images) + 1;
        String imageUri = "http://xavy.net63.net/" + pic + ".jpg";
        il.displayImage(imageUri, iv, null, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                // start progress bar
                setProgressBarIndeterminateVisibility(true);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                // stop progress bar
                setProgressBarIndeterminateVisibility(false);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                iv.setBackgroundDrawable(null);
                // stop progress bar
                setProgressBarIndeterminateVisibility(false);
                bitmap = loadedImage;
                setShareIntent();
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });

    }


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
            File image = new File(sdCardDirectory,"temp.png");

            // Encode the file as a PNG image.
            FileOutputStream outStream;
            try {
                outStream = new FileOutputStream(image);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                outStream.flush();
                outStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("image/png");
            sharingIntent.putExtra(android.content.Intent.EXTRA_STREAM, Uri.fromFile(image));

            // Make sure the provider knows
            // it should work with that Intent
            mShareActionProvider.setShareIntent(sharingIntent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.menu_item_share) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getData() {

        String result = "";
        InputStream isr = null;

        try{
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://xavy.net63.net/index.php");
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            isr = entity.getContent();
        }
        catch(Exception e) {
            Log.e("log_tag", "Error in http connection " + e.toString());
        }

        //convert response to string

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(isr,"iso-8859-1"),8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            if ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            isr.close();

            result=sb.toString();
        }
        catch(Exception e){
            Log.e("log_tag", "Error converting result "+e.toString());
        }

        //parse json data
        try {
            total_images = Integer.parseInt(result);
        }
        catch(Exception e) {
            Log.e("log_tag", "Couldn't set text, damnit.");
        }

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

    public static class AdFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_ad, container, false);
        }

        @Override
        public void onActivityCreated(Bundle bundle) {
            super.onActivityCreated(bundle);
            AdView mAdView = (AdView) getView().findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }
    }
}

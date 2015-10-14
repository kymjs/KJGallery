package com.kymjs.kjgallery.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.kymjs.gallery.KJGalleryActivity;

public class MainActivity extends AppCompatActivity {

    String[] imageUrls = new String[]{
            "http://www.kymjs.com/image/logo_s.jpg",
            "http://www.oschina.net/js/ke/plugins/emoticons/9.gif",
            "http://www.kymjs.com/image/logo_grey.png",
            "http://www.kymjs.com/image/kymjs.png",
            "http://www.kymjs.com/image/logo_s.png",
            "http://www.kymjs.com/image/logo_grey.png",
            "http://www.kymjs.com/image/kymjs.png"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toGallery();
            }
        });
    }

    private void toGallery() {
        /**
         * 这里可以选择三参数(context, 显示第几张图片, 图片路径)
         * 图片路径可以是单个String，也可以是一个String[]
         */
        KJGalleryActivity.toGallery(this, imageUrls);
    }
}

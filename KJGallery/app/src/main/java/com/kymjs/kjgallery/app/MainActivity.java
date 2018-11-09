/*
 * Copyright (c) 2015, 张涛.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kymjs.kjgallery.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.kymjs.gallery.KJGalleryActivity;

public class MainActivity extends AppCompatActivity {

    String[] imageUrls = new String[]{
            "https://www.kymjs.com/qiniu/image/logo_s.jpg",
            "https://static.oschina.net/uploads/zb/2015/0905/214628_00Ui_1164691.gif",
            "https://www.jiuwa.net/tuku/20170823/F45v1uhz.gif",
            "https://www.kymjs.com/qiniu/image/logo_grey.png",
            "https://www.kymjs.com/qiniu/image/kymjs.png",
            "https://www.kymjs.com/qiniu/image/logo_s.png",
            "https://www.kymjs.com/qiniu/image/logo_grey.png",
            "https://www.kymjs.com/qiniu/image/kymjs.png"
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

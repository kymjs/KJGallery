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
package com.kymjs.gallery;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import org.kymjs.kjframe.KJActivity;
import org.kymjs.kjframe.utils.StringUtils;

/**
 * @author kymjs (http://www.kymjs.com/) on 10/13/15.
 */
public class KJGalleryActivity extends KJActivity {

    public static String URL_KEY = "KJGalleryActivity_url";
    public static String URL_INDEX = "KJGalleryActivity_index";

    private TextView textView;

    private String[] imageUrls;
    private int index;

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_kjgallery);
    }

    @Override
    public void initData() {
        super.initData();
        Intent from = getIntent();
        imageUrls = from.getStringArrayExtra(URL_KEY);
        index = from.getIntExtra(URL_INDEX, 0);
    }

    @Override
    public void initWidget() {
        super.initWidget();
        textView = bindView(R.id.page_title);
        if (imageUrls.length < 2) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setText(String.format("%d/%d", index + 1, imageUrls.length));
        }

        HackyViewPager mViewPager = bindView(R.id.view_pager);
        mViewPager.setAdapter(new SamplePagerAdapter(aty, imageUrls));
        mViewPager.setCurrentItem(index);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int
                    positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                textView.setText(String.format("%d/%d", position + 1, imageUrls.length));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    public static void toGallery(Context cxt, int index, String... urls) {
        if (!StringUtils.isEmpty(urls)) {
            Intent intent = new Intent();
            intent.putExtra(KJGalleryActivity.URL_INDEX, index);
            intent.putExtra(KJGalleryActivity.URL_KEY, urls);
            intent.setClass(cxt, KJGalleryActivity.class);
            cxt.startActivity(intent);
        }
    }

    public static void toGallery(Context cxt, String... urls) {
        toGallery(cxt, 0, urls);
    }
}

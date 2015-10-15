package com.kymjs.gallery;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.kymjs.kjframe.KJActivity;
import org.kymjs.kjframe.KJBitmap;
import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.bitmap.BitmapCallBack;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpConfig;
import org.kymjs.kjframe.utils.StringUtils;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * @author kymjs (http://www.kymjs.com/) on 10/13/15.
 */
public class KJGalleryActivity extends KJActivity {

    public static String URL_KEY = "KJGalleryActivity_url";
    public static String URL_INDEX = "KJGalleryActivity_index";

    private HackyViewPager mViewPager;
    private TextView textView;

    private String[] imageUrls;
    private int index;
    private KJBitmap kjb;
    private KJHttp kjh;

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
        kjb = new KJBitmap();
        HttpConfig config = new HttpConfig();
        config.cacheTime = 50000;
        kjh = new KJHttp(config);
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

        mViewPager = bindView(R.id.view_pager);
        mViewPager.setAdapter(new SamplePagerAdapter());
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

    private class SamplePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return imageUrls.length;
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            View root = View.inflate(aty, R.layout.item_pager, null);
            PhotoView photoView = (PhotoView) root.findViewById(R.id.images);
            GifImageView gifView = (GifImageView) root.findViewById(R.id.gifimage);
            ProgressBar mProgressBar = (ProgressBar) root.findViewById(R.id.progress);

            //根据图片类型的不同选择不同的加载方案
            String imageUrl = imageUrls[position];
            if (imageUrl.endsWith(".gif")) {
                displayGif(mProgressBar, gifView, imageUrl);
            } else {
                displayImage(mProgressBar, photoView, imageUrl);
            }

            container.addView(root, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams
                    .MATCH_PARENT);
            return root;
        }

        /**
         * 加载gif图片
         */
        private void displayGif(final ProgressBar proBar, final GifImageView gifView, String url) {
            gifView.setVisibility(View.VISIBLE);

            GifRequest request = new GifRequest(url, new HttpCallBack() {
                @Override
                public void onPreStart() {
                    super.onPreStart();
                    proBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onSuccess(byte[] t) {
                    super.onSuccess(t);
                    try {
                        GifDrawable gifFromBytes = new GifDrawable(t);
                        gifView.setImageDrawable(gifFromBytes);
                        //使用简版的
//                        gifView.setBytes(t);
//                        gifView.startAnimation();
                    } catch (IOException e) {
                        gifView.setImageResource(R.mipmap.default_img_rect);
                    }
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    proBar.setVisibility(View.GONE);
                }
            });

            kjh.doRequest(request);

            gifView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    KJGalleryActivity.this.finish();
                }
            });
        }

        /**
         * 加载普通图片
         */
        private void displayImage(final ProgressBar mProgressBar, PhotoView photoView, String url) {
            photoView.setVisibility(View.VISIBLE);
            BitmapCallBack callBack = new BitmapCallBack() {
                @Override
                public void onPreLoad() {
                    super.onPreLoad();
                    mProgressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    mProgressBar.setVisibility(View.GONE);
                }
            };
            new KJBitmap.Builder().view(photoView).imageUrl(url)
                    .callback(callBack).display(kjb);

            photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float x, float y) {
                    KJGalleryActivity.this.finish();
                }
            });
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

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

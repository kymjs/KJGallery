package com.kymjs.gallery;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.utils.DensityUtils;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * ViewPager适配器
 *
 * @author kymjs (http://www.kymjs.com/) on 10/19/15.
 */
public class SamplePagerAdapter extends PagerAdapter {
    private Activity aty;
    private String[] imageUrls;
    private KJHttp kjh;

    public static int TYPE_GIF = 0;
    public static int TYPE_JPG = 1;
    public static int TYPE_PNG = 2;

    public SamplePagerAdapter(Activity aty, String[] imageUrls) {
        this.aty = aty;
        this.imageUrls = imageUrls;
        kjh = new KJHttp();
    }

    @Override
    public int getCount() {
        return imageUrls.length;
    }

    @Override
    public View instantiateItem(ViewGroup container, int position) {
        View root = View.inflate(aty, R.layout.item_pager, null);
        final PhotoView photoView = (PhotoView) root.findViewById(R.id.images);
        final GifImageView gifView = (GifImageView) root.findViewById(R.id.gifimage);
        final ProgressBar mProgressBar = (ProgressBar) root.findViewById(R.id.progress);

        GifRequest request = new GifRequest(imageUrls[position], new HttpCallBack() {
            @Override
            public void onPreStart() {
                super.onPreStart();
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(byte[] t) {
                super.onSuccess(t);
                //根据图片类型的不同选择不同的加载方案
                if (TYPE_GIF == getType(t)) {
                    displayGif(gifView, t);
                } else {
                    displayImage(photoView, t);
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                mProgressBar.setVisibility(View.GONE);
            }
        });
        kjh.doRequest(request);

        container.addView(root, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams
                .MATCH_PARENT);
        return root;
    }

    /**
     * 加载gif图片
     */
    private void displayGif(final GifImageView gifView, byte[] res) {
        gifView.setVisibility(View.VISIBLE);

        try {
            GifDrawable gifFromBytes = new GifDrawable(res);
            gifView.setImageDrawable(gifFromBytes);
        } catch (IOException e) {
            gifView.setImageResource(R.mipmap.default_img_rect);
        }

        gifView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aty.finish();
            }
        });
    }

    /**
     * 加载普通图片
     */
    private void displayImage(PhotoView photoView, byte[] res) {
        photoView.setVisibility(View.VISIBLE);

        Bitmap bitmap = byteArrayToBitmap(res);
        if (bitmap == null) {
            photoView.setImageResource(R.mipmap.default_img_rect);
        } else {
            photoView.setImageBitmap(bitmap);
        }

        photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                aty.finish();
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


    /**
     * 获取图片类型
     */
    public int getType(byte[] data) {
        // Png test:  
        if (data[1] == 'P' && data[2] == 'N' && data[3] == 'G') {
            return TYPE_PNG;
        }
        // Gif test:  
        if (data[0] == 'G' && data[1] == 'I' && data[2] == 'F') {
            return TYPE_GIF;
        }
        // JPG test:  
        if (data[6] == 'J' && data[7] == 'F' && data[8] == 'I'
                && data[9] == 'F') {
            return TYPE_JPG;
        }
        return TYPE_JPG;
    }

    private Bitmap byteArrayToBitmap(byte[] data) {
        BitmapFactory.Options option = new BitmapFactory.Options();
        int mMaxWidth = DensityUtils.getScreenW(aty);
        int mMaxHeight = DensityUtils.getScreenH(aty);
        option.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, option);
        int actualWidth = option.outWidth;
        int actualHeight = option.outHeight;

        // 计算出图片应该显示的宽高
        int desiredWidth = getResizedDimension(mMaxWidth, mMaxHeight,
                actualWidth, actualHeight);
        int desiredHeight = getResizedDimension(mMaxHeight, mMaxWidth,
                actualHeight, actualWidth);

        option.inJustDecodeBounds = false;
        option.inSampleSize = findBestSampleSize(actualWidth, actualHeight,
                desiredWidth, desiredHeight);
        Bitmap tempBitmap = BitmapFactory.decodeByteArray(data, 0,
                data.length, option);

        Bitmap bitmap;
        // 做缩放
        if (tempBitmap != null
                && (tempBitmap.getWidth() > desiredWidth || tempBitmap
                .getHeight() > desiredHeight)) {
            bitmap = Bitmap.createScaledBitmap(tempBitmap, desiredWidth,
                    desiredHeight, true);
            tempBitmap.recycle();
        } else {
            bitmap = tempBitmap;
        }

        return bitmap;
    }

    /**
     * 框架会自动将大于设定值的bitmap转换成设定值，所以需要这个方法来判断应该显示默认大小或者是设定值大小。<br>
     * 本方法会根据maxPrimary与actualPrimary比较来判断，如果无法判断则会根据辅助值判断，辅助值一般是主要值对应的。
     * 比如宽为主值则高为辅值
     *
     * @param maxPrimary      需要判断的值，用作主要判断
     * @param maxSecondary    需要判断的值，用作辅助判断
     * @param actualPrimary   真实宽度
     * @param actualSecondary 真实高度
     * @return 获取图片需要显示的大小
     */
    private static int getResizedDimension(int maxPrimary, int maxSecondary,
                                           int actualPrimary, int actualSecondary) {
        if (maxPrimary == 0 && maxSecondary == 0) {
            return actualPrimary;
        }
        if (maxPrimary == 0) {
            double ratio = (double) maxSecondary / (double) actualSecondary;
            return (int) (actualPrimary * ratio);
        }

        if (maxSecondary == 0) {
            return maxPrimary;
        }

        double ratio = (double) actualSecondary / (double) actualPrimary;
        int resized = maxPrimary;
        if (resized * ratio > maxSecondary) {
            resized = (int) (maxSecondary / ratio);
        }
        return resized;
    }

    /**
     * 关于本方法的判断，可以查看我的博客：http://kymjs.com/code/2014/12/05/02/
     */
    static int findBestSampleSize(int actualWidth, int actualHeight,
                                  int desiredWidth, int desiredHeight) {
        double wr = (double) actualWidth / desiredWidth;
        double hr = (double) actualHeight / desiredHeight;
        double ratio = Math.min(wr, hr);
        float n = 1.0f;
        while ((n * 2) <= ratio) {
            n *= 2;
        }
        return (int) n;
    }
}

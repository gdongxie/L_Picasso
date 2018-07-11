package com.zzl.lpicasso;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String url = "http://img.hb.aicdn.com/0c1d0db7664dd21d60456a222f446cfafd74dde61c6ae-QquZJq_fw658";
    /**
     * paint的setXfermode表示最终显示的图形取所绘制图形的交集，
     * 我这里先绘制了圆形，又绘制了一个矩形的Bitmap，圆形没有Bitmap大，所以交集肯定是圆形，
     * 所以最终显示结果就为圆形，在加载图片的时候可以通过transform属性来使用自定义的这个transformation
     */
    Transformation transformation = new Transformation() {
        @Override
        public Bitmap transform(Bitmap source) {
            int width = source.getWidth();
            int height = source.getHeight();
            int size = Math.min(width, height);
            Bitmap blankBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(blankBitmap);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            canvas.drawCircle(size / 2, size / 2, size / 2, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(source, 0, 0, paint);
            if (source != null && !source.isRecycled()) {
                source.recycle();
            }
            return blankBitmap;
        }

        @Override
        public String key() {
            return "square";
        }
    };
    private Button btn_loadNet, btn_loadLocal, btn_loadCircle;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_loadNet = findViewById(R.id.btn_loadNetPic);
        btn_loadLocal = findViewById(R.id.btn_loadLocalPic);
        btn_loadCircle = findViewById(R.id.btn_loadCirclePic);
        imageView = findViewById(R.id.imageview);
        btn_loadNet.setOnClickListener(this);
        btn_loadLocal.setOnClickListener(this);
        btn_loadCircle.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //resize() 设置图片裁剪尺寸；
            //placeholder(R.mipmap.loading) 加载过程中的占位符
            //error(R.mipmap.error)加载图片出错，显示的图片。
            // 如果重试3次（下载源代码可以根据需要修改）还是无法成功加载图片，则用错误占位符图片显示。
            //centerCrop()需要和resize在一起使用  否则会抛
            //ava.lang.IllegalStateException: Center crop requires calling resize with positive width and height.异常
            //fit()不可以和resize一起使用
            //图片加载过程设置一个监听器 CallBack(){}
            case R.id.btn_loadNetPic:
                Picasso.with(this).load(url)
                        //.resize(400, 400)
                        //.centerCrop()
                        .placeholder(R.mipmap.loading)
                        .error(R.mipmap.error)
                        .into(imageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                Log.d("LoadNet", "success");
                            }

                            @Override
                            public void onError() {
                                Log.d("LoadNet", "error");

                            }
                        });
                break;
            case R.id.btn_loadLocalPic:
                //加载资源文件
                Picasso picasso = Picasso.with(this);
                //开启指示器，可以看到这个图片是从内存加载来/的还是从SD卡加载来的还是从网络加载来的
                //红、蓝、绿三种颜色分别代表网络、SD卡和内存
                //  picasso.setIndicatorsEnabled(true);
                picasso.load(R.mipmap.revier).into(imageView);
                break;
            //加载圆形图片 需要定义一个transformation，然后对图片做二次处理
            case R.id.btn_loadCirclePic:
                Picasso.with(this).load(url)
                        .transform(transformation)
                        .into(imageView);
                //创建自定义的下载器  设置图片缓存位置
//                Picasso picasso1 = new Picasso.Builder(this)
//                        .downloader(new OkHttp3Downloader(this.getExternalCacheDir()))
//                        .build();
//                Picasso.setSingletonInstance(picasso1);
//                picasso1.load(url).into(imageView, new Callback() {
//                    @Override
//                    public void onSuccess() {
//                        Log.d("LoadNet", "success");
//                    }
//
//                    @Override
//                    public void onError() {
//                        Log.d("LoadNet", "error");
//                    }
//                });
                break;
            default:
                break;
        }
    }
}

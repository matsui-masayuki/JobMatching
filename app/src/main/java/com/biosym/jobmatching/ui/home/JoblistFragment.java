package com.biosym.jobmatching.ui.home;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.amplifyframework.core.Amplify;
import com.amplifyframework.storage.StorageItem;
import com.biosym.jobmatching.MainActivity;
import com.biosym.jobmatching.R;
import com.biosym.jobmatching.databinding.FragmentHomeBinding;
import com.biosym.jobmatching.databinding.FragmentJoblistBinding;
import com.biosym.jobmatching.ui.notifications.NotificationsFragment;

import java.io.IOException;

public class JoblistFragment extends Fragment {

    private FragmentJoblistBinding binding;
    private ImageView cityImage;
    private LinearLayout jobimagesLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_joblist, container, false);

        // 所属親アクティビティを取得
        MainActivity activity = (MainActivity) getActivity();
        // アクションバーにタイトルをセット
        activity.getSupportActionBar().setTitle("タイトル");
        // 戻るボタンを表示する
        activity.setupBackButton(true);
        // この記述でフラグメントでアクションバーメニューが使えるようになる
        this.setHasOptionsMenu(true);
        //binding = FragmentJoblistBinding.inflate(inflater, container, false);
        //View root = binding.getRoot();
        return view;
    }

    @Override
    public void onStart(){
        super.onStart();
        getCityimage("tokyo");
        getJobimage("tokyo");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    //都道府県の画像を取得
    private void getCityimage(String cityname){
        cityImage = getActivity().findViewById(R.id.cityImage2);
        Amplify.Storage.getUrl(
                cityname+"/"+cityname+".jpeg",
                result -> {
                    final Handler mainHandler = new Handler(Looper.getMainLooper());
                    try {
                        Bitmap bmp = BitmapFactory.decodeStream(result.getUrl().openConnection().getInputStream());
                        mainHandler.post(() -> {
                            cityImage.setImageBitmap(bmp);
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.i("MyAmplifyApp222", "Successfully generated: " + result.getUrl());

                },
                error -> Log.e("MplifyApp", "URL generation failure", error)
        );
    }

    //都道府県別の募集案件の画像を取得
    private void getJobimage(String cityname){
        jobimagesLayout = getActivity().findViewById(R.id.joblistView2);
        Amplify.Storage.list(cityname+"/job",
                result1 -> {
                    for (StorageItem item : result1.getItems()) {
                        //案件画像を全て取得
                        if(item.getKey().contains("jpeg")){
                            Amplify.Storage.getUrl(
                                    item.getKey(),
                                    result2 -> {
                                        final Handler mainHandler = new Handler(Looper.getMainLooper());
                                        try {
                                            Bitmap bmp = BitmapFactory.decodeStream(result2.getUrl().openConnection().getInputStream());
                                            mainHandler.post(() -> {
                                                ImageView iv = new ImageView(getActivity().getApplicationContext());
                                                // 画像のサイズを設定
                                                LinearLayout.LayoutParams layoutParams =
                                                        new LinearLayout.LayoutParams(500, 500);
                                                iv.setPadding(50,0,0,0);
                                                iv.setLayoutParams(layoutParams);
                                                iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                                iv.setImageBitmap(bmp);
                                                //タップしたら次の画面に遷移
                                                iv.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Log.i("MyAmplifyApp", "Successfully generated:");
                                                        replaceFragment(new NotificationsFragment());
                                                    }
                                                });
                                                jobimagesLayout.addView(iv);
                                            });
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    },
                                    error -> Log.e("MplifyApp", "URL generation failure", error)
                            );
                        }
                    }
                },
                error -> Log.e("MyAmplifyApp", "List failure", error)
        );
    }

    // 表示させるFragmentを切り替えるメソッドを定義（表示したいFragmentを引数として渡す）
    private void replaceFragment(Fragment fragment) {
        // フラグメントマネージャーの取得
        FragmentManager manager = this.getParentFragmentManager(); // アクティビティではgetSupportFragmentManager()?
        // フラグメントトランザクションの開始
        FragmentTransaction transaction = manager.beginTransaction();
        // レイアウトをfragmentに置き換え（追加）
        transaction.replace(R.id.nav_host_fragment_activity_main, fragment);
        // 置き換えのトランザクションをバックスタックに保存する
        transaction.addToBackStack(null);
        // フラグメントトランザクションをコミット
        transaction.commit();
    }

    // アクションバーボタンを押した時の処理
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            // android.R.id.homeで戻るボタン「←」を押した時の動作を検知
            case android.R.id.home:
                // 遷移前に表示していたFragmentに戻る処理を実行
                Log.i("MyAmplifyBC", "Successfully generated: ");
                FragmentManager manager = this.getParentFragmentManager();
                manager.popBackStack();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
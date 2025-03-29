package com.example.chatter_app;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class adaptor extends FragmentStateAdapter {
    public adaptor(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public adaptor(@NonNull Fragment fragment) {
        super(fragment);
    }

    public adaptor(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position)
        {
            case 0:
                chat_fragment one=new chat_fragment();
                return one;

            case 1:
                status_fragment second=new status_fragment();
                return second;
            default: return null;
        }

    }

    @Override
    public int getItemCount() {
        return 2;
    }
    public static CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "chat";  // Title for the first fragment
            case 1:
                return "status"; // Title for the second fragment
            default:
                return null;
        }
    }
}

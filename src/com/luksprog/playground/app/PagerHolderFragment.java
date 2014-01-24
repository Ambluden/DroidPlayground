/** Copyright 2014 Luksprog
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.luksprog.playground.app;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author Luksprog
 */
public class PagerHolderFragment extends Fragment {

    private static final int PAGER_ID = 0x1000;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewPager pager = new ViewPager(getActivity());
        pager.setId(PAGER_ID);
        pager.setAdapter(new ArticleAdapter(getChildFragmentManager()));
        return pager;
    }

    private static class ArticleAdapter extends FragmentPagerAdapter {

        public ArticleAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return ArticleFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return 4; // assume we have 4 fragments
        }
    }

}

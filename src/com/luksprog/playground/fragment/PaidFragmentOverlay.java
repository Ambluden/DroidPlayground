/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.luksprog.playground.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.luksprog.playground.R;

@SuppressLint("NewApi")
public class PaidFragmentOverlay extends Activity {

    boolean purchased = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frag_paidfragmentoverlay);
        getFragmentManager().beginTransaction()
                .add(R.id.container, new PaidFragment(), "PAID_FRAG").commit();
    }

    public void outsideInteraction(View v) {
        Toast.makeText(this, "Still clickable!!!", Toast.LENGTH_SHORT).show();
    }

    public void testClick(View v) {
        Toast.makeText(this, "Paid fragment click!!!", Toast.LENGTH_SHORT)
                .show();
    }

    public static class PaidFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            FrameLayout content = (FrameLayout) inflater.inflate(
                    R.layout.frag_paidfragment, container, false);
            if (!isContentLicensed()) {
                FrameLayout overlay = buildOverlay();
                content.addView(overlay, new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT));
                disableViews(content);
            }
            return content;
        }

        private FrameLayout buildOverlay() {
            FrameLayout overlay = new FrameLayout(getActivity());
            Button payApp = new Button(getActivity());
            payApp.setText("Pay up!!!");
            payApp.setBackgroundColor(Color.parseColor("#5599cc00"));
            overlay.addView(payApp, new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, 200,
                    Gravity.CENTER_VERTICAL));
            payApp.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(),
                            "The app has been paid! Enjoy!", Toast.LENGTH_SHORT)
                            .show();
                    setContentPurchased(true);
                    PaidFragment pf = (PaidFragment) getFragmentManager()
                            .findFragmentByTag("PAID_FRAG");
                    getFragmentManager()
                            .beginTransaction()
                            .remove(pf)
                            .add(R.id.container, new PaidFragment(),
                                    "PAID_FRAG").commit();
                }
            });
            overlay.setOnTouchListener(new OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return !isContentLicensed();
                }
            });
            return overlay;
        }

        private void setContentPurchased(boolean purchased) {
            ((PaidFragmentOverlay) getActivity()).purchased = purchased;
        }

        private boolean isContentLicensed() {
            return ((PaidFragmentOverlay) getActivity()).purchased;
        }

        private void disableViews(ViewGroup vg) {
            final int count = vg.getChildCount();
            for (int i = 0; i < count; i++) {
                final View child = vg.getChildAt(i);
                if (child instanceof ViewGroup) {
                    disableViews((ViewGroup) child);
                } else if (child instanceof View) {
                    child.setEnabled(false);
                }
            }
        }
    }

}

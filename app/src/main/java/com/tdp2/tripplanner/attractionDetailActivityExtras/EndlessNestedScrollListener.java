package com.tdp2.tripplanner.attractionDetailActivityExtras;

import android.support.v4.widget.NestedScrollView;

/**
 * Created by matias on 5/13/17.
 */

public class EndlessNestedScrollListener implements NestedScrollView.OnScrollChangeListener {

    private CommentsDownloader downloader;
    private float threshold;

    public EndlessNestedScrollListener(CommentsDownloader commentsDownloader){
        this.downloader = commentsDownloader;
        this.threshold = 0;
    }

    @Override
    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        if (scrollY > oldScrollY) {
            if (scrollY >= (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                this.downloader.getNextPage();
            }
        }
    }
}

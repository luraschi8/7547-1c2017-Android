package com.tdp2.tripplanner.ToursExtrasd;

import com.tdp2.tripplanner.modelo.Tour;


public class TourDataHolder {
    private static Tour tour;

    public static Tour getData() {
        return tour;
    }
    public static void setData(Tour tour) {
        TourDataHolder.tour = tour;
    }
}

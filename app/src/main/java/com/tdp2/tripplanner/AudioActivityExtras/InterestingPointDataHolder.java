package com.tdp2.tripplanner.AudioActivityExtras;

import com.tdp2.tripplanner.citySelectionActivityExtras.CityDataHolder;
import com.tdp2.tripplanner.modelo.City;
import com.tdp2.tripplanner.modelo.InterestingPoint;

/**
 * Created by Casa on 18/4/2017.
 */

public class InterestingPointDataHolder {

    private static InterestingPoint data;
    public static InterestingPoint getData() {return data;}
    public static void setData(InterestingPoint data) {
        InterestingPointDataHolder.data = data;
    }
}

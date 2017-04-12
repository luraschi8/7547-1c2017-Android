package com.tdp2.tripplanner.citySelectionActivityExtras;

import com.tdp2.tripplanner.modelo.City;

/**
 * Created by matias on 4/12/17.
 */

public class CityDataHolder {

    private static City data;
    public static City getData() {return data;}
    public static void setData(City data) {
        CityDataHolder.data = data;
    }
}

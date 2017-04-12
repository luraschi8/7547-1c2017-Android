package com.tdp2.tripplanner.attractionSelectionActivityExtras;

import com.tdp2.tripplanner.modelo.Attraction;
import com.tdp2.tripplanner.modelo.City;

/**
 * Created by matias on 4/12/17.
 */

public class AttractionDataHolder {
    private static Attraction data;
    public static Attraction getData() {return data;}
    public static void setData(Attraction data) {
        AttractionDataHolder.data = data;
    }

}

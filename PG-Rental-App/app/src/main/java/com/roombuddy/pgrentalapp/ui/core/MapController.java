package com.roombuddy.pgrentalapp.ui.core;

import com.roombuddy.pgrentalapp.model.PgModel;
import java.util.List;

public interface MapController {

    void showUserLocation(double lat, double lng);

    void showPgMarkers(List<PgModel> pgList);

    void moveCamera(double lat, double lng, float zoom);
}

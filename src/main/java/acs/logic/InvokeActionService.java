package acs.logic;

import acs.boundaries.ActionBoundary;
import acs.data.ActionEntity;

public interface InvokeActionService {
    public ActionEntity invokeAction(ActionBoundary action);

    public static final String USER_PREFERENCES = "userPreferences";
    public static final String USER_LOCATION = "userLocation";
    public static final String USER_ENVIRONMENT = "userEnvironment";
    public static final String LATITUDE = "lat";
    public static final String LONGITUDE = "lng";
    public static final String THEME = "theme";
    public static final String PREFERENCES = "Preferences";
    public static final String USER_PROPERTIES = "userProperties";
    public static final String RADIUS = "radius";
    public static final String USERS = "users";
    public static final String DISTANCE = "distance";
    public static final String DEFAULT_THEME = "Dark";
    String getType();
}

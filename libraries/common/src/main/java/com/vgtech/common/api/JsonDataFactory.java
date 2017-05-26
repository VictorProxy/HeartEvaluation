package com.vgtech.common.api;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonDataFactory {
    final static String LOG_TAG = "JsonDataFactory";
    private static final Map<String, Class<? extends AbsApiData>> MAP_OBJECTS;
    private static final Map<String, Class<? extends AbsApiData>> ARRAY_OBJECTS;

    static {
        MAP_OBJECTS = new HashMap<String, Class<? extends AbsApiData>>();


        ARRAY_OBJECTS = new HashMap<String, Class<? extends AbsApiData>>();
    }

    public static <T extends AbsApiData> T getData(Class<T> c, JSONObject json)
            throws InstantiationException, IllegalAccessException,
            IllegalArgumentException, JSONException {
        T data = c.newInstance();
        data.parser(json);
        if (!data.isValid()) {
            data = null;
        }

        return data;
    }

    public static AbsApiData getData(String key, JSONObject json) {
        final Class<? extends AbsApiData> c = MAP_OBJECTS.get(key);
        if (c == null) {
            return null;
        }

        AbsApiData data = null;
        try {
            data = getData(c, json);
        } catch (Exception e) {
        }

        return data;
    }

    static Class<? extends AbsApiData> getArrayClass(String key) {
        return ARRAY_OBJECTS.get(key);
    }

    static List<AbsApiData> getDataArray(String key, JSONArray jsonArray) {
        final Class<? extends AbsApiData> c = ARRAY_OBJECTS.get(key);
        if (c == null) {
            return new ArrayList<AbsApiData>();
        }

        final List<AbsApiData> list = new ArrayList<AbsApiData>();
        final int count = jsonArray.length();
        for (int i = 0; i < count; i++) {
            try {
                JSONObject json = jsonArray.getJSONObject(i);
                AbsApiData data = getData(c, json);
                if (data != null) {
                    list.add(data);
                }
            } catch (Exception e) {
            }
        }

        return list;
    }

    public static <T extends AbsApiData> List<T> getDataArray(Class<T> c,
                                                              JSONArray jsonArray) {
        if (c == null) {
            return new ArrayList<T>();
        }

        final List<T> list = new ArrayList<T>();
        final int count = jsonArray.length();
        for (int i = 0; i < count; i++) {
            try {
                JSONObject json = jsonArray.getJSONObject(i);
                T data = getData(c, json);
                if (data != null) {
                    list.add(data);
                }
            } catch (Exception e) {
            }
        }

        return list;
    }

    public static RootData getData(JSONObject json) {
        RootData data = null;
        try {
            data = getData(RootData.class, json);
        } catch (Exception e) {
        }
        return data;
    }

    public static RootData getDataOrThrow(JSONObject json)
            throws IllegalArgumentException, InstantiationException,
            IllegalAccessException, JSONException {
        return getData(RootData.class, json);
    }

//    /**
//     * jackson
//     *
//     * @param jsonArray
//     * @return
//     */
//    public static TreeSet<NewStaff> getDataTreeSet(JSONArray jsonArray) {
//        final TreeSet<NewStaff> list = new TreeSet<>();
//
//        final int count = jsonArray.length();
//        for (int i = 0; i < count; i++) {
//            try {
//                JSONObject json = jsonArray.getJSONObject(i);
//                NewStaff data = getData(NewStaff.class, json);
//                if (data != null) {
//                    list.add(data);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        return list;
//    }


}

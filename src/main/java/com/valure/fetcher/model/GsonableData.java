package com.valure.fetcher.model;

import com.google.gson.Gson;

public abstract class GsonableData {

    private static class GsonContainer {

        private static final Gson gson = new Gson();

        public static synchronized String toJson(final Object obj) {
            return GsonContainer.gson.toJson(obj);
        }

    }

    /*
     * Override the toString() method to parse using Gson.
     */
    @Override
    public String toString() {
        return GsonContainer.toJson(this);
    }

}

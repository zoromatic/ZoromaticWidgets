package com.zoromatic.widgets;

public class WidgetRowItem {
    private int appWidgetId;
    private String className;

    public WidgetRowItem(int appWidgetId, String className) {
        this.appWidgetId = appWidgetId;
        this.className = className;
    }

    public int getAppWidgetId() {
        return appWidgetId;
    }

    public void setAppWidgetId(int appWidgetId) {
        this.appWidgetId = appWidgetId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}

package com.tui.qa.models;

public class SearchData {

    private String hotelsTabLabel;
    private String holidaysTabLabel;
    private String priceRegex;

    public String getHotelsTabLabel() {
        return hotelsTabLabel;
    }

    public void setHotelsTabLabel(String hotelsTabLabel) {
        this.hotelsTabLabel = hotelsTabLabel;
    }

    public String getHolidaysTabLabel() {
        return holidaysTabLabel;
    }

    public void setHolidaysTabLabel(String holidaysTabLabel) {
        this.holidaysTabLabel = holidaysTabLabel;
    }

    public String getPriceRegex() {
        return priceRegex;
    }

    public void setPriceRegex(String priceRegex) {
        this.priceRegex = priceRegex;
    }
}

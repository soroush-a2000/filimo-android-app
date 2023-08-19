package com.movieboxtv.app.cast;

import androidx.annotation.ColorInt;

import java.io.Serializable;

public class ExpandedControlsStyle implements Serializable {
    @ColorInt private int seekbarLineColor;
    @ColorInt private int seekbarThumbColor;
    @ColorInt private int statusTextColor;

    private ExpandedControlsStyle() { /* no-op */ }

    public int getSeekbarLineColor() {
        return seekbarLineColor;
    }

    public int getSeekbarThumbColor() {
        return seekbarThumbColor;
    }

    public int getStatusTextColor() {
        return statusTextColor;
    }

    public static class Builder {
        private ExpandedControlsStyle style;

        public Builder() {
            this.style = new ExpandedControlsStyle();
        }

        /**
         * Set the expanded controls seekbar line color
         * @param color A @ColorInt color.
         * @return this instance for chain calls
         */
        public Builder setSeekbarLineColor(@ColorInt int color) {
            style.seekbarLineColor = color;
            return this;
        }

        /**
         * Set the expanded controls seekbar thumb color
         * @param color A @ColorInt color.
         * @return this instance for chain calls
         */
        public Builder setSeekbarThumbColor(@ColorInt int color) {
            style.seekbarThumbColor = color;
            return this;
        }

        /**
         * Set the expanded controls status text (connected to...) color
         * @param color A @ColorInt color.
         * @return this instance for chain calls
         */
        public Builder setStatusTextColor(@ColorInt int color) {
            style.statusTextColor = color;
            return this;
        }

        public ExpandedControlsStyle build() {
            return this.style;
        }
    }
}

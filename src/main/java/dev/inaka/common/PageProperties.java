package dev.inaka.common;

import dev.inaka.common.exceptions.MarginMalformedException;
import dev.inaka.common.exceptions.PageRangeMalformedException;

import java.io.IOException;


public final class PageProperties {
    private final float paperWidth;
    private final float paperHeight;
    private final float marginTop;
    private final float marginBottom;
    private final float marginLeft;
    private final float marginRight;
    private final boolean preferCssPageSize;

    private final boolean printBackground;
    private final boolean landscape;
    private final float scale;
    private final String nativePageRanges;
    private final String pdfFormat;
    private final String nativePdfFormat;

    private PageProperties(Builder builder) {
        paperWidth = builder.paperWidth;
        paperHeight = builder.paperHeight;
        marginTop = builder.marginTop;
        marginBottom = builder.marginBottom;
        marginLeft = builder.marginLeft;
        marginRight = builder.marginRight;
        preferCssPageSize = builder.preferCssPageSize;
        printBackground = builder.printBackground;
        landscape = builder.landscape;
        scale = builder.scale;
        nativePageRanges = builder.nativePageRanges;
        pdfFormat = builder.pdfFormat;
        nativePdfFormat = builder.nativePdfFormat;

    }

    public static class Builder {
        private float paperWidth = 8.5f;
        private float paperHeight = 11f;
        private float marginTop = 0.39f;
        private float marginBottom = 0.39f;
        private float marginLeft = 0.39f;
        private float marginRight = 0.39f;
        private boolean preferCssPageSize = false;
        private boolean printBackground = false;
        private boolean landscape = false;
        private float scale = 1f;
        private String nativePageRanges = "";
        private String pdfFormat = "";

        private String nativePdfFormat = PdfFormat.A_1A.format();

        public Builder addPaperWidth(float paperWidth) throws IOException {
            if (paperWidth < 1.0f) {
                throw new IOException("Width is smaller than the minimum printing requirements (1.0 in)");
            }
            this.paperWidth = paperWidth;
            return this;
        }

        public Builder addPaperHeight(float paperHeight) throws IOException {
            if (paperHeight < 1.5f) {
                throw new IOException("paperHeight is smaller than the minimum printing requirements (1.5 in)");
            }
            this.paperHeight = paperHeight;
            return this;
        }

        public Builder addMarginTop(float marginTop) throws MarginMalformedException {
            if (marginTop < 0f) {
                throw new MarginMalformedException();
            }
            this.marginTop = marginTop;
            return this;
        }

        public Builder addMarginBottom(float marginBottom) throws MarginMalformedException {
            if (marginBottom < 0f) {
                throw new MarginMalformedException();
            }
            this.marginBottom = marginBottom;
            return this;
        }

        public Builder addMarginLeft(float marginLeft) throws MarginMalformedException {
            if (marginLeft < 0f) {
                throw new MarginMalformedException();
            }
            this.marginLeft = marginLeft;
            return this;
        }

        public Builder addMarginRight(float marginRight) throws MarginMalformedException {
            if (marginRight < 0f) {
                throw new MarginMalformedException();
            }

            this.marginRight = marginRight;
            return this;
        }

        public Builder addPreferCssPageSize(boolean preferCssPageSize) {
            this.preferCssPageSize = preferCssPageSize;
            return this;
        }

        public Builder addPrintBackground(boolean printBackground) {
            this.printBackground = printBackground;
            return this;
        }

        public Builder addLandscape(boolean landscape) {
            this.landscape = landscape;
            return this;
        }

        public Builder addScale(float scale) {
            this.scale = scale;
            return this;
        }

        public Builder addNativePageRanges(int start, int end) throws PageRangeMalformedException {
            if (start < 0 || end < 0 || end > start) {
                throw new PageRangeMalformedException();
            }
            this.nativePageRanges = start + "-" + end;
            return this;
        }

        public Builder addPdfFormat(String pdfFormat) {
            this.pdfFormat = pdfFormat;
            return this;
        }

        public PageProperties build() {
            return new PageProperties(this);
        }
    }

    public float getPaperWidth() {
        return paperWidth;
    }

    public float getPaperHeight() {
        return paperHeight;
    }

    public float getMarginTop() {
        return marginTop;
    }

    public float getMarginBottom() {
        return marginBottom;
    }

    public float getMarginLeft() {
        return marginLeft;
    }

    public float getMarginRight() {
        return marginRight;
    }

    public boolean isPreferCssPageSize() {
        return preferCssPageSize;
    }

    public boolean isPrintBackground() {
        return printBackground;
    }

    public boolean isLandscape() {
        return landscape;
    }

    public float getScale() {
        return scale;
    }

    public String getNativePageRanges() {
        return nativePageRanges;
    }

    public String getPdfFormat() {
        return pdfFormat;
    }

    public String getNativePdfFormat() {
        return nativePdfFormat;
    }
}


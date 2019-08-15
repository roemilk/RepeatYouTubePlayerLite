package com.venuskimblessing.tuberepeatfree.Json;

import java.util.ArrayList;

public class YoutubeSearchList {
    public String kind;
    public String etag;
    public String nextPageToken;
    public String regionCode;
    public PageInfo pageInfo;
    public ArrayList<ItemsItem> items;

    public class PageInfo {
        public String totalResults;
        public String resultsPerPage;
    }

    public class ItemsItem{
        public String kind;
        public String etag;
        public Id id;
        public Snippet snippet;
    }

    public class Id{
        public String kind;
        public String videoId;
    }

    public class Snippet{
        public String publishedAt;
        public String channelId;
        public String title;
        public String description;
        private ThumNails thumbnails;

        public ThumNails getThumbnails() {
            return thumbnails;
        }

        public void setThumbnails(ThumNails thumbnails) {
            this.thumbnails = thumbnails;
        }
    }

    public class ThumNails{
        private Medium medium;

        public Medium getMedium() {
            return medium;
        }

        public void setMedium(Medium medium) {
            this.medium = medium;
        }

        public class Medium{
            private String url;
            private String width;
            private String height;

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getWidth() {
                return width;
            }

            public void setWidth(String width) {
                this.width = width;
            }

            public String getHeight() {
                return height;
            }

            public void setHeight(String height) {
                this.height = height;
            }
        }
    }
}

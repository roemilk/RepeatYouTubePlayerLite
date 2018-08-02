package com.venuskimblessing.youtuberepeatlite.Json;

import java.util.ArrayList;

public class Videos {
    public String kind;
    public String etag;
    public PageInfo pageInfo;

    public ArrayList<Item> items;

    public class PageInfo {
        public String totalResults;
        public String resultsPerPage;
    }

    public class Item{
        public String kind;
        public String etag;
        public String id;

        public Snippet snippet;
        public ContentDetails contentDetails;
        public statistics statistics;
    }

    public class Snippet{
        public String publishedAt;
        public String channelId;
        public String channelTitle;
        public String title;
        public String description;
        private YoutubeSearchList.ThumNails thumbnails;

        public YoutubeSearchList.ThumNails getThumbnails() {
            return thumbnails;
        }

        public void setThumbnails(YoutubeSearchList.ThumNails thumbnails) {
            this.thumbnails = thumbnails;
        }
    }

    public class ThumNails{
        private YoutubeSearchList.ThumNails.Medium medium;

        public YoutubeSearchList.ThumNails.Medium getMedium() {
            return medium;
        }

        public void setMedium(YoutubeSearchList.ThumNails.Medium medium) {
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

    public class ContentDetails{
        public String duration;
        public String dimension;
        public String definition;
    }

    public class statistics{
        public String viewCount;
        public String likeCount;
    }
}

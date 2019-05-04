package com.venuskimblessing.youtuberepeatfree.Json;

import java.util.ArrayList;

public class SearchList {
    private String nextPageToken;
    private ArrayList<ItemsItem> items = new ArrayList<>();

    public String getNextPageToken() {
        return nextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

    public ArrayList<ItemsItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<ItemsItem> items) {
        this.items = items;
    }

    public class ItemsItem{
        private String kind;
        private String videoId;
        private String title;
        private String description;
        private String thumbnails_url;
        private String thumbnails_width;
        private String thumbnails_height;
        private String duration;
        private String channelTitle;
        private String viewCount;

        public String getChannelTitle() {
            return channelTitle;
        }

        public void setChannelTitle(String channelTitle) {
            this.channelTitle = channelTitle;
        }

        public String getViewCount() {
            return viewCount;
        }

        public void setViewCount(String viewCount) {
            this.viewCount = viewCount;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getKind() {
            return kind;
        }

        public void setKind(String kind) {
            this.kind = kind;
        }

        public String getVideoId() {
            return videoId;
        }

        public void setVideoId(String videoId) {
            this.videoId = videoId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getThumbnails_url() {
            return thumbnails_url;
        }

        public void setThumbnails_url(String thumbnails_url) {
            this.thumbnails_url = thumbnails_url;
        }

        public String getThumbnails_width() {
            return thumbnails_width;
        }

        public void setThumbnails_width(String thumbnails_width) {
            this.thumbnails_width = thumbnails_width;
        }

        public String getThumbnails_height() {
            return thumbnails_height;
        }

        public void setThumbnails_height(String thumbnails_height) {
            this.thumbnails_height = thumbnails_height;
        }
    }
}

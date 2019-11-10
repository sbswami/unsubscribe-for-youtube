package com.sanshy.unsubscribeforyoutube;

public class SingleUnsubscribeList {

    String PhotoURL;
    String ChannelName;
    long VideosCount;
    long subscriberCount;
    String SubscriptionId;

    public SingleUnsubscribeList(String photoURL, String channelName, long VideosCount, long subscriberCount, String subscriptionId) {
        PhotoURL = photoURL;
        ChannelName = channelName;
        this.VideosCount = VideosCount;
        this.subscriberCount = subscriberCount;
        SubscriptionId = subscriptionId;
    }

    public String getPhotoURL() {
        return PhotoURL;
    }

    public void setPhotoURL(String photoURL) {
        PhotoURL = photoURL;
    }

    public String getChannelName() {
        return ChannelName;
    }

    public void setChannelName(String channelName) {
        ChannelName = channelName;
    }

    public long getVideosCount() {
        return VideosCount;
    }

    public void setVideosCount(long videosCount) {
        this.VideosCount = videosCount;
    }

    public long getSubscriberCount() {
        return subscriberCount;
    }

    public void setSubscriberCount(long subscriberCount) {
        this.subscriberCount = subscriberCount;
    }

    public String getSubscriptionId() {
        return SubscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {
        SubscriptionId = subscriptionId;
    }
}

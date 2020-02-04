package com.sit.update;

public class News {
    private String Title,url,desc,image;
    private Long createdOn;
    public News(String Title,String desc,String image,String url,Long createdOn){
        this.Title=Title;
        this.url=url;
        this.desc=desc;
        this.image=image;
        this.createdOn=createdOn;
    }
    public News(){

    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getUrl() {
        return url;
    }

    public Long getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Long createdOn) {
        this.createdOn = createdOn;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}

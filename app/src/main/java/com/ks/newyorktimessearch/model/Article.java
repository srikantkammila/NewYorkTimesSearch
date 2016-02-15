package com.ks.newyorktimessearch.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by skammila on 2/8/16.
 */
public class Article {
    private String webUrl;
    private String snippet;
    private String leadParagraph;
    private String section;
    private String subSection;
    private String headline;
    private String publishDate;
    private String reportedBy;
    private List<Media> media;

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public String getLeadParagraph() {
        return leadParagraph;
    }

    public void setLeadParagraph(String leadParagraph) {
        this.leadParagraph = leadParagraph;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getSubSection() {
        return subSection;
    }

    public void setSubSection(String subSection) {
        this.subSection = subSection;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(String reportedBy) {
        this.reportedBy = reportedBy;
    }

    public List<Media> getMedia() {
        return media;
    }

    public void setMedia(List<Media> media) {
        this.media = media;
    }

    public Media getThumbnailMedia() {
        if (media != null && media.size() > 0) {
            for (int i=0; i<media.size(); i++) {
                if (media.get(i).getSubtype().equalsIgnoreCase("thumbnail")) {
                    return media.get(i);
                }
            }
        }

        return null;
    }

    public Media getXLargeMedia() {
        if (media != null && media.size() > 0) {
            for (int i=0; i<media.size(); i++) {
                if (media.get(i).getSubtype().equalsIgnoreCase("xlarge")) {
                    return media.get(i);
                }
            }
        }

        return null;
    }

    public Media getWideMedia() {
        if (media != null && media.size() > 0) {
            for (int i=0; i<media.size(); i++) {
                if (media.get(i).getSubtype().equalsIgnoreCase("wide")) {
                    return media.get(i);
                }
            }
        }

        return null;
    }

    public Media getRVMedia() {
        Media wMd = getWideMedia();
        Media xlMd = getXLargeMedia();
//        Media tnMd = getThumbnailMedia();
        return wMd != null ? wMd : (xlMd != null ? xlMd : null);
    }


    public static Article parseArticle(JSONObject obj) {
        Article article = new Article();

        try{
            article.setWebUrl(obj.getString("web_url"));
            if (obj.optJSONObject("headline") != null && obj.getJSONObject("headline").optString("main") != null) {
                article.setHeadline(obj.getJSONObject("headline").getString("main"));
            }
            article.setLeadParagraph(obj.getString("lead_paragraph"));
            article.setSnippet(obj.getString("snippet"));
            article.setPublishDate(obj.getString("pub_date"));
            article.setSection(obj.getString("section_name"));
            article.setSubSection(obj.getString("subsection_name"));
            if (obj.optJSONObject("byline") != null && obj.getJSONObject("byline").optString("original") != null) {
                article.setReportedBy(obj.getJSONObject("byline").getString("original"));
            }

            JSONArray multimedia = obj.optJSONArray("multimedia") != null ? obj.getJSONArray("multimedia") : null;
            if (multimedia != null && multimedia.length() > 0) {
                List<Media> media = new ArrayList<Media>();
                for(int i =0; i < multimedia.length(); i++) {
                    Media md = new Media();
                    md.setUrl(multimedia.getJSONObject(i).getString("url"));
                    md.setWidth(multimedia.getJSONObject(i).getInt("width"));
                    md.setHeight(multimedia.getJSONObject(i).getInt("height"));
                    md.setSubtype(multimedia.getJSONObject(i).getString("subtype"));
                    media.add(md);
                }
                article.setMedia(media);
            }
        } catch (JSONException e){
            Log.e("ERROR", e.getLocalizedMessage());
            e.printStackTrace();
        }



        return article;
    }

    public static List<Article> articlesFromJsonArray(JSONArray array) {
        List<Article> articles = new ArrayList<>();
        try{
            for (int i=0; i<array.length(); i++) {
                articles.add(parseArticle(array.getJSONObject(i)));
            }
        } catch (JSONException e){
            Log.e("ERROR", e.getLocalizedMessage());
            e.printStackTrace();
        }

        return articles;
    }
}

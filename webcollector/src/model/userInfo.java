package model;

import java.util.List;

public class userInfo {
private String id;
private String filehtml;
private String  htmls;
private String urls ;
private String title;
private String nowdtime;
private int numall;
private int pagesize;
private List<String> WORLDS;

public String getHtmls() {
	return htmls;
}
public void setHtmls(String htmls) {
	this.htmls = htmls;
}
public String getFilehtml() {
	return filehtml;
}
public void setFilehtml(String filehtml) {
	this.filehtml = filehtml;
}
public int getPagesize() {
	return pagesize;
}
public void setPagesize(int pagesize) {
	this.pagesize = pagesize;
}
public int getNumall() {
	return numall;
}
public void setNumall(int numall) {
	this.numall = numall;
}

public String getUrls() {
	return urls;
}
public void setUrls(String urls) {
	this.urls = urls;
}
public String getNowdtime() {
	return nowdtime;
}
public void setNowdtime(String nowdtime) {
	this.nowdtime = nowdtime;
}

public List<String> getWORLDS() {
	return WORLDS;
}
public void setWORLDS(List<String> wORLDS) {
	WORLDS = wORLDS;
}
public String getId() {
	return id;
}
public void setId(String id) {
	this.id = id;
}

public String getTitle() {
	return title;
}
public void setTitle(String title) {
	this.title = title;
}






}

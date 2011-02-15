
public class ResultObject {
	
	public ResultObject(String title, String url, String summary){
		this.title = title;
		this.url = url;
		this.summary = summary;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	
	public String toString(){
		return "URL:" + url+ "\n" + "Title:"+title + "\n" + "Summary:" + summary +"\n"; 
	}
	
	private String title;
	private String url;
	private String summary;
}

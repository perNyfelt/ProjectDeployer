package se.alipsa.pd.model;

/**
 * The full json content is
 *       "url": "https://api.github.com/repos/perNyfelt/munin/releases/assets/32220156",
 *       "id": 32220156,
 *       "node_id": "MDEyOlJlbGVhc2VBc3NldDMyMjIwMTU2",
 *       "name": "munin-1.1.2-exec.jar",
 *       "label": null,
 *       "uploader": {
 *         "login": "perNyfelt",
 *         "id": 13261538,
 *         "node_id": "MDQ6VXNlcjEzMjYxNTM4",
 *         "avatar_url": "https://avatars.githubusercontent.com/u/13261538?v=4",
 *         "gravatar_id": "",
 *         "url": "https://api.github.com/users/perNyfelt",
 *         "html_url": "https://github.com/perNyfelt",
 *         "followers_url": "https://api.github.com/users/perNyfelt/followers",
 *         "following_url": "https://api.github.com/users/perNyfelt/following{/other_user}",
 *         "gists_url": "https://api.github.com/users/perNyfelt/gists{/gist_id}",
 *         "starred_url": "https://api.github.com/users/perNyfelt/starred{/owner}{/repo}",
 *         "subscriptions_url": "https://api.github.com/users/perNyfelt/subscriptions",
 *         "organizations_url": "https://api.github.com/users/perNyfelt/orgs",
 *         "repos_url": "https://api.github.com/users/perNyfelt/repos",
 *         "events_url": "https://api.github.com/users/perNyfelt/events{/privacy}",
 *         "received_events_url": "https://api.github.com/users/perNyfelt/received_events",
 *         "type": "User",
 *         "site_admin": false
 *       },
 *       "content_type": "application/x-java-archive",
 *       "state": "uploaded",
 *       "size": 106576225,
 *       "download_count": 0,
 *       "created_at": "2021-02-17T20:25:47Z",
 *       "updated_at": "2021-02-17T20:25:51Z",
 *       "browser_download_url": "https://github.com/perNyfelt/munin/releases/download/v1.1.2/munin-1.1.2-exec.jar"
 *
 */
public class GitHubAsset {

  String name;
  String contentType;
  String size;
  String url;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getContentType() {
    return contentType;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public String getSize() {
    return size;
  }

  public void setSize(String size) {
    this.size = size;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  @Override
  public String toString() {
    return "GitHubAsset{" +
        "name='" + name + '\'' +
        ", contentType='" + contentType + '\'' +
        ", size='" + size + '\'' +
        ", url='" + url + '\'' +
        '}';
  }
}

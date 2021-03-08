package se.alipsa.pd.util

import groovy.json.*
import se.alipsa.pd.model.GitHubAsset

class GitHub {

    /**
     * 1. get json from "https://api.github.com/repos/${owner}/${project}/releases/latest"
     * 2. get the assets list
     * 3. populate GitHubAssets with the content
    */
    static List<GitHubAsset> latestAssets(String owner, String project) {
        def jsonSlurper = new JsonSlurper()
        URL url = new URL("https://api.github.com/repos/${owner}/${project}/releases/latest")
        def json = jsonSlurper.parse(url)
        def assets = json.assets
        List<GitHubAsset> assetList = new ArrayList<>(assets.size())
        for (asset in assets) {
            def gha = new GitHubAsset()
            gha.name = asset.name
            gha.contentType = asset.content_type
            gha.size = asset.size
            gha.url = asset.browser_download_url
            assetList.add(gha)
        }
        return assetList
    }

    /**
     * get latest release from "https://api.github.com/repos/${owner}/${project}/releases/latest"
     * @param owner
     * @param project
     * @param suffix check for the name and if it end with the suffix or not
     * @return the browser_download_url value for the one matching
     */
    static String latestRelease(String owner, String project, String suffix) {
        List<GitHubAsset> assets = latestAssets(owner, project)
        for (asset in assets) {
            if (asset.name.endsWith(suffix)) {
                return asset.url
            }
        }
        return null
    }
}

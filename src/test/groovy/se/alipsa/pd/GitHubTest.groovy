package se.alipsa.pd

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import se.alipsa.pd.model.GitHubAsset
import se.alipsa.pd.util.GitHub

import static org.junit.jupiter.api.Assertions.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GitHubTest {

  private static final Logger LOG = LogManager.getLogger(GitHubTest.class)
  @Test
  void testLatestAssets() {
    List<GitHubAsset> assets = GitHub.latestAssets("perNyfelt", "Munin")
    LOG.info("got ${assets.size()} assets")
    assertTrue(assets.size() > 0)
  }

  @Test
  void testLatestRelease() {
    String url = GitHub.latestRelease("perNyfelt", "Munin", "exec.jar")
    LOG.info("latest release is ${url}")
    assertNotNull(url)
  }
}

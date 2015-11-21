package edu.hit.yh.gitdata.test;

import org.junit.Test;

import edu.hit.yh.gitdata.githubDataAnalyzer.HtmlAnalyzer;
import edu.hit.yh.gitdata.githubDataAnalyzer.TargetFinder;

public class HtmlAnalyzerTest {

	@Test
	public void testGetCommitCommentEventBodyByUrlAndCommentId() {
		
		HtmlAnalyzer htmlAnalyzer = new HtmlAnalyzer();
		TargetFinder targetFinder = new TargetFinder();
		String url = "https://github.com/jquery/jquery/commit/c752a5030bc00eb5b45dea9c28963f824a5c4f44";
		targetFinder.findTargetAmongArtifactsByUrl(url);
		/*String url = "https://github.com/jquery/jquery/commit/727f86cab93a82eb9b13c2f6407b52e8a4fc2250#commitcomment-2376472";
		htmlAnalyzer.getCommitCommentEventBodyByUrlAndCommentId(url, "2376472");
		htmlAnalyzer.getCommitCommentEventCommitIdByUrl(url);*/
		
	}

}

//package crawler;
//
///**
// * Licensed to DigitalPebble Ltd under one or more
// * contributor license agreements.  See the NOTICE file distributed with
// * this work for additional information regarding copyright ownership.
// * DigitalPebble licenses this file to You under the Apache License, Version 2.0
// * (the "License"); you may not use this file except in compliance with
// * the License.  You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//import com.digitalpebble.stormcrawler.*;
//import com.digitalpebble.stormcrawler.bolt.FetcherBolt;
//import com.digitalpebble.stormcrawler.bolt.JSoupParserBolt;
//import com.digitalpebble.stormcrawler.bolt.SiteMapParserBolt;
//import com.digitalpebble.stormcrawler.bolt.URLPartitionerBolt;
//import com.digitalpebble.stormcrawler.indexing.StdOutIndexer;
//import com.digitalpebble.stormcrawler.persistence.StdOutStatusUpdater;
//import com.digitalpebble.stormcrawler.spout.MemorySpout;
//
//import org.apache.storm.topology.TopologyBuilder;
//import org.apache.storm.tuple.Fields;
//
///**
// * Dummy topology to play with the spouts and bolts
// */
//public class CrawlTopology extends ConfigurableTopology {
//
//    public static void main(String[] args) throws Exception {
//        ConfigurableTopology.start(new CrawlTopology(), args);
//    }
//
//    @Override
//    protected int run(String[] args) {
//        TopologyBuilder builder = new TopologyBuilder();
//
//        String[] testURLs = new String[] { "http://www.lequipe.fr/",
//                "http://www.lemonde.fr/", "http://www.bbc.co.uk/",
//                "http://storm.apache.org/", "http://digitalpebble.com/" };
//
//        builder.setSpout("spout", new MemorySpout(testURLs));
//
//        builder.setBolt("partitioner", new URLPartitionerBolt())
//                .shuffleGrouping("spout");
//
//        builder.setBolt("fetch", new FetcherBolt()).fieldsGrouping(
//                "partitioner", new Fields("key"));
//
//        builder.setBolt("sitemap", new SiteMapParserBolt())
//                .localOrShuffleGrouping("fetch");
//
//        builder.setBolt("parse", new JSoupParserBolt()).localOrShuffleGrouping(
//                "sitemap");
//
//        builder.setBolt("index", new StdOutIndexer()).localOrShuffleGrouping(
//                "parse");
//
//        Fields furl = new Fields("url");
//
//        // can also use MemoryStatusUpdater for simple recursive crawls
//        builder.setBolt("status", new StdOutStatusUpdater())
//                .fieldsGrouping("fetch", Constants.StatusStreamName, furl)
//                .fieldsGrouping("sitemap", Constants.StatusStreamName, furl)
//                .fieldsGrouping("parse", Constants.StatusStreamName, furl)
//                .fieldsGrouping("index", Constants.StatusStreamName, furl);
//
//        return submit("crawl", conf, builder);
//    }
//}
//
package crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
// import exception and collection classes
import java.io.IOException;
import java.util.HashSet;
public class CrawlTopology  {

    private HashSet<String> urlLink = new HashSet<>();

    public static void main(String[] args)  {
        CrawlTopology obj = new CrawlTopology();

        obj.getPageLinks("https://www.javatpoint.com/digital-electronics");

    }

    public void getPageLinks(String URL) {

        if (!urlLink.contains(URL)) {
            try {

                if (urlLink.add(URL)) {
                    System.out.println(URL);
                }

                Document doc = Jsoup.connect(URL).get();

                Elements availableLinksOnPage = doc.select("a[href]");

                for (Element ele : availableLinksOnPage) {

                    getPageLinks(ele.attr("abs:href"));
                }
            }

            catch (IOException e) {

                System.err.println("For '" + URL + "': " + e.getMessage());
            }
        }
    }
}

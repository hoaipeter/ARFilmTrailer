package com.AR.Video;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

public class SecondActivity extends Activity {
    private String rssUrl = null;
    private ListView listView;
    private ArrayList listNews;
    private NewsAdapter adapter;

    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Intent intent = getIntent();
        rssUrl = intent.getStringExtra("link");
        listView = (ListView)findViewById(R.id.list_view);
        listNews = new ArrayList();
        adapter = new NewsAdapter(this);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                News news = adapter.getItem(i);
                Intent intent = new Intent(SecondActivity.this, DetailActivity.class);
                intent.putExtra("link", news.getLink());
                startActivity(intent);
                Log.d("link", news.getLink());
            }
        });

        showLoadingDialog("Loading data. Please wait a little");
        new XMLReader().execute(rssUrl);
    }


    private class XMLReader extends AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            listNews = new ArrayList();

            XMLDOMParser parser = new XMLDOMParser();
            Document doc = parser.getDocument(s);
            NodeList nodeList = doc.getElementsByTagName("item");
            ;
            for (int i = 0; i < nodeList.getLength(); i++) {
                News news = new News();
                Element e = (Element)nodeList.item(i);
                NodeList title = e.getElementsByTagName("title");
                Element titleElement = (Element)title.item(0);
                news.setTitle(titleElement.getFirstChild().getNodeValue());

                Node currentNode = nodeList.item(i);
                NodeList nchild = currentNode.getChildNodes();
                int clength = nchild.getLength();
                for (int j = 1; j < clength; j = j + 2) {

                    Node thisNode = nchild.item(j);
                    String nodeContent = null;
                    String nodeName = thisNode.getNodeName();
                    if (nodeName.equals("enclosure")) {
                        Element e1 = (Element) nchild.item(j);
                        nodeContent = ((Element) nchild.item(j)).getAttribute("url");
                        news.setImageLink(nodeContent);
                        //Toast.makeText(SecondActivity.this, nodeContent, Toast.LENGTH_SHORT).show();
                    }
                }

                NodeList link = e.getElementsByTagName("link");
                Element linkElement = (Element)link.item(0);
                news.setLink(linkElement.getFirstChild().getNodeValue());

                listNews.add(news);
            }
            adapter.addAll(listNews);
            progressDialog.dismiss();
        }

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            result = getXmlFromUrl(strings[0]);
            return result;
        }
    }

    private String getXmlFromUrl(String string) {
        String result = "";

        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(string);
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            result = EntityUtils.toString(httpEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private void showLoadingDialog(String message) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
}
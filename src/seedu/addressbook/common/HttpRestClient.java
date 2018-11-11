//@@author andyrobert3
package seedu.addressbook.common;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;


/**
 * Http Client needed for RESTful APIs. Currently, only GET request is supported.
 */
public class HttpRestClient {
    private HttpClient httpClient;

    public HttpRestClient() {
        httpClient = HttpClientBuilder.create().build();
    }

    /**
     * TODO: Add Javadoc comment
     * @param url
     * @return
     * @throws IOException
     */
    public HttpResponse requestGetResponse(String url) throws IOException {
        HttpGet request = new HttpGet(url);
        return httpClient.execute(request);
    }
}

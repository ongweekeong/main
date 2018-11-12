//@@author andyrobert3
package seedu.addressbook.common;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;


/**
 * Http Client needed for RESTful APIs
 */
public class HttpRestClient {
    private HttpClient httpClient;

    public HttpRestClient() {
        httpClient = HttpClientBuilder.create().build();
    }

    /**
     * Sends HTTP GET Request to specified URL
     * @param url
     * @return HttpResponse from GET Request
     * @throws IOException
     */
    public HttpResponse requestGetResponse(String url) throws IOException {
        HttpGet request = new HttpGet(url);
        return httpClient.execute(request);
    }
}

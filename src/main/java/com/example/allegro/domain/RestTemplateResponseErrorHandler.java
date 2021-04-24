package com.example.allegro.domain;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {
    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {

        return response.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR;

    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        if (response.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR) {
            // handle SERVER_ERROR
        } else if (response.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR) {
            // handle CLIENT_ERROR
            if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                System.out.println("ERRRRRRRRRRRRRRr");
                throw new UserNotFoundException();
            }


        }

    }
}

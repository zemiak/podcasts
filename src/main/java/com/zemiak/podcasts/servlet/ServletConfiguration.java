package com.zemiak.podcasts.servlet;

import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class ServletConfiguration {
    @Inject
    private String path;

    public String getPath() {
        return path;
    }
}

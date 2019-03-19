package com.algolia.search.models;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BrowseIndexQuery extends Query {

  public String getCursor() {
    return cursor;
  }

  public BrowseIndexQuery setCursor(String cursor) {
    this.cursor = cursor;
    return this;
  }

  private String cursor;
}
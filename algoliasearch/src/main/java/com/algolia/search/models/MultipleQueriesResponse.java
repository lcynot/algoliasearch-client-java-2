package com.algolia.search.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MultipleQueriesResponse<T> implements Serializable {

  public List<T> getResults() {
    return results;
  }

  public MultipleQueriesResponse<T> setResults(List<T> results) {
    this.results = results;
    return this;
  }

  private List<T> results;
}
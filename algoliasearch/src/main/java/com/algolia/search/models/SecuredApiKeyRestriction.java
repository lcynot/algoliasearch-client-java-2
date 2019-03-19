package com.algolia.search.models;

import java.util.List;

public class SecuredApiKeyRestriction {

  public Query getQuery() {
    return query;
  }

  public SecuredApiKeyRestriction setQuery(Query query) {
    this.query = query;
    return this;
  }

  public Long getValidUntil() {
    return validUntil;
  }

  public SecuredApiKeyRestriction setValidUntil(Long validUntil) {
    this.validUntil = validUntil;
    return this;
  }

  public List<String> getRestrictIndices() {
    return restrictIndices;
  }

  public SecuredApiKeyRestriction setRestrictIndices(List<String> restrictIndices) {
    this.restrictIndices = restrictIndices;
    return this;
  }

  public List<String> getRestrictSources() {
    return restrictSources;
  }

  public SecuredApiKeyRestriction setRestrictSources(List<String> restrictSources) {
    this.restrictSources = restrictSources;
    return this;
  }

  public String getUserToken() {
    return userToken;
  }

  public SecuredApiKeyRestriction setUserToken(String userToken) {
    this.userToken = userToken;
    return this;
  }

  private Query query;
  private Long validUntil;
  private List<String> restrictIndices;
  private List<String> restrictSources;
  private String userToken;
}
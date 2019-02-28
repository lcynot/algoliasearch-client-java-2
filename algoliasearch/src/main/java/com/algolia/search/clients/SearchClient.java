package com.algolia.search.clients;

import com.algolia.search.exceptions.AlgoliaApiException;
import com.algolia.search.exceptions.AlgoliaRetryException;
import com.algolia.search.exceptions.AlgoliaRuntimeException;
import com.algolia.search.exceptions.LaunderThrowable;
import com.algolia.search.http.AlgoliaHttpRequester;
import com.algolia.search.http.IHttpRequester;
import com.algolia.search.inputs.ApiKeys;
import com.algolia.search.inputs.MultipleGetObjectsRequests;
import com.algolia.search.models.*;
import com.algolia.search.objects.ApiKey;
import com.algolia.search.objects.RequestOptions;
import com.algolia.search.transport.HttpTransport;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;

@SuppressWarnings("WeakerAccess")
public class SearchClient {

  private final HttpTransport transport;
  private final AlgoliaConfig config;

  public SearchClient(@Nonnull String applicationID, @Nonnull String apiKey) {
    this(new SearchConfig(applicationID, apiKey));
  }

  public SearchClient(@Nonnull SearchConfig config) {
    this(config, new AlgoliaHttpRequester(config));
  }

  public SearchClient(@Nonnull SearchConfig config, @Nonnull IHttpRequester httpRequester) {

    Objects.requireNonNull(httpRequester, "An httpRequester is required.");
    Objects.requireNonNull(config, "A configuration is required.");
    Objects.requireNonNull(config.getApplicationID(), "An ApplicationID is required.");
    Objects.requireNonNull(config.getApiKey(), "An API key is required.");

    if (config.getApplicationID().trim().length() == 0) {
      throw new NullPointerException("ApplicationID can't be empty.");
    }

    if (config.getApiKey().trim().length() == 0) {
      throw new NullPointerException("APIKey can't be empty.");
    }

    this.config = config;
    this.transport = new HttpTransport(config, httpRequester);
  }

  /**
   * Get the index object initialized (no server call needed for initialization)
   *
   * @param indexName The name of the Algolia index
   * @throws NullPointerException When indexName is null or empty
   */
  public SearchIndex<?> initIndex(@Nonnull String indexName) {

    if (indexName == null || indexName.trim().length() == 0) {
      throw new NullPointerException("The index name is required");
    }

    return new SearchIndex<>(transport, config, indexName, Object.class);
  }

  /**
   * Get the index object initialized (no server call needed for initialization)
   *
   * @param indexName The name of the Algolia index
   * @param klass class of the object in this index
   * @param <T> the type of the objects in this index
   * @throws NullPointerException When indexName is null or empty
   */
  public <T> SearchIndex<T> initIndex(@Nonnull String indexName, @Nonnull Class<T> klass) {

    if (indexName == null || indexName.trim().length() == 0) {
      throw new NullPointerException("The index name is required");
    }

    Objects.requireNonNull(klass, "A class is required.");

    return new SearchIndex<>(transport, config, indexName, klass);
  }

  /**
   * Retrieve one or more objects, potentially from different indices, in a single API call.
   *
   * @param queries The query object
   * @param klass Class of the data to retrieve
   * @param <T> Type of the data to retrieve
   * @throws AlgoliaRetryException When the retry has failed on all hosts
   * @throws AlgoliaApiException When the API sends an http error code
   * @throws AlgoliaRuntimeException When an error occurred during the serialization
   */
  public <T> MultipleGetObjectsResponse<T> multipleGetObjects(
      List<MultipleGetObjectsRequests> queries, Class<T> klass) throws AlgoliaRuntimeException {
    return multipleGetObjects(queries, klass, null);
  }

  /**
   * Retrieve one or more objects, potentially from different indices, in a single API call.
   *
   * @param queries The query object
   * @param klass Class of the data to retrieve
   * @param requestOptions Options to pass to this request
   * @param <T> Type of the data to retrieve
   * @throws AlgoliaRetryException When the retry has failed on all hosts
   * @throws AlgoliaApiException When the API sends an http error code
   * @throws AlgoliaRuntimeException When an error occurred during the serialization
   */
  public <T> MultipleGetObjectsResponse<T> multipleGetObjects(
      List<MultipleGetObjectsRequests> queries, Class<T> klass, RequestOptions requestOptions)
      throws AlgoliaRuntimeException {
    return LaunderThrowable.unwrap(multipleGetObjectsAsync(queries, klass, requestOptions));
  }

  /**
   * Retrieve one or more objects, potentially from different indices, in a single API call.
   *
   * @param queries The query object
   * @param klass Class of the data to retrieve
   * @param <T> Type of the data to retrieve
   */
  public <T> CompletableFuture<MultipleGetObjectsResponse<T>> multipleGetObjectsAsync(
      List<MultipleGetObjectsRequests> queries, Class<T> klass) {
    return multipleGetObjectsAsync(queries, klass, null);
  }

  /**
   * Retrieve one or more objects, potentially from different indices, in a single API call.
   *
   * @param queries The query object
   * @param klass Class of the data to retrieve
   * @param requestOptions Options to pass to this request
   * @param <T> Type of the data to retrieve
   */
  @SuppressWarnings("unchecked")
  public <T> CompletableFuture<MultipleGetObjectsResponse<T>> multipleGetObjectsAsync(
      List<MultipleGetObjectsRequests> queries, Class<T> klass, RequestOptions requestOptions) {

    Objects.requireNonNull(queries, "Queries is required");
    Objects.requireNonNull(klass, "Class is required");

    MultipleGetObjectsRequest request = new MultipleGetObjectsRequest(queries);

    return transport
        .executeRequestAsync(
            HttpMethod.POST,
            "/1/indexes/*/objects",
            CallType.READ,
            request,
            MultipleGetObjectsResponse.class,
            klass,
            requestOptions)
        .thenComposeAsync(
            resp -> {
              CompletableFuture<MultipleGetObjectsResponse<T>> r = new CompletableFuture<>();
              r.complete(resp);
              return r;
            },
            config.getExecutor());
  }

  /**
   * Perform multiple write operations, potentially targeting multiple indices, in a single API
   * call.
   *
   * @param operations The batch operations to process. It could be on multiple indices with multiple actions
   */
  public MultipleIndexBatchIndexingResponse multipleBatch(
      @Nonnull List<BatchOperation> operations) {
    return LaunderThrowable.unwrap(multipleBatchAsync(operations, null));
  }

  /**
   * Perform multiple write operations, potentially targeting multiple indices, in a single API
   * call.
   *
   * @param operations The batch operations to process. It could be on multiple indices with multiple action
   * @param requestOptions Options to pass to this request
   */
  public MultipleIndexBatchIndexingResponse multipleBatch(
      @Nonnull List<BatchOperation> operations, RequestOptions requestOptions) {
    return LaunderThrowable.unwrap(multipleBatchAsync(operations, requestOptions));
  }

  /**
   * Perform multiple write operations, potentially targeting multiple indices, in a single API
   * call.
   *
   * @param operations The batch operations to process. It could be on multiple indices with multiple action
   */
  public CompletableFuture<MultipleIndexBatchIndexingResponse> multipleBatchAsync(
      @Nonnull List<BatchOperation> operations) {
    return multipleBatchAsync(operations, null);
  }

  /**
   * Perform multiple write operations, potentially targeting multiple indices, in a single API
   * call.
   *
   * @param operations The batch operations to process. It could be on multiple indices with multiple action
   * @param requestOptions Options to pass to this request
   */
  public CompletableFuture<MultipleIndexBatchIndexingResponse> multipleBatchAsync(
      @Nonnull List<BatchOperation> operations, RequestOptions requestOptions) {

    Objects.requireNonNull(operations, "Operations are required");

    BatchRequest request = new BatchRequest(operations);

    return transport
        .executeRequestAsync(
            HttpMethod.POST,
            "/1/indexes/*/batch",
            CallType.WRITE,
            request,
            MultipleIndexBatchIndexingResponse.class,
            requestOptions)
        .thenApplyAsync(
            resp -> {
              resp.setWaitConsumer(this::waitTask);
              return resp;
            },
            config.getExecutor());
  }

  /**
   * List all existing indexes
   *
   * @return A List of the indices and their metadata
   * @throws AlgoliaRetryException When the retry has failed on all hosts
   * @throws AlgoliaApiException When the API sends an http error code
   * @throws AlgoliaRuntimeException When an error occurred during the serialization
   */
  public List<IndicesResponse> listIndices() throws AlgoliaRuntimeException {
    return listIndices(null);
  }

  /**
   * List all existing indexes
   *
   * @param requestOptions Options to pass to this request
   * @return A List of the indices and their metadata
   * @throws AlgoliaRetryException When the retry has failed on all hosts
   * @throws AlgoliaApiException When the API sends an http error code
   * @throws AlgoliaRuntimeException When an error occurred during the serialization
   */
  public List<IndicesResponse> listIndices(RequestOptions requestOptions)
      throws AlgoliaRuntimeException {
    return LaunderThrowable.unwrap(listIndicesAsync(requestOptions));
  }

  /**
   * List asynchronously all existing indexes
   *
   * @return A List of the indices and their metadata
   * @throws AlgoliaRetryException When the retry has failed on all hosts
   * @throws AlgoliaApiException When the API sends an http error code
   * @throws AlgoliaRuntimeException When an error occurred during the serialization
   */
  public CompletableFuture<List<IndicesResponse>> listIndicesAsync() {
    return listIndicesAsync(null);
  }

  /**
   * List asynchronously all existing indexes
   *
   * @param requestOptions Options to pass to this request
   * @return A List of the indices and their metadata
   * @throws AlgoliaRetryException When the retry has failed on all hosts
   * @throws AlgoliaApiException When the API sends an http error code
   * @throws AlgoliaRuntimeException When an error occurred during the serialization
   */
  public CompletableFuture<List<IndicesResponse>> listIndicesAsync(RequestOptions requestOptions) {
    return transport
        .executeRequestAsync(
            HttpMethod.GET,
            "/1/indexes",
            CallType.READ,
            null,
            ListIndicesResponse.class,
            requestOptions)
        .thenApplyAsync(ListIndicesResponse::getIndices, config.getExecutor());
  }

  /**
   * List all existing user keys with their associated ACLs
   *
   * @throws AlgoliaRetryException When the retry has failed on all hosts
   * @throws AlgoliaApiException When the API sends an http error code
   * @throws AlgoliaRuntimeException When an error occurred during the serialization
   */
  public List<ApiKey> listApiKeys() throws AlgoliaRuntimeException {
    return listApiKeys(null);
  }

  /**
   * List all existing user keys with their associated ACLs
   *
   * @param requestOptions Options to pass to this request
   * @throws AlgoliaRetryException When the retry has failed on all hosts
   * @throws AlgoliaApiException When the API sends an http error code
   * @throws AlgoliaRuntimeException When an error occurred during the serialization
   */
  public List<ApiKey> listApiKeys(RequestOptions requestOptions) throws AlgoliaRuntimeException {
    return LaunderThrowable.unwrap(listApiKeysAsync(requestOptions));
  }

  /**
   * List asynchronously all existing user keys with their associated ACLs
   *
   * @throws AlgoliaRetryException When the retry has failed on all hosts
   * @throws AlgoliaApiException When the API sends an http error code
   * @throws AlgoliaRuntimeException When an error occurred during the serialization
   */
  public CompletableFuture<List<ApiKey>> listApiKeysAsync() {
    return listApiKeysAsync(null);
  }

  /**
   * List asynchronously all existing user keys with their associated ACLs
   *
   * @param requestOptions Options to pass to this request
   * @throws AlgoliaRetryException When the retry has failed on all hosts
   * @throws AlgoliaApiException When the API sends an http error code
   * @throws AlgoliaRuntimeException When an error occurred during the serialization
   */
  public CompletableFuture<List<ApiKey>> listApiKeysAsync(RequestOptions requestOptions) {
    return transport
        .executeRequestAsync(
            HttpMethod.GET, "/1/keys", CallType.READ, null, ApiKeys.class, requestOptions)
        .thenApplyAsync(ApiKeys::getKeys, config.getExecutor());
  }

  /**
   * Get the permissions of an API Key.
   *
   * @param apiKey The API key to retrieve
   * @throws AlgoliaRetryException When the retry has failed on all hosts
   * @throws AlgoliaApiException When the API sends an http error code
   * @throws AlgoliaRuntimeException When an error occurred during the serialization
   */
  public CompletableFuture<ApiKey> getApiKeyAsync(@Nonnull String apiKey) {
    return getApiKeyAsync(apiKey, null);
  }

  /**
   * Get the permissions of an API Key.
   *
   * @param apiKey The API key to retrieve
   * @param requestOptions Options to pass to this request
   * @throws AlgoliaRetryException When the retry has failed on all hosts
   * @throws AlgoliaApiException When the API sends an http error code
   * @throws AlgoliaRuntimeException When an error occurred during the serialization
   */
  public CompletableFuture<ApiKey> getApiKeyAsync(
      @Nonnull String apiKey, RequestOptions requestOptions) {

    Objects.requireNonNull(apiKey, "An API key is required.");

    return transport.executeRequestAsync(
        HttpMethod.GET, "/1/keys/" + apiKey, CallType.READ, null, ApiKey.class, requestOptions);
  }

  /**
   * Wait for a task to complete before executing the next line of code, to synchronize index
   * updates. All write operations in Algolia are asynchronous by design.
   *
   * @param indexName The indexName to wait on
   * @param taskID The Algolia taskID
   */
  public void waitTask(@Nonnull String indexName, long taskID) {
    waitTask(indexName, taskID, 100, null);
  }

  /**
   * Wait for a task to complete before executing the next line of code, to synchronize index
   * updates. All write operations in Algolia are asynchronous by design.
   *
   * @param indexName The indexName to wait on
   * @param taskID The Algolia taskID
   * @param timeToWait The time to wait between each call
   */
  public void waitTask(@Nonnull String indexName, long taskID, int timeToWait) {
    waitTask(indexName, taskID, timeToWait, null);
  }

  /**
   * Wait for a task to complete before executing the next line of code, to synchronize index
   * updates. All write operations in Algolia are asynchronous by design.
   *
   * @param indexName The indexName to wait on
   * @param taskID The Algolia taskID
   * @param requestOptions Options to pass to this request
   */
  public void waitTask(@Nonnull String indexName, long taskID, RequestOptions requestOptions) {
    waitTask(indexName, taskID, 100, requestOptions);
  }

  /**
   * Wait for a task to complete before executing the next line of code, to synchronize index
   * updates. All write operations in Algolia are asynchronous by design.
   *
   * @param indexName The indexName to wait on
   * @param taskID The Algolia taskID
   * @param timeToWait The time to wait between each call
   * @param requestOptions Options to pass to this request
   */
  public void waitTask(
      @Nonnull String indexName, long taskID, int timeToWait, RequestOptions requestOptions) {

    Objects.requireNonNull(indexName, "The index name is required.");

    SearchIndex indexToWait = initIndex(indexName);
    indexToWait.waitTask(taskID, timeToWait, requestOptions);
  }
}

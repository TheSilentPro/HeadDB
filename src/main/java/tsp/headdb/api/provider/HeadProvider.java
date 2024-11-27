package tsp.headdb.api.provider;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * @author TheSilentPro (Silent)
 */
public interface HeadProvider {

    String getUrl();

    CompletableFuture<ProviderResponse> fetchHeads(ExecutorService executor);

}
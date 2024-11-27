package tsp.headdb.api.provider;

import tsp.headdb.api.model.Head;

import java.util.Date;
import java.util.List;

/**
 * @author TheSilentPro (Silent)
 */
public record ProviderResponse(List<Head> heads, Date date) {}
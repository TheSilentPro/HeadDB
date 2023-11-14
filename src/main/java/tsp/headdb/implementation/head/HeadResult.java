package tsp.headdb.implementation.head;

import tsp.headdb.implementation.category.Category;

import java.util.List;
import java.util.Map;

/**
 * @author TheSilentPro (Silent)
 */
public record HeadResult(long elapsed, Map<Category, List<Head>> heads) {}
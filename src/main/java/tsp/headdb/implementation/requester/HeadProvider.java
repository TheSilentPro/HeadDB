package tsp.headdb.implementation.requester;

import tsp.headdb.implementation.category.Category;

public enum HeadProvider {

    HEAD_STORAGE("https://raw.githubusercontent.com/TheSilentPro/HeadStorage/master/storage/%s.json"),
    HEAD_WORKER(""), // TODO: implement
    HEAD_API("https://minecraft-heads.com/scripts/api.php?cat=%s&tags=true"),
    HEAD_ARCHIVE("https://heads.pages.dev/archive/%s.json");

    private final String url;

    HeadProvider(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public String getFormattedUrl(Category category) {
        return String.format(getUrl(), category.getName());
    }

}

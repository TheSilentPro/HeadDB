package tsp.headdb.core.storage;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public final class HeadDBThreadFactory implements ThreadFactory {

    private HeadDBThreadFactory() {}

    public static final HeadDBThreadFactory FACTORY = new HeadDBThreadFactory();
    private final AtomicInteger ID = new AtomicInteger(1);

    @Override
    public Thread newThread(@NotNull Runnable r) {
        return new Thread(r, "headdb-thread-" + ID.getAndIncrement());
    }

}

package com.sandbox.concurrent;

import java.util.ArrayList;
import java.util.List;

import static com.sandbox.concurrent.ThreadDumper.generateThreadDump;
import static java.lang.Thread.currentThread;
import static java.util.Collections.singletonList;
import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;

public class ThreadTracer {
    private final List<Long> parentThreadIds;

    private ThreadTracer(List<Long> threadIds) {
        List<Long> copy = new ArrayList<>(requireNonNull(threadIds));
        this.parentThreadIds = unmodifiableList(copy);
    }

    public static ThreadTracer createThreadTrace() {
        return new ThreadTracer(singletonList(getCurrentThreadId()));
    }

    private static long getCurrentThreadId() {
        return currentThread().getId();
    }

    public ThreadTracer registerCurrentThread() {
        ThreadTracer result = this;
        if (getCurrentThreadId() != getLastTracedThreadId()) {
            List<Long> copy = new ArrayList<>(parentThreadIds);
            copy.add(getCurrentThreadId());
            result = new ThreadTracer(copy);
        }
        return result;
    }

    private long getLastTracedThreadId() {
        return parentThreadIds.get(parentThreadIds.size() - 1);
    }

    public ThreadReport generateThreadReport() {
        ensureCurrentThreadIsTraced();
        List<String> threadTrace = generateThreadDump(parentThreadIds);
        List<String> threadDump = generateThreadDump();
        return new ThreadReport(threadTrace, threadDump);
    }

    private void ensureCurrentThreadIsTraced() {
        if (getCurrentThreadId() != getLastTracedThreadId()) {
            throw new IllegalStateException("Expected current thread to be registered in ThreadTrace");
        }
    }

}

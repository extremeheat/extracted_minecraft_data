package net.minecraft;

import java.util.concurrent.Callable;
import org.jspecify.annotations.Nullable;

public interface CrashReportDetail<V> extends Callable<@Nullable V> {
}

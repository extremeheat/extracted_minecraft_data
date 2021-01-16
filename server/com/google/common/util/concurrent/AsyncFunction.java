package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import javax.annotation.Nullable;

@FunctionalInterface
@GwtCompatible
public interface AsyncFunction<I, O> {
   ListenableFuture<O> apply(@Nullable I var1) throws Exception;
}

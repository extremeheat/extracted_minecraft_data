package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import javax.annotation.Nullable;

@GwtCompatible
public interface FutureCallback<V> {
   void onSuccess(@Nullable V var1);

   void onFailure(Throwable var1);
}

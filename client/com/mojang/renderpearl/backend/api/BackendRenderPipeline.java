package com.mojang.renderpearl.backend.api;

import com.mojang.renderpearl.util.UncheckedAutoCloseable;
import org.jspecify.annotations.Nullable;

public interface BackendRenderPipeline extends UncheckedAutoCloseable {
   boolean isClosed();

   @FunctionalInterface
   public interface Pending {
      Pending NULL = () -> null;

      @Nullable BackendRenderPipeline finishCompile();
   }
}

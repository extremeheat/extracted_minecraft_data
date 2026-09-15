package com.mojang.renderpearl.api.pipeline;

import com.mojang.renderpearl.util.UncheckedAutoCloseable;
import org.jspecify.annotations.Nullable;

public interface CompiledRenderPipeline extends UncheckedAutoCloseable {
   boolean isClosed();

   @FunctionalInterface
   public interface Pending {
      Pending NULL = () -> null;

      @Nullable CompiledRenderPipeline finishCompile();
   }
}

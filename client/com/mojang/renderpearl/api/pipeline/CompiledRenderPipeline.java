package com.mojang.renderpearl.api.pipeline;

import com.mojang.renderpearl.util.UncheckedAutoCloseable;

public interface CompiledRenderPipeline extends UncheckedAutoCloseable {
   boolean isClosed();
}

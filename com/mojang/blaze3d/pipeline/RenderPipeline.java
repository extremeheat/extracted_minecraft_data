package com.mojang.blaze3d.pipeline;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RenderPipeline {
   private final List renderCalls = ImmutableList.of(new ConcurrentLinkedQueue(), new ConcurrentLinkedQueue(), new ConcurrentLinkedQueue(), new ConcurrentLinkedQueue());
   private volatile int recordingBuffer;
   private volatile int processedBuffer;
   private volatile int renderingBuffer;

   public RenderPipeline() {
      this.recordingBuffer = this.processedBuffer = this.renderingBuffer + 1;
   }
}

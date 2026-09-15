package net.minecraft.client.renderer.state.level;

import net.minecraft.client.renderer.block.MovingBlockRenderState;

public class TransientBlockRenderState {
   public MovingBlockRenderState movingBlockRenderState = new MovingBlockRenderState();
   public long createTimeNs;
   public long liveUntilMs;

   public TransientBlockRenderState() {
      super();
   }

   public static record Removal(long sectionNode, long compileTaskStartTimeNs) {
      public Removal {
         super();
      }
   }
}

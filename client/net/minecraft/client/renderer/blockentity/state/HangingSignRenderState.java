package net.minecraft.client.renderer.blockentity.state;

import net.minecraft.world.level.block.HangingSignBlock;

public class HangingSignRenderState extends SignRenderState {
   public HangingSignBlock.Attachment attachmentType;

   public HangingSignRenderState() {
      super();
      this.attachmentType = HangingSignBlock.Attachment.CEILING;
   }
}

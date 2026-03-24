package net.minecraft.client.renderer.blockentity.state;

import net.minecraft.world.level.block.PlainSignBlock;

public class StandingSignRenderState extends SignRenderState {
   public PlainSignBlock.Attachment attachmentType;

   public StandingSignRenderState() {
      super();
      this.attachmentType = PlainSignBlock.Attachment.GROUND;
   }
}

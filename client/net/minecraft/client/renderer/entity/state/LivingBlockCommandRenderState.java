package net.minecraft.client.renderer.entity.state;

import net.minecraft.world.entity.LivingBlockCommand;

public class LivingBlockCommandRenderState extends EntityRenderState {
   public LivingBlockCommand.Type commandType;
   public float visibility;

   public LivingBlockCommandRenderState() {
      super();
      this.commandType = LivingBlockCommand.Type.TYPE_MOVE;
      this.visibility = 1.0F;
   }
}

package net.minecraft.client.renderer.entity.state;

import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.phys.Vec3;

public class ShulkerRenderState extends LivingEntityRenderState {
   public Vec3 renderOffset;
   @Nullable
   public DyeColor color;
   public float peekAmount;
   public float yHeadRot;
   public float yBodyRot;
   public Direction attachFace;

   public ShulkerRenderState() {
      super();
      this.renderOffset = Vec3.ZERO;
      this.attachFace = Direction.DOWN;
   }
}

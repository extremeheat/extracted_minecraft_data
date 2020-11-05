package net.minecraft.world.level.block.entity;

import net.minecraft.core.Direction;

public class TheEndPortalBlockEntity extends BlockEntity {
   public TheEndPortalBlockEntity(BlockEntityType<?> var1) {
      super(var1);
   }

   public TheEndPortalBlockEntity() {
      this(BlockEntityType.END_PORTAL);
   }

   public boolean shouldRenderFace(Direction var1) {
      return var1 == Direction.UP;
   }
}

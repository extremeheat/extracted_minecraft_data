package net.minecraft.client.renderer.blockentity.state;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.ChestType;

public class ChestRenderState extends BlockEntityRenderState {
   public ChestType type;
   public float open;
   public Direction facing;
   public ChestMaterialType material;

   public ChestRenderState() {
      super();
      this.type = ChestType.SINGLE;
      this.facing = Direction.SOUTH;
      this.material = ChestRenderState.ChestMaterialType.REGULAR;
   }

   public static enum ChestMaterialType {
      ENDER_CHEST,
      CHRISTMAS,
      TRAPPED,
      COPPER_UNAFFECTED,
      COPPER_EXPOSED,
      COPPER_WEATHERED,
      COPPER_OXIDIZED,
      REGULAR;

      private ChestMaterialType() {
      }

      // $FF: synthetic method
      private static ChestMaterialType[] $values() {
         return new ChestMaterialType[]{ENDER_CHEST, CHRISTMAS, TRAPPED, COPPER_UNAFFECTED, COPPER_EXPOSED, COPPER_WEATHERED, COPPER_OXIDIZED, REGULAR};
      }
   }
}

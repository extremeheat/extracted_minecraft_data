package net.minecraft.world.level.block.state.predicate;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

public class BlockMaterialPredicate implements Predicate<BlockState> {
   private static final BlockMaterialPredicate AIR;
   private final Material material;

   BlockMaterialPredicate(Material var1) {
      super();
      this.material = var1;
   }

   public static BlockMaterialPredicate forMaterial(Material var0) {
      return var0 == Material.AIR ? AIR : new BlockMaterialPredicate(var0);
   }

   public boolean test(@Nullable BlockState var1) {
      return var1 != null && var1.getMaterial() == this.material;
   }

   // $FF: synthetic method
   public boolean test(@Nullable Object var1) {
      return this.test((BlockState)var1);
   }

   static {
      AIR = new BlockMaterialPredicate(Material.AIR) {
         public boolean test(@Nullable BlockState var1) {
            return var1 != null && var1.isAir();
         }

         // $FF: synthetic method
         public boolean test(@Nullable Object var1) {
            return this.test((BlockState)var1);
         }
      };
   }
}

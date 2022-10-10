package net.minecraft.block.state.pattern;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;

public class BlockMaterialMatcher implements Predicate<IBlockState> {
   private static final BlockMaterialMatcher field_196961_a;
   private final Material field_189887_a;

   private BlockMaterialMatcher(Material var1) {
      super();
      this.field_189887_a = var1;
   }

   public static BlockMaterialMatcher func_189886_a(Material var0) {
      return var0 == Material.field_151579_a ? field_196961_a : new BlockMaterialMatcher(var0);
   }

   public boolean test(@Nullable IBlockState var1) {
      return var1 != null && var1.func_185904_a() == this.field_189887_a;
   }

   // $FF: synthetic method
   public boolean test(@Nullable Object var1) {
      return this.test((IBlockState)var1);
   }

   // $FF: synthetic method
   BlockMaterialMatcher(Material var1, Object var2) {
      this(var1);
   }

   static {
      field_196961_a = new BlockMaterialMatcher(Material.field_151579_a) {
         public boolean test(@Nullable IBlockState var1) {
            return var1 != null && var1.func_196958_f();
         }

         // $FF: synthetic method
         public boolean test(@Nullable Object var1) {
            return this.test((IBlockState)var1);
         }
      };
   }
}

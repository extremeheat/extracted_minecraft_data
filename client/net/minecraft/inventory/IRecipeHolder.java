package net.minecraft.inventory;

import com.google.common.collect.Lists;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public interface IRecipeHolder {
   void func_193056_a(@Nullable IRecipe var1);

   @Nullable
   IRecipe func_193055_i();

   default void func_201560_d(EntityPlayer var1) {
      IRecipe var2 = this.func_193055_i();
      if (var2 != null && !var2.func_192399_d()) {
         var1.func_195065_a(Lists.newArrayList(new IRecipe[]{var2}));
         this.func_193056_a((IRecipe)null);
      }

   }

   default boolean func_201561_a(World var1, EntityPlayerMP var2, @Nullable IRecipe var3) {
      if (var3 == null || !var3.func_192399_d() && var1.func_82736_K().func_82766_b("doLimitedCrafting") && !var2.func_192037_E().func_193830_f(var3)) {
         return false;
      } else {
         this.func_193056_a(var3);
         return true;
      }
   }
}

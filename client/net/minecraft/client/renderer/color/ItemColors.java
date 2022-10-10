package net.minecraft.client.renderer.color;

import java.util.Iterator;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmorDyeable;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemSpawnEgg;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.GrassColors;
import net.minecraft.world.IWorldReaderBase;

public class ItemColors {
   private final ObjectIntIdentityMap<IItemColor> field_186732_a = new ObjectIntIdentityMap(32);

   public ItemColors() {
      super();
   }

   public static ItemColors func_186729_a(BlockColors var0) {
      ItemColors var1 = new ItemColors();
      var1.func_199877_a((var0x, var1x) -> {
         return var1x > 0 ? -1 : ((ItemArmorDyeable)var0x.func_77973_b()).func_200886_f(var0x);
      }, Items.field_151024_Q, Items.field_151027_R, Items.field_151026_S, Items.field_151021_T);
      var1.func_199877_a((var0x, var1x) -> {
         return GrassColors.func_77480_a(0.5D, 1.0D);
      }, Blocks.field_196804_gh, Blocks.field_196805_gi);
      var1.func_199877_a((var0x, var1x) -> {
         if (var1x != 1) {
            return -1;
         } else {
            NBTTagCompound var2 = var0x.func_179543_a("Explosion");
            int[] var3 = var2 != null && var2.func_150297_b("Colors", 11) ? var2.func_74759_k("Colors") : null;
            if (var3 == null) {
               return 9079434;
            } else if (var3.length == 1) {
               return var3[0];
            } else {
               int var4 = 0;
               int var5 = 0;
               int var6 = 0;
               int[] var7 = var3;
               int var8 = var3.length;

               for(int var9 = 0; var9 < var8; ++var9) {
                  int var10 = var7[var9];
                  var4 += (var10 & 16711680) >> 16;
                  var5 += (var10 & '\uff00') >> 8;
                  var6 += (var10 & 255) >> 0;
               }

               var4 /= var3.length;
               var5 /= var3.length;
               var6 /= var3.length;
               return var4 << 16 | var5 << 8 | var6;
            }
         }
      }, Items.field_196153_dF);
      var1.func_199877_a((var0x, var1x) -> {
         return var1x > 0 ? -1 : PotionUtils.func_190932_c(var0x);
      }, Items.field_151068_bn, Items.field_185155_bH, Items.field_185156_bI);
      Iterator var2 = ItemSpawnEgg.func_195985_g().iterator();

      while(var2.hasNext()) {
         ItemSpawnEgg var3 = (ItemSpawnEgg)var2.next();
         var1.func_199877_a((var1x, var2x) -> {
            return var3.func_195983_a(var2x);
         }, var3);
      }

      var1.func_199877_a((var1x, var2x) -> {
         IBlockState var3 = ((ItemBlock)var1x.func_77973_b()).func_179223_d().func_176223_P();
         return var0.func_186724_a(var3, (IWorldReaderBase)null, (BlockPos)null, var2x);
      }, Blocks.field_196658_i, Blocks.field_150349_c, Blocks.field_196554_aH, Blocks.field_150395_bd, Blocks.field_196642_W, Blocks.field_196645_X, Blocks.field_196647_Y, Blocks.field_196648_Z, Blocks.field_196572_aa, Blocks.field_196574_ab, Blocks.field_196651_dG);
      var1.func_199877_a((var0x, var1x) -> {
         return var1x == 0 ? PotionUtils.func_190932_c(var0x) : -1;
      }, Items.field_185167_i);
      var1.func_199877_a((var0x, var1x) -> {
         return var1x == 0 ? -1 : ItemMap.func_190907_h(var0x);
      }, Items.field_151098_aY);
      return var1;
   }

   public int func_186728_a(ItemStack var1, int var2) {
      IItemColor var3 = (IItemColor)this.field_186732_a.func_148745_a(IRegistry.field_212630_s.func_148757_b(var1.func_77973_b()));
      return var3 == null ? -1 : var3.getColor(var1, var2);
   }

   public void func_199877_a(IItemColor var1, IItemProvider... var2) {
      IItemProvider[] var3 = var2;
      int var4 = var2.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         IItemProvider var6 = var3[var5];
         this.field_186732_a.func_148746_a(var1, Item.func_150891_b(var6.func_199767_j()));
      }

   }
}

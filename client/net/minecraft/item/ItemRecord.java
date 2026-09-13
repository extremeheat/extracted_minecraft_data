package net.minecraft.item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.BlockJukebox;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemRecord extends Item {
   private static final Map field_150928_b = new HashMap();
   public final String field_150929_a;

   protected ItemRecord(String var1) {
      super();
      this.field_150929_a = var1;
      this.field_77777_bU = 1;
      this.func_77637_a(CreativeTabs.field_78026_f);
      field_150928_b.put(var1, this);
   }

   @Override
   public IIcon func_77617_a(int var1) {
      return this.field_77791_bV;
   }

   @Override
   public boolean func_77648_a(ItemStack var1, EntityPlayer var2, World var3, int var4, int var5, int var6, int var7, float var8, float var9, float var10) {
      if (var3.func_147439_a(var4, var5, var6) != Blocks.field_150421_aI || var3.func_72805_g(var4, var5, var6) != 0) {
         return false;
      } else if (var3.field_72995_K) {
         return true;
      } else {
         ((BlockJukebox)Blocks.field_150421_aI).func_149926_b(var3, var4, var5, var6, var1);
         var3.func_72889_a(null, 1005, var4, var5, var6, Item.func_150891_b(this));
         --var1.field_77994_a;
         return true;
      }
   }

   @Override
   public void func_77624_a(ItemStack var1, EntityPlayer var2, List var3, boolean var4) {
      var3.add(this.func_150927_i());
   }

   public String func_150927_i() {
      return StatCollector.func_74838_a("item.record." + this.field_150929_a + ".desc");
   }

   @Override
   public EnumRarity func_77613_e(ItemStack var1) {
      return EnumRarity.rare;
   }

   public static ItemRecord func_150926_b(String var0) {
      return (ItemRecord)field_150928_b.get(var0);
   }
}

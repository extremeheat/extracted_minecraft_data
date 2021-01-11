package net.minecraft.item;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import net.minecraft.block.BlockJukebox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemRecord extends Item {
   private static final Map<String, ItemRecord> field_150928_b = Maps.newHashMap();
   public final String field_150929_a;

   protected ItemRecord(String var1) {
      super();
      this.field_150929_a = var1;
      this.field_77777_bU = 1;
      this.func_77637_a(CreativeTabs.field_78026_f);
      field_150928_b.put("records." + var1, this);
   }

   public boolean func_180614_a(ItemStack var1, EntityPlayer var2, World var3, BlockPos var4, EnumFacing var5, float var6, float var7, float var8) {
      IBlockState var9 = var3.func_180495_p(var4);
      if (var9.func_177230_c() == Blocks.field_150421_aI && !(Boolean)var9.func_177229_b(BlockJukebox.field_176432_a)) {
         if (var3.field_72995_K) {
            return true;
         } else {
            ((BlockJukebox)Blocks.field_150421_aI).func_176431_a(var3, var4, var9, var1);
            var3.func_180498_a((EntityPlayer)null, 1005, var4, Item.func_150891_b(this));
            --var1.field_77994_a;
            var2.func_71029_a(StatList.field_181740_X);
            return true;
         }
      } else {
         return false;
      }
   }

   public void func_77624_a(ItemStack var1, EntityPlayer var2, List<String> var3, boolean var4) {
      var3.add(this.func_150927_i());
   }

   public String func_150927_i() {
      return StatCollector.func_74838_a("item.record." + this.field_150929_a + ".desc");
   }

   public EnumRarity func_77613_e(ItemStack var1) {
      return EnumRarity.RARE;
   }

   public static ItemRecord func_150926_b(String var0) {
      return (ItemRecord)field_150928_b.get(var0);
   }
}

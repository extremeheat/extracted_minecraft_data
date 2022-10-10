package net.minecraft.item;

import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemSword extends ItemTiered {
   private final float field_150934_a;
   private final float field_200895_b;

   public ItemSword(IItemTier var1, int var2, float var3, Item.Properties var4) {
      super(var1, var4);
      this.field_200895_b = var3;
      this.field_150934_a = (float)var2 + var1.func_200929_c();
   }

   public float func_200894_d() {
      return this.field_150934_a;
   }

   public boolean func_195938_a(IBlockState var1, World var2, BlockPos var3, EntityPlayer var4) {
      return !var4.func_184812_l_();
   }

   public float func_150893_a(ItemStack var1, IBlockState var2) {
      Block var3 = var2.func_177230_c();
      if (var3 == Blocks.field_196553_aF) {
         return 15.0F;
      } else {
         Material var4 = var2.func_185904_a();
         return var4 != Material.field_151585_k && var4 != Material.field_151582_l && var4 != Material.field_151589_v && !var2.func_203425_a(BlockTags.field_206952_E) && var4 != Material.field_151572_C ? 1.0F : 1.5F;
      }
   }

   public boolean func_77644_a(ItemStack var1, EntityLivingBase var2, EntityLivingBase var3) {
      var1.func_77972_a(1, var3);
      return true;
   }

   public boolean func_179218_a(ItemStack var1, World var2, IBlockState var3, BlockPos var4, EntityLivingBase var5) {
      if (var3.func_185887_b(var2, var4) != 0.0F) {
         var1.func_77972_a(2, var5);
      }

      return true;
   }

   public boolean func_150897_b(IBlockState var1) {
      return var1.func_177230_c() == Blocks.field_196553_aF;
   }

   public Multimap<String, AttributeModifier> func_111205_h(EntityEquipmentSlot var1) {
      Multimap var2 = super.func_111205_h(var1);
      if (var1 == EntityEquipmentSlot.MAINHAND) {
         var2.put(SharedMonsterAttributes.field_111264_e.func_111108_a(), new AttributeModifier(field_111210_e, "Weapon modifier", (double)this.field_150934_a, 0));
         var2.put(SharedMonsterAttributes.field_188790_f.func_111108_a(), new AttributeModifier(field_185050_h, "Weapon modifier", (double)this.field_200895_b, 0));
      }

      return var2;
   }
}

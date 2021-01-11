package net.minecraft.item;

import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class ItemSword extends Item {
   private float field_150934_a;
   private final Item.ToolMaterial field_150933_b;

   public ItemSword(Item.ToolMaterial var1) {
      super();
      this.field_150933_b = var1;
      this.field_77777_bU = 1;
      this.func_77656_e(var1.func_77997_a());
      this.func_77637_a(CreativeTabs.field_78037_j);
      this.field_150934_a = 4.0F + var1.func_78000_c();
   }

   public float func_150931_i() {
      return this.field_150933_b.func_78000_c();
   }

   public float func_150893_a(ItemStack var1, Block var2) {
      if (var2 == Blocks.field_150321_G) {
         return 15.0F;
      } else {
         Material var3 = var2.func_149688_o();
         return var3 != Material.field_151585_k && var3 != Material.field_151582_l && var3 != Material.field_151589_v && var3 != Material.field_151584_j && var3 != Material.field_151572_C ? 1.0F : 1.5F;
      }
   }

   public boolean func_77644_a(ItemStack var1, EntityLivingBase var2, EntityLivingBase var3) {
      var1.func_77972_a(1, var3);
      return true;
   }

   public boolean func_179218_a(ItemStack var1, World var2, Block var3, BlockPos var4, EntityLivingBase var5) {
      if ((double)var3.func_176195_g(var2, var4) != 0.0D) {
         var1.func_77972_a(2, var5);
      }

      return true;
   }

   public boolean func_77662_d() {
      return true;
   }

   public EnumAction func_77661_b(ItemStack var1) {
      return EnumAction.BLOCK;
   }

   public int func_77626_a(ItemStack var1) {
      return 72000;
   }

   public ItemStack func_77659_a(ItemStack var1, World var2, EntityPlayer var3) {
      var3.func_71008_a(var1, this.func_77626_a(var1));
      return var1;
   }

   public boolean func_150897_b(Block var1) {
      return var1 == Blocks.field_150321_G;
   }

   public int func_77619_b() {
      return this.field_150933_b.func_77995_e();
   }

   public String func_150932_j() {
      return this.field_150933_b.toString();
   }

   public boolean func_82789_a(ItemStack var1, ItemStack var2) {
      return this.field_150933_b.func_150995_f() == var2.func_77973_b() ? true : super.func_82789_a(var1, var2);
   }

   public Multimap<String, AttributeModifier> func_111205_h() {
      Multimap var1 = super.func_111205_h();
      var1.put(SharedMonsterAttributes.field_111264_e.func_111108_a(), new AttributeModifier(field_111210_e, "Weapon modifier", (double)this.field_150934_a, 0));
      return var1;
   }
}

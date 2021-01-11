package net.minecraft.item;

import com.google.common.collect.Multimap;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class ItemTool extends Item {
   private Set<Block> field_150914_c;
   protected float field_77864_a = 4.0F;
   private float field_77865_bY;
   protected Item.ToolMaterial field_77862_b;

   protected ItemTool(float var1, Item.ToolMaterial var2, Set<Block> var3) {
      super();
      this.field_77862_b = var2;
      this.field_150914_c = var3;
      this.field_77777_bU = 1;
      this.func_77656_e(var2.func_77997_a());
      this.field_77864_a = var2.func_77998_b();
      this.field_77865_bY = var1 + var2.func_78000_c();
      this.func_77637_a(CreativeTabs.field_78040_i);
   }

   public float func_150893_a(ItemStack var1, Block var2) {
      return this.field_150914_c.contains(var2) ? this.field_77864_a : 1.0F;
   }

   public boolean func_77644_a(ItemStack var1, EntityLivingBase var2, EntityLivingBase var3) {
      var1.func_77972_a(2, var3);
      return true;
   }

   public boolean func_179218_a(ItemStack var1, World var2, Block var3, BlockPos var4, EntityLivingBase var5) {
      if ((double)var3.func_176195_g(var2, var4) != 0.0D) {
         var1.func_77972_a(1, var5);
      }

      return true;
   }

   public boolean func_77662_d() {
      return true;
   }

   public Item.ToolMaterial func_150913_i() {
      return this.field_77862_b;
   }

   public int func_77619_b() {
      return this.field_77862_b.func_77995_e();
   }

   public String func_77861_e() {
      return this.field_77862_b.toString();
   }

   public boolean func_82789_a(ItemStack var1, ItemStack var2) {
      return this.field_77862_b.func_150995_f() == var2.func_77973_b() ? true : super.func_82789_a(var1, var2);
   }

   public Multimap<String, AttributeModifier> func_111205_h() {
      Multimap var1 = super.func_111205_h();
      var1.put(SharedMonsterAttributes.field_111264_e.func_111108_a(), new AttributeModifier(field_111210_e, "Tool modifier", (double)this.field_77865_bY, 0));
      return var1;
   }
}

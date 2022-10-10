package net.minecraft.item;

import com.google.common.collect.Multimap;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemTool extends ItemTiered {
   private final Set<Block> field_150914_c;
   protected float field_77864_a;
   protected float field_77865_bY;
   protected float field_185065_c;

   protected ItemTool(float var1, float var2, IItemTier var3, Set<Block> var4, Item.Properties var5) {
      super(var3, var5);
      this.field_150914_c = var4;
      this.field_77864_a = var3.func_200928_b();
      this.field_77865_bY = var1 + var3.func_200929_c();
      this.field_185065_c = var2;
   }

   public float func_150893_a(ItemStack var1, IBlockState var2) {
      return this.field_150914_c.contains(var2.func_177230_c()) ? this.field_77864_a : 1.0F;
   }

   public boolean func_77644_a(ItemStack var1, EntityLivingBase var2, EntityLivingBase var3) {
      var1.func_77972_a(2, var3);
      return true;
   }

   public boolean func_179218_a(ItemStack var1, World var2, IBlockState var3, BlockPos var4, EntityLivingBase var5) {
      if (!var2.field_72995_K && var3.func_185887_b(var2, var4) != 0.0F) {
         var1.func_77972_a(1, var5);
      }

      return true;
   }

   public Multimap<String, AttributeModifier> func_111205_h(EntityEquipmentSlot var1) {
      Multimap var2 = super.func_111205_h(var1);
      if (var1 == EntityEquipmentSlot.MAINHAND) {
         var2.put(SharedMonsterAttributes.field_111264_e.func_111108_a(), new AttributeModifier(field_111210_e, "Tool modifier", (double)this.field_77865_bY, 0));
         var2.put(SharedMonsterAttributes.field_188790_f.func_111108_a(), new AttributeModifier(field_185050_h, "Tool modifier", (double)this.field_185065_c, 0));
      }

      return var2;
   }
}

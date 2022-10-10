package net.minecraft.item;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemHoe extends ItemTiered {
   private final float field_185072_b;
   protected static final Map<Block, IBlockState> field_195973_b;

   public ItemHoe(IItemTier var1, float var2, Item.Properties var3) {
      super(var1, var3);
      this.field_185072_b = var2;
   }

   public EnumActionResult func_195939_a(ItemUseContext var1) {
      World var2 = var1.func_195991_k();
      BlockPos var3 = var1.func_195995_a();
      if (var1.func_196000_l() != EnumFacing.DOWN && var2.func_180495_p(var3.func_177984_a()).func_196958_f()) {
         IBlockState var4 = (IBlockState)field_195973_b.get(var2.func_180495_p(var3).func_177230_c());
         if (var4 != null) {
            EntityPlayer var5 = var1.func_195999_j();
            var2.func_184133_a(var5, var3, SoundEvents.field_187693_cj, SoundCategory.BLOCKS, 1.0F, 1.0F);
            if (!var2.field_72995_K) {
               var2.func_180501_a(var3, var4, 11);
               if (var5 != null) {
                  var1.func_195996_i().func_77972_a(1, var5);
               }
            }

            return EnumActionResult.SUCCESS;
         }
      }

      return EnumActionResult.PASS;
   }

   public boolean func_77644_a(ItemStack var1, EntityLivingBase var2, EntityLivingBase var3) {
      var1.func_77972_a(1, var3);
      return true;
   }

   public Multimap<String, AttributeModifier> func_111205_h(EntityEquipmentSlot var1) {
      Multimap var2 = super.func_111205_h(var1);
      if (var1 == EntityEquipmentSlot.MAINHAND) {
         var2.put(SharedMonsterAttributes.field_111264_e.func_111108_a(), new AttributeModifier(field_111210_e, "Weapon modifier", 0.0D, 0));
         var2.put(SharedMonsterAttributes.field_188790_f.func_111108_a(), new AttributeModifier(field_185050_h, "Weapon modifier", (double)this.field_185072_b, 0));
      }

      return var2;
   }

   static {
      field_195973_b = Maps.newHashMap(ImmutableMap.of(Blocks.field_196658_i, Blocks.field_150458_ak.func_176223_P(), Blocks.field_185774_da, Blocks.field_150458_ak.func_176223_P(), Blocks.field_150346_d, Blocks.field_150458_ak.func_176223_P(), Blocks.field_196660_k, Blocks.field_150346_d.func_176223_P()));
   }
}

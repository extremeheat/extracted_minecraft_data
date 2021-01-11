package net.minecraft.item;

import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;

public class ItemPickaxe extends ItemTool {
   private static final Set<Block> field_150915_c;

   protected ItemPickaxe(Item.ToolMaterial var1) {
      super(2.0F, var1, field_150915_c);
   }

   public boolean func_150897_b(Block var1) {
      if (var1 == Blocks.field_150343_Z) {
         return this.field_77862_b.func_77996_d() == 3;
      } else if (var1 != Blocks.field_150484_ah && var1 != Blocks.field_150482_ag) {
         if (var1 != Blocks.field_150412_bA && var1 != Blocks.field_150475_bE) {
            if (var1 != Blocks.field_150340_R && var1 != Blocks.field_150352_o) {
               if (var1 != Blocks.field_150339_S && var1 != Blocks.field_150366_p) {
                  if (var1 != Blocks.field_150368_y && var1 != Blocks.field_150369_x) {
                     if (var1 != Blocks.field_150450_ax && var1 != Blocks.field_150439_ay) {
                        if (var1.func_149688_o() == Material.field_151576_e) {
                           return true;
                        } else if (var1.func_149688_o() == Material.field_151573_f) {
                           return true;
                        } else {
                           return var1.func_149688_o() == Material.field_151574_g;
                        }
                     } else {
                        return this.field_77862_b.func_77996_d() >= 2;
                     }
                  } else {
                     return this.field_77862_b.func_77996_d() >= 1;
                  }
               } else {
                  return this.field_77862_b.func_77996_d() >= 1;
               }
            } else {
               return this.field_77862_b.func_77996_d() >= 2;
            }
         } else {
            return this.field_77862_b.func_77996_d() >= 2;
         }
      } else {
         return this.field_77862_b.func_77996_d() >= 2;
      }
   }

   public float func_150893_a(ItemStack var1, Block var2) {
      return var2.func_149688_o() != Material.field_151573_f && var2.func_149688_o() != Material.field_151574_g && var2.func_149688_o() != Material.field_151576_e ? super.func_150893_a(var1, var2) : this.field_77864_a;
   }

   static {
      field_150915_c = Sets.newHashSet(new Block[]{Blocks.field_150408_cc, Blocks.field_150365_q, Blocks.field_150347_e, Blocks.field_150319_E, Blocks.field_150484_ah, Blocks.field_150482_ag, Blocks.field_150334_T, Blocks.field_150318_D, Blocks.field_150340_R, Blocks.field_150352_o, Blocks.field_150432_aD, Blocks.field_150339_S, Blocks.field_150366_p, Blocks.field_150368_y, Blocks.field_150369_x, Blocks.field_150439_ay, Blocks.field_150341_Y, Blocks.field_150424_aL, Blocks.field_150403_cj, Blocks.field_150448_aq, Blocks.field_150450_ax, Blocks.field_150322_A, Blocks.field_180395_cM, Blocks.field_150348_b, Blocks.field_150333_U});
   }
}

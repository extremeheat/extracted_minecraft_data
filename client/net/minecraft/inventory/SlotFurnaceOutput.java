package net.minecraft.inventory;

import java.util.Iterator;
import java.util.Map.Entry;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class SlotFurnaceOutput extends Slot {
   private final EntityPlayer field_75229_a;
   private int field_75228_b;

   public SlotFurnaceOutput(EntityPlayer var1, IInventory var2, int var3, int var4, int var5) {
      super(var2, var3, var4, var5);
      this.field_75229_a = var1;
   }

   public boolean func_75214_a(ItemStack var1) {
      return false;
   }

   public ItemStack func_75209_a(int var1) {
      if (this.func_75216_d()) {
         this.field_75228_b += Math.min(var1, this.func_75211_c().func_190916_E());
      }

      return super.func_75209_a(var1);
   }

   public ItemStack func_190901_a(EntityPlayer var1, ItemStack var2) {
      this.func_75208_c(var2);
      super.func_190901_a(var1, var2);
      return var2;
   }

   protected void func_75210_a(ItemStack var1, int var2) {
      this.field_75228_b += var2;
      this.func_75208_c(var1);
   }

   protected void func_75208_c(ItemStack var1) {
      var1.func_77980_a(this.field_75229_a.field_70170_p, this.field_75229_a, this.field_75228_b);
      if (!this.field_75229_a.field_70170_p.field_72995_K) {
         Iterator var2 = ((TileEntityFurnace)this.field_75224_c).func_203900_q().entrySet().iterator();

         while(var2.hasNext()) {
            Entry var3 = (Entry)var2.next();
            FurnaceRecipe var4 = (FurnaceRecipe)this.field_75229_a.field_70170_p.func_199532_z().func_199517_a((ResourceLocation)var3.getKey());
            float var5;
            if (var4 != null) {
               var5 = var4.func_201831_g();
            } else {
               var5 = 0.0F;
            }

            int var6 = (Integer)var3.getValue();
            int var7;
            if (var5 == 0.0F) {
               var6 = 0;
            } else if (var5 < 1.0F) {
               var7 = MathHelper.func_76141_d((float)var6 * var5);
               if (var7 < MathHelper.func_76123_f((float)var6 * var5) && Math.random() < (double)((float)var6 * var5 - (float)var7)) {
                  ++var7;
               }

               var6 = var7;
            }

            while(var6 > 0) {
               var7 = EntityXPOrb.func_70527_a(var6);
               var6 -= var7;
               this.field_75229_a.field_70170_p.func_72838_d(new EntityXPOrb(this.field_75229_a.field_70170_p, this.field_75229_a.field_70165_t, this.field_75229_a.field_70163_u + 0.5D, this.field_75229_a.field_70161_v + 0.5D, var7));
            }
         }

         ((IRecipeHolder)this.field_75224_c).func_201560_d(this.field_75229_a);
      }

      this.field_75228_b = 0;
   }
}

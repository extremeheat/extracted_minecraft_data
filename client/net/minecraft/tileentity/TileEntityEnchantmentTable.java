package net.minecraft.tileentity;

import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ITickable;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IInteractionObject;

public class TileEntityEnchantmentTable extends TileEntity implements ITickable, IInteractionObject {
   public int field_145926_a;
   public float field_145933_i;
   public float field_145931_j;
   public float field_145932_k;
   public float field_145929_l;
   public float field_145930_m;
   public float field_145927_n;
   public float field_145928_o;
   public float field_145925_p;
   public float field_145924_q;
   private static Random field_145923_r = new Random();
   private String field_145922_s;

   public TileEntityEnchantmentTable() {
      super();
   }

   public void func_145841_b(NBTTagCompound var1) {
      super.func_145841_b(var1);
      if (this.func_145818_k_()) {
         var1.func_74778_a("CustomName", this.field_145922_s);
      }

   }

   public void func_145839_a(NBTTagCompound var1) {
      super.func_145839_a(var1);
      if (var1.func_150297_b("CustomName", 8)) {
         this.field_145922_s = var1.func_74779_i("CustomName");
      }

   }

   public void func_73660_a() {
      this.field_145927_n = this.field_145930_m;
      this.field_145925_p = this.field_145928_o;
      EntityPlayer var1 = this.field_145850_b.func_72977_a((double)((float)this.field_174879_c.func_177958_n() + 0.5F), (double)((float)this.field_174879_c.func_177956_o() + 0.5F), (double)((float)this.field_174879_c.func_177952_p() + 0.5F), 3.0D);
      if (var1 != null) {
         double var2 = var1.field_70165_t - (double)((float)this.field_174879_c.func_177958_n() + 0.5F);
         double var4 = var1.field_70161_v - (double)((float)this.field_174879_c.func_177952_p() + 0.5F);
         this.field_145924_q = (float)MathHelper.func_181159_b(var4, var2);
         this.field_145930_m += 0.1F;
         if (this.field_145930_m < 0.5F || field_145923_r.nextInt(40) == 0) {
            float var6 = this.field_145932_k;

            do {
               this.field_145932_k += (float)(field_145923_r.nextInt(4) - field_145923_r.nextInt(4));
            } while(var6 == this.field_145932_k);
         }
      } else {
         this.field_145924_q += 0.02F;
         this.field_145930_m -= 0.1F;
      }

      while(this.field_145928_o >= 3.1415927F) {
         this.field_145928_o -= 6.2831855F;
      }

      while(this.field_145928_o < -3.1415927F) {
         this.field_145928_o += 6.2831855F;
      }

      while(this.field_145924_q >= 3.1415927F) {
         this.field_145924_q -= 6.2831855F;
      }

      while(this.field_145924_q < -3.1415927F) {
         this.field_145924_q += 6.2831855F;
      }

      float var7;
      for(var7 = this.field_145924_q - this.field_145928_o; var7 >= 3.1415927F; var7 -= 6.2831855F) {
      }

      while(var7 < -3.1415927F) {
         var7 += 6.2831855F;
      }

      this.field_145928_o += var7 * 0.4F;
      this.field_145930_m = MathHelper.func_76131_a(this.field_145930_m, 0.0F, 1.0F);
      ++this.field_145926_a;
      this.field_145931_j = this.field_145933_i;
      float var3 = (this.field_145932_k - this.field_145933_i) * 0.4F;
      float var8 = 0.2F;
      var3 = MathHelper.func_76131_a(var3, -var8, var8);
      this.field_145929_l += (var3 - this.field_145929_l) * 0.9F;
      this.field_145933_i += this.field_145929_l;
   }

   public String func_70005_c_() {
      return this.func_145818_k_() ? this.field_145922_s : "container.enchant";
   }

   public boolean func_145818_k_() {
      return this.field_145922_s != null && this.field_145922_s.length() > 0;
   }

   public void func_145920_a(String var1) {
      this.field_145922_s = var1;
   }

   public IChatComponent func_145748_c_() {
      return (IChatComponent)(this.func_145818_k_() ? new ChatComponentText(this.func_70005_c_()) : new ChatComponentTranslation(this.func_70005_c_(), new Object[0]));
   }

   public Container func_174876_a(InventoryPlayer var1, EntityPlayer var2) {
      return new ContainerEnchantment(var1, this.field_145850_b, this.field_174879_c);
   }

   public String func_174875_k() {
      return "minecraft:enchanting_table";
   }
}

package net.minecraft.tileentity;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IInteractionObject;

public class TileEntityEnchantmentTable extends TileEntity implements IInteractionObject, ITickable {
   public int field_195522_a;
   public float field_195523_f;
   public float field_195524_g;
   public float field_195525_h;
   public float field_195526_i;
   public float field_195527_j;
   public float field_195528_k;
   public float field_195529_l;
   public float field_195530_m;
   public float field_195531_n;
   private static final Random field_195532_o = new Random();
   private ITextComponent field_195521_p;

   public TileEntityEnchantmentTable() {
      super(TileEntityType.field_200982_m);
   }

   public NBTTagCompound func_189515_b(NBTTagCompound var1) {
      super.func_189515_b(var1);
      if (this.func_145818_k_()) {
         var1.func_74778_a("CustomName", ITextComponent.Serializer.func_150696_a(this.field_195521_p));
      }

      return var1;
   }

   public void func_145839_a(NBTTagCompound var1) {
      super.func_145839_a(var1);
      if (var1.func_150297_b("CustomName", 8)) {
         this.field_195521_p = ITextComponent.Serializer.func_150699_a(var1.func_74779_i("CustomName"));
      }

   }

   public void func_73660_a() {
      this.field_195528_k = this.field_195527_j;
      this.field_195530_m = this.field_195529_l;
      EntityPlayer var1 = this.field_145850_b.func_184137_a((double)((float)this.field_174879_c.func_177958_n() + 0.5F), (double)((float)this.field_174879_c.func_177956_o() + 0.5F), (double)((float)this.field_174879_c.func_177952_p() + 0.5F), 3.0D, false);
      if (var1 != null) {
         double var2 = var1.field_70165_t - (double)((float)this.field_174879_c.func_177958_n() + 0.5F);
         double var4 = var1.field_70161_v - (double)((float)this.field_174879_c.func_177952_p() + 0.5F);
         this.field_195531_n = (float)MathHelper.func_181159_b(var4, var2);
         this.field_195527_j += 0.1F;
         if (this.field_195527_j < 0.5F || field_195532_o.nextInt(40) == 0) {
            float var6 = this.field_195525_h;

            do {
               this.field_195525_h += (float)(field_195532_o.nextInt(4) - field_195532_o.nextInt(4));
            } while(var6 == this.field_195525_h);
         }
      } else {
         this.field_195531_n += 0.02F;
         this.field_195527_j -= 0.1F;
      }

      while(this.field_195529_l >= 3.1415927F) {
         this.field_195529_l -= 6.2831855F;
      }

      while(this.field_195529_l < -3.1415927F) {
         this.field_195529_l += 6.2831855F;
      }

      while(this.field_195531_n >= 3.1415927F) {
         this.field_195531_n -= 6.2831855F;
      }

      while(this.field_195531_n < -3.1415927F) {
         this.field_195531_n += 6.2831855F;
      }

      float var7;
      for(var7 = this.field_195531_n - this.field_195529_l; var7 >= 3.1415927F; var7 -= 6.2831855F) {
      }

      while(var7 < -3.1415927F) {
         var7 += 6.2831855F;
      }

      this.field_195529_l += var7 * 0.4F;
      this.field_195527_j = MathHelper.func_76131_a(this.field_195527_j, 0.0F, 1.0F);
      ++this.field_195522_a;
      this.field_195524_g = this.field_195523_f;
      float var3 = (this.field_195525_h - this.field_195523_f) * 0.4F;
      float var8 = 0.2F;
      var3 = MathHelper.func_76131_a(var3, -0.2F, 0.2F);
      this.field_195526_i += (var3 - this.field_195526_i) * 0.9F;
      this.field_195523_f += this.field_195526_i;
   }

   public ITextComponent func_200200_C_() {
      return (ITextComponent)(this.field_195521_p != null ? this.field_195521_p : new TextComponentTranslation("container.enchant", new Object[0]));
   }

   public boolean func_145818_k_() {
      return this.field_195521_p != null;
   }

   public void func_200229_a(@Nullable ITextComponent var1) {
      this.field_195521_p = var1;
   }

   @Nullable
   public ITextComponent func_200201_e() {
      return this.field_195521_p;
   }

   public Container func_174876_a(InventoryPlayer var1, EntityPlayer var2) {
      return new ContainerEnchantment(var1, this.field_145850_b, this.field_174879_c);
   }

   public String func_174875_k() {
      return "minecraft:enchanting_table";
   }
}

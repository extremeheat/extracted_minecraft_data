package net.minecraft.village;

import java.io.IOException;
import java.util.ArrayList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.PacketBuffer;

public class MerchantRecipeList extends ArrayList<MerchantRecipe> {
   public MerchantRecipeList() {
      super();
   }

   public MerchantRecipeList(NBTTagCompound var1) {
      super();
      this.func_77201_a(var1);
   }

   public MerchantRecipe func_77203_a(ItemStack var1, ItemStack var2, int var3) {
      if (var3 > 0 && var3 < this.size()) {
         MerchantRecipe var6 = (MerchantRecipe)this.get(var3);
         return !this.func_181078_a(var1, var6.func_77394_a()) || (var2 != null || var6.func_77398_c()) && (!var6.func_77398_c() || !this.func_181078_a(var2, var6.func_77396_b())) || var1.field_77994_a < var6.func_77394_a().field_77994_a || var6.func_77398_c() && var2.field_77994_a < var6.func_77396_b().field_77994_a ? null : var6;
      } else {
         for(int var4 = 0; var4 < this.size(); ++var4) {
            MerchantRecipe var5 = (MerchantRecipe)this.get(var4);
            if (this.func_181078_a(var1, var5.func_77394_a()) && var1.field_77994_a >= var5.func_77394_a().field_77994_a && (!var5.func_77398_c() && var2 == null || var5.func_77398_c() && this.func_181078_a(var2, var5.func_77396_b()) && var2.field_77994_a >= var5.func_77396_b().field_77994_a)) {
               return var5;
            }
         }

         return null;
      }
   }

   private boolean func_181078_a(ItemStack var1, ItemStack var2) {
      return ItemStack.func_179545_c(var1, var2) && (!var2.func_77942_o() || var1.func_77942_o() && NBTUtil.func_181123_a(var2.func_77978_p(), var1.func_77978_p(), false));
   }

   public void func_151391_a(PacketBuffer var1) {
      var1.writeByte((byte)(this.size() & 255));

      for(int var2 = 0; var2 < this.size(); ++var2) {
         MerchantRecipe var3 = (MerchantRecipe)this.get(var2);
         var1.func_150788_a(var3.func_77394_a());
         var1.func_150788_a(var3.func_77397_d());
         ItemStack var4 = var3.func_77396_b();
         var1.writeBoolean(var4 != null);
         if (var4 != null) {
            var1.func_150788_a(var4);
         }

         var1.writeBoolean(var3.func_82784_g());
         var1.writeInt(var3.func_180321_e());
         var1.writeInt(var3.func_180320_f());
      }

   }

   public static MerchantRecipeList func_151390_b(PacketBuffer var0) throws IOException {
      MerchantRecipeList var1 = new MerchantRecipeList();
      int var2 = var0.readByte() & 255;

      for(int var3 = 0; var3 < var2; ++var3) {
         ItemStack var4 = var0.func_150791_c();
         ItemStack var5 = var0.func_150791_c();
         ItemStack var6 = null;
         if (var0.readBoolean()) {
            var6 = var0.func_150791_c();
         }

         boolean var7 = var0.readBoolean();
         int var8 = var0.readInt();
         int var9 = var0.readInt();
         MerchantRecipe var10 = new MerchantRecipe(var4, var6, var5, var8, var9);
         if (var7) {
            var10.func_82785_h();
         }

         var1.add(var10);
      }

      return var1;
   }

   public void func_77201_a(NBTTagCompound var1) {
      NBTTagList var2 = var1.func_150295_c("Recipes", 10);

      for(int var3 = 0; var3 < var2.func_74745_c(); ++var3) {
         NBTTagCompound var4 = var2.func_150305_b(var3);
         this.add(new MerchantRecipe(var4));
      }

   }

   public NBTTagCompound func_77202_a() {
      NBTTagCompound var1 = new NBTTagCompound();
      NBTTagList var2 = new NBTTagList();

      for(int var3 = 0; var3 < this.size(); ++var3) {
         MerchantRecipe var4 = (MerchantRecipe)this.get(var3);
         var2.func_74742_a(var4.func_77395_g());
      }

      var1.func_74782_a("Recipes", var2);
      return var1;
   }
}

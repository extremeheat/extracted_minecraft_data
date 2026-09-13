package net.minecraft.village;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class MerchantRecipe {
   private ItemStack field_77403_a;
   private ItemStack field_77401_b;
   private ItemStack field_77402_c;
   private int field_77400_d;
   private int field_82786_e;

   public MerchantRecipe(NBTTagCompound var1) {
      super();
      this.func_77390_a(var1);
   }

   public MerchantRecipe(ItemStack var1, ItemStack var2, ItemStack var3) {
      super();
      this.field_77403_a = var1;
      this.field_77401_b = var2;
      this.field_77402_c = var3;
      this.field_82786_e = 7;
   }

   public MerchantRecipe(ItemStack var1, ItemStack var2) {
      this(var1, null, var2);
   }

   public MerchantRecipe(ItemStack var1, Item var2) {
      this(var1, new ItemStack(var2));
   }

   public ItemStack func_77394_a() {
      return this.field_77403_a;
   }

   public ItemStack func_77396_b() {
      return this.field_77401_b;
   }

   public boolean func_77398_c() {
      return this.field_77401_b != null;
   }

   public ItemStack func_77397_d() {
      return this.field_77402_c;
   }

   public boolean func_77393_a(MerchantRecipe var1) {
      if (this.field_77403_a.func_77973_b() == var1.field_77403_a.func_77973_b() && this.field_77402_c.func_77973_b() == var1.field_77402_c.func_77973_b()) {
         return this.field_77401_b == null && var1.field_77401_b == null
            || this.field_77401_b != null && var1.field_77401_b != null && this.field_77401_b.func_77973_b() == var1.field_77401_b.func_77973_b();
      } else {
         return false;
      }
   }

   public boolean func_77391_b(MerchantRecipe var1) {
      return this.func_77393_a(var1)
         && (
            this.field_77403_a.field_77994_a < var1.field_77403_a.field_77994_a
               || this.field_77401_b != null && this.field_77401_b.field_77994_a < var1.field_77401_b.field_77994_a
         );
   }

   public void func_77399_f() {
      ++this.field_77400_d;
   }

   public void func_82783_a(int var1) {
      this.field_82786_e += var1;
   }

   public boolean func_82784_g() {
      return this.field_77400_d >= this.field_82786_e;
   }

   public void func_82785_h() {
      this.field_77400_d = this.field_82786_e;
   }

   public void func_77390_a(NBTTagCompound var1) {
      NBTTagCompound var2 = var1.func_74775_l("buy");
      this.field_77403_a = ItemStack.func_77949_a(var2);
      NBTTagCompound var3 = var1.func_74775_l("sell");
      this.field_77402_c = ItemStack.func_77949_a(var3);
      if (var1.func_150297_b("buyB", 10)) {
         this.field_77401_b = ItemStack.func_77949_a(var1.func_74775_l("buyB"));
      }

      if (var1.func_150297_b("uses", 99)) {
         this.field_77400_d = var1.func_74762_e("uses");
      }

      if (var1.func_150297_b("maxUses", 99)) {
         this.field_82786_e = var1.func_74762_e("maxUses");
      } else {
         this.field_82786_e = 7;
      }
   }

   public NBTTagCompound func_77395_g() {
      NBTTagCompound var1 = new NBTTagCompound();
      var1.func_74782_a("buy", this.field_77403_a.func_77955_b(new NBTTagCompound()));
      var1.func_74782_a("sell", this.field_77402_c.func_77955_b(new NBTTagCompound()));
      if (this.field_77401_b != null) {
         var1.func_74782_a("buyB", this.field_77401_b.func_77955_b(new NBTTagCompound()));
      }

      var1.func_74768_a("uses", this.field_77400_d);
      var1.func_74768_a("maxUses", this.field_82786_e);
      return var1;
   }
}

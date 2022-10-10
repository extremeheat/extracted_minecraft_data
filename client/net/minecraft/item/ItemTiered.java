package net.minecraft.item;

public class ItemTiered extends Item {
   private final IItemTier field_200892_a;

   public ItemTiered(IItemTier var1, Item.Properties var2) {
      super(var2.func_200915_b(var1.func_200926_a()));
      this.field_200892_a = var1;
   }

   public IItemTier func_200891_e() {
      return this.field_200892_a;
   }

   public int func_77619_b() {
      return this.field_200892_a.func_200927_e();
   }

   public boolean func_82789_a(ItemStack var1, ItemStack var2) {
      return this.field_200892_a.func_200924_f().test(var2) || super.func_82789_a(var1, var2);
   }
}

package net.minecraft.world.item;

public class TieredItem extends Item {
   private final Tier tier;

   public TieredItem(Tier var1, Item.Properties var2) {
      super(var2.durability(var1.getUses()));
      this.tier = var1;
   }

   public Tier getTier() {
      return this.tier;
   }

   public int getEnchantmentValue() {
      return this.tier.getEnchantmentValue();
   }

   public boolean isValidRepairItem(ItemStack var1, ItemStack var2) {
      return this.tier.getRepairIngredient().test(var2) || super.isValidRepairItem(var1, var2);
   }
}

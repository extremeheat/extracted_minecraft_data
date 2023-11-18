package net.minecraft.world.item.trading;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public class MerchantOffer {
   private final ItemStack baseCostA;
   private final ItemStack costB;
   private final ItemStack result;
   private int uses;
   private final int maxUses;
   private boolean rewardExp = true;
   private int specialPriceDiff;
   private int demand;
   private float priceMultiplier;
   private int xp = 1;

   public MerchantOffer(CompoundTag var1) {
      super();
      this.baseCostA = ItemStack.of(var1.getCompound("buy"));
      this.costB = ItemStack.of(var1.getCompound("buyB"));
      this.result = ItemStack.of(var1.getCompound("sell"));
      this.uses = var1.getInt("uses");
      if (var1.contains("maxUses", 99)) {
         this.maxUses = var1.getInt("maxUses");
      } else {
         this.maxUses = 4;
      }

      if (var1.contains("rewardExp", 1)) {
         this.rewardExp = var1.getBoolean("rewardExp");
      }

      if (var1.contains("xp", 3)) {
         this.xp = var1.getInt("xp");
      }

      if (var1.contains("priceMultiplier", 5)) {
         this.priceMultiplier = var1.getFloat("priceMultiplier");
      }

      this.specialPriceDiff = var1.getInt("specialPrice");
      this.demand = var1.getInt("demand");
   }

   public MerchantOffer(ItemStack var1, ItemStack var2, int var3, int var4, float var5) {
      this(var1, ItemStack.EMPTY, var2, var3, var4, var5);
   }

   public MerchantOffer(ItemStack var1, ItemStack var2, ItemStack var3, int var4, int var5, float var6) {
      this(var1, var2, var3, 0, var4, var5, var6);
   }

   public MerchantOffer(ItemStack var1, ItemStack var2, ItemStack var3, int var4, int var5, int var6, float var7) {
      this(var1, var2, var3, var4, var5, var6, var7, 0);
   }

   public MerchantOffer(ItemStack var1, ItemStack var2, ItemStack var3, int var4, int var5, int var6, float var7, int var8) {
      super();
      this.baseCostA = var1;
      this.costB = var2;
      this.result = var3;
      this.uses = var4;
      this.maxUses = var5;
      this.xp = var6;
      this.priceMultiplier = var7;
      this.demand = var8;
   }

   public ItemStack getBaseCostA() {
      return this.baseCostA;
   }

   public ItemStack getCostA() {
      if (this.baseCostA.isEmpty()) {
         return ItemStack.EMPTY;
      } else {
         int var1 = this.baseCostA.getCount();
         int var2 = Math.max(0, Mth.floor((float)(var1 * this.demand) * this.priceMultiplier));
         return this.baseCostA.copyWithCount(Mth.clamp(var1 + var2 + this.specialPriceDiff, 1, this.baseCostA.getItem().getMaxStackSize()));
      }
   }

   public ItemStack getCostB() {
      return this.costB;
   }

   public ItemStack getResult() {
      return this.result;
   }

   public void updateDemand() {
      this.demand = this.demand + this.uses - (this.maxUses - this.uses);
   }

   public ItemStack assemble() {
      return this.result.copy();
   }

   public int getUses() {
      return this.uses;
   }

   public void resetUses() {
      this.uses = 0;
   }

   public int getMaxUses() {
      return this.maxUses;
   }

   public void increaseUses() {
      ++this.uses;
   }

   public int getDemand() {
      return this.demand;
   }

   public void addToSpecialPriceDiff(int var1) {
      this.specialPriceDiff += var1;
   }

   public void resetSpecialPriceDiff() {
      this.specialPriceDiff = 0;
   }

   public int getSpecialPriceDiff() {
      return this.specialPriceDiff;
   }

   public void setSpecialPriceDiff(int var1) {
      this.specialPriceDiff = var1;
   }

   public float getPriceMultiplier() {
      return this.priceMultiplier;
   }

   public int getXp() {
      return this.xp;
   }

   public boolean isOutOfStock() {
      return this.uses >= this.maxUses;
   }

   public void setToOutOfStock() {
      this.uses = this.maxUses;
   }

   public boolean needsRestock() {
      return this.uses > 0;
   }

   public boolean shouldRewardExp() {
      return this.rewardExp;
   }

   public CompoundTag createTag() {
      CompoundTag var1 = new CompoundTag();
      var1.put("buy", this.baseCostA.save(new CompoundTag()));
      var1.put("sell", this.result.save(new CompoundTag()));
      var1.put("buyB", this.costB.save(new CompoundTag()));
      var1.putInt("uses", this.uses);
      var1.putInt("maxUses", this.maxUses);
      var1.putBoolean("rewardExp", this.rewardExp);
      var1.putInt("xp", this.xp);
      var1.putFloat("priceMultiplier", this.priceMultiplier);
      var1.putInt("specialPrice", this.specialPriceDiff);
      var1.putInt("demand", this.demand);
      return var1;
   }

   public boolean satisfiedBy(ItemStack var1, ItemStack var2) {
      return this.isRequiredItem(var1, this.getCostA())
         && var1.getCount() >= this.getCostA().getCount()
         && this.isRequiredItem(var2, this.costB)
         && var2.getCount() >= this.costB.getCount();
   }

   private boolean isRequiredItem(ItemStack var1, ItemStack var2) {
      if (var2.isEmpty() && var1.isEmpty()) {
         return true;
      } else {
         ItemStack var3 = var1.copy();
         if (var3.getItem().canBeDepleted()) {
            var3.setDamageValue(var3.getDamageValue());
         }

         return ItemStack.isSameItem(var3, var2) && (!var2.hasTag() || var3.hasTag() && NbtUtils.compareNbt(var2.getTag(), var3.getTag(), false));
      }
   }

   public boolean take(ItemStack var1, ItemStack var2) {
      if (!this.satisfiedBy(var1, var2)) {
         return false;
      } else {
         var1.shrink(this.getCostA().getCount());
         if (!this.getCostB().isEmpty()) {
            var2.shrink(this.getCostB().getCount());
         }

         return true;
      }
   }
}

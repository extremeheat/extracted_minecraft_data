package net.minecraft.world.item.trading;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public class MerchantOffer {
   public static final Codec<MerchantOffer> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(ItemCost.CODEC.fieldOf("buy").forGetter((var0x) -> {
         return var0x.baseCostA;
      }), ItemCost.CODEC.lenientOptionalFieldOf("buyB").forGetter((var0x) -> {
         return var0x.costB;
      }), ItemStack.CODEC.fieldOf("sell").forGetter((var0x) -> {
         return var0x.result;
      }), Codec.INT.lenientOptionalFieldOf("uses", 0).forGetter((var0x) -> {
         return var0x.uses;
      }), Codec.INT.lenientOptionalFieldOf("maxUses", 4).forGetter((var0x) -> {
         return var0x.maxUses;
      }), Codec.BOOL.lenientOptionalFieldOf("rewardExp", true).forGetter((var0x) -> {
         return var0x.rewardExp;
      }), Codec.INT.lenientOptionalFieldOf("specialPrice", 0).forGetter((var0x) -> {
         return var0x.specialPriceDiff;
      }), Codec.INT.lenientOptionalFieldOf("demand", 0).forGetter((var0x) -> {
         return var0x.demand;
      }), Codec.FLOAT.lenientOptionalFieldOf("priceMultiplier", 0.0F).forGetter((var0x) -> {
         return var0x.priceMultiplier;
      }), Codec.INT.lenientOptionalFieldOf("xp", 1).forGetter((var0x) -> {
         return var0x.xp;
      })).apply(var0, MerchantOffer::new);
   });
   public static final StreamCodec<RegistryFriendlyByteBuf, MerchantOffer> STREAM_CODEC = StreamCodec.of(MerchantOffer::writeToStream, MerchantOffer::createFromStream);
   private final ItemCost baseCostA;
   private final Optional<ItemCost> costB;
   private final ItemStack result;
   private int uses;
   private final int maxUses;
   private final boolean rewardExp;
   private int specialPriceDiff;
   private int demand;
   private final float priceMultiplier;
   private final int xp;

   private MerchantOffer(ItemCost var1, Optional<ItemCost> var2, ItemStack var3, int var4, int var5, boolean var6, int var7, int var8, float var9, int var10) {
      super();
      this.baseCostA = var1;
      this.costB = var2;
      this.result = var3;
      this.uses = var4;
      this.maxUses = var5;
      this.rewardExp = var6;
      this.specialPriceDiff = var7;
      this.demand = var8;
      this.priceMultiplier = var9;
      this.xp = var10;
   }

   public MerchantOffer(ItemCost var1, ItemStack var2, int var3, int var4, float var5) {
      this(var1, Optional.empty(), var2, var3, var4, var5);
   }

   public MerchantOffer(ItemCost var1, Optional<ItemCost> var2, ItemStack var3, int var4, int var5, float var6) {
      this(var1, var2, var3, 0, var4, var5, var6);
   }

   public MerchantOffer(ItemCost var1, Optional<ItemCost> var2, ItemStack var3, int var4, int var5, int var6, float var7) {
      this(var1, var2, var3, var4, var5, var6, var7, 0);
   }

   public MerchantOffer(ItemCost var1, Optional<ItemCost> var2, ItemStack var3, int var4, int var5, int var6, float var7, int var8) {
      this(var1, var2, var3, var4, var5, true, 0, var8, var7, var6);
   }

   private MerchantOffer(MerchantOffer var1) {
      this(var1.baseCostA, var1.costB, var1.result.copy(), var1.uses, var1.maxUses, var1.rewardExp, var1.specialPriceDiff, var1.demand, var1.priceMultiplier, var1.xp);
   }

   public ItemStack getBaseCostA() {
      return this.baseCostA.itemStack();
   }

   public ItemStack getCostA() {
      return this.baseCostA.itemStack().copyWithCount(this.getModifiedCostCount(this.baseCostA));
   }

   private int getModifiedCostCount(ItemCost var1) {
      int var2 = var1.count();
      int var3 = Math.max(0, Mth.floor((float)(var2 * this.demand) * this.priceMultiplier));
      return Mth.clamp(var2 + var3 + this.specialPriceDiff, 1, var1.itemStack().getMaxStackSize());
   }

   public ItemStack getCostB() {
      return (ItemStack)this.costB.map(ItemCost::itemStack).orElse(ItemStack.EMPTY);
   }

   public ItemCost getItemCostA() {
      return this.baseCostA;
   }

   public Optional<ItemCost> getItemCostB() {
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

   public boolean satisfiedBy(ItemStack var1, ItemStack var2) {
      if (this.baseCostA.test(var1) && var1.getCount() >= this.getModifiedCostCount(this.baseCostA)) {
         if (!this.costB.isPresent()) {
            return var2.isEmpty();
         } else {
            return ((ItemCost)this.costB.get()).test(var2) && var2.getCount() >= ((ItemCost)this.costB.get()).count();
         }
      } else {
         return false;
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

   public MerchantOffer copy() {
      return new MerchantOffer(this);
   }

   private static void writeToStream(RegistryFriendlyByteBuf var0, MerchantOffer var1) {
      ItemCost.STREAM_CODEC.encode(var0, var1.getItemCostA());
      ItemStack.STREAM_CODEC.encode(var0, var1.getResult());
      ItemCost.OPTIONAL_STREAM_CODEC.encode(var0, var1.getItemCostB());
      var0.writeBoolean(var1.isOutOfStock());
      var0.writeInt(var1.getUses());
      var0.writeInt(var1.getMaxUses());
      var0.writeInt(var1.getXp());
      var0.writeInt(var1.getSpecialPriceDiff());
      var0.writeFloat(var1.getPriceMultiplier());
      var0.writeInt(var1.getDemand());
   }

   public static MerchantOffer createFromStream(RegistryFriendlyByteBuf var0) {
      ItemCost var1 = (ItemCost)ItemCost.STREAM_CODEC.decode(var0);
      ItemStack var2 = (ItemStack)ItemStack.STREAM_CODEC.decode(var0);
      Optional var3 = (Optional)ItemCost.OPTIONAL_STREAM_CODEC.decode(var0);
      boolean var4 = var0.readBoolean();
      int var5 = var0.readInt();
      int var6 = var0.readInt();
      int var7 = var0.readInt();
      int var8 = var0.readInt();
      float var9 = var0.readFloat();
      int var10 = var0.readInt();
      MerchantOffer var11 = new MerchantOffer(var1, var3, var2, var5, var6, var7, var9, var10);
      if (var4) {
         var11.setToOutOfStock();
      }

      var11.setSpecialPriceDiff(var8);
      return var11;
   }
}

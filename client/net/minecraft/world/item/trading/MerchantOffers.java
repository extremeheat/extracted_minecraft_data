package net.minecraft.world.item.trading;

import java.util.ArrayList;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public class MerchantOffers extends ArrayList<MerchantOffer> {
   public MerchantOffers() {
      super();
   }

   public MerchantOffers(CompoundTag var1) {
      super();
      ListTag var2 = var1.getList("Recipes", 10);

      for(int var3 = 0; var3 < var2.size(); ++var3) {
         this.add(new MerchantOffer(var2.getCompound(var3)));
      }

   }

   @Nullable
   public MerchantOffer getRecipeFor(ItemStack var1, ItemStack var2, int var3) {
      if (var3 > 0 && var3 < this.size()) {
         MerchantOffer var6 = (MerchantOffer)this.get(var3);
         return var6.satisfiedBy(var1, var2) ? var6 : null;
      } else {
         for(int var4 = 0; var4 < this.size(); ++var4) {
            MerchantOffer var5 = (MerchantOffer)this.get(var4);
            if (var5.satisfiedBy(var1, var2)) {
               return var5;
            }
         }

         return null;
      }
   }

   public void writeToStream(FriendlyByteBuf var1) {
      var1.writeByte((byte)(this.size() & 255));

      for(int var2 = 0; var2 < this.size(); ++var2) {
         MerchantOffer var3 = (MerchantOffer)this.get(var2);
         var1.writeItem(var3.getBaseCostA());
         var1.writeItem(var3.getResult());
         ItemStack var4 = var3.getCostB();
         var1.writeBoolean(!var4.isEmpty());
         if (!var4.isEmpty()) {
            var1.writeItem(var4);
         }

         var1.writeBoolean(var3.isOutOfStock());
         var1.writeInt(var3.getUses());
         var1.writeInt(var3.getMaxUses());
         var1.writeInt(var3.getXp());
         var1.writeInt(var3.getSpecialPriceDiff());
         var1.writeFloat(var3.getPriceMultiplier());
         var1.writeInt(var3.getDemand());
      }

   }

   public static MerchantOffers createFromStream(FriendlyByteBuf var0) {
      MerchantOffers var1 = new MerchantOffers();
      int var2 = var0.readByte() & 255;

      for(int var3 = 0; var3 < var2; ++var3) {
         ItemStack var4 = var0.readItem();
         ItemStack var5 = var0.readItem();
         ItemStack var6 = ItemStack.EMPTY;
         if (var0.readBoolean()) {
            var6 = var0.readItem();
         }

         boolean var7 = var0.readBoolean();
         int var8 = var0.readInt();
         int var9 = var0.readInt();
         int var10 = var0.readInt();
         int var11 = var0.readInt();
         float var12 = var0.readFloat();
         int var13 = var0.readInt();
         MerchantOffer var14 = new MerchantOffer(var4, var6, var5, var8, var9, var10, var12, var13);
         if (var7) {
            var14.setToOutOfStock();
         }

         var14.setSpecialPriceDiff(var11);
         var1.add(var14);
      }

      return var1;
   }

   public CompoundTag createTag() {
      CompoundTag var1 = new CompoundTag();
      ListTag var2 = new ListTag();

      for(int var3 = 0; var3 < this.size(); ++var3) {
         MerchantOffer var4 = (MerchantOffer)this.get(var3);
         var2.add(var4.createTag());
      }

      var1.put("Recipes", var2);
      return var1;
   }
}

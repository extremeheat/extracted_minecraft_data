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

   private MerchantOffers(int var1) {
      super(var1);
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
         MerchantOffer var6 = this.get(var3);
         return var6.satisfiedBy(var1, var2) ? var6 : null;
      } else {
         for(int var4 = 0; var4 < this.size(); ++var4) {
            MerchantOffer var5 = this.get(var4);
            if (var5.satisfiedBy(var1, var2)) {
               return var5;
            }
         }

         return null;
      }
   }

   public void writeToStream(FriendlyByteBuf var1) {
      var1.writeCollection(this, (var0, var1x) -> {
         var0.writeItem(var1x.getBaseCostA());
         var0.writeItem(var1x.getResult());
         var0.writeItem(var1x.getCostB());
         var0.writeBoolean(var1x.isOutOfStock());
         var0.writeInt(var1x.getUses());
         var0.writeInt(var1x.getMaxUses());
         var0.writeInt(var1x.getXp());
         var0.writeInt(var1x.getSpecialPriceDiff());
         var0.writeFloat(var1x.getPriceMultiplier());
         var0.writeInt(var1x.getDemand());
      });
   }

   public static MerchantOffers createFromStream(FriendlyByteBuf var0) {
      return var0.readCollection(MerchantOffers::new, var0x -> {
         ItemStack var1 = var0x.readItem();
         ItemStack var2 = var0x.readItem();
         ItemStack var3 = var0x.readItem();
         boolean var4 = var0x.readBoolean();
         int var5 = var0x.readInt();
         int var6 = var0x.readInt();
         int var7 = var0x.readInt();
         int var8 = var0x.readInt();
         float var9 = var0x.readFloat();
         int var10 = var0x.readInt();
         MerchantOffer var11 = new MerchantOffer(var1, var3, var2, var5, var6, var7, var9, var10);
         if (var4) {
            var11.setToOutOfStock();
         }

         var11.setSpecialPriceDiff(var8);
         return var11;
      });
   }

   public CompoundTag createTag() {
      CompoundTag var1 = new CompoundTag();
      ListTag var2 = new ListTag();

      for(int var3 = 0; var3 < this.size(); ++var3) {
         MerchantOffer var4 = this.get(var3);
         var2.add(var4.createTag());
      }

      var1.put("Recipes", var2);
      return var1;
   }
}

package net.minecraft.world.item.trading;

import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public class MerchantOffers extends ArrayList<MerchantOffer> {
   public static final Codec<MerchantOffers> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, MerchantOffers> STREAM_CODEC;

   public MerchantOffers() {
      super();
   }

   private MerchantOffers(int var1) {
      super(var1);
   }

   private MerchantOffers(Collection<MerchantOffer> var1) {
      super(var1);
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

   public MerchantOffers copy() {
      MerchantOffers var1 = new MerchantOffers(this.size());
      Iterator var2 = this.iterator();

      while(var2.hasNext()) {
         MerchantOffer var3 = (MerchantOffer)var2.next();
         var1.add(var3.copy());
      }

      return var1;
   }

   static {
      CODEC = MerchantOffer.CODEC.listOf().fieldOf("Recipes").xmap(MerchantOffers::new, Function.identity()).codec();
      STREAM_CODEC = MerchantOffer.STREAM_CODEC.apply(ByteBufCodecs.collection(MerchantOffers::new));
   }
}

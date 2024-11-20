package net.minecraft.client.renderer.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ClientItem(ItemModel.Unbaked model, Properties properties) {
   public static final Codec<ClientItem> CODEC = RecordCodecBuilder.create((var0) -> var0.group(ItemModels.CODEC.fieldOf("model").forGetter(ClientItem::model), ClientItem.Properties.MAP_CODEC.forGetter(ClientItem::properties)).apply(var0, ClientItem::new));

   public ClientItem(ItemModel.Unbaked var1, Properties var2) {
      super();
      this.model = var1;
      this.properties = var2;
   }

   public static record Properties(boolean handAnimationOnSwap) {
      public static final Properties DEFAULT = new Properties(true);
      public static final MapCodec<Properties> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(Codec.BOOL.optionalFieldOf("hand_animation_on_swap", true).forGetter(Properties::handAnimationOnSwap)).apply(var0, Properties::new));

      public Properties(boolean var1) {
         super();
         this.handAnimationOnSwap = var1;
      }
   }
}

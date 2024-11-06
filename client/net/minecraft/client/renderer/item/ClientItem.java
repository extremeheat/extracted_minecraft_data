package net.minecraft.client.renderer.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ClientItem(ItemModel.Unbaked model) {
   public static final Codec<ClientItem> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(ItemModels.CODEC.fieldOf("model").forGetter(ClientItem::model)).apply(var0, ClientItem::new);
   });

   public ClientItem(ItemModel.Unbaked var1) {
      super();
      this.model = var1;
   }

   public ItemModel.Unbaked model() {
      return this.model;
   }
}

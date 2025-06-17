package net.minecraft.client.renderer.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.Nullable;
import net.minecraft.util.RegistryContextSwapper;

public record ClientItem(ItemModel.Unbaked model, Properties properties, @Nullable RegistryContextSwapper registrySwapper) {
   public static final Codec<ClientItem> CODEC = RecordCodecBuilder.create((var0) -> var0.group(ItemModels.CODEC.fieldOf("model").forGetter(ClientItem::model), ClientItem.Properties.MAP_CODEC.forGetter(ClientItem::properties)).apply(var0, ClientItem::new));

   public ClientItem(ItemModel.Unbaked var1, Properties var2) {
      this(var1, var2, (RegistryContextSwapper)null);
   }

   public ClientItem(ItemModel.Unbaked var1, Properties var2, @Nullable RegistryContextSwapper var3) {
      super();
      this.model = var1;
      this.properties = var2;
      this.registrySwapper = var3;
   }

   public ClientItem withRegistrySwapper(RegistryContextSwapper var1) {
      return new ClientItem(this.model, this.properties, var1);
   }

   public static record Properties(boolean handAnimationOnSwap, boolean oversizedInGui) {
      public static final Properties DEFAULT = new Properties(true, false);
      public static final MapCodec<Properties> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(Codec.BOOL.optionalFieldOf("hand_animation_on_swap", true).forGetter(Properties::handAnimationOnSwap), Codec.BOOL.optionalFieldOf("oversized_in_gui", false).forGetter(Properties::oversizedInGui)).apply(var0, Properties::new));

      public Properties(boolean var1, boolean var2) {
         super();
         this.handAnimationOnSwap = var1;
         this.oversizedInGui = var2;
      }
   }
}

package net.minecraft.world.entity.variant;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.ClientAsset;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public record ModelAndTexture<T>(T model, ClientAsset.ResourceTexture asset) {
   public ModelAndTexture(T var1, Identifier var2) {
      this(var1, (ClientAsset.ResourceTexture)(new ClientAsset.ResourceTexture(var2)));
   }

   public ModelAndTexture(T var1, ClientAsset.ResourceTexture var2) {
      super();
      this.model = var1;
      this.asset = var2;
   }

   public static <T> MapCodec<ModelAndTexture<T>> codec(Codec<T> var0, T var1) {
      return RecordCodecBuilder.mapCodec((var2) -> var2.group(var0.optionalFieldOf("model", var1).forGetter(ModelAndTexture::model), ClientAsset.ResourceTexture.DEFAULT_FIELD_CODEC.forGetter(ModelAndTexture::asset)).apply(var2, ModelAndTexture::new));
   }

   public static <T> StreamCodec<RegistryFriendlyByteBuf, ModelAndTexture<T>> streamCodec(StreamCodec<? super RegistryFriendlyByteBuf, T> var0) {
      return StreamCodec.composite(var0, ModelAndTexture::model, ClientAsset.ResourceTexture.STREAM_CODEC, ModelAndTexture::asset, ModelAndTexture::new);
   }
}

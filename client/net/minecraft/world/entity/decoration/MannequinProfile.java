package net.minecraft.world.entity.decoration;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.item.component.ResolvableProfile;

public record MannequinProfile(ResourceLocation texture, Optional<ResourceLocation> capeTexture, Optional<ResourceLocation> elytraTexture, PlayerModelType model) {
   public static final Codec<MannequinProfile> CODEC = RecordCodecBuilder.create((var0) -> var0.group(ResourceLocation.CODEC.fieldOf("texture").forGetter(MannequinProfile::texture), ResourceLocation.CODEC.optionalFieldOf("cape").forGetter(MannequinProfile::capeTexture), ResourceLocation.CODEC.optionalFieldOf("elytra").forGetter(MannequinProfile::elytraTexture), PlayerModelType.CODEC.optionalFieldOf("model", PlayerModelType.WIDE).forGetter(MannequinProfile::model)).apply(var0, MannequinProfile::new));
   public static final StreamCodec<ByteBuf, MannequinProfile> STREAM_CODEC;
   public static final Codec<Either<MannequinProfile, ResolvableProfile>> PLAYER_OR_MANNEQUIN_CODEC;
   public static final StreamCodec<ByteBuf, Either<MannequinProfile, ResolvableProfile>> PLAYER_OR_MANNEQUIN_STREAM_CODEC;

   public MannequinProfile(ResourceLocation var1, Optional<ResourceLocation> var2, Optional<ResourceLocation> var3, PlayerModelType var4) {
      super();
      this.texture = var1;
      this.capeTexture = var2;
      this.elytraTexture = var3;
      this.model = var4;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ResourceLocation.STREAM_CODEC, MannequinProfile::texture, ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs::optional), MannequinProfile::capeTexture, ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs::optional), MannequinProfile::elytraTexture, PlayerModelType.STREAM_CODEC, MannequinProfile::model, MannequinProfile::new);
      PLAYER_OR_MANNEQUIN_CODEC = Codec.either(CODEC, ResolvableProfile.CODEC);
      PLAYER_OR_MANNEQUIN_STREAM_CODEC = ByteBufCodecs.either(STREAM_CODEC, ResolvableProfile.STREAM_CODEC);
   }
}

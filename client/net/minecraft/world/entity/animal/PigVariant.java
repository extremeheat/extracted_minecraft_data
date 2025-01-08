package net.minecraft.world.entity.animal;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import java.util.Optional;
import java.util.function.IntFunction;
import java.util.function.UnaryOperator;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.biome.Biome;

public class PigVariant {
   public static final Codec<PigVariant> DIRECT_CODEC = RecordCodecBuilder.create((var0) -> var0.group(PigVariant.ModelType.CODEC.optionalFieldOf("model", PigVariant.ModelType.NORMAL).forGetter((var0x) -> var0x.model), ResourceLocation.CODEC.fieldOf("texture").forGetter((var0x) -> var0x.texture), RegistryCodecs.homogeneousList(Registries.BIOME).optionalFieldOf("biomes").forGetter((var0x) -> var0x.biomes)).apply(var0, PigVariant::new));
   public static final StreamCodec<RegistryFriendlyByteBuf, PigVariant> DIRECT_STREAM_CODEC;
   public static final Codec<Holder<PigVariant>> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, Holder<PigVariant>> STREAM_CODEC;
   private final ModelType model;
   private final ResourceLocation texture;
   private final ResourceLocation fullTexture;
   private final Optional<HolderSet<Biome>> biomes;

   public PigVariant(ModelType var1, ResourceLocation var2, Optional<HolderSet<Biome>> var3) {
      super();
      this.model = var1;
      this.texture = var2;
      this.fullTexture = var2.withPath((UnaryOperator)((var0) -> "textures/" + var0 + ".png"));
      this.biomes = var3;
   }

   public ModelType model() {
      return this.model;
   }

   public ResourceLocation fullTexture() {
      return this.fullTexture;
   }

   public Optional<HolderSet<Biome>> biomes() {
      return this.biomes;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof PigVariant var2)) {
         return false;
      } else {
         return this.model.equals(var2.model) && this.texture.equals(var2.texture) && this.biomes.equals(var2.biomes);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.model, this.texture, this.biomes});
   }

   public String toString() {
      String var10000 = String.valueOf(this.model);
      return "PigVariant[model=" + var10000 + ", texture=" + String.valueOf(this.texture) + ", biomes=" + String.valueOf(this.biomes) + "]";
   }

   static {
      DIRECT_STREAM_CODEC = StreamCodec.composite(PigVariant.ModelType.STREAM_CODEC, (var0) -> var0.model, ResourceLocation.STREAM_CODEC, (var0) -> var0.texture, ByteBufCodecs.holderSet(Registries.BIOME).apply(ByteBufCodecs::optional), (var0) -> var0.biomes, PigVariant::new);
      CODEC = RegistryFileCodec.<Holder<PigVariant>>create(Registries.PIG_VARIANT, DIRECT_CODEC);
      STREAM_CODEC = ByteBufCodecs.holder(Registries.PIG_VARIANT, DIRECT_STREAM_CODEC);
   }

   public static enum ModelType implements StringRepresentable {
      NORMAL(0, "normal"),
      COLD(1, "cold");

      public static final Codec<ModelType> CODEC = StringRepresentable.<ModelType>fromEnum(ModelType::values);
      private static final IntFunction<ModelType> BY_ID = ByIdMap.<ModelType>continuous((var0) -> var0.id, values(), ByIdMap.OutOfBoundsStrategy.CLAMP);
      public static final StreamCodec<ByteBuf, ModelType> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, (var0) -> var0.id);
      private final int id;
      private final String name;

      private ModelType(final int var3, final String var4) {
         this.id = var3;
         this.name = var4;
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static ModelType[] $values() {
         return new ModelType[]{NORMAL, COLD};
      }
   }
}

package net.minecraft.world.entity.animal.chicken;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.variant.ModelAndTexture;
import net.minecraft.world.entity.variant.PriorityProvider;
import net.minecraft.world.entity.variant.SpawnCondition;
import net.minecraft.world.entity.variant.SpawnContext;
import net.minecraft.world.entity.variant.SpawnPrioritySelectors;

public record ChickenVariant(ModelAndTexture<ModelType> modelAndTexture, SpawnPrioritySelectors spawnConditions) implements PriorityProvider<SpawnContext, SpawnCondition> {
   public static final Codec<ChickenVariant> DIRECT_CODEC = RecordCodecBuilder.create((var0) -> var0.group(ModelAndTexture.codec(ChickenVariant.ModelType.CODEC, ChickenVariant.ModelType.NORMAL).forGetter(ChickenVariant::modelAndTexture), SpawnPrioritySelectors.CODEC.fieldOf("spawn_conditions").forGetter(ChickenVariant::spawnConditions)).apply(var0, ChickenVariant::new));
   public static final Codec<ChickenVariant> NETWORK_CODEC = RecordCodecBuilder.create((var0) -> var0.group(ModelAndTexture.codec(ChickenVariant.ModelType.CODEC, ChickenVariant.ModelType.NORMAL).forGetter(ChickenVariant::modelAndTexture)).apply(var0, ChickenVariant::new));
   public static final Codec<Holder<ChickenVariant>> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, Holder<ChickenVariant>> STREAM_CODEC;

   private ChickenVariant(ModelAndTexture<ModelType> var1) {
      this(var1, SpawnPrioritySelectors.EMPTY);
   }

   public ChickenVariant(ModelAndTexture<ModelType> var1, SpawnPrioritySelectors var2) {
      super();
      this.modelAndTexture = var1;
      this.spawnConditions = var2;
   }

   public List<PriorityProvider.Selector<SpawnContext, SpawnCondition>> selectors() {
      return this.spawnConditions.selectors();
   }

   static {
      CODEC = RegistryFixedCodec.<Holder<ChickenVariant>>create(Registries.CHICKEN_VARIANT);
      STREAM_CODEC = ByteBufCodecs.holderRegistry(Registries.CHICKEN_VARIANT);
   }

   public static enum ModelType implements StringRepresentable {
      NORMAL("normal"),
      COLD("cold");

      public static final Codec<ModelType> CODEC = StringRepresentable.<ModelType>fromEnum(ModelType::values);
      private final String name;

      private ModelType(final String var3) {
         this.name = var3;
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

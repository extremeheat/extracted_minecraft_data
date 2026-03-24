package net.minecraft.world.entity.animal.pig;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.ClientAsset;
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

public record PigVariant(ModelAndTexture<ModelType> modelAndTexture, ClientAsset.ResourceTexture babyTexture, SpawnPrioritySelectors spawnConditions) implements PriorityProvider<SpawnContext, SpawnCondition> {
   public static final Codec<PigVariant> DIRECT_CODEC = RecordCodecBuilder.create((i) -> i.group(ModelAndTexture.codec(PigVariant.ModelType.CODEC, PigVariant.ModelType.NORMAL).forGetter(PigVariant::modelAndTexture), ClientAsset.ResourceTexture.CODEC.fieldOf("baby_asset_id").forGetter(PigVariant::babyTexture), SpawnPrioritySelectors.CODEC.fieldOf("spawn_conditions").forGetter(PigVariant::spawnConditions)).apply(i, PigVariant::new));
   public static final Codec<PigVariant> NETWORK_CODEC = RecordCodecBuilder.create((i) -> i.group(ModelAndTexture.codec(PigVariant.ModelType.CODEC, PigVariant.ModelType.NORMAL).forGetter(PigVariant::modelAndTexture), ClientAsset.ResourceTexture.CODEC.fieldOf("baby_asset_id").forGetter(PigVariant::babyTexture)).apply(i, PigVariant::new));
   public static final Codec<Holder<PigVariant>> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, Holder<PigVariant>> STREAM_CODEC;

   private PigVariant(final ModelAndTexture<ModelType> assetInfo, final ClientAsset.ResourceTexture babyTexture) {
      this(assetInfo, babyTexture, SpawnPrioritySelectors.EMPTY);
   }

   public PigVariant {
      super();
   }

   public List<PriorityProvider.Selector<SpawnContext, SpawnCondition>> selectors() {
      return this.spawnConditions.selectors();
   }

   static {
      CODEC = RegistryFixedCodec.<Holder<PigVariant>>create(Registries.PIG_VARIANT);
      STREAM_CODEC = ByteBufCodecs.holderRegistry(Registries.PIG_VARIANT);
   }

   public static enum ModelType implements StringRepresentable {
      NORMAL("normal"),
      COLD("cold");

      public static final Codec<ModelType> CODEC = StringRepresentable.<ModelType>fromEnum(ModelType::values);
      private final String name;

      private ModelType(final String name) {
         this.name = name;
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

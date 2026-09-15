package net.minecraft.world.entity.animal.nautilus;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.variant.ModelAndTexture;
import net.minecraft.world.entity.variant.PriorityProvider;
import net.minecraft.world.entity.variant.SpawnCondition;
import net.minecraft.world.entity.variant.SpawnContext;
import net.minecraft.world.entity.variant.SpawnPrioritySelectors;

public record ZombieNautilusVariant(ModelAndTexture<ModelType> modelAndTexture, SpawnPrioritySelectors spawnConditions) implements PriorityProvider<SpawnContext, SpawnCondition> {
   public static final Codec<ZombieNautilusVariant> DIRECT_CODEC = RecordCodecBuilder.create((i) -> i.group(ModelAndTexture.codec(ZombieNautilusVariant.ModelType.CODEC, ZombieNautilusVariant.ModelType.NORMAL).forGetter(ZombieNautilusVariant::modelAndTexture), SpawnPrioritySelectors.CODEC.fieldOf("spawn_conditions").forGetter(ZombieNautilusVariant::spawnConditions)).apply(i, ZombieNautilusVariant::new));
   public static final Codec<ZombieNautilusVariant> NETWORK_CODEC = RecordCodecBuilder.create((i) -> i.group(ModelAndTexture.codec(ZombieNautilusVariant.ModelType.CODEC, ZombieNautilusVariant.ModelType.NORMAL).forGetter(ZombieNautilusVariant::modelAndTexture)).apply(i, ZombieNautilusVariant::new));
   public static final Codec<Holder<ZombieNautilusVariant>> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, Holder<ZombieNautilusVariant>> STREAM_CODEC;

   private ZombieNautilusVariant(final ModelAndTexture<ModelType> assetInfo) {
      this(assetInfo, SpawnPrioritySelectors.EMPTY);
   }

   public ZombieNautilusVariant {
      super();
   }

   public List<PriorityProvider.Selector<SpawnContext, SpawnCondition>> selectors() {
      return this.spawnConditions.selectors();
   }

   static {
      CODEC = RegistryCodecs.holder(Registries.ZOMBIE_NAUTILUS_VARIANT);
      STREAM_CODEC = ByteBufCodecs.holderRegistry(Registries.ZOMBIE_NAUTILUS_VARIANT);
   }

   public static enum ModelType implements StringRepresentable {
      NORMAL("normal"),
      WARM("warm");

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
         return new ModelType[]{NORMAL, WARM};
      }
   }
}

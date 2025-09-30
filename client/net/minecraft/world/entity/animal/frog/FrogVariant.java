package net.minecraft.world.entity.animal.frog;

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
import net.minecraft.world.entity.variant.PriorityProvider;
import net.minecraft.world.entity.variant.SpawnCondition;
import net.minecraft.world.entity.variant.SpawnContext;
import net.minecraft.world.entity.variant.SpawnPrioritySelectors;

public record FrogVariant(ClientAsset.ResourceTexture assetInfo, SpawnPrioritySelectors spawnConditions) implements PriorityProvider<SpawnContext, SpawnCondition> {
   public static final Codec<FrogVariant> DIRECT_CODEC = RecordCodecBuilder.create((var0) -> var0.group(ClientAsset.ResourceTexture.DEFAULT_FIELD_CODEC.forGetter(FrogVariant::assetInfo), SpawnPrioritySelectors.CODEC.fieldOf("spawn_conditions").forGetter(FrogVariant::spawnConditions)).apply(var0, FrogVariant::new));
   public static final Codec<FrogVariant> NETWORK_CODEC = RecordCodecBuilder.create((var0) -> var0.group(ClientAsset.ResourceTexture.DEFAULT_FIELD_CODEC.forGetter(FrogVariant::assetInfo)).apply(var0, FrogVariant::new));
   public static final Codec<Holder<FrogVariant>> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, Holder<FrogVariant>> STREAM_CODEC;

   private FrogVariant(ClientAsset.ResourceTexture var1) {
      this(var1, SpawnPrioritySelectors.EMPTY);
   }

   public FrogVariant(ClientAsset.ResourceTexture var1, SpawnPrioritySelectors var2) {
      super();
      this.assetInfo = var1;
      this.spawnConditions = var2;
   }

   public List<PriorityProvider.Selector<SpawnContext, SpawnCondition>> selectors() {
      return this.spawnConditions.selectors();
   }

   static {
      CODEC = RegistryFixedCodec.<Holder<FrogVariant>>create(Registries.FROG_VARIANT);
      STREAM_CODEC = ByteBufCodecs.holderRegistry(Registries.FROG_VARIANT);
   }
}

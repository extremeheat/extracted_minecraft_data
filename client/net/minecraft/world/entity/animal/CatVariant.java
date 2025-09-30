package net.minecraft.world.entity.animal;

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

public record CatVariant(ClientAsset.ResourceTexture assetInfo, SpawnPrioritySelectors spawnConditions) implements PriorityProvider<SpawnContext, SpawnCondition> {
   public static final Codec<CatVariant> DIRECT_CODEC = RecordCodecBuilder.create((var0) -> var0.group(ClientAsset.ResourceTexture.DEFAULT_FIELD_CODEC.forGetter(CatVariant::assetInfo), SpawnPrioritySelectors.CODEC.fieldOf("spawn_conditions").forGetter(CatVariant::spawnConditions)).apply(var0, CatVariant::new));
   public static final Codec<CatVariant> NETWORK_CODEC = RecordCodecBuilder.create((var0) -> var0.group(ClientAsset.ResourceTexture.DEFAULT_FIELD_CODEC.forGetter(CatVariant::assetInfo)).apply(var0, CatVariant::new));
   public static final Codec<Holder<CatVariant>> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, Holder<CatVariant>> STREAM_CODEC;

   private CatVariant(ClientAsset.ResourceTexture var1) {
      this(var1, SpawnPrioritySelectors.EMPTY);
   }

   public CatVariant(ClientAsset.ResourceTexture var1, SpawnPrioritySelectors var2) {
      super();
      this.assetInfo = var1;
      this.spawnConditions = var2;
   }

   public List<PriorityProvider.Selector<SpawnContext, SpawnCondition>> selectors() {
      return this.spawnConditions.selectors();
   }

   static {
      CODEC = RegistryFixedCodec.<Holder<CatVariant>>create(Registries.CAT_VARIANT);
      STREAM_CODEC = ByteBufCodecs.holderRegistry(Registries.CAT_VARIANT);
   }
}

package net.minecraft.world.entity.animal.wolf;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.ClientAsset;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.variant.PriorityProvider;
import net.minecraft.world.entity.variant.SpawnCondition;
import net.minecraft.world.entity.variant.SpawnContext;
import net.minecraft.world.entity.variant.SpawnPrioritySelectors;

public record WolfVariant(AssetInfo adultInfo, AssetInfo babyInfo, SpawnPrioritySelectors spawnConditions) implements PriorityProvider<SpawnContext, SpawnCondition> {
   public static final Codec<WolfVariant> DIRECT_CODEC = RecordCodecBuilder.create((i) -> i.group(WolfVariant.AssetInfo.CODEC.fieldOf("assets").forGetter(WolfVariant::adultInfo), WolfVariant.AssetInfo.CODEC.fieldOf("baby_assets").forGetter(WolfVariant::babyInfo), SpawnPrioritySelectors.CODEC.fieldOf("spawn_conditions").forGetter(WolfVariant::spawnConditions)).apply(i, WolfVariant::new));
   public static final Codec<WolfVariant> NETWORK_CODEC = RecordCodecBuilder.create((i) -> i.group(WolfVariant.AssetInfo.CODEC.fieldOf("assets").forGetter(WolfVariant::adultInfo), WolfVariant.AssetInfo.CODEC.fieldOf("baby_assets").forGetter(WolfVariant::babyInfo)).apply(i, WolfVariant::new));
   public static final Codec<Holder<WolfVariant>> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, Holder<WolfVariant>> STREAM_CODEC;

   private WolfVariant(final AssetInfo adultInfo, final AssetInfo babyInfo) {
      this(adultInfo, babyInfo, SpawnPrioritySelectors.EMPTY);
   }

   public WolfVariant {
      super();
   }

   public List<PriorityProvider.Selector<SpawnContext, SpawnCondition>> selectors() {
      return this.spawnConditions.selectors();
   }

   static {
      CODEC = RegistryCodecs.holder(Registries.WOLF_VARIANT);
      STREAM_CODEC = ByteBufCodecs.holderRegistry(Registries.WOLF_VARIANT);
   }

   public static record AssetInfo(ClientAsset.ResourceTexture wild, ClientAsset.ResourceTexture tame, ClientAsset.ResourceTexture angry) {
      public static final Codec<AssetInfo> CODEC = RecordCodecBuilder.create((instance) -> instance.group(ClientAsset.ResourceTexture.CODEC.fieldOf("wild").forGetter(AssetInfo::wild), ClientAsset.ResourceTexture.CODEC.fieldOf("tame").forGetter(AssetInfo::tame), ClientAsset.ResourceTexture.CODEC.fieldOf("angry").forGetter(AssetInfo::angry)).apply(instance, AssetInfo::new));

      public AssetInfo {
         super();
      }
   }
}

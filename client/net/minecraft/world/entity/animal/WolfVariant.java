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

public record WolfVariant(AssetInfo assetInfo, SpawnPrioritySelectors spawnConditions) implements PriorityProvider<SpawnContext, SpawnCondition> {
   public static final Codec<WolfVariant> DIRECT_CODEC = RecordCodecBuilder.create((var0) -> var0.group(WolfVariant.AssetInfo.CODEC.fieldOf("assets").forGetter(WolfVariant::assetInfo), SpawnPrioritySelectors.CODEC.fieldOf("spawn_conditions").forGetter(WolfVariant::spawnConditions)).apply(var0, WolfVariant::new));
   public static final Codec<WolfVariant> NETWORK_CODEC = RecordCodecBuilder.create((var0) -> var0.group(WolfVariant.AssetInfo.CODEC.fieldOf("assets").forGetter(WolfVariant::assetInfo)).apply(var0, WolfVariant::new));
   public static final Codec<Holder<WolfVariant>> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, Holder<WolfVariant>> STREAM_CODEC;

   private WolfVariant(AssetInfo var1) {
      this(var1, SpawnPrioritySelectors.EMPTY);
   }

   public WolfVariant(AssetInfo var1, SpawnPrioritySelectors var2) {
      super();
      this.assetInfo = var1;
      this.spawnConditions = var2;
   }

   public List<PriorityProvider.Selector<SpawnContext, SpawnCondition>> selectors() {
      return this.spawnConditions.selectors();
   }

   static {
      CODEC = RegistryFixedCodec.<Holder<WolfVariant>>create(Registries.WOLF_VARIANT);
      STREAM_CODEC = ByteBufCodecs.holderRegistry(Registries.WOLF_VARIANT);
   }

   public static record AssetInfo(ClientAsset wild, ClientAsset tame, ClientAsset angry) {
      public static final Codec<AssetInfo> CODEC = RecordCodecBuilder.create((var0) -> var0.group(ClientAsset.CODEC.fieldOf("wild").forGetter(AssetInfo::wild), ClientAsset.CODEC.fieldOf("tame").forGetter(AssetInfo::tame), ClientAsset.CODEC.fieldOf("angry").forGetter(AssetInfo::angry)).apply(var0, AssetInfo::new));

      public AssetInfo(ClientAsset var1, ClientAsset var2, ClientAsset var3) {
         super();
         this.wild = var1;
         this.tame = var2;
         this.angry = var3;
      }
   }
}

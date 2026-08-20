package net.minecraft.world.item.trading;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public record TradeSet(HolderSet<VillagerTrade> trades, Holder<NumberProvider> amount, boolean allowDuplicates, Optional<Identifier> randomSequence) {
   public static final Codec<TradeSet> CODEC = RecordCodecBuilder.create((i) -> i.group(RegistryCodecs.holderSet(Registries.VILLAGER_TRADE).fieldOf("trades").forGetter(TradeSet::trades), NumberProviders.CODEC.fieldOf("amount").forGetter(TradeSet::amount), Codec.BOOL.optionalFieldOf("allow_duplicates", false).forGetter(TradeSet::allowDuplicates), Identifier.CODEC.optionalFieldOf("random_sequence").forGetter(TradeSet::randomSequence)).apply(i, TradeSet::new));

   public TradeSet {
      super();
   }

   public int calculateNumberOfTrades(final LootContext lootContext) {
      return ((NumberProvider)this.amount.value()).getInt(lootContext);
   }
}

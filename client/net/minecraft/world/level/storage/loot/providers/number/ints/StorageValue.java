package net.minecraft.world.level.storage.loot.providers.number.ints;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.providers.number.StoredNumberAccess;

public record StorageValue(StoredNumberAccess access, Holder<ContextIntProvider> fallback) implements ContextIntProvider {
   public static final MapCodec<StorageValue> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(StoredNumberAccess.MAP_CODEC.forGetter(StorageValue::access), ContextIntProviders.CODEC.optionalFieldOf("fallback", ContextIntProviders.exactly(0)).forGetter(StorageValue::fallback)).apply(i, StorageValue::new));

   public StorageValue {
      super();
   }

   public int getIntUnsafe(final LootContext context) {
      Number value = this.access.getNumericTag(context);
      return value != null ? value.intValue() : ((ContextIntProvider)this.fallback.value()).getIntUnsafe(context);
   }

   public void validate(final ValidationContext context) {
      Validatable.validateHolder(context, "fallback", this.fallback);
   }

   public MapCodec<StorageValue> codec() {
      return MAP_CODEC;
   }
}

package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.SeededContainerLoot;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetContainerLootTable extends LootItemConditionalFunction {
   public static final Codec<Holder.Reference<LootTable>> ID_ONLY_CODEC;
   public static final MapCodec<SetContainerLootTable> MAP_CODEC;
   private final Holder.Reference<LootTable> tag;
   private final long seed;
   private final Holder<BlockEntityType<?>> type;

   private SetContainerLootTable(final Optional<Holder<LootItemCondition>> condition, final Holder.Reference<LootTable> tag, final long seed, final Holder<BlockEntityType<?>> type) {
      super(condition);
      this.tag = tag;
      this.seed = seed;
      this.type = type;
   }

   public MapCodec<SetContainerLootTable> codec() {
      return MAP_CODEC;
   }

   public ItemStack run(final ItemStack itemStack, final LootContext context) {
      if (itemStack.isEmpty()) {
         return itemStack;
      } else {
         itemStack.set(DataComponents.CONTAINER_LOOT, new SeededContainerLoot(this.tag.key(), this.seed));
         return itemStack;
      }
   }

   public static LootItemConditionalFunction.Builder<?> withLootTable(final Holder<BlockEntityType<?>> type, final Holder.Reference<LootTable> value) {
      return simpleBuilder((conditions) -> new SetContainerLootTable(conditions, value, 0L, type));
   }

   public static LootItemConditionalFunction.Builder<?> withLootTable(final Holder<BlockEntityType<?>> type, final Holder.Reference<LootTable> value, final long seed) {
      return simpleBuilder((conditions) -> new SetContainerLootTable(conditions, value, seed, type));
   }

   static {
      ID_ONLY_CODEC = LootTable.CODEC.comapFlatMap((holder) -> {
         DataResult var10000;
         if (holder instanceof Holder.Reference<LootTable> tag) {
            var10000 = DataResult.success(tag);
         } else {
            var10000 = DataResult.error(() -> "Only tag names supported");
         }

         return var10000;
      }, (holder) -> holder);
      MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> commonFields(i).and(i.group(ID_ONLY_CODEC.fieldOf("tag").forGetter((f) -> f.tag), Codec.LONG.optionalFieldOf("seed", 0L).forGetter((f) -> f.seed), RegistryCodecs.holder(Registries.BLOCK_ENTITY_TYPE).fieldOf("type").forGetter((f) -> f.type))).apply(i, SetContainerLootTable::new));
   }
}

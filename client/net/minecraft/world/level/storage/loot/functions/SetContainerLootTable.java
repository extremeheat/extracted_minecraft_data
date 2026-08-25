package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.SeededContainerLoot;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetContainerLootTable extends LootItemConditionalFunction {
   public static final Codec<Holder.Reference<LootTable>> ID_ONLY_CODEC;
   public static final MapCodec<SetContainerLootTable> MAP_CODEC;
   private final Holder.Reference<LootTable> lootTableId;
   private final long seed;

   private SetContainerLootTable(final Optional<Holder<LootItemCondition>> condition, final Holder.Reference<LootTable> lootTableId, final long seed) {
      super(condition);
      this.lootTableId = lootTableId;
      this.seed = seed;
   }

   public MapCodec<SetContainerLootTable> codec() {
      return MAP_CODEC;
   }

   public ItemStack run(final ItemStack itemStack, final LootContext context) {
      if (itemStack.isEmpty()) {
         return itemStack;
      } else {
         itemStack.set(DataComponents.CONTAINER_LOOT, new SeededContainerLoot(this.lootTableId.key(), this.seed));
         return itemStack;
      }
   }

   public static LootItemConditionalFunction.Builder<?> withLootTable(final Holder.Reference<LootTable> value) {
      return simpleBuilder((conditions) -> new SetContainerLootTable(conditions, value, 0L));
   }

   public static LootItemConditionalFunction.Builder<?> withLootTable(final Holder.Reference<LootTable> value, final long seed) {
      return simpleBuilder((conditions) -> new SetContainerLootTable(conditions, value, seed));
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
      MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> commonFields(i).and(i.group(ID_ONLY_CODEC.fieldOf("loot_table_id").forGetter((f) -> f.lootTableId), Codec.LONG.optionalFieldOf("seed", 0L).forGetter((f) -> f.seed))).apply(i, SetContainerLootTable::new));
   }
}

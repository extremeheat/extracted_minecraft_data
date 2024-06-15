package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.SeededContainerLoot;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetContainerLootTable extends LootItemConditionalFunction {
   public static final MapCodec<SetContainerLootTable> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> commonFields(var0)
            .and(
               var0.group(
                  ResourceKey.codec(Registries.LOOT_TABLE).fieldOf("name").forGetter(var0x -> var0x.name),
                  Codec.LONG.optionalFieldOf("seed", 0L).forGetter(var0x -> var0x.seed),
                  BuiltInRegistries.BLOCK_ENTITY_TYPE.holderByNameCodec().fieldOf("type").forGetter(var0x -> var0x.type)
               )
            )
            .apply(var0, SetContainerLootTable::new)
   );
   private final ResourceKey<LootTable> name;
   private final long seed;
   private final Holder<BlockEntityType<?>> type;

   private SetContainerLootTable(List<LootItemCondition> var1, ResourceKey<LootTable> var2, long var3, Holder<BlockEntityType<?>> var5) {
      super(var1);
      this.name = var2;
      this.seed = var3;
      this.type = var5;
   }

   @Override
   public LootItemFunctionType<SetContainerLootTable> getType() {
      return LootItemFunctions.SET_LOOT_TABLE;
   }

   @Override
   public ItemStack run(ItemStack var1, LootContext var2) {
      if (var1.isEmpty()) {
         return var1;
      } else {
         var1.set(DataComponents.CONTAINER_LOOT, new SeededContainerLoot(this.name, this.seed));
         return var1;
      }
   }

   @Override
   public void validate(ValidationContext var1) {
      super.validate(var1);
      if (var1.resolver().get(Registries.LOOT_TABLE, this.name).isEmpty()) {
         var1.reportProblem("Missing loot table used for container: " + this.name.location());
      }
   }

   public static LootItemConditionalFunction.Builder<?> withLootTable(BlockEntityType<?> var0, ResourceKey<LootTable> var1) {
      return simpleBuilder(var2 -> new SetContainerLootTable(var2, var1, 0L, var0.builtInRegistryHolder()));
   }

   public static LootItemConditionalFunction.Builder<?> withLootTable(BlockEntityType<?> var0, ResourceKey<LootTable> var1, long var2) {
      return simpleBuilder(var4 -> new SetContainerLootTable(var4, var1, var2, var0.builtInRegistryHolder()));
   }
}

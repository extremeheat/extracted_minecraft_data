package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.SeededContainerLoot;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootDataId;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetContainerLootTable extends LootItemConditionalFunction {
   public static final Codec<SetContainerLootTable> CODEC = RecordCodecBuilder.create(
      var0 -> commonFields(var0)
            .and(
               var0.group(
                  ResourceLocation.CODEC.fieldOf("name").forGetter(var0x -> var0x.name),
                  ExtraCodecs.strictOptionalField(Codec.LONG, "seed", 0L).forGetter(var0x -> var0x.seed),
                  BuiltInRegistries.BLOCK_ENTITY_TYPE.holderByNameCodec().fieldOf("type").forGetter(var0x -> var0x.type)
               )
            )
            .apply(var0, SetContainerLootTable::new)
   );
   private final ResourceLocation name;
   private final long seed;
   private final Holder<BlockEntityType<?>> type;

   private SetContainerLootTable(List<LootItemCondition> var1, ResourceLocation var2, long var3, Holder<BlockEntityType<?>> var5) {
      super(var1);
      this.name = var2;
      this.seed = var3;
      this.type = var5;
   }

   @Override
   public LootItemFunctionType getType() {
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
      LootDataId var2 = new LootDataId<>(LootDataType.TABLE, this.name);
      if (var1.resolver().getElementOptional(var2).isEmpty()) {
         var1.reportProblem("Missing loot table used for container: " + this.name);
      }
   }

   public static LootItemConditionalFunction.Builder<?> withLootTable(BlockEntityType<?> var0, ResourceLocation var1) {
      return simpleBuilder(var2 -> new SetContainerLootTable(var2, var1, 0L, var0.builtInRegistryHolder()));
   }

   public static LootItemConditionalFunction.Builder<?> withLootTable(BlockEntityType<?> var0, ResourceLocation var1, long var2) {
      return simpleBuilder(var4 -> new SetContainerLootTable(var4, var1, var2, var0.builtInRegistryHolder()));
   }
}

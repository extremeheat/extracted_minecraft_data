package net.minecraft.world.level.storage.loot.entries;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class DynamicLoot extends LootPoolSingletonContainer {
   public static final MapCodec<DynamicLoot> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(ResourceLocation.CODEC.fieldOf("name").forGetter((var0x) -> {
         return var0x.name;
      })).and(singletonFields(var0)).apply(var0, DynamicLoot::new);
   });
   private final ResourceLocation name;

   private DynamicLoot(ResourceLocation var1, int var2, int var3, List<LootItemCondition> var4, List<LootItemFunction> var5) {
      super(var2, var3, var4, var5);
      this.name = var1;
   }

   public LootPoolEntryType getType() {
      return LootPoolEntries.DYNAMIC;
   }

   public void createItemStack(Consumer<ItemStack> var1, LootContext var2) {
      var2.addDynamicDrops(this.name, var1);
   }

   public static LootPoolSingletonContainer.Builder<?> dynamicEntry(ResourceLocation var0) {
      return simpleBuilder((var1, var2, var3, var4) -> {
         return new DynamicLoot(var0, var1, var2, var3, var4);
      });
   }
}

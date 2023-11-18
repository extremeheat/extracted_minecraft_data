package net.minecraft.world.level.storage.loot.entries;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootDataId;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LootTableReference extends LootPoolSingletonContainer {
   public static final Codec<LootTableReference> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(ResourceLocation.CODEC.fieldOf("name").forGetter(var0x -> var0x.name))
            .and(singletonFields(var0))
            .apply(var0, LootTableReference::new)
   );
   private final ResourceLocation name;

   private LootTableReference(ResourceLocation var1, int var2, int var3, List<LootItemCondition> var4, List<LootItemFunction> var5) {
      super(var2, var3, var4, var5);
      this.name = var1;
   }

   @Override
   public LootPoolEntryType getType() {
      return LootPoolEntries.REFERENCE;
   }

   @Override
   public void createItemStack(Consumer<ItemStack> var1, LootContext var2) {
      LootTable var3 = var2.getResolver().getLootTable(this.name);
      var3.getRandomItemsRaw(var2, var1);
   }

   @Override
   public void validate(ValidationContext var1) {
      LootDataId var2 = new LootDataId<>(LootDataType.TABLE, this.name);
      if (var1.hasVisitedElement(var2)) {
         var1.reportProblem("Table " + this.name + " is recursively called");
      } else {
         super.validate(var1);
         var1.resolver()
            .<LootTable>getElementOptional(var2)
            .ifPresentOrElse(
               var3 -> var3.validate(var1.enterElement("->{" + this.name + "}", var2)), () -> var1.reportProblem("Unknown loot table called " + this.name)
            );
      }
   }

   public static LootPoolSingletonContainer.Builder<?> lootTableReference(ResourceLocation var0) {
      return simpleBuilder((var1, var2, var3, var4) -> new LootTableReference(var0, var1, var2, var3, var4));
   }
}

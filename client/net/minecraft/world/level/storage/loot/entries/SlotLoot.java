package net.minecraft.world.level.storage.loot.entries;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.slot.SlotSource;
import net.minecraft.world.item.slot.SlotSources;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SlotLoot extends LootPoolSingletonContainer {
   public static final MapCodec<SlotLoot> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(SlotSources.CODEC.fieldOf("slot_source").forGetter((var0x) -> var0x.slotSource)).and(singletonFields(var0)).apply(var0, SlotLoot::new));
   private final SlotSource slotSource;

   private SlotLoot(SlotSource var1, int var2, int var3, List<LootItemCondition> var4, List<LootItemFunction> var5) {
      super(var2, var3, var4, var5);
      this.slotSource = var1;
   }

   public LootPoolEntryType getType() {
      return LootPoolEntries.SLOTS;
   }

   public void createItemStack(Consumer<ItemStack> var1, LootContext var2) {
      this.slotSource.provide(var2).itemCopies().filter((var0) -> !var0.isEmpty()).forEach(var1);
   }

   public void validate(ValidationContext var1) {
      super.validate(var1);
      this.slotSource.validate(var1.forChild(new ProblemReporter.FieldPathElement("slot_source")));
   }
}

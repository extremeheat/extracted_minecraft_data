package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.storage.loot.LootTable;

public record SeededContainerLoot(ResourceKey<LootTable> lootTable, long seed) implements TooltipProvider {
   private static final Component UNKNOWN_CONTENTS = Component.translatable("item.container.loot_table.unknown");
   public static final Codec<SeededContainerLoot> CODEC = RecordCodecBuilder.create((var0) -> var0.group(LootTable.KEY_CODEC.fieldOf("loot_table").forGetter(SeededContainerLoot::lootTable), Codec.LONG.optionalFieldOf("seed", 0L).forGetter(SeededContainerLoot::seed)).apply(var0, SeededContainerLoot::new));

   public SeededContainerLoot(ResourceKey<LootTable> var1, long var2) {
      super();
      this.lootTable = var1;
      this.seed = var2;
   }

   public void addToTooltip(Item.TooltipContext var1, Consumer<Component> var2, TooltipFlag var3, @Nullable Player var4, ItemStack var5) {
      var2.accept(UNKNOWN_CONTENTS);
   }
}

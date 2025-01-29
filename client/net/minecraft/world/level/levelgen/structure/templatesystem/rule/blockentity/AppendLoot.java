package net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.Nullable;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.storage.loot.LootTable;

public class AppendLoot implements RuleBlockEntityModifier {
   public static final MapCodec<AppendLoot> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ResourceKey.codec(Registries.LOOT_TABLE).fieldOf("loot_table").forGetter((var0x) -> var0x.lootTable)).apply(var0, AppendLoot::new));
   private final ResourceKey<LootTable> lootTable;

   public AppendLoot(ResourceKey<LootTable> var1) {
      super();
      this.lootTable = var1;
   }

   public CompoundTag apply(RandomSource var1, @Nullable CompoundTag var2) {
      CompoundTag var3 = var2 == null ? new CompoundTag() : var2.copy();
      var3.store("LootTable", ResourceKey.codec(Registries.LOOT_TABLE), this.lootTable);
      var3.putLong("LootTableSeed", var1.nextLong());
      return var3;
   }

   public RuleBlockEntityModifierType<?> getType() {
      return RuleBlockEntityModifierType.APPEND_LOOT;
   }
}

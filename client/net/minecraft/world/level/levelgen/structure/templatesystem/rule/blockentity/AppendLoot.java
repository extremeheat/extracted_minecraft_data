package net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.storage.loot.LootTable;
import org.slf4j.Logger;

public class AppendLoot implements RuleBlockEntityModifier {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final MapCodec<AppendLoot> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(ResourceKey.codec(Registries.LOOT_TABLE).fieldOf("loot_table").forGetter((var0x) -> {
         return var0x.lootTable;
      })).apply(var0, AppendLoot::new);
   });
   private final ResourceKey<LootTable> lootTable;

   public AppendLoot(ResourceKey<LootTable> var1) {
      super();
      this.lootTable = var1;
   }

   public CompoundTag apply(RandomSource var1, @Nullable CompoundTag var2) {
      CompoundTag var3 = var2 == null ? new CompoundTag() : var2.copy();
      DataResult var10000 = ResourceKey.codec(Registries.LOOT_TABLE).encodeStart(NbtOps.INSTANCE, this.lootTable);
      Logger var10001 = LOGGER;
      Objects.requireNonNull(var10001);
      var10000.resultOrPartial(var10001::error).ifPresent((var1x) -> {
         var3.put("LootTable", var1x);
      });
      var3.putLong("LootTableSeed", var1.nextLong());
      return var3;
   }

   public RuleBlockEntityModifierType<?> getType() {
      return RuleBlockEntityModifierType.APPEND_LOOT;
   }
}

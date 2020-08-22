package net.minecraft.world.level.levelgen.feature.blockplacers;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.core.Registry;

public class BlockPlacerType {
   public static final BlockPlacerType SIMPLE_BLOCK_PLACER = register("simple_block_placer", SimpleBlockPlacer::new);
   public static final BlockPlacerType DOUBLE_PLANT_PLACER = register("double_plant_placer", DoublePlantPlacer::new);
   public static final BlockPlacerType COLUMN_PLACER = register("column_placer", ColumnPlacer::new);
   private final Function deserializer;

   private static BlockPlacerType register(String var0, Function var1) {
      return (BlockPlacerType)Registry.register(Registry.BLOCK_PLACER_TYPES, (String)var0, new BlockPlacerType(var1));
   }

   private BlockPlacerType(Function var1) {
      this.deserializer = var1;
   }

   public BlockPlacer deserialize(Dynamic var1) {
      return (BlockPlacer)this.deserializer.apply(var1);
   }
}

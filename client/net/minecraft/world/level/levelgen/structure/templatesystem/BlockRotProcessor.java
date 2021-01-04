package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;

public class BlockRotProcessor extends StructureProcessor {
   private final float integrity;

   public BlockRotProcessor(float var1) {
      super();
      this.integrity = var1;
   }

   public BlockRotProcessor(Dynamic<?> var1) {
      this(var1.get("integrity").asFloat(1.0F));
   }

   @Nullable
   public StructureTemplate.StructureBlockInfo processBlock(LevelReader var1, BlockPos var2, StructureTemplate.StructureBlockInfo var3, StructureTemplate.StructureBlockInfo var4, StructurePlaceSettings var5) {
      Random var6 = var5.getRandom(var4.pos);
      return this.integrity < 1.0F && var6.nextFloat() > this.integrity ? null : var4;
   }

   protected StructureProcessorType getType() {
      return StructureProcessorType.BLOCK_ROT;
   }

   protected <T> Dynamic<T> getDynamic(DynamicOps<T> var1) {
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("integrity"), var1.createFloat(this.integrity))));
   }
}

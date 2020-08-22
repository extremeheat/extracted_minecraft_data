package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.Heightmap;

public class GravityProcessor extends StructureProcessor {
   private final Heightmap.Types heightmap;
   private final int offset;

   public GravityProcessor(Heightmap.Types var1, int var2) {
      this.heightmap = var1;
      this.offset = var2;
   }

   public GravityProcessor(Dynamic var1) {
      this(Heightmap.Types.getFromKey(var1.get("heightmap").asString(Heightmap.Types.WORLD_SURFACE_WG.getSerializationKey())), var1.get("offset").asInt(0));
   }

   @Nullable
   public StructureTemplate.StructureBlockInfo processBlock(LevelReader var1, BlockPos var2, StructureTemplate.StructureBlockInfo var3, StructureTemplate.StructureBlockInfo var4, StructurePlaceSettings var5) {
      int var6 = var1.getHeight(this.heightmap, var4.pos.getX(), var4.pos.getZ()) + this.offset;
      int var7 = var3.pos.getY();
      return new StructureTemplate.StructureBlockInfo(new BlockPos(var4.pos.getX(), var6 + var7, var4.pos.getZ()), var4.state, var4.nbt);
   }

   protected StructureProcessorType getType() {
      return StructureProcessorType.GRAVITY;
   }

   protected Dynamic getDynamic(DynamicOps var1) {
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("heightmap"), var1.createString(this.heightmap.getSerializationKey()), var1.createString("offset"), var1.createInt(this.offset))));
   }
}

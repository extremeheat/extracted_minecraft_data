package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.Heightmap;

public class GravityProcessor extends StructureProcessor {
   public static final Codec<GravityProcessor> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(Heightmap.Types.CODEC.fieldOf("heightmap").orElse(Heightmap.Types.WORLD_SURFACE_WG).forGetter((var0x) -> {
         return var0x.heightmap;
      }), Codec.INT.fieldOf("offset").orElse(0).forGetter((var0x) -> {
         return var0x.offset;
      })).apply(var0, GravityProcessor::new);
   });
   private final Heightmap.Types heightmap;
   private final int offset;

   public GravityProcessor(Heightmap.Types var1, int var2) {
      super();
      this.heightmap = var1;
      this.offset = var2;
   }

   @Nullable
   public StructureTemplate.StructureBlockInfo processBlock(LevelReader var1, BlockPos var2, BlockPos var3, StructureTemplate.StructureBlockInfo var4, StructureTemplate.StructureBlockInfo var5, StructurePlaceSettings var6) {
      Heightmap.Types var7;
      if (var1 instanceof ServerLevel) {
         if (this.heightmap == Heightmap.Types.WORLD_SURFACE_WG) {
            var7 = Heightmap.Types.WORLD_SURFACE;
         } else if (this.heightmap == Heightmap.Types.OCEAN_FLOOR_WG) {
            var7 = Heightmap.Types.OCEAN_FLOOR;
         } else {
            var7 = this.heightmap;
         }
      } else {
         var7 = this.heightmap;
      }

      int var8 = var1.getHeight(var7, var5.pos.getX(), var5.pos.getZ()) + this.offset;
      int var9 = var4.pos.getY();
      return new StructureTemplate.StructureBlockInfo(new BlockPos(var5.pos.getX(), var8 + var9, var5.pos.getZ()), var5.state, var5.nbt);
   }

   protected StructureProcessorType<?> getType() {
      return StructureProcessorType.GRAVITY;
   }
}

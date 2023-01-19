package net.minecraft.world.level.levelgen.structure.structures;

import java.util.Map;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class ShipwreckPieces {
   static final BlockPos PIVOT = new BlockPos(4, 0, 15);
   private static final ResourceLocation[] STRUCTURE_LOCATION_BEACHED = new ResourceLocation[]{
      new ResourceLocation("shipwreck/with_mast"),
      new ResourceLocation("shipwreck/sideways_full"),
      new ResourceLocation("shipwreck/sideways_fronthalf"),
      new ResourceLocation("shipwreck/sideways_backhalf"),
      new ResourceLocation("shipwreck/rightsideup_full"),
      new ResourceLocation("shipwreck/rightsideup_fronthalf"),
      new ResourceLocation("shipwreck/rightsideup_backhalf"),
      new ResourceLocation("shipwreck/with_mast_degraded"),
      new ResourceLocation("shipwreck/rightsideup_full_degraded"),
      new ResourceLocation("shipwreck/rightsideup_fronthalf_degraded"),
      new ResourceLocation("shipwreck/rightsideup_backhalf_degraded")
   };
   private static final ResourceLocation[] STRUCTURE_LOCATION_OCEAN = new ResourceLocation[]{
      new ResourceLocation("shipwreck/with_mast"),
      new ResourceLocation("shipwreck/upsidedown_full"),
      new ResourceLocation("shipwreck/upsidedown_fronthalf"),
      new ResourceLocation("shipwreck/upsidedown_backhalf"),
      new ResourceLocation("shipwreck/sideways_full"),
      new ResourceLocation("shipwreck/sideways_fronthalf"),
      new ResourceLocation("shipwreck/sideways_backhalf"),
      new ResourceLocation("shipwreck/rightsideup_full"),
      new ResourceLocation("shipwreck/rightsideup_fronthalf"),
      new ResourceLocation("shipwreck/rightsideup_backhalf"),
      new ResourceLocation("shipwreck/with_mast_degraded"),
      new ResourceLocation("shipwreck/upsidedown_full_degraded"),
      new ResourceLocation("shipwreck/upsidedown_fronthalf_degraded"),
      new ResourceLocation("shipwreck/upsidedown_backhalf_degraded"),
      new ResourceLocation("shipwreck/sideways_full_degraded"),
      new ResourceLocation("shipwreck/sideways_fronthalf_degraded"),
      new ResourceLocation("shipwreck/sideways_backhalf_degraded"),
      new ResourceLocation("shipwreck/rightsideup_full_degraded"),
      new ResourceLocation("shipwreck/rightsideup_fronthalf_degraded"),
      new ResourceLocation("shipwreck/rightsideup_backhalf_degraded")
   };
   static final Map<String, ResourceLocation> MARKERS_TO_LOOT = Map.of(
      "map_chest", BuiltInLootTables.SHIPWRECK_MAP, "treasure_chest", BuiltInLootTables.SHIPWRECK_TREASURE, "supply_chest", BuiltInLootTables.SHIPWRECK_SUPPLY
   );

   public ShipwreckPieces() {
      super();
   }

   public static void addPieces(StructureTemplateManager var0, BlockPos var1, Rotation var2, StructurePieceAccessor var3, RandomSource var4, boolean var5) {
      ResourceLocation var6 = Util.getRandom(var5 ? STRUCTURE_LOCATION_BEACHED : STRUCTURE_LOCATION_OCEAN, var4);
      var3.addPiece(new ShipwreckPieces.ShipwreckPiece(var0, var6, var1, var2, var5));
   }

   public static class ShipwreckPiece extends TemplateStructurePiece {
      private final boolean isBeached;

      public ShipwreckPiece(StructureTemplateManager var1, ResourceLocation var2, BlockPos var3, Rotation var4, boolean var5) {
         super(StructurePieceType.SHIPWRECK_PIECE, 0, var1, var2, var2.toString(), makeSettings(var4), var3);
         this.isBeached = var5;
      }

      public ShipwreckPiece(StructureTemplateManager var1, CompoundTag var2) {
         super(StructurePieceType.SHIPWRECK_PIECE, var2, var1, var1x -> makeSettings(Rotation.valueOf(var2.getString("Rot"))));
         this.isBeached = var2.getBoolean("isBeached");
      }

      @Override
      protected void addAdditionalSaveData(StructurePieceSerializationContext var1, CompoundTag var2) {
         super.addAdditionalSaveData(var1, var2);
         var2.putBoolean("isBeached", this.isBeached);
         var2.putString("Rot", this.placeSettings.getRotation().name());
      }

      private static StructurePlaceSettings makeSettings(Rotation var0) {
         return new StructurePlaceSettings()
            .setRotation(var0)
            .setMirror(Mirror.NONE)
            .setRotationPivot(ShipwreckPieces.PIVOT)
            .addProcessor(BlockIgnoreProcessor.STRUCTURE_AND_AIR);
      }

      @Override
      protected void handleDataMarker(String var1, BlockPos var2, ServerLevelAccessor var3, RandomSource var4, BoundingBox var5) {
         ResourceLocation var6 = ShipwreckPieces.MARKERS_TO_LOOT.get(var1);
         if (var6 != null) {
            RandomizableContainerBlockEntity.setLootTable(var3, var4, var2.below(), var6);
         }
      }

      @Override
      public void postProcess(
         WorldGenLevel var1, StructureManager var2, ChunkGenerator var3, RandomSource var4, BoundingBox var5, ChunkPos var6, BlockPos var7
      ) {
         int var8 = var1.getMaxBuildHeight();
         int var9 = 0;
         Vec3i var10 = this.template.getSize();
         Heightmap.Types var11 = this.isBeached ? Heightmap.Types.WORLD_SURFACE_WG : Heightmap.Types.OCEAN_FLOOR_WG;
         int var12 = var10.getX() * var10.getZ();
         if (var12 == 0) {
            var9 = var1.getHeight(var11, this.templatePosition.getX(), this.templatePosition.getZ());
         } else {
            BlockPos var13 = this.templatePosition.offset(var10.getX() - 1, 0, var10.getZ() - 1);

            for(BlockPos var15 : BlockPos.betweenClosed(this.templatePosition, var13)) {
               int var16 = var1.getHeight(var11, var15.getX(), var15.getZ());
               var9 += var16;
               var8 = Math.min(var8, var16);
            }

            var9 /= var12;
         }

         int var18 = this.isBeached ? var8 - var10.getY() / 2 - var4.nextInt(3) : var9;
         this.templatePosition = new BlockPos(this.templatePosition.getX(), var18, this.templatePosition.getZ());
         super.postProcess(var1, var2, var3, var4, var5, var6, var7);
      }
   }
}

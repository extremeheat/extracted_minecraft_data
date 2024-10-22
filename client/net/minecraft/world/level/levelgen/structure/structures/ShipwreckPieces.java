package net.minecraft.world.level.levelgen.structure.structures;

import java.util.Map;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
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
import net.minecraft.world.level.storage.loot.LootTable;

public class ShipwreckPieces {
   private static final int NUMBER_OF_BLOCKS_ALLOWED_IN_WORLD_GEN_REGION = 32;
   static final BlockPos PIVOT = new BlockPos(4, 0, 15);
   private static final ResourceLocation[] STRUCTURE_LOCATION_BEACHED = new ResourceLocation[]{
      ResourceLocation.withDefaultNamespace("shipwreck/with_mast"),
      ResourceLocation.withDefaultNamespace("shipwreck/sideways_full"),
      ResourceLocation.withDefaultNamespace("shipwreck/sideways_fronthalf"),
      ResourceLocation.withDefaultNamespace("shipwreck/sideways_backhalf"),
      ResourceLocation.withDefaultNamespace("shipwreck/rightsideup_full"),
      ResourceLocation.withDefaultNamespace("shipwreck/rightsideup_fronthalf"),
      ResourceLocation.withDefaultNamespace("shipwreck/rightsideup_backhalf"),
      ResourceLocation.withDefaultNamespace("shipwreck/with_mast_degraded"),
      ResourceLocation.withDefaultNamespace("shipwreck/rightsideup_full_degraded"),
      ResourceLocation.withDefaultNamespace("shipwreck/rightsideup_fronthalf_degraded"),
      ResourceLocation.withDefaultNamespace("shipwreck/rightsideup_backhalf_degraded")
   };
   private static final ResourceLocation[] STRUCTURE_LOCATION_OCEAN = new ResourceLocation[]{
      ResourceLocation.withDefaultNamespace("shipwreck/with_mast"),
      ResourceLocation.withDefaultNamespace("shipwreck/upsidedown_full"),
      ResourceLocation.withDefaultNamespace("shipwreck/upsidedown_fronthalf"),
      ResourceLocation.withDefaultNamespace("shipwreck/upsidedown_backhalf"),
      ResourceLocation.withDefaultNamespace("shipwreck/sideways_full"),
      ResourceLocation.withDefaultNamespace("shipwreck/sideways_fronthalf"),
      ResourceLocation.withDefaultNamespace("shipwreck/sideways_backhalf"),
      ResourceLocation.withDefaultNamespace("shipwreck/rightsideup_full"),
      ResourceLocation.withDefaultNamespace("shipwreck/rightsideup_fronthalf"),
      ResourceLocation.withDefaultNamespace("shipwreck/rightsideup_backhalf"),
      ResourceLocation.withDefaultNamespace("shipwreck/with_mast_degraded"),
      ResourceLocation.withDefaultNamespace("shipwreck/upsidedown_full_degraded"),
      ResourceLocation.withDefaultNamespace("shipwreck/upsidedown_fronthalf_degraded"),
      ResourceLocation.withDefaultNamespace("shipwreck/upsidedown_backhalf_degraded"),
      ResourceLocation.withDefaultNamespace("shipwreck/sideways_full_degraded"),
      ResourceLocation.withDefaultNamespace("shipwreck/sideways_fronthalf_degraded"),
      ResourceLocation.withDefaultNamespace("shipwreck/sideways_backhalf_degraded"),
      ResourceLocation.withDefaultNamespace("shipwreck/rightsideup_full_degraded"),
      ResourceLocation.withDefaultNamespace("shipwreck/rightsideup_fronthalf_degraded"),
      ResourceLocation.withDefaultNamespace("shipwreck/rightsideup_backhalf_degraded")
   };
   static final Map<String, ResourceKey<LootTable>> MARKERS_TO_LOOT = Map.of(
      "map_chest", BuiltInLootTables.SHIPWRECK_MAP, "treasure_chest", BuiltInLootTables.SHIPWRECK_TREASURE, "supply_chest", BuiltInLootTables.SHIPWRECK_SUPPLY
   );

   public ShipwreckPieces() {
      super();
   }

   public static ShipwreckPieces.ShipwreckPiece addRandomPiece(
      StructureTemplateManager var0, BlockPos var1, Rotation var2, StructurePieceAccessor var3, RandomSource var4, boolean var5
   ) {
      ResourceLocation var6 = Util.getRandom(var5 ? STRUCTURE_LOCATION_BEACHED : STRUCTURE_LOCATION_OCEAN, var4);
      ShipwreckPieces.ShipwreckPiece var7 = new ShipwreckPieces.ShipwreckPiece(var0, var6, var1, var2, var5);
      var3.addPiece(var7);
      return var7;
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
         ResourceKey var6 = ShipwreckPieces.MARKERS_TO_LOOT.get(var1);
         if (var6 != null) {
            RandomizableContainer.setBlockEntityLootTable(var3, var4, var2.below(), var6);
         }
      }

      @Override
      public void postProcess(WorldGenLevel var1, StructureManager var2, ChunkGenerator var3, RandomSource var4, BoundingBox var5, ChunkPos var6, BlockPos var7) {
         if (this.isTooBigToFitInWorldGenRegion()) {
            super.postProcess(var1, var2, var3, var4, var5, var6, var7);
         } else {
            int var8 = var1.getMaxY() + 1;
            int var9 = 0;
            Vec3i var10 = this.template.getSize();
            Heightmap.Types var11 = this.isBeached ? Heightmap.Types.WORLD_SURFACE_WG : Heightmap.Types.OCEAN_FLOOR_WG;
            int var12 = var10.getX() * var10.getZ();
            if (var12 == 0) {
               var9 = var1.getHeight(var11, this.templatePosition.getX(), this.templatePosition.getZ());
            } else {
               BlockPos var13 = this.templatePosition.offset(var10.getX() - 1, 0, var10.getZ() - 1);

               for (BlockPos var15 : BlockPos.betweenClosed(this.templatePosition, var13)) {
                  int var16 = var1.getHeight(var11, var15.getX(), var15.getZ());
                  var9 += var16;
                  var8 = Math.min(var8, var16);
               }

               var9 /= var12;
            }

            this.adjustPositionHeight(this.isBeached ? this.calculateBeachedPosition(var8, var4) : var9);
            super.postProcess(var1, var2, var3, var4, var5, var6, var7);
         }
      }

      public boolean isTooBigToFitInWorldGenRegion() {
         Vec3i var1 = this.template.getSize();
         return var1.getX() > 32 || var1.getY() > 32;
      }

      public int calculateBeachedPosition(int var1, RandomSource var2) {
         return var1 - this.template.getSize().getY() / 2 - var2.nextInt(3);
      }

      public void adjustPositionHeight(int var1) {
         this.templatePosition = new BlockPos(this.templatePosition.getX(), var1, this.templatePosition.getZ());
      }
   }
}

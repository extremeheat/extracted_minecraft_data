package net.minecraft.world.level.levelgen.structure;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.ShipwreckConfiguration;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class ShipwreckPieces {
   private static final BlockPos PIVOT = new BlockPos(4, 0, 15);
   private static final ResourceLocation[] STRUCTURE_LOCATION_BEACHED = new ResourceLocation[]{new ResourceLocation("shipwreck/with_mast"), new ResourceLocation("shipwreck/sideways_full"), new ResourceLocation("shipwreck/sideways_fronthalf"), new ResourceLocation("shipwreck/sideways_backhalf"), new ResourceLocation("shipwreck/rightsideup_full"), new ResourceLocation("shipwreck/rightsideup_fronthalf"), new ResourceLocation("shipwreck/rightsideup_backhalf"), new ResourceLocation("shipwreck/with_mast_degraded"), new ResourceLocation("shipwreck/rightsideup_full_degraded"), new ResourceLocation("shipwreck/rightsideup_fronthalf_degraded"), new ResourceLocation("shipwreck/rightsideup_backhalf_degraded")};
   private static final ResourceLocation[] STRUCTURE_LOCATION_OCEAN = new ResourceLocation[]{new ResourceLocation("shipwreck/with_mast"), new ResourceLocation("shipwreck/upsidedown_full"), new ResourceLocation("shipwreck/upsidedown_fronthalf"), new ResourceLocation("shipwreck/upsidedown_backhalf"), new ResourceLocation("shipwreck/sideways_full"), new ResourceLocation("shipwreck/sideways_fronthalf"), new ResourceLocation("shipwreck/sideways_backhalf"), new ResourceLocation("shipwreck/rightsideup_full"), new ResourceLocation("shipwreck/rightsideup_fronthalf"), new ResourceLocation("shipwreck/rightsideup_backhalf"), new ResourceLocation("shipwreck/with_mast_degraded"), new ResourceLocation("shipwreck/upsidedown_full_degraded"), new ResourceLocation("shipwreck/upsidedown_fronthalf_degraded"), new ResourceLocation("shipwreck/upsidedown_backhalf_degraded"), new ResourceLocation("shipwreck/sideways_full_degraded"), new ResourceLocation("shipwreck/sideways_fronthalf_degraded"), new ResourceLocation("shipwreck/sideways_backhalf_degraded"), new ResourceLocation("shipwreck/rightsideup_full_degraded"), new ResourceLocation("shipwreck/rightsideup_fronthalf_degraded"), new ResourceLocation("shipwreck/rightsideup_backhalf_degraded")};

   public static void addPieces(StructureManager var0, BlockPos var1, Rotation var2, List<StructurePiece> var3, Random var4, ShipwreckConfiguration var5) {
      ResourceLocation var6 = var5.isBeached ? STRUCTURE_LOCATION_BEACHED[var4.nextInt(STRUCTURE_LOCATION_BEACHED.length)] : STRUCTURE_LOCATION_OCEAN[var4.nextInt(STRUCTURE_LOCATION_OCEAN.length)];
      var3.add(new ShipwreckPieces.ShipwreckPiece(var0, var6, var1, var2, var5.isBeached));
   }

   public static class ShipwreckPiece extends TemplateStructurePiece {
      private final Rotation rotation;
      private final ResourceLocation templateLocation;
      private final boolean isBeached;

      public ShipwreckPiece(StructureManager var1, ResourceLocation var2, BlockPos var3, Rotation var4, boolean var5) {
         super(StructurePieceType.SHIPWRECK_PIECE, 0);
         this.templatePosition = var3;
         this.rotation = var4;
         this.templateLocation = var2;
         this.isBeached = var5;
         this.loadTemplate(var1);
      }

      public ShipwreckPiece(StructureManager var1, CompoundTag var2) {
         super(StructurePieceType.SHIPWRECK_PIECE, var2);
         this.templateLocation = new ResourceLocation(var2.getString("Template"));
         this.isBeached = var2.getBoolean("isBeached");
         this.rotation = Rotation.valueOf(var2.getString("Rot"));
         this.loadTemplate(var1);
      }

      protected void addAdditionalSaveData(CompoundTag var1) {
         super.addAdditionalSaveData(var1);
         var1.putString("Template", this.templateLocation.toString());
         var1.putBoolean("isBeached", this.isBeached);
         var1.putString("Rot", this.rotation.name());
      }

      private void loadTemplate(StructureManager var1) {
         StructureTemplate var2 = var1.getOrCreate(this.templateLocation);
         StructurePlaceSettings var3 = (new StructurePlaceSettings()).setRotation(this.rotation).setMirror(Mirror.NONE).setRotationPivot(ShipwreckPieces.PIVOT).addProcessor(BlockIgnoreProcessor.STRUCTURE_AND_AIR);
         this.setup(var2, this.templatePosition, var3);
      }

      protected void handleDataMarker(String var1, BlockPos var2, LevelAccessor var3, Random var4, BoundingBox var5) {
         if ("map_chest".equals(var1)) {
            RandomizableContainerBlockEntity.setLootTable(var3, var4, var2.below(), BuiltInLootTables.SHIPWRECK_MAP);
         } else if ("treasure_chest".equals(var1)) {
            RandomizableContainerBlockEntity.setLootTable(var3, var4, var2.below(), BuiltInLootTables.SHIPWRECK_TREASURE);
         } else if ("supply_chest".equals(var1)) {
            RandomizableContainerBlockEntity.setLootTable(var3, var4, var2.below(), BuiltInLootTables.SHIPWRECK_SUPPLY);
         }

      }

      public boolean postProcess(LevelAccessor var1, Random var2, BoundingBox var3, ChunkPos var4) {
         int var5 = 256;
         int var6 = 0;
         BlockPos var7 = this.templatePosition.offset(this.template.getSize().getX() - 1, 0, this.template.getSize().getZ() - 1);

         int var10;
         for(Iterator var8 = BlockPos.betweenClosed(this.templatePosition, var7).iterator(); var8.hasNext(); var5 = Math.min(var5, var10)) {
            BlockPos var9 = (BlockPos)var8.next();
            var10 = var1.getHeight(this.isBeached ? Heightmap.Types.WORLD_SURFACE_WG : Heightmap.Types.OCEAN_FLOOR_WG, var9.getX(), var9.getZ());
            var6 += var10;
         }

         var6 /= this.template.getSize().getX() * this.template.getSize().getZ();
         int var11 = this.isBeached ? var5 - this.template.getSize().getY() / 2 - var2.nextInt(3) : var6;
         this.templatePosition = new BlockPos(this.templatePosition.getX(), var11, this.templatePosition.getZ());
         return super.postProcess(var1, var2, var3, var4);
      }
   }
}

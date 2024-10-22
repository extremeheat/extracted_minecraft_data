package net.minecraft.world.level.levelgen.structure.structures;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class EndCityPieces {
   private static final int MAX_GEN_DEPTH = 8;
   static final EndCityPieces.SectionGenerator HOUSE_TOWER_GENERATOR = new EndCityPieces.SectionGenerator() {
      @Override
      public void init() {
      }

      @Override
      public boolean generate(
         StructureTemplateManager var1, int var2, EndCityPieces.EndCityPiece var3, BlockPos var4, List<StructurePiece> var5, RandomSource var6
      ) {
         if (var2 > 8) {
            return false;
         } else {
            Rotation var7 = var3.placeSettings().getRotation();
            EndCityPieces.EndCityPiece var8 = EndCityPieces.addHelper(var5, EndCityPieces.addPiece(var1, var3, var4, "base_floor", var7, true));
            int var9 = var6.nextInt(3);
            if (var9 == 0) {
               var8 = EndCityPieces.addHelper(var5, EndCityPieces.addPiece(var1, var8, new BlockPos(-1, 4, -1), "base_roof", var7, true));
            } else if (var9 == 1) {
               var8 = EndCityPieces.addHelper(var5, EndCityPieces.addPiece(var1, var8, new BlockPos(-1, 0, -1), "second_floor_2", var7, false));
               var8 = EndCityPieces.addHelper(var5, EndCityPieces.addPiece(var1, var8, new BlockPos(-1, 8, -1), "second_roof", var7, false));
               EndCityPieces.recursiveChildren(var1, EndCityPieces.TOWER_GENERATOR, var2 + 1, var8, null, var5, var6);
            } else if (var9 == 2) {
               var8 = EndCityPieces.addHelper(var5, EndCityPieces.addPiece(var1, var8, new BlockPos(-1, 0, -1), "second_floor_2", var7, false));
               var8 = EndCityPieces.addHelper(var5, EndCityPieces.addPiece(var1, var8, new BlockPos(-1, 4, -1), "third_floor_2", var7, false));
               var8 = EndCityPieces.addHelper(var5, EndCityPieces.addPiece(var1, var8, new BlockPos(-1, 8, -1), "third_roof", var7, true));
               EndCityPieces.recursiveChildren(var1, EndCityPieces.TOWER_GENERATOR, var2 + 1, var8, null, var5, var6);
            }

            return true;
         }
      }
   };
   static final List<Tuple<Rotation, BlockPos>> TOWER_BRIDGES = Lists.newArrayList(
      new Tuple[]{
         new Tuple<>(Rotation.NONE, new BlockPos(1, -1, 0)),
         new Tuple<>(Rotation.CLOCKWISE_90, new BlockPos(6, -1, 1)),
         new Tuple<>(Rotation.COUNTERCLOCKWISE_90, new BlockPos(0, -1, 5)),
         new Tuple<>(Rotation.CLOCKWISE_180, new BlockPos(5, -1, 6))
      }
   );
   static final EndCityPieces.SectionGenerator TOWER_GENERATOR = new EndCityPieces.SectionGenerator() {
      @Override
      public void init() {
      }

      @Override
      public boolean generate(
         StructureTemplateManager var1, int var2, EndCityPieces.EndCityPiece var3, BlockPos var4, List<StructurePiece> var5, RandomSource var6
      ) {
         Rotation var7 = var3.placeSettings().getRotation();
         EndCityPieces.EndCityPiece var8 = EndCityPieces.addHelper(
            var5, EndCityPieces.addPiece(var1, var3, new BlockPos(3 + var6.nextInt(2), -3, 3 + var6.nextInt(2)), "tower_base", var7, true)
         );
         var8 = EndCityPieces.addHelper(var5, EndCityPieces.addPiece(var1, var8, new BlockPos(0, 7, 0), "tower_piece", var7, true));
         EndCityPieces.EndCityPiece var9 = var6.nextInt(3) == 0 ? var8 : null;
         int var10 = 1 + var6.nextInt(3);

         for (int var11 = 0; var11 < var10; var11++) {
            var8 = EndCityPieces.addHelper(var5, EndCityPieces.addPiece(var1, var8, new BlockPos(0, 4, 0), "tower_piece", var7, true));
            if (var11 < var10 - 1 && var6.nextBoolean()) {
               var9 = var8;
            }
         }

         if (var9 != null) {
            for (Tuple var12 : EndCityPieces.TOWER_BRIDGES) {
               if (var6.nextBoolean()) {
                  EndCityPieces.EndCityPiece var13 = EndCityPieces.addHelper(
                     var5, EndCityPieces.addPiece(var1, var9, (BlockPos)var12.getB(), "bridge_end", var7.getRotated((Rotation)var12.getA()), true)
                  );
                  EndCityPieces.recursiveChildren(var1, EndCityPieces.TOWER_BRIDGE_GENERATOR, var2 + 1, var13, null, var5, var6);
               }
            }

            var8 = EndCityPieces.addHelper(var5, EndCityPieces.addPiece(var1, var8, new BlockPos(-1, 4, -1), "tower_top", var7, true));
         } else {
            if (var2 != 7) {
               return EndCityPieces.recursiveChildren(var1, EndCityPieces.FAT_TOWER_GENERATOR, var2 + 1, var8, null, var5, var6);
            }

            var8 = EndCityPieces.addHelper(var5, EndCityPieces.addPiece(var1, var8, new BlockPos(-1, 4, -1), "tower_top", var7, true));
         }

         return true;
      }
   };
   static final EndCityPieces.SectionGenerator TOWER_BRIDGE_GENERATOR = new EndCityPieces.SectionGenerator() {
      public boolean shipCreated;

      @Override
      public void init() {
         this.shipCreated = false;
      }

      @Override
      public boolean generate(
         StructureTemplateManager var1, int var2, EndCityPieces.EndCityPiece var3, BlockPos var4, List<StructurePiece> var5, RandomSource var6
      ) {
         Rotation var7 = var3.placeSettings().getRotation();
         int var8 = var6.nextInt(4) + 1;
         EndCityPieces.EndCityPiece var9 = EndCityPieces.addHelper(var5, EndCityPieces.addPiece(var1, var3, new BlockPos(0, 0, -4), "bridge_piece", var7, true));
         var9.setGenDepth(-1);
         byte var10 = 0;

         for (int var11 = 0; var11 < var8; var11++) {
            if (var6.nextBoolean()) {
               var9 = EndCityPieces.addHelper(var5, EndCityPieces.addPiece(var1, var9, new BlockPos(0, var10, -4), "bridge_piece", var7, true));
               var10 = 0;
            } else {
               if (var6.nextBoolean()) {
                  var9 = EndCityPieces.addHelper(var5, EndCityPieces.addPiece(var1, var9, new BlockPos(0, var10, -4), "bridge_steep_stairs", var7, true));
               } else {
                  var9 = EndCityPieces.addHelper(var5, EndCityPieces.addPiece(var1, var9, new BlockPos(0, var10, -8), "bridge_gentle_stairs", var7, true));
               }

               var10 = 4;
            }
         }

         if (!this.shipCreated && var6.nextInt(10 - var2) == 0) {
            EndCityPieces.addHelper(
               var5, EndCityPieces.addPiece(var1, var9, new BlockPos(-8 + var6.nextInt(8), var10, -70 + var6.nextInt(10)), "ship", var7, true)
            );
            this.shipCreated = true;
         } else if (!EndCityPieces.recursiveChildren(var1, EndCityPieces.HOUSE_TOWER_GENERATOR, var2 + 1, var9, new BlockPos(-3, var10 + 1, -11), var5, var6)) {
            return false;
         }

         var9 = EndCityPieces.addHelper(
            var5, EndCityPieces.addPiece(var1, var9, new BlockPos(4, var10, 0), "bridge_end", var7.getRotated(Rotation.CLOCKWISE_180), true)
         );
         var9.setGenDepth(-1);
         return true;
      }
   };
   static final List<Tuple<Rotation, BlockPos>> FAT_TOWER_BRIDGES = Lists.newArrayList(
      new Tuple[]{
         new Tuple<>(Rotation.NONE, new BlockPos(4, -1, 0)),
         new Tuple<>(Rotation.CLOCKWISE_90, new BlockPos(12, -1, 4)),
         new Tuple<>(Rotation.COUNTERCLOCKWISE_90, new BlockPos(0, -1, 8)),
         new Tuple<>(Rotation.CLOCKWISE_180, new BlockPos(8, -1, 12))
      }
   );
   static final EndCityPieces.SectionGenerator FAT_TOWER_GENERATOR = new EndCityPieces.SectionGenerator() {
      @Override
      public void init() {
      }

      @Override
      public boolean generate(
         StructureTemplateManager var1, int var2, EndCityPieces.EndCityPiece var3, BlockPos var4, List<StructurePiece> var5, RandomSource var6
      ) {
         Rotation var8 = var3.placeSettings().getRotation();
         EndCityPieces.EndCityPiece var7 = EndCityPieces.addHelper(
            var5, EndCityPieces.addPiece(var1, var3, new BlockPos(-3, 4, -3), "fat_tower_base", var8, true)
         );
         var7 = EndCityPieces.addHelper(var5, EndCityPieces.addPiece(var1, var7, new BlockPos(0, 4, 0), "fat_tower_middle", var8, true));

         for (int var9 = 0; var9 < 2 && var6.nextInt(3) != 0; var9++) {
            var7 = EndCityPieces.addHelper(var5, EndCityPieces.addPiece(var1, var7, new BlockPos(0, 8, 0), "fat_tower_middle", var8, true));

            for (Tuple var11 : EndCityPieces.FAT_TOWER_BRIDGES) {
               if (var6.nextBoolean()) {
                  EndCityPieces.EndCityPiece var12 = EndCityPieces.addHelper(
                     var5, EndCityPieces.addPiece(var1, var7, (BlockPos)var11.getB(), "bridge_end", var8.getRotated((Rotation)var11.getA()), true)
                  );
                  EndCityPieces.recursiveChildren(var1, EndCityPieces.TOWER_BRIDGE_GENERATOR, var2 + 1, var12, null, var5, var6);
               }
            }
         }

         var7 = EndCityPieces.addHelper(var5, EndCityPieces.addPiece(var1, var7, new BlockPos(-2, 8, -2), "fat_tower_top", var8, true));
         return true;
      }
   };

   public EndCityPieces() {
      super();
   }

   static EndCityPieces.EndCityPiece addPiece(
      StructureTemplateManager var0, EndCityPieces.EndCityPiece var1, BlockPos var2, String var3, Rotation var4, boolean var5
   ) {
      EndCityPieces.EndCityPiece var6 = new EndCityPieces.EndCityPiece(var0, var3, var1.templatePosition(), var4, var5);
      BlockPos var7 = var1.template().calculateConnectedPosition(var1.placeSettings(), var2, var6.placeSettings(), BlockPos.ZERO);
      var6.move(var7.getX(), var7.getY(), var7.getZ());
      return var6;
   }

   public static void startHouseTower(StructureTemplateManager var0, BlockPos var1, Rotation var2, List<StructurePiece> var3, RandomSource var4) {
      FAT_TOWER_GENERATOR.init();
      HOUSE_TOWER_GENERATOR.init();
      TOWER_BRIDGE_GENERATOR.init();
      TOWER_GENERATOR.init();
      EndCityPieces.EndCityPiece var5 = addHelper(var3, new EndCityPieces.EndCityPiece(var0, "base_floor", var1, var2, true));
      var5 = addHelper(var3, addPiece(var0, var5, new BlockPos(-1, 0, -1), "second_floor_1", var2, false));
      var5 = addHelper(var3, addPiece(var0, var5, new BlockPos(-1, 4, -1), "third_floor_1", var2, false));
      var5 = addHelper(var3, addPiece(var0, var5, new BlockPos(-1, 8, -1), "third_roof", var2, true));
      recursiveChildren(var0, TOWER_GENERATOR, 1, var5, null, var3, var4);
   }

   static EndCityPieces.EndCityPiece addHelper(List<StructurePiece> var0, EndCityPieces.EndCityPiece var1) {
      var0.add(var1);
      return var1;
   }

   static boolean recursiveChildren(
      StructureTemplateManager var0,
      EndCityPieces.SectionGenerator var1,
      int var2,
      EndCityPieces.EndCityPiece var3,
      BlockPos var4,
      List<StructurePiece> var5,
      RandomSource var6
   ) {
      if (var2 > 8) {
         return false;
      } else {
         ArrayList var7 = Lists.newArrayList();
         if (var1.generate(var0, var2, var3, var4, var7, var6)) {
            boolean var8 = false;
            int var9 = var6.nextInt();

            for (StructurePiece var11 : var7) {
               var11.setGenDepth(var9);
               StructurePiece var12 = StructurePiece.findCollisionPiece(var5, var11.getBoundingBox());
               if (var12 != null && var12.getGenDepth() != var3.getGenDepth()) {
                  var8 = true;
                  break;
               }
            }

            if (!var8) {
               var5.addAll(var7);
               return true;
            }
         }

         return false;
      }
   }

   public static class EndCityPiece extends TemplateStructurePiece {
      public EndCityPiece(StructureTemplateManager var1, String var2, BlockPos var3, Rotation var4, boolean var5) {
         super(StructurePieceType.END_CITY_PIECE, 0, var1, makeResourceLocation(var2), var2, makeSettings(var5, var4), var3);
      }

      public EndCityPiece(StructureTemplateManager var1, CompoundTag var2) {
         super(StructurePieceType.END_CITY_PIECE, var2, var1, var1x -> makeSettings(var2.getBoolean("OW"), Rotation.valueOf(var2.getString("Rot"))));
      }

      private static StructurePlaceSettings makeSettings(boolean var0, Rotation var1) {
         BlockIgnoreProcessor var2 = var0 ? BlockIgnoreProcessor.STRUCTURE_BLOCK : BlockIgnoreProcessor.STRUCTURE_AND_AIR;
         return new StructurePlaceSettings().setIgnoreEntities(true).addProcessor(var2).setRotation(var1);
      }

      @Override
      protected ResourceLocation makeTemplateLocation() {
         return makeResourceLocation(this.templateName);
      }

      private static ResourceLocation makeResourceLocation(String var0) {
         return ResourceLocation.withDefaultNamespace("end_city/" + var0);
      }

      @Override
      protected void addAdditionalSaveData(StructurePieceSerializationContext var1, CompoundTag var2) {
         super.addAdditionalSaveData(var1, var2);
         var2.putString("Rot", this.placeSettings.getRotation().name());
         var2.putBoolean("OW", this.placeSettings.getProcessors().get(0) == BlockIgnoreProcessor.STRUCTURE_BLOCK);
      }

      @Override
      protected void handleDataMarker(String var1, BlockPos var2, ServerLevelAccessor var3, RandomSource var4, BoundingBox var5) {
         if (var1.startsWith("Chest")) {
            BlockPos var6 = var2.below();
            if (var5.isInside(var6)) {
               RandomizableContainer.setBlockEntityLootTable(var3, var4, var6, BuiltInLootTables.END_CITY_TREASURE);
            }
         } else if (var5.isInside(var2) && Level.isInSpawnableBounds(var2)) {
            if (var1.startsWith("Sentry")) {
               Shulker var7 = EntityType.SHULKER.create(var3.getLevel(), EntitySpawnReason.STRUCTURE);
               if (var7 != null) {
                  var7.setPos((double)var2.getX() + 0.5, (double)var2.getY(), (double)var2.getZ() + 0.5);
                  var3.addFreshEntity(var7);
               }
            } else if (var1.startsWith("Elytra")) {
               ItemFrame var8 = new ItemFrame(var3.getLevel(), var2, this.placeSettings.getRotation().rotate(Direction.SOUTH));
               var8.setItem(new ItemStack(Items.ELYTRA), false);
               var3.addFreshEntity(var8);
            }
         }
      }
   }

   interface SectionGenerator {
      void init();

      boolean generate(StructureTemplateManager var1, int var2, EndCityPieces.EndCityPiece var3, BlockPos var4, List<StructurePiece> var5, RandomSource var6);
   }
}

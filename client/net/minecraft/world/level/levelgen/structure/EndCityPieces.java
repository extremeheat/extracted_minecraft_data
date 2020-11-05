package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class EndCityPieces {
   private static final StructurePlaceSettings OVERWRITE;
   private static final StructurePlaceSettings INSERT;
   private static final EndCityPieces.SectionGenerator HOUSE_TOWER_GENERATOR;
   private static final List<Tuple<Rotation, BlockPos>> TOWER_BRIDGES;
   private static final EndCityPieces.SectionGenerator TOWER_GENERATOR;
   private static final EndCityPieces.SectionGenerator TOWER_BRIDGE_GENERATOR;
   private static final List<Tuple<Rotation, BlockPos>> FAT_TOWER_BRIDGES;
   private static final EndCityPieces.SectionGenerator FAT_TOWER_GENERATOR;

   private static EndCityPieces.EndCityPiece addPiece(StructureManager var0, EndCityPieces.EndCityPiece var1, BlockPos var2, String var3, Rotation var4, boolean var5) {
      EndCityPieces.EndCityPiece var6 = new EndCityPieces.EndCityPiece(var0, var3, var1.templatePosition, var4, var5);
      BlockPos var7 = var1.template.calculateConnectedPosition(var1.placeSettings, var2, var6.placeSettings, BlockPos.ZERO);
      var6.move(var7.getX(), var7.getY(), var7.getZ());
      return var6;
   }

   public static void startHouseTower(StructureManager var0, BlockPos var1, Rotation var2, List<StructurePiece> var3, Random var4) {
      FAT_TOWER_GENERATOR.init();
      HOUSE_TOWER_GENERATOR.init();
      TOWER_BRIDGE_GENERATOR.init();
      TOWER_GENERATOR.init();
      EndCityPieces.EndCityPiece var5 = addHelper(var3, new EndCityPieces.EndCityPiece(var0, "base_floor", var1, var2, true));
      var5 = addHelper(var3, addPiece(var0, var5, new BlockPos(-1, 0, -1), "second_floor_1", var2, false));
      var5 = addHelper(var3, addPiece(var0, var5, new BlockPos(-1, 4, -1), "third_floor_1", var2, false));
      var5 = addHelper(var3, addPiece(var0, var5, new BlockPos(-1, 8, -1), "third_roof", var2, true));
      recursiveChildren(var0, TOWER_GENERATOR, 1, var5, (BlockPos)null, var3, var4);
   }

   private static EndCityPieces.EndCityPiece addHelper(List<StructurePiece> var0, EndCityPieces.EndCityPiece var1) {
      var0.add(var1);
      return var1;
   }

   private static boolean recursiveChildren(StructureManager var0, EndCityPieces.SectionGenerator var1, int var2, EndCityPieces.EndCityPiece var3, BlockPos var4, List<StructurePiece> var5, Random var6) {
      if (var2 > 8) {
         return false;
      } else {
         ArrayList var7 = Lists.newArrayList();
         if (var1.generate(var0, var2, var3, var4, var7, var6)) {
            boolean var8 = false;
            int var9 = var6.nextInt();
            Iterator var10 = var7.iterator();

            while(var10.hasNext()) {
               StructurePiece var11 = (StructurePiece)var10.next();
               var11.genDepth = var9;
               StructurePiece var12 = StructurePiece.findCollisionPiece(var5, var11.getBoundingBox());
               if (var12 != null && var12.genDepth != var3.genDepth) {
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

   static {
      OVERWRITE = (new StructurePlaceSettings()).setIgnoreEntities(true).addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK);
      INSERT = (new StructurePlaceSettings()).setIgnoreEntities(true).addProcessor(BlockIgnoreProcessor.STRUCTURE_AND_AIR);
      HOUSE_TOWER_GENERATOR = new EndCityPieces.SectionGenerator() {
         public void init() {
         }

         public boolean generate(StructureManager var1, int var2, EndCityPieces.EndCityPiece var3, BlockPos var4, List<StructurePiece> var5, Random var6) {
            if (var2 > 8) {
               return false;
            } else {
               Rotation var7 = var3.placeSettings.getRotation();
               EndCityPieces.EndCityPiece var8 = EndCityPieces.addHelper(var5, EndCityPieces.addPiece(var1, var3, var4, "base_floor", var7, true));
               int var9 = var6.nextInt(3);
               if (var9 == 0) {
                  EndCityPieces.addHelper(var5, EndCityPieces.addPiece(var1, var8, new BlockPos(-1, 4, -1), "base_roof", var7, true));
               } else if (var9 == 1) {
                  var8 = EndCityPieces.addHelper(var5, EndCityPieces.addPiece(var1, var8, new BlockPos(-1, 0, -1), "second_floor_2", var7, false));
                  var8 = EndCityPieces.addHelper(var5, EndCityPieces.addPiece(var1, var8, new BlockPos(-1, 8, -1), "second_roof", var7, false));
                  EndCityPieces.recursiveChildren(var1, EndCityPieces.TOWER_GENERATOR, var2 + 1, var8, (BlockPos)null, var5, var6);
               } else if (var9 == 2) {
                  var8 = EndCityPieces.addHelper(var5, EndCityPieces.addPiece(var1, var8, new BlockPos(-1, 0, -1), "second_floor_2", var7, false));
                  var8 = EndCityPieces.addHelper(var5, EndCityPieces.addPiece(var1, var8, new BlockPos(-1, 4, -1), "third_floor_2", var7, false));
                  var8 = EndCityPieces.addHelper(var5, EndCityPieces.addPiece(var1, var8, new BlockPos(-1, 8, -1), "third_roof", var7, true));
                  EndCityPieces.recursiveChildren(var1, EndCityPieces.TOWER_GENERATOR, var2 + 1, var8, (BlockPos)null, var5, var6);
               }

               return true;
            }
         }
      };
      TOWER_BRIDGES = Lists.newArrayList(new Tuple[]{new Tuple(Rotation.NONE, new BlockPos(1, -1, 0)), new Tuple(Rotation.CLOCKWISE_90, new BlockPos(6, -1, 1)), new Tuple(Rotation.COUNTERCLOCKWISE_90, new BlockPos(0, -1, 5)), new Tuple(Rotation.CLOCKWISE_180, new BlockPos(5, -1, 6))});
      TOWER_GENERATOR = new EndCityPieces.SectionGenerator() {
         public void init() {
         }

         public boolean generate(StructureManager var1, int var2, EndCityPieces.EndCityPiece var3, BlockPos var4, List<StructurePiece> var5, Random var6) {
            Rotation var7 = var3.placeSettings.getRotation();
            EndCityPieces.EndCityPiece var8 = EndCityPieces.addHelper(var5, EndCityPieces.addPiece(var1, var3, new BlockPos(3 + var6.nextInt(2), -3, 3 + var6.nextInt(2)), "tower_base", var7, true));
            var8 = EndCityPieces.addHelper(var5, EndCityPieces.addPiece(var1, var8, new BlockPos(0, 7, 0), "tower_piece", var7, true));
            EndCityPieces.EndCityPiece var9 = var6.nextInt(3) == 0 ? var8 : null;
            int var10 = 1 + var6.nextInt(3);

            for(int var11 = 0; var11 < var10; ++var11) {
               var8 = EndCityPieces.addHelper(var5, EndCityPieces.addPiece(var1, var8, new BlockPos(0, 4, 0), "tower_piece", var7, true));
               if (var11 < var10 - 1 && var6.nextBoolean()) {
                  var9 = var8;
               }
            }

            if (var9 != null) {
               Iterator var14 = EndCityPieces.TOWER_BRIDGES.iterator();

               while(var14.hasNext()) {
                  Tuple var12 = (Tuple)var14.next();
                  if (var6.nextBoolean()) {
                     EndCityPieces.EndCityPiece var13 = EndCityPieces.addHelper(var5, EndCityPieces.addPiece(var1, var9, (BlockPos)var12.getB(), "bridge_end", var7.getRotated((Rotation)var12.getA()), true));
                     EndCityPieces.recursiveChildren(var1, EndCityPieces.TOWER_BRIDGE_GENERATOR, var2 + 1, var13, (BlockPos)null, var5, var6);
                  }
               }

               EndCityPieces.addHelper(var5, EndCityPieces.addPiece(var1, var8, new BlockPos(-1, 4, -1), "tower_top", var7, true));
            } else {
               if (var2 != 7) {
                  return EndCityPieces.recursiveChildren(var1, EndCityPieces.FAT_TOWER_GENERATOR, var2 + 1, var8, (BlockPos)null, var5, var6);
               }

               EndCityPieces.addHelper(var5, EndCityPieces.addPiece(var1, var8, new BlockPos(-1, 4, -1), "tower_top", var7, true));
            }

            return true;
         }
      };
      TOWER_BRIDGE_GENERATOR = new EndCityPieces.SectionGenerator() {
         public boolean shipCreated;

         public void init() {
            this.shipCreated = false;
         }

         public boolean generate(StructureManager var1, int var2, EndCityPieces.EndCityPiece var3, BlockPos var4, List<StructurePiece> var5, Random var6) {
            Rotation var7 = var3.placeSettings.getRotation();
            int var8 = var6.nextInt(4) + 1;
            EndCityPieces.EndCityPiece var9 = EndCityPieces.addHelper(var5, EndCityPieces.addPiece(var1, var3, new BlockPos(0, 0, -4), "bridge_piece", var7, true));
            var9.genDepth = -1;
            byte var10 = 0;

            for(int var11 = 0; var11 < var8; ++var11) {
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
               EndCityPieces.addHelper(var5, EndCityPieces.addPiece(var1, var9, new BlockPos(-8 + var6.nextInt(8), var10, -70 + var6.nextInt(10)), "ship", var7, true));
               this.shipCreated = true;
            } else if (!EndCityPieces.recursiveChildren(var1, EndCityPieces.HOUSE_TOWER_GENERATOR, var2 + 1, var9, new BlockPos(-3, var10 + 1, -11), var5, var6)) {
               return false;
            }

            var9 = EndCityPieces.addHelper(var5, EndCityPieces.addPiece(var1, var9, new BlockPos(4, var10, 0), "bridge_end", var7.getRotated(Rotation.CLOCKWISE_180), true));
            var9.genDepth = -1;
            return true;
         }
      };
      FAT_TOWER_BRIDGES = Lists.newArrayList(new Tuple[]{new Tuple(Rotation.NONE, new BlockPos(4, -1, 0)), new Tuple(Rotation.CLOCKWISE_90, new BlockPos(12, -1, 4)), new Tuple(Rotation.COUNTERCLOCKWISE_90, new BlockPos(0, -1, 8)), new Tuple(Rotation.CLOCKWISE_180, new BlockPos(8, -1, 12))});
      FAT_TOWER_GENERATOR = new EndCityPieces.SectionGenerator() {
         public void init() {
         }

         public boolean generate(StructureManager var1, int var2, EndCityPieces.EndCityPiece var3, BlockPos var4, List<StructurePiece> var5, Random var6) {
            Rotation var8 = var3.placeSettings.getRotation();
            EndCityPieces.EndCityPiece var7 = EndCityPieces.addHelper(var5, EndCityPieces.addPiece(var1, var3, new BlockPos(-3, 4, -3), "fat_tower_base", var8, true));
            var7 = EndCityPieces.addHelper(var5, EndCityPieces.addPiece(var1, var7, new BlockPos(0, 4, 0), "fat_tower_middle", var8, true));

            for(int var9 = 0; var9 < 2 && var6.nextInt(3) != 0; ++var9) {
               var7 = EndCityPieces.addHelper(var5, EndCityPieces.addPiece(var1, var7, new BlockPos(0, 8, 0), "fat_tower_middle", var8, true));
               Iterator var10 = EndCityPieces.FAT_TOWER_BRIDGES.iterator();

               while(var10.hasNext()) {
                  Tuple var11 = (Tuple)var10.next();
                  if (var6.nextBoolean()) {
                     EndCityPieces.EndCityPiece var12 = EndCityPieces.addHelper(var5, EndCityPieces.addPiece(var1, var7, (BlockPos)var11.getB(), "bridge_end", var8.getRotated((Rotation)var11.getA()), true));
                     EndCityPieces.recursiveChildren(var1, EndCityPieces.TOWER_BRIDGE_GENERATOR, var2 + 1, var12, (BlockPos)null, var5, var6);
                  }
               }
            }

            EndCityPieces.addHelper(var5, EndCityPieces.addPiece(var1, var7, new BlockPos(-2, 8, -2), "fat_tower_top", var8, true));
            return true;
         }
      };
   }

   interface SectionGenerator {
      void init();

      boolean generate(StructureManager var1, int var2, EndCityPieces.EndCityPiece var3, BlockPos var4, List<StructurePiece> var5, Random var6);
   }

   public static class EndCityPiece extends TemplateStructurePiece {
      private final String templateName;
      private final Rotation rotation;
      private final boolean overwrite;

      public EndCityPiece(StructureManager var1, String var2, BlockPos var3, Rotation var4, boolean var5) {
         super(StructurePieceType.END_CITY_PIECE, 0);
         this.templateName = var2;
         this.templatePosition = var3;
         this.rotation = var4;
         this.overwrite = var5;
         this.loadTemplate(var1);
      }

      public EndCityPiece(StructureManager var1, CompoundTag var2) {
         super(StructurePieceType.END_CITY_PIECE, var2);
         this.templateName = var2.getString("Template");
         this.rotation = Rotation.valueOf(var2.getString("Rot"));
         this.overwrite = var2.getBoolean("OW");
         this.loadTemplate(var1);
      }

      private void loadTemplate(StructureManager var1) {
         StructureTemplate var2 = var1.getOrCreate(new ResourceLocation("end_city/" + this.templateName));
         StructurePlaceSettings var3 = (this.overwrite ? EndCityPieces.OVERWRITE : EndCityPieces.INSERT).copy().setRotation(this.rotation);
         this.setup(var2, this.templatePosition, var3);
      }

      protected void addAdditionalSaveData(CompoundTag var1) {
         super.addAdditionalSaveData(var1);
         var1.putString("Template", this.templateName);
         var1.putString("Rot", this.rotation.name());
         var1.putBoolean("OW", this.overwrite);
      }

      protected void handleDataMarker(String var1, BlockPos var2, ServerLevelAccessor var3, Random var4, BoundingBox var5) {
         if (var1.startsWith("Chest")) {
            BlockPos var6 = var2.below();
            if (var5.isInside(var6)) {
               RandomizableContainerBlockEntity.setLootTable(var3, var4, var6, BuiltInLootTables.END_CITY_TREASURE);
            }
         } else if (var1.startsWith("Sentry")) {
            Shulker var7 = (Shulker)EntityType.SHULKER.create(var3.getLevel());
            var7.setPos((double)var2.getX() + 0.5D, (double)var2.getY() + 0.5D, (double)var2.getZ() + 0.5D);
            var7.setAttachPosition(var2);
            var3.addFreshEntity(var7);
         } else if (var1.startsWith("Elytra")) {
            ItemFrame var8 = new ItemFrame(var3.getLevel(), var2, this.rotation.rotate(Direction.SOUTH));
            var8.setItem(new ItemStack(Items.ELYTRA), false);
            var3.addFreshEntity(var8);
         }

      }
   }
}

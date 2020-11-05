package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.AlwaysTrueTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlackstoneReplaceProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockAgeProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.LavaSubmergedBlockProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.ProcessorRule;
import net.minecraft.world.level.levelgen.structure.templatesystem.RandomBlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RuinedPortalPiece extends TemplateStructurePiece {
   private static final Logger LOGGER = LogManager.getLogger();
   private final ResourceLocation templateLocation;
   private final Rotation rotation;
   private final Mirror mirror;
   private final RuinedPortalPiece.VerticalPlacement verticalPlacement;
   private final RuinedPortalPiece.Properties properties;

   public RuinedPortalPiece(BlockPos var1, RuinedPortalPiece.VerticalPlacement var2, RuinedPortalPiece.Properties var3, ResourceLocation var4, StructureTemplate var5, Rotation var6, Mirror var7, BlockPos var8) {
      super(StructurePieceType.RUINED_PORTAL, 0);
      this.templatePosition = var1;
      this.templateLocation = var4;
      this.rotation = var6;
      this.mirror = var7;
      this.verticalPlacement = var2;
      this.properties = var3;
      this.loadTemplate(var5, var8);
   }

   public RuinedPortalPiece(StructureManager var1, CompoundTag var2) {
      super(StructurePieceType.RUINED_PORTAL, var2);
      this.templateLocation = new ResourceLocation(var2.getString("Template"));
      this.rotation = Rotation.valueOf(var2.getString("Rotation"));
      this.mirror = Mirror.valueOf(var2.getString("Mirror"));
      this.verticalPlacement = RuinedPortalPiece.VerticalPlacement.byName(var2.getString("VerticalPlacement"));
      DataResult var10001 = RuinedPortalPiece.Properties.CODEC.parse(new Dynamic(NbtOps.INSTANCE, var2.get("Properties")));
      Logger var10003 = LOGGER;
      var10003.getClass();
      this.properties = (RuinedPortalPiece.Properties)var10001.getOrThrow(true, var10003::error);
      StructureTemplate var3 = var1.getOrCreate(this.templateLocation);
      this.loadTemplate(var3, new BlockPos(var3.getSize().getX() / 2, 0, var3.getSize().getZ() / 2));
   }

   protected void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putString("Template", this.templateLocation.toString());
      var1.putString("Rotation", this.rotation.name());
      var1.putString("Mirror", this.mirror.name());
      var1.putString("VerticalPlacement", this.verticalPlacement.getName());
      DataResult var10000 = RuinedPortalPiece.Properties.CODEC.encodeStart(NbtOps.INSTANCE, this.properties);
      Logger var10001 = LOGGER;
      var10001.getClass();
      var10000.resultOrPartial(var10001::error).ifPresent((var1x) -> {
         var1.put("Properties", var1x);
      });
   }

   private void loadTemplate(StructureTemplate var1, BlockPos var2) {
      BlockIgnoreProcessor var3 = this.properties.airPocket ? BlockIgnoreProcessor.STRUCTURE_BLOCK : BlockIgnoreProcessor.STRUCTURE_AND_AIR;
      ArrayList var4 = Lists.newArrayList();
      var4.add(getBlockReplaceRule(Blocks.GOLD_BLOCK, 0.3F, Blocks.AIR));
      var4.add(this.getLavaProcessorRule());
      if (!this.properties.cold) {
         var4.add(getBlockReplaceRule(Blocks.NETHERRACK, 0.07F, Blocks.MAGMA_BLOCK));
      }

      StructurePlaceSettings var5 = (new StructurePlaceSettings()).setRotation(this.rotation).setMirror(this.mirror).setRotationPivot(var2).addProcessor(var3).addProcessor(new RuleProcessor(var4)).addProcessor(new BlockAgeProcessor(this.properties.mossiness)).addProcessor(new LavaSubmergedBlockProcessor());
      if (this.properties.replaceWithBlackstone) {
         var5.addProcessor(BlackstoneReplaceProcessor.INSTANCE);
      }

      this.setup(var1, this.templatePosition, var5);
   }

   private ProcessorRule getLavaProcessorRule() {
      if (this.verticalPlacement == RuinedPortalPiece.VerticalPlacement.ON_OCEAN_FLOOR) {
         return getBlockReplaceRule(Blocks.LAVA, Blocks.MAGMA_BLOCK);
      } else {
         return this.properties.cold ? getBlockReplaceRule(Blocks.LAVA, Blocks.NETHERRACK) : getBlockReplaceRule(Blocks.LAVA, 0.2F, Blocks.MAGMA_BLOCK);
      }
   }

   public boolean postProcess(WorldGenLevel var1, StructureFeatureManager var2, ChunkGenerator var3, Random var4, BoundingBox var5, ChunkPos var6, BlockPos var7) {
      if (!var5.isInside(this.templatePosition)) {
         return true;
      } else {
         var5.expand(this.template.getBoundingBox(this.placeSettings, this.templatePosition));
         boolean var8 = super.postProcess(var1, var2, var3, var4, var5, var6, var7);
         this.spreadNetherrack(var4, var1);
         this.addNetherrackDripColumnsBelowPortal(var4, var1);
         if (this.properties.vines || this.properties.overgrown) {
            BlockPos.betweenClosedStream(this.getBoundingBox()).forEach((var3x) -> {
               if (this.properties.vines) {
                  this.maybeAddVines(var4, var1, var3x);
               }

               if (this.properties.overgrown) {
                  this.maybeAddLeavesAbove(var4, var1, var3x);
               }

            });
         }

         return var8;
      }
   }

   protected void handleDataMarker(String var1, BlockPos var2, ServerLevelAccessor var3, Random var4, BoundingBox var5) {
   }

   private void maybeAddVines(Random var1, LevelAccessor var2, BlockPos var3) {
      BlockState var4 = var2.getBlockState(var3);
      if (!var4.isAir() && !var4.is(Blocks.VINE)) {
         Direction var5 = Direction.Plane.HORIZONTAL.getRandomDirection(var1);
         BlockPos var6 = var3.relative(var5);
         BlockState var7 = var2.getBlockState(var6);
         if (var7.isAir()) {
            if (Block.isFaceFull(var4.getCollisionShape(var2, var3), var5)) {
               BooleanProperty var8 = VineBlock.getPropertyForFace(var5.getOpposite());
               var2.setBlock(var6, (BlockState)Blocks.VINE.defaultBlockState().setValue(var8, true), 3);
            }
         }
      }
   }

   private void maybeAddLeavesAbove(Random var1, LevelAccessor var2, BlockPos var3) {
      if (var1.nextFloat() < 0.5F && var2.getBlockState(var3).is(Blocks.NETHERRACK) && var2.getBlockState(var3.above()).isAir()) {
         var2.setBlock(var3.above(), (BlockState)Blocks.JUNGLE_LEAVES.defaultBlockState().setValue(LeavesBlock.PERSISTENT, true), 3);
      }

   }

   private void addNetherrackDripColumnsBelowPortal(Random var1, LevelAccessor var2) {
      for(int var3 = this.boundingBox.x0 + 1; var3 < this.boundingBox.x1; ++var3) {
         for(int var4 = this.boundingBox.z0 + 1; var4 < this.boundingBox.z1; ++var4) {
            BlockPos var5 = new BlockPos(var3, this.boundingBox.y0, var4);
            if (var2.getBlockState(var5).is(Blocks.NETHERRACK)) {
               this.addNetherrackDripColumn(var1, var2, var5.below());
            }
         }
      }

   }

   private void addNetherrackDripColumn(Random var1, LevelAccessor var2, BlockPos var3) {
      BlockPos.MutableBlockPos var4 = var3.mutable();
      this.placeNetherrackOrMagma(var1, var2, var4);
      int var5 = 8;

      while(var5 > 0 && var1.nextFloat() < 0.5F) {
         var4.move(Direction.DOWN);
         --var5;
         this.placeNetherrackOrMagma(var1, var2, var4);
      }

   }

   private void spreadNetherrack(Random var1, LevelAccessor var2) {
      boolean var3 = this.verticalPlacement == RuinedPortalPiece.VerticalPlacement.ON_LAND_SURFACE || this.verticalPlacement == RuinedPortalPiece.VerticalPlacement.ON_OCEAN_FLOOR;
      Vec3i var4 = this.boundingBox.getCenter();
      int var5 = var4.getX();
      int var6 = var4.getZ();
      float[] var7 = new float[]{1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.9F, 0.9F, 0.8F, 0.7F, 0.6F, 0.4F, 0.2F};
      int var8 = var7.length;
      int var9 = (this.boundingBox.getXSpan() + this.boundingBox.getZSpan()) / 2;
      int var10 = var1.nextInt(Math.max(1, 8 - var9 / 2));
      boolean var11 = true;
      BlockPos.MutableBlockPos var12 = BlockPos.ZERO.mutable();

      for(int var13 = var5 - var8; var13 <= var5 + var8; ++var13) {
         for(int var14 = var6 - var8; var14 <= var6 + var8; ++var14) {
            int var15 = Math.abs(var13 - var5) + Math.abs(var14 - var6);
            int var16 = Math.max(0, var15 + var10);
            if (var16 < var8) {
               float var17 = var7[var16];
               if (var1.nextDouble() < (double)var17) {
                  int var18 = getSurfaceY(var2, var13, var14, this.verticalPlacement);
                  int var19 = var3 ? var18 : Math.min(this.boundingBox.y0, var18);
                  var12.set(var13, var19, var14);
                  if (Math.abs(var19 - this.boundingBox.y0) <= 3 && this.canBlockBeReplacedByNetherrackOrMagma(var2, var12)) {
                     this.placeNetherrackOrMagma(var1, var2, var12);
                     if (this.properties.overgrown) {
                        this.maybeAddLeavesAbove(var1, var2, var12);
                     }

                     this.addNetherrackDripColumn(var1, var2, var12.below());
                  }
               }
            }
         }
      }

   }

   private boolean canBlockBeReplacedByNetherrackOrMagma(LevelAccessor var1, BlockPos var2) {
      BlockState var3 = var1.getBlockState(var2);
      return !var3.is(Blocks.AIR) && !var3.is(Blocks.OBSIDIAN) && !var3.is(Blocks.CHEST) && (this.verticalPlacement == RuinedPortalPiece.VerticalPlacement.IN_NETHER || !var3.is(Blocks.LAVA));
   }

   private void placeNetherrackOrMagma(Random var1, LevelAccessor var2, BlockPos var3) {
      if (!this.properties.cold && var1.nextFloat() < 0.07F) {
         var2.setBlock(var3, Blocks.MAGMA_BLOCK.defaultBlockState(), 3);
      } else {
         var2.setBlock(var3, Blocks.NETHERRACK.defaultBlockState(), 3);
      }

   }

   private static int getSurfaceY(LevelAccessor var0, int var1, int var2, RuinedPortalPiece.VerticalPlacement var3) {
      return var0.getHeight(getHeightMapType(var3), var1, var2) - 1;
   }

   public static Heightmap.Types getHeightMapType(RuinedPortalPiece.VerticalPlacement var0) {
      return var0 == RuinedPortalPiece.VerticalPlacement.ON_OCEAN_FLOOR ? Heightmap.Types.OCEAN_FLOOR_WG : Heightmap.Types.WORLD_SURFACE_WG;
   }

   private static ProcessorRule getBlockReplaceRule(Block var0, float var1, Block var2) {
      return new ProcessorRule(new RandomBlockMatchTest(var0, var1), AlwaysTrueTest.INSTANCE, var2.defaultBlockState());
   }

   private static ProcessorRule getBlockReplaceRule(Block var0, Block var1) {
      return new ProcessorRule(new BlockMatchTest(var0), AlwaysTrueTest.INSTANCE, var1.defaultBlockState());
   }

   public static enum VerticalPlacement {
      ON_LAND_SURFACE("on_land_surface"),
      PARTLY_BURIED("partly_buried"),
      ON_OCEAN_FLOOR("on_ocean_floor"),
      IN_MOUNTAIN("in_mountain"),
      UNDERGROUND("underground"),
      IN_NETHER("in_nether");

      private static final Map<String, RuinedPortalPiece.VerticalPlacement> BY_NAME = (Map)Arrays.stream(values()).collect(Collectors.toMap(RuinedPortalPiece.VerticalPlacement::getName, (var0) -> {
         return var0;
      }));
      private final String name;

      private VerticalPlacement(String var3) {
         this.name = var3;
      }

      public String getName() {
         return this.name;
      }

      public static RuinedPortalPiece.VerticalPlacement byName(String var0) {
         return (RuinedPortalPiece.VerticalPlacement)BY_NAME.get(var0);
      }
   }

   public static class Properties {
      public static final Codec<RuinedPortalPiece.Properties> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(Codec.BOOL.fieldOf("cold").forGetter((var0x) -> {
            return var0x.cold;
         }), Codec.FLOAT.fieldOf("mossiness").forGetter((var0x) -> {
            return var0x.mossiness;
         }), Codec.BOOL.fieldOf("air_pocket").forGetter((var0x) -> {
            return var0x.airPocket;
         }), Codec.BOOL.fieldOf("overgrown").forGetter((var0x) -> {
            return var0x.overgrown;
         }), Codec.BOOL.fieldOf("vines").forGetter((var0x) -> {
            return var0x.vines;
         }), Codec.BOOL.fieldOf("replace_with_blackstone").forGetter((var0x) -> {
            return var0x.replaceWithBlackstone;
         })).apply(var0, RuinedPortalPiece.Properties::new);
      });
      public boolean cold;
      public float mossiness = 0.2F;
      public boolean airPocket;
      public boolean overgrown;
      public boolean vines;
      public boolean replaceWithBlackstone;

      public Properties() {
         super();
      }

      public <T> Properties(boolean var1, float var2, boolean var3, boolean var4, boolean var5, boolean var6) {
         super();
         this.cold = var1;
         this.mossiness = var2;
         this.airPocket = var3;
         this.overgrown = var4;
         this.vines = var5;
         this.replaceWithBlackstone = var6;
      }
   }
}

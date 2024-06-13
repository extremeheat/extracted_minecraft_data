package net.minecraft.world.level.levelgen.structure.structures;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
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
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.AlwaysTrueTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlackstoneReplaceProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockAgeProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.LavaSubmergedBlockProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.ProcessorRule;
import net.minecraft.world.level.levelgen.structure.templatesystem.ProtectedBlockProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.RandomBlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.slf4j.Logger;

public class RuinedPortalPiece extends TemplateStructurePiece {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final float PROBABILITY_OF_GOLD_GONE = 0.3F;
   private static final float PROBABILITY_OF_MAGMA_INSTEAD_OF_NETHERRACK = 0.07F;
   private static final float PROBABILITY_OF_MAGMA_INSTEAD_OF_LAVA = 0.2F;
   private final RuinedPortalPiece.VerticalPlacement verticalPlacement;
   private final RuinedPortalPiece.Properties properties;

   public RuinedPortalPiece(
      StructureTemplateManager var1,
      BlockPos var2,
      RuinedPortalPiece.VerticalPlacement var3,
      RuinedPortalPiece.Properties var4,
      ResourceLocation var5,
      StructureTemplate var6,
      Rotation var7,
      Mirror var8,
      BlockPos var9
   ) {
      super(StructurePieceType.RUINED_PORTAL, 0, var1, var5, var5.toString(), makeSettings(var8, var7, var3, var9, var4), var2);
      this.verticalPlacement = var3;
      this.properties = var4;
   }

   public RuinedPortalPiece(StructureTemplateManager var1, CompoundTag var2) {
      super(StructurePieceType.RUINED_PORTAL, var2, var1, var2x -> makeSettings(var1, var2, var2x));
      this.verticalPlacement = RuinedPortalPiece.VerticalPlacement.byName(var2.getString("VerticalPlacement"));
      this.properties = (RuinedPortalPiece.Properties)RuinedPortalPiece.Properties.CODEC
         .parse(new Dynamic(NbtOps.INSTANCE, var2.get("Properties")))
         .getPartialOrThrow();
   }

   @Override
   protected void addAdditionalSaveData(StructurePieceSerializationContext var1, CompoundTag var2) {
      super.addAdditionalSaveData(var1, var2);
      var2.putString("Rotation", this.placeSettings.getRotation().name());
      var2.putString("Mirror", this.placeSettings.getMirror().name());
      var2.putString("VerticalPlacement", this.verticalPlacement.getName());
      RuinedPortalPiece.Properties.CODEC
         .encodeStart(NbtOps.INSTANCE, this.properties)
         .resultOrPartial(LOGGER::error)
         .ifPresent(var1x -> var2.put("Properties", var1x));
   }

   private static StructurePlaceSettings makeSettings(StructureTemplateManager var0, CompoundTag var1, ResourceLocation var2) {
      StructureTemplate var3 = var0.getOrCreate(var2);
      BlockPos var4 = new BlockPos(var3.getSize().getX() / 2, 0, var3.getSize().getZ() / 2);
      return makeSettings(
         Mirror.valueOf(var1.getString("Mirror")),
         Rotation.valueOf(var1.getString("Rotation")),
         RuinedPortalPiece.VerticalPlacement.byName(var1.getString("VerticalPlacement")),
         var4,
         (RuinedPortalPiece.Properties)RuinedPortalPiece.Properties.CODEC.parse(new Dynamic(NbtOps.INSTANCE, var1.get("Properties"))).getPartialOrThrow()
      );
   }

   private static StructurePlaceSettings makeSettings(
      Mirror var0, Rotation var1, RuinedPortalPiece.VerticalPlacement var2, BlockPos var3, RuinedPortalPiece.Properties var4
   ) {
      BlockIgnoreProcessor var5 = var4.airPocket ? BlockIgnoreProcessor.STRUCTURE_BLOCK : BlockIgnoreProcessor.STRUCTURE_AND_AIR;
      ArrayList var6 = Lists.newArrayList();
      var6.add(getBlockReplaceRule(Blocks.GOLD_BLOCK, 0.3F, Blocks.AIR));
      var6.add(getLavaProcessorRule(var2, var4));
      if (!var4.cold) {
         var6.add(getBlockReplaceRule(Blocks.NETHERRACK, 0.07F, Blocks.MAGMA_BLOCK));
      }

      StructurePlaceSettings var7 = new StructurePlaceSettings()
         .setRotation(var1)
         .setMirror(var0)
         .setRotationPivot(var3)
         .addProcessor(var5)
         .addProcessor(new RuleProcessor(var6))
         .addProcessor(new BlockAgeProcessor(var4.mossiness))
         .addProcessor(new ProtectedBlockProcessor(BlockTags.FEATURES_CANNOT_REPLACE))
         .addProcessor(new LavaSubmergedBlockProcessor());
      if (var4.replaceWithBlackstone) {
         var7.addProcessor(BlackstoneReplaceProcessor.INSTANCE);
      }

      return var7;
   }

   private static ProcessorRule getLavaProcessorRule(RuinedPortalPiece.VerticalPlacement var0, RuinedPortalPiece.Properties var1) {
      if (var0 == RuinedPortalPiece.VerticalPlacement.ON_OCEAN_FLOOR) {
         return getBlockReplaceRule(Blocks.LAVA, Blocks.MAGMA_BLOCK);
      } else {
         return var1.cold ? getBlockReplaceRule(Blocks.LAVA, Blocks.NETHERRACK) : getBlockReplaceRule(Blocks.LAVA, 0.2F, Blocks.MAGMA_BLOCK);
      }
   }

   @Override
   public void postProcess(WorldGenLevel var1, StructureManager var2, ChunkGenerator var3, RandomSource var4, BoundingBox var5, ChunkPos var6, BlockPos var7) {
      BoundingBox var8 = this.template.getBoundingBox(this.placeSettings, this.templatePosition);
      if (var5.isInside(var8.getCenter())) {
         var5.encapsulate(var8);
         super.postProcess(var1, var2, var3, var4, var5, var6, var7);
         this.spreadNetherrack(var4, var1);
         this.addNetherrackDripColumnsBelowPortal(var4, var1);
         if (this.properties.vines || this.properties.overgrown) {
            BlockPos.betweenClosedStream(this.getBoundingBox()).forEach(var3x -> {
               if (this.properties.vines) {
                  this.maybeAddVines(var4, var1, var3x);
               }

               if (this.properties.overgrown) {
                  this.maybeAddLeavesAbove(var4, var1, var3x);
               }
            });
         }
      }
   }

   @Override
   protected void handleDataMarker(String var1, BlockPos var2, ServerLevelAccessor var3, RandomSource var4, BoundingBox var5) {
   }

   private void maybeAddVines(RandomSource var1, LevelAccessor var2, BlockPos var3) {
      BlockState var4 = var2.getBlockState(var3);
      if (!var4.isAir() && !var4.is(Blocks.VINE)) {
         Direction var5 = getRandomHorizontalDirection(var1);
         BlockPos var6 = var3.relative(var5);
         BlockState var7 = var2.getBlockState(var6);
         if (var7.isAir()) {
            if (Block.isFaceFull(var4.getCollisionShape(var2, var3), var5)) {
               BooleanProperty var8 = VineBlock.getPropertyForFace(var5.getOpposite());
               var2.setBlock(var6, Blocks.VINE.defaultBlockState().setValue(var8, Boolean.valueOf(true)), 3);
            }
         }
      }
   }

   private void maybeAddLeavesAbove(RandomSource var1, LevelAccessor var2, BlockPos var3) {
      if (var1.nextFloat() < 0.5F && var2.getBlockState(var3).is(Blocks.NETHERRACK) && var2.getBlockState(var3.above()).isAir()) {
         var2.setBlock(var3.above(), Blocks.JUNGLE_LEAVES.defaultBlockState().setValue(LeavesBlock.PERSISTENT, Boolean.valueOf(true)), 3);
      }
   }

   private void addNetherrackDripColumnsBelowPortal(RandomSource var1, LevelAccessor var2) {
      for (int var3 = this.boundingBox.minX() + 1; var3 < this.boundingBox.maxX(); var3++) {
         for (int var4 = this.boundingBox.minZ() + 1; var4 < this.boundingBox.maxZ(); var4++) {
            BlockPos var5 = new BlockPos(var3, this.boundingBox.minY(), var4);
            if (var2.getBlockState(var5).is(Blocks.NETHERRACK)) {
               this.addNetherrackDripColumn(var1, var2, var5.below());
            }
         }
      }
   }

   private void addNetherrackDripColumn(RandomSource var1, LevelAccessor var2, BlockPos var3) {
      BlockPos.MutableBlockPos var4 = var3.mutable();
      this.placeNetherrackOrMagma(var1, var2, var4);
      int var5 = 8;

      while (var5 > 0 && var1.nextFloat() < 0.5F) {
         var4.move(Direction.DOWN);
         var5--;
         this.placeNetherrackOrMagma(var1, var2, var4);
      }
   }

   private void spreadNetherrack(RandomSource var1, LevelAccessor var2) {
      boolean var3 = this.verticalPlacement == RuinedPortalPiece.VerticalPlacement.ON_LAND_SURFACE
         || this.verticalPlacement == RuinedPortalPiece.VerticalPlacement.ON_OCEAN_FLOOR;
      BlockPos var4 = this.boundingBox.getCenter();
      int var5 = var4.getX();
      int var6 = var4.getZ();
      float[] var7 = new float[]{1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.9F, 0.9F, 0.8F, 0.7F, 0.6F, 0.4F, 0.2F};
      int var8 = var7.length;
      int var9 = (this.boundingBox.getXSpan() + this.boundingBox.getZSpan()) / 2;
      int var10 = var1.nextInt(Math.max(1, 8 - var9 / 2));
      byte var11 = 3;
      BlockPos.MutableBlockPos var12 = BlockPos.ZERO.mutable();

      for (int var13 = var5 - var8; var13 <= var5 + var8; var13++) {
         for (int var14 = var6 - var8; var14 <= var6 + var8; var14++) {
            int var15 = Math.abs(var13 - var5) + Math.abs(var14 - var6);
            int var16 = Math.max(0, var15 + var10);
            if (var16 < var8) {
               float var17 = var7[var16];
               if (var1.nextDouble() < (double)var17) {
                  int var18 = getSurfaceY(var2, var13, var14, this.verticalPlacement);
                  int var19 = var3 ? var18 : Math.min(this.boundingBox.minY(), var18);
                  var12.set(var13, var19, var14);
                  if (Math.abs(var19 - this.boundingBox.minY()) <= 3 && this.canBlockBeReplacedByNetherrackOrMagma(var2, var12)) {
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
      return !var3.is(Blocks.AIR)
         && !var3.is(Blocks.OBSIDIAN)
         && !var3.is(BlockTags.FEATURES_CANNOT_REPLACE)
         && (this.verticalPlacement == RuinedPortalPiece.VerticalPlacement.IN_NETHER || !var3.is(Blocks.LAVA));
   }

   private void placeNetherrackOrMagma(RandomSource var1, LevelAccessor var2, BlockPos var3) {
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

   public static class Properties {
      public static final Codec<RuinedPortalPiece.Properties> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  Codec.BOOL.fieldOf("cold").forGetter(var0x -> var0x.cold),
                  Codec.FLOAT.fieldOf("mossiness").forGetter(var0x -> var0x.mossiness),
                  Codec.BOOL.fieldOf("air_pocket").forGetter(var0x -> var0x.airPocket),
                  Codec.BOOL.fieldOf("overgrown").forGetter(var0x -> var0x.overgrown),
                  Codec.BOOL.fieldOf("vines").forGetter(var0x -> var0x.vines),
                  Codec.BOOL.fieldOf("replace_with_blackstone").forGetter(var0x -> var0x.replaceWithBlackstone)
               )
               .apply(var0, RuinedPortalPiece.Properties::new)
      );
      public boolean cold;
      public float mossiness;
      public boolean airPocket;
      public boolean overgrown;
      public boolean vines;
      public boolean replaceWithBlackstone;

      public Properties() {
         super();
      }

      public Properties(boolean var1, float var2, boolean var3, boolean var4, boolean var5, boolean var6) {
         super();
         this.cold = var1;
         this.mossiness = var2;
         this.airPocket = var3;
         this.overgrown = var4;
         this.vines = var5;
         this.replaceWithBlackstone = var6;
      }
   }

   public static enum VerticalPlacement implements StringRepresentable {
      ON_LAND_SURFACE("on_land_surface"),
      PARTLY_BURIED("partly_buried"),
      ON_OCEAN_FLOOR("on_ocean_floor"),
      IN_MOUNTAIN("in_mountain"),
      UNDERGROUND("underground"),
      IN_NETHER("in_nether");

      public static final StringRepresentable.EnumCodec<RuinedPortalPiece.VerticalPlacement> CODEC = StringRepresentable.fromEnum(
         RuinedPortalPiece.VerticalPlacement::values
      );
      private final String name;

      private VerticalPlacement(String var3) {
         this.name = var3;
      }

      public String getName() {
         return this.name;
      }

      public static RuinedPortalPiece.VerticalPlacement byName(String var0) {
         return CODEC.byName(var0);
      }

      @Override
      public String getSerializedName() {
         return this.name;
      }
   }
}

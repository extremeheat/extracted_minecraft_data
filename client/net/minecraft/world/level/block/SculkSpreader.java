package net.minecraft.world.level.block;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;

public class SculkSpreader {
   public static final int MAX_GROWTH_RATE_RADIUS = 24;
   public static final int MAX_CHARGE = 1000;
   public static final float MAX_DECAY_FACTOR = 0.5F;
   private static final int MAX_CURSORS = 32;
   public static final int SHRIEKER_PLACEMENT_RATE = 11;
   public static final int MAX_CURSOR_DISTANCE = 1024;
   final boolean isWorldGeneration;
   private final TagKey<Block> replaceableBlocks;
   private final int growthSpawnCost;
   private final int noGrowthRadius;
   private final int chargeDecayRate;
   private final int additionalDecayRate;
   private List<ChargeCursor> cursors = new ArrayList();
   private static final Logger LOGGER = LogUtils.getLogger();

   public SculkSpreader(boolean var1, TagKey<Block> var2, int var3, int var4, int var5, int var6) {
      super();
      this.isWorldGeneration = var1;
      this.replaceableBlocks = var2;
      this.growthSpawnCost = var3;
      this.noGrowthRadius = var4;
      this.chargeDecayRate = var5;
      this.additionalDecayRate = var6;
   }

   public static SculkSpreader createLevelSpreader() {
      return new SculkSpreader(false, BlockTags.SCULK_REPLACEABLE, 10, 4, 10, 5);
   }

   public static SculkSpreader createWorldGenSpreader() {
      return new SculkSpreader(true, BlockTags.SCULK_REPLACEABLE_WORLD_GEN, 50, 1, 5, 10);
   }

   public TagKey<Block> replaceableBlocks() {
      return this.replaceableBlocks;
   }

   public int growthSpawnCost() {
      return this.growthSpawnCost;
   }

   public int noGrowthRadius() {
      return this.noGrowthRadius;
   }

   public int chargeDecayRate() {
      return this.chargeDecayRate;
   }

   public int additionalDecayRate() {
      return this.additionalDecayRate;
   }

   public boolean isWorldGeneration() {
      return this.isWorldGeneration;
   }

   @VisibleForTesting
   public List<ChargeCursor> getCursors() {
      return this.cursors;
   }

   public void clear() {
      this.cursors.clear();
   }

   public void load(CompoundTag var1) {
      if (var1.contains("cursors", 9)) {
         this.cursors.clear();
         DataResult var10000 = SculkSpreader.ChargeCursor.CODEC.listOf().parse(new Dynamic(NbtOps.INSTANCE, var1.getList("cursors", 10)));
         Logger var10001 = LOGGER;
         Objects.requireNonNull(var10001);
         List var2 = (List)var10000.resultOrPartial(var10001::error).orElseGet(ArrayList::new);
         int var3 = Math.min(var2.size(), 32);

         for(int var4 = 0; var4 < var3; ++var4) {
            this.addCursor((ChargeCursor)var2.get(var4));
         }
      }

   }

   public void save(CompoundTag var1) {
      DataResult var10000 = SculkSpreader.ChargeCursor.CODEC.listOf().encodeStart(NbtOps.INSTANCE, this.cursors);
      Logger var10001 = LOGGER;
      Objects.requireNonNull(var10001);
      var10000.resultOrPartial(var10001::error).ifPresent((var1x) -> var1.put("cursors", var1x));
   }

   public void addCursors(BlockPos var1, int var2) {
      while(var2 > 0) {
         int var3 = Math.min(var2, 1000);
         this.addCursor(new ChargeCursor(var1, var3));
         var2 -= var3;
      }

   }

   private void addCursor(ChargeCursor var1) {
      if (this.cursors.size() < 32) {
         this.cursors.add(var1);
      }
   }

   public void updateCursors(LevelAccessor var1, BlockPos var2, RandomSource var3, boolean var4) {
      if (!this.cursors.isEmpty()) {
         ArrayList var5 = new ArrayList();
         HashMap var6 = new HashMap();
         Object2IntOpenHashMap var7 = new Object2IntOpenHashMap();

         for(ChargeCursor var9 : this.cursors) {
            if (!var9.isPosUnreasonable(var2)) {
               var9.update(var1, var2, var3, this, var4);
               if (var9.charge <= 0) {
                  var1.levelEvent(3006, var9.getPos(), 0);
               } else {
                  BlockPos var10 = var9.getPos();
                  var7.computeInt(var10, (var1x, var2x) -> (var2x == null ? 0 : var2x) + var9.charge);
                  ChargeCursor var11 = (ChargeCursor)var6.get(var10);
                  if (var11 == null) {
                     var6.put(var10, var9);
                     var5.add(var9);
                  } else if (!this.isWorldGeneration() && var9.charge + var11.charge <= 1000) {
                     var11.mergeWith(var9);
                  } else {
                     var5.add(var9);
                     if (var9.charge < var11.charge) {
                        var6.put(var10, var9);
                     }
                  }
               }
            }
         }

         ObjectIterator var16 = var7.object2IntEntrySet().iterator();

         while(var16.hasNext()) {
            Object2IntMap.Entry var17 = (Object2IntMap.Entry)var16.next();
            BlockPos var18 = (BlockPos)var17.getKey();
            int var19 = var17.getIntValue();
            ChargeCursor var12 = (ChargeCursor)var6.get(var18);
            Set var13 = var12 == null ? null : var12.getFacingData();
            if (var19 > 0 && var13 != null) {
               int var14 = (int)(Math.log1p((double)var19) / 2.299999952316284) + 1;
               int var15 = (var14 << 6) + MultifaceBlock.pack(var13);
               var1.levelEvent(3006, var18, var15);
            }
         }

         this.cursors = var5;
      }
   }

   // $FF: synthetic method
   private static Integer lambda$save$1(ChargeCursor var0) {
      return 1;
   }

   public static class ChargeCursor {
      private static final ObjectArrayList<Vec3i> NON_CORNER_NEIGHBOURS = (ObjectArrayList)Util.make(new ObjectArrayList(18), (var0) -> {
         Stream var10000 = BlockPos.betweenClosedStream(new BlockPos(-1, -1, -1), new BlockPos(1, 1, 1)).filter((var0x) -> (var0x.getX() == 0 || var0x.getY() == 0 || var0x.getZ() == 0) && !var0x.equals(BlockPos.ZERO)).map(BlockPos::immutable);
         Objects.requireNonNull(var0);
         var10000.forEach(var0::add);
      });
      public static final int MAX_CURSOR_DECAY_DELAY = 1;
      private BlockPos pos;
      int charge;
      private int updateDelay;
      private int decayDelay;
      @Nullable
      private Set<Direction> facings;
      private static final Codec<Set<Direction>> DIRECTION_SET;
      public static final Codec<ChargeCursor> CODEC;

      private ChargeCursor(BlockPos var1, int var2, int var3, int var4, Optional<Set<Direction>> var5) {
         super();
         this.pos = var1;
         this.charge = var2;
         this.decayDelay = var3;
         this.updateDelay = var4;
         this.facings = (Set)var5.orElse((Object)null);
      }

      public ChargeCursor(BlockPos var1, int var2) {
         this(var1, var2, 1, 0, Optional.empty());
      }

      public BlockPos getPos() {
         return this.pos;
      }

      boolean isPosUnreasonable(BlockPos var1) {
         return this.pos.distChessboard(var1) > 1024;
      }

      public int getCharge() {
         return this.charge;
      }

      public int getDecayDelay() {
         return this.decayDelay;
      }

      @Nullable
      public Set<Direction> getFacingData() {
         return this.facings;
      }

      private boolean shouldUpdate(LevelAccessor var1, BlockPos var2, boolean var3) {
         if (this.charge <= 0) {
            return false;
         } else if (var3) {
            return true;
         } else if (var1 instanceof ServerLevel) {
            ServerLevel var4 = (ServerLevel)var1;
            return var4.shouldTickBlocksAt(var2);
         } else {
            return false;
         }
      }

      public void update(LevelAccessor var1, BlockPos var2, RandomSource var3, SculkSpreader var4, boolean var5) {
         if (this.shouldUpdate(var1, var2, var4.isWorldGeneration)) {
            if (this.updateDelay > 0) {
               --this.updateDelay;
            } else {
               BlockState var6 = var1.getBlockState(this.pos);
               SculkBehaviour var7 = getBlockBehaviour(var6);
               if (var5 && var7.attemptSpreadVein(var1, this.pos, var6, this.facings, var4.isWorldGeneration())) {
                  if (var7.canChangeBlockStateOnSpread()) {
                     var6 = var1.getBlockState(this.pos);
                     var7 = getBlockBehaviour(var6);
                  }

                  var1.playSound((Player)null, this.pos, SoundEvents.SCULK_BLOCK_SPREAD, SoundSource.BLOCKS, 1.0F, 1.0F);
               }

               this.charge = var7.attemptUseCharge(this, var1, var2, var3, var4, var5);
               if (this.charge <= 0) {
                  var7.onDischarged(var1, var6, this.pos, var3);
               } else {
                  BlockPos var8 = getValidMovementPos(var1, this.pos, var3);
                  if (var8 != null) {
                     var7.onDischarged(var1, var6, this.pos, var3);
                     this.pos = var8.immutable();
                     if (var4.isWorldGeneration() && !this.pos.closerThan(new Vec3i(var2.getX(), this.pos.getY(), var2.getZ()), 15.0)) {
                        this.charge = 0;
                        return;
                     }

                     var6 = var1.getBlockState(var8);
                  }

                  if (var6.getBlock() instanceof SculkBehaviour) {
                     this.facings = MultifaceBlock.availableFaces(var6);
                  }

                  this.decayDelay = var7.updateDecayDelay(this.decayDelay);
                  this.updateDelay = var7.getSculkSpreadDelay();
               }
            }
         }
      }

      void mergeWith(ChargeCursor var1) {
         this.charge += var1.charge;
         var1.charge = 0;
         this.updateDelay = Math.min(this.updateDelay, var1.updateDelay);
      }

      private static SculkBehaviour getBlockBehaviour(BlockState var0) {
         Block var2 = var0.getBlock();
         SculkBehaviour var10000;
         if (var2 instanceof SculkBehaviour var1) {
            var10000 = var1;
         } else {
            var10000 = SculkBehaviour.DEFAULT;
         }

         return var10000;
      }

      private static List<Vec3i> getRandomizedNonCornerNeighbourOffsets(RandomSource var0) {
         return Util.shuffledCopy(NON_CORNER_NEIGHBOURS, var0);
      }

      @Nullable
      private static BlockPos getValidMovementPos(LevelAccessor var0, BlockPos var1, RandomSource var2) {
         BlockPos.MutableBlockPos var3 = var1.mutable();
         BlockPos.MutableBlockPos var4 = var1.mutable();

         for(Vec3i var6 : getRandomizedNonCornerNeighbourOffsets(var2)) {
            var4.setWithOffset(var1, (Vec3i)var6);
            BlockState var7 = var0.getBlockState(var4);
            if (var7.getBlock() instanceof SculkBehaviour && isMovementUnobstructed(var0, var1, var4)) {
               var3.set(var4);
               if (SculkVeinBlock.hasSubstrateAccess(var0, var7, var4)) {
                  break;
               }
            }
         }

         return var3.equals(var1) ? null : var3;
      }

      private static boolean isMovementUnobstructed(LevelAccessor var0, BlockPos var1, BlockPos var2) {
         if (var1.distManhattan(var2) == 1) {
            return true;
         } else {
            BlockPos var3 = var2.subtract(var1);
            Direction var4 = Direction.fromAxisAndDirection(Direction.Axis.X, var3.getX() < 0 ? Direction.AxisDirection.NEGATIVE : Direction.AxisDirection.POSITIVE);
            Direction var5 = Direction.fromAxisAndDirection(Direction.Axis.Y, var3.getY() < 0 ? Direction.AxisDirection.NEGATIVE : Direction.AxisDirection.POSITIVE);
            Direction var6 = Direction.fromAxisAndDirection(Direction.Axis.Z, var3.getZ() < 0 ? Direction.AxisDirection.NEGATIVE : Direction.AxisDirection.POSITIVE);
            if (var3.getX() == 0) {
               return isUnobstructed(var0, var1, var5) || isUnobstructed(var0, var1, var6);
            } else if (var3.getY() == 0) {
               return isUnobstructed(var0, var1, var4) || isUnobstructed(var0, var1, var6);
            } else {
               return isUnobstructed(var0, var1, var4) || isUnobstructed(var0, var1, var5);
            }
         }
      }

      private static boolean isUnobstructed(LevelAccessor var0, BlockPos var1, Direction var2) {
         BlockPos var3 = var1.relative(var2);
         return !var0.getBlockState(var3).isFaceSturdy(var0, var3, var2.getOpposite());
      }

      static {
         DIRECTION_SET = Direction.CODEC.listOf().xmap((var0) -> Sets.newEnumSet(var0, Direction.class), Lists::newArrayList);
         CODEC = RecordCodecBuilder.create((var0) -> var0.group(BlockPos.CODEC.fieldOf("pos").forGetter(ChargeCursor::getPos), Codec.intRange(0, 1000).fieldOf("charge").orElse(0).forGetter(ChargeCursor::getCharge), Codec.intRange(0, 1).fieldOf("decay_delay").orElse(1).forGetter(ChargeCursor::getDecayDelay), Codec.intRange(0, 2147483647).fieldOf("update_delay").orElse(0).forGetter((var0x) -> var0x.updateDelay), DIRECTION_SET.lenientOptionalFieldOf("facings").forGetter((var0x) -> Optional.ofNullable(var0x.getFacingData()))).apply(var0, ChargeCursor::new));
      }
   }
}

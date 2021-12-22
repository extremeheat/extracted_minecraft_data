package net.minecraft.core;

import it.unimi.dsi.fastutil.longs.LongConsumer;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;

public class SectionPos extends Vec3i {
   public static final int SECTION_BITS = 4;
   public static final int SECTION_SIZE = 16;
   public static final int SECTION_MASK = 15;
   public static final int SECTION_HALF_SIZE = 8;
   public static final int SECTION_MAX_INDEX = 15;
   private static final int PACKED_X_LENGTH = 22;
   private static final int PACKED_Y_LENGTH = 20;
   private static final int PACKED_Z_LENGTH = 22;
   private static final long PACKED_X_MASK = 4194303L;
   private static final long PACKED_Y_MASK = 1048575L;
   private static final long PACKED_Z_MASK = 4194303L;
   private static final int Y_OFFSET = 0;
   private static final int Z_OFFSET = 20;
   private static final int X_OFFSET = 42;
   private static final int RELATIVE_X_SHIFT = 8;
   private static final int RELATIVE_Y_SHIFT = 0;
   private static final int RELATIVE_Z_SHIFT = 4;

   SectionPos(int var1, int var2, int var3) {
      super(var1, var2, var3);
   }

   // $FF: renamed from: of (int, int, int) net.minecraft.core.SectionPos
   public static SectionPos method_70(int var0, int var1, int var2) {
      return new SectionPos(var0, var1, var2);
   }

   // $FF: renamed from: of (net.minecraft.core.BlockPos) net.minecraft.core.SectionPos
   public static SectionPos method_71(BlockPos var0) {
      return new SectionPos(blockToSectionCoord(var0.getX()), blockToSectionCoord(var0.getY()), blockToSectionCoord(var0.getZ()));
   }

   // $FF: renamed from: of (net.minecraft.world.level.ChunkPos, int) net.minecraft.core.SectionPos
   public static SectionPos method_72(ChunkPos var0, int var1) {
      return new SectionPos(var0.field_504, var1, var0.field_505);
   }

   // $FF: renamed from: of (net.minecraft.world.entity.Entity) net.minecraft.core.SectionPos
   public static SectionPos method_73(Entity var0) {
      return new SectionPos(blockToSectionCoord(var0.getBlockX()), blockToSectionCoord(var0.getBlockY()), blockToSectionCoord(var0.getBlockZ()));
   }

   // $FF: renamed from: of (long) net.minecraft.core.SectionPos
   public static SectionPos method_74(long var0) {
      return new SectionPos(method_75(var0), method_76(var0), method_77(var0));
   }

   public static SectionPos bottomOf(ChunkAccess var0) {
      return method_72(var0.getPos(), var0.getMinSection());
   }

   public static long offset(long var0, Direction var2) {
      return offset(var0, var2.getStepX(), var2.getStepY(), var2.getStepZ());
   }

   public static long offset(long var0, int var2, int var3, int var4) {
      return asLong(method_75(var0) + var2, method_76(var0) + var3, method_77(var0) + var4);
   }

   public static int posToSectionCoord(double var0) {
      return blockToSectionCoord(Mth.floor(var0));
   }

   public static int blockToSectionCoord(int var0) {
      return var0 >> 4;
   }

   public static int sectionRelative(int var0) {
      return var0 & 15;
   }

   public static short sectionRelativePos(BlockPos var0) {
      int var1 = sectionRelative(var0.getX());
      int var2 = sectionRelative(var0.getY());
      int var3 = sectionRelative(var0.getZ());
      return (short)(var1 << 8 | var3 << 4 | var2 << 0);
   }

   public static int sectionRelativeX(short var0) {
      return var0 >>> 8 & 15;
   }

   public static int sectionRelativeY(short var0) {
      return var0 >>> 0 & 15;
   }

   public static int sectionRelativeZ(short var0) {
      return var0 >>> 4 & 15;
   }

   public int relativeToBlockX(short var1) {
      return this.minBlockX() + sectionRelativeX(var1);
   }

   public int relativeToBlockY(short var1) {
      return this.minBlockY() + sectionRelativeY(var1);
   }

   public int relativeToBlockZ(short var1) {
      return this.minBlockZ() + sectionRelativeZ(var1);
   }

   public BlockPos relativeToBlockPos(short var1) {
      return new BlockPos(this.relativeToBlockX(var1), this.relativeToBlockY(var1), this.relativeToBlockZ(var1));
   }

   public static int sectionToBlockCoord(int var0) {
      return var0 << 4;
   }

   public static int sectionToBlockCoord(int var0, int var1) {
      return sectionToBlockCoord(var0) + var1;
   }

   // $FF: renamed from: x (long) int
   public static int method_75(long var0) {
      return (int)(var0 << 0 >> 42);
   }

   // $FF: renamed from: y (long) int
   public static int method_76(long var0) {
      return (int)(var0 << 44 >> 44);
   }

   // $FF: renamed from: z (long) int
   public static int method_77(long var0) {
      return (int)(var0 << 22 >> 42);
   }

   // $FF: renamed from: x () int
   public int method_78() {
      return this.getX();
   }

   // $FF: renamed from: y () int
   public int method_79() {
      return this.getY();
   }

   // $FF: renamed from: z () int
   public int method_80() {
      return this.getZ();
   }

   public int minBlockX() {
      return sectionToBlockCoord(this.method_78());
   }

   public int minBlockY() {
      return sectionToBlockCoord(this.method_79());
   }

   public int minBlockZ() {
      return sectionToBlockCoord(this.method_80());
   }

   public int maxBlockX() {
      return sectionToBlockCoord(this.method_78(), 15);
   }

   public int maxBlockY() {
      return sectionToBlockCoord(this.method_79(), 15);
   }

   public int maxBlockZ() {
      return sectionToBlockCoord(this.method_80(), 15);
   }

   public static long blockToSection(long var0) {
      return asLong(blockToSectionCoord(BlockPos.getX(var0)), blockToSectionCoord(BlockPos.getY(var0)), blockToSectionCoord(BlockPos.getZ(var0)));
   }

   public static long getZeroNode(long var0) {
      return var0 & -1048576L;
   }

   public BlockPos origin() {
      return new BlockPos(sectionToBlockCoord(this.method_78()), sectionToBlockCoord(this.method_79()), sectionToBlockCoord(this.method_80()));
   }

   public BlockPos center() {
      boolean var1 = true;
      return this.origin().offset(8, 8, 8);
   }

   public ChunkPos chunk() {
      return new ChunkPos(this.method_78(), this.method_80());
   }

   public static long asLong(BlockPos var0) {
      return asLong(blockToSectionCoord(var0.getX()), blockToSectionCoord(var0.getY()), blockToSectionCoord(var0.getZ()));
   }

   public static long asLong(int var0, int var1, int var2) {
      long var3 = 0L;
      var3 |= ((long)var0 & 4194303L) << 42;
      var3 |= ((long)var1 & 1048575L) << 0;
      var3 |= ((long)var2 & 4194303L) << 20;
      return var3;
   }

   public long asLong() {
      return asLong(this.method_78(), this.method_79(), this.method_80());
   }

   public SectionPos offset(int var1, int var2, int var3) {
      return var1 == 0 && var2 == 0 && var3 == 0 ? this : new SectionPos(this.method_78() + var1, this.method_79() + var2, this.method_80() + var3);
   }

   public Stream<BlockPos> blocksInside() {
      return BlockPos.betweenClosedStream(this.minBlockX(), this.minBlockY(), this.minBlockZ(), this.maxBlockX(), this.maxBlockY(), this.maxBlockZ());
   }

   public static Stream<SectionPos> cube(SectionPos var0, int var1) {
      int var2 = var0.method_78();
      int var3 = var0.method_79();
      int var4 = var0.method_80();
      return betweenClosedStream(var2 - var1, var3 - var1, var4 - var1, var2 + var1, var3 + var1, var4 + var1);
   }

   public static Stream<SectionPos> aroundChunk(ChunkPos var0, int var1, int var2, int var3) {
      int var4 = var0.field_504;
      int var5 = var0.field_505;
      return betweenClosedStream(var4 - var1, var2, var5 - var1, var4 + var1, var3 - 1, var5 + var1);
   }

   public static Stream<SectionPos> betweenClosedStream(final int var0, final int var1, final int var2, final int var3, final int var4, final int var5) {
      return StreamSupport.stream(new AbstractSpliterator<SectionPos>((long)((var3 - var0 + 1) * (var4 - var1 + 1) * (var5 - var2 + 1)), 64) {
         final Cursor3D cursor = new Cursor3D(var0, var1, var2, var3, var4, var5);

         public boolean tryAdvance(Consumer<? super SectionPos> var1x) {
            if (this.cursor.advance()) {
               var1x.accept(new SectionPos(this.cursor.nextX(), this.cursor.nextY(), this.cursor.nextZ()));
               return true;
            } else {
               return false;
            }
         }
      }, false);
   }

   public static void aroundAndAtBlockPos(BlockPos var0, LongConsumer var1) {
      aroundAndAtBlockPos(var0.getX(), var0.getY(), var0.getZ(), var1);
   }

   public static void aroundAndAtBlockPos(long var0, LongConsumer var2) {
      aroundAndAtBlockPos(BlockPos.getX(var0), BlockPos.getY(var0), BlockPos.getZ(var0), var2);
   }

   public static void aroundAndAtBlockPos(int var0, int var1, int var2, LongConsumer var3) {
      int var4 = blockToSectionCoord(var0 - 1);
      int var5 = blockToSectionCoord(var0 + 1);
      int var6 = blockToSectionCoord(var1 - 1);
      int var7 = blockToSectionCoord(var1 + 1);
      int var8 = blockToSectionCoord(var2 - 1);
      int var9 = blockToSectionCoord(var2 + 1);
      if (var4 == var5 && var6 == var7 && var8 == var9) {
         var3.accept(asLong(var4, var6, var8));
      } else {
         for(int var10 = var4; var10 <= var5; ++var10) {
            for(int var11 = var6; var11 <= var7; ++var11) {
               for(int var12 = var8; var12 <= var9; ++var12) {
                  var3.accept(asLong(var10, var11, var12));
               }
            }
         }
      }

   }

   // $FF: synthetic method
   public Vec3i offset(int var1, int var2, int var3) {
      return this.offset(var1, var2, var3);
   }
}

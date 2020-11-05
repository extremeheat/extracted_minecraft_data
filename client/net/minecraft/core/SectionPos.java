package net.minecraft.core;

import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;

public class SectionPos extends Vec3i {
   private SectionPos(int var1, int var2, int var3) {
      super(var1, var2, var3);
   }

   public static SectionPos of(int var0, int var1, int var2) {
      return new SectionPos(var0, var1, var2);
   }

   public static SectionPos of(BlockPos var0) {
      return new SectionPos(blockToSectionCoord(var0.getX()), blockToSectionCoord(var0.getY()), blockToSectionCoord(var0.getZ()));
   }

   public static SectionPos of(ChunkPos var0, int var1) {
      return new SectionPos(var0.x, var1, var0.z);
   }

   public static SectionPos of(Entity var0) {
      return new SectionPos(blockToSectionCoord(var0.getBlockX()), blockToSectionCoord(var0.getBlockY()), blockToSectionCoord(var0.getBlockZ()));
   }

   public static SectionPos of(long var0) {
      return new SectionPos(x(var0), y(var0), z(var0));
   }

   public static long offset(long var0, Direction var2) {
      return offset(var0, var2.getStepX(), var2.getStepY(), var2.getStepZ());
   }

   public static long offset(long var0, int var2, int var3, int var4) {
      return asLong(x(var0) + var2, y(var0) + var3, z(var0) + var4);
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

   public static int x(long var0) {
      return (int)(var0 << 0 >> 42);
   }

   public static int y(long var0) {
      return (int)(var0 << 44 >> 44);
   }

   public static int z(long var0) {
      return (int)(var0 << 22 >> 42);
   }

   public int x() {
      return this.getX();
   }

   public int y() {
      return this.getY();
   }

   public int z() {
      return this.getZ();
   }

   public int minBlockX() {
      return sectionToBlockCoord(this.x());
   }

   public int minBlockY() {
      return sectionToBlockCoord(this.y());
   }

   public int minBlockZ() {
      return sectionToBlockCoord(this.z());
   }

   public int maxBlockX() {
      return sectionToBlockCoord(this.x(), 15);
   }

   public int maxBlockY() {
      return sectionToBlockCoord(this.y(), 15);
   }

   public int maxBlockZ() {
      return sectionToBlockCoord(this.z(), 15);
   }

   public static long blockToSection(long var0) {
      return asLong(blockToSectionCoord(BlockPos.getX(var0)), blockToSectionCoord(BlockPos.getY(var0)), blockToSectionCoord(BlockPos.getZ(var0)));
   }

   public static long getZeroNode(long var0) {
      return var0 & -1048576L;
   }

   public BlockPos origin() {
      return new BlockPos(sectionToBlockCoord(this.x()), sectionToBlockCoord(this.y()), sectionToBlockCoord(this.z()));
   }

   public BlockPos center() {
      boolean var1 = true;
      return this.origin().offset(8, 8, 8);
   }

   public ChunkPos chunk() {
      return new ChunkPos(this.x(), this.z());
   }

   public static long asLong(int var0, int var1, int var2) {
      long var3 = 0L;
      var3 |= ((long)var0 & 4194303L) << 42;
      var3 |= ((long)var1 & 1048575L) << 0;
      var3 |= ((long)var2 & 4194303L) << 20;
      return var3;
   }

   public long asLong() {
      return asLong(this.x(), this.y(), this.z());
   }

   public Stream<BlockPos> blocksInside() {
      return BlockPos.betweenClosedStream(this.minBlockX(), this.minBlockY(), this.minBlockZ(), this.maxBlockX(), this.maxBlockY(), this.maxBlockZ());
   }

   public static Stream<SectionPos> cube(SectionPos var0, int var1) {
      int var2 = var0.x();
      int var3 = var0.y();
      int var4 = var0.z();
      return betweenClosedStream(var2 - var1, var3 - var1, var4 - var1, var2 + var1, var3 + var1, var4 + var1);
   }

   public static Stream<SectionPos> aroundChunk(ChunkPos var0, int var1, int var2, int var3) {
      int var4 = var0.x;
      int var5 = var0.z;
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

   // $FF: synthetic method
   SectionPos(int var1, int var2, int var3, Object var4) {
      this(var1, var2, var3);
   }
}

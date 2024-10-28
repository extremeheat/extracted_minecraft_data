package net.minecraft.world.level.levelgen;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.util.BitStorage;
import net.minecraft.util.Mth;
import net.minecraft.util.SimpleBitStorage;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.slf4j.Logger;

public class Heightmap {
   private static final Logger LOGGER = LogUtils.getLogger();
   static final Predicate<BlockState> NOT_AIR = (var0) -> {
      return !var0.isAir();
   };
   static final Predicate<BlockState> MATERIAL_MOTION_BLOCKING = BlockBehaviour.BlockStateBase::blocksMotion;
   private final BitStorage data;
   private final Predicate<BlockState> isOpaque;
   private final ChunkAccess chunk;

   public Heightmap(ChunkAccess var1, Types var2) {
      super();
      this.isOpaque = var2.isOpaque();
      this.chunk = var1;
      int var3 = Mth.ceillog2(var1.getHeight() + 1);
      this.data = new SimpleBitStorage(var3, 256);
   }

   public static void primeHeightmaps(ChunkAccess var0, Set<Types> var1) {
      if (!var1.isEmpty()) {
         int var2 = var1.size();
         ObjectArrayList var3 = new ObjectArrayList(var2);
         ObjectListIterator var4 = var3.iterator();
         int var5 = var0.getHighestSectionPosition() + 16;
         BlockPos.MutableBlockPos var6 = new BlockPos.MutableBlockPos();

         for(int var7 = 0; var7 < 16; ++var7) {
            for(int var8 = 0; var8 < 16; ++var8) {
               Iterator var9 = var1.iterator();

               while(var9.hasNext()) {
                  Types var10 = (Types)var9.next();
                  var3.add(var0.getOrCreateHeightmapUnprimed(var10));
               }

               for(int var12 = var5 - 1; var12 >= var0.getMinY(); --var12) {
                  var6.set(var7, var12, var8);
                  BlockState var13 = var0.getBlockState(var6);
                  if (!var13.is(Blocks.AIR)) {
                     while(var4.hasNext()) {
                        Heightmap var11 = (Heightmap)var4.next();
                        if (var11.isOpaque.test(var13)) {
                           var11.setHeight(var7, var8, var12 + 1);
                           var4.remove();
                        }
                     }

                     if (var3.isEmpty()) {
                        break;
                     }

                     var4.back(var2);
                  }
               }
            }
         }

      }
   }

   public boolean update(int var1, int var2, int var3, BlockState var4) {
      int var5 = this.getFirstAvailable(var1, var3);
      if (var2 <= var5 - 2) {
         return false;
      } else {
         if (this.isOpaque.test(var4)) {
            if (var2 >= var5) {
               this.setHeight(var1, var3, var2 + 1);
               return true;
            }
         } else if (var5 - 1 == var2) {
            BlockPos.MutableBlockPos var6 = new BlockPos.MutableBlockPos();

            for(int var7 = var2 - 1; var7 >= this.chunk.getMinY(); --var7) {
               var6.set(var1, var7, var3);
               if (this.isOpaque.test(this.chunk.getBlockState(var6))) {
                  this.setHeight(var1, var3, var7 + 1);
                  return true;
               }
            }

            this.setHeight(var1, var3, this.chunk.getMinY());
            return true;
         }

         return false;
      }
   }

   public int getFirstAvailable(int var1, int var2) {
      return this.getFirstAvailable(getIndex(var1, var2));
   }

   public int getHighestTaken(int var1, int var2) {
      return this.getFirstAvailable(getIndex(var1, var2)) - 1;
   }

   private int getFirstAvailable(int var1) {
      return this.data.get(var1) + this.chunk.getMinY();
   }

   private void setHeight(int var1, int var2, int var3) {
      this.data.set(getIndex(var1, var2), var3 - this.chunk.getMinY());
   }

   public void setRawData(ChunkAccess var1, Types var2, long[] var3) {
      long[] var4 = this.data.getRaw();
      if (var4.length == var3.length) {
         System.arraycopy(var3, 0, var4, 0, var3.length);
      } else {
         Logger var10000 = LOGGER;
         String var10001 = String.valueOf(var1.getPos());
         var10000.warn("Ignoring heightmap data for chunk " + var10001 + ", size does not match; expected: " + var4.length + ", got: " + var3.length);
         primeHeightmaps(var1, EnumSet.of(var2));
      }
   }

   public long[] getRawData() {
      return this.data.getRaw();
   }

   private static int getIndex(int var0, int var1) {
      return var0 + var1 * 16;
   }

   public static enum Types implements StringRepresentable {
      WORLD_SURFACE_WG("WORLD_SURFACE_WG", Heightmap.Usage.WORLDGEN, Heightmap.NOT_AIR),
      WORLD_SURFACE("WORLD_SURFACE", Heightmap.Usage.CLIENT, Heightmap.NOT_AIR),
      OCEAN_FLOOR_WG("OCEAN_FLOOR_WG", Heightmap.Usage.WORLDGEN, Heightmap.MATERIAL_MOTION_BLOCKING),
      OCEAN_FLOOR("OCEAN_FLOOR", Heightmap.Usage.LIVE_WORLD, Heightmap.MATERIAL_MOTION_BLOCKING),
      MOTION_BLOCKING("MOTION_BLOCKING", Heightmap.Usage.CLIENT, (var0) -> {
         return var0.blocksMotion() || !var0.getFluidState().isEmpty();
      }),
      MOTION_BLOCKING_NO_LEAVES("MOTION_BLOCKING_NO_LEAVES", Heightmap.Usage.LIVE_WORLD, (var0) -> {
         return (var0.blocksMotion() || !var0.getFluidState().isEmpty()) && !(var0.getBlock() instanceof LeavesBlock);
      });

      public static final Codec<Types> CODEC = StringRepresentable.fromEnum(Types::values);
      private final String serializationKey;
      private final Usage usage;
      private final Predicate<BlockState> isOpaque;

      private Types(final String var3, final Usage var4, final Predicate var5) {
         this.serializationKey = var3;
         this.usage = var4;
         this.isOpaque = var5;
      }

      public String getSerializationKey() {
         return this.serializationKey;
      }

      public boolean sendToClient() {
         return this.usage == Heightmap.Usage.CLIENT;
      }

      public boolean keepAfterWorldgen() {
         return this.usage != Heightmap.Usage.WORLDGEN;
      }

      public Predicate<BlockState> isOpaque() {
         return this.isOpaque;
      }

      public String getSerializedName() {
         return this.serializationKey;
      }

      // $FF: synthetic method
      private static Types[] $values() {
         return new Types[]{WORLD_SURFACE_WG, WORLD_SURFACE, OCEAN_FLOOR_WG, OCEAN_FLOOR, MOTION_BLOCKING, MOTION_BLOCKING_NO_LEAVES};
      }
   }

   public static enum Usage {
      WORLDGEN,
      LIVE_WORLD,
      CLIENT;

      private Usage() {
      }

      // $FF: synthetic method
      private static Usage[] $values() {
         return new Usage[]{WORLDGEN, LIVE_WORLD, CLIENT};
      }
   }
}

package net.minecraft.block.state.pattern;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import java.util.Iterator;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3i;
import net.minecraft.world.World;

public class BlockPattern {
   private final Predicate<BlockWorldState>[][][] field_177689_a;
   private final int field_177687_b;
   private final int field_177688_c;
   private final int field_177686_d;

   public BlockPattern(Predicate<BlockWorldState>[][][] var1) {
      super();
      this.field_177689_a = var1;
      this.field_177687_b = var1.length;
      if (this.field_177687_b > 0) {
         this.field_177688_c = var1[0].length;
         if (this.field_177688_c > 0) {
            this.field_177686_d = var1[0][0].length;
         } else {
            this.field_177686_d = 0;
         }
      } else {
         this.field_177688_c = 0;
         this.field_177686_d = 0;
      }

   }

   public int func_177685_b() {
      return this.field_177688_c;
   }

   public int func_177684_c() {
      return this.field_177686_d;
   }

   private BlockPattern.PatternHelper func_177682_a(BlockPos var1, EnumFacing var2, EnumFacing var3, LoadingCache<BlockPos, BlockWorldState> var4) {
      for(int var5 = 0; var5 < this.field_177686_d; ++var5) {
         for(int var6 = 0; var6 < this.field_177688_c; ++var6) {
            for(int var7 = 0; var7 < this.field_177687_b; ++var7) {
               if (!this.field_177689_a[var7][var6][var5].apply(var4.getUnchecked(func_177683_a(var1, var2, var3, var5, var6, var7)))) {
                  return null;
               }
            }
         }
      }

      return new BlockPattern.PatternHelper(var1, var2, var3, var4, this.field_177686_d, this.field_177688_c, this.field_177687_b);
   }

   public BlockPattern.PatternHelper func_177681_a(World var1, BlockPos var2) {
      LoadingCache var3 = func_181627_a(var1, false);
      int var4 = Math.max(Math.max(this.field_177686_d, this.field_177688_c), this.field_177687_b);
      Iterator var5 = BlockPos.func_177980_a(var2, var2.func_177982_a(var4 - 1, var4 - 1, var4 - 1)).iterator();

      while(var5.hasNext()) {
         BlockPos var6 = (BlockPos)var5.next();
         EnumFacing[] var7 = EnumFacing.values();
         int var8 = var7.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            EnumFacing var10 = var7[var9];
            EnumFacing[] var11 = EnumFacing.values();
            int var12 = var11.length;

            for(int var13 = 0; var13 < var12; ++var13) {
               EnumFacing var14 = var11[var13];
               if (var14 != var10 && var14 != var10.func_176734_d()) {
                  BlockPattern.PatternHelper var15 = this.func_177682_a(var6, var10, var14, var3);
                  if (var15 != null) {
                     return var15;
                  }
               }
            }
         }
      }

      return null;
   }

   public static LoadingCache<BlockPos, BlockWorldState> func_181627_a(World var0, boolean var1) {
      return CacheBuilder.newBuilder().build(new BlockPattern.CacheLoader(var0, var1));
   }

   protected static BlockPos func_177683_a(BlockPos var0, EnumFacing var1, EnumFacing var2, int var3, int var4, int var5) {
      if (var1 != var2 && var1 != var2.func_176734_d()) {
         Vec3i var6 = new Vec3i(var1.func_82601_c(), var1.func_96559_d(), var1.func_82599_e());
         Vec3i var7 = new Vec3i(var2.func_82601_c(), var2.func_96559_d(), var2.func_82599_e());
         Vec3i var8 = var6.func_177955_d(var7);
         return var0.func_177982_a(var7.func_177958_n() * -var4 + var8.func_177958_n() * var3 + var6.func_177958_n() * var5, var7.func_177956_o() * -var4 + var8.func_177956_o() * var3 + var6.func_177956_o() * var5, var7.func_177952_p() * -var4 + var8.func_177952_p() * var3 + var6.func_177952_p() * var5);
      } else {
         throw new IllegalArgumentException("Invalid forwards & up combination");
      }
   }

   public static class PatternHelper {
      private final BlockPos field_177674_a;
      private final EnumFacing field_177672_b;
      private final EnumFacing field_177673_c;
      private final LoadingCache<BlockPos, BlockWorldState> field_177671_d;
      private final int field_181120_e;
      private final int field_181121_f;
      private final int field_181122_g;

      public PatternHelper(BlockPos var1, EnumFacing var2, EnumFacing var3, LoadingCache<BlockPos, BlockWorldState> var4, int var5, int var6, int var7) {
         super();
         this.field_177674_a = var1;
         this.field_177672_b = var2;
         this.field_177673_c = var3;
         this.field_177671_d = var4;
         this.field_181120_e = var5;
         this.field_181121_f = var6;
         this.field_181122_g = var7;
      }

      public BlockPos func_181117_a() {
         return this.field_177674_a;
      }

      public EnumFacing func_177669_b() {
         return this.field_177672_b;
      }

      public EnumFacing func_177668_c() {
         return this.field_177673_c;
      }

      public int func_181118_d() {
         return this.field_181120_e;
      }

      public int func_181119_e() {
         return this.field_181121_f;
      }

      public BlockWorldState func_177670_a(int var1, int var2, int var3) {
         return (BlockWorldState)this.field_177671_d.getUnchecked(BlockPattern.func_177683_a(this.field_177674_a, this.func_177669_b(), this.func_177668_c(), var1, var2, var3));
      }

      public String toString() {
         return Objects.toStringHelper(this).add("up", this.field_177673_c).add("forwards", this.field_177672_b).add("frontTopLeft", this.field_177674_a).toString();
      }
   }

   static class CacheLoader extends com.google.common.cache.CacheLoader<BlockPos, BlockWorldState> {
      private final World field_177680_a;
      private final boolean field_181626_b;

      public CacheLoader(World var1, boolean var2) {
         super();
         this.field_177680_a = var1;
         this.field_181626_b = var2;
      }

      public BlockWorldState load(BlockPos var1) throws Exception {
         return new BlockWorldState(this.field_177680_a, var1, this.field_181626_b);
      }

      // $FF: synthetic method
      public Object load(Object var1) throws Exception {
         return this.load((BlockPos)var1);
      }
   }
}

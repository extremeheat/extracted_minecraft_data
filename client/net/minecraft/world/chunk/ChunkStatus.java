package net.minecraft.world.chunk;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.util.ITaskType;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.tasks.BaseChunkTask;
import net.minecraft.world.gen.tasks.CarveChunkTask;
import net.minecraft.world.gen.tasks.ChunkTask;
import net.minecraft.world.gen.tasks.DecorateChunkTask;
import net.minecraft.world.gen.tasks.DummyChunkTask;
import net.minecraft.world.gen.tasks.FinializeChunkTask;
import net.minecraft.world.gen.tasks.LightChunkTask;
import net.minecraft.world.gen.tasks.LiquidCarveChunkTask;
import net.minecraft.world.gen.tasks.SpawnMobsTask;

public enum ChunkStatus implements ITaskType<ChunkPos, ChunkStatus> {
   EMPTY("empty", (ChunkTask)null, -1, false, ChunkStatus.Type.PROTOCHUNK),
   BASE("base", new BaseChunkTask(), 0, false, ChunkStatus.Type.PROTOCHUNK),
   CARVED("carved", new CarveChunkTask(), 0, false, ChunkStatus.Type.PROTOCHUNK),
   LIQUID_CARVED("liquid_carved", new LiquidCarveChunkTask(), 1, false, ChunkStatus.Type.PROTOCHUNK),
   DECORATED("decorated", new DecorateChunkTask(), 1, true, ChunkStatus.Type.PROTOCHUNK) {
      public void func_201492_a_(ChunkPos var1, BiConsumer<ChunkPos, ChunkStatus> var2) {
         int var3 = var1.field_77276_a;
         int var4 = var1.field_77275_b;
         ChunkStatus var5 = this.func_201497_a_();
         boolean var6 = true;

         int var7;
         int var8;
         ChunkPos var9;
         for(var7 = var3 - 8; var7 <= var3 + 8; ++var7) {
            if (var7 < var3 - 1 || var7 > var3 + 1) {
               for(var8 = var4 - 8; var8 <= var4 + 8; ++var8) {
                  if (var8 < var4 - 1 || var8 > var4 + 1) {
                     var9 = new ChunkPos(var7, var8);
                     var2.accept(var9, EMPTY);
                  }
               }
            }
         }

         for(var7 = var3 - 1; var7 <= var3 + 1; ++var7) {
            for(var8 = var4 - 1; var8 <= var4 + 1; ++var8) {
               var9 = new ChunkPos(var7, var8);
               var2.accept(var9, var5);
            }
         }

      }

      // $FF: synthetic method
      @Nullable
      public ITaskType func_201497_a_() {
         return super.func_201497_a_();
      }
   },
   LIGHTED("lighted", new LightChunkTask(), 1, true, ChunkStatus.Type.PROTOCHUNK),
   MOBS_SPAWNED("mobs_spawned", new SpawnMobsTask(), 0, true, ChunkStatus.Type.PROTOCHUNK),
   FINALIZED("finalized", new FinializeChunkTask(), 0, true, ChunkStatus.Type.PROTOCHUNK),
   FULLCHUNK("fullchunk", new DummyChunkTask(), 0, true, ChunkStatus.Type.LEVELCHUNK),
   POSTPROCESSED("postprocessed", new DummyChunkTask(), 0, true, ChunkStatus.Type.LEVELCHUNK);

   private static final Map<String, ChunkStatus> field_202131_k = (Map)Util.func_200696_a(Maps.newHashMap(), (var0) -> {
      ChunkStatus[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         ChunkStatus var4 = var1[var3];
         var0.put(var4.func_202125_b(), var4);
      }

   });
   private final String field_202130_j;
   @Nullable
   private final ChunkTask field_202132_l;
   private final int field_202133_m;
   private final ChunkStatus.Type field_202134_n;
   private final boolean field_207795_p;

   private ChunkStatus(String var3, ChunkTask var4, int var5, @Nullable boolean var6, ChunkStatus.Type var7) {
      this.field_202130_j = var3;
      this.field_202132_l = var4;
      this.field_202133_m = var5;
      this.field_202134_n = var7;
      this.field_207795_p = var6;
   }

   public String func_202125_b() {
      return this.field_202130_j;
   }

   public ChunkPrimer func_202126_a(World var1, IChunkGenerator<?> var2, Map<ChunkPos, ChunkPrimer> var3, int var4, int var5) {
      return this.field_202132_l.func_202839_a(this, var1, var2, var3, var4, var5);
   }

   public void func_201492_a_(ChunkPos var1, BiConsumer<ChunkPos, ChunkStatus> var2) {
      int var3 = var1.field_77276_a;
      int var4 = var1.field_77275_b;
      ChunkStatus var5 = this.func_201497_a_();

      for(int var6 = var3 - this.field_202133_m; var6 <= var3 + this.field_202133_m; ++var6) {
         for(int var7 = var4 - this.field_202133_m; var7 <= var4 + this.field_202133_m; ++var7) {
            var2.accept(new ChunkPos(var6, var7), var5);
         }
      }

   }

   public int func_202128_c() {
      return this.field_202133_m;
   }

   public ChunkStatus.Type func_202129_d() {
      return this.field_202134_n;
   }

   @Nullable
   public static ChunkStatus func_202127_a(String var0) {
      return (ChunkStatus)field_202131_k.get(var0);
   }

   @Nullable
   public ChunkStatus func_201497_a_() {
      return this.ordinal() == 0 ? null : values()[this.ordinal() - 1];
   }

   public boolean func_207794_f() {
      return this.field_207795_p;
   }

   public boolean func_209003_a(ChunkStatus var1) {
      return this.ordinal() >= var1.ordinal();
   }

   // $FF: synthetic method
   @Nullable
   public ITaskType func_201497_a_() {
      return this.func_201497_a_();
   }

   // $FF: synthetic method
   ChunkStatus(String var3, ChunkTask var4, int var5, boolean var6, ChunkStatus.Type var7, Object var8) {
      this(var3, var4, var5, var6, var7);
   }

   public static enum Type {
      PROTOCHUNK,
      LEVELCHUNK;

      private Type() {
      }
   }
}

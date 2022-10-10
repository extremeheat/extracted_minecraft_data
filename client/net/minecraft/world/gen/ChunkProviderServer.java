package net.minecraft.world.gen;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.TaskManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.tasks.ProtoChunkScheduler;
import net.minecraft.world.storage.SessionLockException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkProviderServer implements IChunkProvider {
   private static final Logger field_147417_b = LogManager.getLogger();
   private final LongSet field_73248_b = new LongOpenHashSet();
   private final IChunkGenerator<?> field_186029_c;
   private final IChunkLoader field_73247_e;
   private final Long2ObjectMap<Chunk> field_73244_f = Long2ObjectMaps.synchronize(new ChunkCacheNeighborNotification(8192));
   private Chunk field_212472_f;
   private final ProtoChunkScheduler field_201723_f;
   private final TaskManager<ChunkPos, ChunkStatus, ChunkPrimer> field_201724_g;
   private final WorldServer field_73251_h;
   private final IThreadListener field_212473_j;

   public ChunkProviderServer(WorldServer var1, IChunkLoader var2, IChunkGenerator<?> var3, IThreadListener var4) {
      super();
      this.field_73251_h = var1;
      this.field_73247_e = var2;
      this.field_186029_c = var3;
      this.field_212473_j = var4;
      this.field_201723_f = new ProtoChunkScheduler(2, var1, var3, var2, var4);
      this.field_201724_g = new TaskManager(this.field_201723_f);
   }

   public Collection<Chunk> func_189548_a() {
      return this.field_73244_f.values();
   }

   public void func_189549_a(Chunk var1) {
      if (this.field_73251_h.field_73011_w.func_186056_c(var1.field_76635_g, var1.field_76647_h)) {
         this.field_73248_b.add(ChunkPos.func_77272_a(var1.field_76635_g, var1.field_76647_h));
      }

   }

   public void func_73240_a() {
      ObjectIterator var1 = this.field_73244_f.values().iterator();

      while(var1.hasNext()) {
         Chunk var2 = (Chunk)var1.next();
         this.func_189549_a(var2);
      }

   }

   public void func_212469_a(int var1, int var2) {
      this.field_73248_b.remove(ChunkPos.func_77272_a(var1, var2));
   }

   @Nullable
   public Chunk func_186025_d(int var1, int var2, boolean var3, boolean var4) {
      Chunk var5;
      synchronized(this.field_73247_e) {
         if (this.field_212472_f != null && this.field_212472_f.func_76632_l().field_77276_a == var1 && this.field_212472_f.func_76632_l().field_77275_b == var2) {
            return this.field_212472_f;
         }

         long var7 = ChunkPos.func_77272_a(var1, var2);
         var5 = (Chunk)this.field_73244_f.get(var7);
         if (var5 != null) {
            this.field_212472_f = var5;
            return var5;
         }

         if (var3) {
            try {
               var5 = this.field_73247_e.func_199813_a(this.field_73251_h, var1, var2, (var3x) -> {
                  var3x.func_177432_b(this.field_73251_h.func_82737_E());
                  this.field_73244_f.put(ChunkPos.func_77272_a(var1, var2), var3x);
               });
            } catch (Exception var12) {
               field_147417_b.error("Couldn't load chunk", var12);
            }
         }
      }

      if (var5 != null) {
         this.field_212473_j.func_152344_a(var5::func_76631_c);
         return var5;
      } else if (var4) {
         try {
            this.field_201724_g.func_202928_b();
            this.field_201724_g.func_202926_a(new ChunkPos(var1, var2));
            CompletableFuture var6 = this.field_201724_g.func_202927_c();
            return (Chunk)var6.thenApply(this::func_201719_a).join();
         } catch (RuntimeException var11) {
            throw this.func_201722_a(var1, var2, var11);
         }
      } else {
         return null;
      }
   }

   public IChunk func_201713_d(int var1, int var2, boolean var3) {
      Chunk var4 = this.func_186025_d(var1, var2, true, false);
      return (IChunk)(var4 != null ? var4 : (IChunk)this.field_201723_f.func_212537_b(new ChunkPos(var1, var2), var3));
   }

   public CompletableFuture<ChunkPrimer> func_201720_a(Iterable<ChunkPos> var1, Consumer<Chunk> var2) {
      this.field_201724_g.func_202928_b();
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         ChunkPos var4 = (ChunkPos)var3.next();
         Chunk var5 = this.func_186025_d(var4.field_77276_a, var4.field_77275_b, true, false);
         if (var5 != null) {
            var2.accept(var5);
         } else {
            this.field_201724_g.func_202926_a(var4).thenApply(this::func_201719_a).thenAccept(var2);
         }
      }

      return this.field_201724_g.func_202927_c();
   }

   private ReportedException func_201722_a(int var1, int var2, Throwable var3) {
      CrashReport var4 = CrashReport.func_85055_a(var3, "Exception generating new chunk");
      CrashReportCategory var5 = var4.func_85058_a("Chunk to be generated");
      var5.func_71507_a("Location", String.format("%d,%d", var1, var2));
      var5.func_71507_a("Position hash", ChunkPos.func_77272_a(var1, var2));
      var5.func_71507_a("Generator", this.field_186029_c);
      return new ReportedException(var4);
   }

   private Chunk func_201719_a(IChunk var1) {
      ChunkPos var2 = var1.func_76632_l();
      int var3 = var2.field_77276_a;
      int var4 = var2.field_77275_b;
      long var5 = ChunkPos.func_77272_a(var3, var4);
      Chunk var7;
      synchronized(this.field_73244_f) {
         Chunk var9 = (Chunk)this.field_73244_f.get(var5);
         if (var9 != null) {
            return var9;
         }

         if (var1 instanceof Chunk) {
            var7 = (Chunk)var1;
         } else {
            if (!(var1 instanceof ChunkPrimer)) {
               throw new IllegalStateException();
            }

            var7 = new Chunk(this.field_73251_h, (ChunkPrimer)var1, var3, var4);
         }

         this.field_73244_f.put(var5, var7);
         this.field_212472_f = var7;
      }

      this.field_212473_j.func_152344_a(var7::func_76631_c);
      return var7;
   }

   private void func_73242_b(IChunk var1) {
      try {
         var1.func_177432_b(this.field_73251_h.func_82737_E());
         this.field_73247_e.func_75816_a(this.field_73251_h, var1);
      } catch (IOException var3) {
         field_147417_b.error("Couldn't save chunk", var3);
      } catch (SessionLockException var4) {
         field_147417_b.error("Couldn't save chunk; already in use by another instance of Minecraft?", var4);
      }

   }

   public boolean func_186027_a(boolean var1) {
      int var2 = 0;
      this.field_201723_f.func_208484_a(() -> {
         return true;
      });
      synchronized(this.field_73247_e) {
         ObjectIterator var4 = this.field_73244_f.values().iterator();

         while(var4.hasNext()) {
            Chunk var5 = (Chunk)var4.next();
            if (var5.func_76601_a(var1)) {
               this.func_73242_b(var5);
               var5.func_177427_f(false);
               ++var2;
               if (var2 == 24 && !var1) {
                  return false;
               }
            }
         }

         return true;
      }
   }

   public void close() {
      try {
         this.field_201724_g.func_202925_a();
      } catch (InterruptedException var2) {
         field_147417_b.error("Couldn't stop taskManager", var2);
      }

   }

   public void func_104112_b() {
      synchronized(this.field_73247_e) {
         this.field_73247_e.func_75818_b();
      }
   }

   public boolean func_73156_b(BooleanSupplier var1) {
      if (!this.field_73251_h.field_73058_d) {
         if (!this.field_73248_b.isEmpty()) {
            LongIterator var2 = this.field_73248_b.iterator();

            for(int var3 = 0; var2.hasNext() && (var1.getAsBoolean() || var3 < 200 || this.field_73248_b.size() > 2000); var2.remove()) {
               Long var4 = (Long)var2.next();
               synchronized(this.field_73247_e) {
                  Chunk var6 = (Chunk)this.field_73244_f.get(var4);
                  if (var6 != null) {
                     var6.func_76623_d();
                     this.func_73242_b(var6);
                     this.field_73244_f.remove(var4);
                     this.field_212472_f = null;
                     ++var3;
                  }
               }
            }
         }

         this.field_201723_f.func_208484_a(var1);
      }

      return false;
   }

   public boolean func_73157_c() {
      return !this.field_73251_h.field_73058_d;
   }

   public String func_73148_d() {
      return "ServerChunkCache: " + this.field_73244_f.size() + " Drop: " + this.field_73248_b.size();
   }

   public List<Biome.SpawnListEntry> func_177458_a(EnumCreatureType var1, BlockPos var2) {
      return this.field_186029_c.func_177458_a(var1, var2);
   }

   public int func_203082_a(World var1, boolean var2, boolean var3) {
      return this.field_186029_c.func_203222_a(var1, var2, var3);
   }

   @Nullable
   public BlockPos func_211268_a(World var1, String var2, BlockPos var3, int var4, boolean var5) {
      return this.field_186029_c.func_211403_a(var1, var2, var3, var4, var5);
   }

   public IChunkGenerator<?> func_201711_g() {
      return this.field_186029_c;
   }

   public int func_73152_e() {
      return this.field_73244_f.size();
   }

   public boolean func_73149_a(int var1, int var2) {
      return this.field_73244_f.containsKey(ChunkPos.func_77272_a(var1, var2));
   }
}

package net.minecraft.util;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMaps;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenCustomHashMap;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map.Entry;
import java.util.concurrent.ThreadFactory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraft.world.storage.WorldSavedDataStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldOptimizer {
   private static final Logger field_212219_a = LogManager.getLogger();
   private static final ThreadFactory field_212220_b = (new ThreadFactoryBuilder()).setDaemon(true).build();
   private final String field_212237_s;
   private final ISaveHandler field_212221_c;
   private final WorldSavedDataStorage field_212222_d;
   private final Thread field_212223_e;
   private volatile boolean field_212224_f = true;
   private volatile boolean field_212225_g = false;
   private volatile float field_212232_n;
   private volatile int field_212233_o;
   private volatile int field_212234_p = 0;
   private volatile int field_212235_q = 0;
   private final Object2FloatMap<DimensionType> field_212544_m = Object2FloatMaps.synchronize(new Object2FloatOpenCustomHashMap(Util.func_212443_g()));
   private volatile ITextComponent field_212236_r = new TextComponentTranslation("optimizeWorld.stage.counting", new Object[0]);

   public WorldOptimizer(String var1, ISaveFormat var2, WorldInfo var3) {
      super();
      this.field_212237_s = var3.func_76065_j();
      this.field_212221_c = var2.func_197715_a(var1, (MinecraftServer)null);
      this.field_212221_c.func_75761_a(var3);
      this.field_212222_d = new WorldSavedDataStorage(this.field_212221_c);
      this.field_212223_e = field_212220_b.newThread(this::func_212216_o);
      this.field_212223_e.setUncaughtExceptionHandler(this::func_212206_a);
      this.field_212223_e.start();
   }

   private void func_212206_a(Thread var1, Throwable var2) {
      field_212219_a.error("Error upgrading world", var2);
      this.field_212224_f = false;
      this.field_212236_r = new TextComponentTranslation("optimizeWorld.stage.failed", new Object[0]);
   }

   public void func_212217_a() {
      this.field_212224_f = false;

      try {
         this.field_212223_e.join();
      } catch (InterruptedException var2) {
      }

   }

   private void func_212216_o() {
      File var1 = this.field_212221_c.func_75765_b();
      WorldChunkEnumerator var2 = new WorldChunkEnumerator(var1);
      Builder var3 = ImmutableMap.builder();
      Iterator var4 = DimensionType.func_212681_b().iterator();

      while(var4.hasNext()) {
         DimensionType var5 = (DimensionType)var4.next();
         var3.put(var5, new AnvilChunkLoader(var5.func_212679_a(var1), this.field_212221_c.func_197718_i()));
      }

      ImmutableMap var16 = var3.build();
      long var17 = Util.func_211177_b();
      this.field_212233_o = 0;
      Builder var7 = ImmutableMap.builder();

      List var10;
      for(Iterator var8 = DimensionType.func_212681_b().iterator(); var8.hasNext(); this.field_212233_o += var10.size()) {
         DimensionType var9 = (DimensionType)var8.next();
         var10 = var2.func_212541_a(var9);
         var7.put(var9, var10.listIterator());
      }

      ImmutableMap var18 = var7.build();
      float var19 = (float)this.field_212233_o;
      this.field_212236_r = new TextComponentTranslation("optimizeWorld.stage.structures", new Object[0]);
      Iterator var20 = var16.entrySet().iterator();

      while(var20.hasNext()) {
         Entry var11 = (Entry)var20.next();
         ((AnvilChunkLoader)var11.getValue()).func_212429_a((DimensionType)var11.getKey(), this.field_212222_d);
      }

      this.field_212222_d.func_75744_a();
      this.field_212236_r = new TextComponentTranslation("optimizeWorld.stage.upgrading", new Object[0]);
      if (var19 <= 0.0F) {
         var20 = DimensionType.func_212681_b().iterator();

         while(var20.hasNext()) {
            DimensionType var22 = (DimensionType)var20.next();
            this.field_212544_m.put(var22, 1.0F / (float)var16.size());
         }
      }

      while(this.field_212224_f) {
         boolean var21 = false;
         float var23 = 0.0F;
         Iterator var12 = DimensionType.func_212681_b().iterator();

         while(var12.hasNext()) {
            DimensionType var13 = (DimensionType)var12.next();
            ListIterator var14 = (ListIterator)var18.get(var13);
            var21 |= this.func_212542_a((AnvilChunkLoader)var16.get(var13), var14, var13);
            if (var19 > 0.0F) {
               float var15 = (float)var14.nextIndex() / var19;
               this.field_212544_m.put(var13, var15);
               var23 += var15;
            }
         }

         this.field_212232_n = var23;
         if (!var21) {
            this.field_212224_f = false;
         }
      }

      this.field_212236_r = new TextComponentTranslation("optimizeWorld.stage.finished", new Object[0]);
      var17 = Util.func_211177_b() - var17;
      field_212219_a.info("World optimizaton finished after {} ms", var17);
      var16.values().forEach(AnvilChunkLoader::func_75818_b);
      this.field_212222_d.func_75744_a();
      this.field_212221_c.func_75759_a();
      this.field_212225_g = true;
   }

   private boolean func_212542_a(AnvilChunkLoader var1, ListIterator<ChunkPos> var2, DimensionType var3) {
      if (var2.hasNext()) {
         boolean var4;
         synchronized(var1) {
            var4 = var1.func_212147_a((ChunkPos)var2.next(), var3, this.field_212222_d);
         }

         if (var4) {
            ++this.field_212234_p;
         } else {
            ++this.field_212235_q;
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean func_212218_b() {
      return this.field_212225_g;
   }

   public float func_212543_a(DimensionType var1) {
      return this.field_212544_m.getFloat(var1);
   }

   public float func_212207_i() {
      return this.field_212232_n;
   }

   public int func_212211_j() {
      return this.field_212233_o;
   }

   public int func_212208_k() {
      return this.field_212234_p;
   }

   public int func_212209_l() {
      return this.field_212235_q;
   }

   public ITextComponent func_212215_m() {
      return this.field_212236_r;
   }

   public String func_212214_n() {
      return this.field_212237_s;
   }
}

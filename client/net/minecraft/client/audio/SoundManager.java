package net.minecraft.client.audio;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import io.netty.util.internal.ThreadLocalRandom;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import paulscode.sound.SoundSystemLogger;
import paulscode.sound.Source;
import paulscode.sound.codecs.CodecJOrbis;
import paulscode.sound.libraries.LibraryLWJGLOpenAL;

public class SoundManager {
   private static final Marker field_148623_a = MarkerManager.getMarker("SOUNDS");
   private static final Logger field_148621_b = LogManager.getLogger();
   private final SoundHandler field_148622_c;
   private final GameSettings field_148619_d;
   private SoundManager.SoundSystemStarterThread field_148620_e;
   private boolean field_148617_f;
   private int field_148618_g = 0;
   private final Map<String, ISound> field_148629_h = HashBiMap.create();
   private final Map<ISound, String> field_148630_i;
   private Map<ISound, SoundPoolEntry> field_148627_j;
   private final Multimap<SoundCategory, String> field_148628_k;
   private final List<ITickableSound> field_148625_l;
   private final Map<ISound, Integer> field_148626_m;
   private final Map<String, Integer> field_148624_n;

   public SoundManager(SoundHandler var1, GameSettings var2) {
      super();
      this.field_148630_i = ((BiMap)this.field_148629_h).inverse();
      this.field_148627_j = Maps.newHashMap();
      this.field_148628_k = HashMultimap.create();
      this.field_148625_l = Lists.newArrayList();
      this.field_148626_m = Maps.newHashMap();
      this.field_148624_n = Maps.newHashMap();
      this.field_148622_c = var1;
      this.field_148619_d = var2;

      try {
         SoundSystemConfig.addLibrary(LibraryLWJGLOpenAL.class);
         SoundSystemConfig.setCodec("ogg", CodecJOrbis.class);
      } catch (SoundSystemException var4) {
         field_148621_b.error(field_148623_a, "Error linking with the LibraryJavaSound plug-in", var4);
      }

   }

   public void func_148596_a() {
      this.func_148613_b();
      this.func_148608_i();
   }

   private synchronized void func_148608_i() {
      if (!this.field_148617_f) {
         try {
            (new Thread(new Runnable() {
               public void run() {
                  SoundSystemConfig.setLogger(new SoundSystemLogger() {
                     public void message(String var1, int var2) {
                        if (!var1.isEmpty()) {
                           SoundManager.field_148621_b.info(var1);
                        }

                     }

                     public void importantMessage(String var1, int var2) {
                        if (!var1.isEmpty()) {
                           SoundManager.field_148621_b.warn(var1);
                        }

                     }

                     public void errorMessage(String var1, String var2, int var3) {
                        if (!var2.isEmpty()) {
                           SoundManager.field_148621_b.error("Error in class '" + var1 + "'");
                           SoundManager.field_148621_b.error(var2);
                        }

                     }
                  });
                  SoundManager.this.field_148620_e = SoundManager.this.new SoundSystemStarterThread();
                  SoundManager.this.field_148617_f = true;
                  SoundManager.this.field_148620_e.setMasterVolume(SoundManager.this.field_148619_d.func_151438_a(SoundCategory.MASTER));
                  SoundManager.field_148621_b.info(SoundManager.field_148623_a, "Sound engine started");
               }
            }, "Sound Library Loader")).start();
         } catch (RuntimeException var2) {
            field_148621_b.error(field_148623_a, "Error starting SoundSystem. Turning off sounds & music", var2);
            this.field_148619_d.func_151439_a(SoundCategory.MASTER, 0.0F);
            this.field_148619_d.func_74303_b();
         }

      }
   }

   private float func_148595_a(SoundCategory var1) {
      return var1 != null && var1 != SoundCategory.MASTER ? this.field_148619_d.func_151438_a(var1) : 1.0F;
   }

   public void func_148601_a(SoundCategory var1, float var2) {
      if (this.field_148617_f) {
         if (var1 == SoundCategory.MASTER) {
            this.field_148620_e.setMasterVolume(var2);
         } else {
            Iterator var3 = this.field_148628_k.get(var1).iterator();

            while(var3.hasNext()) {
               String var4 = (String)var3.next();
               ISound var5 = (ISound)this.field_148629_h.get(var4);
               float var6 = this.func_148594_a(var5, (SoundPoolEntry)this.field_148627_j.get(var5), var1);
               if (var6 <= 0.0F) {
                  this.func_148602_b(var5);
               } else {
                  this.field_148620_e.setVolume(var4, var6);
               }
            }

         }
      }
   }

   public void func_148613_b() {
      if (this.field_148617_f) {
         this.func_148614_c();
         this.field_148620_e.cleanup();
         this.field_148617_f = false;
      }

   }

   public void func_148614_c() {
      if (this.field_148617_f) {
         Iterator var1 = this.field_148629_h.keySet().iterator();

         while(var1.hasNext()) {
            String var2 = (String)var1.next();
            this.field_148620_e.stop(var2);
         }

         this.field_148629_h.clear();
         this.field_148626_m.clear();
         this.field_148625_l.clear();
         this.field_148628_k.clear();
         this.field_148627_j.clear();
         this.field_148624_n.clear();
      }

   }

   public void func_148605_d() {
      ++this.field_148618_g;
      Iterator var1 = this.field_148625_l.iterator();

      String var3;
      while(var1.hasNext()) {
         ITickableSound var2 = (ITickableSound)var1.next();
         var2.func_73660_a();
         if (var2.func_147667_k()) {
            this.func_148602_b(var2);
         } else {
            var3 = (String)this.field_148630_i.get(var2);
            this.field_148620_e.setVolume(var3, this.func_148594_a(var2, (SoundPoolEntry)this.field_148627_j.get(var2), this.field_148622_c.func_147680_a(var2.func_147650_b()).func_148728_d()));
            this.field_148620_e.setPitch(var3, this.func_148606_a(var2, (SoundPoolEntry)this.field_148627_j.get(var2)));
            this.field_148620_e.setPosition(var3, var2.func_147649_g(), var2.func_147654_h(), var2.func_147651_i());
         }
      }

      var1 = this.field_148629_h.entrySet().iterator();

      ISound var4;
      while(var1.hasNext()) {
         Entry var9 = (Entry)var1.next();
         var3 = (String)var9.getKey();
         var4 = (ISound)var9.getValue();
         if (!this.field_148620_e.playing(var3)) {
            int var5 = (Integer)this.field_148624_n.get(var3);
            if (var5 <= this.field_148618_g) {
               int var6 = var4.func_147652_d();
               if (var4.func_147657_c() && var6 > 0) {
                  this.field_148626_m.put(var4, this.field_148618_g + var6);
               }

               var1.remove();
               field_148621_b.debug(field_148623_a, "Removed channel {} because it's not playing anymore", new Object[]{var3});
               this.field_148620_e.removeSource(var3);
               this.field_148624_n.remove(var3);
               this.field_148627_j.remove(var4);

               try {
                  this.field_148628_k.remove(this.field_148622_c.func_147680_a(var4.func_147650_b()).func_148728_d(), var3);
               } catch (RuntimeException var8) {
               }

               if (var4 instanceof ITickableSound) {
                  this.field_148625_l.remove(var4);
               }
            }
         }
      }

      Iterator var10 = this.field_148626_m.entrySet().iterator();

      while(var10.hasNext()) {
         Entry var11 = (Entry)var10.next();
         if (this.field_148618_g >= (Integer)var11.getValue()) {
            var4 = (ISound)var11.getKey();
            if (var4 instanceof ITickableSound) {
               ((ITickableSound)var4).func_73660_a();
            }

            this.func_148611_c(var4);
            var10.remove();
         }
      }

   }

   public boolean func_148597_a(ISound var1) {
      if (!this.field_148617_f) {
         return false;
      } else {
         String var2 = (String)this.field_148630_i.get(var1);
         if (var2 == null) {
            return false;
         } else {
            return this.field_148620_e.playing(var2) || this.field_148624_n.containsKey(var2) && (Integer)this.field_148624_n.get(var2) <= this.field_148618_g;
         }
      }
   }

   public void func_148602_b(ISound var1) {
      if (this.field_148617_f) {
         String var2 = (String)this.field_148630_i.get(var1);
         if (var2 != null) {
            this.field_148620_e.stop(var2);
         }

      }
   }

   public void func_148611_c(ISound var1) {
      if (this.field_148617_f) {
         if (this.field_148620_e.getMasterVolume() <= 0.0F) {
            field_148621_b.debug(field_148623_a, "Skipped playing soundEvent: {}, master volume was zero", new Object[]{var1.func_147650_b()});
         } else {
            SoundEventAccessorComposite var2 = this.field_148622_c.func_147680_a(var1.func_147650_b());
            if (var2 == null) {
               field_148621_b.warn(field_148623_a, "Unable to play unknown soundEvent: {}", new Object[]{var1.func_147650_b()});
            } else {
               SoundPoolEntry var3 = var2.func_148720_g();
               if (var3 == SoundHandler.field_147700_a) {
                  field_148621_b.warn(field_148623_a, "Unable to play empty soundEvent: {}", new Object[]{var2.func_148729_c()});
               } else {
                  float var4 = var1.func_147653_e();
                  float var5 = 16.0F;
                  if (var4 > 1.0F) {
                     var5 *= var4;
                  }

                  SoundCategory var6 = var2.func_148728_d();
                  float var7 = this.func_148594_a(var1, var3, var6);
                  double var8 = (double)this.func_148606_a(var1, var3);
                  ResourceLocation var10 = var3.func_148652_a();
                  if (var7 == 0.0F) {
                     field_148621_b.debug(field_148623_a, "Skipped playing sound {}, volume was zero.", new Object[]{var10});
                  } else {
                     boolean var11 = var1.func_147657_c() && var1.func_147652_d() == 0;
                     String var12 = MathHelper.func_180182_a(ThreadLocalRandom.current()).toString();
                     if (var3.func_148648_d()) {
                        this.field_148620_e.newStreamingSource(false, var12, func_148612_a(var10), var10.toString(), var11, var1.func_147649_g(), var1.func_147654_h(), var1.func_147651_i(), var1.func_147656_j().func_148586_a(), var5);
                     } else {
                        this.field_148620_e.newSource(false, var12, func_148612_a(var10), var10.toString(), var11, var1.func_147649_g(), var1.func_147654_h(), var1.func_147651_i(), var1.func_147656_j().func_148586_a(), var5);
                     }

                     field_148621_b.debug(field_148623_a, "Playing sound {} for event {} as channel {}", new Object[]{var3.func_148652_a(), var2.func_148729_c(), var12});
                     this.field_148620_e.setPitch(var12, (float)var8);
                     this.field_148620_e.setVolume(var12, var7);
                     this.field_148620_e.play(var12);
                     this.field_148624_n.put(var12, this.field_148618_g + 20);
                     this.field_148629_h.put(var12, var1);
                     this.field_148627_j.put(var1, var3);
                     if (var6 != SoundCategory.MASTER) {
                        this.field_148628_k.put(var6, var12);
                     }

                     if (var1 instanceof ITickableSound) {
                        this.field_148625_l.add((ITickableSound)var1);
                     }

                  }
               }
            }
         }
      }
   }

   private float func_148606_a(ISound var1, SoundPoolEntry var2) {
      return (float)MathHelper.func_151237_a((double)var1.func_147655_f() * var2.func_148650_b(), 0.5D, 2.0D);
   }

   private float func_148594_a(ISound var1, SoundPoolEntry var2, SoundCategory var3) {
      return (float)MathHelper.func_151237_a((double)var1.func_147653_e() * var2.func_148649_c(), 0.0D, 1.0D) * this.func_148595_a(var3);
   }

   public void func_148610_e() {
      Iterator var1 = this.field_148629_h.keySet().iterator();

      while(var1.hasNext()) {
         String var2 = (String)var1.next();
         field_148621_b.debug(field_148623_a, "Pausing channel {}", new Object[]{var2});
         this.field_148620_e.pause(var2);
      }

   }

   public void func_148604_f() {
      Iterator var1 = this.field_148629_h.keySet().iterator();

      while(var1.hasNext()) {
         String var2 = (String)var1.next();
         field_148621_b.debug(field_148623_a, "Resuming channel {}", new Object[]{var2});
         this.field_148620_e.play(var2);
      }

   }

   public void func_148599_a(ISound var1, int var2) {
      this.field_148626_m.put(var1, this.field_148618_g + var2);
   }

   private static URL func_148612_a(final ResourceLocation var0) {
      String var1 = String.format("%s:%s:%s", "mcsounddomain", var0.func_110624_b(), var0.func_110623_a());
      URLStreamHandler var2 = new URLStreamHandler() {
         protected URLConnection openConnection(URL var1) {
            return new URLConnection(var1) {
               public void connect() throws IOException {
               }

               public InputStream getInputStream() throws IOException {
                  return Minecraft.func_71410_x().func_110442_L().func_110536_a(var0).func_110527_b();
               }
            };
         }
      };

      try {
         return new URL((URL)null, var1, var2);
      } catch (MalformedURLException var4) {
         throw new Error("TODO: Sanely handle url exception! :D");
      }
   }

   public void func_148615_a(EntityPlayer var1, float var2) {
      if (this.field_148617_f && var1 != null) {
         float var3 = var1.field_70127_C + (var1.field_70125_A - var1.field_70127_C) * var2;
         float var4 = var1.field_70126_B + (var1.field_70177_z - var1.field_70126_B) * var2;
         double var5 = var1.field_70169_q + (var1.field_70165_t - var1.field_70169_q) * (double)var2;
         double var7 = var1.field_70167_r + (var1.field_70163_u - var1.field_70167_r) * (double)var2 + (double)var1.func_70047_e();
         double var9 = var1.field_70166_s + (var1.field_70161_v - var1.field_70166_s) * (double)var2;
         float var11 = MathHelper.func_76134_b((var4 + 90.0F) * 0.017453292F);
         float var12 = MathHelper.func_76126_a((var4 + 90.0F) * 0.017453292F);
         float var13 = MathHelper.func_76134_b(-var3 * 0.017453292F);
         float var14 = MathHelper.func_76126_a(-var3 * 0.017453292F);
         float var15 = MathHelper.func_76134_b((-var3 + 90.0F) * 0.017453292F);
         float var16 = MathHelper.func_76126_a((-var3 + 90.0F) * 0.017453292F);
         float var17 = var11 * var13;
         float var19 = var12 * var13;
         float var20 = var11 * var15;
         float var22 = var12 * var15;
         this.field_148620_e.setListenerPosition((float)var5, (float)var7, (float)var9);
         this.field_148620_e.setListenerOrientation(var17, var14, var19, var20, var16, var22);
      }
   }

   class SoundSystemStarterThread extends SoundSystem {
      private SoundSystemStarterThread() {
         super();
      }

      public boolean playing(String var1) {
         synchronized(SoundSystemConfig.THREAD_SYNC) {
            if (this.soundLibrary == null) {
               return false;
            } else {
               Source var3 = (Source)this.soundLibrary.getSources().get(var1);
               if (var3 == null) {
                  return false;
               } else {
                  return var3.playing() || var3.paused() || var3.preLoad;
               }
            }
         }
      }

      // $FF: synthetic method
      SoundSystemStarterThread(Object var2) {
         this();
      }
   }
}

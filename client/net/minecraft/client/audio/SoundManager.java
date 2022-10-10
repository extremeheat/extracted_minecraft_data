package net.minecraft.client.audio;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import io.netty.util.internal.ThreadLocalRandom;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DefaultUncaughtExceptionHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.IRegistry;
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

public class SoundManager {
   private static final Marker field_148623_a = MarkerManager.getMarker("SOUNDS");
   private static final Logger field_148621_b = LogManager.getLogger();
   private static final Set<ResourceLocation> field_188775_c = Sets.newHashSet();
   private final SoundHandler field_148622_c;
   private final GameSettings field_148619_d;
   private SoundManager.SoundSystemStarterThread field_148620_e;
   private boolean field_148617_f;
   private int field_148618_g;
   private final Map<String, ISound> field_148629_h = HashBiMap.create();
   private final Map<ISound, String> field_148630_i;
   private final Multimap<SoundCategory, String> field_188776_k;
   private final List<ITickableSound> field_148625_l;
   private final Map<ISound, Integer> field_148626_m;
   private final Map<String, Integer> field_148624_n;
   private final List<ISoundEventListener> field_188777_o;
   private final List<String> field_189000_p;
   private final List<Sound> field_204261_q;

   public SoundManager(SoundHandler var1, GameSettings var2) {
      super();
      this.field_148630_i = ((BiMap)this.field_148629_h).inverse();
      this.field_188776_k = HashMultimap.create();
      this.field_148625_l = Lists.newArrayList();
      this.field_148626_m = Maps.newHashMap();
      this.field_148624_n = Maps.newHashMap();
      this.field_188777_o = Lists.newArrayList();
      this.field_189000_p = Lists.newArrayList();
      this.field_204261_q = Lists.newArrayList();
      this.field_148622_c = var1;
      this.field_148619_d = var2;

      try {
         SoundSystemConfig.addLibrary(LibraryLWJGL3.class);
         SoundSystemConfig.setCodec("ogg", CodecJOrbis.class);
      } catch (SoundSystemException var4) {
         field_148621_b.error(field_148623_a, "Error linking with the LibraryJavaSound plug-in", var4);
      }

   }

   public void func_148596_a() {
      field_188775_c.clear();
      Iterator var1 = IRegistry.field_212633_v.iterator();

      while(var1.hasNext()) {
         SoundEvent var2 = (SoundEvent)var1.next();
         ResourceLocation var3 = var2.func_187503_a();
         if (this.field_148622_c.func_184398_a(var3) == null) {
            field_148621_b.warn("Missing sound for event: {}", IRegistry.field_212633_v.func_177774_c(var2));
            field_188775_c.add(var3);
         }
      }

      this.func_148613_b();
      this.func_148608_i();
   }

   private synchronized void func_148608_i() {
      if (!this.field_148617_f) {
         try {
            Thread var1 = new Thread(() -> {
               SoundSystemConfig.setLogger(new SoundSystemLogger() {
                  public void message(String var1, int var2) {
                     if (!var1.isEmpty()) {
                        SoundManager.field_148621_b.info(var1);
                     }

                  }

                  public void importantMessage(String var1, int var2) {
                     if (var1.startsWith("Author:")) {
                        SoundManager.field_148621_b.info("SoundSystem {}", var1);
                     } else if (!var1.isEmpty()) {
                        SoundManager.field_148621_b.warn(var1);
                     }

                  }

                  public void errorMessage(String var1, String var2, int var3) {
                     if (!var2.isEmpty()) {
                        SoundManager.field_148621_b.error("Error in class '{}'", var1);
                        SoundManager.field_148621_b.error(var2);
                     }

                  }
               });
               this.field_148620_e = new SoundManager.SoundSystemStarterThread();
               this.field_148617_f = true;
               this.field_148620_e.setMasterVolume(this.field_148619_d.func_186711_a(SoundCategory.MASTER));
               Iterator var1 = this.field_204261_q.iterator();

               while(var1.hasNext()) {
                  Sound var2 = (Sound)var1.next();
                  this.func_204260_b(var2);
                  var1.remove();
               }

               field_148621_b.info(field_148623_a, "Sound engine started");
            }, "Sound Library Loader");
            var1.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(field_148621_b));
            var1.start();
         } catch (RuntimeException var2) {
            field_148621_b.error(field_148623_a, "Error starting SoundSystem. Turning off sounds & music", var2);
            this.field_148619_d.func_186712_a(SoundCategory.MASTER, 0.0F);
            this.field_148619_d.func_74303_b();
         }

      }
   }

   private float func_188769_a(SoundCategory var1) {
      return var1 != null && var1 != SoundCategory.MASTER ? this.field_148619_d.func_186711_a(var1) : 1.0F;
   }

   public void func_188771_a(SoundCategory var1, float var2) {
      if (this.field_148617_f) {
         if (var1 == SoundCategory.MASTER) {
            this.field_148620_e.setMasterVolume(var2);
         } else {
            Iterator var3 = this.field_188776_k.get(var1).iterator();

            while(var3.hasNext()) {
               String var4 = (String)var3.next();
               ISound var5 = (ISound)this.field_148629_h.get(var4);
               float var6 = this.func_188770_e(var5);
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
         this.field_189000_p.clear();
         this.field_188776_k.clear();
         this.field_148624_n.clear();
      }

   }

   public void func_188774_a(ISoundEventListener var1) {
      this.field_188777_o.add(var1);
   }

   public void func_188773_b(ISoundEventListener var1) {
      this.field_188777_o.remove(var1);
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
            this.field_148620_e.setVolume(var3, this.func_188770_e(var2));
            this.field_148620_e.setPitch(var3, this.func_188772_d(var2));
            this.field_148620_e.setPosition(var3, var2.func_147649_g(), var2.func_147654_h(), var2.func_147651_i());
         }
      }

      var1 = this.field_148629_h.entrySet().iterator();

      ISound var4;
      while(var1.hasNext()) {
         Entry var10 = (Entry)var1.next();
         var3 = (String)var10.getKey();
         var4 = (ISound)var10.getValue();
         float var5 = this.field_148619_d.func_186711_a(var4.func_184365_d());
         if (var5 <= 0.0F) {
            this.func_148602_b(var4);
         }

         if (!this.field_148620_e.playing(var3)) {
            int var6 = (Integer)this.field_148624_n.get(var3);
            if (var6 <= this.field_148618_g) {
               int var7 = var4.func_147652_d();
               if (var4.func_147657_c() && var7 > 0) {
                  this.field_148626_m.put(var4, this.field_148618_g + var7);
               }

               var1.remove();
               field_148621_b.debug(field_148623_a, "Removed channel {} because it's not playing anymore", var3);
               this.field_148620_e.removeSource(var3);
               this.field_148624_n.remove(var3);

               try {
                  this.field_188776_k.remove(var4.func_184365_d(), var3);
               } catch (RuntimeException var9) {
               }

               if (var4 instanceof ITickableSound) {
                  this.field_148625_l.remove(var4);
               }
            }
         }
      }

      Iterator var11 = this.field_148626_m.entrySet().iterator();

      while(var11.hasNext()) {
         Entry var12 = (Entry)var11.next();
         if (this.field_148618_g >= (Integer)var12.getValue()) {
            var4 = (ISound)var12.getKey();
            if (var4 instanceof ITickableSound) {
               ((ITickableSound)var4).func_73660_a();
            }

            this.func_148611_c(var4);
            var11.remove();
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
         SoundEventAccessor var2 = var1.func_184366_a(this.field_148622_c);
         ResourceLocation var3 = var1.func_147650_b();
         if (var2 == null) {
            if (field_188775_c.add(var3)) {
               field_148621_b.warn(field_148623_a, "Unable to play unknown soundEvent: {}", var3);
            }

         } else {
            if (!this.field_188777_o.isEmpty()) {
               Iterator var4 = this.field_188777_o.iterator();

               while(var4.hasNext()) {
                  ISoundEventListener var5 = (ISoundEventListener)var4.next();
                  var5.func_184067_a(var1, var2);
               }
            }

            if (this.field_148620_e.getMasterVolume() <= 0.0F) {
               field_148621_b.debug(field_148623_a, "Skipped playing soundEvent: {}, master volume was zero", var3);
            } else {
               Sound var13 = var1.func_184364_b();
               if (var13 == SoundHandler.field_147700_a) {
                  if (field_188775_c.add(var3)) {
                     field_148621_b.warn(field_148623_a, "Unable to play empty soundEvent: {}", var3);
                  }

               } else {
                  float var14 = var1.func_147653_e();
                  float var6 = (float)var13.func_206255_j();
                  if (var14 > 1.0F) {
                     var6 *= var14;
                  }

                  SoundCategory var7 = var1.func_184365_d();
                  float var8 = this.func_188770_e(var1);
                  float var9 = this.func_188772_d(var1);
                  if (var8 == 0.0F && !var1.func_211503_n()) {
                     field_148621_b.debug(field_148623_a, "Skipped playing sound {}, volume was zero.", var13.func_188719_a());
                  } else {
                     boolean var10 = var1.func_147657_c() && var1.func_147652_d() == 0;
                     String var11 = MathHelper.func_180182_a(ThreadLocalRandom.current()).toString();
                     ResourceLocation var12 = var13.func_188721_b();
                     if (var13.func_188723_h()) {
                        this.field_148620_e.newStreamingSource(var1.func_204200_l(), var11, func_148612_a(var12), var12.toString(), var10, var1.func_147649_g(), var1.func_147654_h(), var1.func_147651_i(), var1.func_147656_j().func_148586_a(), var6);
                     } else {
                        this.field_148620_e.newSource(var1.func_204200_l(), var11, func_148612_a(var12), var12.toString(), var10, var1.func_147649_g(), var1.func_147654_h(), var1.func_147651_i(), var1.func_147656_j().func_148586_a(), var6);
                     }

                     field_148621_b.debug(field_148623_a, "Playing sound {} for event {} as channel {}", var13.func_188719_a(), var3, var11);
                     this.field_148620_e.setPitch(var11, var9);
                     this.field_148620_e.setVolume(var11, var8);
                     this.field_148620_e.play(var11);
                     this.field_148624_n.put(var11, this.field_148618_g + 20);
                     this.field_148629_h.put(var11, var1);
                     this.field_188776_k.put(var7, var11);
                     if (var1 instanceof ITickableSound) {
                        this.field_148625_l.add((ITickableSound)var1);
                     }

                  }
               }
            }
         }
      }
   }

   public void func_204259_a(Sound var1) {
      this.field_204261_q.add(var1);
   }

   private void func_204260_b(Sound var1) {
      ResourceLocation var2 = var1.func_188721_b();
      field_148621_b.info(field_148623_a, "Preloading sound {}", var2);
      this.field_148620_e.loadSound(func_148612_a(var2), var2.toString());
   }

   private float func_188772_d(ISound var1) {
      return MathHelper.func_76131_a(var1.func_147655_f(), 0.5F, 2.0F);
   }

   private float func_188770_e(ISound var1) {
      return MathHelper.func_76131_a(var1.func_147653_e() * this.func_188769_a(var1.func_184365_d()), 0.0F, 1.0F);
   }

   public void func_148610_e() {
      Iterator var1 = this.field_148629_h.entrySet().iterator();

      while(var1.hasNext()) {
         Entry var2 = (Entry)var1.next();
         String var3 = (String)var2.getKey();
         boolean var4 = this.func_148597_a((ISound)var2.getValue());
         if (var4) {
            field_148621_b.debug(field_148623_a, "Pausing channel {}", var3);
            this.field_148620_e.pause(var3);
            this.field_189000_p.add(var3);
         }
      }

   }

   public void func_148604_f() {
      Iterator var1 = this.field_189000_p.iterator();

      while(var1.hasNext()) {
         String var2 = (String)var1.next();
         field_148621_b.debug(field_148623_a, "Resuming channel {}", var2);
         this.field_148620_e.play(var2);
      }

      this.field_189000_p.clear();
   }

   public void func_148599_a(ISound var1, int var2) {
      this.field_148626_m.put(var1, this.field_148618_g + var2);
   }

   private static URL func_148612_a(final ResourceLocation var0) {
      String var1 = String.format("%s:%s:%s", "mcsounddomain", var0.func_110624_b(), var0.func_110623_a());
      URLStreamHandler var2 = new URLStreamHandler() {
         protected URLConnection openConnection(URL var1) {
            return new URLConnection(var1) {
               public void connect() {
               }

               public InputStream getInputStream() throws IOException {
                  return Minecraft.func_71410_x().func_195551_G().func_199002_a(var0).func_199027_b();
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

   public void func_195855_a(@Nullable ResourceLocation var1, @Nullable SoundCategory var2) {
      Iterator var3;
      if (var2 != null) {
         var3 = this.field_188776_k.get(var2).iterator();

         while(var3.hasNext()) {
            String var4 = (String)var3.next();
            ISound var5 = (ISound)this.field_148629_h.get(var4);
            if (var1 == null) {
               this.func_148602_b(var5);
            } else if (var5.func_147650_b().equals(var1)) {
               this.func_148602_b(var5);
            }
         }
      } else if (var1 == null) {
         this.func_148614_c();
      } else {
         var3 = this.field_148629_h.values().iterator();

         while(var3.hasNext()) {
            ISound var6 = (ISound)var3.next();
            if (var6.func_147650_b().equals(var1)) {
               this.func_148602_b(var6);
            }
         }
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
               HashMap var3 = this.soundLibrary.getSources();
               if (var3 == null) {
                  return false;
               } else {
                  Source var4 = (Source)var3.get(var1);
                  if (var4 == null) {
                     return false;
                  } else {
                     return var4.playing() || var4.paused() || var4.preLoad;
                  }
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

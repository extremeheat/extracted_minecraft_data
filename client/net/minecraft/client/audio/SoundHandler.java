package net.minecraft.client.audio;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SoundHandler implements IResourceManagerReloadListener, ITickable {
   private static final Logger field_147698_b = LogManager.getLogger();
   private static final Gson field_147699_c = (new GsonBuilder()).registerTypeAdapter(SoundList.class, new SoundListSerializer()).create();
   private static final ParameterizedType field_147696_d = new ParameterizedType() {
      public Type[] getActualTypeArguments() {
         return new Type[]{String.class, SoundList.class};
      }

      public Type getRawType() {
         return Map.class;
      }

      public Type getOwnerType() {
         return null;
      }
   };
   public static final SoundPoolEntry field_147700_a = new SoundPoolEntry(new ResourceLocation("meta:missing_sound"), 0.0D, 0.0D, false);
   private final SoundRegistry field_147697_e = new SoundRegistry();
   private final SoundManager field_147694_f;
   private final IResourceManager field_147695_g;

   public SoundHandler(IResourceManager var1, GameSettings var2) {
      super();
      this.field_147695_g = var1;
      this.field_147694_f = new SoundManager(this, var2);
   }

   public void func_110549_a(IResourceManager var1) {
      this.field_147694_f.func_148596_a();
      this.field_147697_e.func_148763_c();
      Iterator var2 = var1.func_135055_a().iterator();

      while(var2.hasNext()) {
         String var3 = (String)var2.next();

         try {
            List var4 = var1.func_135056_b(new ResourceLocation(var3, "sounds.json"));
            Iterator var5 = var4.iterator();

            while(var5.hasNext()) {
               IResource var6 = (IResource)var5.next();

               try {
                  Map var7 = this.func_175085_a(var6.func_110527_b());
                  Iterator var8 = var7.entrySet().iterator();

                  while(var8.hasNext()) {
                     Entry var9 = (Entry)var8.next();
                     this.func_147693_a(new ResourceLocation(var3, (String)var9.getKey()), (SoundList)var9.getValue());
                  }
               } catch (RuntimeException var10) {
                  field_147698_b.warn("Invalid sounds.json", var10);
               }
            }
         } catch (IOException var11) {
         }
      }

   }

   protected Map<String, SoundList> func_175085_a(InputStream var1) {
      Map var2;
      try {
         var2 = (Map)field_147699_c.fromJson(new InputStreamReader(var1), field_147696_d);
      } finally {
         IOUtils.closeQuietly(var1);
      }

      return var2;
   }

   private void func_147693_a(ResourceLocation var1, SoundList var2) {
      boolean var4 = !this.field_147697_e.func_148741_d(var1);
      SoundEventAccessorComposite var3;
      if (!var4 && !var2.func_148574_b()) {
         var3 = (SoundEventAccessorComposite)this.field_147697_e.func_82594_a(var1);
      } else {
         if (!var4) {
            field_147698_b.debug("Replaced sound event location {}", new Object[]{var1});
         }

         var3 = new SoundEventAccessorComposite(var1, 1.0D, 1.0D, var2.func_148573_c());
         this.field_147697_e.func_148762_a(var3);
      }

      Iterator var5 = var2.func_148570_a().iterator();

      while(var5.hasNext()) {
         final SoundList.SoundEntry var6 = (SoundList.SoundEntry)var5.next();
         String var7 = var6.func_148556_a();
         ResourceLocation var8 = new ResourceLocation(var7);
         final String var9 = var7.contains(":") ? var8.func_110624_b() : var1.func_110624_b();
         Object var10;
         switch(var6.func_148563_e()) {
         case FILE:
            ResourceLocation var11 = new ResourceLocation(var9, "sounds/" + var8.func_110623_a() + ".ogg");
            InputStream var12 = null;

            try {
               var12 = this.field_147695_g.func_110536_a(var11).func_110527_b();
            } catch (FileNotFoundException var18) {
               field_147698_b.warn("File {} does not exist, cannot add it to event {}", new Object[]{var11, var1});
               continue;
            } catch (IOException var19) {
               field_147698_b.warn("Could not load sound file " + var11 + ", cannot add it to event " + var1, var19);
               continue;
            } finally {
               IOUtils.closeQuietly(var12);
            }

            var10 = new SoundEventAccessor(new SoundPoolEntry(var11, (double)var6.func_148560_c(), (double)var6.func_148558_b(), var6.func_148552_f()), var6.func_148555_d());
            break;
         case SOUND_EVENT:
            var10 = new ISoundEventAccessor<SoundPoolEntry>() {
               final ResourceLocation field_148726_a = new ResourceLocation(var9, var6.func_148556_a());

               public int func_148721_a() {
                  SoundEventAccessorComposite var1 = (SoundEventAccessorComposite)SoundHandler.this.field_147697_e.func_82594_a(this.field_148726_a);
                  return var1 == null ? 0 : var1.func_148721_a();
               }

               public SoundPoolEntry func_148720_g() {
                  SoundEventAccessorComposite var1 = (SoundEventAccessorComposite)SoundHandler.this.field_147697_e.func_82594_a(this.field_148726_a);
                  return var1 == null ? SoundHandler.field_147700_a : var1.func_148720_g();
               }

               // $FF: synthetic method
               public Object func_148720_g() {
                  return this.func_148720_g();
               }
            };
            break;
         default:
            throw new IllegalStateException("IN YOU FACE");
         }

         var3.func_148727_a((ISoundEventAccessor)var10);
      }

   }

   public SoundEventAccessorComposite func_147680_a(ResourceLocation var1) {
      return (SoundEventAccessorComposite)this.field_147697_e.func_82594_a(var1);
   }

   public void func_147682_a(ISound var1) {
      this.field_147694_f.func_148611_c(var1);
   }

   public void func_147681_a(ISound var1, int var2) {
      this.field_147694_f.func_148599_a(var1, var2);
   }

   public void func_147691_a(EntityPlayer var1, float var2) {
      this.field_147694_f.func_148615_a(var1, var2);
   }

   public void func_147689_b() {
      this.field_147694_f.func_148610_e();
   }

   public void func_147690_c() {
      this.field_147694_f.func_148614_c();
   }

   public void func_147685_d() {
      this.field_147694_f.func_148613_b();
   }

   public void func_73660_a() {
      this.field_147694_f.func_148605_d();
   }

   public void func_147687_e() {
      this.field_147694_f.func_148604_f();
   }

   public void func_147684_a(SoundCategory var1, float var2) {
      if (var1 == SoundCategory.MASTER && var2 <= 0.0F) {
         this.func_147690_c();
      }

      this.field_147694_f.func_148601_a(var1, var2);
   }

   public void func_147683_b(ISound var1) {
      this.field_147694_f.func_148602_b(var1);
   }

   public SoundEventAccessorComposite func_147686_a(SoundCategory... var1) {
      ArrayList var2 = Lists.newArrayList();
      Iterator var3 = this.field_147697_e.func_148742_b().iterator();

      while(var3.hasNext()) {
         ResourceLocation var4 = (ResourceLocation)var3.next();
         SoundEventAccessorComposite var5 = (SoundEventAccessorComposite)this.field_147697_e.func_82594_a(var4);
         if (ArrayUtils.contains(var1, var5.func_148728_d())) {
            var2.add(var5);
         }
      }

      if (var2.isEmpty()) {
         return null;
      } else {
         return (SoundEventAccessorComposite)var2.get((new Random()).nextInt(var2.size()));
      }
   }

   public boolean func_147692_c(ISound var1) {
      return this.field_147694_f.func_148597_a(var1);
   }
}

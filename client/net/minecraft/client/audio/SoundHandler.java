package net.minecraft.client.audio;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.client.GameSettings;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.ITickable;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SoundHandler implements ITickable, IResourceManagerReloadListener {
   public static final Sound field_147700_a;
   private static final Logger field_147698_b;
   private static final Gson field_147699_c;
   private static final ParameterizedType field_147696_d;
   private final Map<ResourceLocation, SoundEventAccessor> field_147697_e = Maps.newHashMap();
   private final SoundManager field_147694_f;
   private final IResourceManager field_147695_g;

   public SoundHandler(IResourceManager var1, GameSettings var2) {
      super();
      this.field_147695_g = var1;
      this.field_147694_f = new SoundManager(this, var2);
   }

   public void func_195410_a(IResourceManager var1) {
      this.field_147697_e.clear();
      Iterator var2 = var1.func_199001_a().iterator();

      while(var2.hasNext()) {
         String var3 = (String)var2.next();

         try {
            List var4 = var1.func_199004_b(new ResourceLocation(var3, "sounds.json"));
            Iterator var5 = var4.iterator();

            while(var5.hasNext()) {
               IResource var6 = (IResource)var5.next();

               try {
                  Map var7 = this.func_175085_a(var6.func_199027_b());
                  Iterator var8 = var7.entrySet().iterator();

                  while(var8.hasNext()) {
                     Entry var9 = (Entry)var8.next();
                     this.func_147693_a(new ResourceLocation(var3, (String)var9.getKey()), (SoundList)var9.getValue());
                  }
               } catch (RuntimeException var10) {
                  field_147698_b.warn("Invalid sounds.json in resourcepack: '{}'", var6.func_199026_d(), var10);
               }
            }
         } catch (IOException var11) {
         }
      }

      var2 = this.field_147697_e.keySet().iterator();

      ResourceLocation var12;
      while(var2.hasNext()) {
         var12 = (ResourceLocation)var2.next();
         SoundEventAccessor var13 = (SoundEventAccessor)this.field_147697_e.get(var12);
         if (var13.func_188712_c() instanceof TextComponentTranslation) {
            String var14 = ((TextComponentTranslation)var13.func_188712_c()).func_150268_i();
            if (!I18n.func_188566_a(var14)) {
               field_147698_b.debug("Missing subtitle {} for event: {}", var14, var12);
            }
         }
      }

      var2 = this.field_147697_e.keySet().iterator();

      while(var2.hasNext()) {
         var12 = (ResourceLocation)var2.next();
         if (IRegistry.field_212633_v.func_212608_b(var12) == null) {
            field_147698_b.debug("Not having sound event for: {}", var12);
         }
      }

      this.field_147694_f.func_148596_a();
   }

   @Nullable
   protected Map<String, SoundList> func_175085_a(InputStream var1) {
      Map var2;
      try {
         var2 = (Map)JsonUtils.func_193841_a(field_147699_c, new InputStreamReader(var1, StandardCharsets.UTF_8), field_147696_d);
      } finally {
         IOUtils.closeQuietly(var1);
      }

      return var2;
   }

   private void func_147693_a(ResourceLocation var1, SoundList var2) {
      SoundEventAccessor var3 = (SoundEventAccessor)this.field_147697_e.get(var1);
      boolean var4 = var3 == null;
      if (var4 || var2.func_148574_b()) {
         if (!var4) {
            field_147698_b.debug("Replaced sound event location {}", var1);
         }

         var3 = new SoundEventAccessor(var1, var2.func_188701_c());
         this.field_147697_e.put(var1, var3);
      }

      Iterator var5 = var2.func_188700_a().iterator();

      while(var5.hasNext()) {
         final Sound var6 = (Sound)var5.next();
         final ResourceLocation var7 = var6.func_188719_a();
         Object var8;
         switch(var6.func_188722_g()) {
         case FILE:
            if (!this.func_184401_a(var6, var1)) {
               continue;
            }

            var8 = var6;
            break;
         case SOUND_EVENT:
            var8 = new ISoundEventAccessor<Sound>() {
               public int func_148721_a() {
                  SoundEventAccessor var1 = (SoundEventAccessor)SoundHandler.this.field_147697_e.get(var7);
                  return var1 == null ? 0 : var1.func_148721_a();
               }

               public Sound func_148720_g() {
                  SoundEventAccessor var1 = (SoundEventAccessor)SoundHandler.this.field_147697_e.get(var7);
                  if (var1 == null) {
                     return SoundHandler.field_147700_a;
                  } else {
                     Sound var2 = var1.func_148720_g();
                     return new Sound(var2.func_188719_a().toString(), var2.func_188724_c() * var6.func_188724_c(), var2.func_188725_d() * var6.func_188725_d(), var6.func_148721_a(), Sound.Type.FILE, var2.func_188723_h() || var6.func_188723_h(), var2.func_204257_i(), var2.func_206255_j());
                  }
               }

               // $FF: synthetic method
               public Object func_148720_g() {
                  return this.func_148720_g();
               }
            };
            break;
         default:
            throw new IllegalStateException("Unknown SoundEventRegistration type: " + var6.func_188722_g());
         }

         if (((Sound)((ISoundEventAccessor)var8).func_148720_g()).func_204257_i()) {
            this.field_147694_f.func_204259_a((Sound)((ISoundEventAccessor)var8).func_148720_g());
         }

         var3.func_188715_a((ISoundEventAccessor)var8);
      }

   }

   private boolean func_184401_a(Sound var1, ResourceLocation var2) {
      ResourceLocation var3 = var1.func_188721_b();
      IResource var4 = null;

      boolean var6;
      try {
         var4 = this.field_147695_g.func_199002_a(var3);
         var4.func_199027_b();
         return true;
      } catch (FileNotFoundException var11) {
         field_147698_b.warn("File {} does not exist, cannot add it to event {}", var3, var2);
         var6 = false;
      } catch (IOException var12) {
         field_147698_b.warn("Could not load sound file {}, cannot add it to event {}", var3, var2, var12);
         var6 = false;
         return var6;
      } finally {
         IOUtils.closeQuietly(var4);
      }

      return var6;
   }

   @Nullable
   public SoundEventAccessor func_184398_a(ResourceLocation var1) {
      return (SoundEventAccessor)this.field_147697_e.get(var1);
   }

   public Collection<ResourceLocation> func_195477_a() {
      return this.field_147697_e.keySet();
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

   public void func_184399_a(SoundCategory var1, float var2) {
      if (var1 == SoundCategory.MASTER && var2 <= 0.0F) {
         this.func_147690_c();
      }

      this.field_147694_f.func_188771_a(var1, var2);
   }

   public void func_147683_b(ISound var1) {
      this.field_147694_f.func_148602_b(var1);
   }

   public boolean func_147692_c(ISound var1) {
      return this.field_147694_f.func_148597_a(var1);
   }

   public void func_184402_a(ISoundEventListener var1) {
      this.field_147694_f.func_188774_a(var1);
   }

   public void func_184400_b(ISoundEventListener var1) {
      this.field_147694_f.func_188773_b(var1);
   }

   public void func_195478_a(@Nullable ResourceLocation var1, @Nullable SoundCategory var2) {
      this.field_147694_f.func_195855_a(var1, var2);
   }

   static {
      field_147700_a = new Sound("meta:missing_sound", 1.0F, 1.0F, 1, Sound.Type.FILE, false, false, 16);
      field_147698_b = LogManager.getLogger();
      field_147699_c = (new GsonBuilder()).registerTypeHierarchyAdapter(ITextComponent.class, new ITextComponent.Serializer()).registerTypeAdapter(SoundList.class, new SoundListSerializer()).create();
      field_147696_d = new ParameterizedType() {
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
   }
}

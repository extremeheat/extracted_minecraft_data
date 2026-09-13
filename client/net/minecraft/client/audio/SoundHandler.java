package net.minecraft.client.audio;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SoundHandler implements IResourceManagerReloadListener, IUpdatePlayerListBox {
   private static final Logger field_147698_b = LogManager.getLogger();
   private static final Gson field_147699_c = new GsonBuilder().registerTypeAdapter(SoundList.class, new SoundListSerializer()).create();
   private static final ParameterizedType field_147696_d = new SoundHandler$1();
   public static final SoundPoolEntry field_147700_a = new SoundPoolEntry(new ResourceLocation("meta:missing_sound"), 0.0, 0.0, false);
   private final SoundRegistry field_147697_e = new SoundRegistry();
   private final SoundManager field_147694_f;
   private final IResourceManager field_147695_g;

   public SoundHandler(IResourceManager var1, GameSettings var2) {
      super();
      this.field_147695_g = var1;
      this.field_147694_f = new SoundManager(this, var2);
   }

   @Override
   public void func_110549_a(IResourceManager var1) {
      this.field_147694_f.func_148596_a();
      this.field_147697_e.func_148763_c();

      for(String var3 : var1.func_135055_a()) {
         try {
            for(IResource var6 : var1.func_135056_b(new ResourceLocation(var3, "sounds.json"))) {
               try {
                  Map var7 = (Map)field_147699_c.fromJson(new InputStreamReader(var6.func_110527_b()), field_147696_d);

                  for(Entry var9 : var7.entrySet()) {
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

   private void func_147693_a(ResourceLocation var1, SoundList var2) {
      SoundEventAccessorComposite var3;
      if (this.field_147697_e.func_148741_d(var1) && !var2.func_148574_b()) {
         var3 = (SoundEventAccessorComposite)this.field_147697_e.func_82594_a(var1);
      } else {
         field_147698_b.debug("Registered/replaced new sound event location {}", new Object[]{var1});
         var3 = new SoundEventAccessorComposite(var1, 1.0, 1.0, var2.func_148573_c());
         this.field_147697_e.func_148762_a(var3);
      }

      for(SoundList$SoundEntry var5 : var2.func_148570_a()) {
         String var6 = var5.func_148556_a();
         ResourceLocation var7 = new ResourceLocation(var6);
         String var8 = var6.contains(":") ? var7.func_110624_b() : var1.func_110624_b();
         Object var9;
         switch(var5.func_148563_e()) {
            case FILE:
               ResourceLocation var10 = new ResourceLocation(var8, "sounds/" + var7.func_110623_a() + ".ogg");

               try {
                  this.field_147695_g.func_110536_a(var10);
               } catch (FileNotFoundException var12) {
                  field_147698_b.warn("File {} does not exist, cannot add it to event {}", new Object[]{var10, var1});
                  continue;
               } catch (IOException var13) {
                  field_147698_b.warn("Could not load sound file " + var10 + ", cannot add it to event " + var1, var13);
                  continue;
               }

               var9 = new SoundEventAccessor(
                  new SoundPoolEntry(var10, (double)var5.func_148560_c(), (double)var5.func_148558_b(), var5.func_148552_f()), var5.func_148555_d()
               );
               break;
            case SOUND_EVENT:
               var9 = new SoundHandler$2(this, var8, var5);
               break;
            default:
               throw new IllegalStateException("IN YOU FACE");
         }

         var3.func_148727_a((ISoundEventAccessor)var9);
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

   @Override
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

      for(ResourceLocation var4 : this.field_147697_e.func_148742_b()) {
         SoundEventAccessorComposite var5 = (SoundEventAccessorComposite)this.field_147697_e.func_82594_a(var4);
         if (ArrayUtils.contains(var1, var5.func_148728_d())) {
            var2.add(var5);
         }
      }

      return var2.isEmpty() ? null : (SoundEventAccessorComposite)var2.get(new Random().nextInt(var2.size()));
   }

   public boolean func_147692_c(ISound var1) {
      return this.field_147694_f.func_148597_a(var1);
   }
}

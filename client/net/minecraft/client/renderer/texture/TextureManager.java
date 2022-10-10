package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TextureManager implements ITickable, IResourceManagerReloadListener {
   private static final Logger field_147646_a = LogManager.getLogger();
   public static final ResourceLocation field_194008_a = new ResourceLocation("");
   private final Map<ResourceLocation, ITextureObject> field_110585_a = Maps.newHashMap();
   private final List<ITickable> field_110583_b = Lists.newArrayList();
   private final Map<String, Integer> field_110584_c = Maps.newHashMap();
   private final IResourceManager field_110582_d;

   public TextureManager(IResourceManager var1) {
      super();
      this.field_110582_d = var1;
   }

   public void func_110577_a(ResourceLocation var1) {
      Object var2 = (ITextureObject)this.field_110585_a.get(var1);
      if (var2 == null) {
         var2 = new SimpleTexture(var1);
         this.func_110579_a(var1, (ITextureObject)var2);
      }

      ((ITextureObject)var2).func_195412_h();
   }

   public boolean func_110580_a(ResourceLocation var1, ITickableTextureObject var2) {
      if (this.func_110579_a(var1, var2)) {
         this.field_110583_b.add(var2);
         return true;
      } else {
         return false;
      }
   }

   public boolean func_110579_a(ResourceLocation var1, ITextureObject var2) {
      boolean var3 = true;

      try {
         ((ITextureObject)var2).func_195413_a(this.field_110582_d);
      } catch (IOException var8) {
         if (var1 != field_194008_a) {
            field_147646_a.warn("Failed to load texture: {}", var1, var8);
         }

         var2 = MissingTextureSprite.func_195676_d();
         this.field_110585_a.put(var1, var2);
         var3 = false;
      } catch (Throwable var9) {
         CrashReport var5 = CrashReport.func_85055_a(var9, "Registering texture");
         CrashReportCategory var6 = var5.func_85058_a("Resource location being registered");
         var6.func_71507_a("Resource location", var1);
         var6.func_189529_a("Texture object class", () -> {
            return var2.getClass().getName();
         });
         throw new ReportedException(var5);
      }

      this.field_110585_a.put(var1, var2);
      return var3;
   }

   public ITextureObject func_110581_b(ResourceLocation var1) {
      return (ITextureObject)this.field_110585_a.get(var1);
   }

   public ResourceLocation func_110578_a(String var1, DynamicTexture var2) {
      Integer var3 = (Integer)this.field_110584_c.get(var1);
      if (var3 == null) {
         var3 = 1;
      } else {
         var3 = var3 + 1;
      }

      this.field_110584_c.put(var1, var3);
      ResourceLocation var4 = new ResourceLocation(String.format("dynamic/%s_%d", var1, var3));
      this.func_110579_a(var4, var2);
      return var4;
   }

   public void func_110550_d() {
      Iterator var1 = this.field_110583_b.iterator();

      while(var1.hasNext()) {
         ITickable var2 = (ITickable)var1.next();
         var2.func_110550_d();
      }

   }

   public void func_147645_c(ResourceLocation var1) {
      ITextureObject var2 = this.func_110581_b(var1);
      if (var2 != null) {
         TextureUtil.func_147942_a(var2.func_110552_b());
      }

   }

   public void func_195410_a(IResourceManager var1) {
      MissingTextureSprite.func_195676_d();
      Iterator var2 = this.field_110585_a.entrySet().iterator();

      while(true) {
         while(var2.hasNext()) {
            Entry var3 = (Entry)var2.next();
            ResourceLocation var4 = (ResourceLocation)var3.getKey();
            ITextureObject var5 = (ITextureObject)var3.getValue();
            if (var5 == MissingTextureSprite.func_195676_d() && !var4.equals(MissingTextureSprite.func_195675_b())) {
               var2.remove();
            } else {
               this.func_110579_a((ResourceLocation)var3.getKey(), var5);
            }
         }

         return;
      }
   }
}

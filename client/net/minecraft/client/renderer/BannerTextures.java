package net.minecraft.client.renderer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.LayeredColorMaskTexture;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;

public class BannerTextures {
   public static final BannerTextures.Cache field_178466_c = new BannerTextures.Cache("banner_", new ResourceLocation("textures/entity/banner_base.png"), "textures/entity/banner/");
   public static final BannerTextures.Cache field_187485_b = new BannerTextures.Cache("shield_", new ResourceLocation("textures/entity/shield_base.png"), "textures/entity/shield/");
   public static final ResourceLocation field_187486_c = new ResourceLocation("textures/entity/shield_base_nopattern.png");
   public static final ResourceLocation field_187487_d = new ResourceLocation("textures/entity/banner/base.png");

   static class CacheEntry {
      public long field_187483_a;
      public ResourceLocation field_187484_b;

      private CacheEntry() {
         super();
      }

      // $FF: synthetic method
      CacheEntry(Object var1) {
         this();
      }
   }

   public static class Cache {
      private final Map<String, BannerTextures.CacheEntry> field_187479_a = Maps.newLinkedHashMap();
      private final ResourceLocation field_187480_b;
      private final String field_187481_c;
      private final String field_187482_d;

      public Cache(String var1, ResourceLocation var2, String var3) {
         super();
         this.field_187482_d = var1;
         this.field_187480_b = var2;
         this.field_187481_c = var3;
      }

      @Nullable
      public ResourceLocation func_187478_a(String var1, List<BannerPattern> var2, List<EnumDyeColor> var3) {
         if (var1.isEmpty()) {
            return null;
         } else if (!var2.isEmpty() && !var3.isEmpty()) {
            var1 = this.field_187482_d + var1;
            BannerTextures.CacheEntry var4 = (BannerTextures.CacheEntry)this.field_187479_a.get(var1);
            if (var4 == null) {
               if (this.field_187479_a.size() >= 256 && !this.func_187477_a()) {
                  return BannerTextures.field_187487_d;
               }

               ArrayList var5 = Lists.newArrayList();
               Iterator var6 = var2.iterator();

               while(var6.hasNext()) {
                  BannerPattern var7 = (BannerPattern)var6.next();
                  var5.add(this.field_187481_c + var7.func_190997_a() + ".png");
               }

               var4 = new BannerTextures.CacheEntry();
               var4.field_187484_b = new ResourceLocation(var1);
               Minecraft.func_71410_x().func_110434_K().func_110579_a(var4.field_187484_b, new LayeredColorMaskTexture(this.field_187480_b, var5, var3));
               this.field_187479_a.put(var1, var4);
            }

            var4.field_187483_a = Util.func_211177_b();
            return var4.field_187484_b;
         } else {
            return MissingTextureSprite.func_195675_b();
         }
      }

      private boolean func_187477_a() {
         long var1 = Util.func_211177_b();
         Iterator var3 = this.field_187479_a.keySet().iterator();

         BannerTextures.CacheEntry var5;
         do {
            if (!var3.hasNext()) {
               return this.field_187479_a.size() < 256;
            }

            String var4 = (String)var3.next();
            var5 = (BannerTextures.CacheEntry)this.field_187479_a.get(var4);
         } while(var1 - var5.field_187483_a <= 5000L);

         Minecraft.func_71410_x().func_110434_K().func_147645_c(var5.field_187484_b);
         var3.remove();
         return true;
      }
   }
}

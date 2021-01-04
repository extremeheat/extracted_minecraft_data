package net.minecraft.client.renderer.banner;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.LayeredColorMaskTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BannerPattern;

public class BannerTextures {
   public static final BannerTextures.TextureCache BANNER_CACHE = new BannerTextures.TextureCache("banner_", new ResourceLocation("textures/entity/banner_base.png"), "textures/entity/banner/");
   public static final BannerTextures.TextureCache SHIELD_CACHE = new BannerTextures.TextureCache("shield_", new ResourceLocation("textures/entity/shield_base.png"), "textures/entity/shield/");
   public static final ResourceLocation NO_PATTERN_SHIELD = new ResourceLocation("textures/entity/shield_base_nopattern.png");
   public static final ResourceLocation DEFAULT_PATTERN_BANNER = new ResourceLocation("textures/entity/banner/base.png");

   static class TimestampedBannerTexture {
      public long lastUseMilliseconds;
      public ResourceLocation textureLocation;

      private TimestampedBannerTexture() {
         super();
      }

      // $FF: synthetic method
      TimestampedBannerTexture(Object var1) {
         this();
      }
   }

   public static class TextureCache {
      private final Map<String, BannerTextures.TimestampedBannerTexture> cache = Maps.newLinkedHashMap();
      private final ResourceLocation baseResource;
      private final String resourceNameBase;
      private final String hashPrefix;

      public TextureCache(String var1, ResourceLocation var2, String var3) {
         super();
         this.hashPrefix = var1;
         this.baseResource = var2;
         this.resourceNameBase = var3;
      }

      @Nullable
      public ResourceLocation getTextureLocation(String var1, List<BannerPattern> var2, List<DyeColor> var3) {
         if (var1.isEmpty()) {
            return null;
         } else if (!var2.isEmpty() && !var3.isEmpty()) {
            var1 = this.hashPrefix + var1;
            BannerTextures.TimestampedBannerTexture var4 = (BannerTextures.TimestampedBannerTexture)this.cache.get(var1);
            if (var4 == null) {
               if (this.cache.size() >= 256 && !this.freeCacheSlot()) {
                  return BannerTextures.DEFAULT_PATTERN_BANNER;
               }

               ArrayList var5 = Lists.newArrayList();
               Iterator var6 = var2.iterator();

               while(var6.hasNext()) {
                  BannerPattern var7 = (BannerPattern)var6.next();
                  var5.add(this.resourceNameBase + var7.getFilename() + ".png");
               }

               var4 = new BannerTextures.TimestampedBannerTexture();
               var4.textureLocation = new ResourceLocation(var1);
               Minecraft.getInstance().getTextureManager().register((ResourceLocation)var4.textureLocation, (TextureObject)(new LayeredColorMaskTexture(this.baseResource, var5, var3)));
               this.cache.put(var1, var4);
            }

            var4.lastUseMilliseconds = Util.getMillis();
            return var4.textureLocation;
         } else {
            return MissingTextureAtlasSprite.getLocation();
         }
      }

      private boolean freeCacheSlot() {
         long var1 = Util.getMillis();
         Iterator var3 = this.cache.keySet().iterator();

         BannerTextures.TimestampedBannerTexture var5;
         do {
            if (!var3.hasNext()) {
               return this.cache.size() < 256;
            }

            String var4 = (String)var3.next();
            var5 = (BannerTextures.TimestampedBannerTexture)this.cache.get(var4);
         } while(var1 - var5.lastUseMilliseconds <= 5000L);

         Minecraft.getInstance().getTextureManager().release(var5.textureLocation);
         var3.remove();
         return true;
      }
   }
}

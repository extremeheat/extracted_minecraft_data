package net.minecraft.client.renderer;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.resource.ResourceHandle;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public class LevelTargetBundle implements PostChain.TargetBundle {
   public static final ResourceLocation MAIN_TARGET_ID;
   public static final ResourceLocation TRANSLUCENT_TARGET_ID;
   public static final ResourceLocation ITEM_ENTITY_TARGET_ID;
   public static final ResourceLocation PARTICLES_TARGET_ID;
   public static final ResourceLocation WEATHER_TARGET_ID;
   public static final ResourceLocation CLOUDS_TARGET_ID;
   public static final ResourceLocation ENTITY_OUTLINE_TARGET_ID;
   public static final Set<ResourceLocation> MAIN_TARGETS;
   public static final Set<ResourceLocation> OUTLINE_TARGETS;
   public static final Set<ResourceLocation> SORTING_TARGETS;
   public ResourceHandle<RenderTarget> main = ResourceHandle.invalid();
   @Nullable
   public ResourceHandle<RenderTarget> translucent;
   @Nullable
   public ResourceHandle<RenderTarget> itemEntity;
   @Nullable
   public ResourceHandle<RenderTarget> particles;
   @Nullable
   public ResourceHandle<RenderTarget> weather;
   @Nullable
   public ResourceHandle<RenderTarget> clouds;
   @Nullable
   public ResourceHandle<RenderTarget> entityOutline;

   public LevelTargetBundle() {
      super();
   }

   public void replace(ResourceLocation var1, ResourceHandle<RenderTarget> var2) {
      if (var1.equals(MAIN_TARGET_ID)) {
         this.main = var2;
      } else if (var1.equals(TRANSLUCENT_TARGET_ID)) {
         this.translucent = var2;
      } else if (var1.equals(ITEM_ENTITY_TARGET_ID)) {
         this.itemEntity = var2;
      } else if (var1.equals(PARTICLES_TARGET_ID)) {
         this.particles = var2;
      } else if (var1.equals(WEATHER_TARGET_ID)) {
         this.weather = var2;
      } else if (var1.equals(CLOUDS_TARGET_ID)) {
         this.clouds = var2;
      } else {
         if (!var1.equals(ENTITY_OUTLINE_TARGET_ID)) {
            throw new IllegalArgumentException("No target with id " + String.valueOf(var1));
         }

         this.entityOutline = var2;
      }

   }

   @Nullable
   public ResourceHandle<RenderTarget> get(ResourceLocation var1) {
      if (var1.equals(MAIN_TARGET_ID)) {
         return this.main;
      } else if (var1.equals(TRANSLUCENT_TARGET_ID)) {
         return this.translucent;
      } else if (var1.equals(ITEM_ENTITY_TARGET_ID)) {
         return this.itemEntity;
      } else if (var1.equals(PARTICLES_TARGET_ID)) {
         return this.particles;
      } else if (var1.equals(WEATHER_TARGET_ID)) {
         return this.weather;
      } else if (var1.equals(CLOUDS_TARGET_ID)) {
         return this.clouds;
      } else {
         return var1.equals(ENTITY_OUTLINE_TARGET_ID) ? this.entityOutline : null;
      }
   }

   public void clear() {
      this.main = ResourceHandle.invalid();
      this.translucent = null;
      this.itemEntity = null;
      this.particles = null;
      this.weather = null;
      this.clouds = null;
      this.entityOutline = null;
   }

   static {
      MAIN_TARGET_ID = PostChain.MAIN_TARGET_ID;
      TRANSLUCENT_TARGET_ID = ResourceLocation.withDefaultNamespace("translucent");
      ITEM_ENTITY_TARGET_ID = ResourceLocation.withDefaultNamespace("item_entity");
      PARTICLES_TARGET_ID = ResourceLocation.withDefaultNamespace("particles");
      WEATHER_TARGET_ID = ResourceLocation.withDefaultNamespace("weather");
      CLOUDS_TARGET_ID = ResourceLocation.withDefaultNamespace("clouds");
      ENTITY_OUTLINE_TARGET_ID = ResourceLocation.withDefaultNamespace("entity_outline");
      MAIN_TARGETS = Set.of(MAIN_TARGET_ID);
      OUTLINE_TARGETS = Set.of(MAIN_TARGET_ID, ENTITY_OUTLINE_TARGET_ID);
      SORTING_TARGETS = Set.of(MAIN_TARGET_ID, TRANSLUCENT_TARGET_ID, ITEM_ENTITY_TARGET_ID, PARTICLES_TARGET_ID, WEATHER_TARGET_ID, CLOUDS_TARGET_ID);
   }
}

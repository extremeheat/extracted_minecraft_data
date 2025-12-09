package net.minecraft.client.renderer;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.resource.ResourceHandle;
import java.util.Set;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public class LevelTargetBundle implements PostChain.TargetBundle {
   public static final Identifier MAIN_TARGET_ID;
   public static final Identifier TRANSLUCENT_TARGET_ID;
   public static final Identifier ITEM_ENTITY_TARGET_ID;
   public static final Identifier PARTICLES_TARGET_ID;
   public static final Identifier WEATHER_TARGET_ID;
   public static final Identifier CLOUDS_TARGET_ID;
   public static final Identifier ENTITY_OUTLINE_TARGET_ID;
   public static final Set<Identifier> MAIN_TARGETS;
   public static final Set<Identifier> OUTLINE_TARGETS;
   public static final Set<Identifier> SORTING_TARGETS;
   public ResourceHandle<RenderTarget> main = ResourceHandle.<RenderTarget>invalid();
   public @Nullable ResourceHandle<RenderTarget> translucent;
   public @Nullable ResourceHandle<RenderTarget> itemEntity;
   public @Nullable ResourceHandle<RenderTarget> particles;
   public @Nullable ResourceHandle<RenderTarget> weather;
   public @Nullable ResourceHandle<RenderTarget> clouds;
   public @Nullable ResourceHandle<RenderTarget> entityOutline;

   public LevelTargetBundle() {
      super();
   }

   public void replace(Identifier var1, ResourceHandle<RenderTarget> var2) {
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

   public @Nullable ResourceHandle<RenderTarget> get(Identifier var1) {
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
      this.main = ResourceHandle.<RenderTarget>invalid();
      this.translucent = null;
      this.itemEntity = null;
      this.particles = null;
      this.weather = null;
      this.clouds = null;
      this.entityOutline = null;
   }

   static {
      MAIN_TARGET_ID = PostChain.MAIN_TARGET_ID;
      TRANSLUCENT_TARGET_ID = Identifier.withDefaultNamespace("translucent");
      ITEM_ENTITY_TARGET_ID = Identifier.withDefaultNamespace("item_entity");
      PARTICLES_TARGET_ID = Identifier.withDefaultNamespace("particles");
      WEATHER_TARGET_ID = Identifier.withDefaultNamespace("weather");
      CLOUDS_TARGET_ID = Identifier.withDefaultNamespace("clouds");
      ENTITY_OUTLINE_TARGET_ID = Identifier.withDefaultNamespace("entity_outline");
      MAIN_TARGETS = Set.of(MAIN_TARGET_ID);
      OUTLINE_TARGETS = Set.of(MAIN_TARGET_ID, ENTITY_OUTLINE_TARGET_ID);
      SORTING_TARGETS = Set.of(MAIN_TARGET_ID, TRANSLUCENT_TARGET_ID, ITEM_ENTITY_TARGET_ID, PARTICLES_TARGET_ID, WEATHER_TARGET_ID, CLOUDS_TARGET_ID);
   }
}

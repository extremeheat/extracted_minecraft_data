package net.minecraft.client.renderer;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.resource.ResourceHandle;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public class LevelTargetBundle implements PostChain.TargetBundle {
   public static final Identifier MAIN_TARGET_ID;
   public static final Identifier ENTITY_OUTLINE_TARGET_ID;
   public static final Set<Identifier> MAIN_TARGETS;
   public static final Set<Identifier> OUTLINE_TARGETS;
   public ResourceHandle<RenderTarget> main = ResourceHandle.<RenderTarget>invalid();
   public ResourceHandle<RenderTarget> depthBounds = ResourceHandle.<RenderTarget>invalid();
   public final List<ResourceHandle<RenderTarget>> transmittance = new ArrayList();
   public ResourceHandle<RenderTarget> accumulate = ResourceHandle.<RenderTarget>invalid();
   public ResourceHandle<RenderTarget> oitCloudDepth = ResourceHandle.<RenderTarget>invalid();
   public ResourceHandle<RenderTarget> oitTerrainWithWaterPatchDepth = ResourceHandle.<RenderTarget>invalid();
   public @Nullable ResourceHandle<RenderTarget> entityOutline;

   public LevelTargetBundle() {
      super();

      for(int i = 0; i < LevelRenderer.OIT_TRANSMITTANCE_TARGET_COUNT; ++i) {
         this.transmittance.add(ResourceHandle.invalid());
      }

   }

   public void replace(final Identifier id, final ResourceHandle<RenderTarget> handle) {
      if (id.equals(MAIN_TARGET_ID)) {
         this.main = handle;
      } else {
         if (!id.equals(ENTITY_OUTLINE_TARGET_ID)) {
            throw new IllegalArgumentException("No target with id " + String.valueOf(id));
         }

         this.entityOutline = handle;
      }

   }

   public @Nullable ResourceHandle<RenderTarget> get(final Identifier id) {
      if (id.equals(MAIN_TARGET_ID)) {
         return this.main;
      } else {
         return id.equals(ENTITY_OUTLINE_TARGET_ID) ? this.entityOutline : null;
      }
   }

   public void clear() {
      this.main = ResourceHandle.<RenderTarget>invalid();
      this.entityOutline = null;
   }

   static {
      MAIN_TARGET_ID = PostChain.MAIN_TARGET_ID;
      ENTITY_OUTLINE_TARGET_ID = Identifier.withDefaultNamespace("entity_outline");
      MAIN_TARGETS = Set.of(MAIN_TARGET_ID);
      OUTLINE_TARGETS = Set.of(MAIN_TARGET_ID, ENTITY_OUTLINE_TARGET_ID);
   }
}

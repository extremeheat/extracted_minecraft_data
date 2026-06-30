package net.minecraft.client.renderer.rendertype;

import com.mojang.blaze3d.pipeline.RenderTarget;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import org.jspecify.annotations.Nullable;

public class OutputTarget {
   private final String name;
   private final Supplier<@Nullable RenderTarget> renderTargetSupplier;
   public static final OutputTarget MAIN_TARGET = new OutputTarget("main_target", () -> Minecraft.getInstance().gameRenderer.mainRenderTarget());
   public static final OutputTarget OUTLINE_TARGET = new OutputTarget("outline_target", () -> Minecraft.getInstance().levelRenderer.entityOutlineTarget());
   public static final OutputTarget TERRAIN_DEPTH_TARGET = new OutputTarget("terrain_depth_target", () -> Minecraft.getInstance().levelRenderer.terrainDepthTarget());
   public static final OutputTarget DEPTH_BOUNDS_TARGET = new OutputTarget("depth_bounds_target", () -> Minecraft.getInstance().levelRenderer.depthBoundsTarget());
   public static final OutputTarget[] TRANSMITTANCE_TARGETS;
   public static final OutputTarget ACCUMULATE_TARGET = new OutputTarget("accumulate_target", () -> Minecraft.getInstance().levelRenderer.accumulateTarget());

   public OutputTarget(final String name, final Supplier<@Nullable RenderTarget> renderTargetSupplier) {
      super();
      this.name = name;
      this.renderTargetSupplier = renderTargetSupplier;
   }

   public RenderTarget getRenderTarget() {
      RenderTarget preferredTarget = (RenderTarget)this.renderTargetSupplier.get();
      return preferredTarget != null ? preferredTarget : Minecraft.getInstance().gameRenderer.mainRenderTarget();
   }

   public String toString() {
      return "OutputTarget[" + this.name + "]";
   }

   static {
      TRANSMITTANCE_TARGETS = new OutputTarget[LevelRenderer.OIT_TRANSMITTANCE_TARGET_COUNT];

      for(int i = 0; i < LevelRenderer.OIT_TRANSMITTANCE_TARGET_COUNT; ++i) {
         TRANSMITTANCE_TARGETS[i] = new OutputTarget("transmittance_target_" + i, () -> Minecraft.getInstance().levelRenderer.transmittanceTarget(i));
      }

   }
}

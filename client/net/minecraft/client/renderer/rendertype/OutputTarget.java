package net.minecraft.client.renderer.rendertype;

import com.mojang.blaze3d.pipeline.RenderTarget;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import org.jspecify.annotations.Nullable;

public class OutputTarget {
   private final String name;
   private final Supplier<@Nullable RenderTarget> renderTargetSupplier;
   public static final OutputTarget MAIN_TARGET = new OutputTarget("main_target", () -> Minecraft.getInstance().getMainRenderTarget());
   public static final OutputTarget OUTLINE_TARGET = new OutputTarget("outline_target", () -> Minecraft.getInstance().levelRenderer.entityOutlineTarget());
   public static final OutputTarget WEATHER_TARGET = new OutputTarget("weather_target", () -> Minecraft.getInstance().levelRenderer.getWeatherTarget());
   public static final OutputTarget ITEM_ENTITY_TARGET = new OutputTarget("item_entity_target", () -> Minecraft.getInstance().levelRenderer.getItemEntityTarget());

   public OutputTarget(String var1, Supplier<@Nullable RenderTarget> var2) {
      super();
      this.name = var1;
      this.renderTargetSupplier = var2;
   }

   public RenderTarget getRenderTarget() {
      RenderTarget var1 = (RenderTarget)this.renderTargetSupplier.get();
      return var1 != null ? var1 : Minecraft.getInstance().getMainRenderTarget();
   }

   public String toString() {
      return "OutputTarget[" + this.name + "]";
   }
}

package net.minecraft.client.renderer.rendertype;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import org.joml.Matrix4fStack;

public class LayeringTransform {
   private final String name;
   @Nullable
   private final Consumer<Matrix4fStack> modifier;
   public static final LayeringTransform NO_LAYERING = new LayeringTransform("no_layering", (Consumer)null);
   public static final LayeringTransform VIEW_OFFSET_Z_LAYERING = new LayeringTransform("view_offset_z_layering", (var0) -> RenderSystem.getProjectionType().applyLayeringTransform(var0, 1.0F));
   public static final LayeringTransform VIEW_OFFSET_Z_LAYERING_FORWARD = new LayeringTransform("view_offset_z_layering_forward", (var0) -> RenderSystem.getProjectionType().applyLayeringTransform(var0, -1.0F));

   public LayeringTransform(String var1, @Nullable Consumer<Matrix4fStack> var2) {
      super();
      this.name = var1;
      this.modifier = var2;
   }

   public String toString() {
      return "LayeringTransform[" + this.name + "]";
   }

   @Nullable
   public Consumer<Matrix4fStack> getModifier() {
      return this.modifier;
   }
}

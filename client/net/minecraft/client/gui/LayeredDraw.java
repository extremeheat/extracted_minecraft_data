package net.minecraft.client.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BooleanSupplier;

public class LayeredDraw {
   public static final float Z_SEPARATION = 200.0F;
   private final List<Layer> layers = new ArrayList();

   public LayeredDraw() {
      super();
   }

   public LayeredDraw add(Layer var1) {
      this.layers.add(var1);
      return this;
   }

   public LayeredDraw add(LayeredDraw var1, BooleanSupplier var2) {
      return this.add((var2x, var3) -> {
         if (var2.getAsBoolean()) {
            var1.renderInner(var2x, var3);
         }

      });
   }

   public void render(GuiGraphics var1, float var2) {
      var1.pose().pushPose();
      this.renderInner(var1, var2);
      var1.pose().popPose();
   }

   private void renderInner(GuiGraphics var1, float var2) {
      Iterator var3 = this.layers.iterator();

      while(var3.hasNext()) {
         Layer var4 = (Layer)var3.next();
         var4.render(var1, var2);
         var1.pose().translate(0.0F, 0.0F, 200.0F);
      }

   }

   public interface Layer {
      void render(GuiGraphics var1, float var2);
   }
}

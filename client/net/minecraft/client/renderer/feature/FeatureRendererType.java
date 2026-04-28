package net.minecraft.client.renderer.feature;

import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.renderer.feature.submit.SubmitNode;

public record FeatureRendererType<Submit extends SubmitNode>(int id, String name) {
   private static final AtomicInteger NEXT_ID = new AtomicInteger();

   /** @deprecated */
   @Deprecated
   public FeatureRendererType(int id, String name) {
      super();
      this.id = id;
      this.name = name;
   }

   public static <Submit extends SubmitNode> FeatureRendererType<Submit> create(final String name) {
      return new FeatureRendererType<Submit>(NEXT_ID.getAndIncrement(), name);
   }

   public String toString() {
      return this.name;
   }
}

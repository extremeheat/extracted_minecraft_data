package net.minecraft.client.renderer.pipeline.variant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VariantValuesList<T> {
   private final List<T> values = new ArrayList();

   public VariantValuesList() {
      super();
   }

   public T add(final T variant) {
      this.values.add(variant);
      return variant;
   }

   public List<T> values() {
      return Collections.unmodifiableList(this.values);
   }
}

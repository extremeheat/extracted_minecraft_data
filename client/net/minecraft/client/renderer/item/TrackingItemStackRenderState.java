package net.minecraft.client.renderer.item;

import java.util.ArrayList;
import java.util.List;

public class TrackingItemStackRenderState extends ItemStackRenderState {
   private final List<Object> modelIdentityElements = new ArrayList();

   public TrackingItemStackRenderState() {
      super();
   }

   public void appendModelIdentityElement(final Object element) {
      this.modelIdentityElements.add(element);
   }

   public Object getModelIdentity() {
      return this.modelIdentityElements;
   }
}

package net.minecraft.client.renderer.item;

import java.util.ArrayList;
import java.util.List;

public class TrackingItemStackRenderState extends ItemStackRenderState {
   private final List<Object> modelIdentityElements = new ArrayList();

   public TrackingItemStackRenderState() {
      super();
   }

   public void appendModelIdentityElement(Object var1) {
      this.modelIdentityElements.add(var1);
   }

   public Object getModelIdentity() {
      return this.modelIdentityElements;
   }
}

package net.minecraft.client.renderer.blockentity.state;

import java.util.ArrayList;
import java.util.List;

public class BeaconRenderState extends BlockEntityRenderState {
   public float animationTime;
   public float beamRadiusScale;
   public List<Section> sections = new ArrayList();

   public BeaconRenderState() {
      super();
   }

   public static record Section(int color, int height) {
      public Section {
         super();
      }
   }
}

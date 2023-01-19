package net.minecraft.client.model.geom.builders;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.geom.PartPose;

public class MeshDefinition {
   private PartDefinition root = new PartDefinition(ImmutableList.of(), PartPose.ZERO);

   public MeshDefinition() {
      super();
   }

   public PartDefinition getRoot() {
      return this.root;
   }
}

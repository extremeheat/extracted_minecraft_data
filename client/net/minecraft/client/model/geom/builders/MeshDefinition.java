package net.minecraft.client.model.geom.builders;

import com.google.common.collect.ImmutableList;
import java.util.function.UnaryOperator;
import net.minecraft.client.model.geom.PartPose;

public class MeshDefinition {
   private final PartDefinition root;

   public MeshDefinition() {
      this(new PartDefinition(ImmutableList.of(), PartPose.ZERO));
   }

   private MeshDefinition(PartDefinition var1) {
      super();
      this.root = var1;
   }

   public PartDefinition getRoot() {
      return this.root;
   }

   public MeshDefinition transformed(UnaryOperator<PartPose> var1) {
      return new MeshDefinition(this.root.transformed(var1));
   }
}

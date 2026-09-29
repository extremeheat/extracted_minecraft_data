package net.minecraft.client.model;

import java.util.Map;
import java.util.function.UnaryOperator;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.MeshTransformer;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class ArmorStandModelTransformer implements MeshTransformer {
   public static final MeshTransformer ARMOR_STAND_SMALL = new ArmorStandModelTransformer(false);
   public static final MeshTransformer ARMOR_STAND_SMALL_ARMOR = new ArmorStandModelTransformer(true);
   private static final float HEAD_OFFSET_Y = 16.0F;
   private static final float HEAD_SCALE = 2.0F;
   private static final float SMALL_BODY_SCALE = 2.0F;
   private static final float BODY_OFFSET_Y = 24.0F;
   private static final float HEAD_SCALE_BASE = 1.5F;
   private static final float BODY_SCALE_BASE = 1.0F;
   private final boolean armor;

   private ArmorStandModelTransformer(final boolean armor) {
      super();
      this.armor = armor;
   }

   public MeshDefinition apply(final MeshDefinition mesh) {
      float headScaleXZ = (this.armor ? 1.5F : 1.0F) / 2.0F;
      float headScaleY = 0.75F;
      float bodyScale = 0.5F;
      UnaryOperator<PartPose> headTransform = (p) -> p.translated(0.0F, 16.0F, 0.0F).scaled(headScaleXZ, 0.75F, headScaleXZ);
      UnaryOperator<PartPose> bodyTransform = (p) -> p.translated(0.0F, 24.0F, 0.0F).scaled(0.5F);
      MeshDefinition smallMesh = new MeshDefinition();

      for(Map.Entry<String, PartDefinition> entry : mesh.getRoot().getChildren()) {
         String name = (String)entry.getKey();
         PartDefinition part = (PartDefinition)entry.getValue();
         boolean isHead = "head".equals(name);
         smallMesh.getRoot().addOrReplaceChild(name, part.transformed(isHead ? headTransform : bodyTransform));
      }

      return smallMesh;
   }
}

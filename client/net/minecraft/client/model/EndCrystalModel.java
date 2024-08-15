package net.minecraft.client.model;

import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.EndCrystalRenderer;
import net.minecraft.client.renderer.entity.state.EndCrystalRenderState;
import org.joml.Quaternionf;

public class EndCrystalModel extends EntityModel<EndCrystalRenderState> {
   private static final String OUTER_GLASS = "outer_glass";
   private static final String INNER_GLASS = "inner_glass";
   private static final String BASE = "base";
   private static final float SIN_45 = (float)Math.sin(0.7853981633974483);
   private final ModelPart root;
   public final ModelPart base;
   public final ModelPart outerGlass;
   public final ModelPart innerGlass;
   public final ModelPart cube;

   public EndCrystalModel(ModelPart var1) {
      super();
      this.root = var1;
      this.base = var1.getChild("base");
      this.outerGlass = var1.getChild("outer_glass");
      this.innerGlass = this.outerGlass.getChild("inner_glass");
      this.cube = this.innerGlass.getChild("cube");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      float var2 = 0.875F;
      CubeListBuilder var3 = CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F);
      PartDefinition var4 = var1.addOrReplaceChild("outer_glass", var3, PartPose.offset(0.0F, 24.0F, 0.0F).withScale(0.875F));
      PartDefinition var5 = var4.addOrReplaceChild("inner_glass", var3, PartPose.ZERO.withScale(0.765625F));
      var5.addOrReplaceChild("cube", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.ZERO);
      var1.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 16).addBox(-6.0F, 0.0F, -6.0F, 12.0F, 4.0F, 12.0F), PartPose.ZERO);
      return LayerDefinition.create(var0, 64, 32);
   }

   public void setupAnim(EndCrystalRenderState var1) {
      this.root.getAllParts().forEach(ModelPart::resetPose);
      this.base.visible = var1.showsBottom;
      float var2 = var1.ageInTicks * 3.0F;
      float var3 = EndCrystalRenderer.getY(var1.ageInTicks) * 16.0F;
      this.outerGlass.y += var3 / 2.0F;
      this.outerGlass.rotateBy(Axis.YP.rotationDegrees(var2).rotateAxis(1.0471976F, SIN_45, 0.0F, SIN_45));
      this.innerGlass.rotateBy(new Quaternionf().setAngleAxis(1.0471976F, SIN_45, 0.0F, SIN_45).rotateY(var2 * 0.017453292F));
      this.cube.rotateBy(new Quaternionf().setAngleAxis(1.0471976F, SIN_45, 0.0F, SIN_45).rotateY(var2 * 0.017453292F));
   }

   @Override
   public ModelPart root() {
      return this.root;
   }
}

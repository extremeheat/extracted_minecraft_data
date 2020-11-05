package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Shulker;

public class ShulkerModel<T extends Shulker> extends ListModel<T> {
   private final ModelPart base = new ModelPart(64, 64, 0, 28);
   private final ModelPart lid = new ModelPart(64, 64, 0, 0);
   private final ModelPart head = new ModelPart(64, 64, 0, 52);

   public ShulkerModel() {
      super(RenderType::entityCutoutNoCullZOffset);
      this.lid.addBox(-8.0F, -16.0F, -8.0F, 16.0F, 12.0F, 16.0F);
      this.lid.setPos(0.0F, 24.0F, 0.0F);
      this.base.addBox(-8.0F, -8.0F, -8.0F, 16.0F, 8.0F, 16.0F);
      this.base.setPos(0.0F, 24.0F, 0.0F);
      this.head.addBox(-3.0F, 0.0F, -3.0F, 6.0F, 6.0F, 6.0F);
      this.head.setPos(0.0F, 12.0F, 0.0F);
   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6) {
      float var7 = var4 - (float)var1.tickCount;
      float var8 = (0.5F + var1.getClientPeekAmount(var7)) * 3.1415927F;
      float var9 = -1.0F + Mth.sin(var8);
      float var10 = 0.0F;
      if (var8 > 3.1415927F) {
         var10 = Mth.sin(var4 * 0.1F) * 0.7F;
      }

      this.lid.setPos(0.0F, 16.0F + Mth.sin(var8) * 8.0F + var10, 0.0F);
      if (var1.getClientPeekAmount(var7) > 0.3F) {
         this.lid.yRot = var9 * var9 * var9 * var9 * 3.1415927F * 0.125F;
      } else {
         this.lid.yRot = 0.0F;
      }

      this.head.xRot = var6 * 0.017453292F;
      this.head.yRot = (var1.yHeadRot - 180.0F - var1.yBodyRot) * 0.017453292F;
   }

   public Iterable<ModelPart> parts() {
      return ImmutableList.of(this.base, this.lid);
   }

   public ModelPart getBase() {
      return this.base;
   }

   public ModelPart getLid() {
      return this.lid;
   }

   public ModelPart getHead() {
      return this.head;
   }
}

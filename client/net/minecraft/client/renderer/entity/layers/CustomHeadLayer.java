package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.Map;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.SkullBlock;

public class CustomHeadLayer<S extends LivingEntityRenderState, M extends EntityModel<S> & HeadedModel> extends RenderLayer<S, M> {
   private static final float ITEM_SCALE = 0.625F;
   private static final float SKULL_SCALE = 1.1875F;
   private final CustomHeadLayer.Transforms transforms;
   private final Map<SkullBlock.Type, SkullModelBase> skullModels;
   private final ItemRenderer itemRenderer;

   public CustomHeadLayer(RenderLayerParent<S, M> var1, EntityModelSet var2, ItemRenderer var3) {
      this(var1, var2, CustomHeadLayer.Transforms.DEFAULT, var3);
   }

   public CustomHeadLayer(RenderLayerParent<S, M> var1, EntityModelSet var2, CustomHeadLayer.Transforms var3, ItemRenderer var4) {
      super(var1);
      this.transforms = var3;
      this.skullModels = SkullBlockRenderer.createSkullRenderers(var2);
      this.itemRenderer = var4;
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, S var4, float var5, float var6) {
      ItemStack var7 = var4.headItem;
      BakedModel var8 = var4.headItemModel;
      if (!var7.isEmpty() && var8 != null) {
         label17: {
            Item var9 = var7.getItem();
            var1.pushPose();
            var1.scale(this.transforms.horizontalScale(), 1.0F, this.transforms.horizontalScale());
            EntityModel var10 = this.getParentModel();
            var10.root().translateAndRotate(var1);
            ((HeadedModel)var10).getHead().translateAndRotate(var1);
            if (var9 instanceof BlockItem var11 && var11.getBlock() instanceof AbstractSkullBlock var12) {
               var1.translate(0.0F, this.transforms.skullYOffset(), 0.0F);
               var1.scale(1.1875F, -1.1875F, -1.1875F);
               ResolvableProfile var17 = var7.get(DataComponents.PROFILE);
               var1.translate(-0.5, 0.0, -0.5);
               SkullBlock.Type var14 = var12.getType();
               SkullModelBase var15 = this.skullModels.get(var14);
               RenderType var16 = SkullBlockRenderer.getRenderType(var14, var17);
               SkullBlockRenderer.renderSkull(null, 180.0F, var4.wornHeadAnimationPos, var1, var2, var3, var15, var16);
               break label17;
            }

            if (!HumanoidArmorLayer.shouldRender(var7, EquipmentSlot.HEAD)) {
               translateToHead(var1, this.transforms);
               this.itemRenderer.render(var7, ItemDisplayContext.HEAD, false, var1, var2, var3, OverlayTexture.NO_OVERLAY, var8);
            }
         }

         var1.popPose();
      }
   }

   public static void translateToHead(PoseStack var0, CustomHeadLayer.Transforms var1) {
      var0.translate(0.0F, -0.25F + var1.yOffset(), 0.0F);
      var0.mulPose(Axis.YP.rotationDegrees(180.0F));
      var0.scale(0.625F, -0.625F, -0.625F);
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException: Cannot invoke "String.equals(Object)" because "varName" is null
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}

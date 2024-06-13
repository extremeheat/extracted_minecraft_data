package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.Map;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.WalkAnimationState;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.SkullBlock;

public class CustomHeadLayer<T extends LivingEntity, M extends EntityModel<T> & HeadedModel> extends RenderLayer<T, M> {
   private final float scaleX;
   private final float scaleY;
   private final float scaleZ;
   private final Map<SkullBlock.Type, SkullModelBase> skullModels;
   private final ItemInHandRenderer itemInHandRenderer;

   public CustomHeadLayer(RenderLayerParent<T, M> var1, EntityModelSet var2, ItemInHandRenderer var3) {
      this(var1, var2, 1.0F, 1.0F, 1.0F, var3);
   }

   public CustomHeadLayer(RenderLayerParent<T, M> var1, EntityModelSet var2, float var3, float var4, float var5, ItemInHandRenderer var6) {
      super(var1);
      this.scaleX = var3;
      this.scaleY = var4;
      this.scaleZ = var5;
      this.skullModels = SkullBlockRenderer.createSkullRenderers(var2);
      this.itemInHandRenderer = var6;
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, T var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      ItemStack var11 = var4.getItemBySlot(EquipmentSlot.HEAD);
      if (!var11.isEmpty()) {
         Item var12 = var11.getItem();
         var1.pushPose();
         var1.scale(this.scaleX, this.scaleY, this.scaleZ);
         boolean var13 = var4 instanceof Villager || var4 instanceof ZombieVillager;
         if (var4.isBaby() && !(var4 instanceof Villager)) {
            float var14 = 2.0F;
            float var15 = 1.4F;
            var1.translate(0.0F, 0.03125F, 0.0F);
            var1.scale(0.7F, 0.7F, 0.7F);
            var1.translate(0.0F, 1.0F, 0.0F);
         }

         this.getParentModel().getHead().translateAndRotate(var1);
         if (var12 instanceof BlockItem && ((BlockItem)var12).getBlock() instanceof AbstractSkullBlock) {
            float var24 = 1.1875F;
            var1.scale(1.1875F, -1.1875F, -1.1875F);
            if (var13) {
               var1.translate(0.0F, 0.0625F, 0.0F);
            }

            ResolvableProfile var16 = var11.get(DataComponents.PROFILE);
            var1.translate(-0.5, 0.0, -0.5);
            SkullBlock.Type var17 = ((AbstractSkullBlock)((BlockItem)var12).getBlock()).getType();
            SkullModelBase var18 = this.skullModels.get(var17);
            RenderType var19 = SkullBlockRenderer.getRenderType(var17, var16);
            WalkAnimationState var20;
            if (var4.getVehicle() instanceof LivingEntity var21) {
               var20 = var21.walkAnimation;
            } else {
               var20 = var4.walkAnimation;
            }

            float var25 = var20.position(var7);
            SkullBlockRenderer.renderSkull(null, 180.0F, var25, var1, var2, var3, var18, var19);
         } else if (!(var12 instanceof ArmorItem var23) || var23.getEquipmentSlot() != EquipmentSlot.HEAD) {
            translateToHead(var1, var13);
            this.itemInHandRenderer.renderItem(var4, var11, ItemDisplayContext.HEAD, false, var1, var2, var3);
         }

         var1.popPose();
      }
   }

   public static void translateToHead(PoseStack var0, boolean var1) {
      float var2 = 0.625F;
      var0.translate(0.0F, -0.25F, 0.0F);
      var0.mulPose(Axis.YP.rotationDegrees(180.0F));
      var0.scale(0.625F, -0.625F, -0.625F);
      if (var1) {
         var0.translate(0.0F, 0.1875F, 0.0F);
      }
   }
}

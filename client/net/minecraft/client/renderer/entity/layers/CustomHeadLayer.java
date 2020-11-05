package net.minecraft.client.renderer.entity.layers;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import org.apache.commons.lang3.StringUtils;

public class CustomHeadLayer<T extends LivingEntity, M extends EntityModel<T> & HeadedModel> extends RenderLayer<T, M> {
   private final float scaleX;
   private final float scaleY;
   private final float scaleZ;
   private final Map<SkullBlock.Type, SkullModelBase> skullModels;

   public CustomHeadLayer(RenderLayerParent<T, M> var1, EntityModelSet var2) {
      this(var1, var2, 1.0F, 1.0F, 1.0F);
   }

   public CustomHeadLayer(RenderLayerParent<T, M> var1, EntityModelSet var2, float var3, float var4, float var5) {
      super(var1);
      this.scaleX = var3;
      this.scaleY = var4;
      this.scaleZ = var5;
      this.skullModels = SkullBlockRenderer.createSkullRenderers(var2);
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, T var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      ItemStack var11 = var4.getItemBySlot(EquipmentSlot.HEAD);
      if (!var11.isEmpty()) {
         Item var12 = var11.getItem();
         var1.pushPose();
         var1.scale(this.scaleX, this.scaleY, this.scaleZ);
         boolean var13 = var4 instanceof Villager || var4 instanceof ZombieVillager;
         float var14;
         if (var4.isBaby() && !(var4 instanceof Villager)) {
            var14 = 2.0F;
            float var15 = 1.4F;
            var1.translate(0.0D, 0.03125D, 0.0D);
            var1.scale(0.7F, 0.7F, 0.7F);
            var1.translate(0.0D, 1.0D, 0.0D);
         }

         ((HeadedModel)this.getParentModel()).getHead().translateAndRotate(var1);
         if (var12 instanceof BlockItem && ((BlockItem)var12).getBlock() instanceof AbstractSkullBlock) {
            var14 = 1.1875F;
            var1.scale(1.1875F, -1.1875F, -1.1875F);
            if (var13) {
               var1.translate(0.0D, 0.0625D, 0.0D);
            }

            GameProfile var19 = null;
            if (var11.hasTag()) {
               CompoundTag var16 = var11.getTag();
               if (var16.contains("SkullOwner", 10)) {
                  var19 = NbtUtils.readGameProfile(var16.getCompound("SkullOwner"));
               } else if (var16.contains("SkullOwner", 8)) {
                  String var17 = var16.getString("SkullOwner");
                  if (!StringUtils.isBlank(var17)) {
                     var19 = SkullBlockEntity.updateGameprofile(new GameProfile((UUID)null, var17));
                     var16.put("SkullOwner", NbtUtils.writeGameProfile(new CompoundTag(), var19));
                  }
               }
            }

            var1.translate(-0.5D, 0.0D, -0.5D);
            SkullBlock.Type var20 = ((AbstractSkullBlock)((BlockItem)var12).getBlock()).getType();
            SkullModelBase var21 = (SkullModelBase)this.skullModels.get(var20);
            RenderType var18 = SkullBlockRenderer.getRenderType(var20, var19);
            SkullBlockRenderer.renderSkull((Direction)null, 180.0F, var5, var1, var2, var3, var21, var18);
         } else if (!(var12 instanceof ArmorItem) || ((ArmorItem)var12).getSlot() != EquipmentSlot.HEAD) {
            var14 = 0.625F;
            var1.translate(0.0D, -0.25D, 0.0D);
            var1.mulPose(Vector3f.YP.rotationDegrees(180.0F));
            var1.scale(0.625F, -0.625F, -0.625F);
            if (var13) {
               var1.translate(0.0D, 0.1875D, 0.0D);
            }

            Minecraft.getInstance().getItemInHandRenderer().renderItem(var4, var11, ItemTransforms.TransformType.HEAD, false, var1, var2, var3);
         }

         var1.popPose();
      }
   }
}

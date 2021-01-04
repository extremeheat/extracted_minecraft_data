package net.minecraft.client.renderer.entity.layers;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.entity.HeadedModel;
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
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import org.apache.commons.lang3.StringUtils;

public class CustomHeadLayer<T extends LivingEntity, M extends EntityModel<T> & HeadedModel> extends RenderLayer<T, M> {
   public CustomHeadLayer(RenderLayerParent<T, M> var1) {
      super(var1);
   }

   public void render(T var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      ItemStack var9 = var1.getItemBySlot(EquipmentSlot.HEAD);
      if (!var9.isEmpty()) {
         Item var10 = var9.getItem();
         GlStateManager.pushMatrix();
         if (var1.isVisuallySneaking()) {
            GlStateManager.translatef(0.0F, 0.2F, 0.0F);
         }

         boolean var11 = var1 instanceof Villager || var1 instanceof ZombieVillager;
         float var12;
         if (var1.isBaby() && !(var1 instanceof Villager)) {
            var12 = 2.0F;
            float var13 = 1.4F;
            GlStateManager.translatef(0.0F, 0.5F * var8, 0.0F);
            GlStateManager.scalef(0.7F, 0.7F, 0.7F);
            GlStateManager.translatef(0.0F, 16.0F * var8, 0.0F);
         }

         ((HeadedModel)this.getParentModel()).translateToHead(0.0625F);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         if (var10 instanceof BlockItem && ((BlockItem)var10).getBlock() instanceof AbstractSkullBlock) {
            var12 = 1.1875F;
            GlStateManager.scalef(1.1875F, -1.1875F, -1.1875F);
            if (var11) {
               GlStateManager.translatef(0.0F, 0.0625F, 0.0F);
            }

            GameProfile var16 = null;
            if (var9.hasTag()) {
               CompoundTag var14 = var9.getTag();
               if (var14.contains("SkullOwner", 10)) {
                  var16 = NbtUtils.readGameProfile(var14.getCompound("SkullOwner"));
               } else if (var14.contains("SkullOwner", 8)) {
                  String var15 = var14.getString("SkullOwner");
                  if (!StringUtils.isBlank(var15)) {
                     var16 = SkullBlockEntity.updateGameprofile(new GameProfile((UUID)null, var15));
                     var14.put("SkullOwner", NbtUtils.writeGameProfile(new CompoundTag(), var16));
                  }
               }
            }

            SkullBlockRenderer.instance.renderSkull(-0.5F, 0.0F, -0.5F, (Direction)null, 180.0F, ((AbstractSkullBlock)((BlockItem)var10).getBlock()).getType(), var16, -1, var2);
         } else if (!(var10 instanceof ArmorItem) || ((ArmorItem)var10).getSlot() != EquipmentSlot.HEAD) {
            var12 = 0.625F;
            GlStateManager.translatef(0.0F, -0.25F, 0.0F);
            GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.scalef(0.625F, -0.625F, -0.625F);
            if (var11) {
               GlStateManager.translatef(0.0F, 0.1875F, 0.0F);
            }

            Minecraft.getInstance().getItemInHandRenderer().renderItem(var1, var9, ItemTransforms.TransformType.HEAD);
         }

         GlStateManager.popMatrix();
      }
   }

   public boolean colorsOnDamage() {
      return false;
   }
}

package net.minecraft.client.renderer;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Arrays;
import java.util.Comparator;
import java.util.UUID;
import net.minecraft.client.model.ShieldModel;
import net.minecraft.client.model.TridentModel;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.block.AbstractBannerBlock;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BedBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.ConduitBlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.entity.TrappedChestBlockEntity;
import org.apache.commons.lang3.StringUtils;

public class BlockEntityWithoutLevelRenderer {
   private static final ShulkerBoxBlockEntity[] SHULKER_BOXES = (ShulkerBoxBlockEntity[])Arrays.stream(DyeColor.values()).sorted(Comparator.comparingInt(DyeColor::getId)).map(ShulkerBoxBlockEntity::new).toArray((var0) -> {
      return new ShulkerBoxBlockEntity[var0];
   });
   private static final ShulkerBoxBlockEntity DEFAULT_SHULKER_BOX = new ShulkerBoxBlockEntity((DyeColor)null);
   public static final BlockEntityWithoutLevelRenderer instance = new BlockEntityWithoutLevelRenderer();
   private final ChestBlockEntity chest = new ChestBlockEntity();
   private final ChestBlockEntity trappedChest = new TrappedChestBlockEntity();
   private final EnderChestBlockEntity enderChest = new EnderChestBlockEntity();
   private final BannerBlockEntity banner = new BannerBlockEntity();
   private final BedBlockEntity bed = new BedBlockEntity();
   private final ConduitBlockEntity conduit = new ConduitBlockEntity();
   private final ShieldModel shieldModel = new ShieldModel();
   private final TridentModel tridentModel = new TridentModel();

   public void renderByItem(ItemStack var1, PoseStack var2, MultiBufferSource var3, int var4, int var5) {
      Item var6 = var1.getItem();
      if (var6 instanceof BlockItem) {
         Block var11 = ((BlockItem)var6).getBlock();
         if (var11 instanceof AbstractSkullBlock) {
            GameProfile var15 = null;
            if (var1.hasTag()) {
               CompoundTag var14 = var1.getTag();
               if (var14.contains("SkullOwner", 10)) {
                  var15 = NbtUtils.readGameProfile(var14.getCompound("SkullOwner"));
               } else if (var14.contains("SkullOwner", 8) && !StringUtils.isBlank(var14.getString("SkullOwner"))) {
                  var15 = new GameProfile((UUID)null, var14.getString("SkullOwner"));
                  var15 = SkullBlockEntity.updateGameprofile(var15);
                  var14.remove("SkullOwner");
                  var14.put("SkullOwner", NbtUtils.writeGameProfile(new CompoundTag(), var15));
               }
            }

            SkullBlockRenderer.renderSkull((Direction)null, 180.0F, ((AbstractSkullBlock)var11).getType(), var15, 0.0F, var2, var3, var4);
         } else {
            Object var12;
            if (var11 instanceof AbstractBannerBlock) {
               this.banner.fromItem(var1, ((AbstractBannerBlock)var11).getColor());
               var12 = this.banner;
            } else if (var11 instanceof BedBlock) {
               this.bed.setColor(((BedBlock)var11).getColor());
               var12 = this.bed;
            } else if (var11 == Blocks.CONDUIT) {
               var12 = this.conduit;
            } else if (var11 == Blocks.CHEST) {
               var12 = this.chest;
            } else if (var11 == Blocks.ENDER_CHEST) {
               var12 = this.enderChest;
            } else if (var11 == Blocks.TRAPPED_CHEST) {
               var12 = this.trappedChest;
            } else {
               if (!(var11 instanceof ShulkerBoxBlock)) {
                  return;
               }

               DyeColor var13 = ShulkerBoxBlock.getColorFromItem(var6);
               if (var13 == null) {
                  var12 = DEFAULT_SHULKER_BOX;
               } else {
                  var12 = SHULKER_BOXES[var13.getId()];
               }
            }

            BlockEntityRenderDispatcher.instance.renderItem((BlockEntity)var12, var2, var3, var4, var5);
         }
      } else {
         if (var6 == Items.SHIELD) {
            boolean var7 = var1.getTagElement("BlockEntityTag") != null;
            var2.pushPose();
            var2.scale(1.0F, -1.0F, -1.0F);
            Material var8 = var7 ? ModelBakery.SHIELD_BASE : ModelBakery.NO_PATTERN_SHIELD;
            VertexConsumer var9 = var8.sprite().wrap(ItemRenderer.getFoilBuffer(var3, this.shieldModel.renderType(var8.atlasLocation()), false, var1.hasFoil()));
            this.shieldModel.handle().render(var2, var9, var4, var5, 1.0F, 1.0F, 1.0F, 1.0F);
            if (var7) {
               this.banner.fromItem(var1, ShieldItem.getColor(var1));
               BannerRenderer.renderPatterns(this.banner, var2, var3, var4, var5, this.shieldModel.plate(), var8, false);
            } else {
               this.shieldModel.plate().render(var2, var9, var4, var5, 1.0F, 1.0F, 1.0F, 1.0F);
            }

            var2.popPose();
         } else if (var6 == Items.TRIDENT) {
            var2.pushPose();
            var2.scale(1.0F, -1.0F, -1.0F);
            VertexConsumer var10 = ItemRenderer.getFoilBuffer(var3, this.tridentModel.renderType(TridentModel.TEXTURE), false, var1.hasFoil());
            this.tridentModel.renderToBuffer(var2, var10, var4, var5, 1.0F, 1.0F, 1.0F, 1.0F);
            var2.popPose();
         }

      }
   }
}

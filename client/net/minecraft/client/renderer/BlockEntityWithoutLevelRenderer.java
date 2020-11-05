package net.minecraft.client.renderer;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import net.minecraft.client.model.ShieldModel;
import net.minecraft.client.model.TridentModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
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

   public BlockEntityWithoutLevelRenderer() {
      super();
   }

   public void renderByItem(ItemStack var1, ItemTransforms.TransformType var2, PoseStack var3, MultiBufferSource var4, int var5, int var6) {
      Item var7 = var1.getItem();
      if (var7 instanceof BlockItem) {
         Block var13 = ((BlockItem)var7).getBlock();
         if (var13 instanceof AbstractSkullBlock) {
            GameProfile var17 = null;
            if (var1.hasTag()) {
               CompoundTag var16 = var1.getTag();
               if (var16.contains("SkullOwner", 10)) {
                  var17 = NbtUtils.readGameProfile(var16.getCompound("SkullOwner"));
               } else if (var16.contains("SkullOwner", 8) && !StringUtils.isBlank(var16.getString("SkullOwner"))) {
                  var17 = new GameProfile((UUID)null, var16.getString("SkullOwner"));
                  var17 = SkullBlockEntity.updateGameprofile(var17);
                  var16.remove("SkullOwner");
                  var16.put("SkullOwner", NbtUtils.writeGameProfile(new CompoundTag(), var17));
               }
            }

            SkullBlockRenderer.renderSkull((Direction)null, 180.0F, ((AbstractSkullBlock)var13).getType(), var17, 0.0F, var3, var4, var5);
         } else {
            Object var14;
            if (var13 instanceof AbstractBannerBlock) {
               this.banner.fromItem(var1, ((AbstractBannerBlock)var13).getColor());
               var14 = this.banner;
            } else if (var13 instanceof BedBlock) {
               this.bed.setColor(((BedBlock)var13).getColor());
               var14 = this.bed;
            } else if (var13 == Blocks.CONDUIT) {
               var14 = this.conduit;
            } else if (var13 == Blocks.CHEST) {
               var14 = this.chest;
            } else if (var13 == Blocks.ENDER_CHEST) {
               var14 = this.enderChest;
            } else if (var13 == Blocks.TRAPPED_CHEST) {
               var14 = this.trappedChest;
            } else {
               if (!(var13 instanceof ShulkerBoxBlock)) {
                  return;
               }

               DyeColor var15 = ShulkerBoxBlock.getColorFromItem(var7);
               if (var15 == null) {
                  var14 = DEFAULT_SHULKER_BOX;
               } else {
                  var14 = SHULKER_BOXES[var15.getId()];
               }
            }

            BlockEntityRenderDispatcher.instance.renderItem((BlockEntity)var14, var3, var4, var5, var6);
         }
      } else {
         if (var7 == Items.SHIELD) {
            boolean var8 = var1.getTagElement("BlockEntityTag") != null;
            var3.pushPose();
            var3.scale(1.0F, -1.0F, -1.0F);
            Material var9 = var8 ? ModelBakery.SHIELD_BASE : ModelBakery.NO_PATTERN_SHIELD;
            VertexConsumer var10 = var9.sprite().wrap(ItemRenderer.getFoilBufferDirect(var4, this.shieldModel.renderType(var9.atlasLocation()), true, var1.hasFoil()));
            this.shieldModel.handle().render(var3, var10, var5, var6, 1.0F, 1.0F, 1.0F, 1.0F);
            if (var8) {
               List var11 = BannerBlockEntity.createPatterns(ShieldItem.getColor(var1), BannerBlockEntity.getItemPatterns(var1));
               BannerRenderer.renderPatterns(var3, var4, var5, var6, this.shieldModel.plate(), var9, false, var11, var1.hasFoil());
            } else {
               this.shieldModel.plate().render(var3, var10, var5, var6, 1.0F, 1.0F, 1.0F, 1.0F);
            }

            var3.popPose();
         } else if (var7 == Items.TRIDENT) {
            var3.pushPose();
            var3.scale(1.0F, -1.0F, -1.0F);
            VertexConsumer var12 = ItemRenderer.getFoilBufferDirect(var4, this.tridentModel.renderType(TridentModel.TEXTURE), false, var1.hasFoil());
            this.tridentModel.renderToBuffer(var3, var12, var5, var6, 1.0F, 1.0F, 1.0F, 1.0F);
            var3.popPose();
         }

      }
   }
}

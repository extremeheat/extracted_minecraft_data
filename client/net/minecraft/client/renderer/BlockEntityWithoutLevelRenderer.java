package net.minecraft.client.renderer;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.model.ShieldModel;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.TridentModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
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
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BedBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.ConduitBlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.entity.TrappedChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.StringUtils;

public class BlockEntityWithoutLevelRenderer implements ResourceManagerReloadListener {
   private static final ShulkerBoxBlockEntity[] SHULKER_BOXES = (ShulkerBoxBlockEntity[])Arrays.stream(DyeColor.values()).sorted(Comparator.comparingInt(DyeColor::getId)).map((var0) -> {
      return new ShulkerBoxBlockEntity(var0, BlockPos.ZERO, Blocks.SHULKER_BOX.defaultBlockState());
   }).toArray((var0) -> {
      return new ShulkerBoxBlockEntity[var0];
   });
   private static final ShulkerBoxBlockEntity DEFAULT_SHULKER_BOX;
   private final ChestBlockEntity chest;
   private final ChestBlockEntity trappedChest;
   private final EnderChestBlockEntity enderChest;
   private final BannerBlockEntity banner;
   private final BedBlockEntity bed;
   private final ConduitBlockEntity conduit;
   private ShieldModel shieldModel;
   private TridentModel tridentModel;
   private Map<SkullBlock.Type, SkullModelBase> skullModels;
   private final BlockEntityRenderDispatcher blockEntityRenderDispatcher;
   private final EntityModelSet entityModelSet;

   public BlockEntityWithoutLevelRenderer(BlockEntityRenderDispatcher var1, EntityModelSet var2) {
      super();
      this.chest = new ChestBlockEntity(BlockPos.ZERO, Blocks.CHEST.defaultBlockState());
      this.trappedChest = new TrappedChestBlockEntity(BlockPos.ZERO, Blocks.TRAPPED_CHEST.defaultBlockState());
      this.enderChest = new EnderChestBlockEntity(BlockPos.ZERO, Blocks.ENDER_CHEST.defaultBlockState());
      this.banner = new BannerBlockEntity(BlockPos.ZERO, Blocks.WHITE_BANNER.defaultBlockState());
      this.bed = new BedBlockEntity(BlockPos.ZERO, Blocks.RED_BED.defaultBlockState());
      this.conduit = new ConduitBlockEntity(BlockPos.ZERO, Blocks.CONDUIT.defaultBlockState());
      this.blockEntityRenderDispatcher = var1;
      this.entityModelSet = var2;
   }

   public void onResourceManagerReload(ResourceManager var1) {
      this.shieldModel = new ShieldModel(this.entityModelSet.bakeLayer(ModelLayers.SHIELD));
      this.tridentModel = new TridentModel(this.entityModelSet.bakeLayer(ModelLayers.TRIDENT));
      this.skullModels = SkullBlockRenderer.createSkullRenderers(this.entityModelSet);
   }

   public void renderByItem(ItemStack var1, ItemTransforms.TransformType var2, PoseStack var3, MultiBufferSource var4, int var5, int var6) {
      Item var7 = var1.getItem();
      if (var7 instanceof BlockItem) {
         Block var14 = ((BlockItem)var7).getBlock();
         if (var14 instanceof AbstractSkullBlock) {
            GameProfile var21 = null;
            if (var1.hasTag()) {
               CompoundTag var17 = var1.getTag();
               if (var17.contains("SkullOwner", 10)) {
                  var21 = NbtUtils.readGameProfile(var17.getCompound("SkullOwner"));
               } else if (var17.contains("SkullOwner", 8) && !StringUtils.isBlank(var17.getString("SkullOwner"))) {
                  var21 = new GameProfile((UUID)null, var17.getString("SkullOwner"));
                  var17.remove("SkullOwner");
                  SkullBlockEntity.updateGameprofile(var21, (var1x) -> {
                     var17.put("SkullOwner", NbtUtils.writeGameProfile(new CompoundTag(), var1x));
                  });
               }
            }

            SkullBlock.Type var18 = ((AbstractSkullBlock)var14).getType();
            SkullModelBase var20 = (SkullModelBase)this.skullModels.get(var18);
            RenderType var12 = SkullBlockRenderer.getRenderType(var18, var21);
            SkullBlockRenderer.renderSkull((Direction)null, 180.0F, 0.0F, var3, var4, var5, var20, var12);
         } else {
            BlockState var16 = var14.defaultBlockState();
            Object var15;
            if (var14 instanceof AbstractBannerBlock) {
               this.banner.fromItem(var1, ((AbstractBannerBlock)var14).getColor());
               var15 = this.banner;
            } else if (var14 instanceof BedBlock) {
               this.bed.setColor(((BedBlock)var14).getColor());
               var15 = this.bed;
            } else if (var16.is(Blocks.CONDUIT)) {
               var15 = this.conduit;
            } else if (var16.is(Blocks.CHEST)) {
               var15 = this.chest;
            } else if (var16.is(Blocks.ENDER_CHEST)) {
               var15 = this.enderChest;
            } else if (var16.is(Blocks.TRAPPED_CHEST)) {
               var15 = this.trappedChest;
            } else {
               if (!(var14 instanceof ShulkerBoxBlock)) {
                  return;
               }

               DyeColor var19 = ShulkerBoxBlock.getColorFromItem(var7);
               if (var19 == null) {
                  var15 = DEFAULT_SHULKER_BOX;
               } else {
                  var15 = SHULKER_BOXES[var19.getId()];
               }
            }

            this.blockEntityRenderDispatcher.renderItem((BlockEntity)var15, var3, var4, var5, var6);
         }
      } else {
         if (var1.is(Items.SHIELD)) {
            boolean var8 = BlockItem.getBlockEntityData(var1) != null;
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
         } else if (var1.is(Items.TRIDENT)) {
            var3.pushPose();
            var3.scale(1.0F, -1.0F, -1.0F);
            VertexConsumer var13 = ItemRenderer.getFoilBufferDirect(var4, this.tridentModel.renderType(TridentModel.TEXTURE), false, var1.hasFoil());
            this.tridentModel.renderToBuffer(var3, var13, var5, var6, 1.0F, 1.0F, 1.0F, 1.0F);
            var3.popPose();
         }

      }
   }

   static {
      DEFAULT_SHULKER_BOX = new ShulkerBoxBlockEntity(BlockPos.ZERO, Blocks.SHULKER_BOX.defaultBlockState());
   }
}

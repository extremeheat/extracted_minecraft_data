package net.minecraft.client.renderer;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import net.minecraft.client.model.ShieldModel;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.TridentModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
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
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.entity.TrappedChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEntityWithoutLevelRenderer implements ResourceManagerReloadListener {
   private static final ShulkerBoxBlockEntity[] SHULKER_BOXES = Arrays.stream(DyeColor.values())
      .sorted(Comparator.comparingInt(DyeColor::getId))
      .map(var0 -> new ShulkerBoxBlockEntity(var0, BlockPos.ZERO, Blocks.SHULKER_BOX.defaultBlockState()))
      .toArray(var0 -> new ShulkerBoxBlockEntity[var0]);
   private static final ShulkerBoxBlockEntity DEFAULT_SHULKER_BOX = new ShulkerBoxBlockEntity(BlockPos.ZERO, Blocks.SHULKER_BOX.defaultBlockState());
   private final ChestBlockEntity chest = new ChestBlockEntity(BlockPos.ZERO, Blocks.CHEST.defaultBlockState());
   private final ChestBlockEntity trappedChest = new TrappedChestBlockEntity(BlockPos.ZERO, Blocks.TRAPPED_CHEST.defaultBlockState());
   private final EnderChestBlockEntity enderChest = new EnderChestBlockEntity(BlockPos.ZERO, Blocks.ENDER_CHEST.defaultBlockState());
   private final BannerBlockEntity banner = new BannerBlockEntity(BlockPos.ZERO, Blocks.WHITE_BANNER.defaultBlockState());
   private final BedBlockEntity bed = new BedBlockEntity(BlockPos.ZERO, Blocks.RED_BED.defaultBlockState());
   private final ConduitBlockEntity conduit = new ConduitBlockEntity(BlockPos.ZERO, Blocks.CONDUIT.defaultBlockState());
   private final DecoratedPotBlockEntity decoratedPot = new DecoratedPotBlockEntity(BlockPos.ZERO, Blocks.DECORATED_POT.defaultBlockState());
   private ShieldModel shieldModel;
   private TridentModel tridentModel;
   private Map<SkullBlock.Type, SkullModelBase> skullModels;
   private final BlockEntityRenderDispatcher blockEntityRenderDispatcher;
   private final EntityModelSet entityModelSet;

   public BlockEntityWithoutLevelRenderer(BlockEntityRenderDispatcher var1, EntityModelSet var2) {
      super();
      this.blockEntityRenderDispatcher = var1;
      this.entityModelSet = var2;
   }

   @Override
   public void onResourceManagerReload(ResourceManager var1) {
      this.shieldModel = new ShieldModel(this.entityModelSet.bakeLayer(ModelLayers.SHIELD));
      this.tridentModel = new TridentModel(this.entityModelSet.bakeLayer(ModelLayers.TRIDENT));
      this.skullModels = SkullBlockRenderer.createSkullRenderers(this.entityModelSet);
   }

   // $QF: Could not properly define all variable types!
   // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void renderByItem(ItemStack var1, ItemDisplayContext var2, PoseStack var3, MultiBufferSource var4, int var5, int var6) {
      Item var7 = var1.getItem();
      if (var7 instanceof BlockItem) {
         Block var15 = ((BlockItem)var7).getBlock();
         if (var15 instanceof AbstractSkullBlock var17) {
            CompoundTag var19 = var1.getTag();
            GameProfile var21 = var19 != null ? SkullBlockEntity.getOrResolveGameProfile(var19) : null;
            SkullModelBase var12 = this.skullModels.get(var17.getType());
            RenderType var13 = SkullBlockRenderer.getRenderType(var17.getType(), var21);
            SkullBlockRenderer.renderSkull(null, 180.0F, 0.0F, var3, var4, var5, var12, var13);
         } else {
            BlockState var18 = var15.defaultBlockState();
            Object var16;
            if (var15 instanceof AbstractBannerBlock) {
               this.banner.fromItem(var1, ((AbstractBannerBlock)var15).getColor());
               var16 = this.banner;
            } else if (var15 instanceof BedBlock) {
               this.bed.setColor(((BedBlock)var15).getColor());
               var16 = this.bed;
            } else if (var18.is(Blocks.CONDUIT)) {
               var16 = this.conduit;
            } else if (var18.is(Blocks.CHEST)) {
               var16 = this.chest;
            } else if (var18.is(Blocks.ENDER_CHEST)) {
               var16 = this.enderChest;
            } else if (var18.is(Blocks.TRAPPED_CHEST)) {
               var16 = this.trappedChest;
            } else if (var18.is(Blocks.DECORATED_POT)) {
               this.decoratedPot.setFromItem(var1);
               var16 = this.decoratedPot;
            } else {
               if (!(var15 instanceof ShulkerBoxBlock)) {
                  return;
               }

               DyeColor var20 = ShulkerBoxBlock.getColorFromItem(var7);
               if (var20 == null) {
                  var16 = DEFAULT_SHULKER_BOX;
               } else {
                  var16 = SHULKER_BOXES[var20.getId()];
               }
            }

            this.blockEntityRenderDispatcher.renderItem((BlockEntity)var16, var3, var4, var5, var6);
         }
      } else {
         if (var1.is(Items.SHIELD)) {
            boolean var8 = BlockItem.getBlockEntityData(var1) != null;
            var3.pushPose();
            var3.scale(1.0F, -1.0F, -1.0F);
            Material var9 = var8 ? ModelBakery.SHIELD_BASE : ModelBakery.NO_PATTERN_SHIELD;
            VertexConsumer var10 = var9.sprite()
               .wrap(ItemRenderer.getFoilBufferDirect(var4, this.shieldModel.renderType(var9.atlasLocation()), true, var1.hasFoil()));
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
            VertexConsumer var14 = ItemRenderer.getFoilBufferDirect(var4, this.tridentModel.renderType(TridentModel.TEXTURE), false, var1.hasFoil());
            this.tridentModel.renderToBuffer(var3, var14, var5, var6, 1.0F, 1.0F, 1.0F, 1.0F);
            var3.popPose();
         }
      }
   }
}

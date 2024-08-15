package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import net.minecraft.client.Minecraft;
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
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.AbstractBannerBlock;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.entity.BedBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.ConduitBlockEntity;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.entity.TrappedChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEntityWithoutLevelRenderer implements ResourceManagerReloadListener {
   private static final ShulkerBoxBlockEntity[] SHULKER_BOXES = Arrays.stream(DyeColor.values())
      .sorted(Comparator.comparingInt(DyeColor::getId))
      .map(var0 -> new ShulkerBoxBlockEntity(var0, BlockPos.ZERO, Blocks.SHULKER_BOX.defaultBlockState()))
      .toArray(ShulkerBoxBlockEntity[]::new);
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

   public void renderByItem(ItemStack var1, ItemDisplayContext var2, PoseStack var3, MultiBufferSource var4, int var5, int var6) {
      Item var7 = var1.getItem();
      if (var7 instanceof BlockItem) {
         Block var14 = ((BlockItem)var7).getBlock();
         if (var14 instanceof AbstractSkullBlock var16) {
            ResolvableProfile var18 = var1.get(DataComponents.PROFILE);
            if (var18 != null && !var18.isResolved()) {
               var1.remove(DataComponents.PROFILE);
               var18.resolve().thenAcceptAsync(var1x -> var1.set(DataComponents.PROFILE, var1x), Minecraft.getInstance());
               var18 = null;
            }

            SkullModelBase var20 = this.skullModels.get(var16.getType());
            RenderType var21 = SkullBlockRenderer.getRenderType(var16.getType(), var18);
            SkullBlockRenderer.renderSkull(null, 180.0F, 0.0F, var3, var4, var5, var20, var21);
         } else {
            BlockState var17 = var14.defaultBlockState();
            Object var15;
            if (var14 instanceof AbstractBannerBlock) {
               this.banner.fromItem(var1, ((AbstractBannerBlock)var14).getColor());
               var15 = this.banner;
            } else if (var14 instanceof BedBlock) {
               this.bed.setColor(((BedBlock)var14).getColor());
               var15 = this.bed;
            } else if (var17.is(Blocks.CONDUIT)) {
               var15 = this.conduit;
            } else if (var17.is(Blocks.CHEST)) {
               var15 = this.chest;
            } else if (var17.is(Blocks.ENDER_CHEST)) {
               var15 = this.enderChest;
            } else if (var17.is(Blocks.TRAPPED_CHEST)) {
               var15 = this.trappedChest;
            } else if (var17.is(Blocks.DECORATED_POT)) {
               this.decoratedPot.setFromItem(var1);
               var15 = this.decoratedPot;
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
            BannerPatternLayers var8 = var1.getOrDefault(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY);
            DyeColor var9 = var1.get(DataComponents.BASE_COLOR);
            boolean var10 = !var8.layers().isEmpty() || var9 != null;
            var3.pushPose();
            var3.scale(1.0F, -1.0F, -1.0F);
            Material var11 = var10 ? ModelBakery.SHIELD_BASE : ModelBakery.NO_PATTERN_SHIELD;
            VertexConsumer var12 = var11.sprite()
               .wrap(ItemRenderer.getFoilBufferDirect(var4, this.shieldModel.renderType(var11.atlasLocation()), false, var1.hasFoil()));
            this.shieldModel.handle().render(var3, var12, var5, var6);
            if (var10) {
               BannerRenderer.renderPatterns(
                  var3, var4, var5, var6, this.shieldModel.plate(), var11, false, Objects.requireNonNullElse(var9, DyeColor.WHITE), var8, var1.hasFoil(), false
               );
            } else {
               this.shieldModel.plate().render(var3, var12, var5, var6);
            }

            var3.popPose();
         } else if (var1.is(Items.TRIDENT)) {
            var3.pushPose();
            var3.scale(1.0F, -1.0F, -1.0F);
            VertexConsumer var13 = ItemRenderer.getFoilBufferDirect(var4, this.tridentModel.renderType(TridentModel.TEXTURE), false, var1.hasFoil());
            this.tridentModel.renderToBuffer(var3, var13, var5, var6);
            var3.popPose();
         }
      }
   }
}

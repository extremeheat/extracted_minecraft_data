package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.ColorLerper;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.entity.state.LivingBlockRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.entity.livingblock.LivingBlockGroup;
import net.minecraft.world.entity.livingblock.movement.BouncingMovement;
import net.minecraft.world.entity.livingblock.movement.RollingMovement;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public class LivingBlockRenderer<T extends LivingBlock> extends EntityRenderer<T, LivingBlockRenderState> {
   public static final int SELECTED_COLOR = -1;
   public static final int OTHER_PLAYER_COLOR = -8355712;
   public static final BlockDisplayContext BLOCK_DISPLAY_CONTEXT = BlockDisplayContext.create();
   private final ItemModelResolver itemModelResolver;
   protected final BlockModelResolver blockModelResolver;

   public LivingBlockRenderer(final EntityRendererProvider.Context context) {
      super(context);
      this.itemModelResolver = context.getItemModelResolver();
      this.blockModelResolver = context.getBlockModelResolver();
   }

   public void submit(final LivingBlockRenderState state, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final CameraRenderState camera) {
      poseStack.pushPose();
      int overlayCoords = OverlayTexture.pack(0.0F, state.hasRedOverlay);
      if (!state.blockState.isAir()) {
         applyRotation(poseStack, state.deathTime, state.hasRotation, state.rotationPivot, state.rotation, state.ageInTicks);
         state.blockModel.submit(poseStack, submitNodeCollector, state.lightCoords, overlayCoords, state.outlineColor);
      } else if (!state.item.isEmpty()) {
         if (state.deathTime > 0.0F) {
            applyDeathFall(state.deathTime, poseStack);
         } else {
            poseStack.mulPose((Quaternionfc)Axis.YP.rotation(3.1415927F));
         }

         poseStack.translate(0.0F, 0.2F, 0.0F);
         poseStack.scale(1.5F, 1.5F, 1.5F);
         poseStack.scale(state.pogoScale.x(), state.pogoScale.y(), state.pogoScale.z());
         state.item.submit(poseStack, submitNodeCollector, state.lightCoords, overlayCoords, state.outlineColor);
      }

      poseStack.popPose();
      super.submit(state, poseStack, submitNodeCollector, camera);
   }

   public static void applyRotation(final PoseStack poseStack, final float deathTime, final boolean hasRotation, final Vec3 rotationPivot, final Quaternionf rotation, final float ageInTicks) {
      if (deathTime > 0.0F) {
         applyDeathFall(deathTime, poseStack);
      }

      if (hasRotation) {
         poseStack.mulPose((Quaternionfc)rotation);
         poseStack.translate(rotationPivot);
      } else {
         float bob = Mth.sin((double)(ageInTicks / 10.0F) + 0.9) * 0.1F + 0.1F;
         poseStack.translate(-0.5, (double)bob + 0.2, -0.5);
      }

   }

   private static Vector3f[] getCorners(final float sizeX, final float sizeY, final float sizeZ) {
      return new Vector3f[]{new Vector3f(-sizeX, -sizeY, -sizeZ), new Vector3f(-sizeX, -sizeY, sizeZ), new Vector3f(-sizeX, sizeY, -sizeZ), new Vector3f(-sizeX, sizeY, sizeZ), new Vector3f(sizeX, -sizeY, -sizeZ), new Vector3f(sizeX, -sizeY, sizeZ), new Vector3f(sizeX, sizeY, -sizeZ), new Vector3f(sizeX, sizeY, sizeZ)};
   }

   private static void applyDeathFall(final float deathTime, final PoseStack poseStack) {
      float tip = (deathTime - 1.0F) / 20.0F * 1.6F;
      tip = Mth.sqrt(tip);
      if (tip > 1.0F) {
         tip = 1.0F;
      }

      float tipAngle = tip * 90.0F;
      poseStack.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(tipAngle));
   }

   public LivingBlockRenderState createRenderState() {
      return new LivingBlockRenderState();
   }

   public void extractRenderState(final T entity, final LivingBlockRenderState state, final float partialTicks) {
      super.extractRenderState(entity, state, partialTicks);
      state.outlineColor = computeOutlineColor(entity);
      state.blockState = entity.getBlockState();
      ItemStack itemStack = entity.getItemStack();
      if (!itemStack.isEmpty()) {
         if (itemStack.getItem() instanceof BlockItem && !state.blockState.isAir()) {
            this.blockModelResolver.update(state.blockModel, state.blockState, BLOCK_DISPLAY_CONTEXT);
         } else {
            this.itemModelResolver.updateForNonLiving(state.item, entity.getItemStack(), ItemDisplayContext.GROUND, entity);
         }
      } else {
         state.item.clear();
      }

      extractRotation(entity, state, partialTicks);
      state.hasRedOverlay = entity.hurtTime > 0 || entity.deathTime > 0;
      if (entity.getMovement() instanceof BouncingMovement) {
         state.pogoScale = entity.getPogoScale(partialTicks);
      }

   }

   public static <T extends LivingBlock> void extractRotation(final T entity, final LivingBlockRenderState state, final float partialTicks) {
      state.hasRotation = entity.getMovement() instanceof RollingMovement;
      if (state.hasRotation) {
         entity.getRotation(state.rotation, partialTicks);
         state.rotation.x = -state.rotation.x;
         state.rotation.w = -state.rotation.w;
         AABB box = entity.getBoundingBox();
         AABB.Builder builder = new AABB.Builder();
         double sizeX = box.getXsize();
         double sizeY = box.getYsize();
         double sizeZ = box.getZsize();

         for(Vector3f corner : getCorners((float)sizeX, (float)sizeY, (float)sizeZ)) {
            state.rotation.transformUnit(corner);
            builder.include(corner);
         }

         Direction direction = entity.getClimbingDirection();
         Direction.Axis axis = direction.getAxis();
         AABB extents = builder.build();
         double edgeOffset = extents.edge(direction);
         if (axis == Direction.Axis.Y) {
            state.y -= edgeOffset * 0.5;
         } else {
            double sideLength = axis.choose(sizeX, 0.0, sizeZ);
            double offset = (edgeOffset - sideLength * (double)direction.getAxisDirection().getStep()) * 0.5;
            state.y += sizeY * 0.5;
            if (axis == Direction.Axis.X) {
               state.x -= offset;
            } else {
               state.z -= offset;
            }
         }
      }

      state.rotationPivot = entity.boundingBoxOffset;
      state.deathTime = entity.deathTime > 0 ? (float)entity.deathTime + partialTicks : 0.0F;
   }

   private static int computeOutlineColor(final LivingBlock entity) {
      Player owner = entity.getOwner();
      boolean ownedByLocal = owner instanceof LocalPlayer;
      if (entity.isSelected()) {
         return ownedByLocal ? -1 : -8355712;
      } else if (Minecraft.getInstance().player.getItemInHand(InteractionHand.MAIN_HAND).is(Items.HIGHLIGHT_ACTION)) {
         return ColorLerper.getLerpedColor(ColorLerper.Type.HIGHLIGHT, (float)entity.level().getGameTime());
      } else {
         LivingBlockGroup group = entity.getGroup();
         if (group != LivingBlockGroup.NONE && owner != null) {
            if (ownedByLocal && owner.getSelectedGroup() == LivingBlockGroup.ALL) {
               return ColorLerper.getLerpedColor(ColorLerper.Type.LIVING_BLOCK_GROUPS, (float)entity.level().getGameTime());
            } else {
               return ownedByLocal ? group.color() : -8355712;
            }
         } else {
            return 0;
         }
      }
   }
}

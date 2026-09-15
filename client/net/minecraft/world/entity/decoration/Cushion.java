package net.minecraft.world.entity.decoration;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Continuation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class Cushion extends BlockAttachedEntity {
   private static final DyeColor DEFAULT_COLOR;
   private static final int LIGHTNING_DROP_INVULNERABLE_TICKS = 20;
   private static final EntityDataAccessor<DyeColor> DATA_COLOR;

   public Cushion(final EntityType<Cushion> type, final Level level) {
      super(type, level);
   }

   public DyeColor getColor() {
      return (DyeColor)this.entityData.get(DATA_COLOR);
   }

   public void setColor(final DyeColor color) {
      this.entityData.set(DATA_COLOR, color);
   }

   public void dropItem(final ServerLevel level, final @Nullable Entity causedBy) {
      this.playSound(SoundEvents.CUSHION_BREAK, 1.0F, 1.0F);
      this.showBreakingParticles();
      if ((Boolean)level.getGameRules().get(GameRules.ENTITY_DROPS)) {
         if (causedBy instanceof Player) {
            Player player = (Player)causedBy;
            if (player.hasInfiniteMaterials()) {
               return;
            }
         }

         ItemEntity itemEntity = this.spawnAtLocation(level, this.getCushionItemStackWithData());
         if (itemEntity != null && causedBy instanceof LightningBolt) {
            itemEntity.setInvulnerableTime(20);
         }

      }
   }

   private static boolean isBreakingDeniedFor(final DamageSource source) {
      Entity var2 = source.getEntity();
      boolean var10000;
      if (var2 instanceof Player player) {
         if (!player.mayBuild()) {
            var10000 = true;
            return var10000;
         }
      }

      var10000 = false;
      return var10000;
   }

   private boolean isBreakingDeniedAtPosFor(final ServerLevel level, final DamageSource source) {
      Entity var4 = source.getEntity();
      boolean var10000;
      if (var4 instanceof Player player) {
         if (!level.mayInteract(player, this.pos)) {
            var10000 = true;
            return var10000;
         }
      }

      var10000 = false;
      return var10000;
   }

   public boolean hurtServer(final ServerLevel level, final DamageSource source, final float damage) {
      return !isBreakingDeniedFor(source) && !this.isBreakingDeniedAtPosFor(level, source) ? super.hurtServer(level, source, damage) : false;
   }

   public boolean hurtClient(final DamageSource source) {
      return isBreakingDeniedFor(source) ? false : super.hurtClient(source);
   }

   public InteractionResult interact(final Player player, final InteractionHand hand, final Vec3 location) {
      if (!player.isSecondaryUseActive() && !this.isVehicle()) {
         if (!this.level().isClientSide() && player.startRiding(this)) {
            this.playSound(SoundEvents.CUSHION_SIT, 1.0F, 1.0F);
            return InteractionResult.SUCCESS_SERVER;
         } else {
            return InteractionResult.CONSUME;
         }
      } else {
         return InteractionResult.PASS;
      }
   }

   protected void removePassenger(final Entity passenger) {
      super.removePassenger(passenger);
      if (!this.level().isClientSide() && this.getRemovalReason() == null) {
         this.playSound(SoundEvents.CUSHION_GET_UP, 1.0F, 1.0F);
      }

   }

   public ItemStack getPickResult() {
      return new ItemStack(Items.CUSHION.pick(this.getColor()));
   }

   protected void tickAtCheckInterval() {
      Level var2 = this.level();
      if (var2 instanceof ServerLevel level) {
         BlockPos blockPos = this.blockPosition();
         FluidState fluidState = level.getBlockState(blockPos).getFluidState();
         if (this.collidedWithFluid(fluidState, blockPos, this.position(), this.position())) {
            fluidState.entityInside(level, blockPos, this, this.insideEffectCollector);
            this.insideEffectCollector.applyAndClear(this);
         }

         this.destroyIfInFire(level);
      }

   }

   public void destroyIfInFire(final ServerLevel level) {
      if (!this.isRemoved()) {
         level.findBlocksIn(this.getBoundingBox().nextDeflated()).filterState((state) -> state.is(BlockTags.FIRE)).forEachUntil((var2, var3) -> {
            this.hurtServer(level, this.damageSources().inFire(), 1.0F);
            return Continuation.ABORT;
         });
      }
   }

   public void thunderHit(final ServerLevel level, final LightningBolt lightningBolt) {
      if (!this.isRemoved()) {
         this.kill(level, lightningBolt);
         this.dropItem(level, lightningBolt);
      }

   }

   public void setPos(final double x, final double y, final double z) {
      this.setPosRaw(x, y, z);
      super.setPos(x, y, z);
   }

   private void showBreakingParticles() {
      Level var2 = this.level();
      if (var2 instanceof ServerLevel level) {
         level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, ((Block)Blocks.WOOL.pick(this.getColor())).defaultBlockState()), this.getX(), this.getY(0.6666666666666666), this.getZ(), 10, (double)(this.getBbWidth() / 4.0F), (double)(this.getBbHeight() / 4.0F), (double)(this.getBbWidth() / 4.0F), 0.05);
      }

   }

   public static boolean canBePlacedAt(final Level level, final AABB boundingBox) {
      return wouldSurviveAt(level, boundingBox) && !isAnchorBuried(level, boundingBox);
   }

   public static boolean wouldSurviveAt(final Level level, final AABB boundingBox) {
      return hasAnchorBelow(level, boundingBox) && !isCoveredBySuffocatingBlocks(level, boundingBox);
   }

   private static boolean hasAnchorBelow(final Level level, final AABB boundingBox) {
      AABB anchorBox = new AABB(boundingBox.minX, boundingBox.minY - 0.015625, boundingBox.minZ, Math.nextDown(boundingBox.maxX), boundingBox.minY, Math.nextDown(boundingBox.maxZ));
      return level.findBlocksIn(anchorBox.expandTowards(0.0, -0.125, 0.0)).forEachUntil((blockPos, blockState) -> {
         VoxelShape shape = blockState.getShape(level, blockPos);
         return !shape.isEmpty() && shape.bounds().move(blockPos).intersects(anchorBox) ? Continuation.ABORT : Continuation.CONTINUE;
      });
   }

   private static boolean isAnchorBuried(final Level level, final AABB boundingBox) {
      AABB restingSlice = (new AABB(boundingBox.minX, boundingBox.minY, boundingBox.minZ, boundingBox.maxX, boundingBox.minY + 0.015625, boundingBox.maxZ)).nextDeflated();
      VoxelShape exposedSurface = Shapes.create(restingSlice);

      for(VoxelShape collider : level.getBlockCollisions((Entity)null, restingSlice)) {
         exposedSurface = Shapes.join(exposedSurface, collider, BooleanOp.ONLY_FIRST);
         if (exposedSurface.isEmpty()) {
            return true;
         }
      }

      return false;
   }

   private static boolean isCoveredBySuffocatingBlocks(final Level level, final AABB boundingBox) {
      for(BlockPos blockPos : BlockPos.betweenClosed(boundingBox.nextDeflated())) {
         if (!level.getBlockState(blockPos).isSuffocating(level, blockPos)) {
            return false;
         }
      }

      return true;
   }

   public boolean survives() {
      return wouldSurviveAt(this.level(), this.getBoundingBox());
   }

   protected void recalculateBoundingBox() {
      this.setBoundingBox(this.makeBoundingBox());
   }

   protected void defineSynchedData(final SynchedEntityData.Builder entityData) {
      entityData.define(DATA_COLOR, DEFAULT_COLOR);
   }

   protected void addAdditionalSaveData(final ValueOutput output) {
      super.addAdditionalSaveData(output);
      output.store("color", DyeColor.CODEC, this.getColor());
   }

   protected void readAdditionalSaveData(final ValueInput input) {
      super.readAdditionalSaveData(input);
      this.setColor((DyeColor)input.read("color", DyeColor.CODEC).orElse(DEFAULT_COLOR));
   }

   public <T> @Nullable T get(final DataComponentType<? extends T> type) {
      return (T)(type == DataComponents.CUSHION_COLOR ? castComponentValue(type, this.getColor()) : super.get(type));
   }

   protected void applyImplicitComponents(final DataComponentGetter components) {
      this.applyImplicitComponentIfPresent(components, DataComponents.CUSHION_COLOR);
      super.applyImplicitComponents(components);
   }

   protected <T> boolean applyImplicitComponent(final DataComponentType<T> type, final T value) {
      if (type == DataComponents.CUSHION_COLOR) {
         this.setColor((DyeColor)castComponentValue(DataComponents.CUSHION_COLOR, value));
         return true;
      } else {
         return super.applyImplicitComponent(type, value);
      }
   }

   private ItemStack getCushionItemStackWithData() {
      ItemStack itemStack = new ItemStack(Items.CUSHION.pick(this.getColor()));
      itemStack.set(DataComponents.CUSTOM_NAME, this.getCustomName());
      return itemStack;
   }

   static {
      DEFAULT_COLOR = DyeColor.WHITE;
      DATA_COLOR = SynchedEntityData.<DyeColor>defineId(Cushion.class, EntityDataSerializers.DYE_COLOR);
   }
}

package net.minecraft.world.entity.item;

import com.mojang.logging.LogUtils;
import java.util.function.Predicate;
import net.minecraft.CrashReportCategory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ConcretePowderBlock;
import net.minecraft.world.level.block.Fallable;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class FallingBlockEntity extends Entity {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final BlockState DEFAULT_BLOCK_STATE;
   private static final int DEFAULT_TIME = 0;
   private static final float DEFAULT_FALL_DAMAGE_PER_DISTANCE = 0.0F;
   private static final int DEFAULT_MAX_FALL_DAMAGE = 40;
   private static final boolean DEFAULT_DROP_ITEM = true;
   private static final boolean DEFAULT_CANCEL_DROP = false;
   private BlockState blockState;
   public int time;
   public boolean dropItem;
   private boolean cancelDrop;
   private boolean hurtEntities;
   private int fallDamageMax;
   private float fallDamagePerDistance;
   public @Nullable CompoundTag blockData;
   public boolean forceTickAfterTeleportToDuplicate;
   protected static final EntityDataAccessor<BlockPos> DATA_START_POS;

   public FallingBlockEntity(final EntityType<? extends FallingBlockEntity> type, final Level level) {
      super(type, level);
      this.blockState = DEFAULT_BLOCK_STATE;
      this.time = 0;
      this.dropItem = true;
      this.cancelDrop = false;
      this.fallDamageMax = 40;
      this.fallDamagePerDistance = 0.0F;
   }

   private FallingBlockEntity(final Level level, final double x, final double y, final double z, final BlockState blockState) {
      this(EntityTypes.FALLING_BLOCK, level);
      this.blockState = blockState;
      this.blocksBuilding = true;
      this.setPos(x, y, z);
      this.setDeltaMovement(Vec3.ZERO);
      this.xo = x;
      this.yo = y;
      this.zo = z;
      this.setStartPos(this.blockPosition());
   }

   public static FallingBlockEntity fall(final Level level, final BlockPos pos, final BlockState state) {
      FallingBlockEntity entity = new FallingBlockEntity(level, (double)pos.getX() + 0.5, (double)pos.getY(), (double)pos.getZ() + 0.5, state.hasProperty(BlockStateProperties.WATERLOGGED) ? (BlockState)state.setValue(BlockStateProperties.WATERLOGGED, false) : state);
      level.setBlock(pos, state.getFluidState().createLegacyBlock(), 3);
      level.addFreshEntity(entity);
      return entity;
   }

   public boolean isAttackable() {
      return false;
   }

   public final boolean hurtServer(final ServerLevel level, final DamageSource source, final float damage) {
      if (!this.isInvulnerableToBase(source)) {
         this.markHurt();
      }

      return false;
   }

   public void setStartPos(final BlockPos pos) {
      this.entityData.set(DATA_START_POS, pos);
   }

   public BlockPos getStartPos() {
      return (BlockPos)this.entityData.get(DATA_START_POS);
   }

   protected Entity.MovementEmission getMovementEmission() {
      return Entity.MovementEmission.NONE;
   }

   protected void defineSynchedData(final SynchedEntityData.Builder entityData) {
      entityData.define(DATA_START_POS, BlockPos.ZERO);
   }

   public boolean isPickable() {
      return !this.isRemoved();
   }

   protected double getDefaultGravity() {
      return 0.04;
   }

   public void tick() {
      if (this.blockState.isAir()) {
         this.discard();
      } else {
         Block block = this.blockState.getBlock();
         ++this.time;
         this.applyGravity();
         this.move(MoverType.SELF, this.getDeltaMovement());
         this.applyEffectsFromBlocks();
         this.handlePortal();
         Level var3 = this.level();
         if (var3 instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel)var3;
            if (this.isAlive() || this.forceTickAfterTeleportToDuplicate) {
               BlockPos pos = this.blockPosition();
               boolean isConcrete = this.blockState.getBlock() instanceof ConcretePowderBlock;
               boolean isStuckInWater = isConcrete && this.level().getFluidState(pos).is(FluidTags.WATER);
               double moveVec = this.getDeltaMovement().lengthSqr();
               if (isConcrete && moveVec > 1.0) {
                  BlockHitResult clip = this.level().clip(new ClipContext(new Vec3(this.xo, this.yo, this.zo), this.position(), ClipContext.Block.COLLIDER, ClipContext.Fluid.SOURCE_ONLY, this));
                  if (clip.getType() != HitResult.Type.MISS && this.level().getFluidState(clip.getBlockPos()).is(FluidTags.WATER)) {
                     pos = clip.getBlockPos();
                     isStuckInWater = true;
                  }
               }

               if (!this.onGround() && !isStuckInWater) {
                  if (this.time > 100 && (pos.getY() <= this.level().getMinY() || pos.getY() > this.level().getMaxY()) || this.time > 600) {
                     if (this.dropItem && (Boolean)serverLevel.getGameRules().get(GameRules.ENTITY_DROPS)) {
                        this.spawnAtLocation(serverLevel, block);
                     }

                     this.discard();
                  }
               } else {
                  BlockState currentState = this.level().getBlockState(pos);
                  this.setDeltaMovement(this.getDeltaMovement().multiply(0.7, -0.5, 0.7));
                  if (!currentState.is(Blocks.MOVING_PISTON)) {
                     if (!this.cancelDrop) {
                        boolean mayReplace = currentState.canBeReplaced(new DirectionalPlaceContext(this.level(), pos, Direction.DOWN, ItemStack.EMPTY, Direction.UP));
                        boolean wouldContinueFalling = FallingBlock.isFree(this.level().getBlockState(pos.below())) && (!isConcrete || !isStuckInWater);
                        boolean wouldSurvive = this.blockState.canSurvive(this.level(), pos) && !wouldContinueFalling;
                        if (mayReplace && wouldSurvive) {
                           if (this.blockState.hasProperty(BlockStateProperties.WATERLOGGED) && this.level().getFluidState(pos).is(Fluids.WATER)) {
                              this.blockState = (BlockState)this.blockState.setValue(BlockStateProperties.WATERLOGGED, true);
                           }

                           if (this.level().setBlock(pos, this.blockState, 3)) {
                              serverLevel.getChunkSource().chunkMap.sendToTrackingPlayers(this, new ClientboundBlockUpdatePacket(pos, this.level().getBlockState(pos)));
                              this.discard();
                              if (block instanceof Fallable) {
                                 Fallable fallable = (Fallable)block;
                                 fallable.onLand(this.level(), pos, this.blockState, currentState, this);
                              }

                              if (this.blockData != null && this.blockState.hasBlockEntity()) {
                                 BlockEntity blockEntity = this.level().getBlockEntity(pos);
                                 if (blockEntity != null) {
                                    try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(blockEntity.problemPath(), LOGGER)) {
                                       RegistryAccess registryAccess = this.level().registryAccess();
                                       TagValueOutput output = TagValueOutput.createWithContext(reporter, registryAccess);
                                       blockEntity.saveWithoutMetadata((ValueOutput)output);
                                       CompoundTag merged = output.buildResult();
                                       this.blockData.forEach((name, tag) -> merged.put(name, tag.copy()));
                                       blockEntity.loadWithComponents(TagValueInput.create(reporter, registryAccess, merged));
                                    } catch (Exception e) {
                                       LOGGER.error("Failed to load block entity from falling block", e);
                                    }

                                    blockEntity.setChanged();
                                 }
                              }
                           } else if (this.dropItem && (Boolean)serverLevel.getGameRules().get(GameRules.ENTITY_DROPS)) {
                              this.discard();
                              this.callOnBrokenAfterFall(block, pos);
                              this.spawnAtLocation(serverLevel, block);
                           }
                        } else {
                           this.discard();
                           if (this.dropItem && (Boolean)serverLevel.getGameRules().get(GameRules.ENTITY_DROPS)) {
                              this.callOnBrokenAfterFall(block, pos);
                              this.spawnAtLocation(serverLevel, block);
                           }
                        }
                     } else {
                        this.discard();
                        this.callOnBrokenAfterFall(block, pos);
                     }
                  }
               }
            }
         }

         this.setDeltaMovement(this.getDeltaMovement().scale(0.98));
      }
   }

   public void callOnBrokenAfterFall(final Block block, final BlockPos pos) {
      if (block instanceof Fallable fallable) {
         fallable.onBrokenAfterFall(this.level(), pos, this);
      }

   }

   public boolean causeFallDamage(final double fallDistance, final float damageModifier, final DamageSource damageSource) {
      if (!this.hurtEntities) {
         return false;
      } else {
         int fallDistanceInt = Mth.ceil(fallDistance - 1.0);
         if (fallDistanceInt < 0) {
            return false;
         } else {
            Predicate<Entity> entitySelector = EntitySelector.NO_CREATIVE_OR_SPECTATOR.and(EntitySelector.LIVING_ENTITY_STILL_ALIVE);
            Block var9 = this.blockState.getBlock();
            DamageSource var10000;
            if (var9 instanceof Fallable) {
               Fallable fallable = (Fallable)var9;
               var10000 = fallable.getFallDamageSource(this);
            } else {
               var10000 = this.damageSources().fallingBlock(this);
            }

            DamageSource actualDamageSource = var10000;
            float damage = (float)Math.min(Mth.floor((float)fallDistanceInt * this.fallDamagePerDistance), this.fallDamageMax);
            this.level().getEntities(this, this.getBoundingBox(), entitySelector).forEach((entity) -> entity.hurt(actualDamageSource, damage));
            boolean isAnvil = this.blockState.is(BlockTags.ANVIL);
            if (isAnvil && damage > 0.0F && this.random.nextFloat() < 0.05F + (float)fallDistanceInt * 0.05F) {
               BlockState newBlockState = AnvilBlock.damage(this.blockState);
               if (newBlockState == null) {
                  this.cancelDrop = true;
               } else {
                  this.blockState = newBlockState;
               }
            }

            return false;
         }
      }
   }

   protected void addAdditionalSaveData(final ValueOutput output) {
      output.store("BlockState", BlockState.CODEC, this.blockState);
      output.putInt("Time", this.time);
      output.putBoolean("DropItem", this.dropItem);
      output.putBoolean("HurtEntities", this.hurtEntities);
      output.putFloat("FallHurtAmount", this.fallDamagePerDistance);
      output.putInt("FallHurtMax", this.fallDamageMax);
      if (this.blockData != null) {
         output.store("TileEntityData", CompoundTag.CODEC, this.blockData);
      }

      output.putBoolean("CancelDrop", this.cancelDrop);
   }

   protected void readAdditionalSaveData(final ValueInput input) {
      this.blockState = (BlockState)input.read("BlockState", BlockState.CODEC).orElse(DEFAULT_BLOCK_STATE);
      this.time = input.getIntOr("Time", 0);
      boolean defaultHurtEntities = this.blockState.is(BlockTags.ANVIL);
      this.hurtEntities = input.getBooleanOr("HurtEntities", defaultHurtEntities);
      this.fallDamagePerDistance = input.getFloatOr("FallHurtAmount", 0.0F);
      this.fallDamageMax = input.getIntOr("FallHurtMax", 40);
      this.dropItem = input.getBooleanOr("DropItem", true);
      this.blockData = (CompoundTag)input.read("TileEntityData", CompoundTag.CODEC).orElse((Object)null);
      this.cancelDrop = input.getBooleanOr("CancelDrop", false);
   }

   public void setHurtsEntities(final float damagePerDistance, final int damageMax) {
      this.hurtEntities = true;
      this.fallDamagePerDistance = damagePerDistance;
      this.fallDamageMax = damageMax;
   }

   public void disableDrop() {
      this.cancelDrop = true;
   }

   public boolean displayFireAnimation() {
      return false;
   }

   public void fillCrashReportCategory(final CrashReportCategory category) {
      super.fillCrashReportCategory(category);
      category.setDetail("Immitating BlockState", this.blockState.toString());
   }

   public BlockState getBlockState() {
      return this.blockState;
   }

   protected Component getTypeName() {
      return Component.translatable("entity.minecraft.falling_block_type", this.blockState.getBlock().getName());
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket(final ServerEntity serverEntity) {
      return new ClientboundAddEntityPacket(this, serverEntity, Block.getId(this.getBlockState()));
   }

   public void recreateFromPacket(final ClientboundAddEntityPacket packet) {
      super.recreateFromPacket(packet);
      this.blockState = Block.stateById(packet.getData());
      this.blocksBuilding = true;
      double x = packet.getX();
      double y = packet.getY();
      double z = packet.getZ();
      this.setPos(x, y, z);
      this.setStartPos(this.blockPosition());
   }

   public @Nullable Entity teleport(final TeleportTransition transition) {
      ResourceKey<Level> newDimension = transition.newLevel().dimension();
      ResourceKey<Level> oldDimension = this.level().dimension();
      boolean fromOrToEnd = (oldDimension == Level.END || newDimension == Level.END) && oldDimension != newDimension;
      Entity newEntity = super.teleport(transition);
      this.forceTickAfterTeleportToDuplicate = newEntity != null && fromOrToEnd;
      return newEntity;
   }

   static {
      DEFAULT_BLOCK_STATE = Blocks.SAND.defaultBlockState();
      DATA_START_POS = SynchedEntityData.<BlockPos>defineId(FallingBlockEntity.class, EntityDataSerializers.BLOCK_POS);
   }
}

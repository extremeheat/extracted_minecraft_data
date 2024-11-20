package net.minecraft.world.entity.item;

import com.mojang.logging.LogUtils;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.CrashReportCategory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
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
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
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
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class FallingBlockEntity extends Entity {
   private static final Logger LOGGER = LogUtils.getLogger();
   private BlockState blockState;
   public int time;
   public boolean dropItem;
   private boolean cancelDrop;
   private boolean hurtEntities;
   private int fallDamageMax;
   private float fallDamagePerDistance;
   @Nullable
   public CompoundTag blockData;
   public boolean forceTickAfterTeleportToDuplicate;
   protected static final EntityDataAccessor<BlockPos> DATA_START_POS;

   public FallingBlockEntity(EntityType<? extends FallingBlockEntity> var1, Level var2) {
      super(var1, var2);
      this.blockState = Blocks.SAND.defaultBlockState();
      this.dropItem = true;
      this.fallDamageMax = 40;
   }

   private FallingBlockEntity(Level var1, double var2, double var4, double var6, BlockState var8) {
      this(EntityType.FALLING_BLOCK, var1);
      this.blockState = var8;
      this.blocksBuilding = true;
      this.setPos(var2, var4, var6);
      this.setDeltaMovement(Vec3.ZERO);
      this.xo = var2;
      this.yo = var4;
      this.zo = var6;
      this.setStartPos(this.blockPosition());
   }

   public static FallingBlockEntity fall(Level var0, BlockPos var1, BlockState var2) {
      FallingBlockEntity var3 = new FallingBlockEntity(var0, (double)var1.getX() + 0.5, (double)var1.getY(), (double)var1.getZ() + 0.5, var2.hasProperty(BlockStateProperties.WATERLOGGED) ? (BlockState)var2.setValue(BlockStateProperties.WATERLOGGED, false) : var2);
      var0.setBlock(var1, var2.getFluidState().createLegacyBlock(), 3);
      var0.addFreshEntity(var3);
      return var3;
   }

   public boolean isAttackable() {
      return false;
   }

   public final boolean hurtServer(ServerLevel var1, DamageSource var2, float var3) {
      if (!this.isInvulnerableToBase(var2)) {
         this.markHurt();
      }

      return false;
   }

   public void setStartPos(BlockPos var1) {
      this.entityData.set(DATA_START_POS, var1);
   }

   public BlockPos getStartPos() {
      return (BlockPos)this.entityData.get(DATA_START_POS);
   }

   protected Entity.MovementEmission getMovementEmission() {
      return Entity.MovementEmission.NONE;
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      var1.define(DATA_START_POS, BlockPos.ZERO);
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
         Block var1 = this.blockState.getBlock();
         ++this.time;
         this.applyGravity();
         this.move(MoverType.SELF, this.getDeltaMovement());
         this.applyEffectsFromBlocks();
         this.handlePortal();
         Level var3 = this.level();
         if (var3 instanceof ServerLevel) {
            ServerLevel var2 = (ServerLevel)var3;
            if (this.isAlive() || this.forceTickAfterTeleportToDuplicate) {
               BlockPos var17 = this.blockPosition();
               boolean var4 = this.blockState.getBlock() instanceof ConcretePowderBlock;
               boolean var5 = var4 && this.level().getFluidState(var17).is(FluidTags.WATER);
               double var6 = this.getDeltaMovement().lengthSqr();
               if (var4 && var6 > 1.0) {
                  BlockHitResult var8 = this.level().clip(new ClipContext(new Vec3(this.xo, this.yo, this.zo), this.position(), ClipContext.Block.COLLIDER, ClipContext.Fluid.SOURCE_ONLY, this));
                  if (var8.getType() != HitResult.Type.MISS && this.level().getFluidState(var8.getBlockPos()).is(FluidTags.WATER)) {
                     var17 = var8.getBlockPos();
                     var5 = true;
                  }
               }

               if (!this.onGround() && !var5) {
                  if (this.time > 100 && (var17.getY() <= this.level().getMinY() || var17.getY() > this.level().getMaxY()) || this.time > 600) {
                     if (this.dropItem && var2.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                        this.spawnAtLocation(var2, var1);
                     }

                     this.discard();
                  }
               } else {
                  BlockState var18 = this.level().getBlockState(var17);
                  this.setDeltaMovement(this.getDeltaMovement().multiply(0.7, -0.5, 0.7));
                  if (!var18.is(Blocks.MOVING_PISTON)) {
                     if (!this.cancelDrop) {
                        boolean var9 = var18.canBeReplaced(new DirectionalPlaceContext(this.level(), var17, Direction.DOWN, ItemStack.EMPTY, Direction.UP));
                        boolean var10 = FallingBlock.isFree(this.level().getBlockState(var17.below())) && (!var4 || !var5);
                        boolean var11 = this.blockState.canSurvive(this.level(), var17) && !var10;
                        if (var9 && var11) {
                           if (this.blockState.hasProperty(BlockStateProperties.WATERLOGGED) && this.level().getFluidState(var17).getType() == Fluids.WATER) {
                              this.blockState = (BlockState)this.blockState.setValue(BlockStateProperties.WATERLOGGED, true);
                           }

                           if (this.level().setBlock(var17, this.blockState, 3)) {
                              ((ServerLevel)this.level()).getChunkSource().chunkMap.broadcast(this, new ClientboundBlockUpdatePacket(var17, this.level().getBlockState(var17)));
                              this.discard();
                              if (var1 instanceof Fallable) {
                                 ((Fallable)var1).onLand(this.level(), var17, this.blockState, var18, this);
                              }

                              if (this.blockData != null && this.blockState.hasBlockEntity()) {
                                 BlockEntity var12 = this.level().getBlockEntity(var17);
                                 if (var12 != null) {
                                    CompoundTag var13 = var12.saveWithoutMetadata(this.level().registryAccess());

                                    for(String var15 : this.blockData.getAllKeys()) {
                                       var13.put(var15, this.blockData.get(var15).copy());
                                    }

                                    try {
                                       var12.loadWithComponents(var13, this.level().registryAccess());
                                    } catch (Exception var16) {
                                       LOGGER.error("Failed to load block entity from falling block", var16);
                                    }

                                    var12.setChanged();
                                 }
                              }
                           } else if (this.dropItem && var2.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                              this.discard();
                              this.callOnBrokenAfterFall(var1, var17);
                              this.spawnAtLocation(var2, var1);
                           }
                        } else {
                           this.discard();
                           if (this.dropItem && var2.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                              this.callOnBrokenAfterFall(var1, var17);
                              this.spawnAtLocation(var2, var1);
                           }
                        }
                     } else {
                        this.discard();
                        this.callOnBrokenAfterFall(var1, var17);
                     }
                  }
               }
            }
         }

         this.setDeltaMovement(this.getDeltaMovement().scale(0.98));
      }
   }

   public void callOnBrokenAfterFall(Block var1, BlockPos var2) {
      if (var1 instanceof Fallable) {
         ((Fallable)var1).onBrokenAfterFall(this.level(), var2, this);
      }

   }

   public boolean causeFallDamage(float var1, float var2, DamageSource var3) {
      if (!this.hurtEntities) {
         return false;
      } else {
         int var4 = Mth.ceil(var1 - 1.0F);
         if (var4 < 0) {
            return false;
         } else {
            Predicate var5 = EntitySelector.NO_CREATIVE_OR_SPECTATOR.and(EntitySelector.LIVING_ENTITY_STILL_ALIVE);
            Block var8 = this.blockState.getBlock();
            DamageSource var10000;
            if (var8 instanceof Fallable) {
               Fallable var7 = (Fallable)var8;
               var10000 = var7.getFallDamageSource(this);
            } else {
               var10000 = this.damageSources().fallingBlock(this);
            }

            DamageSource var6 = var10000;
            float var10 = (float)Math.min(Mth.floor((float)var4 * this.fallDamagePerDistance), this.fallDamageMax);
            this.level().getEntities(this, this.getBoundingBox(), var5).forEach((var2x) -> var2x.hurt(var6, var10));
            boolean var11 = this.blockState.is(BlockTags.ANVIL);
            if (var11 && var10 > 0.0F && this.random.nextFloat() < 0.05F + (float)var4 * 0.05F) {
               BlockState var9 = AnvilBlock.damage(this.blockState);
               if (var9 == null) {
                  this.cancelDrop = true;
               } else {
                  this.blockState = var9;
               }
            }

            return false;
         }
      }
   }

   protected void addAdditionalSaveData(CompoundTag var1) {
      var1.put("BlockState", NbtUtils.writeBlockState(this.blockState));
      var1.putInt("Time", this.time);
      var1.putBoolean("DropItem", this.dropItem);
      var1.putBoolean("HurtEntities", this.hurtEntities);
      var1.putFloat("FallHurtAmount", this.fallDamagePerDistance);
      var1.putInt("FallHurtMax", this.fallDamageMax);
      if (this.blockData != null) {
         var1.put("TileEntityData", this.blockData);
      }

      var1.putBoolean("CancelDrop", this.cancelDrop);
   }

   protected void readAdditionalSaveData(CompoundTag var1) {
      this.blockState = NbtUtils.readBlockState(this.level().holderLookup(Registries.BLOCK), var1.getCompound("BlockState"));
      this.time = var1.getInt("Time");
      if (var1.contains("HurtEntities", 99)) {
         this.hurtEntities = var1.getBoolean("HurtEntities");
         this.fallDamagePerDistance = var1.getFloat("FallHurtAmount");
         this.fallDamageMax = var1.getInt("FallHurtMax");
      } else if (this.blockState.is(BlockTags.ANVIL)) {
         this.hurtEntities = true;
      }

      if (var1.contains("DropItem", 99)) {
         this.dropItem = var1.getBoolean("DropItem");
      }

      if (var1.contains("TileEntityData", 10)) {
         this.blockData = var1.getCompound("TileEntityData").copy();
      }

      this.cancelDrop = var1.getBoolean("CancelDrop");
      if (this.blockState.isAir()) {
         this.blockState = Blocks.SAND.defaultBlockState();
      }

   }

   public void setHurtsEntities(float var1, int var2) {
      this.hurtEntities = true;
      this.fallDamagePerDistance = var1;
      this.fallDamageMax = var2;
   }

   public void disableDrop() {
      this.cancelDrop = true;
   }

   public boolean displayFireAnimation() {
      return false;
   }

   public void fillCrashReportCategory(CrashReportCategory var1) {
      super.fillCrashReportCategory(var1);
      var1.setDetail("Immitating BlockState", this.blockState.toString());
   }

   public BlockState getBlockState() {
      return this.blockState;
   }

   protected Component getTypeName() {
      return Component.translatable("entity.minecraft.falling_block_type", this.blockState.getBlock().getName());
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity var1) {
      return new ClientboundAddEntityPacket(this, var1, Block.getId(this.getBlockState()));
   }

   public void recreateFromPacket(ClientboundAddEntityPacket var1) {
      super.recreateFromPacket(var1);
      this.blockState = Block.stateById(var1.getData());
      this.blocksBuilding = true;
      double var2 = var1.getX();
      double var4 = var1.getY();
      double var6 = var1.getZ();
      this.setPos(var2, var4, var6);
      this.setStartPos(this.blockPosition());
   }

   @Nullable
   public Entity teleport(TeleportTransition var1) {
      ResourceKey var2 = var1.newLevel().dimension();
      ResourceKey var3 = this.level().dimension();
      boolean var4 = (var3 == Level.END || var2 == Level.END) && var3 != var2;
      Entity var5 = super.teleport(var1);
      this.forceTickAfterTeleportToDuplicate = var5 != null && var4;
      return var5;
   }

   static {
      DATA_START_POS = SynchedEntityData.<BlockPos>defineId(FallingBlockEntity.class, EntityDataSerializers.BLOCK_POS);
   }
}

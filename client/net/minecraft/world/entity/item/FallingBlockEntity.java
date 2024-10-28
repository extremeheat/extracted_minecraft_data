package net.minecraft.world.entity.item;

import com.mojang.logging.LogUtils;
import java.util.Iterator;
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
import net.minecraft.world.level.portal.DimensionTransition;
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
         this.handlePortal();
         if (!this.level().isClientSide && (this.isAlive() || this.forceTickAfterTeleportToDuplicate)) {
            BlockPos var2 = this.blockPosition();
            boolean var3 = this.blockState.getBlock() instanceof ConcretePowderBlock;
            boolean var4 = var3 && this.level().getFluidState(var2).is(FluidTags.WATER);
            double var5 = this.getDeltaMovement().lengthSqr();
            if (var3 && var5 > 1.0) {
               BlockHitResult var7 = this.level().clip(new ClipContext(new Vec3(this.xo, this.yo, this.zo), this.position(), ClipContext.Block.COLLIDER, ClipContext.Fluid.SOURCE_ONLY, this));
               if (var7.getType() != HitResult.Type.MISS && this.level().getFluidState(var7.getBlockPos()).is(FluidTags.WATER)) {
                  var2 = var7.getBlockPos();
                  var4 = true;
               }
            }

            if (!this.onGround() && !var4) {
               if (!this.level().isClientSide && (this.time > 100 && (var2.getY() <= this.level().getMinBuildHeight() || var2.getY() > this.level().getMaxBuildHeight()) || this.time > 600)) {
                  if (this.dropItem && this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                     this.spawnAtLocation(var1);
                  }

                  this.discard();
               }
            } else {
               BlockState var16 = this.level().getBlockState(var2);
               this.setDeltaMovement(this.getDeltaMovement().multiply(0.7, -0.5, 0.7));
               if (!var16.is(Blocks.MOVING_PISTON)) {
                  if (!this.cancelDrop) {
                     boolean var8 = var16.canBeReplaced(new DirectionalPlaceContext(this.level(), var2, Direction.DOWN, ItemStack.EMPTY, Direction.UP));
                     boolean var9 = FallingBlock.isFree(this.level().getBlockState(var2.below())) && (!var3 || !var4);
                     boolean var10 = this.blockState.canSurvive(this.level(), var2) && !var9;
                     if (var8 && var10) {
                        if (this.blockState.hasProperty(BlockStateProperties.WATERLOGGED) && this.level().getFluidState(var2).getType() == Fluids.WATER) {
                           this.blockState = (BlockState)this.blockState.setValue(BlockStateProperties.WATERLOGGED, true);
                        }

                        if (this.level().setBlock(var2, this.blockState, 3)) {
                           ((ServerLevel)this.level()).getChunkSource().chunkMap.broadcast(this, new ClientboundBlockUpdatePacket(var2, this.level().getBlockState(var2)));
                           this.discard();
                           if (var1 instanceof Fallable) {
                              ((Fallable)var1).onLand(this.level(), var2, this.blockState, var16, this);
                           }

                           if (this.blockData != null && this.blockState.hasBlockEntity()) {
                              BlockEntity var11 = this.level().getBlockEntity(var2);
                              if (var11 != null) {
                                 CompoundTag var12 = var11.saveWithoutMetadata(this.level().registryAccess());
                                 Iterator var13 = this.blockData.getAllKeys().iterator();

                                 while(var13.hasNext()) {
                                    String var14 = (String)var13.next();
                                    var12.put(var14, this.blockData.get(var14).copy());
                                 }

                                 try {
                                    var11.loadWithComponents(var12, this.level().registryAccess());
                                 } catch (Exception var15) {
                                    LOGGER.error("Failed to load block entity from falling block", var15);
                                 }

                                 var11.setChanged();
                              }
                           }
                        } else if (this.dropItem && this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                           this.discard();
                           this.callOnBrokenAfterFall(var1, var2);
                           this.spawnAtLocation(var1);
                        }
                     } else {
                        this.discard();
                        if (this.dropItem && this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                           this.callOnBrokenAfterFall(var1, var2);
                           this.spawnAtLocation(var1);
                        }
                     }
                  } else {
                     this.discard();
                     this.callOnBrokenAfterFall(var1, var2);
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
            this.level().getEntities((Entity)this, this.getBoundingBox(), var5).forEach((var2x) -> {
               var2x.hurt(var6, var10);
            });
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
      var1.setDetail("Immitating BlockState", (Object)this.blockState.toString());
   }

   public BlockState getBlockState() {
      return this.blockState;
   }

   protected Component getTypeName() {
      return Component.translatable("entity.minecraft.falling_block_type", this.blockState.getBlock().getName());
   }

   public boolean onlyOpCanSetNbt() {
      return true;
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
   public Entity changeDimension(DimensionTransition var1) {
      ResourceKey var2 = var1.newLevel().dimension();
      ResourceKey var3 = this.level().dimension();
      boolean var4 = (var3 == Level.END || var2 == Level.END) && var3 != var2;
      Entity var5 = super.changeDimension(var1);
      this.forceTickAfterTeleportToDuplicate = var5 != null && var4;
      return var5;
   }

   static {
      DATA_START_POS = SynchedEntityData.defineId(FallingBlockEntity.class, EntityDataSerializers.BLOCK_POS);
   }
}

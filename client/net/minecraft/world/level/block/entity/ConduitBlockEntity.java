package net.minecraft.world.level.block.entity;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ConduitBlockEntity extends BlockEntity implements TickableBlockEntity {
   private static final Block[] VALID_BLOCKS;
   public int tickCount;
   private float activeRotation;
   private boolean isActive;
   private boolean isHunting;
   private final List<BlockPos> effectBlocks;
   @Nullable
   private LivingEntity destroyTarget;
   @Nullable
   private UUID destroyTargetUUID;
   private long nextAmbientSoundActivation;

   public ConduitBlockEntity() {
      this(BlockEntityType.CONDUIT);
   }

   public ConduitBlockEntity(BlockEntityType<?> var1) {
      super(var1);
      this.effectBlocks = Lists.newArrayList();
   }

   public void load(CompoundTag var1) {
      super.load(var1);
      if (var1.contains("target_uuid")) {
         this.destroyTargetUUID = NbtUtils.loadUUIDTag(var1.getCompound("target_uuid"));
      } else {
         this.destroyTargetUUID = null;
      }

   }

   public CompoundTag save(CompoundTag var1) {
      super.save(var1);
      if (this.destroyTarget != null) {
         var1.put("target_uuid", NbtUtils.createUUIDTag(this.destroyTarget.getUUID()));
      }

      return var1;
   }

   @Nullable
   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return new ClientboundBlockEntityDataPacket(this.worldPosition, 5, this.getUpdateTag());
   }

   public CompoundTag getUpdateTag() {
      return this.save(new CompoundTag());
   }

   public void tick() {
      ++this.tickCount;
      long var1 = this.level.getGameTime();
      if (var1 % 40L == 0L) {
         this.setActive(this.updateShape());
         if (!this.level.isClientSide && this.isActive()) {
            this.applyEffects();
            this.updateDestroyTarget();
         }
      }

      if (var1 % 80L == 0L && this.isActive()) {
         this.playSound(SoundEvents.CONDUIT_AMBIENT);
      }

      if (var1 > this.nextAmbientSoundActivation && this.isActive()) {
         this.nextAmbientSoundActivation = var1 + 60L + (long)this.level.getRandom().nextInt(40);
         this.playSound(SoundEvents.CONDUIT_AMBIENT_SHORT);
      }

      if (this.level.isClientSide) {
         this.updateClientTarget();
         this.animationTick();
         if (this.isActive()) {
            ++this.activeRotation;
         }
      }

   }

   private boolean updateShape() {
      this.effectBlocks.clear();

      int var1;
      int var2;
      int var3;
      for(var1 = -1; var1 <= 1; ++var1) {
         for(var2 = -1; var2 <= 1; ++var2) {
            for(var3 = -1; var3 <= 1; ++var3) {
               BlockPos var4 = this.worldPosition.offset(var1, var2, var3);
               if (!this.level.isWaterAt(var4)) {
                  return false;
               }
            }
         }
      }

      for(var1 = -2; var1 <= 2; ++var1) {
         for(var2 = -2; var2 <= 2; ++var2) {
            for(var3 = -2; var3 <= 2; ++var3) {
               int var13 = Math.abs(var1);
               int var5 = Math.abs(var2);
               int var6 = Math.abs(var3);
               if ((var13 > 1 || var5 > 1 || var6 > 1) && (var1 == 0 && (var5 == 2 || var6 == 2) || var2 == 0 && (var13 == 2 || var6 == 2) || var3 == 0 && (var13 == 2 || var5 == 2))) {
                  BlockPos var7 = this.worldPosition.offset(var1, var2, var3);
                  BlockState var8 = this.level.getBlockState(var7);
                  Block[] var9 = VALID_BLOCKS;
                  int var10 = var9.length;

                  for(int var11 = 0; var11 < var10; ++var11) {
                     Block var12 = var9[var11];
                     if (var8.getBlock() == var12) {
                        this.effectBlocks.add(var7);
                     }
                  }
               }
            }
         }
      }

      this.setHunting(this.effectBlocks.size() >= 42);
      return this.effectBlocks.size() >= 16;
   }

   private void applyEffects() {
      int var1 = this.effectBlocks.size();
      int var2 = var1 / 7 * 16;
      int var3 = this.worldPosition.getX();
      int var4 = this.worldPosition.getY();
      int var5 = this.worldPosition.getZ();
      AABB var6 = (new AABB((double)var3, (double)var4, (double)var5, (double)(var3 + 1), (double)(var4 + 1), (double)(var5 + 1))).inflate((double)var2).expandTowards(0.0D, (double)this.level.getMaxBuildHeight(), 0.0D);
      List var7 = this.level.getEntitiesOfClass(Player.class, var6);
      if (!var7.isEmpty()) {
         Iterator var8 = var7.iterator();

         while(var8.hasNext()) {
            Player var9 = (Player)var8.next();
            if (this.worldPosition.closerThan(new BlockPos(var9), (double)var2) && var9.isInWaterOrRain()) {
               var9.addEffect(new MobEffectInstance(MobEffects.CONDUIT_POWER, 260, 0, true, true));
            }
         }

      }
   }

   private void updateDestroyTarget() {
      LivingEntity var1 = this.destroyTarget;
      int var2 = this.effectBlocks.size();
      if (var2 < 42) {
         this.destroyTarget = null;
      } else if (this.destroyTarget == null && this.destroyTargetUUID != null) {
         this.destroyTarget = this.findDestroyTarget();
         this.destroyTargetUUID = null;
      } else if (this.destroyTarget == null) {
         List var3 = this.level.getEntitiesOfClass(LivingEntity.class, this.getDestroyRangeAABB(), (var0) -> {
            return var0 instanceof Enemy && var0.isInWaterOrRain();
         });
         if (!var3.isEmpty()) {
            this.destroyTarget = (LivingEntity)var3.get(this.level.random.nextInt(var3.size()));
         }
      } else if (!this.destroyTarget.isAlive() || !this.worldPosition.closerThan(new BlockPos(this.destroyTarget), 8.0D)) {
         this.destroyTarget = null;
      }

      if (this.destroyTarget != null) {
         this.level.playSound((Player)null, this.destroyTarget.x, this.destroyTarget.y, this.destroyTarget.z, SoundEvents.CONDUIT_ATTACK_TARGET, SoundSource.BLOCKS, 1.0F, 1.0F);
         this.destroyTarget.hurt(DamageSource.MAGIC, 4.0F);
      }

      if (var1 != this.destroyTarget) {
         BlockState var4 = this.getBlockState();
         this.level.sendBlockUpdated(this.worldPosition, var4, var4, 2);
      }

   }

   private void updateClientTarget() {
      if (this.destroyTargetUUID == null) {
         this.destroyTarget = null;
      } else if (this.destroyTarget == null || !this.destroyTarget.getUUID().equals(this.destroyTargetUUID)) {
         this.destroyTarget = this.findDestroyTarget();
         if (this.destroyTarget == null) {
            this.destroyTargetUUID = null;
         }
      }

   }

   private AABB getDestroyRangeAABB() {
      int var1 = this.worldPosition.getX();
      int var2 = this.worldPosition.getY();
      int var3 = this.worldPosition.getZ();
      return (new AABB((double)var1, (double)var2, (double)var3, (double)(var1 + 1), (double)(var2 + 1), (double)(var3 + 1))).inflate(8.0D);
   }

   @Nullable
   private LivingEntity findDestroyTarget() {
      List var1 = this.level.getEntitiesOfClass(LivingEntity.class, this.getDestroyRangeAABB(), (var1x) -> {
         return var1x.getUUID().equals(this.destroyTargetUUID);
      });
      return var1.size() == 1 ? (LivingEntity)var1.get(0) : null;
   }

   private void animationTick() {
      Random var1 = this.level.random;
      float var2 = Mth.sin((float)(this.tickCount + 35) * 0.1F) / 2.0F + 0.5F;
      var2 = (var2 * var2 + var2) * 0.3F;
      Vec3 var3 = new Vec3((double)((float)this.worldPosition.getX() + 0.5F), (double)((float)this.worldPosition.getY() + 1.5F + var2), (double)((float)this.worldPosition.getZ() + 0.5F));
      Iterator var4 = this.effectBlocks.iterator();

      float var6;
      float var7;
      while(var4.hasNext()) {
         BlockPos var5 = (BlockPos)var4.next();
         if (var1.nextInt(50) == 0) {
            var6 = -0.5F + var1.nextFloat();
            var7 = -2.0F + var1.nextFloat();
            float var8 = -0.5F + var1.nextFloat();
            BlockPos var9 = var5.subtract(this.worldPosition);
            Vec3 var10 = (new Vec3((double)var6, (double)var7, (double)var8)).add((double)var9.getX(), (double)var9.getY(), (double)var9.getZ());
            this.level.addParticle(ParticleTypes.NAUTILUS, var3.x, var3.y, var3.z, var10.x, var10.y, var10.z);
         }
      }

      if (this.destroyTarget != null) {
         Vec3 var11 = new Vec3(this.destroyTarget.x, this.destroyTarget.y + (double)this.destroyTarget.getEyeHeight(), this.destroyTarget.z);
         float var12 = (-0.5F + var1.nextFloat()) * (3.0F + this.destroyTarget.getBbWidth());
         var6 = -1.0F + var1.nextFloat() * this.destroyTarget.getBbHeight();
         var7 = (-0.5F + var1.nextFloat()) * (3.0F + this.destroyTarget.getBbWidth());
         Vec3 var13 = new Vec3((double)var12, (double)var6, (double)var7);
         this.level.addParticle(ParticleTypes.NAUTILUS, var11.x, var11.y, var11.z, var13.x, var13.y, var13.z);
      }

   }

   public boolean isActive() {
      return this.isActive;
   }

   public boolean isHunting() {
      return this.isHunting;
   }

   private void setActive(boolean var1) {
      if (var1 != this.isActive) {
         this.playSound(var1 ? SoundEvents.CONDUIT_ACTIVATE : SoundEvents.CONDUIT_DEACTIVATE);
      }

      this.isActive = var1;
   }

   private void setHunting(boolean var1) {
      this.isHunting = var1;
   }

   public float getActiveRotation(float var1) {
      return (this.activeRotation + var1) * -0.0375F;
   }

   public void playSound(SoundEvent var1) {
      this.level.playSound((Player)null, (BlockPos)this.worldPosition, var1, SoundSource.BLOCKS, 1.0F, 1.0F);
   }

   static {
      VALID_BLOCKS = new Block[]{Blocks.PRISMARINE, Blocks.PRISMARINE_BRICKS, Blocks.SEA_LANTERN, Blocks.DARK_PRISMARINE};
   }
}

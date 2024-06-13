package net.minecraft.world.entity;

import java.util.List;
import java.util.Map.Entry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddExperienceOrbPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ExperienceOrb extends Entity {
   private static final int LIFETIME = 6000;
   private static final int ENTITY_SCAN_PERIOD = 20;
   private static final int MAX_FOLLOW_DIST = 8;
   private static final int ORB_GROUPS_PER_AREA = 40;
   private static final double ORB_MERGE_DISTANCE = 0.5;
   private int age;
   private int health = 5;
   private int value;
   private int count = 1;
   private Player followingPlayer;

   public ExperienceOrb(Level var1, double var2, double var4, double var6, int var8) {
      this(EntityType.EXPERIENCE_ORB, var1);
      this.setPos(var2, var4, var6);
      this.setYRot((float)(this.random.nextDouble() * 360.0));
      this.setDeltaMovement(
         (this.random.nextDouble() * 0.20000000298023224 - 0.10000000149011612) * 2.0,
         this.random.nextDouble() * 0.2 * 2.0,
         (this.random.nextDouble() * 0.20000000298023224 - 0.10000000149011612) * 2.0
      );
      this.value = var8;
   }

   public ExperienceOrb(EntityType<? extends ExperienceOrb> var1, Level var2) {
      super(var1, var2);
   }

   @Override
   protected Entity.MovementEmission getMovementEmission() {
      return Entity.MovementEmission.NONE;
   }

   @Override
   protected void defineSynchedData(SynchedEntityData.Builder var1) {
   }

   @Override
   protected double getDefaultGravity() {
      return 0.03;
   }

   @Override
   public void tick() {
      super.tick();
      this.xo = this.getX();
      this.yo = this.getY();
      this.zo = this.getZ();
      if (this.isEyeInFluid(FluidTags.WATER)) {
         this.setUnderwaterMovement();
      } else {
         this.applyGravity();
      }

      if (this.level().getFluidState(this.blockPosition()).is(FluidTags.LAVA)) {
         this.setDeltaMovement(
            (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.2F),
            0.20000000298023224,
            (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.2F)
         );
      }

      if (!this.level().noCollision(this.getBoundingBox())) {
         this.moveTowardsClosestSpace(this.getX(), (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0, this.getZ());
      }

      if (this.tickCount % 20 == 1) {
         this.scanForEntities();
      }

      if (this.followingPlayer != null && (this.followingPlayer.isSpectator() || this.followingPlayer.isDeadOrDying())) {
         this.followingPlayer = null;
      }

      if (this.followingPlayer != null) {
         Vec3 var1 = new Vec3(
            this.followingPlayer.getX() - this.getX(),
            this.followingPlayer.getY() + (double)this.followingPlayer.getEyeHeight() / 2.0 - this.getY(),
            this.followingPlayer.getZ() - this.getZ()
         );
         double var2 = var1.lengthSqr();
         if (var2 < 64.0) {
            double var4 = 1.0 - Math.sqrt(var2) / 8.0;
            this.setDeltaMovement(this.getDeltaMovement().add(var1.normalize().scale(var4 * var4 * 0.1)));
         }
      }

      this.move(MoverType.SELF, this.getDeltaMovement());
      float var6 = 0.98F;
      if (this.onGround()) {
         var6 = this.level().getBlockState(this.getBlockPosBelowThatAffectsMyMovement()).getBlock().getFriction() * 0.98F;
      }

      this.setDeltaMovement(this.getDeltaMovement().multiply((double)var6, 0.98, (double)var6));
      if (this.onGround()) {
         this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, -0.9, 1.0));
      }

      this.age++;
      if (this.age >= 6000) {
         this.discard();
      }
   }

   @Override
   protected BlockPos getBlockPosBelowThatAffectsMyMovement() {
      return this.getOnPos(0.999999F);
   }

   private void scanForEntities() {
      if (this.followingPlayer == null || this.followingPlayer.distanceToSqr(this) > 64.0) {
         this.followingPlayer = this.level().getNearestPlayer(this, 8.0);
      }

      if (this.level() instanceof ServerLevel) {
         for (ExperienceOrb var3 : this.level().getEntities(EntityTypeTest.forClass(ExperienceOrb.class), this.getBoundingBox().inflate(0.5), this::canMerge)) {
            this.merge(var3);
         }
      }
   }

   public static void award(ServerLevel var0, Vec3 var1, int var2) {
      while (var2 > 0) {
         int var3 = getExperienceValue(var2);
         var2 -= var3;
         if (!tryMergeToExisting(var0, var1, var3)) {
            var0.addFreshEntity(new ExperienceOrb(var0, var1.x(), var1.y(), var1.z(), var3));
         }
      }
   }

   private static boolean tryMergeToExisting(ServerLevel var0, Vec3 var1, int var2) {
      AABB var3 = AABB.ofSize(var1, 1.0, 1.0, 1.0);
      int var4 = var0.getRandom().nextInt(40);
      List var5 = var0.getEntities(EntityTypeTest.forClass(ExperienceOrb.class), var3, var2x -> canMerge(var2x, var4, var2));
      if (!var5.isEmpty()) {
         ExperienceOrb var6 = (ExperienceOrb)var5.get(0);
         var6.count++;
         var6.age = 0;
         return true;
      } else {
         return false;
      }
   }

   private boolean canMerge(ExperienceOrb var1) {
      return var1 != this && canMerge(var1, this.getId(), this.value);
   }

   private static boolean canMerge(ExperienceOrb var0, int var1, int var2) {
      return !var0.isRemoved() && (var0.getId() - var1) % 40 == 0 && var0.value == var2;
   }

   private void merge(ExperienceOrb var1) {
      this.count = this.count + var1.count;
      this.age = Math.min(this.age, var1.age);
      var1.discard();
   }

   private void setUnderwaterMovement() {
      Vec3 var1 = this.getDeltaMovement();
      this.setDeltaMovement(var1.x * 0.9900000095367432, Math.min(var1.y + 5.000000237487257E-4, 0.05999999865889549), var1.z * 0.9900000095367432);
   }

   @Override
   protected void doWaterSplashEffect() {
   }

   @Override
   public boolean hurt(DamageSource var1, float var2) {
      if (this.isInvulnerableTo(var1)) {
         return false;
      } else if (this.level().isClientSide) {
         return true;
      } else {
         this.markHurt();
         this.health = (int)((float)this.health - var2);
         if (this.health <= 0) {
            this.discard();
         }

         return true;
      }
   }

   @Override
   public void addAdditionalSaveData(CompoundTag var1) {
      var1.putShort("Health", (short)this.health);
      var1.putShort("Age", (short)this.age);
      var1.putShort("Value", (short)this.value);
      var1.putInt("Count", this.count);
   }

   @Override
   public void readAdditionalSaveData(CompoundTag var1) {
      this.health = var1.getShort("Health");
      this.age = var1.getShort("Age");
      this.value = var1.getShort("Value");
      this.count = Math.max(var1.getInt("Count"), 1);
   }

   @Override
   public void playerTouch(Player var1) {
      if (!this.level().isClientSide) {
         if (var1.takeXpDelay == 0) {
            var1.takeXpDelay = 2;
            var1.take(this, 1);
            int var2 = this.repairPlayerItems(var1, this.value);
            if (var2 > 0) {
               var1.giveExperiencePoints(var2);
            }

            this.count--;
            if (this.count == 0) {
               this.discard();
            }
         }
      }
   }

   private int repairPlayerItems(Player var1, int var2) {
      Entry var3 = EnchantmentHelper.getRandomItemWith(Enchantments.MENDING, var1, ItemStack::isDamaged);
      if (var3 != null) {
         ItemStack var4 = (ItemStack)var3.getValue();
         int var5 = Math.min(this.xpToDurability(var2), var4.getDamageValue());
         var4.setDamageValue(var4.getDamageValue() - var5);
         int var6 = var2 - this.durabilityToXp(var5);
         return var6 > 0 ? this.repairPlayerItems(var1, var6) : 0;
      } else {
         return var2;
      }
   }

   private int durabilityToXp(int var1) {
      return var1 / 2;
   }

   private int xpToDurability(int var1) {
      return var1 * 2;
   }

   public int getValue() {
      return this.value;
   }

   public int getIcon() {
      if (this.value >= 2477) {
         return 10;
      } else if (this.value >= 1237) {
         return 9;
      } else if (this.value >= 617) {
         return 8;
      } else if (this.value >= 307) {
         return 7;
      } else if (this.value >= 149) {
         return 6;
      } else if (this.value >= 73) {
         return 5;
      } else if (this.value >= 37) {
         return 4;
      } else if (this.value >= 17) {
         return 3;
      } else if (this.value >= 7) {
         return 2;
      } else {
         return this.value >= 3 ? 1 : 0;
      }
   }

   public static int getExperienceValue(int var0) {
      if (var0 >= 2477) {
         return 2477;
      } else if (var0 >= 1237) {
         return 1237;
      } else if (var0 >= 617) {
         return 617;
      } else if (var0 >= 307) {
         return 307;
      } else if (var0 >= 149) {
         return 149;
      } else if (var0 >= 73) {
         return 73;
      } else if (var0 >= 37) {
         return 37;
      } else if (var0 >= 17) {
         return 17;
      } else if (var0 >= 7) {
         return 7;
      } else {
         return var0 >= 3 ? 3 : 1;
      }
   }

   @Override
   public boolean isAttackable() {
      return false;
   }

   @Override
   public Packet<ClientGamePacketListener> getAddEntityPacket() {
      return new ClientboundAddExperienceOrbPacket(this);
   }

   @Override
   public SoundSource getSoundSource() {
      return SoundSource.AMBIENT;
   }
}

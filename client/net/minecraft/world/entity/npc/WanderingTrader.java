package net.minecraft.world.entity.npc;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.InteractGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.LookAtTradingPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.TradeWithPlayerGoal;
import net.minecraft.world.entity.ai.goal.UseItemGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.monster.Illusioner;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.entity.monster.Zoglin;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;

public class WanderingTrader extends AbstractVillager {
   private static final int NUMBER_OF_TRADE_OFFERS = 5;
   @Nullable
   private BlockPos wanderTarget;
   private int despawnDelay;

   public WanderingTrader(EntityType<? extends WanderingTrader> var1, Level var2) {
      super(var1, var2);
   }

   @Override
   protected void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector
         .addGoal(
            0,
            new UseItemGoal<>(
               this,
               PotionContents.createItemStack(Items.POTION, Potions.INVISIBILITY),
               SoundEvents.WANDERING_TRADER_DISAPPEARED,
               var1 -> this.level().isNight() && !var1.isInvisible()
            )
         );
      this.goalSelector
         .addGoal(
            0,
            new UseItemGoal<>(
               this, new ItemStack(Items.MILK_BUCKET), SoundEvents.WANDERING_TRADER_REAPPEARED, var1 -> this.level().isDay() && var1.isInvisible()
            )
         );
      this.goalSelector.addGoal(1, new TradeWithPlayerGoal(this));
      this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Zombie.class, 8.0F, 0.5, 0.5));
      this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Evoker.class, 12.0F, 0.5, 0.5));
      this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Vindicator.class, 8.0F, 0.5, 0.5));
      this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Vex.class, 8.0F, 0.5, 0.5));
      this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Pillager.class, 15.0F, 0.5, 0.5));
      this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Illusioner.class, 12.0F, 0.5, 0.5));
      this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Zoglin.class, 10.0F, 0.5, 0.5));
      this.goalSelector.addGoal(1, new PanicGoal(this, 0.5));
      this.goalSelector.addGoal(1, new LookAtTradingPlayerGoal(this));
      this.goalSelector.addGoal(2, new WanderingTrader.WanderToPositionGoal(this, 2.0, 0.35));
      this.goalSelector.addGoal(4, new MoveTowardsRestrictionGoal(this, 0.35));
      this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 0.35));
      this.goalSelector.addGoal(9, new InteractGoal(this, Player.class, 3.0F, 1.0F));
      this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
   }

   @Nullable
   @Override
   public AgeableMob getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      return null;
   }

   @Override
   public boolean showProgressBar() {
      return false;
   }

   @Override
   public InteractionResult mobInteract(Player var1, InteractionHand var2) {
      ItemStack var3 = var1.getItemInHand(var2);
      if (!var3.is(Items.VILLAGER_SPAWN_EGG) && this.isAlive() && !this.isTrading() && !this.isBaby()) {
         if (var2 == InteractionHand.MAIN_HAND) {
            var1.awardStat(Stats.TALKED_TO_VILLAGER);
         }

         if (this.getOffers().isEmpty()) {
            return InteractionResult.sidedSuccess(this.level().isClientSide);
         } else {
            if (!this.level().isClientSide) {
               this.setTradingPlayer(var1);
               this.openTradingScreen(var1, this.getDisplayName(), 1);
            }

            return InteractionResult.sidedSuccess(this.level().isClientSide);
         }
      } else {
         return super.mobInteract(var1, var2);
      }
   }

   @Override
   protected void updateTrades() {
      if (this.level().enabledFeatures().contains(FeatureFlags.TRADE_REBALANCE)) {
         this.experimentalUpdateTrades();
      } else {
         VillagerTrades.ItemListing[] var1 = (VillagerTrades.ItemListing[])VillagerTrades.WANDERING_TRADER_TRADES.get(1);
         VillagerTrades.ItemListing[] var2 = (VillagerTrades.ItemListing[])VillagerTrades.WANDERING_TRADER_TRADES.get(2);
         if (var1 != null && var2 != null) {
            MerchantOffers var3 = this.getOffers();
            this.addOffersFromItemListings(var3, var1, 5);
            int var4 = this.random.nextInt(var2.length);
            VillagerTrades.ItemListing var5 = var2[var4];
            MerchantOffer var6 = var5.getOffer(this, this.random);
            if (var6 != null) {
               var3.add(var6);
            }
         }
      }
   }

   private void experimentalUpdateTrades() {
      MerchantOffers var1 = this.getOffers();

      for (Pair var3 : VillagerTrades.EXPERIMENTAL_WANDERING_TRADER_TRADES) {
         VillagerTrades.ItemListing[] var4 = (VillagerTrades.ItemListing[])var3.getLeft();
         this.addOffersFromItemListings(var1, var4, (Integer)var3.getRight());
      }
   }

   @Override
   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putInt("DespawnDelay", this.despawnDelay);
      if (this.wanderTarget != null) {
         var1.put("wander_target", NbtUtils.writeBlockPos(this.wanderTarget));
      }
   }

   @Override
   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      if (var1.contains("DespawnDelay", 99)) {
         this.despawnDelay = var1.getInt("DespawnDelay");
      }

      NbtUtils.readBlockPos(var1, "wander_target").ifPresent(var1x -> this.wanderTarget = var1x);
      this.setAge(Math.max(0, this.getAge()));
   }

   @Override
   public boolean removeWhenFarAway(double var1) {
      return false;
   }

   @Override
   protected void rewardTradeXp(MerchantOffer var1) {
      if (var1.shouldRewardExp()) {
         int var2 = 3 + this.random.nextInt(4);
         this.level().addFreshEntity(new ExperienceOrb(this.level(), this.getX(), this.getY() + 0.5, this.getZ(), var2));
      }
   }

   @Override
   protected SoundEvent getAmbientSound() {
      return this.isTrading() ? SoundEvents.WANDERING_TRADER_TRADE : SoundEvents.WANDERING_TRADER_AMBIENT;
   }

   @Override
   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.WANDERING_TRADER_HURT;
   }

   @Override
   protected SoundEvent getDeathSound() {
      return SoundEvents.WANDERING_TRADER_DEATH;
   }

   @Override
   protected SoundEvent getDrinkingSound(ItemStack var1) {
      return var1.is(Items.MILK_BUCKET) ? SoundEvents.WANDERING_TRADER_DRINK_MILK : SoundEvents.WANDERING_TRADER_DRINK_POTION;
   }

   @Override
   protected SoundEvent getTradeUpdatedSound(boolean var1) {
      return var1 ? SoundEvents.WANDERING_TRADER_YES : SoundEvents.WANDERING_TRADER_NO;
   }

   @Override
   public SoundEvent getNotifyTradeSound() {
      return SoundEvents.WANDERING_TRADER_YES;
   }

   public void setDespawnDelay(int var1) {
      this.despawnDelay = var1;
   }

   public int getDespawnDelay() {
      return this.despawnDelay;
   }

   @Override
   public void aiStep() {
      super.aiStep();
      if (!this.level().isClientSide) {
         this.maybeDespawn();
      }
   }

   private void maybeDespawn() {
      if (this.despawnDelay > 0 && !this.isTrading() && --this.despawnDelay == 0) {
         this.discard();
      }
   }

   public void setWanderTarget(@Nullable BlockPos var1) {
      this.wanderTarget = var1;
   }

   @Nullable
   BlockPos getWanderTarget() {
      return this.wanderTarget;
   }

   class WanderToPositionGoal extends Goal {
      final WanderingTrader trader;
      final double stopDistance;
      final double speedModifier;

      WanderToPositionGoal(final WanderingTrader nullx, final double nullxx, final double nullxxx) {
         super();
         this.trader = nullx;
         this.stopDistance = nullxx;
         this.speedModifier = nullxxx;
         this.setFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      @Override
      public void stop() {
         this.trader.setWanderTarget(null);
         WanderingTrader.this.navigation.stop();
      }

      @Override
      public boolean canUse() {
         BlockPos var1 = this.trader.getWanderTarget();
         return var1 != null && this.isTooFarAway(var1, this.stopDistance);
      }

      @Override
      public void tick() {
         BlockPos var1 = this.trader.getWanderTarget();
         if (var1 != null && WanderingTrader.this.navigation.isDone()) {
            if (this.isTooFarAway(var1, 10.0)) {
               Vec3 var2 = new Vec3(
                     (double)var1.getX() - this.trader.getX(), (double)var1.getY() - this.trader.getY(), (double)var1.getZ() - this.trader.getZ()
                  )
                  .normalize();
               Vec3 var3 = var2.scale(10.0).add(this.trader.getX(), this.trader.getY(), this.trader.getZ());
               WanderingTrader.this.navigation.moveTo(var3.x, var3.y, var3.z, this.speedModifier);
            } else {
               WanderingTrader.this.navigation.moveTo((double)var1.getX(), (double)var1.getY(), (double)var1.getZ(), this.speedModifier);
            }
         }
      }

      private boolean isTooFarAway(BlockPos var1, double var2) {
         return !var1.closerToCenterThan(this.trader.position(), var2);
      }
   }
}

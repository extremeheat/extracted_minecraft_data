package net.minecraft.world.entity;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public interface Leashable {
   String LEASH_TAG = "leash";
   double LEASH_TOO_FAR_DIST = 12.0;
   double LEASH_ELASTIC_DIST = 6.0;
   double MAXIMUM_ALLOWED_LEASHED_DIST = 16.0;
   Vec3 AXIS_SPECIFIC_ELASTICITY = new Vec3(0.8, 0.2, 0.8);
   float SPRING_DAMPENING = 0.7F;
   double TORSIONAL_ELASTICITY = 10.0;
   double STIFFNESS = 0.11;
   List<Vec3> ENTITY_ATTACHMENT_POINT = ImmutableList.of(new Vec3(0.0, 0.5, 0.5));
   List<Vec3> LEASHER_ATTACHMENT_POINT = ImmutableList.of(new Vec3(0.0, 0.5, 0.0));
   List<Vec3> SHARED_QUAD_ATTACHMENT_POINTS = ImmutableList.of(new Vec3(-0.5, 0.5, 0.5), new Vec3(-0.5, 0.5, -0.5), new Vec3(0.5, 0.5, -0.5), new Vec3(0.5, 0.5, 0.5));

   @Nullable
   LeashData getLeashData();

   void setLeashData(@Nullable LeashData var1);

   default boolean isLeashed() {
      return this.getLeashData() != null && this.getLeashData().leashHolder != null;
   }

   default boolean mayBeLeashed() {
      return this.getLeashData() != null;
   }

   default boolean canHaveALeashAttachedTo(Entity var1) {
      if (this == var1) {
         return false;
      } else {
         return this.leashDistanceTo(var1) > this.leashSnapDistance() ? false : this.canBeLeashed();
      }
   }

   default double leashDistanceTo(Entity var1) {
      return var1.getBoundingBox().getCenter().distanceTo(((Entity)this).getBoundingBox().getCenter());
   }

   default boolean canBeLeashed() {
      return true;
   }

   default void setDelayedLeashHolderId(int var1) {
      this.setLeashData(new LeashData(var1));
      dropLeash((Entity)this, false, false);
   }

   default void readLeashData(ValueInput var1) {
      LeashData var2 = (LeashData)var1.read("leash", Leashable.LeashData.CODEC).orElse((Object)null);
      if (this.getLeashData() != null && var2 == null) {
         this.removeLeash();
      }

      this.setLeashData(var2);
   }

   default void writeLeashData(ValueOutput var1, @Nullable LeashData var2) {
      var1.storeNullable("leash", Leashable.LeashData.CODEC, var2);
   }

   private static <E extends Entity & Leashable> void restoreLeashFromSave(E var0, LeashData var1) {
      if (var1.delayedLeashInfo != null) {
         Level var3 = var0.level();
         if (var3 instanceof ServerLevel) {
            ServerLevel var2 = (ServerLevel)var3;
            Optional var6 = var1.delayedLeashInfo.left();
            Optional var4 = var1.delayedLeashInfo.right();
            if (var6.isPresent()) {
               Entity var5 = var2.getEntity((UUID)var6.get());
               if (var5 != null) {
                  setLeashedTo(var0, var5, true);
                  return;
               }
            } else if (var4.isPresent()) {
               setLeashedTo(var0, LeashFenceKnotEntity.getOrCreateKnot(var2, (BlockPos)var4.get()), true);
               return;
            }

            if (var0.tickCount > 100) {
               var0.spawnAtLocation(var2, (ItemLike)Items.LEAD);
               ((Leashable)var0).setLeashData((LeashData)null);
            }
         }
      }

   }

   default void dropLeash() {
      dropLeash((Entity)this, true, true);
   }

   default void removeLeash() {
      dropLeash((Entity)this, true, false);
   }

   default void onLeashRemoved() {
   }

   private static <E extends Entity & Leashable> void dropLeash(E var0, boolean var1, boolean var2) {
      LeashData var3 = ((Leashable)var0).getLeashData();
      if (var3 != null && var3.leashHolder != null) {
         ((Leashable)var0).setLeashData((LeashData)null);
         ((Leashable)var0).onLeashRemoved();
         Level var5 = var0.level();
         if (var5 instanceof ServerLevel) {
            ServerLevel var4 = (ServerLevel)var5;
            if (var2) {
               var0.spawnAtLocation(var4, (ItemLike)Items.LEAD);
            }

            if (var1) {
               var4.getChunkSource().broadcast(var0, new ClientboundSetEntityLinkPacket(var0, (Entity)null));
            }

            var3.leashHolder.notifyLeasheeRemoved((Leashable)var0);
         }
      }

   }

   static <E extends Entity & Leashable> void tickLeash(ServerLevel var0, E var1) {
      LeashData var2 = ((Leashable)var1).getLeashData();
      if (var2 != null && var2.delayedLeashInfo != null) {
         restoreLeashFromSave(var1, var2);
      }

      if (var2 != null && var2.leashHolder != null) {
         if (!var1.isAlive() || !var2.leashHolder.isAlive()) {
            if (var0.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
               ((Leashable)var1).dropLeash();
            } else {
               ((Leashable)var1).removeLeash();
            }
         }

         Entity var3 = ((Leashable)var1).getLeashHolder();
         if (var3 != null && var3.level() == var1.level()) {
            double var4 = ((Leashable)var1).leashDistanceTo(var3);
            ((Leashable)var1).whenLeashedTo(var3);
            if (var4 > ((Leashable)var1).leashSnapDistance()) {
               var0.playSound((Entity)null, var3.getX(), var3.getY(), var3.getZ(), SoundEvents.LEAD_BREAK, SoundSource.NEUTRAL, 1.0F, 1.0F);
               ((Leashable)var1).leashTooFarBehaviour();
            } else if (var4 > ((Leashable)var1).leashElasticDistance() - (double)var3.getBbWidth() - (double)var1.getBbWidth() && ((Leashable)var1).checkElasticInteractions(var3, var2)) {
               ((Leashable)var1).onElasticLeashPull();
            } else {
               ((Leashable)var1).closeRangeLeashBehaviour(var3);
            }

            var1.setYRot((float)((double)var1.getYRot() - var2.angularMomentum));
            var2.angularMomentum *= (double)angularFriction(var1);
         }

      }
   }

   default void onElasticLeashPull() {
      Entity var1 = (Entity)this;
      var1.checkFallDistanceAccumulation();
   }

   default double leashSnapDistance() {
      return 12.0;
   }

   default double leashElasticDistance() {
      return 6.0;
   }

   static <E extends Entity & Leashable> float angularFriction(E var0) {
      if (var0.onGround()) {
         return var0.level().getBlockState(var0.getBlockPosBelowThatAffectsMyMovement()).getBlock().getFriction() * 0.91F;
      } else {
         return var0.isInLiquid() ? 0.8F : 0.91F;
      }
   }

   default void whenLeashedTo(Entity var1) {
      var1.notifyLeashHolder(this);
   }

   default void leashTooFarBehaviour() {
      this.dropLeash();
   }

   default void closeRangeLeashBehaviour(Entity var1) {
   }

   default boolean checkElasticInteractions(Entity var1, LeashData var2) {
      boolean var3 = var1.supportQuadLeashAsHolder() && this.supportQuadLeash();
      List var4 = computeElasticInteraction((Entity)this, var1, var3 ? SHARED_QUAD_ATTACHMENT_POINTS : ENTITY_ATTACHMENT_POINT, var3 ? SHARED_QUAD_ATTACHMENT_POINTS : LEASHER_ATTACHMENT_POINT);
      if (var4.isEmpty()) {
         return false;
      } else {
         Wrench var5 = Leashable.Wrench.accumulate(var4).scale(var3 ? 0.25 : 1.0);
         var2.angularMomentum += 10.0 * var5.torque();
         Vec3 var6 = getHolderMovement(var1).subtract(((Entity)this).getKnownMovement());
         ((Entity)this).addDeltaMovement(var5.force().multiply(AXIS_SPECIFIC_ELASTICITY).add(var6.scale(0.11)));
         return true;
      }
   }

   private static Vec3 getHolderMovement(Entity var0) {
      if (var0 instanceof Mob var1) {
         if (var1.isNoAi()) {
            return Vec3.ZERO;
         }
      }

      return var0.getKnownMovement();
   }

   private static <E extends Entity & Leashable> List<Wrench> computeElasticInteraction(E var0, Entity var1, List<Vec3> var2, List<Vec3> var3) {
      double var4 = ((Leashable)var0).leashElasticDistance();
      Vec3 var6 = getHolderMovement(var0);
      float var7 = var0.getYRot() * 0.017453292F;
      Vec3 var8 = new Vec3((double)var0.getBbWidth(), (double)var0.getBbHeight(), (double)var0.getBbWidth());
      float var9 = var1.getYRot() * 0.017453292F;
      Vec3 var10 = new Vec3((double)var1.getBbWidth(), (double)var1.getBbHeight(), (double)var1.getBbWidth());
      ArrayList var11 = new ArrayList();

      for(int var12 = 0; var12 < var2.size(); ++var12) {
         Vec3 var13 = ((Vec3)var2.get(var12)).multiply(var8).yRot(-var7);
         Vec3 var14 = var0.position().add(var13);
         Vec3 var15 = ((Vec3)var3.get(var12)).multiply(var10).yRot(-var9);
         Vec3 var16 = var1.position().add(var15);
         Optional var10000 = computeDampenedSpringInteraction(var16, var14, var4, var6, var13);
         Objects.requireNonNull(var11);
         var10000.ifPresent(var11::add);
      }

      return var11;
   }

   private static Optional<Wrench> computeDampenedSpringInteraction(Vec3 var0, Vec3 var1, double var2, Vec3 var4, Vec3 var5) {
      double var6 = var1.distanceTo(var0);
      if (var6 < var2) {
         return Optional.empty();
      } else {
         Vec3 var8 = var0.subtract(var1).normalize().scale(var6 - var2);
         double var9 = Leashable.Wrench.torqueFromForce(var5, var8);
         boolean var11 = var4.dot(var8) >= 0.0;
         if (var11) {
            var8 = var8.scale(0.30000001192092896);
         }

         return Optional.of(new Wrench(var8, var9));
      }
   }

   default boolean supportQuadLeash() {
      return false;
   }

   default Vec3[] getQuadLeashOffsets() {
      return createQuadLeashOffsets((Entity)this, 0.0, 0.5, 0.5, 0.5);
   }

   static Vec3[] createQuadLeashOffsets(Entity var0, double var1, double var3, double var5, double var7) {
      float var9 = var0.getBbWidth();
      double var10 = var1 * (double)var9;
      double var12 = var3 * (double)var9;
      double var14 = var5 * (double)var9;
      double var16 = var7 * (double)var0.getBbHeight();
      return new Vec3[]{new Vec3(-var14, var16, var12 + var10), new Vec3(-var14, var16, -var12 + var10), new Vec3(var14, var16, -var12 + var10), new Vec3(var14, var16, var12 + var10)};
   }

   default Vec3 getLeashOffset(float var1) {
      return this.getLeashOffset();
   }

   default Vec3 getLeashOffset() {
      Entity var1 = (Entity)this;
      return new Vec3(0.0, (double)var1.getEyeHeight(), (double)(var1.getBbWidth() * 0.4F));
   }

   default void setLeashedTo(Entity var1, boolean var2) {
      if (this != var1) {
         setLeashedTo((Entity)this, var1, var2);
      }
   }

   private static <E extends Entity & Leashable> void setLeashedTo(E var0, Entity var1, boolean var2) {
      LeashData var3 = ((Leashable)var0).getLeashData();
      if (var3 == null) {
         var3 = new LeashData(var1);
         ((Leashable)var0).setLeashData(var3);
      } else {
         Entity var4 = var3.leashHolder;
         var3.setLeashHolder(var1);
         if (var4 != null && var4 != var1) {
            var4.notifyLeasheeRemoved((Leashable)var0);
         }
      }

      if (var2) {
         Level var5 = var0.level();
         if (var5 instanceof ServerLevel) {
            ServerLevel var7 = (ServerLevel)var5;
            var7.getChunkSource().broadcast(var0, new ClientboundSetEntityLinkPacket(var0, var1));
         }
      }

      if (var0.isPassenger()) {
         var0.stopRiding();
      }

   }

   @Nullable
   default Entity getLeashHolder() {
      return getLeashHolder((Entity)this);
   }

   @Nullable
   private static <E extends Entity & Leashable> Entity getLeashHolder(E var0) {
      LeashData var1 = ((Leashable)var0).getLeashData();
      if (var1 == null) {
         return null;
      } else {
         if (var1.delayedLeashHolderId != 0 && var0.level().isClientSide()) {
            Entity var3 = var0.level().getEntity(var1.delayedLeashHolderId);
            if (var3 instanceof Entity) {
               var1.setLeashHolder(var3);
            }
         }

         return var1.leashHolder;
      }
   }

   static List<Leashable> leashableLeashedTo(Entity var0) {
      return leashableInArea(var0, (var1) -> var1.getLeashHolder() == var0);
   }

   static List<Leashable> leashableInArea(Entity var0, Predicate<Leashable> var1) {
      return leashableInArea(var0.level(), var0.getBoundingBox().getCenter(), var1);
   }

   static List<Leashable> leashableInArea(Level var0, Vec3 var1, Predicate<Leashable> var2) {
      double var3 = 32.0;
      AABB var5 = AABB.ofSize(var1, 32.0, 32.0, 32.0);
      Stream var10000 = var0.getEntitiesOfClass(Entity.class, var5, (var1x) -> {
         boolean var10000;
         if (var1x instanceof Leashable var2x) {
            if (var2.test(var2x)) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }).stream();
      Objects.requireNonNull(Leashable.class);
      return var10000.map(Leashable.class::cast).toList();
   }

   public static final class LeashData {
      public static final Codec<LeashData> CODEC;
      int delayedLeashHolderId;
      @Nullable
      public Entity leashHolder;
      @Nullable
      public Either<UUID, BlockPos> delayedLeashInfo;
      public double angularMomentum;

      private LeashData(Either<UUID, BlockPos> var1) {
         super();
         this.delayedLeashInfo = var1;
      }

      LeashData(Entity var1) {
         super();
         this.leashHolder = var1;
      }

      LeashData(int var1) {
         super();
         this.delayedLeashHolderId = var1;
      }

      public void setLeashHolder(Entity var1) {
         this.leashHolder = var1;
         this.delayedLeashInfo = null;
         this.delayedLeashHolderId = 0;
      }

      static {
         CODEC = Codec.xor(UUIDUtil.CODEC.fieldOf("UUID").codec(), BlockPos.CODEC).xmap(LeashData::new, (var0) -> {
            Entity var2 = var0.leashHolder;
            if (var2 instanceof LeashFenceKnotEntity var1) {
               return Either.right(var1.getPos());
            } else {
               return var0.leashHolder != null ? Either.left(var0.leashHolder.getUUID()) : (Either)Objects.requireNonNull(var0.delayedLeashInfo, "Invalid LeashData had no attachment");
            }
         });
      }
   }

   public static record Wrench(Vec3 force, double torque) {
      static Wrench ZERO;

      public Wrench(Vec3 var1, double var2) {
         super();
         this.force = var1;
         this.torque = var2;
      }

      static double torqueFromForce(Vec3 var0, Vec3 var1) {
         return var0.z * var1.x - var0.x * var1.z;
      }

      static Wrench accumulate(List<Wrench> var0) {
         if (var0.isEmpty()) {
            return ZERO;
         } else {
            double var1 = 0.0;
            double var3 = 0.0;
            double var5 = 0.0;
            double var7 = 0.0;

            for(Wrench var10 : var0) {
               Vec3 var11 = var10.force;
               var1 += var11.x;
               var3 += var11.y;
               var5 += var11.z;
               var7 += var10.torque;
            }

            return new Wrench(new Vec3(var1, var3, var5), var7);
         }
      }

      public Wrench scale(double var1) {
         return new Wrench(this.force.scale(var1), this.torque * var1);
      }

      static {
         ZERO = new Wrench(Vec3.ZERO, 0.0);
      }
   }
}

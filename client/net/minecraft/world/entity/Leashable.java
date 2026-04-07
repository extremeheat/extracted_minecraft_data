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
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

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

   @Nullable LeashData getLeashData();

   void setLeashData(@Nullable LeashData leashData);

   default boolean isLeashed() {
      return this.getLeashData() != null && this.getLeashData().leashHolder != null;
   }

   default boolean mayBeLeashed() {
      return this.getLeashData() != null;
   }

   default boolean canHaveALeashAttachedTo(final Entity entity) {
      if (this == entity) {
         return false;
      } else {
         return this.leashDistanceTo(entity) > this.leashSnapDistance() ? false : this.canBeLeashed();
      }
   }

   default double leashDistanceTo(final Entity entity) {
      return entity.getBoundingBox().getCenter().distanceTo(((Entity)this).getBoundingBox().getCenter());
   }

   default boolean canBeLeashed() {
      return true;
   }

   default void setDelayedLeashHolderId(final int entityId) {
      this.setLeashData(new LeashData(entityId));
      dropLeash((Entity)this, false, false);
   }

   default void readLeashData(final ValueInput input) {
      LeashData newLeashData = (LeashData)input.read("leash", Leashable.LeashData.CODEC).orElse((Object)null);
      if (this.getLeashData() != null && newLeashData == null) {
         this.removeLeash();
      }

      this.setLeashData(newLeashData);
   }

   default void writeLeashData(final ValueOutput output, final @Nullable LeashData leashData) {
      output.storeNullable("leash", Leashable.LeashData.CODEC, leashData);
   }

   private static <E extends Entity & Leashable> void restoreLeashFromSave(final E entity, final LeashData leashData) {
      if (leashData.delayedLeashInfo != null) {
         Level var3 = entity.level();
         if (var3 instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel)var3;
            Optional<UUID> leashUuid = leashData.delayedLeashInfo.left();
            Optional<BlockPos> pos = leashData.delayedLeashInfo.right();
            if (leashUuid.isPresent()) {
               Entity leasher = serverLevel.getEntity((UUID)leashUuid.get());
               if (leasher != null) {
                  setLeashedTo(entity, leasher, true);
                  return;
               }
            } else if (pos.isPresent()) {
               setLeashedTo(entity, LeashFenceKnotEntity.getOrCreateKnot(serverLevel, (BlockPos)pos.get()), true);
               return;
            }

            if (entity.tickCount > 100) {
               entity.spawnAtLocation(serverLevel, (ItemLike)Items.LEAD);
               ((Leashable)entity).setLeashData((LeashData)null);
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

   private static <E extends Entity & Leashable> void dropLeash(final E entity, final boolean sendPacket, final boolean dropLead) {
      LeashData leashData = ((Leashable)entity).getLeashData();
      if (leashData != null && leashData.leashHolder != null) {
         ((Leashable)entity).setLeashData((LeashData)null);
         ((Leashable)entity).onLeashRemoved();
         Level var5 = entity.level();
         if (var5 instanceof ServerLevel) {
            ServerLevel level = (ServerLevel)var5;
            if (dropLead) {
               entity.spawnAtLocation(level, (ItemLike)Items.LEAD);
            }

            if (sendPacket) {
               level.getChunkSource().sendToTrackingPlayers(entity, new ClientboundSetEntityLinkPacket(entity, (Entity)null));
            }

            leashData.leashHolder.notifyLeasheeRemoved(entity);
         }
      }

   }

   static <E extends Entity & Leashable> void tickLeash(final ServerLevel level, final E entity) {
      LeashData leashData = ((Leashable)entity).getLeashData();
      if (leashData != null && leashData.delayedLeashInfo != null) {
         restoreLeashFromSave(entity, leashData);
      }

      if (leashData != null && leashData.leashHolder != null) {
         if (!entity.canInteractWithLevel() || !leashData.leashHolder.canInteractWithLevel()) {
            if ((Boolean)level.getGameRules().get(GameRules.ENTITY_DROPS)) {
               ((Leashable)entity).dropLeash();
            } else {
               ((Leashable)entity).removeLeash();
            }
         }

         Entity leashHolder = ((Leashable)entity).getLeashHolder();
         if (leashHolder != null && leashHolder.level() == entity.level()) {
            double distanceTo = ((Leashable)entity).leashDistanceTo(leashHolder);
            ((Leashable)entity).whenLeashedTo(leashHolder);
            if (distanceTo > ((Leashable)entity).leashSnapDistance()) {
               level.playSound((Entity)null, leashHolder.getX(), leashHolder.getY(), leashHolder.getZ(), SoundEvents.LEAD_BREAK, SoundSource.NEUTRAL, 1.0F, 1.0F);
               ((Leashable)entity).leashTooFarBehaviour();
            } else if (distanceTo > ((Leashable)entity).leashElasticDistance() - (double)leashHolder.getBbWidth() - (double)entity.getBbWidth() && ((Leashable)entity).checkElasticInteractions(leashHolder, leashData)) {
               ((Leashable)entity).onElasticLeashPull();
            } else {
               ((Leashable)entity).closeRangeLeashBehaviour(leashHolder);
            }

            entity.setYRot((float)((double)entity.getYRot() - leashData.angularMomentum));
            leashData.angularMomentum *= (double)angularFriction(entity);
         }

      }
   }

   default void onElasticLeashPull() {
      Entity entity = (Entity)this;
      entity.checkFallDistanceAccumulation();
   }

   default double leashSnapDistance() {
      return 12.0;
   }

   default double leashElasticDistance() {
      return 6.0;
   }

   static <E extends Entity & Leashable> float angularFriction(final E entity) {
      if (entity.onGround()) {
         return entity.level().getBlockState(entity.getBlockPosBelowThatAffectsMyMovement()).getBlock().getFriction() * 0.91F;
      } else {
         return entity.isInLiquid() ? 0.8F : 0.91F;
      }
   }

   default void whenLeashedTo(final Entity leashHolder) {
      leashHolder.notifyLeashHolder(this);
   }

   default void leashTooFarBehaviour() {
      this.dropLeash();
   }

   default void closeRangeLeashBehaviour(final Entity leashHolder) {
   }

   default boolean checkElasticInteractions(final Entity leashHolder, final LeashData leashData) {
      boolean quadConnection = leashHolder.supportQuadLeashAsHolder() && this.supportQuadLeash();
      List<Wrench> wrenches = computeElasticInteraction((Entity)this, leashHolder, quadConnection ? SHARED_QUAD_ATTACHMENT_POINTS : ENTITY_ATTACHMENT_POINT, quadConnection ? SHARED_QUAD_ATTACHMENT_POINTS : LEASHER_ATTACHMENT_POINT);
      if (wrenches.isEmpty()) {
         return false;
      } else {
         Wrench result = Leashable.Wrench.accumulate(wrenches).scale(quadConnection ? 0.25 : 1.0);
         leashData.angularMomentum += 10.0 * result.torque();
         Vec3 relativeVelocityToLeasher = getHolderMovement(leashHolder).subtract(((Entity)this).getKnownMovement());
         ((Entity)this).addDeltaMovement(result.force().multiply(AXIS_SPECIFIC_ELASTICITY).add(relativeVelocityToLeasher.scale(0.11)));
         return true;
      }
   }

   private static Vec3 getHolderMovement(final Entity leashHolder) {
      if (leashHolder instanceof Mob mob) {
         if (mob.isNoAi()) {
            return Vec3.ZERO;
         }
      }

      return leashHolder.getKnownMovement();
   }

   private static <E extends Entity & Leashable> List<Wrench> computeElasticInteraction(final E entity, final Entity leashHolder, final List<Vec3> entityAttachmentPoints, final List<Vec3> leasherAttachmentPoints) {
      double slackDistance = ((Leashable)entity).leashElasticDistance();
      Vec3 currentMovement = getHolderMovement(entity);
      float entityYRot = entity.getYRot() * 0.017453292F;
      Vec3 entityDimensions = new Vec3((double)entity.getBbWidth(), (double)entity.getBbHeight(), (double)entity.getBbWidth());
      float leashHolderYRot = leashHolder.getYRot() * 0.017453292F;
      Vec3 leasherDimensions = new Vec3((double)leashHolder.getBbWidth(), (double)leashHolder.getBbHeight(), (double)leashHolder.getBbWidth());
      List<Wrench> wrenches = new ArrayList();

      for(int i = 0; i < entityAttachmentPoints.size(); ++i) {
         Vec3 entityAttachVector = ((Vec3)entityAttachmentPoints.get(i)).multiply(entityDimensions).yRot(-entityYRot);
         Vec3 entityAttachPos = entity.position().add(entityAttachVector);
         Vec3 leasherAttachVector = ((Vec3)leasherAttachmentPoints.get(i)).multiply(leasherDimensions).yRot(-leashHolderYRot);
         Vec3 leasherAttachPos = leashHolder.position().add(leasherAttachVector);
         Optional var10000 = computeDampenedSpringInteraction(leasherAttachPos, entityAttachPos, slackDistance, currentMovement, entityAttachVector);
         Objects.requireNonNull(wrenches);
         var10000.ifPresent(wrenches::add);
      }

      return wrenches;
   }

   private static Optional<Wrench> computeDampenedSpringInteraction(final Vec3 pivotPoint, final Vec3 objectPosition, final double springSlack, final Vec3 objectMotion, final Vec3 leverArm) {
      double distance = objectPosition.distanceTo(pivotPoint);
      if (distance < springSlack) {
         return Optional.empty();
      } else {
         Vec3 displacement = pivotPoint.subtract(objectPosition).normalize().scale(distance - springSlack);
         double torque = Leashable.Wrench.torqueFromForce(leverArm, displacement);
         boolean sameDirectionToMovement = objectMotion.dot(displacement) >= 0.0;
         if (sameDirectionToMovement) {
            displacement = displacement.scale(0.30000001192092896);
         }

         return Optional.of(new Wrench(displacement, torque));
      }
   }

   default boolean supportQuadLeash() {
      return false;
   }

   default Vec3[] getQuadLeashOffsets() {
      return createQuadLeashOffsets((Entity)this, 0.0, 0.5, 0.5, 0.5);
   }

   static Vec3[] createQuadLeashOffsets(final Entity entity, final double frontOffset, final double frontBack, final double leftRight, final double height) {
      float width = entity.getBbWidth();
      double frontOffsetScaled = frontOffset * (double)width;
      double frontBackScaled = frontBack * (double)width;
      double leftRightScaled = leftRight * (double)width;
      double heightScaled = height * (double)entity.getBbHeight();
      return new Vec3[]{new Vec3(-leftRightScaled, heightScaled, frontBackScaled + frontOffsetScaled), new Vec3(-leftRightScaled, heightScaled, -frontBackScaled + frontOffsetScaled), new Vec3(leftRightScaled, heightScaled, -frontBackScaled + frontOffsetScaled), new Vec3(leftRightScaled, heightScaled, frontBackScaled + frontOffsetScaled)};
   }

   default Vec3 getLeashOffset(final float partialTicks) {
      return this.getLeashOffset();
   }

   default Vec3 getLeashOffset() {
      Entity entity = (Entity)this;
      return new Vec3(0.0, (double)entity.getEyeHeight(), (double)(entity.getBbWidth() * 0.4F));
   }

   default void setLeashedTo(final Entity holder, final boolean synch) {
      if (this != holder) {
         setLeashedTo((Entity)this, holder, synch);
      }
   }

   private static <E extends Entity & Leashable> void setLeashedTo(final E entity, final Entity holder, final boolean synch) {
      LeashData leashData = ((Leashable)entity).getLeashData();
      if (leashData == null) {
         leashData = new LeashData(holder);
         ((Leashable)entity).setLeashData(leashData);
      } else {
         Entity oldHolder = leashData.leashHolder;
         leashData.setLeashHolder(holder);
         if (oldHolder != null && oldHolder != holder) {
            oldHolder.notifyLeasheeRemoved(entity);
         }
      }

      if (synch) {
         Level var5 = entity.level();
         if (var5 instanceof ServerLevel) {
            ServerLevel level = (ServerLevel)var5;
            level.getChunkSource().sendToTrackingPlayers(entity, new ClientboundSetEntityLinkPacket(entity, holder));
         }
      }

      if (entity.isPassenger()) {
         entity.stopRiding();
      }

   }

   default @Nullable Entity getLeashHolder() {
      return getLeashHolder((Entity)this);
   }

   private static <E extends Entity & Leashable> @Nullable Entity getLeashHolder(final E entity) {
      LeashData leashData = ((Leashable)entity).getLeashData();
      if (leashData == null) {
         return null;
      } else {
         if (leashData.delayedLeashHolderId != 0 && entity.level().isClientSide()) {
            Entity ntt = entity.level().getEntity(leashData.delayedLeashHolderId);
            if (ntt instanceof Entity) {
               leashData.setLeashHolder(ntt);
            }
         }

         return leashData.leashHolder;
      }
   }

   static List<Leashable> leashableLeashedTo(final Entity entity) {
      return leashableInArea(entity, (l) -> l.getLeashHolder() == entity);
   }

   static List<Leashable> leashableInArea(final Entity entity, final Predicate<Leashable> test) {
      return leashableInArea(entity.level(), entity.getBoundingBox().getCenter(), test);
   }

   static List<Leashable> leashableInArea(final Level level, final Vec3 pos, final Predicate<Leashable> test) {
      double size = 32.0;
      AABB scanArea = AABB.ofSize(pos, 32.0, 32.0, 32.0);
      Stream var10000 = level.getEntitiesOfClass(Entity.class, scanArea, (e) -> {
         boolean var10000;
         if (e instanceof Leashable leashable) {
            if (test.test(leashable)) {
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
      private int delayedLeashHolderId;
      public @Nullable Entity leashHolder;
      public @Nullable Either<UUID, BlockPos> delayedLeashInfo;
      public double angularMomentum;

      private LeashData(final Either<UUID, BlockPos> delayedLeashInfo) {
         super();
         this.delayedLeashInfo = delayedLeashInfo;
      }

      private LeashData(final Entity entity) {
         super();
         this.leashHolder = entity;
      }

      private LeashData(final int entityId) {
         super();
         this.delayedLeashHolderId = entityId;
      }

      public void setLeashHolder(final Entity leashHolder) {
         this.leashHolder = leashHolder;
         this.delayedLeashInfo = null;
         this.delayedLeashHolderId = 0;
      }

      static {
         CODEC = Codec.xor(UUIDUtil.CODEC.fieldOf("UUID").codec(), BlockPos.CODEC).xmap(LeashData::new, (data) -> {
            Entity patt0$temp = data.leashHolder;
            if (patt0$temp instanceof LeashFenceKnotEntity leashKnot) {
               return Either.right(leashKnot.getPos());
            } else {
               return data.leashHolder != null ? Either.left(data.leashHolder.getUUID()) : (Either)Objects.requireNonNull(data.delayedLeashInfo, "Invalid LeashData had no attachment");
            }
         });
      }
   }

   public static record Wrench(Vec3 force, double torque) {
      static final Wrench ZERO;

      public Wrench {
         super();
      }

      static double torqueFromForce(final Vec3 leverArm, final Vec3 force) {
         return leverArm.z * force.x - leverArm.x * force.z;
      }

      static Wrench accumulate(final List<Wrench> wrenches) {
         if (wrenches.isEmpty()) {
            return ZERO;
         } else {
            double x = 0.0;
            double y = 0.0;
            double z = 0.0;
            double t = 0.0;

            for(Wrench wrench : wrenches) {
               Vec3 force = wrench.force;
               x += force.x;
               y += force.y;
               z += force.z;
               t += wrench.torque;
            }

            return new Wrench(new Vec3(x, y, z), t);
         }
      }

      public Wrench scale(final double scale) {
         return new Wrench(this.force.scale(scale), this.torque * scale);
      }

      static {
         ZERO = new Wrench(Vec3.ZERO, 0.0);
      }
   }
}

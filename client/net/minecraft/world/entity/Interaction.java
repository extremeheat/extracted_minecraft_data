package net.minecraft.world.entity;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import org.slf4j.Logger;

public class Interaction extends Entity implements Attackable, Targeting {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final EntityDataAccessor<Float> DATA_WIDTH_ID = SynchedEntityData.defineId(Interaction.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> DATA_HEIGHT_ID = SynchedEntityData.defineId(Interaction.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Boolean> DATA_RESPONSE_ID = SynchedEntityData.defineId(Interaction.class, EntityDataSerializers.BOOLEAN);
   private static final String TAG_WIDTH = "width";
   private static final String TAG_HEIGHT = "height";
   private static final String TAG_ATTACK = "attack";
   private static final String TAG_INTERACTION = "interaction";
   private static final String TAG_RESPONSE = "response";
   @Nullable
   private Interaction.PlayerAction attack;
   @Nullable
   private Interaction.PlayerAction interaction;

   public Interaction(EntityType<?> var1, Level var2) {
      super(var1, var2);
      this.noPhysics = true;
   }

   @Override
   protected void defineSynchedData() {
      this.entityData.define(DATA_WIDTH_ID, 1.0F);
      this.entityData.define(DATA_HEIGHT_ID, 1.0F);
      this.entityData.define(DATA_RESPONSE_ID, false);
   }

   @Override
   protected void readAdditionalSaveData(CompoundTag var1) {
      if (var1.contains("width", 99)) {
         this.setWidth(var1.getFloat("width"));
      }

      if (var1.contains("height", 99)) {
         this.setHeight(var1.getFloat("height"));
      }

      if (var1.contains("attack")) {
         Interaction.PlayerAction.CODEC
            .decode(NbtOps.INSTANCE, var1.get("attack"))
            .resultOrPartial(Util.prefix("Interaction entity", LOGGER::error))
            .ifPresent(var1x -> this.attack = (Interaction.PlayerAction)var1x.getFirst());
      } else {
         this.attack = null;
      }

      if (var1.contains("interaction")) {
         Interaction.PlayerAction.CODEC
            .decode(NbtOps.INSTANCE, var1.get("interaction"))
            .resultOrPartial(Util.prefix("Interaction entity", LOGGER::error))
            .ifPresent(var1x -> this.interaction = (Interaction.PlayerAction)var1x.getFirst());
      } else {
         this.interaction = null;
      }

      this.setResponse(var1.getBoolean("response"));
      this.setBoundingBox(this.makeBoundingBox());
   }

   @Override
   protected void addAdditionalSaveData(CompoundTag var1) {
      var1.putFloat("width", this.getWidth());
      var1.putFloat("height", this.getHeight());
      if (this.attack != null) {
         Interaction.PlayerAction.CODEC.encodeStart(NbtOps.INSTANCE, this.attack).result().ifPresent(var1x -> var1.put("attack", var1x));
      }

      if (this.interaction != null) {
         Interaction.PlayerAction.CODEC.encodeStart(NbtOps.INSTANCE, this.interaction).result().ifPresent(var1x -> var1.put("interaction", var1x));
      }

      var1.putBoolean("response", this.getResponse());
   }

   @Override
   public void onSyncedDataUpdated(EntityDataAccessor<?> var1) {
      super.onSyncedDataUpdated(var1);
      if (DATA_HEIGHT_ID.equals(var1) || DATA_WIDTH_ID.equals(var1)) {
         this.setBoundingBox(this.makeBoundingBox());
      }
   }

   @Override
   public boolean canBeHitByProjectile() {
      return false;
   }

   @Override
   public boolean isPickable() {
      return true;
   }

   @Override
   public PushReaction getPistonPushReaction() {
      return PushReaction.IGNORE;
   }

   @Override
   public boolean isIgnoringBlockTriggers() {
      return true;
   }

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public boolean skipAttackInteraction(Entity var1) {
      if (var1 instanceof Player var2) {
         this.attack = new Interaction.PlayerAction(var2.getUUID(), this.level().getGameTime());
         if (var2 instanceof ServerPlayer var3) {
            CriteriaTriggers.PLAYER_HURT_ENTITY.trigger((ServerPlayer)var3, this, var2.damageSources().generic(), 1.0F, 1.0F, false);
         }

         return !this.getResponse();
      } else {
         return false;
      }
   }

   @Override
   public InteractionResult interact(Player var1, InteractionHand var2) {
      if (this.level().isClientSide) {
         return this.getResponse() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
      } else {
         this.interaction = new Interaction.PlayerAction(var1.getUUID(), this.level().getGameTime());
         return InteractionResult.CONSUME;
      }
   }

   @Override
   public void tick() {
   }

   @Nullable
   @Override
   public LivingEntity getLastAttacker() {
      return this.attack != null ? this.level().getPlayerByUUID(this.attack.player()) : null;
   }

   @Nullable
   @Override
   public LivingEntity getTarget() {
      return this.interaction != null ? this.level().getPlayerByUUID(this.interaction.player()) : null;
   }

   private void setWidth(float var1) {
      this.entityData.set(DATA_WIDTH_ID, var1);
   }

   private float getWidth() {
      return this.entityData.get(DATA_WIDTH_ID);
   }

   private void setHeight(float var1) {
      this.entityData.set(DATA_HEIGHT_ID, var1);
   }

   private float getHeight() {
      return this.entityData.get(DATA_HEIGHT_ID);
   }

   private void setResponse(boolean var1) {
      this.entityData.set(DATA_RESPONSE_ID, var1);
   }

   private boolean getResponse() {
      return this.entityData.get(DATA_RESPONSE_ID);
   }

   private EntityDimensions getDimensions() {
      return EntityDimensions.scalable(this.getWidth(), this.getHeight());
   }

   @Override
   public EntityDimensions getDimensions(Pose var1) {
      return this.getDimensions();
   }

   @Override
   protected AABB makeBoundingBox() {
      return this.getDimensions().makeBoundingBox(this.position());
   }

   static record PlayerAction(UUID b, long c) {
      private final UUID player;
      private final long timestamp;
      public static final Codec<Interaction.PlayerAction> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  UUIDUtil.CODEC.fieldOf("player").forGetter(Interaction.PlayerAction::player),
                  Codec.LONG.fieldOf("timestamp").forGetter(Interaction.PlayerAction::timestamp)
               )
               .apply(var0, Interaction.PlayerAction::new)
      );

      PlayerAction(UUID var1, long var2) {
         super();
         this.player = var1;
         this.timestamp = (long)var2;
      }
   }
}

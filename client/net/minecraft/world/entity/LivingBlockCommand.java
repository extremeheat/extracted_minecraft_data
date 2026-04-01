package net.minecraft.world.entity;

import java.util.function.IntFunction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

public class LivingBlockCommand extends Entity {
   public static final int LIFETIME_TICKS = 100;
   private static final EntityDataAccessor<Integer> DATA_TYPE;
   private static final EntityDataAccessor<Long> DATA_SPAWNTIME;
   private static final String TAG_TYPE = "command_type";
   private static final String TAG_SPAWN_TIME = "spawn_time";

   public LivingBlockCommand(final EntityType<?> type, final Level level) {
      super(type, level);
   }

   public static LivingBlockCommand create(final Level level, final Vec3 pos, final Type type) {
      LivingBlockCommand command = new LivingBlockCommand(EntityType.LIVING_BLOCK_COMMAND, level);
      command.setPos(pos);
      command.setCommandType(type);
      command.setSpawnTime(level.getGameTime());
      return command;
   }

   public Type getCommandType() {
      return (Type)LivingBlockCommand.Type.BY_ID.apply((Integer)this.entityData.get(DATA_TYPE));
   }

   public void setCommandType(final Type type) {
      this.entityData.set(DATA_TYPE, type.getId());
   }

   public long getSpawnTime() {
      return (Long)this.entityData.get(DATA_SPAWNTIME);
   }

   public void setSpawnTime(final long time) {
      this.entityData.set(DATA_SPAWNTIME, time);
   }

   protected void defineSynchedData(final SynchedEntityData.Builder entityData) {
      entityData.define(DATA_TYPE, LivingBlockCommand.Type.TYPE_MOVE.getId());
      entityData.define(DATA_SPAWNTIME, 0L);
   }

   public boolean hurtServer(final ServerLevel level, final DamageSource source, final float damage) {
      return false;
   }

   public void tick() {
      super.tick();
      if (this.level().getGameTime() > this.getSpawnTime() + 100L) {
         this.discard();
      }

   }

   protected void readAdditionalSaveData(final ValueInput input) {
      input.read("command_type", LivingBlockCommand.Type.CODEC).ifPresent(this::setCommandType);
      this.setSpawnTime(input.getLongOr("spawn_time", 0L));
   }

   protected void addAdditionalSaveData(final ValueOutput output) {
      output.store("command_type", LivingBlockCommand.Type.CODEC, this.getCommandType());
      output.putLong("spawn_time", this.getSpawnTime());
   }

   static {
      DATA_TYPE = SynchedEntityData.<Integer>defineId(LivingBlockCommand.class, EntityDataSerializers.INT);
      DATA_SPAWNTIME = SynchedEntityData.<Long>defineId(LivingBlockCommand.class, EntityDataSerializers.LONG);
   }

   public static enum Type implements StringRepresentable {
      TYPE_MOVE(0, "move"),
      TYPE_ATTACK(1, "attack"),
      TYPE_FOLLOW(2, "follow"),
      TYPE_BUILD(3, "build");

      public static final StringRepresentable.EnumCodec<Type> CODEC = StringRepresentable.<Type>fromEnum(Type::values);
      private static final IntFunction<Type> BY_ID = ByIdMap.<Type>continuous(Type::getId, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
      private final int id;
      private final String name;

      private Type(final int id, final String name) {
         this.id = id;
         this.name = name;
      }

      public String getSerializedName() {
         return this.name;
      }

      public int getId() {
         return this.id;
      }

      // $FF: synthetic method
      private static Type[] $values() {
         return new Type[]{TYPE_MOVE, TYPE_ATTACK, TYPE_FOLLOW, TYPE_BUILD};
      }
   }
}

package net.minecraft.world.level.mines;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;

public interface MineEvent {
   Codec<MineEvent> CODEC = BuiltInRegistries.MINE_EVENT_TYPE.byNameCodec().dispatch(MineEvent::codec, Function.identity());

   void tick(ServerLevel var1);

   void end(ServerLevel var1, boolean var2);

   BlockPos getPosition();

   Status getStatus();

   MapCodec<? extends MineEvent> codec();

   static MapCodec<? extends MineEvent> bootstrap(Registry<MapCodec<? extends MineEvent>> var0) {
      Registry.register(var0, (String)"end_dragon_battle", EndDragonBattle.CODEC);
      Registry.register(var0, (String)"raid", RaidEvent.CODEC);
      return (MapCodec)Registry.register(var0, (String)"battle", Battle.CODEC);
   }

   public static enum Status implements StringRepresentable {
      ACTIVE,
      WON,
      FAILED;

      public static final Codec<Status> CODEC = StringRepresentable.<Status>fromEnum(Status::values);

      private Status() {
      }

      public String getSerializedName() {
         return this.name();
      }

      // $FF: synthetic method
      private static Status[] $values() {
         return new Status[]{ACTIVE, WON, FAILED};
      }
   }
}

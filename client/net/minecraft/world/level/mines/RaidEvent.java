package net.minecraft.world.level.mines;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raids;

public class RaidEvent implements MineEvent {
   public static final MapCodec<RaidEvent> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(BlockPos.CODEC.fieldOf("pos").forGetter((var0x) -> var0x.position), MineEvent.Status.CODEC.fieldOf("win_status").forGetter((var0x) -> var0x.winStatus)).apply(var0, RaidEvent::new));
   private BlockPos position;
   private MineEvent.Status winStatus;

   public RaidEvent(BlockPos var1, MineEvent.Status var2) {
      super();
      this.position = var1;
      this.winStatus = var2;
   }

   public RaidEvent() {
      this(BlockPos.ZERO, MineEvent.Status.ACTIVE);
   }

   public void tick(ServerLevel var1) {
      Raids var2 = var1.getRaids();
      Raid var3 = var2.getNearbyRaid(this.position, 100000);
      if (var3 != null) {
         this.position = var3.getCenter();
         if (var3.isOver()) {
            this.winStatus = var3.isVictory() ? MineEvent.Status.WON : MineEvent.Status.FAILED;
         }
      }

   }

   public void end(ServerLevel var1, boolean var2) {
   }

   public BlockPos getPosition() {
      return this.position;
   }

   public MineEvent.Status getStatus() {
      return this.winStatus;
   }

   public MapCodec<RaidEvent> codec() {
      return CODEC;
   }
}

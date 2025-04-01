package net.minecraft.world.level.mines;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.dimension.end.EndDragonFight;

public class EndDragonBattle implements MineEvent {
   public static final MapCodec<EndDragonBattle> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(Codec.BOOL.fieldOf("completed").forGetter((var0x) -> var0x.completed), BlockPos.CODEC.fieldOf("pos").forGetter((var0x) -> var0x.pos)).apply(var0, EndDragonBattle::new));
   private boolean completed;
   private BlockPos pos;

   public void complete() {
      this.completed = true;
   }

   public EndDragonBattle(boolean var1, BlockPos var2) {
      super();
      this.completed = var1;
      this.pos = var2;
   }

   public EndDragonBattle() {
      this(false, BlockPos.ZERO);
   }

   public void tick(ServerLevel var1) {
      if (!this.completed && var1.dragonFight == null) {
         var1.dragonFight = new EndDragonFight(var1, var1.getSeed(), new EndDragonFight.Data(false, true, true, false, Optional.empty(), Optional.empty()));
         this.pos = var1.dragonFight.spawnExitPortal(false);
         ArrayList var2 = new ArrayList();

         for(Direction var4 : List.of(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST)) {
            BlockPos var5 = this.pos.above().relative((Direction)var4, 3);
            EndCrystal var6 = new EndCrystal(var1, (double)((float)var5.getX() + 0.5F), (double)var5.getY(), (double)((float)var5.getZ() + 0.5F));
            var6.setShowBottom(false);
            var6.setInvulnerable(true);
            var1.addFreshEntity(var6);
            var2.add(var6);
         }

         var1.dragonFight.respawnDragon(var2);
      } else if (!this.completed && var1.dragonFight.portalLocation != null) {
         this.pos = var1.dragonFight.portalLocation;
      }

   }

   public void end(ServerLevel var1, boolean var2) {
      UUID var3 = var1.dragonFight != null ? var1.dragonFight.getDragonUUID() : null;
      Entity var4 = var3 != null ? var1.getEntity(var3) : null;
      if (var4 != null) {
         var4.kill(var1);
      }

      var1.dragonFight = null;
   }

   public BlockPos getPosition() {
      return this.pos;
   }

   public MineEvent.Status getStatus() {
      return this.completed ? MineEvent.Status.WON : MineEvent.Status.ACTIVE;
   }

   public MapCodec<EndDragonBattle> codec() {
      return CODEC;
   }
}

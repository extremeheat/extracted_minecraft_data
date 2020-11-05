package net.minecraft.world.entity.decoration;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddPaintingPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

public class Painting extends HangingEntity {
   public Motive motive;

   public Painting(EntityType<? extends Painting> var1, Level var2) {
      super(var1, var2);
   }

   public Painting(Level var1, BlockPos var2, Direction var3) {
      super(EntityType.PAINTING, var1, var2);
      ArrayList var4 = Lists.newArrayList();
      int var5 = 0;
      Iterator var6 = Registry.MOTIVE.iterator();

      Motive var7;
      while(var6.hasNext()) {
         var7 = (Motive)var6.next();
         this.motive = var7;
         this.setDirection(var3);
         if (this.survives()) {
            var4.add(var7);
            int var8 = var7.getWidth() * var7.getHeight();
            if (var8 > var5) {
               var5 = var8;
            }
         }
      }

      if (!var4.isEmpty()) {
         var6 = var4.iterator();

         while(var6.hasNext()) {
            var7 = (Motive)var6.next();
            if (var7.getWidth() * var7.getHeight() < var5) {
               var6.remove();
            }
         }

         this.motive = (Motive)var4.get(this.random.nextInt(var4.size()));
      }

      this.setDirection(var3);
   }

   public Painting(Level var1, BlockPos var2, Direction var3, Motive var4) {
      this(var1, var2, var3);
      this.motive = var4;
      this.setDirection(var3);
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      var1.putString("Motive", Registry.MOTIVE.getKey(this.motive).toString());
      var1.putByte("Facing", (byte)this.direction.get2DDataValue());
      super.addAdditionalSaveData(var1);
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      this.motive = (Motive)Registry.MOTIVE.get(ResourceLocation.tryParse(var1.getString("Motive")));
      this.direction = Direction.from2DDataValue(var1.getByte("Facing"));
      super.readAdditionalSaveData(var1);
      this.setDirection(this.direction);
   }

   public int getWidth() {
      return this.motive == null ? 1 : this.motive.getWidth();
   }

   public int getHeight() {
      return this.motive == null ? 1 : this.motive.getHeight();
   }

   public void dropItem(@Nullable Entity var1) {
      if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
         this.playSound(SoundEvents.PAINTING_BREAK, 1.0F, 1.0F);
         if (var1 instanceof Player) {
            Player var2 = (Player)var1;
            if (var2.getAbilities().instabuild) {
               return;
            }
         }

         this.spawnAtLocation(Items.PAINTING);
      }
   }

   public void playPlacementSound() {
      this.playSound(SoundEvents.PAINTING_PLACE, 1.0F, 1.0F);
   }

   public void moveTo(double var1, double var3, double var5, float var7, float var8) {
      this.setPos(var1, var3, var5);
   }

   public void lerpTo(double var1, double var3, double var5, float var7, float var8, int var9, boolean var10) {
      BlockPos var11 = this.pos.offset(var1 - this.getX(), var3 - this.getY(), var5 - this.getZ());
      this.setPos((double)var11.getX(), (double)var11.getY(), (double)var11.getZ());
   }

   public Packet<?> getAddEntityPacket() {
      return new ClientboundAddPaintingPacket(this);
   }

   public ItemStack getPickResult() {
      return new ItemStack(Items.PAINTING);
   }
}

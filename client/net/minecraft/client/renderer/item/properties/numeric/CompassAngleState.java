package net.minecraft.client.renderer.item.properties.numeric;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.LodestoneTracker;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class CompassAngleState extends NeedleDirectionHelper {
   public static final MapCodec<CompassAngleState> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(Codec.BOOL.optionalFieldOf("wobble", true).forGetter(NeedleDirectionHelper::wobble), CompassAngleState.CompassTarget.CODEC.fieldOf("target").forGetter(CompassAngleState::target)).apply(var0, CompassAngleState::new));
   private final NeedleDirectionHelper.Wobbler wobbler = this.newWobbler(0.8F);
   private final NeedleDirectionHelper.Wobbler noTargetWobbler = this.newWobbler(0.8F);
   private final CompassTarget compassTarget;
   private final RandomSource random = RandomSource.create();

   public CompassAngleState(boolean var1, CompassTarget var2) {
      super(var1);
      this.compassTarget = var2;
   }

   protected float calculate(ItemStack var1, ClientLevel var2, int var3, ItemOwner var4) {
      GlobalPos var5 = this.compassTarget.get(var2, var1, var4);
      long var6 = var2.getGameTime();
      return !isValidCompassTargetPos(var4, var5) ? this.getRandomlySpinningRotation(var3, var6) : this.getRotationTowardsCompassTarget(var4, var6, var5.pos());
   }

   private float getRandomlySpinningRotation(int var1, long var2) {
      if (this.noTargetWobbler.shouldUpdate(var2)) {
         this.noTargetWobbler.update(var2, this.random.nextFloat());
      }

      float var4 = this.noTargetWobbler.rotation() + (float)hash(var1) / 2.1474836E9F;
      return Mth.positiveModulo(var4, 1.0F);
   }

   private float getRotationTowardsCompassTarget(ItemOwner var1, long var2, BlockPos var4) {
      float var5 = (float)getAngleFromEntityToPos(var1, var4);
      float var6 = getWrappedVisualRotationY(var1);
      LivingEntity var8 = var1.asLivingEntity();
      float var7;
      if (var8 instanceof Player var9) {
         if (var9.isLocalPlayer() && var9.level().tickRateManager().runsNormally()) {
            if (this.wobbler.shouldUpdate(var2)) {
               this.wobbler.update(var2, 0.5F - (var6 - 0.25F));
            }

            var7 = var5 + this.wobbler.rotation();
            return Mth.positiveModulo(var7, 1.0F);
         }
      }

      var7 = 0.5F - (var6 - 0.25F - var5);
      return Mth.positiveModulo(var7, 1.0F);
   }

   private static boolean isValidCompassTargetPos(ItemOwner var0, @Nullable GlobalPos var1) {
      return var1 != null && var1.dimension() == var0.level().dimension() && !(var1.pos().distToCenterSqr(var0.position()) < 9.999999747378752E-6);
   }

   private static double getAngleFromEntityToPos(ItemOwner var0, BlockPos var1) {
      Vec3 var2 = Vec3.atCenterOf(var1);
      Vec3 var3 = var0.position();
      return Math.atan2(var2.z() - var3.z(), var2.x() - var3.x()) / 6.2831854820251465;
   }

   private static float getWrappedVisualRotationY(ItemOwner var0) {
      return Mth.positiveModulo(var0.getVisualRotationYInDegrees() / 360.0F, 1.0F);
   }

   private static int hash(int var0) {
      return var0 * 1327217883;
   }

   protected CompassTarget target() {
      return this.compassTarget;
   }

   public static enum CompassTarget implements StringRepresentable {
      NONE("none") {
         public @Nullable GlobalPos get(ClientLevel var1, ItemStack var2, @Nullable ItemOwner var3) {
            return null;
         }
      },
      LODESTONE("lodestone") {
         public @Nullable GlobalPos get(ClientLevel var1, ItemStack var2, @Nullable ItemOwner var3) {
            LodestoneTracker var4 = (LodestoneTracker)var2.get(DataComponents.LODESTONE_TRACKER);
            return var4 != null ? (GlobalPos)var4.target().orElse((Object)null) : null;
         }
      },
      SPAWN("spawn") {
         public GlobalPos get(ClientLevel var1, ItemStack var2, @Nullable ItemOwner var3) {
            return var1.getRespawnData().globalPos();
         }
      },
      RECOVERY("recovery") {
         public @Nullable GlobalPos get(ClientLevel var1, ItemStack var2, @Nullable ItemOwner var3) {
            LivingEntity var4 = var3 == null ? null : var3.asLivingEntity();
            GlobalPos var10000;
            if (var4 instanceof Player var5) {
               var10000 = (GlobalPos)var5.getLastDeathLocation().orElse((Object)null);
            } else {
               var10000 = null;
            }

            return var10000;
         }
      };

      public static final Codec<CompassTarget> CODEC = StringRepresentable.<CompassTarget>fromEnum(CompassTarget::values);
      private final String name;

      CompassTarget(final String var3) {
         this.name = var3;
      }

      public String getSerializedName() {
         return this.name;
      }

      abstract @Nullable GlobalPos get(ClientLevel var1, ItemStack var2, @Nullable ItemOwner var3);

      // $FF: synthetic method
      private static CompassTarget[] $values() {
         return new CompassTarget[]{NONE, LODESTONE, SPAWN, RECOVERY};
      }
   }
}

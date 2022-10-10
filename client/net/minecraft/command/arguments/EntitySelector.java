package net.minecraft.command.arguments;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.world.WorldServer;

public class EntitySelector {
   private final int field_197354_a;
   private final boolean field_197355_b;
   private final boolean field_197356_c;
   private final Predicate<Entity> field_197357_d;
   private final MinMaxBounds.FloatBound field_197358_e;
   private final Function<Vec3d, Vec3d> field_197359_f;
   @Nullable
   private final AxisAlignedBB field_197360_g;
   private final BiConsumer<Vec3d, List<? extends Entity>> field_197361_h;
   private final boolean field_197362_i;
   @Nullable
   private final String field_197363_j;
   @Nullable
   private final UUID field_197364_k;
   private final Class<? extends Entity> field_201957_l;
   private final boolean field_210325_m;

   public EntitySelector(int var1, boolean var2, boolean var3, Predicate<Entity> var4, MinMaxBounds.FloatBound var5, Function<Vec3d, Vec3d> var6, @Nullable AxisAlignedBB var7, BiConsumer<Vec3d, List<? extends Entity>> var8, boolean var9, @Nullable String var10, @Nullable UUID var11, Class<? extends Entity> var12, boolean var13) {
      super();
      this.field_197354_a = var1;
      this.field_197355_b = var2;
      this.field_197356_c = var3;
      this.field_197357_d = var4;
      this.field_197358_e = var5;
      this.field_197359_f = var6;
      this.field_197360_g = var7;
      this.field_197361_h = var8;
      this.field_197362_i = var9;
      this.field_197363_j = var10;
      this.field_197364_k = var11;
      this.field_201957_l = var12;
      this.field_210325_m = var13;
   }

   public int func_197346_a() {
      return this.field_197354_a;
   }

   public boolean func_197351_b() {
      return this.field_197355_b;
   }

   public boolean func_197352_c() {
      return this.field_197362_i;
   }

   public boolean func_197353_d() {
      return this.field_197356_c;
   }

   private void func_210324_e(CommandSource var1) throws CommandSyntaxException {
      if (this.field_210325_m && !var1.func_197034_c(2)) {
         throw EntityArgument.field_210323_f.create();
      }
   }

   public Entity func_197340_a(CommandSource var1) throws CommandSyntaxException {
      this.func_210324_e(var1);
      List var2 = this.func_197341_b(var1);
      if (var2.isEmpty()) {
         throw EntityArgument.field_197101_d.create();
      } else if (var2.size() > 1) {
         throw EntityArgument.field_197098_a.create();
      } else {
         return (Entity)var2.get(0);
      }
   }

   public List<? extends Entity> func_197341_b(CommandSource var1) throws CommandSyntaxException {
      this.func_210324_e(var1);
      if (!this.field_197355_b) {
         return this.func_197342_d(var1);
      } else if (this.field_197363_j != null) {
         EntityPlayerMP var8 = var1.func_197028_i().func_184103_al().func_152612_a(this.field_197363_j);
         return (List)(var8 == null ? Collections.emptyList() : Lists.newArrayList(new EntityPlayerMP[]{var8}));
      } else if (this.field_197364_k != null) {
         Iterator var7 = var1.func_197028_i().func_212370_w().iterator();

         Entity var10;
         do {
            if (!var7.hasNext()) {
               return Collections.emptyList();
            }

            WorldServer var9 = (WorldServer)var7.next();
            var10 = var9.func_175733_a(this.field_197364_k);
         } while(var10 == null);

         return Lists.newArrayList(new Entity[]{var10});
      } else {
         Vec3d var2 = (Vec3d)this.field_197359_f.apply(var1.func_197036_d());
         Predicate var3 = this.func_197349_a(var2);
         if (this.field_197362_i) {
            return (List)(var1.func_197022_f() != null && var3.test(var1.func_197022_f()) ? Lists.newArrayList(new Entity[]{var1.func_197022_f()}) : Collections.emptyList());
         } else {
            ArrayList var4 = Lists.newArrayList();
            if (this.func_197353_d()) {
               this.func_197348_a(var4, var1.func_197023_e(), var2, var3);
            } else {
               Iterator var5 = var1.func_197028_i().func_212370_w().iterator();

               while(var5.hasNext()) {
                  WorldServer var6 = (WorldServer)var5.next();
                  this.func_197348_a(var4, var6, var2, var3);
               }
            }

            return this.func_197345_a(var2, var4);
         }
      }
   }

   private void func_197348_a(List<Entity> var1, WorldServer var2, Vec3d var3, Predicate<Entity> var4) {
      Class var10002;
      if (this.field_197360_g != null) {
         var10002 = this.field_201957_l;
         AxisAlignedBB var10003 = this.field_197360_g.func_191194_a(var3);
         var4.getClass();
         var1.addAll(var2.func_175647_a(var10002, var10003, var4::test));
      } else {
         var10002 = this.field_201957_l;
         var4.getClass();
         var1.addAll(var2.func_175644_a(var10002, var4::test));
      }

   }

   public EntityPlayerMP func_197347_c(CommandSource var1) throws CommandSyntaxException {
      this.func_210324_e(var1);
      List var2 = this.func_197342_d(var1);
      if (var2.size() != 1) {
         throw EntityArgument.field_197102_e.create();
      } else {
         return (EntityPlayerMP)var2.get(0);
      }
   }

   public List<EntityPlayerMP> func_197342_d(CommandSource var1) throws CommandSyntaxException {
      this.func_210324_e(var1);
      EntityPlayerMP var7;
      if (this.field_197363_j != null) {
         var7 = var1.func_197028_i().func_184103_al().func_152612_a(this.field_197363_j);
         return (List)(var7 == null ? Collections.emptyList() : Lists.newArrayList(new EntityPlayerMP[]{var7}));
      } else if (this.field_197364_k != null) {
         var7 = var1.func_197028_i().func_184103_al().func_177451_a(this.field_197364_k);
         return (List)(var7 == null ? Collections.emptyList() : Lists.newArrayList(new EntityPlayerMP[]{var7}));
      } else {
         Vec3d var2 = (Vec3d)this.field_197359_f.apply(var1.func_197036_d());
         Predicate var3 = this.func_197349_a(var2);
         if (this.field_197362_i) {
            if (var1.func_197022_f() instanceof EntityPlayerMP) {
               EntityPlayerMP var8 = (EntityPlayerMP)var1.func_197022_f();
               if (var3.test(var8)) {
                  return Lists.newArrayList(new EntityPlayerMP[]{var8});
               }
            }

            return Collections.emptyList();
         } else {
            Object var4;
            if (this.func_197353_d()) {
               WorldServer var10000 = var1.func_197023_e();
               var3.getClass();
               var4 = var10000.func_175661_b(EntityPlayerMP.class, var3::test);
            } else {
               var4 = Lists.newArrayList();
               Iterator var5 = var1.func_197028_i().func_184103_al().func_181057_v().iterator();

               while(var5.hasNext()) {
                  EntityPlayerMP var6 = (EntityPlayerMP)var5.next();
                  if (var3.test(var6)) {
                     ((List)var4).add(var6);
                  }
               }
            }

            return this.func_197345_a(var2, (List)var4);
         }
      }
   }

   private Predicate<Entity> func_197349_a(Vec3d var1) {
      Predicate var2 = this.field_197357_d;
      if (this.field_197360_g != null) {
         AxisAlignedBB var3 = this.field_197360_g.func_191194_a(var1);
         var2 = var2.and((var1x) -> {
            return var3.func_72326_a(var1x.func_174813_aQ());
         });
      }

      if (!this.field_197358_e.func_211335_c()) {
         var2 = var2.and((var2x) -> {
            return this.field_197358_e.func_211351_a(var2x.func_195048_a(var1));
         });
      }

      return var2;
   }

   private <T extends Entity> List<T> func_197345_a(Vec3d var1, List<T> var2) {
      if (var2.size() > 1) {
         this.field_197361_h.accept(var1, var2);
      }

      return var2.subList(0, Math.min(this.field_197354_a, var2.size()));
   }

   public static ITextComponent func_197350_a(List<? extends Entity> var0) {
      return TextComponentUtils.func_197677_b(var0, Entity::func_145748_c_);
   }
}

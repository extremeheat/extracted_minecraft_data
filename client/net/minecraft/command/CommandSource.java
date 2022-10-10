package net.minecraft.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.function.BinaryOperator;
import javax.annotation.Nullable;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServer;

public class CommandSource implements ISuggestionProvider {
   public static final SimpleCommandExceptionType field_197039_a = new SimpleCommandExceptionType(new TextComponentTranslation("permissions.requires.player", new Object[0]));
   public static final SimpleCommandExceptionType field_197040_b = new SimpleCommandExceptionType(new TextComponentTranslation("permissions.requires.entity", new Object[0]));
   private final ICommandSource field_197041_c;
   private final Vec3d field_197042_d;
   private final WorldServer field_197043_e;
   private final int field_197044_f;
   private final String field_197045_g;
   private final ITextComponent field_197046_h;
   private final MinecraftServer field_197047_i;
   private final boolean field_197048_j;
   @Nullable
   private final Entity field_197049_k;
   private final ResultConsumer<CommandSource> field_197050_l;
   private final EntityAnchorArgument.Type field_201011_m;
   private final Vec2f field_201012_n;

   public CommandSource(ICommandSource var1, Vec3d var2, Vec2f var3, WorldServer var4, int var5, String var6, ITextComponent var7, MinecraftServer var8, @Nullable Entity var9) {
      this(var1, var2, var3, var4, var5, var6, var7, var8, var9, false, (var0, var1x, var2x) -> {
      }, EntityAnchorArgument.Type.FEET);
   }

   protected CommandSource(ICommandSource var1, Vec3d var2, Vec2f var3, WorldServer var4, int var5, String var6, ITextComponent var7, MinecraftServer var8, @Nullable Entity var9, boolean var10, ResultConsumer<CommandSource> var11, EntityAnchorArgument.Type var12) {
      super();
      this.field_197041_c = var1;
      this.field_197042_d = var2;
      this.field_197043_e = var4;
      this.field_197048_j = var10;
      this.field_197049_k = var9;
      this.field_197044_f = var5;
      this.field_197045_g = var6;
      this.field_197046_h = var7;
      this.field_197047_i = var8;
      this.field_197050_l = var11;
      this.field_201011_m = var12;
      this.field_201012_n = var3;
   }

   public CommandSource func_197024_a(Entity var1) {
      return this.field_197049_k == var1 ? this : new CommandSource(this.field_197041_c, this.field_197042_d, this.field_201012_n, this.field_197043_e, this.field_197044_f, var1.func_200200_C_().getString(), var1.func_145748_c_(), this.field_197047_i, var1, this.field_197048_j, this.field_197050_l, this.field_201011_m);
   }

   public CommandSource func_201009_a(Vec3d var1) {
      return this.field_197042_d.equals(var1) ? this : new CommandSource(this.field_197041_c, var1, this.field_201012_n, this.field_197043_e, this.field_197044_f, this.field_197045_g, this.field_197046_h, this.field_197047_i, this.field_197049_k, this.field_197048_j, this.field_197050_l, this.field_201011_m);
   }

   public CommandSource func_201007_a(Vec2f var1) {
      return this.field_201012_n.func_201069_c(var1) ? this : new CommandSource(this.field_197041_c, this.field_197042_d, var1, this.field_197043_e, this.field_197044_f, this.field_197045_g, this.field_197046_h, this.field_197047_i, this.field_197049_k, this.field_197048_j, this.field_197050_l, this.field_201011_m);
   }

   public CommandSource func_197029_a(ResultConsumer<CommandSource> var1) {
      return this.field_197050_l.equals(var1) ? this : new CommandSource(this.field_197041_c, this.field_197042_d, this.field_201012_n, this.field_197043_e, this.field_197044_f, this.field_197045_g, this.field_197046_h, this.field_197047_i, this.field_197049_k, this.field_197048_j, var1, this.field_201011_m);
   }

   public CommandSource func_209550_a(ResultConsumer<CommandSource> var1, BinaryOperator<ResultConsumer<CommandSource>> var2) {
      ResultConsumer var3 = (ResultConsumer)var2.apply(this.field_197050_l, var1);
      return this.func_197029_a(var3);
   }

   public CommandSource func_197031_a() {
      return this.field_197048_j ? this : new CommandSource(this.field_197041_c, this.field_197042_d, this.field_201012_n, this.field_197043_e, this.field_197044_f, this.field_197045_g, this.field_197046_h, this.field_197047_i, this.field_197049_k, true, this.field_197050_l, this.field_201011_m);
   }

   public CommandSource func_197033_a(int var1) {
      return var1 == this.field_197044_f ? this : new CommandSource(this.field_197041_c, this.field_197042_d, this.field_201012_n, this.field_197043_e, var1, this.field_197045_g, this.field_197046_h, this.field_197047_i, this.field_197049_k, this.field_197048_j, this.field_197050_l, this.field_201011_m);
   }

   public CommandSource func_197026_b(int var1) {
      return var1 <= this.field_197044_f ? this : new CommandSource(this.field_197041_c, this.field_197042_d, this.field_201012_n, this.field_197043_e, var1, this.field_197045_g, this.field_197046_h, this.field_197047_i, this.field_197049_k, this.field_197048_j, this.field_197050_l, this.field_201011_m);
   }

   public CommandSource func_201010_a(EntityAnchorArgument.Type var1) {
      return var1 == this.field_201011_m ? this : new CommandSource(this.field_197041_c, this.field_197042_d, this.field_201012_n, this.field_197043_e, this.field_197044_f, this.field_197045_g, this.field_197046_h, this.field_197047_i, this.field_197049_k, this.field_197048_j, this.field_197050_l, var1);
   }

   public CommandSource func_201003_a(WorldServer var1) {
      return var1 == this.field_197043_e ? this : new CommandSource(this.field_197041_c, this.field_197042_d, this.field_201012_n, var1, this.field_197044_f, this.field_197045_g, this.field_197046_h, this.field_197047_i, this.field_197049_k, this.field_197048_j, this.field_197050_l, this.field_201011_m);
   }

   public CommandSource func_201006_a(Entity var1, EntityAnchorArgument.Type var2) throws CommandSyntaxException {
      return this.func_201005_b(var2.func_201017_a(var1));
   }

   public CommandSource func_201005_b(Vec3d var1) throws CommandSyntaxException {
      Vec3d var2 = this.field_201011_m.func_201015_a(this);
      double var3 = var1.field_72450_a - var2.field_72450_a;
      double var5 = var1.field_72448_b - var2.field_72448_b;
      double var7 = var1.field_72449_c - var2.field_72449_c;
      double var9 = (double)MathHelper.func_76133_a(var3 * var3 + var7 * var7);
      float var11 = MathHelper.func_76142_g((float)(-(MathHelper.func_181159_b(var5, var9) * 57.2957763671875D)));
      float var12 = MathHelper.func_76142_g((float)(MathHelper.func_181159_b(var7, var3) * 57.2957763671875D) - 90.0F);
      return this.func_201007_a(new Vec2f(var11, var12));
   }

   public ITextComponent func_197019_b() {
      return this.field_197046_h;
   }

   public String func_197037_c() {
      return this.field_197045_g;
   }

   public boolean func_197034_c(int var1) {
      return this.field_197044_f >= var1;
   }

   public Vec3d func_197036_d() {
      return this.field_197042_d;
   }

   public WorldServer func_197023_e() {
      return this.field_197043_e;
   }

   @Nullable
   public Entity func_197022_f() {
      return this.field_197049_k;
   }

   public Entity func_197027_g() throws CommandSyntaxException {
      if (this.field_197049_k == null) {
         throw field_197040_b.create();
      } else {
         return this.field_197049_k;
      }
   }

   public EntityPlayerMP func_197035_h() throws CommandSyntaxException {
      if (!(this.field_197049_k instanceof EntityPlayerMP)) {
         throw field_197039_a.create();
      } else {
         return (EntityPlayerMP)this.field_197049_k;
      }
   }

   public Vec2f func_201004_i() {
      return this.field_201012_n;
   }

   public MinecraftServer func_197028_i() {
      return this.field_197047_i;
   }

   public EntityAnchorArgument.Type func_201008_k() {
      return this.field_201011_m;
   }

   public void func_197030_a(ITextComponent var1, boolean var2) {
      if (this.field_197041_c.func_195039_a() && !this.field_197048_j) {
         this.field_197041_c.func_145747_a(var1);
      }

      if (var2 && this.field_197041_c.func_195041_r_() && !this.field_197048_j) {
         this.func_197020_b(var1);
      }

   }

   private void func_197020_b(ITextComponent var1) {
      ITextComponent var2 = (new TextComponentTranslation("chat.type.admin", new Object[]{this.func_197019_b(), var1})).func_211709_a(new TextFormatting[]{TextFormatting.GRAY, TextFormatting.ITALIC});
      if (this.field_197047_i.func_200252_aR().func_82766_b("sendCommandFeedback")) {
         Iterator var3 = this.field_197047_i.func_184103_al().func_181057_v().iterator();

         while(var3.hasNext()) {
            EntityPlayerMP var4 = (EntityPlayerMP)var3.next();
            if (var4 != this.field_197041_c && this.field_197047_i.func_184103_al().func_152596_g(var4.func_146103_bH())) {
               var4.func_145747_a(var2);
            }
         }
      }

      if (this.field_197041_c != this.field_197047_i && this.field_197047_i.func_200252_aR().func_82766_b("logAdminCommands")) {
         this.field_197047_i.func_145747_a(var2);
      }

   }

   public void func_197021_a(ITextComponent var1) {
      if (this.field_197041_c.func_195040_b() && !this.field_197048_j) {
         this.field_197041_c.func_145747_a((new TextComponentString("")).func_150257_a(var1).func_211708_a(TextFormatting.RED));
      }

   }

   public void func_197038_a(CommandContext<CommandSource> var1, boolean var2, int var3) {
      if (this.field_197050_l != null) {
         this.field_197050_l.onCommandComplete(var1, var2, var3);
      }

   }

   public Collection<String> func_197011_j() {
      return Lists.newArrayList(this.field_197047_i.func_71213_z());
   }

   public Collection<String> func_197012_k() {
      return this.field_197047_i.func_200251_aP().func_96531_f();
   }

   public Collection<ResourceLocation> func_197010_l() {
      return IRegistry.field_212633_v.func_148742_b();
   }

   public Collection<ResourceLocation> func_199612_m() {
      return this.field_197047_i.func_199529_aN().func_199511_c();
   }

   public CompletableFuture<Suggestions> func_197009_a(CommandContext<ISuggestionProvider> var1, SuggestionsBuilder var2) {
      return null;
   }

   public Collection<ISuggestionProvider.Coordinates> func_199613_a(boolean var1) {
      return Collections.singleton(ISuggestionProvider.Coordinates.field_209005_b);
   }
}

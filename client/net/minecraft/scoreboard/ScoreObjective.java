package net.minecraft.scoreboard;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.event.HoverEvent;

public class ScoreObjective {
   private final Scoreboard field_96686_a;
   private final String field_96684_b;
   private final ScoreCriteria field_96685_c;
   private ITextComponent field_96683_d;
   private ScoreCriteria.RenderType field_199867_e;

   public ScoreObjective(Scoreboard var1, String var2, ScoreCriteria var3, ITextComponent var4, ScoreCriteria.RenderType var5) {
      super();
      this.field_96686_a = var1;
      this.field_96684_b = var2;
      this.field_96685_c = var3;
      this.field_96683_d = var4;
      this.field_199867_e = var5;
   }

   public Scoreboard func_96682_a() {
      return this.field_96686_a;
   }

   public String func_96679_b() {
      return this.field_96684_b;
   }

   public ScoreCriteria func_96680_c() {
      return this.field_96685_c;
   }

   public ITextComponent func_96678_d() {
      return this.field_96683_d;
   }

   public ITextComponent func_197890_e() {
      return TextComponentUtils.func_197676_a(this.field_96683_d.func_212638_h().func_211710_a((var1) -> {
         var1.func_150209_a(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(this.func_96679_b())));
      }));
   }

   public void func_199864_a(ITextComponent var1) {
      this.field_96683_d = var1;
      this.field_96686_a.func_199869_b(this);
   }

   public ScoreCriteria.RenderType func_199865_f() {
      return this.field_199867_e;
   }

   public void func_199866_a(ScoreCriteria.RenderType var1) {
      this.field_199867_e = var1;
      this.field_96686_a.func_199869_b(this);
   }
}

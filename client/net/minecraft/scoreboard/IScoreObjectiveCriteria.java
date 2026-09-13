package net.minecraft.scoreboard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IScoreObjectiveCriteria {
   Map field_96643_a = new HashMap();
   IScoreObjectiveCriteria field_96641_b = new ScoreDummyCriteria("dummy");
   IScoreObjectiveCriteria field_96642_c = new ScoreDummyCriteria("deathCount");
   IScoreObjectiveCriteria field_96639_d = new ScoreDummyCriteria("playerKillCount");
   IScoreObjectiveCriteria field_96640_e = new ScoreDummyCriteria("totalKillCount");
   IScoreObjectiveCriteria field_96638_f = new ScoreHealthCriteria("health");

   String func_96636_a();

   int func_96635_a(List var1);

   boolean func_96637_b();
}

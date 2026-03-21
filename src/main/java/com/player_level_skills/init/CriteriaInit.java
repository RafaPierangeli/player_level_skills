package com.player_level_skills.init;

import com.player_level_skills.criteria.LevelCriterion;
import com.player_level_skills.criteria.SkillCriterion;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.scoreboard.ScoreboardCriterion;

public class CriteriaInit {

    public static final ScoreboardCriterion LEVELZ = ScoreboardCriterion.create("levelz");

    public static final LevelCriterion LEVEL_UP = Criteria.register("levelz:level", new LevelCriterion());
    public static final SkillCriterion SKILL_UP = Criteria.register("levelz:skill",new SkillCriterion());

    public static void init() {
    }

}

package com.player_level_skills.level;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;

public class PlayerSkill {

    private final int id;
    private int level;

    public PlayerSkill(int id, int level) {
        this.id = id;
        this.level = Math.max(0, level);
    }

    public PlayerSkill(NbtCompound nbt) {
        this.id = nbt.getInt("Id").orElse(0);
        this.level = nbt.getInt("Level").orElse(0);

        if (this.level < 0) {
            this.level = 0;
        }
    }

    public static PlayerSkill readDataFromView(ReadView skillView) {
        if (skillView == null) {
            return null;
        }

        int id = skillView.getInt("Id", 0);
        int level = skillView.getInt("Level", 0);

        return new PlayerSkill(id, Math.max(0, level));
    }

    public NbtCompound writeDataToNbt(WriteView skillView) {
        NbtCompound nbt = new NbtCompound();
        nbt.putInt("Id", this.id);
        nbt.putInt("Level", this.level);
        return nbt;
    }

    public int getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = Math.max(0, level);
    }

    public void increaseLevel(int level) {
        Skill skill = LevelManager.SKILLS.get(this.id);
        if (skill == null) {
            this.level = Math.max(0, this.level + level);
            return;
        }

        int maxLevel = skill.getMaxLevel();
        if ((this.level + level) <= maxLevel) {
            this.level += level;
        } else {
            this.level = maxLevel;
        }

        if (this.level < 0) {
            this.level = 0;
        }
    }

    public void decreaseLevel(int level) {
        if ((this.level - level) >= 0) {
            this.level -= level;
        } else {
            this.level = 0;
        }
    }
}
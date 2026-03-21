package com.player_level_skills.access;


import java.util.Map;
import java.util.UUID;

public interface ClientPlayerListAccess {

    public int getLevel();

    public void setLevel(int level);

    public Map<UUID, Integer> getLevelMap();

}
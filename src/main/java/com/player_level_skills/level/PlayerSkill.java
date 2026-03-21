package com.player_level_skills.level;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;

public class PlayerSkill {

    private final int id;
    private int level;

    public PlayerSkill(int id, int level) {
        this.id = id;
        this.level = level;
    }

    // Construtor para deserialização do NBT (corrigido para 1.21.11)
    public PlayerSkill(NbtCompound nbt) {
        // Extrai o ID: se a chave "Id" existir, usa o valor; caso contrário, usa 0 como padrão
        // Nota: getInt retorna Optional<Integer> na 1.21.11, então usamos orElse para converter para int
        this.id = nbt.getInt("Id").orElse(0);

        // Extrai o nível: se a chave "Level" existir, usa o valor; caso contrário, usa 1 como padrão
        this.level = nbt.getInt("Level").orElse(1);

        // Opcional: Validação extra para garantir valores válidos (boa prática para mods)
        if (this.level < 1) {
            this.level = 1; // Evita níveis inválidos em saves corrompidos
        }
    }

    public static PlayerSkill readDataFromView(ReadView skillView) {
        return null;
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
        this.level = level;
    }

    public void increaseLevel(int level) {
        int maxLevel = LevelManager.SKILLS.get(this.id).getMaxLevel();
        if ((this.level + level) <= maxLevel) {
            this.level += level;
        } else {
            this.level = maxLevel;
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

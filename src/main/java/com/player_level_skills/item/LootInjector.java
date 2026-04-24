package com.player_level_skills.item;

import com.player_level_skills.init.ItemInit; // Ajuste para o seu registro
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;

public class LootInjector {

    public static void register() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            String path = key.getValue().getPath();

            // 🌟 ALVO: Qualquer baú de estrutura (Vilas, Templos, Fortalezas, etc.)
            if (path.startsWith("chests/")) {

                // 🛑 TRAVA DE SEGURANÇA: Evita spam nas Trial Chambers da 1.21
                if (path.contains("trial_chambers") && (path.contains("_common") || path.contains("_rare") || path.contains("_unique"))) {
                    return;
                }

                // 📊 BALANCEAMENTO: Chance de aparecer o Rare Candy
                // Diferente do outro mod (pergaminhos), o Rare Candy é um item de "Nível",
                // então não deve ser 100% garantido para não quebrar a progressão.
                float chance = 0.10f; // 10% de chance padrão em baús comuns

                // 🌟 BAÚS ÉPICOS: Aumentamos a chance em lugares perigosos
                if (path.contains("ancient_city") || path.contains("end_city") || path.contains("bastion")) {
                    chance = 0.35f; // 35% de chance em cidades do End/Ancient City
                } else if (path.contains("stronghold") || path.contains("nether_bridge")) {
                    chance = 0.20f; // 20% de chance em Fortalezas/Pontes do Nether
                }

                // Criamos a "Piscina" de loot do Rare Candy
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1)) // Tenta colocar 1 item
                        .conditionally(RandomChanceLootCondition.builder(chance)) // Aplica a nossa chance
                        .with(ItemEntry.builder(ItemInit.RARE_CANDY)); // O seu item

                // Injeta no baú
                tableBuilder.pool(poolBuilder);
            }
        });
    }
}

package io.redspace.ironsspellbooks.capabilities.magic;

import com.google.common.collect.Maps;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;

import java.util.Map;

public class PlayerRecastHandler {
    public static final String SPELL_ID = "id";
    public static final String SPELL_COOLDOWN = "scd";
    public static final String COOLDOWN_REMAINING = "cdr";

    //spell type and for how many more ticks it will be on cooldown
    private final Map<String, RecastInstance> spellCooldowns;

    public PlayerRecastHandler() {
        this(Maps.newHashMap());
    }

    public PlayerRecastHandler(Map<String, RecastInstance> spellCooldowns) {
        this.spellCooldowns = spellCooldowns;
    }

//    public void syncToPlayer(ServerPlayer serverPlayer) {
//        Messages.sendToPlayer(new ClientboundSyncCooldowns(this.spellCooldowns), serverPlayer);
//    }

    /**
     * Begins or refreshes recast tracking for this spell
     */
    public void addRecast(AbstractSpell spell, int maxCasts, int recastTime) {
        IronsSpellbooks.LOGGER.debug("PlayerRecastHandler.addRecast: existing cast: {}", spellCooldowns.get(spell.getSpellId()));
        if (spellCooldowns.containsKey(spell.getSpellId())) {
            //If we're already tracking, refresh our time remaining
            var recastInstance = spellCooldowns.get(spell.getSpellId());
            recastInstance.updateRecast();
            if (recastInstance.getCastCount() >= recastInstance.getMaxCastCount()) {
                //TODO: on recast finished?
                spellCooldowns.remove(spell.getSpellId());
            }
        } else {
            //Otherwise, create a new instance to track
            spellCooldowns.put(spell.getSpellId(), new RecastInstance(1, maxCasts, recastTime));
        }
    }

    public boolean isRecastAvailable(AbstractSpell spell) {
        IronsSpellbooks.LOGGER.debug("PlayerRecastHandler.isRecastAvailable {}: {}", spell.getSpellId(), spellCooldowns.get(spell.getSpellId()));
        var recastInstance = spellCooldowns.get(spell.getSpellId());
        return recastInstance != null && recastInstance.getCastCount() < recastInstance.getMaxCastCount() && recastInstance.getTicksRemaining() >= 0;
    }
//
//    public void saveNBTData(ListTag listTag) {
//        spellCooldowns.forEach((spellId, cooldown) -> {
//            if (cooldown.getCooldownRemaining() > 0) {
//                CompoundTag ct = new CompoundTag();
//                ct.putString(SPELL_ID, spellId);
//                ct.putInt(SPELL_COOLDOWN, cooldown.getSpellCooldown());
//                ct.putInt(COOLDOWN_REMAINING, cooldown.getCooldownRemaining());
//                listTag.add(ct);
//            }
//        });
//    }
//
//    public void loadNBTData(ListTag listTag) {
//        if (listTag != null) {
//            listTag.forEach(tag -> {
//                CompoundTag t = (CompoundTag) tag;
//                String spellId = t.getString(SPELL_ID);
//                int spellCooldown = t.getInt(SPELL_COOLDOWN);
//                int cooldownRemaining = t.getInt(COOLDOWN_REMAINING);
//                spellCooldowns.put(spellId, new CooldownInstance(spellCooldown, cooldownRemaining));
//            });
//        }
//    }
}

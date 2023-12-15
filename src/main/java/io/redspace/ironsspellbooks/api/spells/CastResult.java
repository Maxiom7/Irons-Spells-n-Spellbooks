package io.redspace.ironsspellbooks.api.spells;

import ca.weblite.objc.Message;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CastResult {
    public enum Type {
        SUCCESS,
        RECAST,
        FAILURE
    }

    public final Type type;
    public final @Nullable Component message;

    public CastResult(Type type) {
        this(type, null);
    }

    public CastResult(Type type, Component message) {
        this.type = type;
        this.message = message;
    }

    public boolean isSuccess() {
        return this.type == Type.SUCCESS;
    }

    public Optional<Component> castMessage() {
        return Optional.of(message);
    }
}
